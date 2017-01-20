// NDC Service

tsApp.service('ndcService', [
  '$http',
  '$q',
  'gpService',
  'utilService',
  function($http, $q, gpService, utilService) {
    console.debug("configure ndcService");

  //  $http.defaults.headers.common.Authorization = 'guest';

    // do NOT show tabs
    // tabService.setShowing(false);

    // Autocomplete function
    this.autocomplete = function(query) {

      // if invalid query, return empty results
      if (!query || query.length < 3) {
        return new Array();
      }

      // Setup deferred
      var deferred = $q.defer();

      // NO GLASS PANE
      // Make GET call
      $http.get("rxnorm/ndc/autocomplete?query=" + encodeURIComponent(query)).then(
      // success
      function(response) {
        deferred.resolve(response.data.strings);
      },
      // error
      function(response) {
        utilService.handleError(response);
        deferred.resolve(response.data);
      });

      return deferred.promise;
    };

    // Get NDC info
    this.getNdcInfo = function(ndc) {
      console.debug("Get NDC info", ndc);
      var deferred = $q.defer();

      gpService.increment();
      $http.get('rxnorm/ndc/' + encodeURIComponent(ndc) + '?history=true').then(function(response) {
        gpService.decrement();
        console.debug("  ndc info = ", response);
        deferred.resolve(response.data);
      }, function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });

      return deferred.promise;
    };

    // Get RXCUI info
    this.getRxcuiInfo = function(rxcui) {
      console.debug("Get RXCUI info", rxcui);
      var deferred = $q.defer();

      gpService.increment();
      $http.get('rxnorm/rxcui/' + encodeURIComponent(rxcui) + '?history=true').then(
        function(response) {
          gpService.decrement();
          console.debug("  rxcui info = ", response);
          deferred.resolve(response.data);
        }, function(response) {
          utilService.handleError(response);
          gpService.decrement();
          deferred.reject(response.data);
        });

      return deferred.promise;
    };

    // Get NDC properties
    this.getNdcProperties = function(ndc) {
      console.debug("Get NDC properties", ndc);
      var deferred = $q.defer();

      gpService.increment();
      $http.get('rxnorm/ndc/' + encodeURIComponent(ndc) + '/properties').then(function(response) {
        gpService.decrement();
        console.debug("  ndc properties = ", response);
        deferred.resolve(response.data);
      }, function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });

      return deferred.promise;
    };

    // Get NDC properties for SPL_SET_ID
    this.getNdcPropertiesForSplSetId = function(splSetId) {
      console.debug("Get NDC properties for SPL_SET_ID", splSetId);
      var deferred = $q.defer();

      gpService.increment();
      $http.get('rxnorm/spl/' + encodeURIComponent(splSetId) + '/ndc/properties').then(
        function(response) {
          gpService.decrement();
          console.debug("  splSetId properties = ", response);
          deferred.resolve(response.data);
        }, function(response) {
          utilService.handleError(response);
          gpService.decrement();
          deferred.reject(response.data);
        });

      return deferred.promise;
    };

    // Find RxNorm concepts
    this.findConceptsByQuery = function(query, pageSize, page) {
      // Setup deferred
      var deferred = $q.defer();

      // PFS
      var pfs = {
        startIndex : (page - 1) * pageSize,
        maxResults : pageSize,
        sortField : null,
        queryRestriction : ''
      };

      // Make POST call
      gpService.increment();
      $http.post("rxnorm/rxcui/search?query=" + encodeURIComponent(query), pfs).then(

      // success
      function(response) {
        console.debug("  concepts = ", response.data);
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
    };

    // end

  } ]);
