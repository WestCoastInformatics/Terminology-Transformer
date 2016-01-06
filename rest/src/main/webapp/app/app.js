'use strict'

var ttApp = angular.module('ttApp',
  [ 'ngRoute', 'ui.bootstrap', 'ngCookies', 'ngTable', 'angularFileUpload' ]).config(
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
    templateUrl : 'app/page/login/login.html',
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

// Tab controller
ttApp.controller('TabCtrl', [ '$scope', '$interval', '$timeout', 'securityService', 'tabService',
  function($scope, $interval, $timeout, securityService, tabService) {
    console.debug('configure TabCtrl');

    // Setup tabs
    $scope.tabs = tabService.tabs;

    // Set selected tab (change the view)
    $scope.setSelectedTab = function(tab) {
      tabService.setSelectedTab(tab);
    };

    // sets the selected tab by label
    // to be called by controllers when their
    // respective tab is selected
    this.setSelectedTabByLabel = function(label) {
      for (var i = 0; i < this.tabs.length; i++) {
        if (this.tabs[i].label === label) {
          this.selectedTab = this.tabs[i];
          break;
        }
      }
    };

    // Set 'active' or not
    $scope.tabClass = function(tab) {
      if (tabService.selectedTab == tab) {
        return 'active';
      } else {
        return '';
      }
    };

    // for ng-show
    $scope.isShowing = function() {
      return securityService.isLoggedIn();
    };

    // for ng-show
    $scope.isAdmin = function() {
      return securityService.isAdmin();
    };

    // for ng-show
    $scope.isUser = function() {
      return securityService.isUser();
    };

  } ]);

// Simple glass pane controller
ttApp.controller('GlassPaneCtrl', [ '$scope', 'gpService', function($scope, gpService) {
  console.debug('configure GlassPaneCtrl');

  $scope.glassPane = gpService.glassPane;

} ]);

// Simple error controller
ttApp.controller('ErrorCtrl', [ '$scope', 'utilService', function($scope, utilService) {
  console.debug('configure ErrorCtrl');

  $scope.error = utilService.error;

  $scope.clearError = function() {
    utilService.clearError();
  }

  $scope.setError = function(message) {
    utilService.setError(message);
  }

} ]);