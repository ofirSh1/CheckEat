var userType;

$(function () {
    var dishes = JSON.parse(sessionStorage.getItem("dishes"));
    var restUsername = sessionStorage.getItem("restUsername");
    setDishesList(dishes, restUsername);
    getUserType();
});

function getUserType() {
    $.ajax({
        url: 'login',
        data: {
            'requestType': 'getUserType'
        },
        type: 'get',
        success: function(type){
            userType = type;
        }
    });
}

function checkUploader(userName, element) {
 /*   $.ajax({
        url: 'login',
        data: {
            'requestType': 'getGivenUserType',
            'userName': userName
        },
        type: 'get',
        success: function(userType){
            if(userType && userType === 'restaurant') {
                var vIcon = addIcon(element, 'fa fa-check-circle');
                vIcon.title = 'הועלה ע"י המסעדה';
            }
        }
    });*/

    $.ajax({
        url: 'profile',
        async: false,
        data: {
            'requestType': 'checkIfCustomerOrRestaurant',
            'userName': userName
        },
        success: function(isRest){
            if (isRest.length!=0) {
                if (isRest === "true") {
                    var vIcon = addIcon(element, 'fa fa-check-circle');
                    vIcon.title = 'הועלה ע"י המסעדה';
                }
            }
            else {
                var vIcon = addIcon(element, 'fa fa-check-circle');
                vIcon.title = 'הועלה ע"י CheckEat';
            }
        }
    });
}

function setDishesList(dishes, restUsername) {
    var dishesList = document.getElementById('dishList');

    for(var i = 0; i < dishes.length; i++) {
        if(dishes[i].restUsername == restUsername) {
            var dish = document.createElement('article');
            dish.classList.add('fixDishesSize');
            dishesList.appendChild(dish);
            var dishName = document.createElement('h1');
            dishName.innerHTML = dishes[i].dishName;
            dishName.classList.add('headerStyle');
            dish.appendChild(dishName);
            checkUploader(dishes[i].addByUserName, dishName);
            if (addImg(dish, dishes[i].dishUrl, '100px', '200px'))
                addNewLine(dish);
            addDishDetails(dishes[i], dish);
            addDishActions(dishes[i], dish);
            $(dish).addClass('bodyStyle');
        }
    }
}

//*******************************************************************************************
//Dish Details
function addDishDetails(dish, details) {
    if(addList(details, 'סוג המנה:', dish.otherTypes))
        addNewLine(details);

    if(addList(details, 'קטגוריות:', dish.specialTypes))
        addNewLine(details);

    if(addList(details, 'מרכיבים:', dish.ingredients))
        addNewLine(details);

    if(addDetail(details, 'הועלה בתאריך:', dish.uploadDate))
        addNewLine(details);

    if(addDetail(details, 'מסעדה:', dish.restName))
        addNewLine(details);

    if(addAddress(details, dish.restCity, dish.restStree, dish.restStreetNum))
        addNewLine(details);

    if(addLink(details, 'קישור לאתר המסעדה:', dish.restLink))
        addNewLine(details);
}

//*******************************************************************************************
//Dish Actions
function addDishActions(dish, actions) {
    addNewLine(actions);
    var likeDishIcon = addIcon(actions, 'fa fa-heart-o');
    likeDishIcon.onclick = function () { likeDish(this, dish, likesLabel) };
    checkIfLiked(dish, likeDishIcon);
    addSpace(actions);

    addLabel(actions, 'לייקים:');
    addSpace(actions);
    var likesLabel = addLabel(actions, dish.numLikes);
    addNewLine(actions);

    var commentsBtn = addButton(actions, 'הצג תגובות');
    commentsBtn.onclick = function () { buildCommentsWindow(dish.dishId, dish.comments) };
    addSpace(actions);

    var saveDishBtn = addButton(actions, 'הוסף מנה למועדפים');
    saveDishBtn.onclick = function () { saveDish(this, dish) };
    checkIfSaved(dish, saveDishBtn);
}

//*******************************************************************************************
//Like Dish
function checkIfLiked(dish, likeDishIcon) {
    $.ajax({
        url: 'dish',
        data: {
            "requestType": 'checkIfLiked',
            "dishId": dish.dishId
        },
        success: function (res) {
            if (res.length != 0) { // not restaurant or no user
                if (res === 'true') { // the user already liked this dish {
                    alert(res);
                    changeIcon(likeDishIcon, 'fa fa-heart', 'fa fa-heart-o');
                }
            }
        }
    });
}

function likeDish(icon, dish, likesLabel){
    if(userType) {
        changeIcon(icon, 'fa fa-heart', 'fa fa-heart-o');
        changeLikes(icon, likesLabel);

        $.ajax({
            url: 'profile',
            data: {
                'requestType': 'like',
                'dishId': dish.dishId
            }
        });
    }
    else {
        alert('יש להתחבר קודם');
        window.location.href = "login.html";
    }
}

function changeIcon(icon, class1, class2) {
    if(icon.className === class1) {
        icon.className -= class1;
        icon.className = class2;
    }
    else {
        icon.className -= class2;
        icon.className = class1;
    }
}

function changeLikes(icon, likesLabel) {
    var newVal = parseInt(likesLabel.innerHTML);
    if(icon.className === 'fa fa-heart')
        newVal = newVal + 1;
    else
        newVal = newVal - 1;
    likesLabel.innerHTML = newVal;
}

//*******************************************************************************************
//Save Dish
function checkIfSaved(dish, saveDishBtn) {
    $.ajax({
        url: 'dish',
        data: {
            "requestType": 'checkIfSaved',
            "dishId": dish.dishId
        },
        success: function (res) {
            if (res.length != 0) { // signed user
                if (res === true) { // the user already saved this dish
                    toggleText(saveDishBtn, 'הוסף מנה למועדפים', 'הסר מנה מהמועדפים');
                }
            }
        }
    });
}

function saveDish(btn, dish) {
    if(userType != null) {
        toggleText(btn, 'הוסף מנה למועדפים', 'הסר מנה מהמועדפים');
        $.ajax({
            url: 'profile',
            data: {
                'requestType': 'favorites',
                'dishId': dish.dishId
            }
        });
    }
    else {
        alert('יש להתחבר קודם');
        window.location.href = "login.html";
    }
}

function toggleText(element, text1, text2) {
    if (element.innerHTML == text1) {
        element.innerHTML = text2;
    }
    else {
        element.innerHTML = text1;
    }
}

//*******************************************************************************************
//Comments
function buildCommentsWindow(dishId, comments) {
    var boardWin = window.open("", "Comments", "width=400,height=400");
    var link = document.createElement("link");
    link.media = "screen,print";

    var addCommentButton = boardWin.document.createElement('button');
    addCommentButton.innerHTML = "הוסף תגובה";
    addCommentButton.onclick = function () { addCommentToDish(dishId, inputComment.value) };
    boardWin.document.body.appendChild(addCommentButton);

    var ul = boardWin.document.createElement('ul');
    $.each(comments, function(outerKey, outerValue){
        var li = boardWin.document.createElement('li');
        addLabel(outerKey + " : " + outerValue, li);
        ul.appendChild(li);
    });
    boardWin.document.body.appendChild(ul);
    var inputComment = boardWin.document.createElement('input');
    inputComment.setAttribute("type", "text");
    boardWin.document.body.appendChild(inputComment);
}

function addCommentToDish(dishId, text, ul) {
    if(userType != null) {
        if (text === "")
            alert("אין תוכן");
        else {
            $.ajax({
                url: 'profile',
                data: {
                    'requestType': 'comments',
                    'comment': text,
                    'dishId': dishId
                },
                success: function (response) {
                    addCommentToList(text, ul);
                }
            });
        }
    }
    else {
        alert('יש להתחבר קודם');
    }

}

function addCommentToList(text, ul) {
    var li = boardWin.document.createElement('li');
    addLabel(" : " + text, li);
    ul.appendChild(li);
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