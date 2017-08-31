var specialTypesEnum = {vegetarian:'צמחוני', naturalist:'טבעוני', kosher:'כשר', noSugar:'ללא סוכר', noGluten:'ללא גלוטן'};
var restProfile = false;

$(function () {
    if (window.location.href != "http://localhost:8080/editDish.html")
        checkIfRestaurant();
});

function checkIfRestaurant(){
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
                    myOption.value = i;
                    citySelect.add(myOption);
                }
            }
        });
    $.ajax({
        url: 'profile',
        data: {
            "requestType": 'checkIfRestaurant'
        },
        success: function(isRest) {
            if (isRest.length!=0) {
                restProfile = true;
                document.getElementById("restName").setAttribute('value',isRest.restaurantName);
                document.getElementById("restName").setAttribute('disabled',true);
                var option = $('#selectCity option').filter(function() { return $(this).html() == isRest.city;});
                option.attr('selected', true);
                document.getElementById("selectCity").setAttribute('disabled',true);
                document.getElementById("street").setAttribute('value',isRest.street);
                document.getElementById("street").setAttribute('disabled',true);
                document.getElementById("streetNum").setAttribute('value',isRest.streetNum);
                document.getElementById("streetNum").setAttribute('disabled',true);
            }
        }
    });
}

function addDish() {
    var restName = $('#restName').val();
    var restCity = $("#selectCity option:selected").text();
    var restStreet = $('#street').val();
    var restStreetNum = $('#streetNum').val();
    var dishName = $('#dishName').val();
    var ingredients = $('#ingredients').val().split(',');
    var specialTypes = getSpecialTypes();
    var otherTypes = $('#otherTypes').val().split(',');
    var image = document.getElementById('image').files[0];
    var imageUrl = $('#imageURL').val();

    var formData = new FormData();
    formData.append("restName", restName);
    formData.append("restCity", restCity);
    formData.append("dishName", dishName);
    formData.append("restStreet", restStreet);
    formData.append("restStreetNum", restStreetNum);
    formData.append("image", image);
    formData.append("specialTypes", JSON.stringify(specialTypes));
    formData.append("otherTypes", JSON.stringify(otherTypes));
    formData.append("ingredients", JSON.stringify(ingredients));
    formData.append("imageURL", imageUrl);

    $.ajax({
        url: "addDish",
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            if(response.length != 0)
                alert(response);
            else {
                if (restProfile == true)
                    window.location.href = 'restProfile.html';
                else
                    window.location.href = 'profile.html';
            }
        },
        error: function() {
            alert("שגיאה בהוספת המנה")
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

function editDish() {
    $.ajax({
        url: 'profile',
        data: {
            "requestType": 'checkIfRestaurant'
        },
        success: function (isRest) {
            if (isRest.length != 0) {
                restProfile = true;
            }
        }
    });
    var dishId = sessionStorage.getItem("dishId");
    var dishName = $('#dishName').val();
    var ingredients = $('#ingredients').val().split(',');
    var specialTypes = getSpecialTypes();
    var otherTypes = $('#otherTypes').val().split(',');
    var image = document.getElementById('image').files[0];
    var imageUrl = $('#imageURL').val();

    if (dishName === "") {
        alert("שדה שם המנה צריך להיות מלא");
        undoEdit();
    }
    else {
        var formData = new FormData();
        formData.append("requestType","editDish");
        formData.append("dishId",dishId);
        formData.append("dishName", dishName);
        formData.append("image", image);
        formData.append("specialTypes", JSON.stringify(specialTypes));
        formData.append("otherTypes", JSON.stringify(otherTypes));
        formData.append("ingredients", JSON.stringify(ingredients));
        formData.append("imageURL", imageUrl);
        $.ajax({
            url: 'profile',
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function () {
                alert("השינויים נשמרו");
                if (restProfile == true) {
                    window.location.href = 'restProfile.html';
                }
                else
                    window.location.href = 'profile.html';
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert('Error: ' + textStatus + ' ' + errorThrown);
            }
        });
    }
}

function undoEdit() {
    if (restProfile == true) {
        window.location.href = 'restProfile.html';
    }
    else
        window.location.href = 'profile.html';
}

function editDishOnLoad() {
    var dishId = sessionStorage.getItem("dishId");
    $.ajax({
        url: 'profile',
        data: {
            "requestType": 'getDish',
            "dishId": dishId
        },
        success: function (dish) {
            document.getElementById("dishName").setAttribute('value',dish.dishName);
            document.getElementById("otherTypes").setAttribute('value',dish.otherTypes);
            document.getElementById("ingredients").setAttribute('value',dish.ingredients);
            if(dish.specialTypes.includes("צמחוני"))
                document.getElementById("vegetarian").checked = true;
            if(dish.specialTypes.includes("טבעוני"))
                document.getElementById("naturalist").checked = true;
            if(dish.specialTypes.includes("כשר"))
                document.getElementById("kosher").checked = true;
            if(dish.specialTypes.includes("ללא סוכר"))
                document.getElementById("noSugar").checked = true;
            if(dish.specialTypes.includes("ללא גלוטן"))
                document.getElementById("noGluten").checked = true;
        }
    });
}

function duplicateDishOnLoad(){
    var dishId = sessionStorage.getItem("dishId");
    $.ajax({
        url: 'profile',
        data: {
            "requestType": 'getDish',
            "dishId": dishId
        },
        success: function (dish) {
            document.getElementById("dishName").setAttribute('value',dish.dishName);
            document.getElementById("otherTypes").setAttribute('value',dish.otherTypes);
            document.getElementById("ingredients").setAttribute('value',dish.ingredients);
            document.getElementById("imageURL").setAttribute('value',dish.dishUrl);
            if(dish.specialTypes.includes("צמחוני"))
                document.getElementById("vegetarian").checked = true;
            if(dish.specialTypes.includes("טבעוני"))
                document.getElementById("naturalist").checked = true;
            if(dish.specialTypes.includes("כשר"))
                document.getElementById("kosher").checked = true;
            if(dish.specialTypes.includes("ללא סוכר"))
                document.getElementById("noSugar").checked = true;
            if(dish.specialTypes.includes("ללא גלוטן"))
                document.getElementById("noGluten").checked = true;
        }
    });
}