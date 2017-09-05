var specialTypesEnum = {vegetarian:'צמחוני', naturalist:'טבעוני', kosher:'כשר', noSugar:'ללא סוכר', noGluten:'ללא גלוטן'};
var restaurantsNearby = [];
var restNearBy = false;

$(function () {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(findCitiesNearby,
            function () {
                document.getElementById("restNearByLabel").style.display = 'none';
            },
            {maximumAge: 50000, timeout: 20000, enableHighAccuracy: true});
    }
    get3LatestCommentsFromServlet();
});

function findDish() {
    var restName = $('#restName').val();
    var restCity = $("#selectCity option:selected").text();
    var dishName = $('#dishName').val();
    var specialTypes = getSpecialTypes();
    var otherTypes = $('#otherTypes').val();
    if (otherTypes.slice(-2,-1) == ',')
        otherTypes = otherTypes.slice(0,-2);
    otherTypes = otherTypes.split(',');
    var ingredients = $('#ingredients').val();
    if (ingredients.slice(-2,-1) == ',')
        ingredients = ingredients.slice(0,-2);
    ingredients = ingredients.split(',');
    if($('#restNearBy').is(":checked")) {
        restNearBy = true;
        getRestaurantsNearbyFromServer();
    }
    searchDishes(restName,restCity,dishName,specialTypes,otherTypes,ingredients);
}

function searchDishes(restName,restCity,dishName,specialTypes,otherTypes,ingredients) {
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
            navigator.geolocation.getCurrentPosition(findCitiesNearby);
    }
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
                urlApi = 'https://maps.googleapis.com/maps/api/geocode/json?address=' + restaurants[i].city + '&sensor=true';
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

function get3LatestCommentsFromServlet() {
    $.ajax({
        type: 'get',
        url: 'dish',
        data: {
            'requestType': 'getLatestComments'
        },
        success: function(comments)
        {
            for (var i = 0; i < 3 && i<comments.length; i++)
            {
                var comment = '#comment' + (i+1);
                var name = '#name' + (i+1);
                var date = '#date' + (i+1);
                $(comment).text(comments[i].content);
                $(name).text(comments[i].userName);
                $(date).text(timeSince(comments[i].date));
            }
        }
    });
}

//*******************************************************************************************
//Calculate Time Ago
function timeSince(mili) {
    var date = new Date(mili);
    var seconds = Math.floor((new Date() - date) / 1000);
    var interval = Math.floor(seconds / 31536000);
    if (interval > 1) {
        return "לפני " + interval + " שנים";
    }
    interval = Math.floor(seconds / 2592000);
    if (interval > 1) {
        return "לפני " + interval + " חודשים";
    }
    interval = Math.floor(seconds / 86400);
    if (interval > 1) {
        return "לפני " + interval + " ימים";
    }
    interval = Math.floor(seconds / 3600);
    if (interval > 1) {
        return "לפני " + interval + " שעות";
    }
    interval = Math.floor(seconds / 60);
    if (interval > 1) {
        return "לפני " + interval + " דקות";
    }
    return Math.floor(seconds) + " שניות";
}
