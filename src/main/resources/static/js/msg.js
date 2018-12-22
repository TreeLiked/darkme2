$(function () {
    getMsgs();
});

function getMsgs() {

    $.get({
        url: "gtmymsg",
        dataType: "json",
        cache: false,
        success: function (data) {
            if (data !== "0") {
                var $msgl = $(".msg-list");
                $msgl.empty();
                $.each(data, function (index, obj) {
                    // console.log("循环"+index);
                    // console.log(obj.msg_state);
                    // console.log(obj.msg_from);
                    // console.log(obj.msg_to);
                    // console.log(obj.msg_content);
                    $msgl.append(createOneDiv(obj));
                });
            }
        }
    });
}


function createOneDiv(obj) {
// <div class="alert alert-warning alert-dismissible fade in" role="alert">
//         <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
//     <strong>Holy guacamole!</strong> Best check yo self, you're not looking too good.
//     </div>
    var $div = $("<div>");
    var $btn = $("<button>");
    var $strong = $("<strong>");
    var $p = $("<p>");
    var $pt = $("<p>");

    $btn.addClass("close");
    $btn.attr("type", "button");
    $btn.attr("data-dismiss", "alert");
    $btn.attr("aria-label", "Close");
    $btn.html("<span aria-hidden='true'>×</span>");

    $btn.on("click", function () {
        rmMsg(obj.id);
        $(this).parent("div").remove();
    });

    $p.html(obj.msg_content);
    $pt.html("发送于：" + obj.msg_time);
    $p.css("display", "inline");
    $pt.css("display", "inline");
    $pt.addClass("pull-right");

    $div.addClass("alert alert-dismissible fade in");
    $div.attr("role", "alert");
    $div.attr("name", obj.id);

    switch (obj.msg_type) {
        case 0:
            $div.addClass("alert-warning");
            $strong.html("【系统消息：】");
            break;
        case 1:
            $div.addClass("alert-success");
            $strong.html("【用户消息：】");
            break;
        // case 2:
        //     $div.addClass("alert-info");
        //     $strong.html("【收到一个文件：】");
        //     break;
        case 2:
            $div.addClass("alert-info");
            $strong.html("【收到一个文件：】");
            break;
    }


    $div.append($btn);
    $div.append($strong);
    $div.append($p);

    $div.append($pt);
    if (obj.msg_btn_text !== "null") {
        var $btn_do = $("<button>");
        $btn_do.addClass("btn btn-success btn-sm pull-right");
        $btn_do.css("margin", "0 10px");
        $btn_do.css("padding", "4px 12px");
        $btn_do.html(obj.msg_btn_text);
        $btn_do.on("click", function () {
            agrMkFriReq(obj.id, obj.msg_from, obj.msg_to);
            rmMsg(obj.id);
            $(this).parent("div").remove();
        });
        $div.append($btn_do);
    }
    return $div;
}

function agrMkFriReq(a, b, c) {
    $.get({
        url: "agrMkFriReq",
        data: {
            u0: a,
            u1: b,
            u2: c
        },
        dataType: "text",
        cache: false,
        success: function (data) {
            if (data === "1") {
                dPS("alert-success", "与【" + b + "】添加成功，哦吼吼～", 4000, 1000);
            } else {
                dPS("alert-warning", "添加失败", 3000, 1000);
            }
        }
    });
}

function rmMsg(a) {
    $.get({
        url: "rmMsg",
        data: {
            u0: a
        },
        cache: false,
        success: function () {
            parent.showAmountFlags();
        }
    });
}

function dPS(type, s, t1, t2) {
    parent.parent.showBannerInfo(type, 0, s, t1, t2, true);
}