// Administration controller
tsApp
  .controller(
    'RawTerms',
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
        console.debug('configure RawTermsCtrl');

        // Set up tabs and controller
        tabService.setShowing(true);
        utilService.clearError();
        tabService.setSelectedTabByLabel('Raw Terms');
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

          // the rawTermeviation selected in the browse table
          rawTermViewed : null,

          // the rawTermeviation selected for editing
          rawTermEdited : null,

          // edit/import/export tab selection
          editTab : 'Edit'
        }

        $scope.lists = {
          projects : [],
          terminologies : [],
          rawTermsViewed : [],
          rawTermsReviewed : [],
          rawTermsReviewedAcrossTerminologies : [],
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
            value : 'Duplicate rawTerm'
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
        $scope.paging['rawTerm'] = utilService.getPaging();
        $scope.paging['rawTerm'].sortField = 'key';
        $scope.paging['rawTerm'].workflowStatus = null;
        $scope.paging['rawTerm'].filterType = null;
        $scope.paging['rawTerm'].pageSize = 10;
        $scope.paging['rawTerm'].callbacks = {
          getPagedList : findRawTerm
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

       

        $scope.findRawTerm = function(rawTerm) {
          findRawTerm(rawTerm);
        }
        function findRawTerm(rawTerm) {
          if (!$scope.selected.metadata.terminology) {
            return;
          }
          if (rawTerm) {
            $scope.setRawTermeviationViewed(rawTerm);
          }
          var pfs = prepRawTermPfs('rawTerm');
          console.debug('pfs', pfs);
          // retrieval call
          mldpService.findRawTerm($scope.paging['rawTerm'].filter, $scope.selected.project.id,
            $scope.paging['rawTerm'].filterType, pfs).then(function(response) {
            $scope.lists.rawTermsViewed = response;
            console.debug('rawTerm', $scope.lists.rawTermsViewed);
          });

          //truncated NEEDS_REVIEW and NEW calls
          pfs = prepRawTermPfs('rawTerm');
          pfs.maxResults = 0;
          mldpService.findRawTerm('workflowStatus:NEEDS_REVIEW', $scope.selected.project.id,
            $scope.paging['rawTerm'].filterType, pfs).then(function(response) {
            $scope.paging['rawTerm'].hasNeedsReview = response.totalCount > 0;
          });
          mldpService.findRawTerm('workflowStatus:NEW', $scope.selected.project.id,
            $scope.paging['rawTerm'].filterType, pfs).then(function(response) {
            $scope.paging['rawTerm'].hasNew = response.totalCount > 0;
          });

        }

        function prepRawTermPfs(type) {
          var paging = $scope.paging[type];
          var pfs = {
            startIndex : (paging.page - 1) * paging.pageSize,
            maxResults : paging.pageSize,
            sortField : paging.sortField,
            ascending : paging.sortAscending,
            queryRestriction : $scope.paging['rawTerm'].workflowStatus ? 'workflowStatus:'
              + $scope.paging[type].workflowStatus : null
          };
          return pfs;
        }

        $scope.setRawTermeviationViewed = function(rawTerm) {
          $scope.selected.editTab = 'Edit';
          $scope.selected.rawTermViewed = rawTerm;
          $scope.setRawTermeviationEdited(rawTerm);
          $scope.getReviewForRawTerm(rawTerm);
        }

        $scope.setRawTermeviationEdited = function(rawTerm) {
          // Copy object to prevent changes propagating before update invocation
          $scope.selected.rawTermEdited = angular.copy(rawTerm);
        }

        $scope.createRawTermeviation = function() {
          var rawTerm = {
            type : null,
            key : null,
            value : null
          }
          $scope.setRawTermeviationEdited(rawTerm);
        }

        $scope.cancelRawTermeviation = function() {
          $scope.setRawTermeviationEdited(null);
        }

        $scope.addRawTermeviation = function(rawTerm) {
          mldpService.addRawTermeviation(rawTerm, $scope.selected.project.id).then(function(newRawTerm) {

            $scope.setRawTermeviationViewed(newRawTerm);

            // perform all actions triggered by rawTermeviation change
            $scope.processRawTermeviationChange();

          });
        }

        $scope.updateRawTermeviation = function(rawTerm) {
          mldpService.updateRawTermeviation(rawTerm, $scope.selected.project.id).then(function() {

            // perform all actions triggered by rawTermeviation change
            $scope.processRawTermeviationChange();
          });
        }

        $scope.removeRawTerm = function() {
          var pfs = prepRawTermPfs('rawTerm');
          pfs.startIndex = -1;
          pfs.maxResults = -1;

          mldpService.findRawTerm($scope.paging['rawTerm'].filter, $scope.selected.project.id,
            $scope.paging['rawTerm'].filterType, pfs).then(function(response) {
            var ids = response.typeKeyValues.map(function(t) {
              return t.id;
            });
            mldpService.removeRawTerm(ids, $scope.selected.project.id).then(function() {
              findRawTerm();

              // clear edited rawTermeviation
              if ($scope.selected.rawTermEdited && ids.indexOf($scope.selected.rawTermEdited.id) != -1) {
                $scope.setRawTermeviationEdited(null);
              }

              console.debug('cycling over review list', $scope.lists.rawTermsReviewed);

              // remove the rawTermeviation from the review list if present
              for (var i = 0; i < $scope.lists.rawTermsReviewed.typeKeyValues.length; i++) {
                console.debug('checking', ids, $scope.lists.rawTermsReviewed.typeKeyValues[i].id)
                if (ids.indexOf($scope.lists.rawTermsReviewed.typeKeyValues[i].id) != -1) {

                  $scope.lists.rawTermsReviewed.typeKeyValues.splice(i--, 1);
                  console.debug('-> found, new list ', $scope.lists.rawTermsReviewed);
                }
              }

              $scope.processRawTermeviationChange();
            });
          });
        }

        $scope.removeRawTermeviation = function(rawTerm) {
          console.debug('remove rawTermeviation', rawTerm);
          mldpService.removeRawTermeviation(rawTerm.id, $scope.selected.project.id).then(function() {

            // clear edited rawTermeviation
            $scope.setRawTermeviationEdited(null);

            console.debug('cycling over review list', $scope.lists.rawTermsReviewed);

            // remove the rawTermeviation from the review list if present
            if ($scope.lists.rawTermsReviewed && $scope.lists.rawTermsReviewed.typeKeyValues) {
              for (var i = 0; i < $scope.lists.rawTermsReviewed.typeKeyValues.length; i++) {
                console.debug('checking', rawTerm.id, $scope.lists.rawTermsReviewed.typeKeyValues[i].id)
                if ($scope.lists.rawTermsReviewed.typeKeyValues[i].id == rawTerm.id) {

                  $scope.lists.rawTermsReviewed.typeKeyValues.splice(i, 1);
                  console.debug('-> found, new list ', $scope.lists.rawTermsReviewed);
                }
              }
            }

            // perform all actions triggered by rawTermeviation change
            console.debug('REMOVE_ABBREVIATION: PROCESS ', $scope.lists.rawTermsReviewed);
            $scope.processRawTermeviationChange();
          });
        }

        //
        // Display functions
        //

        $scope.toggleNewMode = function() {
          $scope.paging['rawTerm'].workflowStatus = $scope.paging['rawTerm'].workflowStatus == 'NEW' ? null
            : 'NEW';
          $scope.findRawTerm();
        }

        $scope.toggleReviewMode = function() {
          $scope.paging['rawTerm'].workflowStatus = $scope.paging['rawTerm'].workflowStatus == 'NEEDS_REVIEW' ? null
            : 'NEEDS_REVIEW';
          $scope.findRawTerm();
        }
        //
        // Review functions
        //

        $scope.getReviewForRawTerm = function(rawTerm) {
          var deferred = $q.defer();

          // if starting rawTermeviation supplied, initialize list
          if (rawTerm) {
            console.debug('getReviewForRawTerm: iitializing from ', rawTerm);
            $scope.lists.rawTermsReviewed = {
              'typeKeyValues' : [ rawTerm ],
              'totalCount' : 1
            };
          } else {
            console.debug('getReviewForRawTerm from rawTerm list', $scope.lists.rawTermsReviewed);
          }

          if (!$scope.lists.rawTermsReviewed) {
            deferred.reject('No rawTerm');
          } else {

            mldpService.getReviewForRawTerm($scope.lists.rawTermsReviewed.typeKeyValues,
              $scope.selected.project.id).then(function(rawTermReviews) {

              // filter by whether in current terminology
              var rawTermsReviewed = {
                'typeKeyValues' : rawTermReviews.typeKeyValues.filter(function(rawTerm) {
                  return rawTerm.type == $scope.selected.rawTermEdited.type;
                })
              };
              rawTermsReviewed.totalCount = rawTermsReviewed.typeKeyValues.length;
              $scope.lists.rawTermsReviewed = rawTermsReviewed;

              var rawTermsReviewedOther = {
                'typeKeyValues' : rawTermReviews.typeKeyValues.filter(function(rawTerm) {
                  return rawTerm.type != $scope.selected.rawTermEdited.type;
                })
              };
              rawTermsReviewedOther.totalCount = rawTermsReviewedOther.typeKeyValues.length;
              $scope.lists.rawTermsReviewedOther = rawTermsReviewedOther;

              // on review load, find and select for editing the viewed rawTermeviation in the review list
              angular.forEach($scope.lists.rawTermsReviewed.typeKeyValues, function(rawTermReview) {
                if (rawTermReview.id == $scope.selected.rawTermViewed.id) {
                  $scope.setRawTermeviationEdited(rawTermReview);
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
          console.debug('getPagedReview', $scope.lists.rawTermsReviewed.typeKeyValues,
            $scope.paging['review'], $scope.paging['reviewOther']);
          $scope.lists.pagedReview = utilService.getPagedArray(
            $scope.lists.rawTermsReviewed.typeKeyValues, $scope.paging['review']);
          $scope.lists.pagedReviewOther = utilService.getPagedArray(
            $scope.lists.rawTermsReviewedOther.typeKeyValues, $scope.paging['reviewOther']);
        }

        // NOTE: Helper function intended for DEBUG use only
        // recomputes workflow status for ALL rawTerm in type
        $scope.recomputeAllReviewStatuses = function() {
          mldpService.computeReviewStatuses($scope.selected.project.id).then(function() {
            $scope.findRawTerm();
          });
        }

        // recompute review status for all items currently in graph other than currently edited
        // intended for use after add, update, or remove
        $scope.processRawTermeviationChange = function() {

          var deferred = [];

          // check current review table for possible changes to other concepts
          angular.forEach($scope.lists.rawTermsReviewed.typeKeyValues, function(rawTerm) {
            // call update to force re-check (unless the currently edited rawTermeviation)
            if (rawTerm.workflowStatus == 'NEEDS_REVIEW'
              && (!$scope.selected.rawTermEdited || $scope.selected.rawTermEdited.id != rawTerm.id)) {
              deferred.push(mldpService.updateRawTermeviation(rawTerm, $scope.selected.project.id));
            }
          });

          // after all recomputation, get new review and perform new find
          $q.all(deferred).then(function() {
            $scope.getReviewForRawTerm();
            findRawTerm();
          }, function() {
            $scope.getReviewForRawTerm();
            findRawTerm();
          })
        }

        $scope.finishReview = function() {
          var deferred = [];
          gpService.increment();
          angular.forEach($scope.lists.rawTermsReviewed.typeKeyValues,
            function(rawTerm) {
              if (rawTerm.workflowStatus == 'NEEDS_REVIEW') {
                rawTerm.workflowStatus = 'NEW';

                // NOTE: skip checks to prevent NEEDS_REVIEW from being re-applied
                deferred.push(mldpService
                  .updateRawTermeviation(rawTerm, $scope.selected.project.id, true));
              }
            })
          console.debug('deferred', deferred);
          $q.all(deferred).then(function() {
            findRawTerm();
            gpService.decrement();
            $scope.getReviewForRawTerm();
          });
        }

        //
        // Import/export
        //
        $scope.validateRawTermFile = function() {
          if (!$scope.selected.file) {
            return;
          }
          mldpService.validateRawTermFile($scope.selected.project.id, $scope.selected.file)
            .then(function(response) {
              $scope.validateRawTermFileResults = response;
            })
        }

        $scope.importRawTermFile = function() {
          mldpService.importRawTermFile($scope.selected.project.id, $scope.selected.file)
            .then(function(response) {
              $scope.importRawTermFileResults = response;
              $scope.findRawTerm();
            })
        };

        $scope.exportRawTerm = function() {
          mldpService.exportRawTerm($scope.selected.project, $scope.selected.exportAcceptNew,
            $scope.selected.exportReadyOnly).then(function() {
            if ($scope.selected.exportAcceptNew) {
              $scope.findRawTerm();
            }
          })
        };

        $scope.clearImportResults = function() {
          $scope.importRawTermFileResults = null;
          $scope.validateRawTermFileResults = null;
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
          if (table === 'rawTerm') {
            findRawTerm();
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
         

          $scope.selected.rawTermEdited = null;
          $scope.lists.rawTermsReviewed = null;
          $scope.lists.rawTermsReviewedOther = null;

          $scope.selected.project = null;
          for (var i = 0; i < $scope.lists.projects.length; i++) {
            if ($scope.lists.projects[i].terminology == terminology.terminology) {
              $scope.selected.project = $scope.lists.projects[i];
              console.debug('  project found: ', $scope.selected.project);
              $scope.clearImportResults();
              $scope.findRawTerm();
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
          securityService.saveTab($scope.user.userPreferences, '/rawTerm');
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
