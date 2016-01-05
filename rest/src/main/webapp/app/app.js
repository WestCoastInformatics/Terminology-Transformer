'use strict'

var ttApp = angular
  .module('ttApp', [ 'ngRoute', 'ui.bootstrap', 'ngCookies', 'ngTable', 'angularFileUpload' ]).config(
    function($rootScopeProvider) {

      // Set recursive digest limit higher to handle very deep trees.
      $rootScopeProvider.digestTtl(17);
    });

// Declare top level URL vars
var securityUrl = 'security/';
var fileUrl = 'file/';

// Initialization of ttApp
ttApp.run([ '$rootScope', '$http', '$window', 'securityService',
            function($rootScope, $http, $window, securityService) {

  // TODO Re-enable formal login
  $http({
    url : securityUrl + 'authenticate/guest',
    method : 'POST',
    data : 'guest',
    headers : {
      'Content-Type' : 'text/plain'
    }
  }).then(function(response) {
    securityService.setUser(response.data);
    // set request header authorization and reroute
    console.debug('authToken = ' + response.data.authToken);
    $http.defaults.headers.common.Authorization = response.data.authToken;
  });

} ]);

// Route provider configuration
ttApp.config([ '$routeProvider', '$logProvider', function($routeProvider, $logProvider) {
  console.debug('configure $routeProvider');
  $logProvider.debugEnabled(true);

  // Set reloadOnSearch so that $location.hash() calls do not reload
  // the
  // controller
  $routeProvider

  .when('/', {
    templateUrl : 'app/page/upload/upload.html',
    controller : 'SourceDataUploadCtrl',
    reloadOnSearch : false
  })

  .when('/upload', {
    templateUrl : 'app/page/upload/upload.html',
    controller : 'SourceDataUploadCtrl',
    reloadOnSearch : false
  })

  // $locationProvider.html5Mode(true);

} ]);

// Header controller
ttApp.controller('HeaderCtrl', [ '$scope', '$location', '$http', 'securityService',
  function($scope, $location, $http, securityService) {
    console.debug('configure HeaderCtrl');

    // Declare user
    $scope.user = securityService.getUser();

    // Logout method
    $scope.logout = function() {
      securityService.logout();
    }

    // Open help page dynamically
    $scope.goToHelp = function() {
      var path = $location.path();
      path = '/help' + path + '?authToken=' + $http.defaults.headers.common.Authorization;
      var currentUrl = window.location.href;
      var baseUrl = currentUrl.substring(0, currentUrl.indexOf('#') + 1);
      var newUrl = baseUrl + path;
      var myWindow = window.open(newUrl, 'helpWindow');
      myWindow.focus();
    };

    // for ng-show
    $scope.isShowing = function() {
      return securityService.isLoggedIn();
    }

  } ]);

// Footer controller
ttApp.controller('FooterCtrl', [ '$scope', 'gpService', 'securityService',
  function($scope, gpService, securityService) {
    console.debug('configure FooterCtrl');
    // Declare user
    $scope.user = securityService.getUser();

    // Logout method
    $scope.logout = securityService.logout;

    // for ng-show
    $scope.isShowing = function() {
      return securityService.isLoggedIn();
    }

  } ]);