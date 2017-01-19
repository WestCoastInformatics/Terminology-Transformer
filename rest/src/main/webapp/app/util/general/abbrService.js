// Tab service
var abbrUrl = 'rxnorm/tkv/';
tsApp.service('abbrService', [ '$q', '$http', 'utilService', 'gpService',
  function($q, $http, utilService, gpService) {

    this.findAbbreviations = function(query, pfs) {
      var deferred = $q.defer();

      console.debug('find abbreviations', query, pfs);
      // Get projects
      gpService.increment();
      $http.post(abbrUrl + 'find' + (query ? '?query=' + query + '*': ''), pfs).then(
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
      $http.get(abbrUrl + id).then(
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
      $http['delete'](abbrUrl + 'remove/' + id).then(
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
      $http.post(abbrUrl + '/', abbreviation).then(
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
      $http.post(abbrUrl + 'update/', abbreviation).then(
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
      $http.put(abbrUrl + 'add/', abbreviation).then(
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
  } ]);