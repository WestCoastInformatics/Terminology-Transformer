// Administration controller
tsApp.controller('AbbrCtrl', [
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
      abbr : null
    }

    $scope.lists = {
      projects : [],
      terminologies : [],
      abbrs : [],
      fileTypesFilter : '.txt,.csv',
      workflowStatus : [ {
        key : null,
        value : 'Any'
      }, {
        key : 'PUBLISHED',
        value : 'Accepted'
      },{
        key : 'NEW',
        value : 'New'
      }, {
        key : 'NEEDS_REVIEW',
        value : 'Needs Review'
      } ]
    };
    
   
    $scope.paging = {};
    $scope.paging['abbr'] = utilService.getPaging();
    $scope.paging['abbr'].sortField = 'key';
    $scope.paging['abbr'].workflowStatus = null;
    $scope.paging['abbr'].callbacks = {
      getPagedList : findAbbreviations
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
        $scope.selected.abbr = abbr;
      }
      var term = $scope.paging['abbr'].filter;
      var query = term ? 'key:' + term + ' OR value:' + term : null;
      console.debug('query', query);
      abbrService.findAbbreviations(query, getPfs('abbr')).then(function(response) {
        console.debug('abbreviations', response);
        $scope.selected.abbrs = response;

        console.debug('$scope.selected', $scope.selected);
      })
    }

    function getPfs(type) {
      var paging = $scope.paging[type];
      console.debug(paging);
      var pfs = {
        startIndex : (paging.page - 1) * paging.pageSize,
        maxResults : paging.pageSize,
        sortField : paging.sortField,
        ascending : paging.sortAscending,
        queryRestriction : null
      };
      if (type == 'abbr') {
         var clauses = [];
        clauses.push('type:\"' + $scope.selected.metadata.terminology.preferredName + '-ABBR\"');
        if ($scope.paging['abbr'].workflowStatus) {
          clauses.push('workflowStatus:' + $scope.paging['abbr'].workflowStatus);
        }
        pfs.queryRestriction = '';
        for (var i = 0; i < clauses.length; i++) {
          pfs.queryRestriction += clauses[i] + (i < clauses.length - 1 ? ' AND ' : '');
        }

      }
      return pfs;
    }

    $scope.setAbbreviation = function(abbr) {
      $scope.selected.abbr = abbr;
    }

    $scope.createAbbreviation = function() {
      var abbr = {
        type : $scope.selected.metadata.terminology.preferredName + '-ABBR',
        key : null,
        value : null
      }
      $scope.setAbbreviation(abbr);
    }
    
    $scope.cancelAbbreviation = function() {
      $scope.selected.abbr = null;
    }
    
    $scope.addAbbreviation = function(abbr) {
      abbrService.addAbbreviation(abbr).then(function(newAbbr) {
        findAbbreviations();
        $scope.selected.abbr = null;
      });
    }

   
    $scope.updateAbbreviation = function(abbr) {
      abbrService.updateAbbreviation(abbr).then(function() {
        findAbbreviations();
        $scope.selected.abbr = null;
      });
    }

    $scope.removeAbbreviation = function(abbr) {
      abbrService.removeAbbreviation(abbr.id).then(function() {
        $scope.selected.abbr = null;
        findAbbreviations();
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
        $scope.selected.metadata.terminology.preferredName + '-ABBR', $scope.selected.file).then(
        function(response) {
          $scope.validateAbbreviationsFileResults = response;
        })
    }

    $scope.importAbbreviationsFile = function() {
      abbrService.importAbbreviationsFile(
        $scope.selected.metadata.terminology.preferredName + '-ABBR', $scope.selected.file).then(
        function(response) {
          $scope.importAbbreviationsFileResults = response;
          $scope.findAbbreviations();
        })
    };
    
    $scope.exportAbbreviations = function() {
      abbrService.exportAbbreviations($scope.selected.metadata.terminology.preferredName + '-ABBR').then(
        function() {
          // do nothing
        })
    };

    $scope.clearImportResults = function() {
      $scope.importAbbreviationsFileResults = null;
      $scope.validateAbbreviationsFileResults = null;
    }

    $scope.cancelImport = function() {
      $scope.selected.file = null;
      $scope.changeImportFile();
    }

    // Wait for "terminologies" to load
    $scope.initMetadata = function() {

      metadataService.getTerminologies().then(
      // Success
      function(data) {
        console.debug('terminologies', data.terminologies);

        $scope.lists.terminologies = data.terminologies;
        // Load all terminologies upon controller load (unless already
        // loaded)
        if ($scope.lists.terminologies) {

          console.debug('  user pref', $scope.userPreferences);

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
