module.controller('UserDetailExtendedCtrl', function ($scope, $controller, realm, user, Schools, SchoolSearchState, BruteForceUser, User, Components,
    UserImpersonation, RequiredActions, UserStorageOperations, $location, $http, Dialog, Notifications) {

    var vm = this;
   
    console.log("---------User Extended------------");

    $scope.realm = realm;
    Schools.query({realm: realm.realm}, function(data) {
        $scope.schools = data;
    });

    // Inherit from Base Controller
    angular.extend(vm, $controller('UserDetailCtrl', {
        $scope: $scope, realm: realm, user: user, BruteForceUser: BruteForceUser,
        User: User, Components: Components, UserImpersonation: UserImpersonation,
        RequiredActions: RequiredActions, UserStorageOperations: UserStorageOperations,
        $location: $location, $http: $http, Dialog: Dialog, Notifications: Notifications
    }));
   
});