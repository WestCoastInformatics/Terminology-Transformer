ttApp.service('sourceDataService', [ '$http', '$location', '$q', '$cookies', 'utilService',
  'gpService', function($http, $location, $q, ngCookies, utilService, gpService) {
    console.debug('configure sourceDataService');

    /**
     * Retrieves details for all currently uploaded files
     */
    this.getSourceDataFiles = function() {
      var deferred = $q.defer();
      $http.get(fileUrl + 'sourceDataFile/sourceDataFiles').then(function(response) {
        deferred.resolve(response.data);
      }, function(error) {

        deferred.reject(error);
      });
      return deferred.promise;
    }

    /**
     * Deletes a file from the server (by filename)
     */
    this.deleteSourceDataFile = function(fileId) {
      var deferred = $q.defer();
      $http['delete'](fileUrl + 'sourceDataFile/delete/' + fileId).then(function(response) {
        deferred.resolve();
      }, function(error) {

        deferred.reject(error);
      });
      return deferred.promise;
    }

    this.saveSourceDataFile = function(sourceDataFile) {
      var deferred = $q.defer();
      if (sourceDataFile.id) {
        $http.post(fileUrl + 'sourceDataFile/update', sourceDataFile).then(function(response) {
          deferred.resolve(sourceDataFile);
        }, function(error) {

          deferred.reject(sourceDataFile);
        });
      } else {
        $http.put(fileUrl + 'sourceDataFile/add', sourceDataFile).then(function(response) {
          deferred.resolve(response);
        }, function(error) {

          deferred.reject();
        });
      }
      return deferred.promise;
    }

    this.saveSourceData = function(sourceData) {
      var deferred = $q.defer();
      if (sourceData.id) {
        $http.post(fileUrl + 'sourceData/update', sourceData).then(function(response) {
          deferred.resolve(sourceData);
        }, function(error) {

          deferred.reject(sourceData);
        });
      } else {
        $http.put(fileUrl + 'sourceData/add', sourceData).then(function(response) {
          deferred.resolve(response);
        }, function(error) {

          deferred.reject();
        });
      }
      return deferred.promise;
    }

    this.deleteSourceData = function(sourceData) {
      var deferred = $q.defer()
      $http['delete'](fileUrl + 'sourceData/delete/' + sourceData.id).then(function(response) {
        deferred.resolve();
      }, function(error) {
        deferred.reject(error);
      });
      return deferred.promise;
    }

    this.getSourceDatas = function() {
      var deferred = $q.defer();
      $http.get(fileUrl + 'sourceData/sourceDatas').then(function(response) {
        deferred.resolve(response.data);
      }, function(error) {
        deferred.reject([]);
      });
      return deferred.promise;
    };

    // cached converter names
    var converterNames = null;

    // get converter names
    this.getConverterNames = function() {
      var deferred = $q.defer();

      if (converterNames) {
        deferred.resolve(converterNames);
      } else {

        $http.get(fileUrl + 'converter/converters').then(function(response) {
          console.debug('converter names', response);
          converterNames = response.data.strings;
          deferred.resolve(response.data.strings);
        }, function(error) {
          deferred.reject([]);
        });
      }
      return deferred.promise;
    };
  } ]);

// Error service
ttApp
  .service(
    'utilService',
    [
      '$location',
      function($location) {
        console.debug('configure utilService');
        // declare the error
        this.error = {
          message : null
        };

        // tinymce options
        this.tinymceOptions = {
          menubar : false,
          statusbar : false,
          plugins : 'autolink autoresize link image charmap searchreplace lists paste',
          toolbar : 'undo redo | styleselect lists | bold italic underline strikethrough | charmap link image',
          forced_root_block : ''
        }

        // Sets the error
        this.setError = function(message) {
          this.error.message = message;
        }

        // Clears the error
        this.clearError = function() {
          this.error.message = null;
        }
        // Handle error message
        this.handleError = function(response) {
          console.debug('Handle error: ', response);
          this.error.message = response.data;
          // If authtoken expired, relogin
          if (this.error.message && this.error.message.indexOf('AuthToken') != -1) {
            // Reroute back to login page with 'auth
            // token has
            // expired' message
            $location.path('/');
          }
        }

        // Convert date to a string
        this.toDate = function(lastModified) {
          var date = new Date(lastModified);
          var year = '' + date.getFullYear();
          var month = '' + (date.getMonth() + 1);
          if (month.length == 1) {
            month = '0' + month;
          }
          var day = '' + date.getDate();
          if (day.length == 1) {
            day = '0' + day;
          }
          var hour = '' + date.getHours();
          if (hour.length == 1) {
            hour = '0' + hour;
          }
          var minute = '' + date.getMinutes();
          if (minute.length == 1) {
            minute = '0' + minute;
          }
          var second = '' + date.getSeconds();
          if (second.length == 1) {
            second = '0' + second;
          }
          return year + '-' + month + '-' + day + ' ' + hour + ':' + minute + ':' + second;
        }

        // Convert date to a short string
        this.toShortDate = function(lastModified) {
          var date = new Date(lastModified);
          var year = '' + date.getFullYear();
          var month = '' + (date.getMonth() + 1);
          if (month.length == 1) {
            month = '0' + month;
          }
          var day = '' + date.getDate();
          if (day.length == 1) {
            day = '0' + day;
          }
          return year + '-' + month + '-' + day;
        }

        // Convert date to a simple string
        this.toSimpleDate = function(lastModified) {
          var date = new Date(lastModified);
          var year = '' + date.getFullYear();
          var month = '' + (date.getMonth() + 1);
          if (month.length == 1) {
            month = '0' + month;
          }
          var day = '' + date.getDate();
          if (day.length == 1) {
            day = '0' + day;
          }
          return year + month + day;
        }

        // Utility for cleaning a query
        this.cleanQuery = function(queryStr) {
          if (queryStr == null) {
            return '';
          }
          var cleanQuery = queryStr;
          // Replace all slash characters
          cleanQuery = queryStr.replace(new RegExp('[/\\\\]', 'g'), ' ');
          // Remove brackets if not using a fielded query
          if (queryStr.indexOf(':') == -1) {
            cleanQuery = queryStr.replace(new RegExp('[^a-zA-Z0-9:\\.\\-\'\\*]', 'g'), ' ');
          }

          return cleanQuery;
        }

        // Table sorting mechanism
        this.setSortField = function(table, field, paging) {
          paging[table].sortField = field;
          // reset page number too
          paging[table].page = 1;
          // handles null case also
          if (!paging[table].ascending) {
            paging[table].ascending = true;
          } else {
            paging[table].ascending = false;
          }
          // reset the paging for the correct table
          for ( var key in paging) {
            if (paging.hasOwnProperty(key)) {
              if (key == table)
                paging[key].page = 1;
            }
          }
        };

        // Return up or down sort chars if sorted
        this.getSortIndicator = function(table, field, paging) {
          if (paging[table].ascending == null) {
            return '';
          }
          if (paging[table].sortField == field && paging[table].ascending) {
            return '▴';
          }
          if (paging[table].sortField == field && !paging[table].ascending) {
            return '▾';
          }
        };

        // Helper to get a paged array with show/hide flags
        // and filtered by query string
        this.getPagedArray = function(array, paging, pageSize) {
          var newArray = new Array();

          // if array blank or not an array, return blank
          // list
          if (array == null || array == undefined || !Array.isArray(array)) {
            return newArray;
          }

          newArray = array;

          // apply sort if specified
          if (paging.sortField) {
            // if ascending specified, use that value,
            // otherwise use false
            newArray.sort(this.sort_by(paging.sortField, paging.ascending))
          }

          // apply filter
          if (paging.filter) {
            newArray = this.getArrayByFilter(newArray, paging.filter);
          }

          // get the page indices
          var fromIndex = (paging.page - 1) * pageSize;
          var toIndex = Math.min(fromIndex + pageSize, array.length);

          // slice the array
          var results = newArray.slice(fromIndex, toIndex);

          // add the total count before slicing
          results.totalCount = newArray.length;

          return results;
        }

        // function for sorting an array by (string) field
        // and direction
        this.sort_by = function(field, reverse) {

          // key: function to return field value from
          // object
          var key = function(x) {
            return x[field]
          };

          // convert reverse to integer (1 = ascending, -1
          // =
          // descending)
          reverse = !reverse ? 1 : -1;

          return function(a, b) {
            return a = key(a), b = key(b), reverse * ((a > b) - (b > a));
          }
        }

        // Get array by filter text matching terminologyId
        // or name
        this.getArrayByFilter = function(array, filter) {
          var newArray = [];

          for ( var object in array) {

            if (this.objectContainsFilterText(array[object], filter)) {
              newArray.push(array[object]);
            }
          }
          return newArray;
        }

        // Returns true if any field on object contains
        // filter text
        this.objectContainsFilterText = function(object, filter) {

          if (!filter || !object)
            return false;

          for ( var prop in object) {
            var value = object[prop];
            // check property for string, note this will
            // cover child elements
            if (value && value.toString().toLowerCase().indexOf(filter.toLowerCase()) != -1) {
              return true;
            }
          }

          return false;
        }

        // Get words of a string
        this.getWords = function(str) {
          // Same as in tinymce options
          return str.match(/[^\s,\.]+/g);
        }

        // Single and multiple-word ordered phrases
        this.getPhrases = function(str) {
          var words = str.match(/[^\s,\.]+/g);
          var phrases = [];

          for (var i = 0; i < words.length; i++) {
            for (var j = i + 1; j <= words.length; j++) {
              var phrase = words.slice(i, j).join(' ');
              // a phrase have at least 5 chars and no
              // start/end words that are purely
              // punctuation
              if (phrase.length > 5 && words[i].match(/.*[A-Za-z0-9].*/)
                && words[j - 1].match(/.*[A-Za-z0-9].*/)) {
                phrases.push(phrase.toLowerCase());
              }
            }
          }
          return phrases;
        }

      } ]);

// Glass pane service
ttApp.service('gpService', function() {
  console.debug('configure gpService');
  // declare the glass pane counter
  this.glassPane = {
    counter : 0,
    messages : []
  };

  this.isGlassPaneSet = function() {
    return this.glassPane.counter;
  }

  this.isGlassPaneNegative = function() {
    return this.glassPane.counter < 0;
  }

  // Increments glass pane counter
  this.increment = function(message) {
    if (message) {
      this.glassPane.messages.push(message);
    }
    this.glassPane.counter++;
  }

  // Decrements glass pane counter
  this.decrement = function(message) {
    if (message) {
      var index = this.glassPane.messages.indexOf(message); // <-- Not
      // supported
      // in <IE9
      if (index !== -1) {
        this.glassPane.messages.splice(index, 1);
      }
    }
    this.glassPane.counter--;
  }

});

// Security service
ttApp.service('securityService', [ '$rootScope', '$http', '$location', '$q', '$cookies',
  'utilService', 'gpService',
  function($rootScope, $http, $location, $q, $cookies, utilService, gpService) {
    console.debug('configure securityService');

    // Declare the user
    var user = {
      userName : null,
      password : null,
      name : null,
      authToken : null,
      applicationRole : null,
      userPreferences : null
    };

    // Search results
    var searchParams = {
      page : 1,
      query : null
    }

    this.getCurrentAuthToken = function() {
      return user.authToken;
    }

    // Gets the user
    this.getUser = function() {

      // Determine if page has been reloaded
      if (!$http.defaults.headers.common.Authorization) {

        if ($cookies.get('user')) {
          console.debug('no header', $cookies.get('user'));
          // Retrieve cookie
          var cookieUser = JSON.parse($cookies.get('user'));
          // If there is a user cookie, load it
          if (cookieUser) {
            this.setUser(cookieUser);
            $http.defaults.headers.common.Authorization = user.authToken;
          }
        }
      }
      return user;
    }

    // Sets the user
    this.setUser = function(data) {
      user.userName = data.userName;
      user.name = data.name;
      user.authToken = data.authToken;
      user.password = '';
      user.applicationRole = data.applicationRole;
      user.userPreferences = data.userPreferences;

      $http.defaults.headers.common.Authorization = user.authToken;

      // Whenver set user is called, we should save a
      // cookie
      $cookies.put('user', JSON.stringify(user));

    }

    // Clears the user
    this.clearUser = function() {
      user.userName = null;
      user.name = null;
      user.authToken = null;
      user.password = null;
      user.applicationRole = null;
      user.userPreferences = null;
      $http.defaults.headers.common.Authorization = null;
      $rootScope.tabs = [];
      $cookies.remove('user');
    }

    var httpClearUser = this.clearUser;

    /**
     * Retrieves user details for specified auth token
     */
    this.getUserForAuthToken = function(authToken) {
      $http.get(securityUrl + 'user').then(function(user) {
        // re-append auth token
        user.authToken = authToken;
        return response;
      }, function(error) {
        return null;
      })
    }

    // isLoggedIn function
    this.isLoggedIn = function() {
      return user.authToken;
    }

    // isAdmin function
    this.isAdmin = function() {
      return user.applicationRole == 'ADMIN';
    }

    // isUser function
    this.isUser = function() {
      return user.applicationRole == 'ADMIN' || user.applicationRole == 'USER';
    }

    // checks if current user has privileges of specified role
    this.hasPrivilegesOfRole = function(role) {
      switch (role) {
      case 'VIEWER':
        return true;
      case 'USER':
        return user.applicationRole === 'USER' || user.applicationRole == 'ADMIN';
      case 'ADMIN':
        return user.applicationRole === 'ADMIN';
      default:
        return false;
      }

    }

    // Logout
    this.logout = function() {
      if (user.authToken == null) {
        alert('You are not currently logged in');
        return;
      }
      gpService.increment();

      // logout
      $http.get(securityUrl + 'logout/' + user.authToken).then(
      // success
      function(response) {

        // clear scope variables
        httpClearUser();

        // clear http authorization
        // header
        $http.defaults.headers.common.Authorization = null;
        gpService.decrement();
        $location.url('login');
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
      });
    }

    // Accessor for search params
    this.getSearchParams = function() {
      return searchParams;
    }

    // get all users
    this.getUsers = function() {
      console.debug('getUsers');
      var deferred = $q.defer();

      // Get users
      gpService.increment()
      $http.get(securityUrl + 'user/users').then(
      // success
      function(response) {
        console.debug('  users = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    }

    // get user for auth token
    this.getUserForAuthToken = function() {
      console.debug('getUserforAuthToken');
      var deferred = $q.defer();

      // Get users
      gpService.increment()
      $http.get(securityUrl + 'user').then(
      // success
      function(response) {
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    }
    // add user
    this.addUser = function(user) {
      console.debug('addUser');
      var deferred = $q.defer();

      // Add user
      gpService.increment()
      $http.put(securityUrl + 'user/add', user).then(
      // success
      function(response) {
        console.debug('  user = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    }

    // update user
    this.updateUser = function(user) {
      console.debug('updateUser');
      var deferred = $q.defer();

      // Add user
      gpService.increment()
      $http.post(securityUrl + 'user/update', user).then(
      // success
      function(response) {
        console.debug('  user = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    }

    // remove user
    this.removeUser = function(user) {
      console.debug('removeUser');
      var deferred = $q.defer();

      // Add user
      gpService.increment();
      $http['delete'](securityUrl + 'user/remove' + '/' + user.id).then(
      // success
      function(response) {
        console.debug('  user = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    }

    // get application roles
    this.getApplicationRoles = function() {
      console.debug('getApplicationRoles');
      var deferred = $q.defer();

      // Get application roles
      gpService.increment()
      $http.get(securityUrl + 'roles').then(
      // success
      function(response) {
        console.debug('  roles = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    }

    // Finds users as a list
    this.findUsersAsList = function(queryStr, pfs) {
      console.debug('findUsersAsList', queryStr, pfs);
      // Setup deferred
      var deferred = $q.defer();

      // Make POST call
      gpService.increment();
      $http.post(securityUrl + 'user/find' + '?query=' + queryStr, pfs)
      // +
      // encodeURIComponent(utilService.cleanQuery(queryStr)),
      // pfs)
      .then(
      // success
      function(response) {
        console.debug('  output = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });

      return deferred.promise;
    }

    // update user preferences
    this.updateUserPreferences = function(userPreferences) {
      console.debug('updateUserPreferences');
      // skip if user preferences is not set
      if (!userPreferences) {
        return;
      }

      // Whenever we update user preferences, we need
      // to update the cookie
      $cookies.put('user', JSON.stringify(user));

      var deferred = $q.defer();

      gpService.increment()
      $http.post(securityUrl + 'user/preferences/update', userPreferences).then(
      // success
      function(response) {
        console.debug('  userPreferences = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    }

  } ]);

// Tab service
ttApp.service('tabService', [ '$rootScope', '$location', 'utilService', 'gpService',
  'securityService', function($rootScope, $location, utilService, gpService, securityService) {
    console.debug('configure tabService');

    // Available tabs
    var tabsAvailable = [ {
      link : 'upload',
      label : 'Files',
      minRole : 'USER'
    }, {
      link : 'source',
      label : 'Source Data',
      minRole : 'USER'
    }, {
      link : 'transform',
      label : 'Transform',
      minRole : 'VIEWER'
    }, {
      link : 'edit',
      label : 'Review',
      minRole : 'USER'
    }, {
      link : 'admin',
      label : 'Admin',
      minRole : 'ADMIN'
    } ];

    this.initializeTabsForUser = function(user) {

      console.debug('get tabs for user', user);
      var tabs = [];
      angular.forEach(tabsAvailable, function(tab) {
        console.debug('checking tab', tab, tab.minRole);
        if (securityService.hasPrivilegesOfRole(tab.minRole)) {
          tabs.push(tab);
        }
      });

      console.debug('tabs', tabs);

      if (tabs.length === 0) {
        handleError("Could not set available tab content from user information");
      } else {

        if (user && user.userPreferences && user.userPreferences.lastTab) {
          console.debug('location set to ' + user.userPreferences.lastTab.link);
          $location.url(user.userPreferences.lastTab.link);
        } else {
          console.debug('location set to ' + tabs[0].link);
          $location.url(tabs[0].link);
        }
      }

      $rootScope.tabs = tabs;
    };

    // Sets the selected tab
    this.setSelectedTab = function(tab) {
      this.selectedTab = tab;
      console.debug('location set to ' + tab.link);
      $location.url(tab.link);
    }

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
    }

  } ]);

// Websocket service

ttApp.service('websocketService', [ '$location', 'utilService', 'gpService',
  function($location, utilService, gpService) {
    console.debug('configure websocketService');
    this.data = {
      message : null
    };

    // Determine URL without requiring injection
    // should support wss for https
    // and assumes REST services and websocket are deployed together
    this.getUrl = function() {
      var url = window.location.href;
      url = url.replace('http', 'ws');
      url = url.replace('index.html', '');
      url = url.substring(0, url.indexOf('#'));
      url = url + '/websocket';
      console.debug('url = ' + url);
      return url;

    }

    this.connection = new WebSocket(this.getUrl());

    this.connection.onopen = function() {
      // Log so we know it is happening
      console.log('Connection open');
    }

    this.connection.onclose = function() {
      // Log so we know it is happening
      console.log('Connection closed');
    }

    // error handler
    this.connection.onerror = function(error) {
      utilService.handleError(error, null, null, null);
    }

    // handle receipt of a message
    this.connection.onmessage = function(e) {
      var message = e.data;
      console.log('MESSAGE: ' + message);
      // TODO: what else to do?
    }

    // Send a message to the websocket server endpoint
    this.send = function(message) {
      this.connection.send(JSON.stringify(message));
    }

  } ]);