$(function () {
    $("#mybatis").click(function () {
        // sendRequest();
        $("#mybatisModal").modal('show');
    });
});

function sendRequest() {

    let config = {
        ip: "106.14.115.245",
        port: "3306",
        dbName: "todo",
        name: "root",
        pwd: "root",
        tableName: ["todo_user"]
    };
    $.post({
        url: 'api/util/mybatis',
        dataType: 'json',
        contentType: 'application/json;charset=utf-8',
        data: JSON.stringify(config),
        success: function (resp) {
            if (resp.message === 'SUCCESS') {
                window.open(resp.data0.toString());
            }
        },
        fail: function () {
            alert("123");
        }
    })
}