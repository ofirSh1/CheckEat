var specialTypesEnum = {vegetarian:'צמחוני', naturalist:'טבעוני', kosher:'כשר', noSugar:'ללא סוכר', noGluten:'ללא גלוטן'};

function findDish()
{
    var restName = $('#restName').val();
    var restCity = $("#selectCity option:selected").text();
    var dishName = $('#dishName').val();
    var specialTypes = getSpecialTypes();
    var otherTypes = $('#otherTypes').val().split(',');
    var ingredients = $('#ingredients').val().split(',');

    $.ajax({
        type: 'get',
        url: 'dish',
        data: {
            'requestType': 'findDishes',
            "restName": restName,
            "restCity": restCity,
            "dishName": dishName,
            specialTypes: specialTypes,
            otherTypes: otherTypes,
            ingredients: ingredients
        },
        success: function(dishes)
        {
            sessionStorage.setItem("dishes", JSON.stringify(dishes));
            window.location.href = "restaurants.html";
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