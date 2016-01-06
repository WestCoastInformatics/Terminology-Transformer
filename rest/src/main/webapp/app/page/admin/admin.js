// Route
ttApp.config(function config($routeProvider) {
  $routeProvider.when('/admin', {
    controller : 'AdminCtrl',
    templateUrl : 'app/page/admin/admin.html'
  });
})

ttApp.controller('AdminCtrl',
  function($scope, $filter, fileService, FileUploader, NgTableParams) {
    console.debug('configure AdminCtrl');
    
  });