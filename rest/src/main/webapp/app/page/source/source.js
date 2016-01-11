// Route
ttApp.config(function config($routeProvider) {
  $routeProvider.when('/source', {
    controller : 'SourceDataCtrl',
    templateUrl : 'app/page/source/source.html'
  });
})

// Controller
ttApp.controller('SourceDataCtrl', function($scope, $http, $q, $timeout, NgTableParams,
  sourceDataService, utilService, securityService, gpService) {
  console.debug('configure SourceDataCtrl');

  ///////////////////////
  // Local variables
  ///////////////////////

  var sourceDatas = [];
  var sourceDataFiles = [];
  var currentSourceData = null;

  ////////////////////////
  // Scope variables
  ////////////////////////
  sourceDataService.getConverterNames().then(function(names) {
    console.debug('converter names', names);
    $scope.converterNames = names;
  });

  ///////////////////////////////
  // ngTable refresh functions
  ///////////////////////////////

  // Instantiates new table params from sourceDatas array
  function refreshSourceDataTable() {
    console.debug('Refreshing table with values', sourceDatas);
    $scope.tpSourceDatas = new NgTableParams({}, {
      dataset : sourceDatas
    })
  }
  ;

  // Instantiates new table params from source data files and current source data viewed
  function refreshFileTables() {

    // extract current file ids for convenience
    var currentFileIds = $scope.currentSourceData.sourceDataFiles.map(function(item) {
      return item.id;
    })

    // available:  any items not in current file ids (not attached to viewed sourceData)
    $scope.tpAvailable = new NgTableParams({}, {
      dataset : sourceDataFiles.filter(function(item) {
        return currentFileIds.indexOf(item.id) === -1;
      })
    });

    // attached:  any items in current file ids (attached to viewed sourceData)
    $scope.tpAttached = new NgTableParams({}, {
      dataset : sourceDataFiles.filter(function(item) {
        return currentFileIds.indexOf(item.id) !== -1;
      })
    });
  }

  /**
   * Refreshes source data list from server and instantiates new table params
   */
  function refreshSourceDatas() { // on load, get all source datas
    var deferred = $q.defer();
    gpService.increment();
    sourceDataService.getSourceDatas().then(function(response) {
      console.debug('source datas', response);
      gpService.decrement();
      sourceDatas = response.sourceDatas;
      refreshSourceDataTable();
      deferred.resolve(response);
    }, function(error) {
      gpService.decrement();
      utilService.handleError(error);
      deferred.reject(response);
    });
    return deferred.promise;
  }

  // on load, get source datas
  refreshSourceDatas();

  // view the source data and retrieve current source data file list
  $scope.viewSourceData = function(sourceData) {
    console.debug('Viewing sourceData', sourceData);
    $scope.currentSourceData = sourceData;

    // hackish way to set the isModified flag
    $timeout(function() {
      $scope.isSourceDataModified = false;
    }, 250)

    gpService.increment();
    sourceDataService.getSourceDataFiles().then(function(response) {
      gpService.decrement();
      sourceDataFiles = response.sourceDataFiles;
      refreshFileTables();
    }, function(error) {
      gpService.decrement();
      utilService.handleError(error);
    });
  };

  // watch for hanges to current source data to enable save/cancel buttons
  $scope.$watch('currentSourceData', function() {
    $scope.isSourceDataModified = true;
  }, true);

  /*
   * Create new source data JSON object
   */
  $scope.createSourceData = function() {

    console.debug('Creating new source data');

    var sourceData = {
      name : null,
      description : null,
      lastModifiedBy : securityService.getUser().userName,
      sourceDataFiles : [],
      converterName : null,
    }

    sourceDatas.splice(0, 0, sourceData);
    refreshSourceDataTable();

  }

  $scope.saveSourceData = function(sourceData) {
    if (!sourceData.name) {
      window.alert('You must specify the source data name.');
      return;
    }
    gpService.increment();
    sourceData.lastModifiedBy = securityService.getUser().userName;
    sourceDataService.saveSourceData(sourceData).then(function(response) {
      sourceData = response;
      gpService.decrement();
    }, function(error) {
      utilService.handleError(error);
      gpService.decrement();
    });
  };

  $scope.cancelSourceDataModifications = function() {
    if (!$scope.isSourceDataModified || window.confirm('Discard changes?')) {
      $scope.currentSourceData = null;
    }
  }

  $scope.deleteSourceData = function(sourceData) {
    gpService.increment()
    sourceDataService.deleteSourceData(sourceData).then(function(response) {
      refreshSourceDatas.then(function(response) {
        gpService.decrement();
      }, function(error) {
        gpService.decrement();
      })
    })
  };

  $scope.addSourceDataFileToSourceData = function(sourceData, file) {
    if (!sourceData.sourceDataFiles || !Array.isArray(sourceDataFiles)) {
      sourceData.sourceDataFiles = [ file ];
    } else {
      sourceData.sourceDataFiles.push(file);
    }
    refreshFileTables();
  };

  $scope.removeSourceDataFileFromSourceData = function(sourceData, file) {

    $scope.currentSourceData.sourceDataFiles = $scope.currentSourceData.sourceDataFiles
      .filter(function(item) {
        return item.id !== file.id;
      });
    refreshFileTables();
  };

});