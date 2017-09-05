var userType;

$(function () {
    getUserType();
    var dishes = JSON.parse(sessionStorage.getItem("dishes"));
    var restUsername = sessionStorage.getItem("restUsername");
    setDishesList(dishes, restUsername);
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

function checkUploader(userName, element) {
    $.ajax({
        url: 'profile',
        async: false,
        data: {
            'requestType': 'checkUploader',
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
    if (dishes.length == 0){
        var noDishes = document.createElement('h3');
        noDishes.innerHTML = "לא נמצאו מנות מתאימות";
        dishesList.appendChild(noDishes);
    }
    else {
        for (var i = 0; i < dishes.length; i++) {
            if (dishes[i].restUsername == restUsername) {
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

    if(addDetail(details, 'הועלה בתאריך:', dish.dateStr))
        addNewLine(details);

    if(addDetail(details, 'מסעדה:', dish.restaurantName))
        addNewLine(details);

    if(addAddress(details, dish.restaurantCity, dish.restaurantStreet, dish.restaurantStreetNum))
        addNewLine(details);

    if(addLink(details, 'קישור לאתר המסעדה:', dish.restLink))
        addNewLine(details);
}

//*******************************************************************************************
//Dish Actions
function addDishActions(dish, actions) {
    addNewLine(actions);
    var likeDishIcon = addIcon(actions, 'fa fa-heart-o');
    likeDishIcon.onclick = function () { likeDislikeDish(this, dish, likesLabel) };
    checkIfLiked(dish, likeDishIcon);
    addSpace(actions);

    addLabel(actions, 'לייקים:');
    addSpace(actions);
    var likesLabel = addLabel(actions, dish.numLikes);
    addNewLine(actions);

    var commentsBtn = addButton(actions, 'הצג תגובות');
    commentsBtn.onclick = function () {
        toggleCommentsSection(dish.id);
        toggleText(this, 'הסתר תגובות', 'הצג תגובות');
    };
    addSpace(actions);

    if(!userType || userType === "customer") {
        var saveDishBtn = addButton(actions, 'הוסף מנה למועדפים');
        saveDishBtn.onclick = function () {
            saveDish(this, dish)
        };
        checkIfSaved(dish, saveDishBtn);
    }

    addNewLine(actions);

    addCommentsSection(actions, dish);
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
//Like Actions
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
//Comment Actions
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
    if(userType) {
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
            loadComments(element, comments, dishId);
        }
    });
}

function loadComments(element, comments, dishId) {
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
        addSpace(date);
        canDeleteComment(comments[i].commentId, dishId, date);
        addNewLine(container);

        var text = document.createElement('div');
        text.classList.add('commentContent');
        text.innerHTML = comments[i].content;
        container.appendChild(text);
    }
}

function canDeleteComment(commentId, dishId, element) {
    $.ajax({
        url: 'dish',
        data: {
            'requestType': 'canDeleteComment',
            'commentId': commentId
        },
        success: function (res) {
            if(res === 'true') {
                var icon = addIcon(element, 'fa fa-trash-o');
                icon.onclick = function () { deleteComment(commentId, dishId); }
            }
        }
    });
}

function deleteComment(commentId, dishId) {
    $.ajax({
        url: 'dish',
        data: {
            'requestType': 'deleteComment',
            'dishId': dishId,
            'commentId': commentId
        },
        success: function (res) {
            if(res === 'true')
                setCommentsList(dishId);
        }
    });
}

//*******************************************************************************************
//Save Dish Actions
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
    if(userType) {
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

function addTextArea(element) {
    var input = document.createElement('textarea');
    input.rows = 2;
    input.cols = 15;
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