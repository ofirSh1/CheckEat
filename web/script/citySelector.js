$(document).ready(function() {
    $(".js-example-basic-single").select2();
});

$(function () {
    getCitiesFromServer();
});

function getCitiesFromServer() {
    $.ajax({
        type: 'get',
        url: 'dish',
        async: false,
        data: {
            'requestType': 'loadCities'
        },
        success: function (cities) {
            var citySelect = document.getElementById("selectCity");
            var myOption;
            myOption = document.createElement("option");
            myOption.text = "";
            citySelect.add(myOption);
            for (var i = 0; i < cities.length; i++) {
                myOption = document.createElement("option");
                myOption.text = cities[i].name;
                citySelect.add(myOption);
            }
        }
    });
}