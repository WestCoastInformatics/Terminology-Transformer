// Administration controller
tsApp
  .controller(
    'AbbrCtrl',
    [
      '$scope',
      '$http',
      '$q',
      '$location',
      '$uibModal',
      'gpService',
      'utilService',
      'tabService',
      'configureService',
      'securityService',
      'metadataService',
      'projectService',
      'mldpService',
      function($scope, $http, $q, $location, $uibModal, gpService, utilService, tabService,
        configureService, securityService, metadataService, projectService, mldpService) {
        console.debug('configure AbbrCtrl');

        // Set up tabs and controller
        tabService.setShowing(true);
        utilService.clearError();
        tabService.setSelectedTabByLabel('Abbreviations');
        $scope.user = securityService.getUser();
        projectService.getUserHasAnyRole();

        $scope.selected = {
          metadata : metadataService.getModel(),
          component : null,
          project : null
        };

        // Scope variables
        $scope.selected = {
          project : null,
          terminology : null,
          metadata : metadataService.getModel(),

          // the abbreviation selected in the browse table
          abbrViewed : null,

          // the abbreviation selected for editing
          abbrEdited : null,

          // edit/import/export tab selection
          editTab : 'Edit'
        }

        $scope.lists = {
          projects : [],
          terminologies : [],
          abbrsViewed : [],
          abbrsReviewed : [],
          abbrsReviewedAcrossTerminologies : [],
          fileTypesFilter : '.txt',
          workflowStatus : [ {
            key : null,
            label : 'All Active'
          }, {
            key : 'PUBLISHED',
            label : 'Accepted'
          }, {
            key : 'NEW',
            label : 'New'
          }, {
            key : 'NEEDS_REVIEW',
            label : 'Needs Review'
          }, {
            key : 'DEMOTION',
            label : 'Ignored'
          } ],
          filterTypes : [ {
            key : null,
            value : 'None'
          }, {
            key : 'blankValue',
            value : 'Blank expansion'
          }, {
            key : 'duplicate',
            value : 'Duplicate'
          }, {
            key : 'duplicateKey',
            value : 'Duplicate abbr'
          }, {
            key : 'duplicateValue',
            value : 'Duplicate expansion within terminology'
          }, {
            key : 'duplicateValueAcrossTerminologies',
            value : 'Duplicate expansion in another terminology'
          } ],

          pageSizes : [ {
            key : 10,
            value : "10"
          }, {
            key : 20,
            value : "20"
          }, {
            key : 50,
            value : "50"
          }, {
            key : 100,
            value : '100'
          }, {
            key : 200,
            value : '200'
          } ]

        };

        $scope.paging = {};
        $scope.paging['abbr'] = utilService.getPaging();
        $scope.paging['abbr'].sortField = 'key';
        $scope.paging['abbr'].workflowStatus = null;
        $scope.paging['abbr'].filterType = null;
        $scope.paging['abbr'].pageSize = 10;
        $scope.paging['abbr'].callbacks = {
          getPagedList : findAbbreviations
        };
        $scope.paging['review'] = utilService.getPaging();
        $scope.paging['review'].sortField = 'key';
        $scope.paging['review'].workflowStatus = null;
        $scope.paging['review'].callbacks = {
          getPagedList : getPagedReview
        };
        $scope.paging['reviewOther'] = utilService.getPaging();
        $scope.paging['reviewOther'].sortField = 'key';
        $scope.paging['reviewOther'].workflowStatus = null;
        $scope.paging['reviewOther'].callbacks = {
          getPagedList : getPagedReview
        };

        // pass utility functions to scope
        $scope.toShortDate = utilService.toShortDate;

       

        $scope.findAbbreviations = function(abbr) {
          findAbbreviations(abbr);
        }
        function findAbbreviations(abbr) {
          if (!$scope.selected.metadata.terminology) {
            return;
          }
          if (abbr) {
            $scope.setAbbreviationViewed(abbr);
          }
          var pfs = prepAbbrPfs('abbr');
          console.debug('pfs', pfs);
          // retrieval call
          mldpService.findAbbreviations($scope.paging['abbr'].filter, $scope.selected.project.id,
            $scope.paging['abbr'].filterType, pfs).then(function(response) {
            $scope.lists.abbrsViewed = response;
            console.debug('abbreviations', $scope.lists.abbrsViewed);
          });

          //truncated NEEDS_REVIEW and NEW calls
          pfs = prepAbbrPfs('abbr');
          pfs.maxResults = 0;
          mldpService.findAbbreviations('workflowStatus:NEEDS_REVIEW', $scope.selected.project.id,
            $scope.paging['abbr'].filterType, pfs).then(function(response) {
            $scope.paging['abbr'].hasNeedsReview = response.totalCount > 0;
          });
          mldpService.findAbbreviations('workflowStatus:NEW', $scope.selected.project.id,
            $scope.paging['abbr'].filterType, pfs).then(function(response) {
            $scope.paging['abbr'].hasNew = response.totalCount > 0;
          });

        }

        function prepAbbrPfs(type) {
          var paging = $scope.paging[type];
          var pfs = {
            startIndex : (paging.page - 1) * paging.pageSize,
            maxResults : paging.pageSize,
            sortField : paging.sortField,
            ascending : paging.sortAscending,
            queryRestriction : $scope.paging['abbr'].workflowStatus ? 'workflowStatus:'
              + $scope.paging[type].workflowStatus : null
          };
          return pfs;
        }

        $scope.setAbbreviationViewed = function(abbr) {
          $scope.selected.editTab = 'Edit';
          $scope.selected.abbrViewed = abbr;
          $scope.setAbbreviationEdited(abbr);
          $scope.getReviewForAbbreviations(abbr);
        }

        $scope.setAbbreviationEdited = function(abbr) {
          // Copy object to prevent changes propagating before update invocation
          $scope.selected.abbrEdited = angular.copy(abbr);
        }

        $scope.createAbbreviation = function() {
          var abbr = {
            type : null,
            key : null,
            value : null
          }
          $scope.setAbbreviationEdited(abbr);
        }

        $scope.cancelAbbreviation = function() {
          $scope.setAbbreviationEdited(null);
        }

        $scope.addAbbreviation = function(abbr) {
          mldpService.addAbbreviation(abbr, $scope.selected.project.id).then(function(newAbbr) {

            $scope.setAbbreviationViewed(newAbbr);

            // perform all actions triggered by abbreviation change
            $scope.processAbbreviationChange();

          });
        }

        $scope.updateAbbreviation = function(abbr) {
          mldpService.updateAbbreviation(abbr, $scope.selected.project.id).then(function() {

            // perform all actions triggered by abbreviation change
            $scope.processAbbreviationChange();
          });
        }

        $scope.removeAbbreviations = function() {
          var pfs = prepAbbrPfs('abbr');
          pfs.startIndex = -1;
          pfs.maxResults = -1;

          mldpService.findAbbreviations($scope.paging['abbr'].filter, $scope.selected.project.id,
            $scope.paging['abbr'].filterType, pfs).then(function(response) {
            var ids = response.typeKeyValues.map(function(t) {
              return t.id;
            });
            mldpService.removeAbbreviations(ids, $scope.selected.project.id).then(function() {
              findAbbreviations();

              // clear edited abbreviation
              if ($scope.selected.abbrEdited && ids.indexOf($scope.selected.abbrEdited.id) != -1) {
                $scope.setAbbreviationEdited(null);
              }

              console.debug('cycling over review list', $scope.lists.abbrsReviewed);

              // remove the abbreviation from the review list if present
              for (var i = 0; i < $scope.lists.abbrsReviewed.typeKeyValues.length; i++) {
                console.debug('checking', ids, $scope.lists.abbrsReviewed.typeKeyValues[i].id)
                if (ids.indexOf($scope.lists.abbrsReviewed.typeKeyValues[i].id) != -1) {

                  $scope.lists.abbrsReviewed.typeKeyValues.splice(i--, 1);
                  console.debug('-> found, new list ', $scope.lists.abbrsReviewed);
                }
              }

              $scope.processAbbreviationChange();
            });
          });
        }

        $scope.removeAbbreviation = function(abbr) {
          console.debug('remove abbreviation', abbr);
          mldpService.removeAbbreviation(abbr.id, $scope.selected.project.id).then(function() {

            // clear edited abbreviation
            $scope.setAbbreviationEdited(null);

            console.debug('cycling over review list', $scope.lists.abbrsReviewed);

            // remove the abbreviation from the review list if present
            if ($scope.lists.abbrsReviewed && $scope.lists.abbrsReviewed.typeKeyValues) {
              for (var i = 0; i < $scope.lists.abbrsReviewed.typeKeyValues.length; i++) {
                console.debug('checking', abbr.id, $scope.lists.abbrsReviewed.typeKeyValues[i].id)
                if ($scope.lists.abbrsReviewed.typeKeyValues[i].id == abbr.id) {

                  $scope.lists.abbrsReviewed.typeKeyValues.splice(i, 1);
                  console.debug('-> found, new list ', $scope.lists.abbrsReviewed);
                }
              }
            }

            // perform all actions triggered by abbreviation change
            console.debug('REMOVE_ABBREVIATION: PROCESS ', $scope.lists.abbrsReviewed);
            $scope.processAbbreviationChange();
          });
        }

        //
        // Display functions
        //

        $scope.toggleNewMode = function() {
          $scope.paging['abbr'].workflowStatus = $scope.paging['abbr'].workflowStatus == 'NEW' ? null
            : 'NEW';
          $scope.findAbbreviations();
        }

        $scope.toggleReviewMode = function() {
          $scope.paging['abbr'].workflowStatus = $scope.paging['abbr'].workflowStatus == 'NEEDS_REVIEW' ? null
            : 'NEEDS_REVIEW';
          $scope.findAbbreviations();
        }
        //
        // Review functions
        //

        $scope.getReviewForAbbreviations = function(abbr) {
          var deferred = $q.defer();

          // if starting abbreviation supplied, initialize list
          if (abbr) {
            console.debug('getReviewForAbbreviations: iitializing from ', abbr);
            $scope.lists.abbrsReviewed = {
              'typeKeyValues' : [ abbr ],
              'totalCount' : 1
            };
          } else {
            console.debug('getReviewForAbbreviations from abbr list', $scope.lists.abbrsReviewed);
          }

          if (!$scope.lists.abbrsReviewed) {
            deferred.reject('No abbreviations');
          } else {

            mldpService.getReviewForAbbreviations($scope.lists.abbrsReviewed.typeKeyValues,
              $scope.selected.project.id).then(function(abbrReviews) {

              // filter by whether in current terminology
              var abbrsReviewed = {
                'typeKeyValues' : abbrReviews.typeKeyValues.filter(function(abbr) {
                  return abbr.type == $scope.selected.abbrEdited.type;
                })
              };
              abbrsReviewed.totalCount = abbrsReviewed.typeKeyValues.length;
              $scope.lists.abbrsReviewed = abbrsReviewed;

              var abbrsReviewedOther = {
                'typeKeyValues' : abbrReviews.typeKeyValues.filter(function(abbr) {
                  return abbr.type != $scope.selected.abbrEdited.type;
                })
              };
              abbrsReviewedOther.totalCount = abbrsReviewedOther.typeKeyValues.length;
              $scope.lists.abbrsReviewedOther = abbrsReviewedOther;

              // on review load, find and select for editing the viewed abbreviation in the review list
              angular.forEach($scope.lists.abbrsReviewed.typeKeyValues, function(abbrReview) {
                if (abbrReview.id == $scope.selected.abbrViewed.id) {
                  $scope.setAbbreviationEdited(abbrReview);
                }
              });

              // get paged list
              getPagedReview();
              deferred.resolve();
            }, function(error) {
              deferred.reject();
            });
          }
          return deferred.promise;
        }

        // paging done client-side
        function getPagedReview() {
          console.debug('getPagedReview', $scope.lists.abbrsReviewed.typeKeyValues,
            $scope.paging['review'], $scope.paging['reviewOther']);
          $scope.lists.pagedReview = utilService.getPagedArray(
            $scope.lists.abbrsReviewed.typeKeyValues, $scope.paging['review']);
          $scope.lists.pagedReviewOther = utilService.getPagedArray(
            $scope.lists.abbrsReviewedOther.typeKeyValues, $scope.paging['reviewOther']);
        }

        // NOTE: Helper function intended for DEBUG use only
        // recomputes workflow status for ALL abbreviations in type
        $scope.recomputeAllReviewStatuses = function() {
          mldpService.computeReviewStatuses($scope.selected.project.id).then(function() {
            $scope.findAbbreviations();
          });
        }

        // recompute review status for all items currently in graph other than currently edited
        // intended for use after add, update, or remove
        $scope.processAbbreviationChange = function() {

          var deferred = [];

          // check current review table for possible changes to other concepts
          angular.forEach($scope.lists.abbrsReviewed.typeKeyValues, function(abbr) {
            // call update to force re-check (unless the currently edited abbreviation)
            if (abbr.workflowStatus == 'NEEDS_REVIEW'
              && (!$scope.selected.abbrEdited || $scope.selected.abbrEdited.id != abbr.id)) {
              deferred.push(mldpService.updateAbbreviation(abbr, $scope.selected.project.id));
            }
          });

          // after all recomputation, get new review and perform new find
          $q.all(deferred).then(function() {
            $scope.getReviewForAbbreviations();
            findAbbreviations();
          }, function() {
            $scope.getReviewForAbbreviations();
            findAbbreviations();
          })
        }

        $scope.finishReview = function() {
          var deferred = [];
          gpService.increment();
          angular.forEach($scope.lists.abbrsReviewed.typeKeyValues,
            function(abbr) {
              if (abbr.workflowStatus == 'NEEDS_REVIEW') {
                abbr.workflowStatus = 'NEW';

                // NOTE: skip checks to prevent NEEDS_REVIEW from being re-applied
                deferred.push(mldpService
                  .updateAbbreviation(abbr, $scope.selected.project.id, true));
              }
            })
          console.debug('deferred', deferred);
          $q.all(deferred).then(function() {
            findAbbreviations();
            gpService.decrement();
            $scope.getReviewForAbbreviations();
          });
        }

        //
        // Import/export
        //
        $scope.validateAbbreviationsFile = function() {
          if (!$scope.selected.file) {
            return;
          }
          mldpService.validateAbbreviationsFile($scope.selected.project.id, $scope.selected.file)
            .then(function(response) {
              $scope.validateAbbreviationsFileResults = response;
            })
        }

        $scope.importAbbreviationsFile = function() {
          mldpService.importAbbreviationsFile($scope.selected.project.id, $scope.selected.file)
            .then(function(response) {
              $scope.importAbbreviationsFileResults = response;
              $scope.findAbbreviations();
            })
        };

        $scope.exportAbbreviations = function() {
          mldpService.exportAbbreviations($scope.selected.project, $scope.selected.exportAcceptNew,
            $scope.selected.exportReadyOnly).then(function() {
            if ($scope.selected.exportAcceptNew) {
              $scope.findAbbreviations();
            }
          })
        };

        $scope.clearImportResults = function() {
          $scope.importAbbreviationsFileResults = null;
          $scope.validateAbbreviationsFileResults = null;
        }

        $scope.cancelImport = function() {
          $scope.selected.file = null;
          $scope.clearImportResults();
        }

        //
        // Utility functions
        // 

        // Table sorting mechanism
        $scope.setSortField = function(table, field, object) {
          utilService.setSortField('' + table, field, $scope.paging);

          // retrieve the correct table
          if (table === 'abbr') {
            findAbbreviations();
          }
          if (table === 'review') {
            getPagedReview();
          }
        };

        // Return up or down sort chars if sorted
        $scope.getSortIndicator = function(table, field) {
          return utilService.getSortIndicator('' + table, field, $scope.paging);
        };

        // 
        // Initialization
        //

        // Wait for "terminologies" to load
        $scope.initMetadata = function() {

          projectService.getProjectsForUser($scope.user).then(
            // Success
            function(projectData) {
              $scope.lists.projects = projectData.projects;
              $scope.selected.project = projectData.project;
              if ($scope.selected.project) {
                securityService.saveProjectId($scope.user.userPreferences,
                  $scope.selected.project.id);
              }

              metadataService.getTerminologies().then(
              // Success
              function(metadata) {

                $scope.lists.terminologies = metadata.terminologies;
                // Load all terminologies upon controller load (unless already
                // loaded)
                if ($scope.lists.terminologies) {
                  // look for user preferences
                  var found = false;
                  if ($scope.user.userPreferences && $scope.user.userPreferences.lastTerminology) {
                    for (var i = 0; i < $scope.lists.terminologies.length; i++) {
                      var terminology = $scope.lists.terminologies[i];
                      // set from user prefs
                      if (terminology.terminology === $scope.user.userPreferences.lastTerminology) {
                        $scope.setTerminology(terminology);
                        found = true;
                        break;
                      }
                    }
                  }

                  // If nothing set, pick the first one
                  if (!found) {

                    if (!$scope.lists.terminologies) {
                      window.alert('No terminologies found, database may not be properly loaded.');
                    } else {
                      $scope.setTerminology($scope.lists.terminologies[0]);
                    }
                  }
                }

              });
            });
        }

        // Function to filter viewable terminologies for picklist
        $scope.getViewableTerminologies = function() {
          var viewableTerminologies = new Array();
          if (!$scope.lists.terminologies) {
            return null;
          }
          for (var i = 0; i < $scope.lists.terminologies.length; i++) {
            // exclude MTH and SRC
            if ($scope.lists.terminologies[i].terminology != 'MTH'
              && $scope.lists.terminologies[i].terminology != 'SRC')
              viewableTerminologies.push($scope.lists.terminologies[i]);
          }
          return viewableTerminologies;
        };

        // Sets the terminology
        $scope.setTerminology = function(terminology) {
          // Set shared model (may already be set)
          metadataService.setTerminology(terminology);
          $scope.user.userPreferences.lastTerminology = terminology.terminology;
          securityService.updateUserPreferences($scope.user.userPreferences);
          
          console.debug('clearing selected and computed lists');
         

          $scope.selected.abbrEdited = null;
          $scope.lists.abbrsReviewed = null;
          $scope.lists.abbrsReviewedOther = null;

          $scope.selected.project = null;
          for (var i = 0; i < $scope.lists.projects.length; i++) {
            if ($scope.lists.projects[i].terminology == terminology.terminology) {
              $scope.selected.project = $scope.lists.projects[i];
              console.debug('  project found: ', $scope.selected.project);
              $scope.clearImportResults();
              $scope.findAbbreviations();
            }
          }
          if ($scope.selected.project == null) {
            utilService.setError('Unexpected error: no project for terminology');
          }

        };

        //
        // Initialize - DO NOT PUT ANYTHING AFTER THIS SECTION OTHER THAN CONFIG CHECK
        //
        $scope.initialize = function() {
          securityService.saveTab($scope.user.userPreferences, '/abbr');
          $scope.initMetadata();
        }

        //
        // Check that application is configured
        //
        configureService.isConfigured().then(function(isConfigured) {
          if (!isConfigured) {
            $location.path('/configure');
          } else {
            $scope.initialize();
          }
        });

        // end

      }

    ]);

tsApp.filter('highlightExact', function($sce) {
  return function(text, phrase) {
    //  console.debug("higlightLabel", text, phrase);
    var htext = text;
    if (text && phrase && text == phrase) {
      htext = '<span class="highlighted">' + text + '</span>';
    }
    return $sce.trustAsHtml(htext);
  };
})
