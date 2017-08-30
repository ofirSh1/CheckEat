var editBy=null;

function buildCustomerProfile() {
    $.ajax({
        url: 'profile',
        data: {
            'requestType': 'getCustomer'
        },
        success: function (user) {
            $('#username').html("&nbsp;"+user.userName);
            $('#email').html("&nbsp;"+user.email);
            $('#phone').html("&nbsp;"+user.phone);
            buildDishes(user.addedDishes);
            buildFavoritesDishes(user.favoritesDishes);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert('Error: ' + textStatus + ' ' + errorThrown);
        }
    });
}

function buildRestaurantProfile() {
    $.ajax({
        url: 'profile',
        data: {
            'requestType': 'getRestaurant'
        },
        success: function (user) {
            $('#username').html("&nbsp;"+user.userName);
            $('#restaurantName').html("&nbsp;"+user.restaurantName);
            $('#email').html("&nbsp;"+user.email);
            $('#phone').html("&nbsp;"+user.phone);
            $('#location').html("&nbsp;"+user.city+ "&nbsp;"+user.street+"&nbsp;"+user.streetNum);
            $('#link').html("&nbsp;"+user.link);
            $('#contactName').html("&nbsp;"+user.contactName);
            $('#contactPhone').html("&nbsp;"+user.contactPhone);
            var image = document.getElementById('image');
            addImg(image,user.logoUrl,"200px","200px");
            buildDishes(user.dishes);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert('Error: ' + textStatus + ' ' + errorThrown);
        }
    });
}

function buildDishes(dishes) {
    $("#addedDishes").empty();
    for(var i = 0; i < dishes.length; i++) {
        var edit = "<button class=\"btn btn-default\" onclick=\"edit(" + dishes[i].id + ")\">עריכה</button>";
        var remove = "<button class=\"btn btn-default\" onclick=\"deleteDish(" + dishes[i].id + ")\">מחק מנה</button>";
        var duplicate = "<button class=\"btn btn-default\" onclick=\"duplicateDish(" + dishes[i].id + ")\">שכפל</button>";
        if (dishes[i].dishUrl != "") {
            var img = "<img src=\"" + dishes[i].dishUrl + "\" height=\"100px\" width=\"100px\" </img>";
            $('<tr><td>' + Number(Number(i) + Number(1)) + '</td><td>' + dishes[i].dishName + '</td><td>' + dishes[i].specialTypes + '</td><td>' + dishes[i].ingredients + '</td><td>' + dishes[i].restaurantName + '</td>' +
                '<td>' + dishes[i].restaurantCity + '</td><td>' + img + '</td><td>' + edit + duplicate + remove + '</td></tr>').appendTo($("#addedDishes"));
        }
        else {
            $('<tr><td>' + Number(Number(i) + Number(1)) + '</td><td>' + dishes[i].dishName + '</td><td>' + dishes[i].specialTypes + '</td><td>' + dishes[i].ingredients + '</td><td>' + dishes[i].restaurantName + '</td>' +
                '<td>' + dishes[i].restaurantCity + '</td><td></td><td>' + edit + duplicate + remove + '</td></tr>').appendTo($("#addedDishes"));
        }
    }
}

function buildFavoritesDishes(dishes) {
    $("#favoritesDishes").empty();
    for(var i = 0; i < dishes.length; i++) {
        var remove = "<button class=\"btn btn-default\" onclick=\"remove(" + dishes[i].id + ")\">הסר מנה</button>";
        if (dishes[i].dishUrl!="") {
            var img = "<img src=\""+ dishes[i].dishUrl + "\" height=\"100px\" width=\"100px\" </img>";
            $('<tr><td>' + Number(Number(i)+ Number(1)) + '</td><td>' + dishes[i].dishName + '</td><td>' + dishes[i].specialTypes + '</td><td>' + dishes[i].ingredients + '</td><td>' + dishes[i].restaurantName + '</td>' +
                '<td>' + dishes[i].restaurantCity + '</td><td>'+img+'</td><td>' + remove + '</td></tr>').appendTo($("#favoritesDishes"));
        }
        else {
            $('<tr><td>' + Number(Number(i)+ Number(1)) + '</td><td>' + dishes[i].dishName + '</td><td>' + dishes[i].specialTypes + '</td><td>' + dishes[i].ingredients + '</td><td>' + dishes[i].restaurantName + '</td>' +
                '<td>' + dishes[i].restaurantCity + '</td><td></td><td>' + remove + '</td></tr>').appendTo($("#favoritesDishes"));
        }

    }
}

function deleteDish(dishId) {
    $.ajax({
        url: 'profile',
        data: {
            'requestType': 'deleteDish',
            'dishId': dishId
        },
        success: function () {
            location.reload();
        }
    });
}

function remove(dishId) {
    $.ajax({
        url: 'profile',
        data: {
            'requestType': 'favorites',
            'dishId': dishId
        },
        success: function () {
            location.reload();
        }
    });
}

function duplicateDish(dishId) {
    sessionStorage.setItem("dishId", dishId);
    window.location.href = "duplicateDish.html";
}

function edit(dishId) {
    sessionStorage.setItem("dishId", dishId);
    window.location.href = 'editDish.html';
}

function editRestaurant() {
    var restUserName = sessionStorage.getItem("restaurantUserName");
    var restName = $('#restName').val();
    var restCity = $("#selectCity option:selected").text();
    var restStreet = $('#street').val();
    var restStreetNum = $('#streetNum').val();
    var email = $('#email').val();
    var contactName = $('#contactName').val();
    var contactPhone = $('#contactPhone').val();
    var restPhone = $('#phone').val();
    var restLink = $('#restLink').val();
    var image = document.getElementById('image').files[0];
    var imageUrl = $('#imageURL').val();

    if (restName === "" || restCity === "" || email === "" || contactName === "" || restLink === "") {
        alert("נא למלא את שדות החובה");
        undoEdit();
    }
    else {
        var formData = new FormData();
        formData.append("requestType","editRestaurant");
        formData.append("restName",restName);
        formData.append("restCity", restCity);
        formData.append("restStreet", restStreet);
        formData.append("restStreetNum", restStreetNum);
        formData.append("email",email);
        formData.append("contactName",contactName);
        formData.append("contactPhone",contactPhone);
        formData.append("phone",restPhone);
        formData.append("restLink",restLink);
        formData.append("image", image);
        formData.append("imageURL", imageUrl);
        formData.append("restUserName",restUserName);
        $.ajax({
            url: 'profile',
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (isAdmin) {
                alert("השינויים נשמרו");
                if (isAdmin === "true")
                    window.location.href = 'admin.html';
                else
                    window.location.href = 'restProfile.html';
            }
        });
    }
}

function undoEdit() {
    if (editBy === "restaurant") {
        window.location.href = 'restProfile.html';
    }
    else if (editBy === "customer")
        window.location.href = 'profile.html';
    else
        window.location.href = 'admin.html';
}

function editRestaurantOnLoad() {
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
            for (var i = 0; i < cities.length; i++) {
                myOption = document.createElement("option");
                myOption.text = cities[i].name;
                citySelect.add(myOption);
            }
        }
    });
    var restUserName = sessionStorage.getItem("restaurantUserName");
    if (restUserName!=null)
        editBy = "admin";
    else
        editBy = "restaurant";
    $.ajax({
        url: 'profile',
        data: {
            "requestType": 'getRestaurant',
            "restUserName":restUserName
        },
        success: function (restaurant) {
            document.getElementById("restName").setAttribute('value',restaurant.restaurantName);
            var option = $('#selectCity option').filter(function() { return $(this).html() == restaurant.city;});
            option.attr('selected', true);
            document.getElementById("street").setAttribute('value',restaurant.street);
            document.getElementById("streetNum").setAttribute('value',restaurant.streetNum);
            document.getElementById("email").setAttribute('value',restaurant.email);
            document.getElementById("contactName").setAttribute('value',restaurant.contactName);
            document.getElementById("contactPhone").setAttribute('value',restaurant.contactPhone);
            document.getElementById("phone").setAttribute('value',restaurant.phone);
            document.getElementById("restLink").setAttribute('value',restaurant.link);
        }
    });
}

function editCustomerOnLoad() {
    var customerUserName = sessionStorage.getItem("customerUserName");
    if (customerUserName!=null)
        editBy = "admin";
    else
        editBy = "customer";
    $.ajax({
        url: 'profile',
        data: {
            "requestType": 'getCustomer',
            "customerUserName": customerUserName
        },
        success: function (customer) {
            document.getElementById("email").setAttribute('value',customer.email);
            document.getElementById("phone").setAttribute('value',customer.phone);
        }
    });
}

function editCustomer() {
    var customerUserName = sessionStorage.getItem("customerUserName");
    var email = $('#email').val();
    var phone = $('#phone').val();

    if (email === "") {
        alert("נא למלא את שדות החובה");
        undoEdit();
    }
    else {
        $.ajax({
            url: 'profile',
            data: {
                'requestType': 'editCustomer',
                'email': email,
                'phone':phone,
                "customerUserName": customerUserName
            },
            success: function (isAdmin) {
                alert("השינויים נשמרו");
                if (isAdmin === "true")
                    window.location.href = 'admin.html';
                else
                    window.location.href = 'profile.html';
            }
        });
    }
}

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