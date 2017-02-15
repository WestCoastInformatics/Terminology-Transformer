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

    this.findAbbreviations = function(query, projectId, typeFilter, pfs) {
      var deferred = $q.defer();

      console.debug('find abbreviations', query, typeFilter, pfs);

      var lquery = '?projectId=' + projectId
        + (query ? '&query=' + utilService.prepQuery(query, false) : '')
        + (typeFilter ? '&filter=' + typeFilter : '');

      // Get projects
      gpService.increment();
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

    this.getAbbreviation = function(id, projectId) {
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(mldpUrl + '/abbr/' + id + '?projectId=' + projectId).then(
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

    this.removeAbbreviation = function(id, projectId) {
      var deferred = $q.defer();

      console.debug('remove abbreviation', id);
      // Get projects
      gpService.increment();
      $http['delete'](mldpUrl + '/abbr/remove/' + id + '?projectId=' + projectId).then(
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

    this.removeAbbreviations = function(ids, projectId) {
      var deferred = $q.defer();

      console.debug('remove abbreviations', ids);

      // Get projects
      gpService.increment();
      $http.post(mldpUrl + '/abbr/remove' + '?projectId=' + projectId, ids).then(
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

    this.updateAbbreviation = function(abbreviation, projectId, useProjectService) {
      var deferred = $q.defer();

      gpService.increment();

      // if flag set, update type key value directly without additional checks
      // abbreviation endpoint performs post-processing, which re-applies
      // NEEDS_REVIEW on finishReview updates
      $http.post(
        useProjectService ? projectUrl + '/typeKeyValue/update' : mldpUrl + '/abbr/update/'
          + '?projectId=' + projectId, abbreviation).then(
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

    this.addAbbreviation = function(abbr, projectId) {
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.put(mldpUrl + '/abbr/add?projectId=' + projectId, abbr ).then(
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

    this.importAbbreviationsFile = function(projectId, file) {
      var deferred = $q.defer();

      console.debug('import abbreviations file', projectId, file);
      // Get projects
      gpService.increment();
      Upload.upload({
        url : mldpUrl + '/abbr/import' + '?projectId=' + projectId,
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

    this.validateAbbreviationsFile = function(projectId, file) {
      var deferred = $q.defer();

      console.debug('validate', projectId, file);

      // Get projects
      gpService.increment();
      Upload.upload({
        url : mldpUrl + '/abbr/import/validate' + '?projectId=' + projectId,
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

    this.exportAbbreviations = function(project, acceptNew, readyOnly) {
      console.debug('exportAbbreviations', project, acceptNew, readyOnly);
      var deferred = $q.defer();
      gpService.increment()
      $http.post(
        mldpUrl + '/abbr/export?projectId=' + project.id + (readyOnly ? '&readyOnly=true' : '')
          + (acceptNew ? '&acceptNew=true' : '')).then(
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
        a.download = project.terminology + "Abbr.txt";
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

    this.computeReviewStatuses = function(projectId) {
      var deferred = $q.defer();

      console.debug('compute review statuses', type);

      // Get projects
      gpService.increment();
      $http.post(mldpUrl + '/abbr/review/compute' + '?projectId=' + projectId).then(
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

    this.getReviewForAbbreviations = function(abbrs, projectId) {
      var deferred = $q.defer();

      console.debug('get review for abbreviations', abbrs, projectId);

      if (!abbrs || abbrs.length == 0) {
        deferred.reject('getReviewForAbbreviations: Bad argument');
      } else {

        // extract ids
        var ids = abbrs.map(function(a) {
          return a.id;
        })

        // Get projects
        gpService.increment();
        $http.post(mldpUrl + '/abbr/review' + '?projectId=' + projectId, ids).then(
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
      $http
        .post(
          mldpUrl + '/concept/workflow/?projectId=' + projectId + '&workflowStatus='
            + workflowStatus, conceptIds).then(
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

    // TODO Temporary function in advance of workflow operations
    this.clearReviewWorkflow = function(projectId) {
      var deferred = $q.defer();

      console.debug('clearReviewWorkflow', projectId);

      // Get projects
      gpService.increment();
      $http.post(mldpUrl + '/concept/workflow/clear?projectId=' + projectId).then(// success
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
