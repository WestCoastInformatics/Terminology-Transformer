// Administration controller
tsApp.controller('TermCtrl', [
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
  function($scope, $http, $q, $location, $uibModal, gpService, utilService, tabService,
    configureService, securityService, metadataService, projectService, contentService,
    termService, transformService) {
    console.debug('configure TermsCtrl');

    // Set up tabs and controller
    tabService.setShowing(true);
    utilService.clearError();
    tabService.setSelectedTabByLabel('Raw Terms');
    $scope.user = securityService.getUser();
    projectService.getUserHasAnyRole();

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
    }

    function prepTermPfs(type) {
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

    // on concept change, re-set the edited concept and perform search
    function processConceptChange() {
      if ($scope.selected.component) {
        setConceptEdited($scope.selected.component);
      }
      findConcepts();
    }

    $scope.setTermViewed = function(term) {
      $scope.selected.editTab = 'Edit';
      $scope.selected.termViewed = term;
      var dataContext = transformService
        .getDataContextList($scope.selected.metadata.terminology.terminology);
      transformService.process(term.key, dataContext).then(function(response) {
        console.debug('process response', response);
        if (!response || !Array.isArray(response.scoredDataContextTuples) || response.scoredDataContextTuples.length == 1) {
        $scope.selected.term = response.scoredDataContextTuples[0];
        } else {
          utilService.setError('Process response invalid');
        }
      });
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

      projectService.getProjectsForUser($scope.user).then(
      // Success
      function(projectData) {
        $scope.lists.projects = projectData.projects;
        $scope.selected.project = projectData.project;
        if ($scope.selected.project) {
          securityService.saveProjectId($scope.user.userPreferences, $scope.selected.project.id);
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

      $scope.selected.termEdited = null;
      $scope.lists.terms = null;
      $scope.lists.concepts = null;

      $scope.selected.project = null;
      for (var i = 0; i < $scope.lists.projects.length; i++) {
        if ($scope.lists.projects[i].terminology == terminology.terminology) {
          $scope.selected.project = $scope.lists.projects[i];
          console.debug('  project found: ', $scope.selected.project);

          $scope.findTerms();
          
          // get features (semantic types)
          metadataService.getSemanticTypes($scope.selected.project.terminology,
            $scope.selected.project.version).then(function(response) {
            console.debug('semantic types', response);

            $scope.lists.features = response.types;
          });
        }
      }
     
      
      if ($scope.selected.project == null) {
        utilService.setError('Unexpected error: no project for terminology');
      }
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
