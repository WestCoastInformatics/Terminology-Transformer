// NDC controller
tsApp.controller('NdcCtrl', [
  '$scope',
  '$location',
  'securityService',
  'utilService',
  'ndcService',
  'appConfig',
  function($scope, $location, securityService, utilService, ndcService,
    appConfig) {
    console.debug('configure NdcCtrl');

    //utilService.clearError();

    // Set up scope
    $scope.inputString = "";
    $scope.actionOptions = [ 'Ndc Info', 'Rxcui Info', 'Ndc Properties' ];
    $scope.selectedAction = $scope.actionOptions[0];
    $scope.results = null;

    // Define functions

    $scope.submit = function() {
      if ($scope.selectedAction == 'Ndc Info') {
        ndcService.getNdcInfo($scope.inputString).then(function(data) {
          $scope.results = data;
        });
      } else if ($scope.selectedAction == 'Rxcui Info') {
        ndcService.getRxcuiInfo($scope.inputString).then(function(data) {
          $scope.results = data;
        });
      } else if ($scope.selectedAction == 'Ndc Properties'
        && $scope.inputString.length > 15) {
        ndcService.getNdcPropertiesForSplSetId($scope.inputString).then(
          function(data) {
            $scope.results = data;
          });
      } else if ($scope.selectedAction == 'Ndc Properties'
        && $scope.inputString.length <= 15) {
        ndcService.getNdcProperties($scope.inputString).then(function(data) {
          $scope.results = data;
        });
      }
    }

    // Autocomplete function
    $scope.autocomplete = function(searchTerms) {
      // if invalid search terms, return empty array
      if (searchTerms == null || searchTerms == undefined || searchTerms.length < 3) {
        return new Array();
      }
      return ndcService.autocomplete(searchTerms);
    };
    
    // Initialize

  } ]);