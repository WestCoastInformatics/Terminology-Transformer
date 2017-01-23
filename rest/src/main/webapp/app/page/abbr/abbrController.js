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
      fileTypesFilter : '.txt,.csv',
      workflowStatus : [ {
        key : null,
        label : 'Any'
      }, {
        key : 'PUBLISHED',
        label : 'Accepted'
      }, {
        key : 'NEW',
        label : 'New'
      }, {
        key : 'NEEDS_REVIEW',
        label : 'Needs Review'
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
        $scope.setAbbreviationReviewed
      }
      var term = $scope.paging['abbr'].filter;
      var query = term ? 'key:' + term + ' OR value:' + term : null;
      console.debug('query', query);
      abbrService.findAbbreviations(query, getPfs('abbr')).then(function(response) {

        $scope.lists.abbrsViewed = response;
        console.debug('abbreviations', $scope.lists.abbrsViewed);
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

    $scope.setAbbreviationViewed = function(abbr) {
      $scope.selected.editTab = 'Edit';
      $scope.selected.abbrViewed = abbr;
      $scope.selected.abbrEdited = angular.copy(abbr);
      $scope.getReviewForAbbreviation($scope.selected.abbrEdited);

    }

    $scope.setAbbreviationEdited = function(abbr) {
      $scope.selected.abbrEdited = abbr;
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
      $scope.selected.abbrEdited = null;
    }

    $scope.addAbbreviation = function(abbr) {
      abbrService.addAbbreviation(abbr).then(function(newAbbr) {
        findAbbreviations();
        $scope.setAbbreviationViewed(newAbbr);
         
        // get review for already-selected abbreviation
        $scope.getReviewForAbbreviation($scope.selected.abbrReviewed);
        $scope.selected.abbrEdited = null;
        
        // re-find abbreviations
        findAbbreviations();

      });
    }

    $scope.updateAbbreviation = function(abbr) {
      abbrService.updateAbbreviation(abbr).then(function() {
        $scope.selected.abbrEdited = null;
      
        // get review for already-selected abbreviation
        $scope.getReviewForAbbreviation($scope.selected.abbrReviewed);
       
        // re-find abbreviations
        findAbbreviations();
      });
    }

    $scope.removeAbbreviation = function(abbr) {
      abbrService.removeAbbreviation(abbr.id).then(function() {
        $scope.selected.abbrEdited = null;
        // get review for already-selected abbreviation
        $scope.getReviewForAbbreviation($scope.selected.abbrReviewed);
        
        // re-find abbreviations
        findAbbreviations();
      });
    }

    //
    // Review functions
    //

    $scope.setReviewMode = function() {
      $scope.paging['abbr'].workflowStatus = 'NEEDS_REVIEW';
      $scope.findObservations();
    }

    $scope.getReviewForAbbreviation = function(abbr) {
      if (!abbr) {
        return;
      }
      $scope.selected.abbrReviewed = abbr;

      abbrService.getReviewForAbbreviationId($scope.selected.abbrReviewed.id).then(
        function(abbrReviews) {
          $scope.lists.abbrsReviewed = abbrReviews;

          // on review load, find and select for editing the viewed abbreviation in the review list
          angular.forEach($scope.lists.abbrReviews, function(abbrReview) {
            if (abbrReview.id == $scope.selected.abbrViewed.id) {
              $scope.setAbbreviationEdited(abbrReview);
            }
          })
        });
    }

    $scope.computeReviewStatuses = function() {
      abbrService.computeReviewStatuses(
        $scope.selected.metadata.terminology.preferredName + '-ABBR').then(function() {
        $scope.findAbbreviations();
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
      abbrService.exportAbbreviations($scope.selected.metadata.terminology.preferredName + '-ABBR', $scope.selected.exportReadyOnly)
        .then(function() {
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

    $scope.setNewMode = function() {
      $scope.paging['abbr'].workflowStatus = 'NEW';
      $scope.findAbbreviations();
    }

    // 
    // Initialization
    //

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
