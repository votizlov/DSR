var resultApp=angular.module("resultApp", []);
resultApp.controller('stageBook', function($scope, $http) {
    $scope.name = null;
    $scope.author = null;
    $scope.description = null;

    $scope.initBook = function () {
        $http.get("/result").then(function (response) {
            let book = response.data;
            if (book == null) return;
            console.log(book);
            $scope.name = book.bookID.name;
            $scope.author = book.bookID.author;
            description = book.desc;

            $("#comment p").append('Описание : ' +description);
        }, function (response) {
            console.log(response);
        });
    };
});

resultApp.controller('comments', function ($scope, $http) {

    $scope.pushComments = function (count) {
        $http.post("/result", count).then(function (value) {
            value.data.forEach(function (comment) {
                    let selector = $("#comments");
                    let piece = '<div id="comment">'+'<p>'+comment.author+'<h2>'+comment.title+'</h2>'+
                        comment.desc+'<br>'+'<br>'+comment.date+'<br>'+comment.site+'</p>'+'</div>'+"<hr color='#bfa68e'/><br>";
                    selector.append(piece);
            })
        }, function (reason) {
            console.log(reason);
        });
    }
});