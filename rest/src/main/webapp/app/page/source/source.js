// Route
ttApp.config(function config($routeProvider) {
  $routeProvider.when('/source', {
    controller : 'SourceDataCtrl',
    templateUrl : 'app/page/source/source.html'
  });
})

// Controller
ttApp.controller('SourceDataCtrl', function($scope, $filter, fileService, FileUploader,
  NgTableParams) {
  console.debug('configure SourceDataCtrl');

});