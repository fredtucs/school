(function () {

    'use strict';

    angular.module('keycloak').requires.push('school.services');

    angular.module('keycloak').config(['$routeProvider', function config($routeProvider) {
        $routeProvider
            .when('/create/school/:realm', {
                templateUrl: resourceUrl + '/partials/school-detail.html',
                resolve: {
                    realm: function (RealmLoader) {
                        return RealmLoader();
                    },
                    school: function () {
                        return {};
                    }
                },
                controller: 'SchoolDetailCtrl'
            })
            .when('/realms/:realm/rooms/:school', {
                templateUrl: resourceUrl + '/partials/school-detail.html',
                resolve: {
                    realm: function (RealmLoader) {
                        return RealmLoader();
                    },
                    school: function (SchoolLoader) {
                        return SchoolLoader();
                    }
                },
                controller: 'SchoolDetailCtrl'
            })
            .when('/realms/:realm/rooms/:school/school-rooms', {
                templateUrl: resourceUrl + '/partials/school-rooms.html',
                resolve: {
                    realm: function (RealmLoader) {
                        return RealmLoader();
                    },
                    school: function (SchoolLoader) {
                        return SchoolLoader();
                    }
                },
                controller: 'SchoolDetailCtrl'
            })
            .when('/realms/:realm/rooms', {
                templateUrl: resourceUrl + '/partials/room-list.html',
                resolve: {
                    realm: function (RealmLoader) {
                        return RealmLoader();
                    }
                },
                controller: 'RoomListCtrl'
            })
            .when('/realms/:realm/users/:user', {
                templateUrl : resourceUrl + '/partials/user-detail.html',
                resolve : {
                    realm : function(RealmLoader) {
                        return RealmLoader();
                    },
                    user : function(UserLoader) {
                        return UserLoader();
                    }
                },
                controller : 'UserDetailExtendedCtrl'
            })
            .when('/create/user/:realm', {
                templateUrl : resourceUrl + '/partials/user-detail.html',
                resolve : {
                    realm : function(RealmLoader) {
                        return RealmLoader();
                    },
                    user : function() {
                        return {};
                    }
                },
                controller : 'UserDetailExtendedCtrl'
            })
    }]);

}());

(function () {
    
    'use strict';

    angular.module('keycloak').directive('kcTabsSchool', function () {
        return {
            scope: true,
            restrict: 'E',
            replace: true,
            templateUrl: resourceUrl + '/templates/kc-tabs-school.html'
        }
    });

    angular.module('keycloak').directive('kcTabsRooms', function () {
        return {
            scope: true,
            restrict: 'E',
            replace: true,
            templateUrl: resourceUrl + '/templates/kc-tabs-rooms.html'
        }
    });

}());