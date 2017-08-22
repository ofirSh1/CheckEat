$(function () {
    var title = sessionStorage.getItem("title");
    var header = document.getElementById('header');
    header.innerHTML = title;

    if(title === "העלאות אחרונות")
        getLastUploadsFromServer();
    else if(title === "מנות מועדפות")
        getFavDishesFromServer();
    else if(title === "מסעדות בקרבת מקום")
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
    var restaurants = JSON.parse(sessionStorage.getItem('restNearby'));
    if(restaurants && restaurants.length > 0)
        loadRestaurantsNearby(restaurants);
}

function loadLastUploads(dishList) {
    var dishes = document.getElementById('list');
    var size = dishList.length;
    for(var i = 0; i < size; i++) {
        var col = document.createElement('div');
        col.classList.add('col-md-4');
        loadDish(col, dishList[i]);
        dishes.appendChild(col);
    }
}

function loadFavDishes(dishList) {
    var favDishes = document.getElementById('list');
    var size = dishList.length;
    for(var i = 0; i < size; i++) {
        var col = document.createElement('div');
        col.classList.add('col-md-4');
        loadDish(col, dishList[i]);
        favDishes.appendChild(col);
    }
}

function loadRestaurantsNearby(restList) {
    $('#restaurants').show();
    var restaurants = document.getElementById('list');
    var size = restList.length;
    for(var i = 0; i < size; i++) {
        var col = document.createElement('div');
        col.classList.add('col-md-4');
        loadRest(col, restList[i]);
        restaurants.appendChild(col);
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
    if(addDetail(body, 'הועלה בתאריך:', dish.uploadDate))
        addNewLine(body);
    var btn = addButton(body, 'הצג');
    btn.classList.add('alignLeft');
    btn.onclick = function () { showDish(dish.restUsername, dish.id); };
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
    body.classList.add('panel-body', 'restSizeHome');
    if(addImg(body, rest.logoUrl, '100px', '200px'))
        addNewLine(body);
    if(addDetail(body, 'כתובת:', rest.city + ', ' + rest.street + ' ' + rest.streetNum))
        addNewLine(body);
    if(addLink(body, 'אתר המסעדה:', rest.link))
        addNewLine(body);
    var btn = addButton(body, 'הצג');
    btn.classList.add('alignLeft');
    btn.onclick = function () { showRest(rest.restUsername); }
    panel.appendChild(body);
}

//*******************************************************************************************
//Adding Controls
function addImg(element, url, height, width) {
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
function showRest(restUsername)
{
    sessionStorage.setItem("restUsername", restUsername);
    sessionStorage.setItem("dishId", "");
    window.location.href = "restaurant.html";
}

function showDish(restUsername, dishId)
{
   // sessionStorage.setItem("restUsername", restUsername);
    sessionStorage.setItem("dishId", dishId);
    window.location.href = "restaurant.html";
}