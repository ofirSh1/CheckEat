function changePassword() {
    var username = sessionStorage.getItem("userNameToChange");
    var pass = $('#password').val();
    var passE = document.getElementById("password");
    var vPassE = document.getElementById("verifyPassword");

    if(passE.checkValidity() == false) {
        document.getElementById("vpMsg").innerHTML = "";
        document.getElementById("pMsg").innerHTML = passE.validationMessage;
    }
    else if(vPassE.checkValidity() == false) {
        document.getElementById("pMsg").innerHTML = "";
        document.getElementById("vpMsg").innerHTML = vPassE.validationMessage;
    }
    else {
        $.ajax({
            url: 'profile',
            type: 'post',
            data: {
                'requestType': 'changePassword',
                'password': pass,
                'userName': username
            },
            success: function (response) {
                if(response) {
                    alert('הסיסמא שונתה בהצלחה');
                    window.location.href = "admin.html";
                }
                else
                    alert("לא היה ניתן לאפס סיסמא");
            }
        });
    }
}