// Administration controller
tsApp
  .controller(
    'ContentCtrl',
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
      function($scope, $http, $q, $location, $uibModal, gpService, utilService, tabService,
        configureService, securityService, metadataService, projectService, contentService) {
        console.debug('configure ComponentCtrl');

        // Set up tabs and controller
        tabService.setShowing(true);
        utilService.clearError();
        tabService.setSelectedTabByLabel('Content');
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

          // the component selected in the browse table
          componentViewed : null,

          // the component selected for editing
          componentEdited : null,

          // edit/import/export tab selection
          editTab : 'Edit'
        }

        $scope.lists = {
          projects : [],
          terminologies : [],
          componentsViewed : [],
          componentsReviewed : [],
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
        $scope.paging['component'] = utilService.getPaging();
        $scope.paging['component'].sortField = 'terminologyId';
        $scope.paging['component'].workflowStatus = null;
        $scope.paging['component'].filterType = null;
        $scope.paging['component'].pageSize = 10;
        $scope.paging['component'].callbacks = {
          getPagedList : findComponents
        };
        $scope.paging['review'] = utilService.getPaging();
        $scope.paging['review'].sortField = 'key';
        $scope.paging['review'].workflowStatus = null;
        $scope.paging['review'].callbacks = {
          getPagedList : getPagedReview
        };

        // pass utility functions to scope
        $scope.toDate = utilService.toDate;

        $scope.findComponents = function(component) {
          findComponents(component);
        }
        function findComponents(component) {
          if (!$scope.selected.metadata.terminology) {
            return;
          }
          if (component) {
            $scope.setComponentViewed(component);
          }
          var pfs = prepComponentPfs('component');
          console.debug('pfs', pfs);
          // retrieval call
          // findComponentsAsList = function(queryStr, type, terminology, version, searchParams) {

          console.debug('findComponents', $scope.paging['component'], $scope.selected);

          var searchParams = {
            page : $scope.paging['component'].page,
            pageSize : $scope.paging['component'].pageSize
          }

          contentService.findComponentsAsList($scope.paging['component'].filter,
            $scope.selected.metadata.terminology.organizingClassType,
            $scope.selected.metadata.terminology.preferredName,
            $scope.selected.metadata.terminology.version, searchParams).then(function(response) {
            $scope.lists.componentsViewed = response;
            console.debug('components', $scope.lists.componentsViewed);
          });

          // truncated NEEDS_REVIEW and NEW calls
          /*
                     * pfs = prepComponentPfs('component'); pfs.maxResults = 0;
                     * contentService.findComponents('workflowStatus:NEEDS_REVIEW',
                     * $scope.paging['component'].filterType, pfs).then(function(response) {
                     * $scope.paging['component'].hasNeedsReview = response.totalCount > 0; });
                     * contentService .findComponents('workflowStatus:NEW',
                     * $scope.paging['component'].filterType, pfs).then( function(response) {
                     * $scope.paging['component'].hasNew = response.totalCount > 0; });
                     */

        }

        function prepComponentPfs(type) {
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
          if ($scope.paging['component'].workflowStatus) {
            clauses.push('workflowStatus:' + $scope.paging[type].workflowStatus);
          }

          // construct the query restriction
          pfs.queryRestriction = '';
          for (var i = 0; i < clauses.length; i++) {
            pfs.queryRestriction += clauses[i] + (i < clauses.length - 1 ? ' AND ' : '');
          }

          return pfs;
        }

        $scope.setComponentViewed = function(component) {
          console.debug('set component', component);
          $scope.selected.editTab = 'Edit';
          $scope.selected.componentViewed = component;
          $scope.setComponentEdited(component);

        }

        $scope.setComponentEdited = function(searchResult) {
          // NOTE: findConcepts returns search result, need to retrieve
          // NOTE: UMLS TermServer report uses "selected" structure instead of component directly
          contentService.getComponent(searchResult, $scope.selected.project.id).then(
            function(component) {
              console.debug('edited component', component);
              $scope.selected.component = component;
            })
        }

        $scope.createComponent = function() {
          var component = {
            type : $scope.selected.metadata.terminology.preferredName + '-ABBR',
            key : null,
            value : null
          }
          $scope.setComponentEdited(component);
        }

        $scope.cancelComponent = function() {
          $scope.setComponentEdited(null);
        }

        $scope.addComponent = function(component) {
          contentService.addComponent(component).then(function(newComponent) {
            findComponents();
            $scope.setComponentViewed(newComponent);

            // perform all actions triggered by component change
            $scope.processComponentChange();

          });
        }

        $scope.updateComponent = function(component) {
          contentService.updateComponent(component).then(function() {

            // perform all actions triggered by component change
            $scope.processComponentChange();
          });
        }

        $scope.removeComponents = function() {
          var pfs = prepComponentPfs('component');
          pfs.startIndex = -1;
          pfs.maxResults = -1;

          contentService.findComponents($scope.paging['component'].filter,
            $scope.paging['component'].filterType, pfs).then(
            function(response) {
              var ids = response.typeKeyValues.map(function(t) {
                return t.id;
              });
              contentService.removeComponents(ids).then(
                function() {
                  findComponents();

                  // clear edited component
                  if ($scope.selected.componentEdited
                    && ids.indexOf($scope.selected.componentEdited.id) != -1) {
                    $scope.setComponentEdited(null);
                  }

                  console.debug('cycling over review list', $scope.lists.componentsReviewed);

                  // remove the component from the review list if present
                  for (var i = 0; i < $scope.lists.componentsReviewed.typeKeyValues.length; i++) {
                    console.debug('checking', ids,
                      $scope.lists.componentsReviewed.typeKeyValues[i].id)
                    if (ids.indexOf($scope.lists.componentsReviewed.typeKeyValues[i].id) != -1) {

                      $scope.lists.componentsReviewed.typeKeyValues.splice(i--, 1);
                      console.debug('-> found, new list ', $scope.lists.componentsReviewed);
                    }
                  }

                  $scope.processComponentChange();
                });
            });
        }

        $scope.removeComponent = function(component) {
          console.debug('remove component', component);
          contentService.removeComponent(component.id).then(
            function() {

              // clear edited component
              $scope.setComponentEdited(null);

              console.debug('cycling over review list', $scope.lists.componentsReviewed);

              // remove the component from the review list if present
              for (var i = 0; i < $scope.lists.componentsReviewed.typeKeyValues.length; i++) {
                console.debug('checking', component.id,
                  $scope.lists.componentsReviewed.typeKeyValues[i].id)
                if ($scope.lists.componentsReviewed.typeKeyValues[i].id == component.id) {

                  $scope.lists.componentsReviewed.typeKeyValues.splice(i, 1);
                  console.debug('-> found, new list ', $scope.lists.componentsReviewed);
                }
              }

              // perform all actions triggered by component change
              console.debug('REMOVE_ABBREVIATION: PROCESS ', $scope.lists.componentsReviewed);
              $scope.processComponentChange();
            });
        }

        //
        // Display functions
        //

        $scope.toggleNewMode = function() {
          $scope.paging['component'].workflowStatus = $scope.paging['component'].workflowStatus == 'NEW' ? null
            : 'NEW';
          $scope.findComponents();
        }

        $scope.toggleReviewMode = function() {
          $scope.paging['component'].workflowStatus = $scope.paging['component'].workflowStatus == 'NEEDS_REVIEW' ? null
            : 'NEEDS_REVIEW';
          $scope.findComponents();
        }
        //
        // Review functions
        //

        $scope.getReviewForComponents = function(component) {
          var deferred = $q.defer();

          // if starting component supplied, initialize list
          if (component) {
            console.debug('getReviewForComponents: iitializing from ', component);
            $scope.lists.componentsReviewed = {
              'typeKeyValues' : [ component ],
              'totalCount' : 1
            };
          } else {
            console.debug('getReviewForComponents from component list',
              $scope.lists.componentsReviewed);
          }

          if (!$scope.lists.componentsReviewed) {
            deferred.reject('No components');
          } else {

            contentService.getReviewForComponents($scope.lists.componentsReviewed.typeKeyValues)
              .then(
                function(componentReviews) {
                  $scope.lists.componentsReviewed = componentReviews;

                  // on review load, find and select for editing the viewed component in the review list
                  angular.forEach($scope.lists.componentsReviewed.typeKeyValues, function(
                    componentReview) {
                    if (componentReview.id == $scope.selected.componentViewed.id) {
                      $scope.setComponentEdited(componentReview);
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
          console.debug('getPagedReview', $scope.lists.componentsReviewed.typeKeyValues,
            $scope.paging['review']);
          $scope.lists.pagedReview = utilService.getPagedArray(
            $scope.lists.componentsReviewed.typeKeyValues, $scope.paging['review']);
        }

        // NOTE: Helper function intended for DEBUG use only
        // recomputes workflow status for ALL components in type
        $scope.recomputeAllReviewStatuses = function() {
          contentService.computeReviewStatuses(
            $scope.selected.metadata.terminology.preferredName + '-ABBR').then(function() {
            $scope.findComponents();
          });
        }

        // recompute review status for all items currently in graph other than currently edited
        // intended for use after add, update, or remove
        $scope.processComponentChange = function() {

          var deferred = [];

          // check current review table for possible changes to other components
          angular
            .forEach(
              $scope.lists.componentsReviewed.typeKeyValues,
              function(component) {
                // call update to force re-check (unless the currently edited component)
                if (component.workflowStatus == 'NEEDS_REVIEW'
                  && (!$scope.selected.componentEdited || $scope.selected.componentEdited.id != component.id)) {
                  deferred.push(contentService.updateComponent(component));
                }
              });

          // after all recomputation, get new review and perform new find
          $q.all(deferred).then(function() {
            $scope.getReviewForComponents();
            findComponents();
          }, function() {
            $scope.getReviewForComponents();
            findComponents();
          })
        }

        $scope.finishReview = function() {
          var deferred = [];
          gpService.increment();
          angular.forEach($scope.lists.componentsReviewed.typeKeyValues, function(component) {
            if (component.workflowStatus == 'NEEDS_REVIEW') {
              component.workflowStatus = 'NEW';

              // NOTE: skip checks to prevent NEEDS_REVIEW from being re-applied
              deferred.push(contentService.updateComponent(component, true));
            }
          })
          console.debug('deferred', deferred);
          $q.all(deferred).then(function() {
            findComponents();
            gpService.decrement();
            $scope.lists.componentsReviewed = null;
          });
        }

        //
        // Import/export
        //
        $scope.validateComponentsFile = function() {
          if (!$scope.selected.file) {
            return;
          }
          contentService.validateComponentsFile(
            $scope.selected.metadata.terminology.preferredName + '-ABBR', $scope.selected.file)
            .then(function(response) {
              $scope.validateComponentsFileResults = response;
            })
        }

        $scope.importComponentsFile = function() {
          contentService.importComponentsFile(
            $scope.selected.metadata.terminology.preferredName + '-ABBR', $scope.selected.file)
            .then(function(response) {
              $scope.importComponentsFileResults = response;
              $scope.findComponents();
            })
        };

        $scope.exportComponents = function() {
          contentService.exportComponents(
            $scope.selected.metadata.terminology.preferredName + '-ABBR',
            $scope.selected.exportAcceptNew, $scope.selected.exportReadyOnly).then(function() {
            // do nothing
          })
        };

        $scope.clearImportResults = function() {
          $scope.importComponentsFileResults = null;
          $scope.validateComponentsFileResults = null;
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
          if (table === 'component') {
            findComponents();
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

            projectService.getProjectsForUser(  $scope.user).then(function(projectData) {
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

          // get the semantic types for this terminology
          metadataService.getSemanticTypes(terminology.preferredName, terminology.version).then(
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
          $scope.findComponents();
        };

        //
        // Initialize - DO NOT PUT ANYTHING AFTER THIS SECTION OTHER THAN CONFIG CHECK
        //
        $scope.initialize = function() {
          securityService.saveTab($scope.user.userPreferences, '/content');
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
