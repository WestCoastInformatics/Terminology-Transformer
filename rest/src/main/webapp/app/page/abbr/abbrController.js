// Administration controller
tsApp.controller('AbbrCtrl', [
  '$scope',
  '$http',
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
  function($scope, $http, $location, $uibModal, gpService, utilService, tabService,
    configureService, securityService, metadataService, projectService, abbrService, ndcService) {
    console.debug('configure AbbrCtrl');

    // Set up tabs and controller
    tabService.setShowing(true);
    utilService.clearError();
    tabService.setSelectedTabByLabel('Abbreviations');
    $scope.user = securityService.getUser();
    projectService.getUserHasAnyRole();


    // Scope variables
    $scope.selected = {
      project : null,
      terminology : null,
      metadata : null,
      abbr : null
    }
    $scope.lists = {
      projects : [],
      terminologies : [],
      abbrs : [],
      abbrTypes : [ 'labAbbr', 'medAbbr', 'conditionAbbr', 'procedureAbbr' ]
    }
    $scope.paging = {};
    $scope.paging['abbr'] = utilService.getPaging();
    $scope.paging['abbr'].sortField = 'key';
    $scope.paging['abbr'].callbacks = {
      getPagedList : findAbbreviations
    };

    $scope.findAbbreviations = function(abbr) {
      findAbbreviations(abbr);
    }
    function findAbbreviations(abbr) {
      if (!$scope.selected.abbrType) {
        return;
      }
      if (abbr) {
        $scope.selected.abbr = abbr;
      }
      var terms = $scope.paging['abbr'].filter ? $scope.paging['abbr'].filter.split(' ') : [];
      
      var localQuery = '';
      angular.forEach(terms, function(term) {
        var append = term.endsWith('"') ? '' : '*';
        localQuery += 'key:' + term + append + ' OR value:' + term + + append + ' OR ';
      });
      if (localQuery.length > 0) {
        localQuery = localQuery.substring(0, localQuery.length -3);
      }
      console.debug('localQuery', localQuery);
      abbrService.findAbbreviations(localQuery, getPfs('abbr')).then(function(response) {
        console.debug('abbreviations', response);
        $scope.selected.abbrs = response;
      })
    }

    function getPfs(type) {
      var paging = $scope.paging[type];
      console.debug( paging);
      var pfs = {
        startIndex : (paging.page - 1) * paging.pageSize,
        maxResults : paging.pageSize,
        sortField : paging.sortField,
        ascending : paging.sortAscending,
        queryRestriction : 'type:' + $scope.selected.abbrType

      };
      return pfs;
    }

    $scope.setAbbreviation = function(abbr) {
      $scope.selected.abbr = abbr;
    }
    
    $scope.createAbbreviation = function() {
      var abbr = {
        type: $scope.selected.abbrType,
        key: null,
        value: null
      }
      $scope.setAbbreviation(abbr);
    }
    
    $scope.cancelAbbreviation = function() {
      $scope.selected.abbr = null;
    }

    $scope.addAbbreviation = function(abbr) {
      abbrService.addAbbreviation(abbr).then(function(newAbbr) {
        $scope.selected.abbr = newAbbr;
      });
    }

    $scope.updateAbbreviation = function(abbr) {
      abbrService.updateAbbreviation(abbr).then(function() {
        // do nothing
      });
    }
    
    $scope.removeAbbreviation = function(abbr) {
      abbrService.removeAbbreviation(abbr.id).then(function() {
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
      abbrService.validateAbbreviationsFile($scope.selected.abbrType, $scope.selected.file).then(function(response) {
        $scope.validateAbbreviationsFileResults = response;
      })
    }
    
    $scope.importAbbreviationsFile = function() {
      abbrService.importAbbreviationsFile($scope.selected.abbrType, $scope.selected.file).then(function(response) {
        $scope.importAbbreviationsFileResults = response;
        $scope.findAbbreviations();
      })
    }
    
    $scope.exportAbbreviations = function() {
      abbrService.exportAbbreviations($scope.selected.abbrType).then(function() {
        // do nothing
      })
    }
    
    $scope.changeImportFile = function() {
      $scope.importAbbreviationsFileResults = null;
      $scope.validateAbbreviationsFileResults = null;
    }
    
    $scope.cancelImport = function() {
      $scope.selected.file = null;
      $scope.changeImportFile();
    }
    
    //
    // Initialize - DO NOT PUT ANYTHING AFTER THIS SECTION OTHER THAN CONFIG CHECK
    //
    $scope.initialize = function() {
      securityService.saveTab($scope.user.userPreferences, '/abbr');
      $scope.findAbbreviations();
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
