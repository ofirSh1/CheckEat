var specialTypesEnum = {vegetarian:'צמחוני', naturalist:'טבעוני', kosher:'כשר', noSugar:'ללא סוכר', noGluten:'ללא גלוטן'};
var restaurantsNearby = [];

$(function () {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(){},function () {
            document.getElementById("restNearByLabel").style.display = 'none';
        });
    }
});

function findDish()
{
    var restName = $('#restName').val();
    var restCity = $("#selectCity option:selected").text();
    var dishName = $('#dishName').val();
    var specialTypes = getSpecialTypes();
    var otherTypes = $('#otherTypes').val().split(',');
    var ingredients = $('#ingredients').val().split(',');
    var restNearBy = false;
    if($('#restNearBy').is(":checked")) {
        restNearBy = true;
        getRestaurantsNearbyFromServer();
    }
    var restJson = JSON.stringify(restaurantsNearby);
    var data = {requestType: 'findDishes',
        restName: restName,
        restCity: restCity,
        dishName: dishName,
        specialTypes: specialTypes,
        otherTypes: otherTypes,
        ingredients: ingredients,
        searchNearBy: restNearBy,
        restaurantsNearby: restJson
    };

    $.ajax({
        type: 'post',
        dataType:'json',
        url: 'dish',
        data: data,
        success: function(dishes)
        {
            sessionStorage.setItem("dishes", JSON.stringify(dishes));
            window.location.href = "restaurants.html";
        }
    });
}

function getSpecialTypes() {
    var specialTypes = [];
    if($('#vegetarian').is(":checked"))
    {
        specialTypes.push(specialTypesEnum.vegetarian);
    }
    if($('#naturalist').is(":checked"))
    {
        specialTypes.push(specialTypesEnum.naturalist);
    }
    if($('#kosher').is(":checked"))
    {
        specialTypes.push(specialTypesEnum.kosher);
    }
    if($('#noSugar').is(":checked"))
    {
        specialTypes.push(specialTypesEnum.noSugar);
    }
    if($('#noGluten').is(":checked"))
    {
        specialTypes.push(specialTypesEnum.noGluten);
    }
    return specialTypes;
}

function getRestaurantsNearbyFromServer() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(setNearbyRestaurantsHomePage);
    }
}

function setNearbyRestaurantsHomePage(position) {
    findCitiesNearby(position);
}

function findCitiesNearby(position) {
    var latitude = position.coords.latitude;
    var longitude = position.coords.longitude;
    var request = new XMLHttpRequest();
    var method = 'GET';
    var async = false;
    var urlApi;

    $.ajax({
        url: 'admin',
        async: false,
        data: {
            'requestType': 'getRestaurants'
        },
        success: function (restaurants) {
            for (var i = 0; i < restaurants.length; i++) { // find each city coords and check the distance
                urlApi = 'http://maps.googleapis.com/maps/api/geocode/json?address=' + restaurants[i].city + '&sensor=true';
                request.open(method, urlApi, async); // find rest coords
                request.onreadystatechange = function(){
                    if(request.readyState == 4 && request.status == 200) {
                        var data = JSON.parse(request.responseText);
                        var coords = data.results[0].geometry.location;
                        var lon = coords.lng;
                        var lat = coords.lat;
                        if (distance(latitude,longitude,lat,lon)<=10)
                            restaurantsNearby.push(restaurants[i]);
                    }
                };
                request.send();
            }
        }
    });
    sessionStorage.setItem("restNearby", JSON.stringify(restaurantsNearby));
}

function distance($lat1, $lng1, $lat2, $lng2) {
    // convert latitude/longitude degrees for both coordinates
    // to radians: radian = degree * π / 180
    $lat1 = deg2rad($lat1);
    $lng1 = deg2rad($lng1);
    $lat2 = deg2rad($lat2);
    $lng2 = deg2rad($lng2);

    // calculate great-circle distance
    $distance = Math.acos(Math.sin($lat1) * Math.sin($lat2) + Math.cos($lat1) * Math.cos($lat2) * Math.cos($lng1 - $lng2));

    // distance in human-readable format:
    // earth's radius in km = ~6371
    return 6371 * $distance;
}

function deg2rad(degree) {
    return degree * Math.PI / 180;
}
