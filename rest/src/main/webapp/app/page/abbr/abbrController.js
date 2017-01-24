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
      'abbrService',
      'ndcService',
      function($scope, $http, $q, $location, $uibModal, gpService, utilService, tabService,
        configureService, securityService, metadataService, projectService, abbrService, ndcService) {
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
          editTab : 'Edit',

          // delimiter for exports (default: tab)
          delimiter : '\t'
        }

        $scope.lists = {
          projects : [],
          terminologies : [],
          abbrsViewed : [],
          abbrsReviewed : [],
          fileTypesFilter : '.txt,.csv',
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
          delimiters : [ {
            key : '\t',
            value : 'Tab'
          }, {
            key : ',',
            value : 'Comma'
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
            value : 'Duplicate expansion'
          } ]
        };

        $scope.paging = {};
        $scope.paging['abbr'] = utilService.getPaging();
        $scope.paging['abbr'].sortField = 'key';
        $scope.paging['abbr'].workflowStatus = null;
        $scope.paging['abbr'].callbacks = {
          getPagedList : findAbbreviations
        };
        $scope.paging['review'] = utilService.getPaging();
        $scope.paging['review'].sortField = 'key';
        $scope.paging['review'].workflowStatus = null;
        $scope.paging['review'].callbacks = {
          getPagedList : getPagedReview
        };

        // Sets the terminololgy
        $scope.setTerminology = function(terminology) {
          // Set shared model (may already be set)
          metadataService.setTerminology(terminology);

          // set the autocomplete url, with pattern:
          // /type/{terminology}/{version}/autocomplete/{searchTerm}
          $scope.autocompleteUrl = $scope.selected.metadata.terminology.organizingClassType
            .toLowerCase()
            + '/'
            + $scope.selected.metadata.terminology.terminology
            + '/'
            + $scope.selected.metadata.terminology.version + "/autocomplete/";

          // Choose a project
          for (var i = 0; i < $scope.lists.projects.length; i++) {
            var p = $scope.lists.projects[i];
            // Pick the first project if nothing has been selected
            if (!$scope.selected.project) {
              $scope.selected.project = p;
            }
            if (p.terminology == terminology.terminology) {
              $scope.selected.project = p;
            }
          }
          if ($scope.selected.project) {
            securityService.saveProjectId($scope.user.userPreferences, $scope.selected.project.id);
          }

          // otherwise, leave project setting as is (last chosen)

          // Load all metadata for this terminology, store it in the metadata
          // service and return deferred promise
          var deferred = $q.defer();
          metadataService.getAllMetadata(terminology.terminology, terminology.version).then(
          // Success
          function(data) {

            // Set the shared model in the metadata service
            metadataService.setModel(data);

            deferred.resolve();
          }, function() {
            deferred.reject();
          });

          return deferred.promise;
        };

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
          abbrService.findAbbreviations($scope.paging['abbr'].filter,
            $scope.paging['abbr'].filterType, pfs).then(function(response) {
            $scope.lists.abbrsViewed = response;
            console.debug('abbreviations', $scope.lists.abbrsViewed);
          });

          // truncated NEEDS_REVIEW and NEW calls
          pfs = prepAbbrPfs('abbr');
          pfs.maxResults = 0;
          abbrService.findAbbreviations('workflowStatus:NEEDS_REVIEW',
            $scope.paging['abbr'].filterType, pfs).then(function(response) {
            $scope.paging['abbr'].hasNeedsReview = response.totalCount > 0;
          });
          abbrService.findAbbreviations('workflowStatus:NEW',
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
            queryRestriction : null
          };

          // construct the query restriction clauses
          var clauses = [];

          // first, restriction by type (required)
          clauses.push('type:\"' + $scope.selected.metadata.terminology.preferredName + '-ABBR\"');

          // restriction by workflow status (optional)
          if ($scope.paging['abbr'].workflowStatus) {
            clauses.push('workflowStatus:' + $scope.paging[type].workflowStatus);
          }

          // construct the query restriction
          pfs.queryRestriction = '';
          for (var i = 0; i < clauses.length; i++) {
            pfs.queryRestriction += clauses[i] + (i < clauses.length - 1 ? ' AND ' : '');
          }

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
            type : $scope.selected.metadata.terminology.preferredName + '-ABBR',
            key : null,
            value : null
          }
          $scope.setAbbreviationEdited(abbr);
        }

        $scope.cancelAbbreviation = function() {
          $scope.setAbbreviationEdited(null);
        }

        $scope.addAbbreviation = function(abbr) {
          abbrService.addAbbreviation(abbr).then(function(newAbbr) {
            findAbbreviations();
            $scope.setAbbreviationViewed(newAbbr);

            // perform all actions triggered by abbreviation change
            $scope.processAbbreviationChange();

          });
        }

        $scope.updateAbbreviation = function(abbr) {
          abbrService.updateAbbreviation(abbr).then(function() {

            // perform all actions triggered by abbreviation change
            $scope.processAbbreviationChange();
          });
        }

        $scope.removeAbbreviation = function(abbr) {
          console.debug('remove abbreviation', abbr);
          abbrService.removeAbbreviation(abbr.id).then(function() {

            // clear edited abbreviation
            $scope.setAbbreviationEdited(null);
            
            console.debug('cycling over review list', $scope.lists.abbrsReviewed);
            
            // remove the abbreviation from the review list if present
            for (var i = 0; i < $scope.lists.abbrsReviewed.typeKeyValues.length; i++) {
              console.debug('checking', abbr.id, $scope.lists.abbrsReviewed.typeKeyValues[i].id)
              if ($scope.lists.abbrsReviewed.typeKeyValues[i].id == abbr.id) {
            
                $scope.lists.abbrsReviewed.typeKeyValues.splice(i, 1);
                console.debug('-> found, new list ', $scope.lists.abbrsReviewed);
              }
            }

            // perform all actions triggered by abbreviation change
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
          if (!abbr) {
            deferred.reject();
          }

          // if starting abbreviation supplied, initialize list
          if (abbr) {
            console.debug('initializing from ', abbr);
            $scope.lists.abbrsReviewed = {
              'typeKeyValues' : [ abbr ],
              'totalCount' : 1
            };
          } else {
            console.debug('review from abbr list', $scope.lists.abbrsReviewed);
          }

          abbrService.getReviewForAbbreviations($scope.lists.abbrsReviewed.typeKeyValues).then(
            function(abbrReviews) {
              $scope.lists.abbrsReviewed = abbrReviews;

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
          return deferred.promise;
        }

        // paging done client-side
        function getPagedReview() {
          console.debug('getPagedReview', $scope.lists.abbrsReviewed.typeKeyValues,
            $scope.paging['review']);
          $scope.lists.pagedReview = utilService.getPagedArray(
            $scope.lists.abbrsReviewed.typeKeyValues, $scope.paging['review']);
        }

        // NOTE: Helper function intended for DEBUG use only
        // recomputes workflow status for ALL abbreviations in type
        $scope.recomputeAllReviewStatuses = function() {
          abbrService.computeReviewStatuses(
            $scope.selected.metadata.terminology.preferredName + '-ABBR').then(function() {
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
              deferred.push(abbrService.updateAbbreviation(abbr));
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
          angular.forEach($scope.lists.abbrsReviewed.typeKeyValues, function(abbr) {
            if (abbr.workflowStatus == 'NEEDS_REVIEW') {
              abbr.workflowStatus = 'NEW';

              // NOTE: skip checks to prevent NEEDS_REVIEW from being re-applied
              deferred.push(abbrService.updateAbbreviation(abbr, true));
            }
          })
          console.debug('deferred', deferred);
          $q.all(deferred).then(function() {
            findAbbreviations();
            gpService.decrement();
            $scope.lists.abbrsReviewed = null;
          });
        }

        //
        // Import/export
        //
        $scope.validateAbbreviationsFile = function() {
          if (!$scope.selected.file) {
            return;
          }
          abbrService.validateAbbreviationsFile(
            $scope.selected.metadata.terminology.preferredName + '-ABBR', $scope.selected.file)
            .then(function(response) {
              $scope.validateAbbreviationsFileResults = response;
            })
        }

        $scope.importAbbreviationsFile = function() {
          abbrService.importAbbreviationsFile(
            $scope.selected.metadata.terminology.preferredName + '-ABBR', $scope.selected.file)
            .then(function(response) {
              $scope.importAbbreviationsFileResults = response;
              $scope.findAbbreviations();
            })
        };

        $scope.exportAbbreviations = function() {
          abbrService.exportAbbreviations(
            $scope.selected.metadata.terminology.preferredName + '-ABBR',
            $scope.selected.delimiter, $scope.selected.exportReadyOnly).then(function() {
            // do nothing
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

          metadataService.getTerminologies().then(
          // Success
          function(data) {

            $scope.lists.terminologies = data.terminologies;
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
          $scope.clearImportResults();
          $scope.findAbbreviations();
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
