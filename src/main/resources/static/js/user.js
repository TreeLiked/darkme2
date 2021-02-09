// noinspection SpellCheckingInspection
async function searchUserByKey(key, callback) {
    $.get({
        url: "api/user/searchByKey?key=" + key,
        cache: false,
        async: false,
        dataType: "json",
        success: function (result) {
            callback(result);
        }
    });
}

function checkLoginStatus(callback) {
    $.get({
        url: "api/user/cls",
        cache: false,
        async: true,
        dataType: "json",
        contentType: "application/json",
        success: (result) => callback(result)
    });
}

function dropLoginStatus() {
    $.get({
        url: "api/user/dls",
        cache: false,
        async: true,
        dataType: "json",
        contentType: "application/json",
        success: (result) => {
        },
    });
}

function login(username, password, callback) {
    let body = {
        "name": username,
        "password": password
    };
    $.post({
        url: "api/user/login",
        cache: false,
        async: true,
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(body),
        success: (result) => callback(result)
    });
}

async function register(username, password, callback) {
    let body = {
        "name": username,
        "password": password
    };
    $.post({
        url: "api/user/register",
        cache: false,
        async: false,
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(body),
        success: function (result) {
            callback(result);
        }
    });
}

async function checkUsername(username, callback) {
    $.get({
        url: "/api/user/trunie",
        data: {
            u1: username
        },
        dataType: "json",
        cache: false,
        success: result => callback(result),
    });
}