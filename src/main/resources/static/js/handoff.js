$(function () {

    var $get = $("#handoff_fetch_a");
    var $post = $("#handoff_post_a");


    $get.click(function () {
        checkLoginStateAndDoSth(1, "");
    });

    $post.click(function () {
        var text = $(".search-box").val();
        if (text !== null && text !== undefined && text !== "") {
            checkLoginStateAndDoSth(0, text);
        } else {
            showBannerInfo("alert-info", 1, "没有检测到需要推送的文本，请在上方搜索框中粘贴推送的内容后点击此按钮", 6000, 1000, true);
        }
    });


});


function fetchHandoffText(un) {

    $.get({
        url: "guhtfp",
        data: {
            "un": un
        },
        dataType: "text",
        cache: false,
        success: function (data) {
            if ("-1" === data) {
                showBannerInfo("alert-warning", 1, "用户身份异常", 2000, 1000, true);
            } else if ("0" === data) {
                showBannerInfo("alert-info", 1, "没有检测到推送的文本", 2000, 1000, true);
            } else {
                $("#handoff_text_input").html(data);
                // alert(data);
                // copyText();
                var showText;
                if (data.length > 30) {
                    showText = data.substring(0, 30);
                } else {
                    showText = data;
                }
                showBannerInfo("alert-success", 1, "检测到文本：" + showText + "&emsp;&emsp;&emsp;&emsp;<input type='button' class='btn-small' id = 'copy' onclick='copyText()' value='复制到粘贴板(点击两次)'/>", 10000, 1000, true);
                // $(".search-box").attr("value", data);
                // showBannerInfo("alert-success", 1, "检测到文本：" + data + "&emsp;&emsp;&emsp;&emsp;<button class='btn btn-success btn-copy' onclick='copyText()' data-clipboard-action='copy' data-clipboard-text='" + data + "'>复制到粘贴板</button>", 6000, 1000, true);
                // copyText(data);
            }
        }
    });
}

function postHandoffText(un, text) {

    $.get({
        url: "puhttp",
        data: {
            "un": un,
            "text": text
        },
        dataType: "text",
        cache: false,
        success: function (data) {

            if ("success" === data) {
                showResultDialog("alert-success", "文本推送成功，喵～")
            } else {
                showResultDialog("alert-danger", "文本推送失败，呜～")
            }
        }
    });

}

function checkLoginStateAndDoSth(type, text) {
    var un = $.session.get("un");
    if (un === undefined && un === "") {
        alert("您还没有登录");
    } else {
        switch (type) {
            case 0:
                postHandoffText(un, text);
                break;
            case 1:
                fetchHandoffText(un);
                break;
            default:
                break;
        }
    }

}


function showResultDialog(type, msg) {
    var $div = createAlertDivClosed(type, msg, "");
    var $info_area = $(".right_info_area");
    $info_area.append($div);
    $div.show(1000);
}

// function copyText() {

// alert("复制成功");
// var $input = $("#handoff_text_input");
// $input.attr("value", str);
// $(".btn_copy").click();
// var Url2 = document.getElementsByClassName("search-box")[0];
// Url2.select(); // 选择对象
// document.execCommand("Copy"); // 执行浏览器复制命令
// $(".search-box").attr("value", "");
//
// var text = $.session.get("un");

// }
// }
function autoClick() {
    // $("")
}

function copyText() {
    var $btn = $("#copy");
    $btn.zclip({
        path: "/ZeroClipboard.swf",
        copy: function () {
            return $("#handoff_text_input").html();
        }
    });
    offHandoff();
}

function offHandoff() {
    var un = $.session.get("un");
    if (un !== undefined && un !== "") {
        $.get({
            url: "offptw",
            data: {
                "un": un
            },
            dataType: "text",
            cache: false
        });
    }
}

// function copyText(value) {
//     var tempInput = document.createElement("input");
//     tempInput.style = "position: absolute; left: -1000px; top: -1000px";
//     tempInput.value = value;
//     document.body.appendChild(tempInput);
//     tempInput.select();
//     document.execCommand("copy");
//     document.body.removeChild(tempInput);
//
// }