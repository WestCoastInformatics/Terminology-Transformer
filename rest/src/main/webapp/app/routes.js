// For dynamic configuring of routes
// See:
// http://blog.brunoscopelliti.com/how-to-defer-route-definition-in-an-angularjs-web-app/

// Routes
tsApp.run([
  '$rootScope',
  '$location',
  '$q',
  'configureService',
  'securityService',
  'appConfig',
  'tabService',
  'utilService',
  '$route',
  function configureRoutes($rootScope, $location, $q, configureService, securityService, appConfig,
    tabService, utilService, $route) {

    console.debug('Configuring routes');
    


    // Register
    var initNextPath;
    var nextPath = $location.path();
    $rootScope.disable = $rootScope.$on("$routeChangeStart", function(event, next, current) {
      console.debug("Route change start");
      $rootScope.disable();
      $rootScope.configureRoutes().then(function() { // Restart at the top
        // If nextPath isn't set, then go with the "initNextPath" as defined by
        // route configs.
        if (!nextPath) {
          $location.path(initNextPath);
        } else {
          $location.path(nextPath);
        }
        $rootScope.configureRoutes = null;
      });
    });

    // configure the routes
    $rootScope.configureRoutes = function() {
      var deferred = $q.defer();
      // Get configuration settings (e.g. 'appConfig')
      configureService.getConfigProperties()
        .then(
          // Success
          function(data) {
            // Configure 'appConfig' so it matches prior specification
            for ( var key in data) {
              appConfig[key] = data[key];
            }

            // if appConfig not set or contains nonsensical values, throw error
            var errMsg = '';
            if (!appConfig) {
              errMsg += 'Application configuration (appConfig.js) could not be found';
            }

            // Iterate through app config variables and verify interpolation
            console.debug('Application configuration variables set:');
            for ( var key in appConfig) {
              if (appConfig.hasOwnProperty(key)) {
                console.debug('  ' + key + ': ' + appConfig[key]);
                if (appConfig[key].startsWith('${')) {
                  errMsg += 'Configuration property ' + key
                    + ' not set in project or configuration file';
                }
              }

            }
            // set page title if set
            if (appConfig['deploy.title']) {
              console.debug('setting deployment title: ' + appConfig['deploy.title']);
              document.title = appConfig['deploy.title'];
            }
           
            // if login not enabled, set guest user
            if (appConfig['deploy.login.enabled'] !== 'true') {
              console.debug("LOGIN not enabled - set guest user");
              securityService.setGuestUser();
            }

            if (errMsg.length > 0) {
              // Send an embedded 'data' object
              utilService.handleError({
                data : 'Configuration Error:\n' + errMsg
              });
            }

            if (!appConfig) {
              console.error('Application configuration could not be retrieved, see appConfig.js');
            }
            if (appConfig && !appConfig['deploy.enabled.tabs']) {
              console.error('No tabs specified for user view in appConfig.js');
            }

            console.debug('Route enabled: /configure');
            $routeProviderReference.when('/configure', {
              templateUrl : 'app/page/configure/configure.html',
              controller : 'ConfigureCtrl',
              reloadOnSearch : false
            });

            // Source Data Configurations
            if (appConfig['deploy.enabled.tabs']
              && appConfig['deploy.enabled.tabs'].split(',').indexOf('source') != -1
              && appConfig['deploy.login.enabled'] === 'true') {
              console.debug('Route enabled: /source');
              $routeProviderReference.when('/source', {
                controller : 'SourceCtrl',
                templateUrl : 'app/page/source/source.html',
                reloadOnSearch : false
              });
            }

            // Content -- Default Mode
            if (appConfig['deploy.enabled.tabs']
              && appConfig['deploy.enabled.tabs'].split(',').indexOf('content') != -1) {
              console.debug('Route enabled: /content');
              $routeProviderReference.when('/content', {
                templateUrl : 'app/page/content/content.html',
                controller : 'ContentCtrl',
                reloadOnSearch : false
              });
            }

            // Terminology page
            if (appConfig['deploy.enabled.tabs']
              && appConfig['deploy.enabled.tabs'].split(',').indexOf('terminology') != -1) {
              console.debug('Route enabled: /terminology');
              $routeProviderReference.when('/terminology', {
                templateUrl : 'app/page/terminology/terminology.html',
                controller : 'TerminologyCtrl',
                reloadOnSearch : false
              });
            }

            // Metadata page
            if (appConfig['deploy.enabled.tabs']
              && appConfig['deploy.enabled.tabs'].split(',').indexOf('metadata') != -1) {
              console.debug('Route enabled: /metadata');
              $routeProviderReference.when('/metadata', {
                templateUrl : 'app/page/metadata/metadata.html',
                controller : 'MetadataCtrl',
                reloadOnSearch : false
              });
            }

            // Abbreviations page
            if (appConfig['deploy.enabled.tabs']
              && appConfig['deploy.enabled.tabs'].split(',').indexOf('abbr') != -1) {
              console.debug('Route enabled: /abbr');
              $routeProviderReference.when('/abbr', {
                templateUrl : 'app/page/abbr/abbr.html',
                controller : 'AbbrCtrl',
                reloadOnSearch : false
              });
            }
            
            // Abbreviations page
            if (appConfig['deploy.enabled.tabs']
              && appConfig['deploy.enabled.tabs'].split(',').indexOf('term') != -1) {
              console.debug('Route enabled: /term');
              $routeProviderReference.when('/term', {
                templateUrl : 'app/page/term/term.html',
                controller : 'TermCtrl',
                reloadOnSearch : false
              });
            }

            // Workflow page
            if (appConfig['deploy.enabled.tabs']
              && appConfig['deploy.enabled.tabs'].split(',').indexOf('workflow') != -1) {
              console.debug('Route enabled: /workflow');
              $routeProviderReference.when('/workflow', {
                templateUrl : 'app/page/workflow/workflow.html',
                controller : 'WorkflowCtrl',
                reloadOnSearch : false
              });
            }

            // Edit page
            if (appConfig['deploy.enabled.tabs']
              && appConfig['deploy.enabled.tabs'].split(',').indexOf('concept') != -1) {
              console.debug('Route enabled: /concept');
              $routeProviderReference.when('/concept', {
                templateUrl : 'app/page/concept/concept.html',
                controller : 'ConceptCtrl',
                reloadOnSearch : false
              });

            }

            // Process page
            if (appConfig['deploy.enabled.tabs']
              && appConfig['deploy.enabled.tabs'].split(',').indexOf('process') != -1) {
              console.debug('Route enabled: /process');
              $routeProviderReference.when('/process', {
                templateUrl : 'app/page/process/process.html',
                controller : 'ProcessCtrl',
                reloadOnSearch : false
              });
            }

            // Admin page
            if (appConfig['deploy.enabled.tabs']
              && appConfig['deploy.enabled.tabs'].split(',').indexOf('admin') != -1) {
              console.debug('Route enabled: /admin');
              $routeProviderReference.when('/admin', {
                templateUrl : 'app/page/admin/admin.html',
                controller : 'AdminCtrl',
                reloadOnSearch : false
              });
            }

            // These routes are always enabled
            // Content with mode set (e.g. 'simple' for component report)
            console
              .debug('Route enabled: /content/:mode/:type/:terminology/:version/:terminologyId');
            $routeProviderReference.when(
              '/content/:mode/:type/:terminology/:version/:terminologyId', {
                templateUrl : function(urlAttr) {
                  return 'app/page/content/' + urlAttr.mode + '.html';
                },
                controller : 'ContentCtrl',
                reloadOnSearch : false
              });

            // Content with mode set (e.g. 'simple' for component report)
            console.debug('Route enabled: /content/:mode/:type/:terminology/:id');
            $routeProviderReference.when('/content/:mode/:type/:terminology/:id', {
              templateUrl : function(urlAttr) {
                return 'app/page/content/' + urlAttr.mode + '.html';
              },
              controller : 'ContentCtrl',
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
            if (appConfig && appConfig['deploy.landing.enabled'] === 'true') {
              $routeProviderReference.when('/landing', landingRoute);
              $routeProviderReference.when('/', landingRoute);
              initNextPath = '/landing';
            }

            // if login enabled
            if (appConfig && appConfig['deploy.login.enabled'] === 'true') {
              $routeProviderReference.when('/login', loginRoute);
              if (appConfig && appConfig['deploy.landing.enabled'] !== 'true') {
                $routeProviderReference.when('/', loginRoute);
                initNextPath = '/login';
              }
            }

            // if license enabled
            if (appConfig && appConfig['deploy.license.enabled'] === 'true') {
              $routeProviderReference.when('/license', licenseRoute);
              if (appConfig && appConfig['deploy.landing.enabled'] !== 'true'
                && appConfig['deploy.login.enabled'] !== 'true') {
                $routeProviderReference.when('/', licenseRoute);
                initNextPath = '/license';
              }
            }

            // if none enabled, default is content/
            if (appConfig['deploy.landing.enabled'] !== 'true'
              && appConfig['deploy.login.enabled'] !== 'true'
              && appConfig['deploy.license.enabled'] !== 'true') {
              console.debug('No landing, license, or login pages -- default route is /content');
              $routeProviderReference.when('/', {
                templateUrl : 'app/page/content/content.html',
                controller : 'ContentCtrl',
                reloadOnSearch : false
              });
              initNextPath = '/content';
            }

            // Now that enabled tabs exists, initialize it
            tabService.initEnabledTabs();
            
            console.debug('Routes', $route.routes);
            deferred.resolve();
          }, function(data) {
            deferred.reject(data);
          }

        );
      return deferred.promise;
    } // end configureRoutes

    // Create an initial route change to start it all
    $routeProviderReference.otherwise({
      redirectTo : '/content'
    });

  }

]);
