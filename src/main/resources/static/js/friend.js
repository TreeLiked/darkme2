$(function () {

    // alert($.session.get("un"));
    $(".btn-del-fri").click(function () {
        doFriDel();
    });

    $(".btn-add-fri").click(function () {
        doFriAdd();
    });
    $(".btn-send-file").click(function () {
        var name = getSelectFriendId();
        if (name !== "0") {
            doSendFile(name);
        } else {
            alert("选中一个好友进行操作");
        }
    });
    $(".btn-send-msg").click(function () {

        var name = getSelectFriendId();
        if (name !== "0") {
            doSendMsg(name);
        } else {
            alert("Choose one friend to operate");
        }
    });
    $(".btn-mod-remark").click(function () {

        var name = getSelectFriendId();
        if (name !== "0") {
            doModRemark(name);
        } else {
            alert("Choose one friend to remark");
        }
    });


    $(".btn-do-send-message").click(function () {

        var c = $(".sd-msg-detail").val();
        if (c !== null && c !== "" && c.length < 100) {
            doReallySendMessage(c);
            $(".btn-cancel-send-msg").click();

        }
    });
});


function showMyFriends() {
    $.get({
        url: "lff",
        dataType: "json",
        cache: false,
        success: function (data) {

            if (data !== "0") {
                // data = JSON.parse(data);
                var $fri_list = $(".friend-list");
                $fri_list.empty();
                $fri_list.append(createFriHead());
                $.each(data, function (k, v) {
                    // console.log(k);
                    // console.log(v[0]);
                    // console.log(v[1]);
                    var $a = createOneFriend(k, v);
                    $fri_list.append($a);
                });
            }
        }
    });
}

function createFriHead() {
// <a class="list-group-item disabled" style="height: 50px">
//         <h4 style="display: block">我的好友</h4>
//         </a>

    var $a = $("<a>");
    $a.addClass("list-group-item disabled");
    $a.css("height", "50px");
    $a.html("<h4 style='display: block'>好友列表</h4>");
    return $a;

}

function createOneFriend(k, v) {


    var $a = $("<a>");
    $a.addClass("list-group-item");
    $a.attr("name", k);
    // $a.css("background-color", "transparent");
    // if (fri_obj.fri_id!= )
    $a.html("<strong>" + k + "</strong>【 备注：<input style='display: inline; color: #0f0f0f' type='text'  disabled='disabled' value=" + v[0] + "> 】<em class='pull-right'>添加时间： " + v[1] + "</em>");
    $a.on("click", function () {
        removeAllActive();
        $(this).addClass("active");
        // $(this).select("input").css("background-color", "black");
    });
    return $a;
}

function removeAllActive() {
    $(".friend-list a").each(function () {
        $(this).removeClass("active");
    })
}

function doFriDel() {
    var fri_id = getSelectFriendId();
    if (fri_id !== "0") {
        $("a[name=" + fri_id + "]").remove();
        $.get({
            url: "rmFri",
            data: {
                fri: fri_id
            },
            cache: false,
            success: function (data) {
                if (data === "1") {
                    doParentShowBanner("alert-success", 0, "删除成功", 3000, 1000, true);
                } else {
                    doParentShowBanner("alert-danger", 0, "删除失败", 3000, 1000, true);
                }
            }
        });
    } else {
        alert("Choose one friend to operate");
    }
}

function doFriAdd() {

    doParentShowBanner("alert-info", 0, "在上方搜索框以f开头输入用户名或邮箱，【例：f张三】", 4000, 1000, true);
    // var str =  parent.parent.$(".search-box").val();
    // alert(str);
}

function doSendMsg(name) {
    $(".btn-show-send-msg-modal").click();
    $(".modal-sd-msg-title").html("Message @【 " + name + " 】");
    $(".btn-send-msg").attr("name", name);
}

function doSendFile(name) {
    parent.parent.$("#file").click();
    // parent.parent.$(".file_up_modal").click();
    parent.parent.$(".file-upload-destination").val(name);
    // parent.parent.$(".file-upload-destination").focus();


}

function doModRemark(n) {

    var nr = prompt("输入新的备注：");
    if (nr !== null && nr !== "") {

        $.get({
            url: "modrem",
            data: {
                u1: n,
                r: nr
            },
            cache: false,
            success: function (r) {
                if (r === "1") {
                    $("a[name=" + n + "] input").val(nr);
                } else {
                    alert("修改失败，请稍后重试");
                }
            }
        });
    }

}

function doReallyFriAdd(str) {
    $.get({
        url: "makeOneFriend",
        dataType: "text",
        cache: false,
        data: {
            u1: str
        },
        success: function (data) {
            if (data !== "0") {
                // alert("111");
                // showFriendInfo("alert-success", "查找成功", "用户名："+data , "请求添加", "btn-info", 10000);

                var usr = data.split(" ")[0];
                showBannerInfo("alert-success", 0, "查找成功【 用户名：" + usr + " 性别：" + data.split(" ")[1] + " 】<a class='btn btn-sm btn-success' href='smkfrqu?sw=" + usr + "&url=" + location.href + "\'>请求添加</a>", 5000, 1000, true);
                // showBannerInfo("alert-success", 0, "查找成功【 用户名：" + usr + " 性别：" + data.split(" ")[1] + " 】<button class='btn btn-sm btn-success' onclick='smkfrqu(\'" + usr + "\')'>"+"请求添加</button>", 10000, 1000, true);
            } else {
                showBannerInfo("alert-info", 0, "System.out.println(\"查无此人\");", 5000, 1000, true);
            }
        }
    });
}

function doReallySendMessage(v) {

    var d = $(".btn-send-msg").attr("name");
    $.get({
        url: "smsg",
        data: {
            u1: v,
            u2: d
        },
        cache: false,
        success: function (r) {
            if (r !== "0") {
                doParentShowBanner("alert-success", 0, "发送成功", 2000, 1000, true);
            } else {
                doParentShowBanner("alert-danger", 1, "发送失败", 3000, 500, true);
            }
        }
    });
}

function showFriendInfo(alert_type, head, content, btn_info, btn_type, show_time) {
    var $div = $(".friend-head-info-div");
    alert(typeof $div);
    $div.show();
    $(".btn-mod-remark").hide();
    // $div.addClass(alert_type);
    //
    // $("#friend-head-info-div-head").text(head);
    // $("#friend-head-info-div-detail").text(content);
    //
    // var $btn = $(".friend-todo-btn");
    // $btn.text(btn_info);
    // $btn.addClass(btn_type);
    //
    //
    // $div.css("display", "inline");
    // $div.slideDown(1000);
    //
    // setTimeout(function () {
    //     $div.slideUp(1000);
    // }, show_time);
}

function getSelectFriendId() {

    var name = "0";
    $(".friend-list a").each(function () {
        if ($(this).hasClass("active")) {
            // alert("111");
            // alert(typeof $(this).attr("name"));
            name = $(this).attr("name");
            return false;
        }
    });
    return name;
}

function doParentShowBanner(alert_type, hide_type, str, show_time, hide_time, auto_hide) {
    parent.parent.showBannerInfo(alert_type, hide_type, str, show_time, hide_time, auto_hide)
}