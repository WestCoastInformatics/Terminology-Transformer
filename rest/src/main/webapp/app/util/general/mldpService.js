// Tab service
// TODO Change this to TransformRest or AbbrRest
// NOTE: CRUD services for type key values use project endpoint
// NOTE: File manipulations use transformer endpoint
var mldpUrl = 'mldp';
var projectUrl = 'project';
tsApp.service('mldpService', [
  '$q',
  '$http',
  'utilService',
  'gpService',
  'Upload',
  function($q, $http, utilService, gpService, Upload) {

    this.findAbbreviations = function(query, typeFilter, pfs) {
      var deferred = $q.defer();

      console.debug('find abbreviations', query, typeFilter, pfs);

      var lquery = (query ? '?query=' + utilService.prepQuery(query, false) : '')
        + (typeFilter ? (query ? '&' : '?') + 'filter=' + typeFilter : '');

      // Get projects
      gpService.increment();
      console.debug(' PFS: ' + pfs);
      $http.post(mldpUrl + '/abbr/find' + lquery, pfs).then(
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
      $http.get(mldpUrl + '/abbr/' + id).then(
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

      console.debug('remove abbreviation', id);
      // Get projects
      gpService.increment();
      $http['delete'](mldpUrl + '/abbr/remove/' + id).then(
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

    this.removeAbbreviations = function(ids) {
      var deferred = $q.defer();

      console.debug('remove abbreviations', ids);

      // Get projects
      gpService.increment();
      $http.post(mldpUrl + '/abbr/remove', ids).then(
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

    this.updateAbbreviation = function(abbreviation, useProjectService) {
      var deferred = $q.defer();

      gpService.increment();

      // if flag set, update type key value directly without additional checks
      // abbreviation endpoint performs post-processing, which re-applies
      // NEEDS_REVIEW on finishReview updates
      $http.post(
        useProjectService ? projectUrl + '/typeKeyValue/update' : mldpUrl + '/abbr/update/',
        abbreviation).then(
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
      $http.put(mldpUrl + '/abbr/add/', abbreviation).then(
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
        url : mldpUrl + '/abbr/import/' + type,
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
        url : mldpUrl + '/abbr/import/' + type + '/validate',
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

    this.exportAbbreviations = function(type, acceptNew, readyOnly) {
      console.debug('exportAbbreviations', type, acceptNew, readyOnly);
      var deferred = $q.defer();
      gpService.increment()
      var queryParams = (readyOnly ? 'readyOnly=true' : '');
      queryParams += acceptNew ? (queryParams.length > 0 ? '&' : '') + 'acceptNew=true' : '';
      $http.post(mldpUrl + '/abbr/export/' + type + (queryParams ? '?' + queryParams : '')).then(
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

    this.computeReviewStatuses = function(type) {
      var deferred = $q.defer();

      console.debug('compute review statuses', type);

      // Get projects
      gpService.increment();
      $http.post(mldpUrl + '/abbr/review/' + type + '/compute').then(
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

    this.getReviewForAbbreviations = function(abbrs) {
      var deferred = $q.defer();

      console.debug('get review for abbreviations', abbrs);

      if (!abbrs || abbrs.length == 0) {
        deferred.reject('getReviewForAbbreviations: Bad argument');
      } else {

        // extract ids
        var ids = abbrs.map(function(a) {
          return a.id;
        })

        // Get projects
        gpService.increment();
        $http.post(mldpUrl + '/abbr/review', ids).then(
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
      }
      return deferred.promise;
    }

    this.importConcepts = function(projectId, file) {
      var deferred = $q.defer();
      
      console.debug('importConcepts', projectId, file);

      // Get projects
      gpService.increment();
      Upload.upload({
        url : mldpUrl + '/concept/import?projectId=' + projectId,
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

    this.exportConcepts = function(project, acceptNew, readyOnly) {
      console.debug('exportConcepts', project, acceptNew, readyOnly);
      var deferred = $q.defer();
      gpService.increment()

      $http.post(
        mldpUrl + '/concept/export?projectId=' + project.id + (acceptNew ? '&acceptNew=true' : '')
          + (readyOnly ? '&readyOnly=true' : '')).then(
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
        a.download = 'concepts.' + project.terminology + ".txt";
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
    
    // TODO Temporary function in advance of workflow operations
    this.putConceptsInWorkflow = function(projectId, conceptIds, workflowStatus) {
      var deferred = $q.defer();
      
      console.debug('putConceptsInWorkflow', projectId, conceptIds, workflowStatus);

      // Get projects
      gpService.increment();
      $http.post(mldpUrl + '/concept/workflow/?projectId=' + projectId + '&workflowStatus=' + workflowStatus, conceptIds).then(
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
