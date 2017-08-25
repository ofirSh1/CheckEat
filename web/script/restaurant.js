var specialTypesEnum = {vegetarian:'צמחוני', naturalist:'טבעוני', kosher:'כשר', noSugar:'ללא סוכר', noGluten:'ללא גלוטן'};
var userType;
var chosenDishId;

$(function () {
    getUserType();
    var restUsername = sessionStorage.getItem("restUsername");
    var filterBtn = document.getElementById("filterBtn");
    filterBtn.onclick = function () { getFilteredRestaurantsDishesFromServer(restUsername); };
    getRestaurantFromServer(restUsername);
    chosenDishId = sessionStorage.getItem("dishId");
    if(chosenDishId) {
        getDishFromServer(chosenDishId);
        document.getElementById("filter").style.display = 'none';
    }
});

function getUserType() {
    $.ajax({
        url: 'login',
        async: false,
        data: {
            'requestType': 'getUserType'
        },
        type: 'get',
        success: function(type){
            userType = type;
        }
    });
}

function getRestaurantFromServer(restUsername) {
    $.ajax({
        url: 'profile',
        data: {
            'requestType': 'getRestaurant',
            'restUserName': restUsername
        },
        success: function(rest)
        {
            if(rest) {
                loadRest(rest, restUsername);
                if(!chosenDishId) {
                    loadDishes(rest.dishes);
                }
            }
        }
    });
}

function getDishFromServer(dishId) {
    $.ajax({
        url: 'profile',
        data: {
            "requestType": 'getDish',
            "dishId": dishId
        },
        success: function(dish)
        {
            if(dish){
                var dishes = [];
                dishes.push(dish);
                loadDishes(dishes);
            }
        }
    });
}

function getFilteredRestaurantsDishesFromServer(restUsername) {
    var dishName = $('#dishName').val();
    var specialTypes = getSpecialTypes();
    var otherTypes = $('#otherTypes').val().split(',');
    var ingredients = $('#ingredients').val().split(',');

    $.ajax({
        type: 'get',
        url: 'dish',
        data: {
            'requestType': 'getFilteredRestaurantDishes',
            'restUsername': restUsername,
            'dishName': dishName,
            specialTypes: specialTypes,
            otherTypes: otherTypes,
            ingredients: ingredients
        },
        success: function(dishes)
        {
            if(dishes && dishes.length > 0)
                loadDishes(dishes);
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

function loadRest(rest, restUsername) {
    var restaurant = document.getElementById('restaurant');
    var col = document.createElement('div');
    col.classList.add('col-md-12');
    restaurant.appendChild(col);

    if(addImg(col, rest.logoUrl, '100px', '200px'))
        addNewLine(col);
    if(addDetail(col, 'שם המסעדה:', rest.restaurantName))
        addNewLine(col);
    if(addAddress(col, rest.city, rest.street, rest.streetNum))
        addNewLine(col);
    if(addLink(col, 'אתר המסעדה:', rest.link))
        addNewLine(col);
    if (chosenDishId) { // if show dish
        var moreDishesInRestaurant = addButton(restaurant, 'הצג מנות נוספות במסעדה');
        moreDishesInRestaurant.onclick = function () {
            sessionStorage.clear();
            sessionStorage.setItem("restUsername", restUsername);
            sessionStorage.setItem("dishId", "");
            window.location.href = "restaurant.html";
        };
    }
}

function loadDishes(dishList) {
    var dishes = document.getElementById('dishes');
    var size = dishList.length;

    dishes.innerHTML = "";
    //TODO if empty write no dishes
    for(var i = 0; i < size; i++) {
        var col = document.createElement('div');
        col.classList.add('col-md-12');
        loadDish(col, dishList[i]);
        dishes.appendChild(col);
    }
}

function loadDish(element, dish) {
    var row = document.createElement('div');
    row.classList.add('row');
    element.appendChild(row);
    var col1 = document.createElement('div');
    col1.classList.add('col-md-6');
    row.appendChild(col1);
    var col2 = document.createElement('div');
    col2.classList.add('col-md-6');
    row.appendChild(col2);

    var panel = document.createElement('div');
    panel.classList.add('panel', 'panel-default');
    col1.appendChild(panel);

    var header = document.createElement('div');
    header.classList.add('panel-heading');
    panel.appendChild(header);
    var dishName = document.createElement('h4');
    dishName.textContent = dish.dishName;
    header.appendChild(dishName);

    var body = document.createElement('div');
    body.classList.add('panel-body', 'dishSizeShow');
    addDishDetails(body, dish);
    addLikes(body, dish);
    addCommentsBtn(body, dish);
    addSaveDishBtn(body, dish);
    panel.appendChild(body);

    addCommentsSection(col2, dish);
}

function addDishDetails(element, dish) {
    if(addImg(element, dish.dishUrl, '100px', '200px'))
        addNewLine(element);
    if(addList(element, 'סוג המנה:', dish.otherTypes))
        addNewLine(element);
    if(addList(element, 'קטגוריות:', dish.specialTypes))
        addNewLine(element);
    if(addList(element, 'מרכיבים:', dish.ingredients))
        addNewLine(element);
    if(addDetail(element, 'הועלה בתאריך:', dish.uploadDate))
        addNewLine(element);
}

function addLikes(element, dish) {
    addNewLine(element);
    var likeDishIcon = addIcon(element, 'fa fa-heart-o');
    likeDishIcon.onclick = function () { likeDislikeDish(this, dish, likesLabel) };
    checkIfLiked(dish, likeDishIcon);
    addSpace(element);

    addLabel(element, 'לייקים:');
    addSpace(element);
    var likesLabel = addLabel(element, dish.numLikes);
    addNewLine(element);
}

function addCommentsBtn(element, dish) {
    var commentsBtn = addButton(element, 'הצג תגובות');
    commentsBtn.onclick = function () {
        toggleCommentsSection(dish.id);
        toggleText(this, 'הסתר תגובות', 'הצג תגובות');
    };
    addSpace(element);
}

function addSaveDishBtn(element, dish) {
    if(userType == null || userType === "customer") {
        var saveDishBtn = addButton(element, 'הוסף מנה למועדפים');
        saveDishBtn.onclick = function () { saveDish(this, dish) };
        checkIfSaved(dish, saveDishBtn);
    }
}

function addCommentsSection(element, dish) {
    var container = document.createElement('div');
    container.classList.add('commentsSection');
    container.id = 'commentsSection' + dish.id;
    container.style.display = 'none';
    element.appendChild(container);

    var header = addLabel(container, 'תגובות');
    header.classList.add('commentsHeaderLabel');
    addNewLine(container);

    var commentsList = document.createElement('div');
    commentsList.classList.add('commentsList');
    commentsList.id = 'commentsList' + dish.id;
    container.appendChild(commentsList);
    addNewLine(container);

    var commentBtn = addButton(container, 'הוסף תגובה');
    commentBtn.classList.add('commentBtn');
    commentBtn.onclick = function () { comment(dish.id, textBox); };
    container.appendChild(commentBtn);
    addSpace(container);

    var textBox = addTextArea(container);
}
//*******************************************************************************************
//LIKE ACTIONS
function checkIfLiked(dish, likeDishIcon) {
    $.ajax({
        url: 'profile',
        data: {
            "requestType": 'checkIfLiked',
            "dishId": dish.id
        },
        success: function (liked) {
            if (liked) {
                changeIcon(likeDishIcon, 'fa fa-heart', 'fa fa-heart-o');
            }
        }
    });
}

function likeDislikeDish(icon, dish, likesLabel){
    if(userType) {
        changeIcon(icon, 'fa fa-heart', 'fa fa-heart-o');
        changeLikes(icon, likesLabel);

        $.ajax({
            url: 'dish',
            data: {
                'requestType': 'likeDislikeDish',
                'dishId': dish.id
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
//COMMENT ACTIONS
function toggleCommentsSection(dishId) {
    var x = document.getElementById('commentsSection' + dishId);
    if (x.style.display === 'none') {
        setCommentsList(dishId);
        x.style.display = 'block';
    } else {
        x.style.display = 'none';
    }
}

function comment(dishId, textBox) {
    var content = textBox.value;
    if(userType != null) {
        if (content === "")
            alert("אין תוכן");
        else {
            $.ajax({
                url: 'dish',
                data: {
                    'requestType': 'addComment',
                    'content': content,
                    'dishId': dishId
                },
                success: function (res) {
                    if(res === 'true') {
                        setCommentsList(dishId);
                        textBox.value = "";
                    }
                }
            });
        }
    }
    else {
        alert('יש להתחבר קודם');
        window.location.href = "login.html";
    }
}

function setCommentsList(dishId) {
    $.ajax({
        url: 'dish',
        type: 'get',
        data: {
            'requestType': 'getDishComments',
            'dishId': dishId
        },
        success: function(comments)
        {
            var element = document.getElementById('commentsList' + dishId);
            loadComments(element, comments);
        }
    });
}

function loadComments(element, comments) {
    element.innerHTML = "";
    if(comments == null || comments.length == 0){
        var label = addLabel(element, 'אין תגובות');
        label.classList.add('noCommentsLabel');
    }

    for(var i = 0; i < comments.length; i++){
        var container = document.createElement('div');
        container.classList.add('comment');
        element.appendChild(container);

        var userName = addLabel(container, comments[i].userName);
        userName.classList.add('userName');
        addSpace(container);
        var date = addLabel(container, timeSince(comments[i].date));
        date.classList.add('date');
        addNewLine(container);

        var text = document.createElement('div');
        text.classList.add('commentContent');
        text.innerHTML = comments[i].content;
        container.appendChild(text);
    }
}

function removeComment() {

}

//*******************************************************************************************
//SAVE DISH ACTIONS
function checkIfSaved(dish, saveDishBtn) {
    $.ajax({
        url: 'profile',
        data: {
            "requestType": 'checkIfSaved',
            "dishId": dish.id
        },
        success: function (res) {
            if(res) {
                toggleText(saveDishBtn, 'הוסף מנה למועדפים', 'הסר מנה מהמועדפים');
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
                'dishId': dish.id
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

function addTextArea(element) {
    var input = document.createElement('textarea');
    input.rows = 2;
    input.cols = 30;
    input.classList.add('commentTextBox');
    element.appendChild(input);
    return input;
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