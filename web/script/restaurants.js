$(function () {
    var dishes = JSON.parse(sessionStorage.getItem("dishes"));
    setRestaurantsList(dishes);
});

function setRestaurantsList(dishes) {
    var restList = document.getElementById('restList');
    var restaurants = [];

    for (var i = 0; i < dishes.length; i++) {
        if(restaurants.indexOf(dishes[i].restUsername) <= -1) {
            restaurants.push(dishes[i].restUsername);

            var rest = document.createElement('article');
            restList.appendChild(rest);

            var header = document.createElement('h1');
            rest.appendChild(header);
            header.classList.add('headerStyle');
            header.innerHTML = dishes[i].restName;
            if(dishes[i].restUrl) {
                var img = document.createElement('img');
                img.setAttribute('src', dishes[i].restUrl);
                img.setAttribute('height', '50px');
                img.setAttribute('width', '50px');
                img.classList.add('alignLeft');
                header.appendChild(img);
            }

            //$("<h1 class='headerStyle'></h1>").text(dishes[i].restName).appendTo(rest);

            //if (addImg(rest, dishes[i].restUrl, '100px', '200px'))
            //    addNewLine(rest);

            if (addAddress(rest, dishes[i].restCity, dishes[i].restStreet, dishes[i].restStreetNum))
                addNewLine(rest);

            if (addLink(rest, 'אתר המסעדה:', dishes[i].restlink))
                addNewLine(rest);

            setShowDishButton(dishes[i], dishes, rest);

            $(rest).addClass('bodyStyle');
        }
    }
}

function  setShowDishButton(dish,dishes,element) {
    var showDishes = addButton(element, 'צפה במנות המתאימות');
    $(showDishes).addClass('btnStyle');
    showDishes.onclick = function () {
        showDishesInThisRestaurants(dish.restUsername, dishes);
    };
}

function showDishesInThisRestaurants(restUsername, dishes) {
    sessionStorage.setItem("restUsername", restUsername);
    window.location.href = "dishes.html";
}

//*******************************************************************************************
//Adding Controls
function addImg(element, url, height, width)
{
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

function addDetail(element, detail, text)
{
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

function addAddress(element, city, street, streetNum)
{
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

function addLabel(element, text)
{
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

function addIcon(element, _class) {
    var i = document.createElement('i');
    i.className = _class;
    i.setAttribute('aria-hidden', 'true');
    element.appendChild(i);
    return i;
}

function addList(element, text, list) {
    var added = false;
    if (!isEmptyList(list)) {
        added = true;
        var label = addLabel(element, text);
        label.classList.add('detailStyle');
        addSpace(element);
        for (var i = 0; i < list.length; i++) {
            addLabel(element, list[i]);
            if(i < list.length - 1) {
                addLabel(element, ',');
                addSpace(element);
            }
        }
    }
    return added;
}

//*******************************************************************************************
//Validation
function isEmptyList(list) {
    var empty = true;
    if(typeof list !== 'undefined' && list.length > 0) {
        for (var i = 0; i < list.length; i++) {
            if(list[i].length !== 0) {
                empty = false;
                break;
            }
        }
    }
    return empty;
}

function isEmptyString(str) {
    var empty = true;
    if(str && str.length !== 0)
        empty = false;
    return empty;
}