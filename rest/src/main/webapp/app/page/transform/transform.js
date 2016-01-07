// Route
ttApp.config(function config($routeProvider) {
  $routeProvider.when('/transform', {
    controller : 'TransformCtrl',
    templateUrl : 'app/page/transform/transform.html'
  });
})

// Controller
ttApp.controller('TransformCtrl',
  function($scope, $filter, sourceDataService, FileUploader, NgTableParams) {
    console.debug('configure TransformCtrl');
    
    var uploadedFiles = [];

    /**
     * Function to retrieve the list of currently uploaded files
     */
    function getUploadedFileDetails() {
      sourceDataService.getUploadedFileDetails().then(function(response) {
        // TODO Update this once file details format worked out
        uploadedFiles = response.strings.map(function(item) {
          console.debug(item);
          return { 'name' : item, 'size' : '???', 'date' : '???'};
        });
        console.debug(uploadedFiles);
        $scope.tpUploaded = new NgTableParams({}, {dataset : uploadedFiles});
        
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
      sourceDataService.deleteFile(file.name).then(function(response) {
        getUploadedFileDetails();
      }, function(error) {
        // do nothing
      });
    }

    /**
     * Function to delete all currently displayed files from the server
     */
    $scope.deleteAllFiles = function() {
      window.alert('Not yet functional');
    }

    // on load, get the uploaded file details
    getUploadedFileDetails();

    /////////////////////////
    // Table Parameters
    /////////////////////////
    
    
    console.debug($scope.tpUploaded);
    
    /*
    // declare table parameters
    $scope.tpUploaded = new ngTableParams({
      page : 1,
      count : 10,
      sorting : {
        'name' : 'asc'
      }
    }, {
      filterDelay : 50,
      total : $scope.tasks ? $scope.tasks.length : 0, // length of data
      getData : function($defer, params) {

        if (!$scope.uploadedFiles || $scope.uploadedFiles.length == 0) {
          $defer.resolve([]);
        } else {

          var data = params.sorting() ? $filter('orderBy')($scope.uploadedFiles, params.orderBy()) : mydata;

          $defer.resolve(data.slice((params.page() - 1) * params.count(), params.page()
            * params.count()));
        }
      }
    });*/
    
    
    /////////////////////////
    // Specify the angular-file-uploader
    /////////////////////////

    var uploader = $scope.uploader = new FileUploader({
      url : fileUrl + '/upload'
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
    };
    uploader.onAfterAddingAll = function(addedFileItems) {
      console.info('onAfterAddingAll', addedFileItems);
    };
    uploader.onBeforeUploadItem = function(item) {
      console.info('onBeforeUploadItem', item);
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