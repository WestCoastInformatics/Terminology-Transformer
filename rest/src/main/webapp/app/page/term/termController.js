// Administration controller
tsApp
  .controller(
    'TermCtrl',
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
      'termService',
      'transformService',
      'editService',
      function($scope, $http, $q, $location, $uibModal, gpService, utilService, tabService,
        configureService, securityService, metadataService, projectService, contentService,
        termService, transformService, editService) {

        console.debug('configure TermsCtrl');

        // Set up tabs and controller
        tabService.setShowing(true);
        utilService.clearError();
        tabService.setSelectedTabByLabel('Raw Terms');
        $scope.user = securityService.getUser();
        projectService.getUserHasAnyRole();

        // always enable simple editing
        editService.enableEditing();

        $scope.selected = {
          metadata : metadataService.getModel(),
          term : null,
          concept : null,
          project : null,
          tab : 'Edit'
        };

        $scope.lists = {
          projects : [],
          terminologies : [],
          concepts : [],

          workflowStatus : [ 'Covered', 'Needs Review', 'Excluded' ],

          pageSizes : [ {
            key : 5,
            value : "5"
          }, {
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
        $scope.paging['term'] = utilService.getPaging();
        $scope.paging['term'].sortField = 'key';
        $scope.paging['term'].pageSize = 10;
        $scope.paging['term'].callbacks = {
          getPagedList : findTerms
        };
        $scope.paging['concept'] = utilService.getPaging();
        $scope.paging['concept'].showFilter = true;
        $scope.paging['concept'].pageSize = 5;
        $scope.paging['concept'].callbacks = {
          getPagedList : findConcepts
        };

        //
        // Local variables
        //
        $scope.local = {
          newConcept : {
            pt : null,
            sys : [],
            feature : null
          }
        }

        // pass utility functions to scope
        $scope.toShortDate = utilService.toShortDate;

        $scope.findTerms = function(term) {
          findTerms(term);
        }
        function findTerms(term) {
          if (!$scope.selected.metadata.terminology) {
            return;
          }
          if (term) {
            $scope.setTermViewed(term);
          }
          var pfs = prepTermPfs('term');
          console.debug('pfs', pfs);
          // retrieval call
          termService.findTerms($scope.paging['term'].filter, $scope.selected.project.id,
            $scope.paging['term'].filterType, pfs).then(function(response) {
            $scope.lists.terms = response;
            console.debug('term', $scope.lists.terms);
          });

          // status calls
          var pfsCovered = prepTermPfs('term');
          pfsCovered.maxResults = 0;
          pfsCovered.startIndex = 0;
          pfsCovered.queryRestriction = 'workflowStatus:PUBLISHED';
          termService.findTerms($scope.paging['term'].filter, $scope.selected.project.id,
            $scope.paging['term'].filterType, pfsCovered).then(function(response) {
            $scope.paging['term'].hasCovered = response.totalCount > 0;
          });

          // status calls
          var pfsNeedsReview = prepTermPfs('term');
          pfsNeedsReview.maxResults = 0;
          pfsNeedsReview.startIndex = 0;
          pfsNeedsReview.queryRestriction = 'workflowStatus:NEEDS_REVIEW';
          termService.findTerms($scope.paging['term'].filter, $scope.selected.project.id,
            $scope.paging['term'].filterType, pfsNeedsReview).then(function(response) {
            $scope.paging['term'].hasNeedsReview = response.totalCount > 0;
          });

          // status calls
          var pfsExcluded = prepTermPfs('term');
          pfsExcluded.maxResults = 0;
          pfsExcluded.startIndex = 0;
          pfsExcluded.queryRestriction = 'workflowStatus:REVIEW_DONE';
          termService.findTerms($scope.paging['term'].filter, $scope.selected.project.id,
            $scope.paging['term'].filterType, pfsExcluded).then(function(response) {
            $scope.paging['term'].hasExcluded = response.totalCount > 0;
          });

        }

        function prepTermPfs(type) {
          var paging = $scope.paging[type];
          var pfs = {
            startIndex : (paging.page - 1) * paging.pageSize,
            maxResults : paging.pageSize,
            sortField : paging.sortField,
            ascending : paging.sortAscending,
            queryRestriction : paging.workflowStatus ? 'workflowStatus:' + paging.workflowStatus
              : null
          };
          return pfs;
        }

        $scope.findConcepts = function(concept) {
          findConcepts(concept);
        }
        function findConcepts(concept) {
          if (!$scope.selected.metadata.terminology) {
            return;
          }
          if (concept) {
            $scope.setConceptEdited(concept);
          }

          // only display values if filter text entered (no blank search)
          if (!$scope.paging['concept'].filter) {
            $scope.lists.concepts = null;
          }
          var pfs = prepConceptPfs('concept');

          console.debug('findConcepts', pfs, $scope.paging['concept']);

          contentService.getConceptsForQuery($scope.paging['concept'].filter,
            $scope.selected.metadata.terminology.terminology,
            $scope.selected.metadata.terminology.version, $scope.selected.project.id, pfs).then(
            function(response) {
              $scope.lists.concepts = response;
              $scope.lists.concepts.totalCount = response.totalCount;
              console.debug('concepts', $scope.lists.concepts);
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

          return pfs;
        }

        $scope.setTermViewed = function(term) {
          console.debug('set term viewed', term);
          $scope.selected.editTab = 'Edit';
          $scope.selected.termViewed = term;
          $scope.processTerm(term);
        }

        $scope.createTerm = function() {
          var term = {
            type : null,
            key : null,
            value : null
          }
          $scope.setTermEdited(term);
        }

        $scope.cancelTerm = function() {
          $scope.setTermEdited(null);
        }

        $scope.addTerm = function(term) {
          termService.addTerm(term, $scope.selected.project.id).then(function(newTerm) {

            $scope.setTermViewed(newTerm);

            // perform all actions triggered by term change
            $scope.processTermChange();

            $scope.atom = {
              workflowStatus : 'NEEDS_REVIEW',
              publishable : true,
              language : 'ENG'
            };

          });
        }

        $scope.updateTerm = function(term) {
          termService.updateTerm(term, $scope.selected.project.id).then(function() {

            // perform all actions triggered by term change
            $scope.processTermChange();
          });
        }

        $scope.removeTerms = function() {
          var pfs = prepTermPfs('term');
          pfs.startIndex = -1;
          pfs.maxResults = -1;

          termService.findTerms($scope.paging['term'].filter, $scope.selected.project.id,
            $scope.paging['term'].filterType, pfs).then(function(response) {
            var ids = response.typeKeyValues.map(function(t) {
              return t.id;
            });
            termService.removeTerms(ids, $scope.selected.project.id).then(function() {
              findTerms();
            });
          });
        }

        $scope.removeTerm = function(term) {
          console.debug('remove term', term);
          termService.removeTerm(term.id, $scope.selected.project.id).then(function() {
            findTerms();
          });
        }

        $scope.callbacks = {
          getComponent : findConcepts
        };

        // add simple editing callbacks if enabled
        if (editService.canEdit()) {
          console.debug('CAN EDIT');
          utilService.extendCallbacks($scope.callbacks, editService.getCallbacks());
        }

        function setConceptEdited(concept) {
          console.debug('CALLBACK: ', concept);
          $scope.setConceptEdited(concept);
        }
        $scope.setConceptEdited = function(concept) {

          console.debug('SET EDITED: ', concept);

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

        $scope.editConcept = function(concept) {
          console.debug('edit concept', concept);
          var ptFound = false;
          angular.forEach(concept.atoms, function(atom) {
            if (atom.termType == 'PT') {
              console.debug('found pt');
              ptFound = true;
            }
          })
          $scope.selected.defaultTermType = ptFound ? 'SY' : 'PT';

          console.debug('default term group', $scope.selected.defaultTermType);

          $scope.setConceptEdited(concept);
        }

        $scope.createConcept = function() {

          console.debug('create concept');

          $scope.selected.defaultTermType = 'PT';
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

        //
        // Display functions
        //

        $scope.toggleCoveredMode = function() {
          $scope.paging['term'].workflowStatus = $scope.paging['term'].workflowStatus == 'PUBLISHED' ? null
            : 'PUBLISHED';
          $scope.findTerms();
        }

        $scope.toggleReviewMode = function() {
          $scope.paging['term'].workflowStatus = $scope.paging['term'].workflowStatus == 'NEEDS_REVIEW' ? null
            : 'NEEDS_REVIEW';
          $scope.findTerms();
        }

        $scope.toggleExcludedMode = function() {
          $scope.paging['term'].workflowStatus = $scope.paging['term'].workflowStatus == 'REVIEW_DONE' ? null
            : 'REVIEW_DONE';
          $scope.findTerms();
        }

        //
        // Utility functions
        // 

        // Table sorting mechanism
        $scope.setSortField = function(table, field, object) {
          utilService.setSortField('' + table, field, $scope.paging);

          // retrieve the correct table
          if (table === 'term') {
            findTerms();
          }
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
              $scope.lists.features = stys;
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
          $scope.findTerms();
        };

        //
        // Import/Export
        //
        $scope.importTermsFile = function() {
          console.debug('selected', $scope.selected)
          termService.importTermsFile($scope.selected.project.id, $scope.selected.file).then(
            function(response) {
              $scope.importTermsFileResults = response;
              $scope.findTerms();
            })
        };

        
        //
        // Term Processing
        //
        $scope.processTerm = function(term) {
          console.debug('process all terms', $scope.selected);

          termService.processTerm($scope.selected.project.id, term).then(
            function(response) {
              console.debug('process response', response);
              if (!response || !Array.isArray(response.scoredDataContextTuples)
                || response.scoredDataContextTuples.length == 1) {
                $scope.selected.term = response.scoredDataContextTuples[0];
              } else {
                utilService.setError('Process response invalid');
              }
            });
        }

        $scope.processAllTerms = function() {
          console.debug('process terms', $scope.selected);

          termService.processAllTerms($scope.selected.project.id).then(function() {
            // TODO Consider validation result for output display
          })
        }
        //
        // Initialize - DO NOT PUT ANYTHING AFTER THIS SECTION OTHER THAN CONFIG CHECK
        //
        $scope.initialize = function() {
          securityService.saveTab($scope.user.userPreferences, '/term');
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
