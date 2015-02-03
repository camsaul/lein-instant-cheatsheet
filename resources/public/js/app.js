'use strict';

var App = angular.module('cheatsheet', [
    'ngResource',
    'ngRoute',
    'ui.bootstrap',
    'ui.bootstrap.tooltip'
]);

App.config(['$routeProvider', function($routeProvider) {
    // $routeProvider.when('/', {
    //     controller: 'MainController'
    // });
}]);

App.factory('API', ['$resource', function($resource) {
    return $resource('/', {}, {
        matches: {
            url: '/api/matches',
            method: 'GET',
            isArray: true
        },
        source: {
            url: '/api/source',
            method: 'GET'
        }
    });
}]);

App.controller('MainController', ['$scope', '$location', 'API', function($scope, $location, API) {
    $scope.textInput = $location.search().q || "";
    $scope.results = [];

    $scope.onTextChange = function() {
        $location.search('q', $scope.textInput);
        console.log("API CALL: " + $scope.textInput);
        API.matches({
            q: $scope.textInput
        }, function(results) {
            $scope.results = results;
        });
    };

    $scope.$on("$locationChangeSuccess", function() {
        if ($scope.textInput != $location.search().q) {
            $scope.textInput = $location.search().q || "";
            $scope.onTextChange();
        }
    });

    $scope.fetchSource = function(result, subresult) {
        console.log('-------------------- fetchSource(' + result.name + ", " + subresult.namespace + ")");
        if (subresult.src) return;
        API.source({
            ns: subresult.namespace,
            symbol: result.name
        }, function(source) {
            subresult.src = source ? source.source : "";
        });
    };

    $scope.leftTooltip = function(result) {
        var len = result.matches.length,
            output = "";
        for (var i = 0; i < len; i++) {
            var subresult = result.matches[i];
            output += '<div class="doc"><pre>' +
                "<b>" + subresult.namespace + "</b>/" +
                result.name + " " +
                "<i>" + subresult.args + "</i>\n" + '</pre></div>';
        }
        return output;
    };

    $scope.onTextChange();
}]);