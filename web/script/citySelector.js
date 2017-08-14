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

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(locationSuccess);
    }
}

function locationSuccess(position) {
    var latitude = position.coords.latitude;
    var longitude = position.coords.longitude;
    var request = new XMLHttpRequest();
    var method = 'GET';
    var url = 'http://maps.googleapis.com/maps/api/geocode/json?latlng=' + latitude + ',' + longitude + '&sensor=true';
    var async = true;

    request.open(method, url, async);
    request.onreadystatechange = function(){
        if(request.readyState == 4 && request.status == 200){
            var data = JSON.parse(request.responseText);
            var address = data.results[0];
            var formatted_address = address.formatted_address.replace(/\s*,\s*/g, ",");
            var currCity = formatted_address.split(',').slice(1,2);

            var option = $('#selectCity option').filter(function() { return $(this).html() == currCity;});
            option.attr('selected', true);
            //TODO
        }
    };
    request.send();
}