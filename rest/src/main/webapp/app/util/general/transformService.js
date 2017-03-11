// Raw term service
var transformUrl = 'transform';
tsApp.service('transformService', [
  '$q',
  '$http',
  'utilService',
  'gpService',
  'Upload',
  function($q, $http, utilService, gpService, Upload) {

    /**
     * Returns input/output data contexts as two-item DataContextList
     */
    this.getDataContextList = function(terminology, outputType) {

      return {
        dataContexts : [
        // input model
        {
          type : 'NAME',
          terminology : terminology
        },

        // output model
        {
          type : outputType,
          terminology : terminology
        } ],
        totalCount : 2
      };
    }

    this.process = function(inputStr, dataContext) {
      console.debug('process', inputStr, dataContext)
      var deferred = $q.defer();

      gpService.increment();

      // if flag set, update type key value directly without additional checks
      // abbreviation endpoint performs post-processing, which re-applies
      // NEEDS_REVIEW on finishReview updates
      $http.post(transformUrl + '/process/' + encodeURIComponent(inputStr),
        dataContext).then(
      // success
      function(response) {
        gpService.decrement();
        console.debug(response);
        angular.forEach(response.data.scoredDataContextTuples, function(tuple) {
          tuple.data = JSON.parse(tuple.data);
        });
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
