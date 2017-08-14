function buildAdminPage() {
    getMsg();
    getCustomers();
    getRestaurants();
}

function getMsg() {
    $.ajax({
        url: 'admin',
        data: {
            'requestType': 'getAdmin'
        },
        success: function (msgs) {
            $("#contactList").empty();
            for (var i = 0; i < msgs.length; i++) {
                var answer = "<button class=\"btn btn-default\" onclick=\"answer(" + i + ")\">מענה</button>";
                var remove = "<button class=\"btn btn-default\" onclick=\"removeMsg(" + i + ")\">הסר</button>";
                $('<tr><td>' + Number(Number(i) + Number(1)) + '</td><td>' + msgs[i].name + '</td><td>' + msgs[i].email + '</td><td>' + msgs[i].phone + '</td><td>' + msgs[i].request + '</td>' +
                    '<td>' + msgs[i].content + '</td><td>' + answer + '</td><td>' + remove + '</td></tr>').appendTo($("#contactList"));
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
            for (var i = 0; i < restaurants.length; i++) {
                var edit = "<button class=\"btn btn-default\" onclick=\"editRestaurant(\'' + restaurants[i].userName + '\')\">עריכה</button>";
                var changePassword = "<button class=\"btn btn-default\" onclick=\"changePassword(\'' + restaurants[i].userName + '\')\">שינוי סיסמא</button>";
                var duplicate = "<button class=\"btn btn-default\" onclick=\"duplicate(\'' + restaurants[i].userName + '\')\">שכפול</button>";
                var remove = "<button class=\"btn btn-default\" onclick=\"remove(\'' + restaurants[i].userName + '\')\">הסר</button>";
                $('<tr><td>' + Number(Number(i) + Number(1)) + '</td><td>' + restaurants[i].userName + '</td><td>' + restaurants[i].password + '</td><td>' + restaurants[i].email + '</td><td>' + restaurants[i].restaurantName + '</td>' +
                    '<td>' + restaurants[i].city + '</td><td>' + restaurants[i].street + '</td><td>' + restaurants[i].streetNum + '</td>' +
                    '<td>' + restaurants[i].phone + '</td><td>' + restaurants[i].link + '</td><td>' + restaurants[i].contactName + '</td><td>' + restaurants[i].contactPhone + '</td>' +
                    '<td></td><td>' + edit + changePassword + '</td><td>' + duplicate + '</td><td>' + remove + '</td></tr>').appendTo($("#restaurants"));
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
                var changePassword = "<button class=\"btn btn-default\" onclick=\"changePassword(\'' + customers[i].userName + '\')\">שינוי סיסמא</button>";
                var edit = "<button class=\"btn btn-default\" onclick=\"editCustomer(\'' + customers[i].userName + '\')\">עריכה</button>";
                var remove = "<button class=\"btn btn-default\" onclick=\"remove(\'' + customers[i].userName + '\')\">הסר</button>";
                $('<tr><td>' + Number(Number(i)+ Number(1)) + '</td><td>' + customers[i].userName + '</td><td>' + customers[i].password + '</td><td>' + customers[i].email + '</td><td>' + customers[i].phone + '</td>' +
                    '<td>' + edit + changePassword + '</td><td>' + remove + '</td></tr>').appendTo($("#customers"));
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert('Error: ' + textStatus + ' ' + errorThrown);
        }
    });
}

function duplicate(userName) {

}

function changePassword(userName) {

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

function answer(numMsg) {

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
