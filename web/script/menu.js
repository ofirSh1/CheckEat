$(function () {
    $.ajaxSetup({cache: false});
    $.ajax({
        url: 'login',
        data: {
            'requestType': 'getUsername'
        },
        type: 'get',
        success: function(username){
            buildMenu(username);
        }
    });
});

function buildMenu(username) {
    $("#menuNav").empty();
    $('<li><a href="searchDish.html">חיפוש</a></li>').appendTo($("#menuNav"));
    $('<li><a href="about.html">אודות</a></li>').appendTo($("#menuNav"));
    $('<li><a href="terms.html">תנאי שימוש</a></li>').appendTo($("#menuNav"));
    $('<li><a href="contact.html">צור קשר</a></li>').appendTo($("#menuNav"));

    if(username) {
        $('<li><a onclick="logout()">יציאה</a></li>').appendTo($("#menuNav"));
        if (username === 'customer')
            $('<li><a href="profile.html">פרופיל</a></li>').appendTo($("#menuNav"));
        else if (username === 'restaurant')
            $('<li><a href="restProfile.html">פרופיל</a></li>').appendTo($("#menuNav"));
        else
            $('<li><a href="admin.html">פרופיל מנהל מערכת</a></li>').appendTo($("#menuNav"));
    }
    else {
        $('<li><a href="login.html">כניסה</a></li>').appendTo($("#menuNav"));
        $('<li><a href="registerType.html">הרשמה</a></li>').appendTo($("#menuNav"));
    }
}

function logout() {
    $.ajax({
        url: 'login',
        data: {
            'requestType': 'logout'
        },
        type: 'post',
        success: function(response){
            window.location.href = 'index.html';
        }
    });
}