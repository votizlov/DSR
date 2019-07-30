var resultApp=angular.module("resultApp", []);
resultApp.controller('stageItem', function($scope, $http) {
    $scope.item = {};

    $scope.getItem = function () {
        $http.post("/result-getItem").then(function (response) {
            $scope.item = response.data;
            console.log('url('+$scope.item.urlImg+')');
            $("#img_item").css("background-image", 'url('+$scope.item.urlImg+')');
        }, function (response) {
            console.log(response);
        });
    };
});

resultApp.controller('comments', function ($scope, $http) {

    $scope.getComments = function (page) {
        console.log(page);
        $http.post("/result-getComments", page).then(function (value) {
            $(".column_comments").html("");
            value.data.forEach(function (comment) {
                    let selector = $(".column_comments");
                    let piece = '<div class="comment">'+'<p>'+comment.author+'<h2>'+comment.title+'</h2>'+
                        comment.desc+'<br>'+'<br>'+comment.date+'<br>'+comment.site+'</p>'+'</div>'+"<hr color='#bfa68e'/><br>";
                    selector.append(piece);
            })
        }, function (reason) {
            console.log(reason);
        });
    };
});

resultApp.controller('back', function ($scope, $http) {
    $scope.toBack = function () {
        $http.delete("/result-clear").then(function (value) {
            console.log(value);
            if (value.data)
                document.location.href = "search";
        }, function (reason) {
            console.log(reason);
        });
    }
});