'use strict';

var module = angular.module('school.services', ['ngResource', 'ngRoute']);

module.service('SchoolSearchState', function () {
    this.isFirstSearch = true;
    this.query = {
        max: 20,
        first: 0
    };
});

module.factory('School', function ($resource) {
    return $resource(authUrl + '/realms/:realm/rooms/schools/:schoolId', {
        realm: '@realm',
        schoolId: '@schoolId'
    }, {
        update: {
            method: 'PUT'
        }
    });
});

module.factory('Schools', function ($resource) {
    return $resource(authUrl + '/realms/:realm/rooms/schools/list', {
        realm: '@realm'
    })
});