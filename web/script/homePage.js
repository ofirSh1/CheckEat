$(function () {
    $('.carousel').carousel({interval: 5000});
    sessionStorage.clear();
    getLastUploadsFromServer();
    getFavDishesFromServer();
    getRestaurantsNearbyFromServer();
});

function getLastUploadsFromServer() {
    $.ajax({
        type: 'get',
        url: 'dish',
        data: {
            'requestType': 'getDishesOrderedByUploadDate'
        },
        success: function(dishes)
        {
            if(dishes && dishes.length > 0)
                loadLastUploads(dishes);
        }
    });
}

function getFavDishesFromServer() {
    $.ajax({
        type: 'get',
        url: 'dish',
        data: {
            'requestType': 'getDishesOrderedByLikes'
        },
        success: function(dishes)
        {
            if(dishes && dishes.length > 0)
                loadFavDishes(dishes);
        }
    });
}

function getRestaurantsNearbyFromServer() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(setNearbyRestaurantsHomePage);
    }
}

function setNearbyRestaurantsHomePage(position) {
    var currCity = findCurrCity(position);
    $('#currCity').text(currCity);
    findCitiesNearby(position);
}

function findCitiesNearby(position) {
    var latitude = position.coords.latitude;
    var longitude = position.coords.longitude;
    var request = new XMLHttpRequest();
    var method = 'GET';
    var async = false;
    var urlApi;
    var restaurantsNearby = [];

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
    loadRestaurantsNearby(restaurantsNearby); // list of GsonRestaurants
    sessionStorage.setItem("restNearby", JSON.stringify(restaurantsNearby));
}

function findCurrCity(position) {
    var latitude = position.coords.latitude;
    var longitude = position.coords.longitude;
    var request = new XMLHttpRequest();
    var method = 'GET';
    var url = 'http://maps.googleapis.com/maps/api/geocode/json?latlng=' + latitude + ',' + longitude + '&sensor=true';
    var async = false;
    var currCity;
    request.open(method, url, async);
    request.onreadystatechange = function () {
        if (request.readyState == 4 && request.status == 200) {
            var data = JSON.parse(request.responseText);
            var address = data.results[0];
            var formatted_address = address.formatted_address.replace(/\s*,\s*/g, ",");
            currCity = formatted_address.split(',').slice(1, 2);
        }
    };
    request.send();
    return currCity;
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

function loadLastUploads(dishList) {
    var lastUploads = document.getElementById('lastUploads');
    var size = dishList.length;
    var i = 0;
        for(var j = 0; j < 3 && i < size &&  i < 6; j++) {
            var col = document.createElement('div');
            col.classList.add('col-md-4');
            loadDish(col, dishList[i]);
            lastUploads.appendChild(col);
            i++;
        }
}

function loadFavDishes(dishList) {
    var favDishes = document.getElementById('favDishes');
    var size = dishList.length;
    var i = 0;
        for(var j = 0; j < 3 && i < size &&  i < 6; j++) {
            var col = document.createElement('div');
            col.classList.add('col-md-4');
            loadDish(col, dishList[i]);
            favDishes.appendChild(col);
            i++;
        }
}

function loadRestaurantsNearby(restList) {
    $('#restaurants').show();
    var restaurants = document.getElementById('restaurants');
    var size = restList.length;
    var i = 0;
        for(var j = 0; j < 3 && i < size &&  i < 6; j++) {
            var col = document.createElement('div');
            col.classList.add('col-md-4');
            loadRest(col, restList[i]);
            restaurants.appendChild(col);
            i++;
        }
}

function loadDish(element, dish) {
    var panel = document.createElement('div');
    panel.classList.add('panel', 'panel-default');
    element.appendChild(panel);

    var header = document.createElement('div');
    header.classList.add('panel-heading');
    panel.appendChild(header);
    var dishName = document.createElement('h4');
    dishName.textContent = dish.dishName;
    header.appendChild(dishName);

    var body = document.createElement('div');
    body.classList.add('panel-body', 'dishSizeHome');
    if(addImg(body, dish.dishUrl, '100px', '200px'))
        addNewLine(body);
    if(addDetail(body, 'מסעדה:', dish.restaurantName))
        addNewLine(body);
    if(addAddress(body, dish.restaurantCity, dish.restaurantStreet, dish.restaurantStreetNum))
        addNewLine(body);
    if(addDetail(body, 'הועלה בתאריך:', dish.dateStr))
        addNewLine(body);
    var btn = addButton(body, 'הצג');
    btn.classList.add('alignLeft');
    btn.onclick = function () { showDish(dish.id, dish.restUsername); };
    panel.appendChild(body);
}

function loadRest(element, rest) {
    var panel = document.createElement('div');
    panel.classList.add('panel', 'panel-default');
    element.appendChild(panel);

    var header = document.createElement('div');
    header.classList.add('panel-heading');
    panel.appendChild(header);
    var restName = document.createElement('h4');
    restName.textContent = rest.restaurantName;
    header.appendChild(restName);

    var body = document.createElement('div');
    body.classList.add('panel-body','restSizeHome');
    if(addImg(body, rest.logoUrl, '100px', '200px'))
        addNewLine(body);
    if(addAddress(body, rest.city, rest.street, rest.streetNum))
        addNewLine(body);
    if(addLink(body, 'אתר המסעדה:', rest.link))
        addNewLine(body);
    var btn = addButton(body, 'הצג');
    btn.classList.add('alignLeft');
    btn.onclick = function () { showRest(rest.userName); };
    panel.appendChild(body);
}

//*******************************************************************************************
//Adding Controls
function addImg(element, url, height, width) {
    if(!url)
        url = "resources/logo.png";

    var added = false;
    if(url) {
        added = true;
        var img = document.createElement('img');
        img.setAttribute('src', url);
        img.setAttribute('height', height);
        img.setAttribute('width', width);
        element.appendChild(img);
    }
    return added;
}

function addDetail(element, detail, text) {
    var added = false;
    if(text) {
        added = true;
        var label = addLabel(element, detail);
        label.classList.add('detailStyle');
        addSpace(element);
        addLabel(element, text);
    }
    return added;
}

function addAddress(element, city, street, streetNum) {
    var added = false;
    if(city) {
        added = true;
        var label = addLabel(element, 'כתובת:');
        label.classList.add('detailStyle');
        addSpace(element);
        addLabel(element, city);
        if(street) {
            addLabel(element, ',');
            addSpace(element);
            addLabel(element, street);
            if(streetNum) {
                addSpace(element);
                addLabel(element, streetNum);
            }
        }
    }
    return added;
}

function addLabel(element, text) {
    var label = document.createElement('label');
    label.innerHTML = text;
    element.appendChild(label);
    return label;
}

function addLink(element, text, link) {
    var added = false;
    if(link) {
        added = true;
        var label = addLabel(element, text);
        label.classList.add('detailStyle');
        addSpace(element);
        var a = document.createElement('a');
        a.href = link;
        a.innerHTML = "לחץ כאן";
        element.appendChild(a);
    }
    return added;
}

function addButton(element, text) {
    var button = document.createElement('button');
    button.innerHTML = text;
    button.setAttribute('class', 'btn btn-default');
    element.appendChild(button);
    return button;
}

function addNewLine(element) {
    var br = document.createElement('br');
    element.appendChild(br);
}

function addSpace(element) {
    addLabel(element, "&nbsp;");
}

//*******************************************************************************************
//Redirect
function showMoreLastUploads() {
    sessionStorage.setItem("title", "העלאות אחרונות");
    window.location.href = "showMore.html";
}

function showMoreFavDishes() {
    sessionStorage.setItem("title", "מנות מועדפות");
    window.location.href = "showMore.html";
}

function showMoreNearby() {
    sessionStorage.setItem("title", "מסעדות בקרבת מקום");
    //sessionStorage.setItem("currCity", document.getElementById('currCity').innerHTML);
    window.location.href = "showMore.html";
}

function showRest(restUsername) {
    sessionStorage.setItem("restUsername", restUsername);
    sessionStorage.setItem("dishId", "");
    window.location.href = "restaurant.html";
}

function showDish(dishId,restUsername) { // TODO
    sessionStorage.setItem("restUsername", restUsername);
    sessionStorage.setItem("dishId", dishId);
    window.location.href = "restaurant.html";
}