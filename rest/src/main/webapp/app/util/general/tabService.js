// Tab service
tsApp
  .service(
    'tabService',
    [
      '$route',
      '$location',
      'utilService',
      'gpService',
      'securityService',
      'appConfig',
      function($route, $location, utilService, gpService, securityService, appConfig) {

        this.showTabs = true;

        // Available tabs, keep as array
        this.tabs = new Array();

        // Setup tabs array based on "enabled tabs"
        // Called at the end of routes.js
        this.initEnabledTabs = function() {
          if (appConfig['deploy.enabled.tabs']) {
            var tabArray = appConfig['deploy.enabled.tabs'].split(',');
            for (var i = 0; i < tabArray.length; i++) {
              switch (tabArray[i]) {
              case 'source':
                this.tabs.push({
                  link : 'source',
                  label : 'Sources',
                  role : 'USER'
                });

                break;
              case 'content':
                this.tabs.push({
                  link : 'content',
                  label : 'Content',
                  role : false
                });
                break;
              case 'concept':
                this.tabs.push({
                  link : 'concept',
                  label : 'Concepts',
                  role : false
                });
                break;
              case 'terminology':
                this.tabs.push({
                  link : 'terminology',
                  label : 'Terminology',
                  role : false
                });
                break;
              case 'metadata':
                this.tabs.push({
                  link : 'metadata',
                  label : 'Metadata',
                  role : false
                });
                break;
              case 'abbr':
                this.tabs.push({
                  link: 'abbr',
                  label : 'Abbreviations',
                  role : 'USER'
                })
                break;
              case 'admin':
                this.tabs.push({
                  link : 'admin',
                  label : 'Admin',
                  role : 'USER'
                });
                break;
              case 'default':
                utilService.setError('Invalid tab ' + tabArray[i] + ' specified, skipping');
              }
            }
          }

          // Fail if no enabled tabs
          else {
            utilService.setError('Configuration error: no enabled tabs in appConfig');
          }
        }

        // Set a flag indicating whether tabs are to be showing
        this.setShowing = function(showTabs) {
          this.showTabs = showTabs;
        };

        // Indicates whether tabs are showing at all
        this.isShowing = function() {
          return this.showTabs;
        };

        // the selected tab
        this.selectedTab = null;

        // Sets the selected tab
        this.setSelectedTab = function(tab) {
          this.selectedTab = tab;
          $location.path(tab.link);
        };

        // sets the selected tab by label
        // to be called by controllers when their
        // respective tab is selected
        this.setSelectedTabByLabel = function(label) {
          for (var i = 0; i < this.tabs.length; i++) {
            if (this.tabs[i].label === label) {
           
              this.selectedTab = this.tabs[i];
              $location.path(this.selectedTab.link);
              break;
            }
          }
        };

        // Route an authorized user to the starting tab
        this.routeAuthorizedUser = function(userPreferences) {
          // If user preferences tab is set and valid, go to that path
          if (userPreferences && userPreferences.lastTab) {

            // Ensure user preferences lastTab is valid
            if (appConfig['deploy.enabled.tabs'].indexOf(userPreferences.lastTab.replace('/', '')) != -1) {
              $location.path(userPreferences.lastTab);
            }

            // If user preferences lastTab is invalid, just go to first tab.
            else {
              $location.path(this.tabs[0].link);
            }

          }

          // Otherwise, just go to the first tab
          else {
            $location.path(this.tabs[0].link);
          }
        };

      } ]);