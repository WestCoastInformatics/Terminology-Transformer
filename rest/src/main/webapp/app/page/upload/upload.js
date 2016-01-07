// Route
ttApp.config(function config($routeProvider) {
  $routeProvider.when('/upload', {
    controller : 'SourceDataUploadCtrl',
    templateUrl : 'app/page/upload/upload.html'
  });
})

// Controller
ttApp.controller('SourceDataUploadCtrl', function($scope, $filter, $timeout, sourceDataService,
  gpService, utilService, FileUploader, NgTableParams) {
  console.debug('configure SourceDataUploadCtrl');

  var uploadedFiles = [];

  /**
   * Function to retrieve the list of currently uploaded files
   */
  function getUploadedFileDetails() {
    sourceDataService.getSourceDataFiles().then(function(response) {
      // TODO Update this once file details format worked out
      uploadedFiles = response.sourceDataFiles;
      console.debug(uploadedFiles);
      $scope.tpUploaded = new NgTableParams({}, {
        dataset : uploadedFiles
      });

      console.debug('after reload', $scope.tpUploaded);
    });
  }

  /**
   * Function to download a file from the server
   */
  $scope.downloadFile = function() {
    // TODO
    window.alert('Not yet functional');
  }

  /**
   * Function to download all currently displayed files as a zip file
   */
  $scope.downloadAllFiles = function() {
    // TODO
    window.alert('Not yet functional');
  }

  /**
   * Function to delete file from server
   */
  $scope.deleteFile = function(file) {
    console.debug('delete file', file);
    sourceDataService.deleteFile(file.id).then(function(response) {
      getUploadedFileDetails();
    }, function(error) {
      // do nothing
    });
  }

  /**
   * Function to delete all currently displayed files from the server
   */
  $scope.deleteAllFiles = function() {

    if (!window.confirm('Are you sure you want to delete all uploaded files?')) {
      return;
    }

    // declare timeout object used to prevent retrieval calls more than once per half-second
    // used to prevent enormous number of getUploadedFileDetails for large lists
    // while still allowing for visual update of deleted items
    var refreshTimeout;
    angular.forEach(uploadedFiles, function(file) {
      gpService.increment();
      sourceDataService.deleteFile(file.id).then(function() {
        gpService.decrement();

        // cancel existing timeout
        $timeout.cancel(refreshTimeout);

        // set the new timeout
        refreshTimeout = $timeout(function() {
          getUploadedFileDetails();
        }, 500);
      }, function(error) {
        utilService.handleError(error);
        gpService.decrement();
      })
    });
  };

  // on load, get the uploaded file details
  getUploadedFileDetails();

  /////////////////////////
  // Table Parameters
  /////////////////////////

  console.debug($scope.tpUploaded);

  /*
     * // declare table parameters $scope.tpUploaded = new ngTableParams({ page : 1, count : 10,
     * sorting : { 'name' : 'asc' } }, { filterDelay : 50, total : $scope.tasks ?
     * $scope.tasks.length : 0, // length of data getData : function($defer, params) {
     * 
     * if (!$scope.uploadedFiles || $scope.uploadedFiles.length == 0) { $defer.resolve([]); } else {
     * 
     * var data = params.sorting() ? $filter('orderBy')($scope.uploadedFiles, params.orderBy()) :
     * mydata;
     * 
     * $defer.resolve(data.slice((params.page() - 1) * params.count(), params.page()
     * params.count())); } } });
     */

  function isZipFile(item) {
    return item.file.name.match(/.*\.zip/g) !== null;
  }

  // flag for whether zipped files are present, sent in uploader event listeners
  // NOTE: Only updated on add events, as angular-file-upload does not have a _remove event
  $scope.hasZippedFiles = false;

  ////////////////////////////////////////////////
  // Specify the angular-file-uploader
  // TODO:  Move this into sourceData service
  ////////////////////////////////////////////////

  var uploader = $scope.uploader = new FileUploader({
    url : fileUrl + 'sourceDataFile/add'
  });

  // FILTERS

  uploader.filters.push({
    name : 'customFilter',
    fn : function(item /* {File|FileLikeObject} */, options) {
      return this.queue.length < 10;
    }
  });

  // CALLBACKS
  uploader.onWhenAddingFileFailed = function(item /* {File|FileLikeObject} */, filter, options) {
    console.info('onWhenAddingFileFailed', item, filter, options);
  };
  uploader.onAfterAddingFile = function(fileItem) {
    console.info('onAfterAddingFile', fileItem);
    fileItem.isZipped = isZipFile(fileItem);
    if (fileItem.isZipped) {
      $scope.hasZippedFiles = true;
    }
  };
  uploader.onAfterAddingAll = function(addedFileItems) {
    console.info('onAfterAddingAll', addedFileItems);
    angular.forEach(addedFileItems, function(fileItem) {
      fileItem.isZipped = isZipFile(fileItem);
      if (fileItem.isZipped) {
        $scope.hasZippedFiles = true;
      }
    });

    //checkForZippedFiles();
  };
  uploader.onBeforeUploadItem = function(item) {

    // dynamically set the upload url with the unzip flag
    item.url = fileUrl + 'sourceDataFile/add?unzip=' + (item.unzip ? 'true' : 'false')

    // manually set the headers on the item's request (does not inherit from $http, apparently)
    // TODO Wire this to security service, current cookie
    item.headers = {
      'Authorization' : 'guest'
    };
  };
  uploader.onProgressItem = function(fileItem, progress) {
    console.info('onProgressItem', fileItem, progress);
  };
  uploader.onProgressAll = function(progress) {
    console.info('onProgressAll', progress);
  };
  uploader.onSuccessItem = function(fileItem, response, status, headers) {
    console.info('onSuccessItem', fileItem, response, status, headers);
  };
  uploader.onErrorItem = function(fileItem, response, status, headers) {
    console.info('onErrorItem', fileItem, response, status, headers);
    utilService.handleError({
      data : response ? response : "Folders cannot be uploaded; only single files or zip files containing no folders may be uploaded.",
      status : status,
      headers : headers
    }); // shoehorn into ttApp expected error format
  };
  uploader.onCancelItem = function(fileItem, response, status, headers) {
    console.info('onCancelItem', fileItem, response, status, headers);
  };
  uploader.onCompleteItem = function(fileItem, response, status, headers) {
    console.info('onCompleteItem', fileItem, response, status, headers);
    getUploadedFileDetails();
  };
  uploader.onCompleteAll = function() {
    console.info('onCompleteAll');
  };

  console.info('uploader', uploader);

});