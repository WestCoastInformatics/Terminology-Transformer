// Tab service
var abbrUrl = 'rxnorm/';
tsApp.service('abbrService', [ '$q', '$http', 'utilService', 'gpService', 'Upload',
  function($q, $http, utilService, gpService, Upload) {

    this.findAbbreviations = function(query, pfs) {
      var deferred = $q.defer();

      console.debug('find abbreviations', query, pfs);
      // Get projects
      gpService.increment();
      $http.post(abbrUrl + 'tkv/find' + (query ? '?query=' + query + '*' : ''), pfs).then(
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

    this.getAbbreviation = function(id) {
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(abbrUrl + 'tkv/' + id).then(
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

    this.removeAbbreviation = function(id) {
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http['delete'](abbrUrl + 'tkv/remove/' + id).then(
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

    this.updateAbbreviation = function(abbreviation) {
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.post(abbrUrl + 'tkv/update/', abbreviation).then(
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

    this.addAbbreviation = function(abbreviation) {
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.put(abbrUrl + 'tkv/add/', abbreviation).then(
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

    this.importAbbreviationsFile = function(type, file) {
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      Upload.upload({
        url : abbrUrl + 'import/' + type,
        data : {
          file : file
        }
      }).then(
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

    this.validateAbbreviationsFile = function(type, file) {
      var deferred = $q.defer();

      console.debug('validate', type, file);

      // Get projects
      gpService.increment();
      Upload.upload({
        url : abbrUrl + 'import/' + type + '/validate',
        data : {
          file : file
        }
      }).then(
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

    this.exportAbbreviations = function(type) {
      console.debug('exportAbbreviations');
      var deferred = $q.defer();
      gpService.increment()
      $http.get(abbrUrl + 'export/' + type).then(
      // Success
      function(response) {
        var blob = new Blob([ response.data ], {
          type : ''
        });

        // fake a file URL and download it
        var fileURL = URL.createObjectURL(blob);
        var a = document.createElement('a');
        a.href = fileURL;
        a.target = '_blank';
        a.download = 'abbreviations.' + type + ".txt";
        document.body.appendChild(a);
        gpService.decrement();
        a.click();
        
        deferred.resolve();

      },
      // Error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };

  } ]);