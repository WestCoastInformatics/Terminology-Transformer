// NDC controller
tsApp.controller('NdcCtrl', [
  '$scope',
  '$location',
  'ndcService',
  'utilService',
  'appConfig',
  function($scope, $location, ndcService, utilService,
    appConfig) {
    console.debug('configure NdcCtrl');


    // Set up scope
    $scope.inputString = "";
    $scope.actionOptions = [ 'Ndc Info', 'Rxcui Info', 'Ndc Properties' ];
    $scope.selectedAction = $scope.actionOptions[0];
    $scope.results = null;

    $scope.paging = {};
    $scope.paging['history'] = {
      page : 1,
      filter : '',
      typeFilter : '',
      sortField : 'start',
      ascending : null,
      pageSize : 10
    };
    $scope.paging['splsetid'] = {
      page : 1,
      filter : '',
      typeFilter : '',
      sortField : 'ndc11',
      ascending : null,
      pageSize : 1
    };

    // Define functions

    $scope.submit = function() {
      if ($scope.selectedAction == 'Ndc Info') {
        $scope.getNdcInfo($scope.inputString);
      } else if ($scope.selectedAction == 'Rxcui Info') {
        $scope.getRxcuiInfo($scope.inputString);
      } else if ($scope.selectedAction == 'Ndc Properties'
        && $scope.inputString.length > 15) {
        $scope.getNdcPropertiesForSplSetId($scope.inputString);
      } else if ($scope.selectedAction == 'Ndc Properties'
        && $scope.inputString.length <= 15) {
        $scope.getNdcProperties($scope.inputString);
      }
    }

    $scope.getNdcProperties = function(ndcCode) {
      ndcService.getNdcProperties($scope.inputString).then(function(data) {
        $scope.results = data;
        $scope.pagedSplsetid = null;
        $scope.getPagedHistory();          
      });
    }
    
    $scope.getNdcPropertiesForSplSetId = function(splsetid) {
      ndcService.getNdcPropertiesForSplSetId($scope.inputString).then(
        function(data) {
          $scope.results = data;
          $scope.pagedHistory = null;
          $scope.getPagedSplsetid();
        });
    }
    
    $scope.getNdcInfo = function(ndcCode) {
      $scope.inputString = ndcCode;
      $scope.selectedAction = 'Ndc Info';
      ndcService.getNdcInfo($scope.inputString).then(function(data) {
        $scope.results = data;
        $scope.pagedSplsetid = null;
        $scope.getPagedHistory();
      });
    }
    
    $scope.getRxcuiInfo = function(rxcuiCode) {
      $scope.inputString = rxcuiCode;
      $scope.selectedAction = 'Rxcui Info';
      ndcService.getRxcuiInfo($scope.inputString).then(function(data) {
        $scope.results = data;
        $scope.pagedSplsetid = null;
        $scope.getPagedHistory();
      });
    }
    
    // Autocomplete function
    $scope.autocomplete = function(searchTerms) {
      // if invalid search terms, return empty array
      if (searchTerms == null || searchTerms == undefined || searchTerms.length < 3) {
        return new Array();
      }
      return ndcService.autocomplete(searchTerms);
    };
    
    // Get paged history (assume all are loaded)
    $scope.getPagedHistory = function() {
      $scope.pagedHistory = utilService.getPagedArray($scope.results.history,
        $scope.paging['history']);
    };
    
    // Get paged splsetid search property results (assume all are loaded)    
    $scope.getPagedSplsetid = function() {
      $scope.pagedSplsetid = utilService.getPagedArray($scope.results.list,
        $scope.paging['splsetid']);
    };
    


    // Return up or down sort chars if sorted
    $scope.getSortIndicator = function(table, field) {
      return utilService.getSortIndicator(table, field, $scope.paging);
    };
    
    // sort mechanism
    $scope.setSortField = function(table, field) {
      utilService.setSortField(table, field, $scope.paging);

      // retrieve the correct table
      if (table === 'history') {
        $scope.getRxcuiInfo($scope.inputString);
      } else if (table === 'splsetid') {
        $scope.getNdcPropertiesForSplSetId($scope.inputString);
      } 
    };

    
    // Initialize

  } ]);