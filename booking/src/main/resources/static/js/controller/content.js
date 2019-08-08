var resultApp=angular.module("resultApp", []);
resultApp.controller('stageItem', function($scope, $http) {
    $scope.item = {};

    $scope.getItem = function () {
        let id = sessionStorage.getItem("id");
        $http.post("/cache/item", id).then(function (response) {
            $scope.item = response.data;
            $("#img_item").css("background-image", 'url('+$scope.item.urlImg+')');
        }, function (response) {
            console.log(response);
        });
    };
});

resultApp.controller('comments', function ($scope, $http) {

    $scope.getComments = function (page) {
        let id = sessionStorage.getItem("id");
        $http.post("/cache/comments?page="+page, id).then(function (value) {
            $(".column_comments").html("");
            let i = 1;
            value.data.forEach(function (comment) {
                    let selector = $(".column_comments");
                    let piece;
                    if (comment.desc.length > 255) {
                        piece =
                            '<div class="comment">' +
                            '<p>' + comment.author +
                            '<h2>' + comment.title + '</h2>' +
                            '<div id="preview-comment_number_' + i + '">' +
                            comment.desc.substring(0, 255) + '...' + '<br>' +
                            '<div class="centerW" style="width: 100px"><button class="view" onclick="openComment(' + i + ')">Открыть</button></div>' + '<br>' +
                            '</div>' +
                            '<div id="full-comment_number_' + i + '" style="display: none">' +
                            comment.desc + '<br>' +
                            '<div class="centerW" style="width: 100px"><button class="view" onclick="hideComment(' + i + ')">Закрыть</button></div>' + '<br>' +
                            '</div>' +
                            comment.date + '<br>' +
                            comment.site + '</p>' +
                            '</div>' + "<hr color='#949799'/><br>";
                    } else {
                        piece =
                            '<div class="comment">' +
                            '<p>' + comment.author +
                            '<h2>' + comment.title + '</h2>' +
                            comment.desc + '<br>' + '<br>' +
                            comment.date + '<br>' +
                            comment.site + '</p>' +
                            '</div>' + "<hr color='#949799'/><br>";
                    }

                    selector.append(piece);
                    i++;
            })
        }, function (reason) {
            console.log(reason);
        });
    };
});

resultApp.controller('back', function ($scope, $http) {
    $scope.toBack = function () {
        $http.delete("/cache/delete").then(function (value) {
            if (value.data)
                document.location.href = "search";
        }, function (reason) {
            console.log(reason);
        });
    }
});