// Route
tsApp.config(function configureRoutes($routeProvider, appConfig) {

  console.debug('Configure routes');

  $routeProvider.when('/ndc', {
    templateUrl : 'app/page/ndc/ndc.html',
    controller : 'NdcCtrl',
    reloadOnSearch : false
  });


  //
  // Configurable routes
  //

  var loginRoute = {
    templateUrl : 'app/page/login/login.html',
    controller : 'LoginCtrl',
    reloadOnSearch : false
  };

  var landingRoute = {
    templateUrl : 'app/page/landing/landing.html',
    controller : 'LandingCtrl',
    reloadOnSearch : false
  };

  var licenseRoute = {
    templateUrl : 'app/page/license/license.html',
    controller : 'LicenseCtrl',
    reloadOnSearch : false
  };

  // if landing enabled
  if (appConfig && appConfig.landingEnabled === 'true') {
    $routeProvider.when('/landing', landingRoute);
    $routeProvider.when('/', landingRoute);
  }

  // if login enabled
  if (appConfig && appConfig.loginEnabled) {
    $routeProvider.when('/login', loginRoute);
    if (appConfig && appConfig.landingEnabled !== 'true') {
      $routeProvider.when('/', loginRoute);
    }
  }

  // if license enabled
  if (appConfig && appConfig.licenseEnabled) {
    $routeProvider.when('/license', licenseRoute);
    if (appConfig && appConfig.landingEnabled !== 'true' && appConfig.loginEnabled !== 'true') {
      $routeProvider.when('/', licenseRoute);
    }
  }

  // otherwise, redirect to content
  $routeProvider.otherwise({
    redirectTo : '/ndc'
  });

});
