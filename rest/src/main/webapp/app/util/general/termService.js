// Raw term service
var termUrl = 'mldp';
tsApp.service('termService', [
  '$q',
  '$http',
  'utilService',
  'gpService',
  'Upload',
  function($q, $http, utilService, gpService, Upload) {

    this.findTerms = function(query, projectId, typeFilter, pfs) {
      var deferred = $q.defer();

      console.debug('find terms', query, typeFilter, pfs);

      var lquery = '?projectId=' + projectId
        + (query ? '&query=' + utilService.prepQuery(query, false) : '')
        + (typeFilter ? '&filter=' + typeFilter : '');

      // Get projects
      gpService.increment();
      $http.post(mldpUrl + '/term/find' + lquery, pfs).then(
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
    
    this.getTerm = function(id, projectId) {
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(mldpUrl + '/term/' + id + '?projectId=' + projectId).then(
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

    this.removeTerm = function(id, projectId) {
      var deferred = $q.defer();

      console.debug('remove term', id);
      // Get projects
      gpService.increment();
      $http['delete'](mldpUrl + '/term/remove/' + id + '?projectId=' + projectId).then(
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

    this.removeTerms = function(ids, projectId) {
      var deferred = $q.defer();

      console.debug('remove terms', ids);

      // Get projects
      gpService.increment();
      $http.post(mldpUrl + '/term/remove' + '?projectId=' + projectId, ids).then(
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

    this.updateTerm = function(term, projectId, useProjectService) {
      var deferred = $q.defer();

      gpService.increment();

      // if flag set, update type key value directly without additional checks
      // term endpoint performs post-processing, which re-applies
      // NEEDS_REVIEW on finishReview updates
      $http.post(
        useProjectService ? projectUrl + '/typeKeyValue/update' : mldpUrl + '/term/update/'
          + '?projectId=' + projectId, term).then(
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

    this.addTerm = function(term, projectId) {
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.put(mldpUrl + '/term/add?projectId=' + projectId, term ).then(
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
    
    
    this.importTermsFile = function(projectId, file) {
      var deferred = $q.defer();

      console.debug('import terms file', projectId, file);
      // Get projects
      gpService.increment();
      Upload.upload({
        url : mldpUrl + '/term/import' + '?projectId=' + projectId,
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
    
  } ]);
