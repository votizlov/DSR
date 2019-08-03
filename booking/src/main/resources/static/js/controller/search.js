var searchApp=angular.module("searchApp", []);

searchApp.controller("search_menu", function($scope, $http) {
    $scope.type = "BOOK";

    $scope.selectType = function (type) {
        if (type === $scope.type) return;
        switch (type) {
            case "BOOK" : {
                $("#check-movie, #check-game").attr('class', 'checkbox_none');
                $("#check-book").attr('class', 'checkbox_selected');
                $("#first-name").attr('placeholder', '  Название книги');
                $("#last-name").attr('placeholder', '  Имя автора');
                break
            }
            case "MOVIE" : {
                $("#check-book, #check-game").attr('class', 'checkbox_none');
                $("#check-movie").attr('class', 'checkbox_selected');
                $("#first-name").attr('placeholder', '  Название фильма');
                $("#last-name").attr('placeholder', '  Имя режиссера');
                break
            }
            case "GAME" : {
                $("#check-movie, #check-book").attr('class', 'checkbox_none');
                $("#check-game").attr('class', 'checkbox_selected');
                $("#first-name").attr('placeholder', '  Название игры');
                $("#last-name").attr('placeholder', '  Название компании');
                break
            }
            default : {
                console.log("warning switch type search! Now type is \'BOOK\'");
                $scope.type = "BOOK";
                return
            }
        }
        $scope.type = type;
    };

    $scope.i = 0;
    $scope.saveItemID = function () {
        let itemID = {};
        itemID.firstName = $("#first-name").val();
        itemID.lastName = $("#last-name").val();
        if (itemID.firstName === "") return;
        itemID.type = $scope.type;
        let loadTime;
        $http.post("/result-init", itemID).then(function (response) {
            if (response.data) {
                console.log("init is complied!");
                document.location.href = "result";
            } else {
                console.log("init isn't complied!");
                console.log(response);
                return;
            }
        }, function (reason) {
            console.log(reason);
            clearTimeout(loadTime);
        });
        loadTime = setInterval(function() {
            let suf = '';
            for (let j = 0; j < $scope.i % 3; j++) {
                suf+='.';
            }
            $scope.i++;
            $("#toResult").attr("value", "Загрузка"+suf);
            console.log($scope.i);
        }, 1000);
    };
});