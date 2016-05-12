// NDC controller
tsApp
  .controller(
    'NdcCtrl',
    [
      '$scope',
      '$location',
      '$anchorScroll',
      '$sce',
      '$routeParams',
      'ndcService',
      'utilService',
      'appConfig',
      function($scope, $location, $anchorScroll, $sce, $routeParams, ndcService, utilService,
        appConfig) {
        console.debug('configure NdcCtrl');

        // Set up scope
        $scope.query = null;

        $scope.model = null;
        $scope.propertiesModel = null;
        $scope.propertiesListModel = null;

        $scope.ndc = null;
        $scope.rxcui = null;
        $scope.rxcuiUrl = null;
        $scope.splSetId = null;
        $scope.splSetUrl = null;
        $scope.splSetImageUrl = null;

        $scope.paging = {};
        $scope.paging['history'] = {
          page : 1,
          filter : '',
          typeFilter : '',
          sortField : 'start',
          ascending : null,
          pageSize : 10,
          showHidden : false
        };
        $scope.paging['splSetId'] = {
          page : 1,
          filter : '',
          typeFilter : '',
          sortField : 'ndc11',
          ascending : null,
          pageSize : 1,
          showHidden : false
        };
        $scope.paging['search'] = {
          page : 1,
          filter : '',
          typeFilter : '',
          sortField : '',
          ascending : null,
          pageSize : 10,
          showHidden : false
        };
        $scope.searchResults = null;
        $scope.selectedResult = null;

        // Define functions

        // Submit form
        $scope.submit = function(query, clearFlag) {

          if (clearFlag) {
            $scope.searchResults = null;
            $scope.selectedResult = null;
          }

          // Reset data model
          $scope.model = null;
          $scope.propertiesModel = null;
          $scope.propertiesListModel = null;
          $scope.ndc = null;
          $scope.rxcui = null;
          $scope.rxcuiUrl = null;
          $scope.splSetId = null;
          $scope.splSetUrl = null;
          $scope.splSetImageUrl = null;
          $scope.pagedSplSet = null;
          $scope.pagedHistory = null;

          var queryTrim = query.trim();

          // if (query) {
          // $scope.query = query.trim();
          // } else {
          // return;
          // }

          // If < 9 digits, do an RXCUI lookup
          if ($scope.isRxcui(queryTrim)) {
            $scope.rxcui = queryTrim;
            // Lookup RXCUI info
            ndcService
              .getRxcuiInfo(queryTrim)
              .then(
                // Success
                function(data) {
                  $scope.model = data;
                  $scope.rxcuiUrl = $sce
                    .trustAsResourceUrl("http://bioportal.bioontology.org/ontologies/RXNORM?p=classes&conceptid="
                      + $scope.model.rxcui);
                  $scope.getPagedHistory();
                });
          }

          // If > 15 digits, do an SPL_SET_ID lookup
          else if ($scope.isSplSetId(queryTrim)) {
            $scope.splSetId = queryTrim;
            $scope.splSetUrl = $sce
              .trustAsResourceUrl("https://dailymed.nlm.nih.gov/dailymed/drugInfo.cfm?setid="
                + $scope.splSetId);
            ndcService.getNdcPropertiesForSplSetId(queryTrim).then(
            // Success
            function(data) {
              $scope.propertiesListModel = data;
              $scope.getPagedSplSet();
            });

          }

          // Otherwise, do an NDC lookup
          else if ($scope.isNdc(queryTrim)) {
            $scope.ndc = queryTrim;
            ndcService.getNdcInfo(queryTrim).then(
            // Success
            function(data) {
              $scope.model = data;
              $scope.getPagedHistory();
            });
            ndcService.getNdcProperties(queryTrim).then(
            // Success
            function(data) {
              $scope.propertiesModel = data;
            });
          }

          // Anything else, just perform a normal concept search (on all
          // fields).
          else {
            $scope.selectedResult = null;
            $scope.paging['search'].page = 1;
            $scope.findConceptsByQuery();
          }

          // Scroll to top
          $location.hash('top');
          $anchorScroll();

        };

        // Set trusted RXCUI URL
        $scope.getRxcuiUrl = function(rxcui) {
          return $sce
            .trustAsResourceUrl("http://bioportal.bioontology.org/ontologies/RXNORM?p=classes&conceptid="
              + rxcui);
        };

        // Set trusted RXCUI URL
        $scope.getSplSetUrl = function(splSetId) {
          return $sce
            .trustAsResourceUrl("https://dailymed.nlm.nih.gov/dailymed/drugInfo.cfm?setid="
              + splSetId);
        };

        // Identify input string as an RXCUI
        // < 9 charactrs and all digits
        $scope.isRxcui = function(query) {
          return query.length < 9 && query.match(/^[\d]+$/);
        };

        // Identify input string as an SPL_SET_ID
        // UUID format
        $scope.isSplSetId = function(query) {
          return query.match(/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}/);
        };

        // Identify input string as an NDC
        // Between 8 and 14 digits or dashes
        $scope.isNdc = function(query) {
          return query.length > 8 && query.length < 14 && query.match(/^[\d\-]+$/);
        };

        // Autocomplete function
        $scope.autocomplete = function(query) {
          // if invalid search terms, return empty array
          if (query == null || query == undefined || query.length < 3) {
            return new Array();
          }
          return ndcService.autocomplete(query);
        };

        // Get paged history (assume all are loaded)
        $scope.getPagedHistory = function() {
          $scope.pagedHistory = utilService.getPagedArray($scope.model.history,
            $scope.paging['history']);
        };

        // Get paged splSetId search property info (assume all are loaded)
        $scope.getPagedSplSet = function() {
          if ($scope.propertiesListModel) {
            $scope.pagedSplSet = utilService.getPagedArray($scope.propertiesListModel.list,
              $scope.paging['splSetId']);
          }
        };

        // Return up or down sort chars if sorted
        $scope.getSortIndicator = function(table, field) {
          return utilService.getSortIndicator(table, field, $scope.paging);
        };

        // sort mechanism
        $scope.setSortField = function(table, field) {
          utilService.setSortField(table, field, $scope.paging);

          // refresh paging
          if (table === 'history') {
            $scope.getPagedHistory();
          } else if (table === 'splSetId') {
            $scope.getPagedSplSet();
          }
        };

        // Perform concept search
        $scope.findConceptsByQuery = function() {
          ndcService.findConceptsByQuery($scope.query, $scope.paging['search'].pageSize,
            $scope.paging['search'].page).then(
          // Success
          function(data) {
            $scope.searchResults = data.results;
            $scope.searchResults.totalCount = data.totalCount;
            // Select first result
            if ($scope.searchResults.length > 0) {
              $scope.selectResult($scope.searchResults[0]);
            }
          });

        };

        // Select a search result and perform the search
        $scope.selectResult = function(result) {
          $scope.selectedResult = result.id;
          $scope.submit(result.terminologyId);
        };

        // Initialize
        if ($routeParams.query) {
          $scope.submit(query);
        }

      } ]);