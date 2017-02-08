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

        $scope.display = {
          qaStatus : null,
          qaStatusType : null
        }

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
          editTab : 'Edit',

          // the QA check to run
          check : null

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
        $scope.paging['concept'].sortField = 'name';
        $scope.paging['concept'].workflowStatus = null;
        $scope.paging['concept'].semanticTypeFilter = null;
        $scope.paging['concept'].pageSize = 10;
        $scope.paging['concept'].callbacks = {
          getPagedList : findConcepts
        };

        $scope.configureCallbacks = function() {
          console.debug('*** CONFIGURE CALLBACKS ***');

          //
          // Local scope functions pertaining to concept retrieval
          //

          // when callbacks wish to retrieve component, assume a change has occurred
          // see directives atoms.js and semanticTypes.js for use of callback
          $scope.callbacks = {
            getComponent : processConceptChange
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
        $scope.toShortDate = utilService.toShortDate;

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
          var pfs = prepConceptPfs('concept');

          console.debug('findConcepts', pfs, $scope.paging['concept']);

          contentService.getConceptsForQuery($scope.paging['concept'].filter,
            $scope.selected.metadata.terminology.terminology,
            $scope.selected.metadata.terminology.version, $scope.selected.project.id, pfs)
            .then(function(response) {
              $scope.lists.conceptsViewed = response;
              console.debug('concepts', $scope.lists.conceptsViewed);
            });

          //  truncated NEEDS_REVIEW and NEW calls
          pfs = prepConceptPfs('concept');
          pfs.maxResults = 0;
          pfs.queryRestriction = 'workflowStatus:NEEDS_REVIEW';
          contentService.getConceptsForQuery($scope.paging['concept'].filter,
            $scope.selected.metadata.terminology.terminology,
            $scope.selected.metadata.terminology.version, $scope.selected.project.id, pfs)
            .then(function(response) {
              $scope.paging['concept'].hasNeedsReview = response.totalCount > 0;
            });
          pfs = prepConceptPfs('concept');
          pfs.maxResults = 0;
          pfs.queryRestriction = 'workflowStatus:NEW';
          contentService.getConceptsForQuery($scope.paging['concept'].filter,
            $scope.selected.metadata.terminology.terminology,
            $scope.selected.metadata.terminology.version, $scope.selected.project.id, pfs)
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

          // apply workflow filter
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

          // apply semantic type filter
          if ($scope.paging['concept'].semanticTypeFilter) {

            pfs.queryRestriction = (pfs.queryRestriction != null ? pfs.queryRestriction + " AND "
              : "")
              + 'semanticTypes.semanticType:' + $scope.paging['concept'].semanticTypeFilter;
          }
          return pfs;
        }

        $scope.setConceptViewed = function(concept) {
          console.debug('set concept', concept);
          $scope.selected.editTab = 'Edit';
          $scope.selected.conceptViewed = concept;
          $scope.setConceptEdited(concept);

        }

        // on concept change, re-set the edited concept and perform search
        function processConceptChange() {
          if ($scope.selected.component) {
            setConceptEdited($scope.selected.component);
          }
          findConcepts();
        }

        function setConceptEdited(concept) {
          console.debug('CALLBACK: ', concept);
          $scope.setConceptEdited(concept);
        }
        $scope.setConceptEdited = function(concept) {

          console.debug('SET EDITED: ', concept);

          $scope.selected.review = null;
          
          // if set to null, clear selected and stop
          if (!concept) {
            $scope.selected.component = null;
            return;
          }

          // NOTE: UMLS TermServer report uses "component" instead of "concept"
          // NOTE: Always re-retrieve concept for up-to-date state
          contentService.getConcept(concept.id, $scope.selected.project.id).then(
            function(response) {
              console.debug('response', response);
              $scope.selected.component = response;
              contentService.validateConcept($scope.selected.project.id, response, null).then(
                function(review) {
                  $scope.selected.review = review;
                });
            }, function(error) {
              console.error('error', error);
            });
        }

        //
        // Concept CRUD functions
        //

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
          editService.addConcept($scope.selected.project.id, concept).then(function(newConcept) {
            $scope.setConceptEdited(newConcept);
          })
        }

        $scope.cancelConcept = function() {
          $scope.setConceptEdited(null);
        }

        $scope.updateConcept = function(concept) {
          editService.updateConcept($scope.selected.project.id, concept).then(function() {
            processConceptChange();
          })
        }

        // used for removing concept from list
        // see UMLS report.js for concept removal in simple edit mode from report
        $scope.removeConcept = function(concept) {
          console.debug('remove concept', concept, $scope.selected.component);
          editService.removeConcept($scope.selected.project.id, concept.id).then(function() {
            if ($scope.selected.component && $scope.selected.component.id == concept.id) {
              $scope.selected.component = null;
            }
            processConceptChange();
          });
        }

        $scope.removeConcepts = function(ids) {
          var pfs = prepConceptPfs('concept');
          pfs.startIndex = -1;
          pfs.maxResults = -1;

          editService.removeConcepts($scope.selected.project.id, $scope.paging['concept'].filter, pfs)
            .then(
              function() {

                // if no ids specified (remove all) or in list, clear edited
                if (!ids
                  || ($scope.selected.conceptEdited && ids
                    .indexOf($scope.selected.conceptEdited.id) != -1)) {
                  $scope.selected.component = null;
                }
                // perform all actions triggered by concept change
                processConceptChange();
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
        // Validation and Review
        //

        $scope.finishReview = function() {
          var deferred = [];
          gpService.increment();
          if ($scope.selected.component.workflowStatus == 'NEEDS_REVIEW') {
            $scope.selected.component.workflowStatus = 'NEW';

            angular.forEach($scope.selected.component.atoms,
              function(atom) {
                if (atom.workflowStatus == 'NEEDS_REVIEW') {
                  atom.workflowStatus = 'NEW';
                  deferred.push(editService
                    .updateAtom($scope.selected.project.id, $scope.selected.component.id, atom));
                }
              })

            deferred.push(editService.updateConcept($scope.selected.project.id,
              $scope.selected.component, true));
          }

          console.debug('deferred', deferred);
          $q.all(deferred).then(function() {
            processConceptChange();
            gpService.decrement();

          });
        };

        $scope.performChecks = function() {
          $scope.display.qaStatus = {
            warning : 'Validating concepts in ' + $scope.selected.project.terminology
              + ($scope.selected.check ? ' for check ' + $scope.selected.check.value : '')
          };
          contentService.validateConcepts($scope.selected.project.id, null,
            $scope.selected.check ? $scope.selected.check.key : null).then(
            function(ids) {
              if (ids && ids.length > 0) {

                mldpService.putConceptsInWorkflow($scope.selected.project.id, ids, 'NEEDS_REVIEW')
                  .then(
                    function() {
                      $scope.display.qaStatus = {
                        error : ids.length + ' concepts failed validation  '
                          + ($scope.selected.check ? ' for check \'' + $scope.selected.check.value + '\'' : '')
                          + ' and were marked for review'
                      };
                      findConcepts();
                    })
              } else {
                $scope.display.qaStatus = {
                  success : 'All concepts passed validation '
                    + ($scope.selected.check ? ' for check \' ' + $scope.selected.check.value + '\'' : '')
                }
              }

            })
        }
        
        $scope.clearReviewWorkflow = function() {
          $scope.display.qaStatus = {
            warning : 'Clearing review workflow for ' + $scope.selected.project.terminology + '...'
          };
          mldpService.clearReviewWorkflow($scope.selected.project.id).then(function() {
            $scope.display.qaStatus = {
              success : 'All concepts in review moved to \'New\' status'
            }
            findConcepts();
          }, function(error) {
            $scope.display.qaStatus = null;
          });
        }

        //
        // Import/export
        //

        $scope.importConceptsFile = function() {
          mldpService.importConcepts($scope.selected.project.id, $scope.selected.file).then(
            function(response) {
              $scope.importConceptsFileResults = response;
              findConcepts();
            })
        };

        $scope.exportConceptsFile = function() {
          mldpService.exportConcepts($scope.selected.project, $scope.selected.exportAcceptNew,
            $scope.selected.exportReadyOnly).then(function() {
            if ($scope.selected.exportAcceptNew) {
              findConcepts();
            }
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

          // initialize validation check human readable map
          if ($scope.selected.project) {
            $scope.lists.checks = [];
            angular.forEach($scope.selected.project.validationChecks, function(checkId) {
              var name = checkId.replace(/([A-Z])/g, " $1");
              $scope.lists.checks.push({
                'key' : checkId,
                'value' : name.charAt(0).toUpperCase() + name.slice(1)
              })
            })
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
