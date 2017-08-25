function changePassword() {
    var url = new URL(window.location);
    var token = url.searchParams.get("token");
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
            url: 'login',
            type: 'post',
            data: {
                'requestType': 'resetPassword',
                'token': token,
                'password': pass
            },
            success: function (response) {
                if(response === 'true')
                    window.location.href = "index.html";
                else
                    alert("לא היה ניתן לאפס סיסמא");
            }
        });
    }
}