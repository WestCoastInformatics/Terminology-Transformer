// Simple Edit view controller
tsApp
  .controller(
    'ConceptCtrl',
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
      'contentService',
      'editService',
      'mldpService',
      function($scope, $http, $q, $location, $uibModal, gpService, utilService, tabService,
        configureService, securityService, metadataService, projectService, contentService,
        editService, mldpService) {
        console.debug('configure ConceptCtrl');

        // Set up tabs and controller
        tabService.setShowing(true);
        utilService.clearError();
        tabService.setSelectedTabByLabel('Concepts');
        $scope.user = securityService.getUser();
        projectService.getUserHasAnyRole();

        // always enable simple editing
        editService.enableEditing();

        $scope.selected = {
          metadata : metadataService.getModel(),
          concept : null,
          project : null
        };

        // Scope variables
        $scope.selected = {
          project : null,
          terminology : null,
          metadata : metadataService.getModel(),

          // the concept selected in the browse table
          conceptViewed : null,

          // the concept selected for editing
          conceptEdited : null,

          // edit/import/export tab selection
          editTab : 'Edit'
        }

        $scope.lists = {
          projects : [],
          terminologies : [],
          conceptsViewed : [],
          conceptsReviewed : [],
          fileTypesFilter : '.csv',
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
          } ],
          filterTypes : [],
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
        $scope.paging['concept'] = utilService.getPaging();
        $scope.paging['concept'].sortField = 'terminologyId';
        $scope.paging['concept'].workflowStatus = null;
        $scope.paging['concept'].filterType = null;
        $scope.paging['concept'].pageSize = 10;
        $scope.paging['concept'].callbacks = {
          getPagedList : findConcepts
        };
        $scope.paging['review'] = utilService.getPaging();
        $scope.paging['review'].sortField = 'key';
        $scope.paging['review'].workflowStatus = null;
        $scope.paging['review'].callbacks = {
          getPagedList : getPagedReview
        };

        $scope.configureCallbacks = function() {
          console.debug('*** CONFIGURE CALLBACKS ***');

          //
          // Local scope functions pertaining to concept retrieval
          //
          $scope.callbacks = {
            getComponent : setConceptEdited
          };

          //
          // Concept report callbacks
          //

          // add content callbacks for special content retrieval (relationships,
          // mappings, etc.)
          utilService.extendCallbacks($scope.callbacks, contentService.getCallbacks());

          // add simple editing callbacks if enabled
          if (editService.canEdit()) {
            console.debug('CAN EDIT');
            utilService.extendCallbacks($scope.callbacks, editService.getCallbacks());
          }

        };

        // pass utility functions to scope
        $scope.toDate = utilService.toDate;

        $scope.findConcepts = function(concept) {
          findConcepts(concept);
        }
        function findConcepts(concept) {
          if (!$scope.selected.metadata.terminology) {
            return;
          }
          if (concept) {
            $scope.setConceptViewed(concept);
          }
          var searchParams = prepConceptPfs('concept');

          console.debug('findConcepts', searchParams);

          contentService.getConceptsForQuery($scope.paging['concept'].filter,
            $scope.selected.metadata.terminology.terminology,
            $scope.selected.metadata.terminology.version, $scope.selected.project.id, searchParams)
            .then(function(response) {
              $scope.lists.conceptsViewed = response;
              console.debug('concepts', $scope.lists.conceptsViewed);
            });

          //  truncated NEEDS_REVIEW and NEW calls
          searchParams = prepConceptPfs('concept');
          searchParams.maxResults = 0;
          searchParams.queryRestriction = 'workflowStatus:NEEDS_REVIEW';
          contentService.getConceptsForQuery($scope.paging['concept'].filter,
            $scope.selected.metadata.terminology.terminology,
            $scope.selected.metadata.terminology.version, $scope.selected.project.id, searchParams)
            .then(function(response) {
              $scope.paging['concept'].hasNeedsReview = response.totalCount > 0;
            });
          searchParams = prepConceptPfs('concept');
          searchParams.maxResults = 0;
          searchParams.queryRestriction = 'workflowStatus:NEW';
          contentService.getConceptsForQuery($scope.paging['concept'].filter,
            $scope.selected.metadata.terminology.terminology,
            $scope.selected.metadata.terminology.version, $scope.selected.project.id, searchParams)
            .then(function(response) {
              $scope.paging['concept'].hasNew = response.totalCount > 0;
            });
        }

        function prepConceptPfs(type) {
          var paging = $scope.paging[type];
          var pfs = {
            startIndex : (paging.page - 1) * paging.pageSize,
            maxResults : paging.pageSize,
            sortField : paging.sortField,
            ascending : paging.sortAscending,
            queryRestriction : null
          };

          switch ($scope.paging['concept'].workflowStatus) {
          case 'PUBLISHED':
            pfs.queryRestriction = 'workflowStatus:PUBLISHED';
            break;
          case 'NEW':
            pfs.queryRestriction = 'workflowStatus:NEW';
            break;
          case 'NEEDS_REVIEW':
            pfs.queryRestriction = 'workflowStatus:NEEDS_REVIEW';
            break;
          default:
            // do nothing
          }
          return pfs;
        }

        $scope.setConceptViewed = function(concept) {
          console.debug('set concept', concept);
          $scope.selected.editTab = 'Edit';
          $scope.selected.conceptViewed = concept;
          $scope.setConceptEdited(concept);

        }

        function processConceptChange() {
          if ($scope.selected.component) {
            setConceptEdited($scope.selected.component);
          }
        }

        function setConceptEdited(concept) {
          $scope.setConceptEdited(concept);
        }
        $scope.setConceptEdited = function(concept) {

          // NOTE: UMLS TermServer report uses "component" instead of "concept"
          // NOTE: Always re-retrieve concept for up-to-date state
          contentService.getConcept(concept.id, $scope.selected.project.id).then(
            function(response) {
              $scope.selected.component = response;
              contentService.validateConcept($scope.selected.project.id, concept, null).then(
                function(response) {
                  $scope.selected.review = response;
                });
            }, function(error) {
              console.error('error', error);
            });

        }

        $scope.createConcept = function() {
          var concept = {
            type : $scope.selected.metadata.terminology.organizingClassType,
            terminologyId : null,
            terminology : $scope.selected.metadata.terminology.terminology,
            version : $scope.selected.metadata.terminology.version,
            name : '(New Concept)',
            atoms : [],
            semanticTypes : []
          }
          editService.addConcept($scope.selected.project.id, null, concept).then(
            function(newConcept) {
              $scope.setConceptEdited(newConcept);
            })
        }

        $scope.cancelConcept = function() {
          $scope.setConceptEdited(null);
        }

        $scope.addConcept = function(concept) {
          contentService.addConcept(concept).then(function(newConcept) {
            findConcepts();
            $scope.setConceptViewed(newConcept);

            // perform all actions triggered by concept change
            $scope.processConceptChange();

          });
        }

        $scope.updateConcept = function(concept) {
          contentService.updateConcept(concept).then(function() {

            // perform all actions triggered by concept change
            $scope.processConceptChange();
          });
        }

        $scope.removeConcepts = function(ids) {
          var pfs = prepConceptPfs('concept');
          pfs.startIndex = -1;
          pfs.maxResults = -1;

          contentService.removeConcepts(ids)
            .then(
              function() {

                // if no ids specified (remove all) or in list, clear edited
                if (!ids
                  || ($scope.selected.conceptEdited && ids
                    .indexOf($scope.selected.conceptEdited.id) != -1)) {
                  $scope.selected.component = null;
                }
                // perform all actions triggered by concept change
                $scope.refreshConceptEdited();
              });
        }

        $scope.removeConcept = function(concept) {
          console.debug('remove concept', concept);
          editService.removeConcept($scope.selected.project.id, concept.id).then(function() {
            if ($scope.conceptEdited && $scope.conceptEdited.id == concept.id) {
              $scope.conceptEdited = null;
            } else {
              processConceptChange();
            }
          });

        }

        //
        // Display functions
        //

        $scope.toggleNewMode = function() {
          $scope.paging['concept'].workflowStatus = $scope.paging['concept'].workflowStatus == 'NEW' ? null
            : 'NEW';
          $scope.findConcepts();
        }

        $scope.toggleReviewMode = function() {
          $scope.paging['concept'].workflowStatus = $scope.paging['concept'].workflowStatus == 'NEEDS_REVIEW' ? null
            : 'NEEDS_REVIEW';
          $scope.findConcepts();
        }
        //
        // Review functions
        //

        $scope.getReviewForConcepts = function(concept) {
          var deferred = $q.defer();

          // if starting concept supplied, initialize list
          if (concept) {
            console.debug('getReviewForConcepts: iitializing from ', concept);
            $scope.lists.conceptsReviewed = {
              'typeKeyValues' : [ concept ],
              'totalCount' : 1
            };
          } else {
            console.debug('getReviewForConcepts from concept list', $scope.lists.conceptsReviewed);
          }

          if (!$scope.lists.conceptsReviewed) {
            deferred.reject('No concepts');
          } else {

            contentService.getReviewForConcepts($scope.lists.conceptsReviewed.typeKeyValues).then(
              function(conceptReviews) {
                $scope.lists.conceptsReviewed = conceptReviews;

                // on review load, find and select for editing the viewed concept in the review list
                angular.forEach($scope.lists.conceptsReviewed.typeKeyValues,
                  function(conceptReview) {
                    if (conceptReview.id == $scope.selected.conceptViewed.id) {
                      $scope.setConceptEditedFromSearchResult(conceptReview);
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
          console.debug('getPagedReview', $scope.lists.conceptsReviewed.typeKeyValues,
            $scope.paging['review']);
          $scope.lists.pagedReview = utilService.getPagedArray(
            $scope.lists.conceptsReviewed.typeKeyValues, $scope.paging['review']);
        }

        // NOTE: Helper function intended for DEBUG use only
        // recomputes workflow status for ALL concepts in type
        $scope.recomputeAllReviewStatuses = function() {
          contentService.computeReviewStatuses(
            $scope.selected.metadata.terminology.terminology + '-ABBR').then(function() {
            $scope.findConcepts();
          });
        }

        // recompute review status for all items currently in graph other than currently edited
        // intended for use after add, update, or remove
        $scope.processConceptChange = function() {

          var deferred = [];

          // check current review table for possible changes to other concepts
          angular
            .forEach(
              $scope.lists.conceptsReviewed.typeKeyValues,
              function(concept) {
                // call update to force re-check (unless the currently edited concept)
                if (concept.workflowStatus == 'NEEDS_REVIEW'
                  && (!$scope.selected.conceptEdited || $scope.selected.conceptEdited.id != concept.id)) {
                  deferred.push(contentService.updateConcept(concept));
                }
              });

          // after all recomputation, get new review and perform new find
          $q.all(deferred).then(function() {
            $scope.getReviewForConcepts();
            findConcepts();
          }, function() {
            $scope.getReviewForConcepts();
            findConcepts();
          })
        }

        $scope.finishReview = function() {
          var deferred = [];
          gpService.increment();
          angular.forEach($scope.lists.conceptsReviewed.typeKeyValues, function(concept) {
            if (concept.workflowStatus == 'NEEDS_REVIEW') {
              concept.workflowStatus = 'NEW';

              // NOTE: skip checks to prevent NEEDS_REVIEW from being re-applied
              deferred.push(contentService.updateConcept(concept, true));
            }
          })
          console.debug('deferred', deferred);
          $q.all(deferred).then(function() {
            findConcepts();
            gpService.decrement();
            $scope.lists.conceptsReviewed = null;
          });
        }

        //
        // Import/export
        //

        $scope.importConceptsFile = function() {
          mldpService.importConcepts($scope.selected.project.id, $scope.selected.file).then(
            function() {
              findConcepts();
            })
        };

        $scope.exportConceptsFile = function() {
          mldpService.exportConcepts($scope.selected.project, $scope.selected.exportAcceptNew,
            $scope.selected.exportReadyOnly).then(function() {
            // do nothing
          });
        };

        $scope.clearImportResults = function() {
          $scope.importConceptsFileResults = null;
          $scope.validateConceptsFileResults = null;
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
          if (table === 'concept') {
            findConcepts();
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
          function(termData) {

            projectService.getProjectsForUser($scope.user).then(function(projectData) {
              console.debug('*** results', projectData);
              $scope.lists.projects = projectData.projects;
              $scope.lists.terminologies = termData.terminologies;
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

            })

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

          console.debug('**** Set Terminology ****', terminology, $scope.lists)
          // Set shared model (may already be set)
          metadataService.setTerminology(terminology);

          // get all metadata for this terminology
          metadataService.getAllMetadata(terminology.terminology, terminology.version).then(
            function(data) {
              console.debug('All Metadata', data);
              metadataService.setModel(data);
            });

          // get the semantic types for this terminolog y
          metadataService.getSemanticTypes(terminology.terminology, terminology.version).then(
            function(stys) {
              $scope.lists.stys = stys;
            });

          // Choose a project
          for (var i = 0; i < $scope.lists.projects.length; i++) {
            var p = $scope.lists.projects[i];
            console.debug('checking against ', p.terminology);
            if (p.terminology == terminology.terminology) {
              $scope.selected.project = p;
            }

            console.debug('PROJECT', $scope.selected.project);
          }
          if (!$scope.selected.project) {
            utilService
              .setError('Configuration Error: Terminology has no project; add via Admin tab')
          }

          // update terminology
          $scope.user.userPreferences.lastTerminology = terminology.terminology;
          $scope.user.lastProjectId, $scope.selected.projectId;
          securityService.updateUserPreferences($scope.user.userPreferences);

          // perform actions
          $scope.clearImportResults();
          $scope.findConcepts();
        };

        //
        // Initialize - DO NOT PUT ANYTHING AFTER THIS SECTION OTHER THAN CONFIG CHECK
        //
        $scope.initialize = function() {
          securityService.saveTab($scope.user.userPreferences, '/content');
          $scope.initMetadata();
          $scope.configureCallbacks();
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
