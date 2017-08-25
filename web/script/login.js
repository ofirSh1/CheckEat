function forgotPassword() {
    var username = $('#username').val();
    if(!username)
        alert('נא להזין שם משתמש');
    else {
        $.ajax({
            type: 'post',
            url: 'login',
            data: {
                'requestType': 'forgotPassword',
                'username': username
            },
            success: function (response) {
            if (response)
                alert(response);
            }
        });
    }
}