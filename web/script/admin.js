function buildAdminPage() {
    getMsg();
    getCustomers();
    getRestaurants();
}

function getMsg() {
    $.ajax({
        url: 'admin',
        data: {
            'requestType': 'getAdminMsg'
        },
        success: function (msgs) {
            $("#contactList").empty();
            for (var i = 0; i < msgs.length; i++) {
                var remove = "<button class=\"btn btn-default\" onclick=\"removeMsg(" + i + ")\">הסר</button>";
                $('<tr><td>' + Number(Number(i) + Number(1)) + '</td><td>' + msgs[i].name + '</td><td>' + msgs[i].email + '</td><td>' + msgs[i].phone + '</td><td>' + msgs[i].request + '</td>' +
                    '<td>' + msgs[i].content + '</td><td>' + remove + '</td></tr>').appendTo($("#contactList"));
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert('Error: ' + textStatus + ' ' + errorThrown);
        }
    });
}

function getRestaurants() {
    $.ajax({
        url: 'admin',
        data: {
            'requestType': 'getRestaurants'
        },
        success: function (restaurants) {
            $("#restaurants").empty();
            var link;
            var img;
            for (var i = 0; i < restaurants.length; i++) {
                var edit = "<button class=\"btn btn-default\" onclick=\"editRestaurant(\'" + restaurants[i].userName + "\')\">עריכה</button>";
                var changePassword = "<button class=\"btn btn-default\" onclick=\"changePassword(\'" + restaurants[i].userName + "\')\">שינוי סיסמא</button>";
                var duplicate = "<button class=\"btn btn-default\" onclick=\"duplicate(\'" + restaurants[i].userName + "\')\">שכפול</button>";
                var remove = "<button class=\"btn btn-default\" onclick=\"remove(\'" + restaurants[i].userName + "\')\">הסר</button>";
                if (restaurants[i].logoUrl != "")
                    img = "<img src=\"" + restaurants[i].logoUrl + "\" height=\"100px\" width=\"100px\" </img>";
                else
                    img = "";
                if (restaurants[i].link != "")
                    link = "<a href=\"" + restaurants[i].link + "\"> לחץ כאן </a>";
                else
                    link = "";
                $('<tr><td>' + Number(Number(i) + Number(1)) + '</td><td>' + restaurants[i].userName + '</td><td>' + restaurants[i].email + '</td><td>' + restaurants[i].restaurantName + '</td>' +
                    '<td>' + restaurants[i].city + '</td><td>' + restaurants[i].street + '</td><td>' + restaurants[i].streetNum + '</td>' +
                    '<td>' + restaurants[i].phone + '</td><td>' + link + '</td><td>' + restaurants[i].contactName + '</td><td>' + restaurants[i].contactPhone + '</td>' +
                    '<td>' + img + '</td><td>' + edit + changePassword + '</td><td>' + duplicate + '</td><td>' + remove + '</td></tr>').appendTo($("#restaurants"));
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert('Error: ' + textStatus + ' ' + errorThrown);
        }
    });
}

function getCustomers() {
    $.ajax({
        url: 'admin',
        data: {
            'requestType': 'getCustomers'
        },
        success: function (customers) {
            $("#customers").empty();
            for(var i = 0; i < customers.length; i++) {
                var changePassword = "<button class=\"btn btn-default\" onclick=\"changePassword(\'" + customers[i].userName + "\')\">שינוי סיסמא</button>";
                var edit = "<button class=\"btn btn-default\" onclick=\"editCustomer(\'" + customers[i].userName + "\')\">עריכה</button>";
                var remove = "<button class=\"btn btn-default\" onclick=\"remove(\'" + customers[i].userName + "\')\">הסר</button>";
                $('<tr><td>' + Number(Number(i)+ Number(1)) + '</td><td>' + customers[i].userName + '</td><td>' + customers[i].email + '</td><td>' + customers[i].phone + '</td>' +
                    '<td>' + edit + changePassword + '</td><td>' + remove + '</td></tr>').appendTo($("#customers"));
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert('Error: ' + textStatus + ' ' + errorThrown);
        }
    });
}

function duplicate(userName) {
    sessionStorage.setItem("restaurantUserName", userName);
    window.location.href = "duplicateRestaurant.html";
}

function changePassword(userName) {
    sessionStorage.setItem("userNameToChange", userName);
    window.location.href = "changePassword.html";
}

function editCustomer(userName) {
    sessionStorage.setItem("customerUserName", userName);
    window.location.href = "editCustomer.html";
}

function editRestaurant(userName) {
    sessionStorage.setItem("restaurantUserName", userName);
    window.location.href = "editRestaurant.html";
}

function remove(userName) {
    var result = confirm("האם אתה בטוח?");
    if (result) {
        $.ajax({
            url: 'admin',
            data: {
                'requestType': 'removeUser',
                'userName': userName
            }
        });
        window.location.reload();
    }
}

function removeMsg(numMsg) {
    $.ajax({
        url: 'admin',
        data: {
            'requestType': 'removeMsg',
            'numMsg': numMsg
        }
    });
    window.location.reload();
}

function duplicateRestaurantOnLoad() {
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
    $.ajax({
        url: 'profile',
        data: {
            "requestType": 'getRestaurant',
            "restUserName":restUserName
        },
        success: function (restaurant) {
            document.getElementById("username").setAttribute('value',restaurant.userName);
            document.getElementById("password").setAttribute('value',restaurant.password);
            document.getElementById("verifyPassword").setAttribute('value',restaurant.verifyPassword);
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
            document.getElementById("imageURL").setAttribute('value',restaurant.logoUrl);
        }
    });
}