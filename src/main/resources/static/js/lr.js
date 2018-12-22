$(function () {

    let r_flag1 = false;
    let r_flag2 = false;
    let r_flag3 = true;

    $(".r-un").focusout(function () {

        let $r_un_div = $(".r-un-div");
        let $r_un_s = $(".r-un-s");
        if (this.value === null || this.value === "" || this.value.length > 16 || this.value.length < 2 || this.value.indexOf("@") !== -1 || this.value.indexOf(" ") !== -1) {
            $(this).attr("placeholder", "[ " + $(this).val() + " ] " + "用户名不能为空，长度在2-16个字符以内 (@符号和空格禁止使用)");
            $(this).val("");
            $r_un_div.removeClass("has-success");
            $r_un_div.addClass("has-error");
            $r_un_s.removeClass("glyphicon-ok");
            $r_un_s.addClass("glyphicon-remove");
            r_flag1 = false;
        } else {
            // noinspection SpellCheckingInspection
            $.get({
                url: "trunie",
                data: {
                    u1: this.value
                },
                dataType: "json",
                cache: false,
                success: function (resp) {
                    if ("1" === resp.message) {
                        let $r_un = $(".r-un");
                        $r_un.attr("placeholder", "用户名 [ " + $r_un.val() + " ] 已经被别人注册了，换一个试试吧");
                        $r_un.val("");
                        $r_un_div.removeClass("has-success");
                        $r_un_div.addClass("has-error");
                        $r_un_s.removeClass("glyphicon-ok");
                        $r_un_s.addClass("glyphicon-remove");
                        r_flag1 = false;
                    } else {
                        $r_un_div.removeClass("has-error");
                        $r_un_div.addClass("has-success");
                        $r_un_s.removeClass("glyphicon-remove");
                        $r_un_s.addClass("glyphicon-ok");
                        r_flag1 = true;

                    }
                }
            });
        }
    });

    $(".r-up").focusout(function () {
        let $r_up_div = $(".r-up-div");
        let $r_up_s = $(".r-up-s");
        if (this.value === null || this.value === "" || this.value.length > 16 || this.value.length < 6) {
            $(this).val("");
            $(this).attr("placeholder", "密码不能为空，长度6-16位");
            $r_up_div.removeClass("has-success");
            $r_up_div.addClass("has-error");
            $r_up_s.removeClass("glyphicon-ok");
            $r_up_s.addClass("glyphicon-remove");
            r_flag2 = false;
        } else {
            $r_up_div.removeClass("has-error");
            $r_up_div.addClass("has-success");
            $r_up_s.removeClass("glyphicon-remove");
            $r_up_s.addClass("glyphicon-ok");
            r_flag2 = true;
        }
    });
    $(".r-ue").focusout(function () {
        let $r_ue_div = $(".r-ue-div");
        let $r_ue_s = $(".r-ue-s");
        if (this.value !== null && this.value !== "") {
            let reg = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/;
            if (!reg.test(this.value)) { //格式不对
                $(this).val("");
                $(this).attr("placeholder", "邮箱格式不正确[例如：jane.doe@example.com]");
                $r_ue_div.removeClass("has-success");
                $r_ue_div.removeClass("has-warning");
                $r_ue_div.addClass("has-error");
                $r_ue_s.removeClass("glyphicon-ok");
                $r_ue_s.removeClass("glyphicon-warning-sign");
                $r_ue_s.addClass("glyphicon-remove");
                r_flag3 = false;
            } else {
                $r_ue_div.removeClass("has-error");
                $r_ue_div.removeClass("has-warning");
                $r_ue_div.addClass("has-success");
                $r_ue_s.removeClass("glyphicon-remove");
                $r_ue_s.removeClass("glyphicon-warning-sign");
                $r_ue_s.addClass("glyphicon-ok");
                r_flag3 = true;
            }
        } else {
            $r_ue_div.removeClass("has-success");
            $r_ue_div.removeClass("has-error");
            $r_ue_div.addClass("has-warning");
            $r_ue_s.removeClass("glyphicon-ok");
            $r_ue_s.removeClass("glyphicon-remove");
            $r_ue_s.addClass("glyphicon-warning-sign");
            $(this).attr("placeholder", "邮箱留空将不能使用找回密码功能");
            r_flag3 = true;
        }
    });

    $(".r-btn").click(function () {
        let isMan = $("#r-sex-radio-0").prop("checked");
        let $un = $(".r-un");
        let $up = $(".r-up");
        let $ue = $(".r-ue");
        if (r_flag1 === false) {
            $un.focus();
        } else if (r_flag2 === false) {
            $up.focus();
        } else if (r_flag3 === false) {
            $ue.focus();
        } else {

            let user = {
                username: $un.val(),
                password: $up.val(),
                email: $ue.val(),
                isMan: isMan,
            };
            $.post({
                url: "rv",
                data: JSON.stringify(user),
                contentType: 'application/json',
                async: true,
                dataType: "json",
                cache: false,
                success: function (resp) {
                    if (resp.code === "SUCCESS") {
                        showLRHeader(0);
                        $un.attr("placeholder", "您刚刚注册的用户名为： " + " [" + $un.val() + "] ");
                        showBannerInfo("alert-success", 0, "<a href= 'javascript:void(0)' onclick='" + "login_onRegister(" + $un.val() + "," + $up.val() + ")'>" + "恭喜您注册成功！ [ 用户名： " + $un.val() + " ]" + "</a>", 6000, 1000, true);
                        $un.val("");
                        $up.val("");
                        $ue.val("");
                    } else {
                        alert("注册失败，请稍后重试");
                    }
                }
            });
        }
    });

    $(".l-btn").click(function () {

        let flag1 = true;
        let flag2 = true;
        let $un = $(".l-un");
        let $up = $(".l-up");
        let $ur = $(".l-ur");
        let v1 = $un.val();
        let v2 = $up.val();
        let v3 = $ur.prop("checked");


        if (v1 === null || v1 === "") {
            flag1 = false;
            $un.attr("placeholder", "用户名不可以为空");
            $un.focus();
        }
        if (v2 === null || v2 === "") {
            flag2 = false;
            $up.attr("placeholder", "密码不可以为空");
            if (flag1) {
                $up.focus();
            }
        }
        if (flag1 && flag2) {
            l_post(v1, v2, v3);
            $up.val("");
        } else {
            return false;
        }
    });
});

function l_post(v1, v2, v3) {
    $.post({
        url: "lv",
        data: {
            u1: v1,
            u2: v2,
            u3: v3
        },
        dataType: "json",
        cache: false,
        success: function (resp) {
            let code = resp.code;
            if (code === "SUCCESS") {
                let flag = resp.message;
                if (flag === "1") {
                    showLRHeader(3);
                    $("#personal-center").html("欢迎您, " + resp.data0 + "<span class='caret'></span>");
                    $("#li-personal-center").show();
                    $("#li-login").hide();
                    $("#li-login-out").show();
                    $("#li-register").hide();
                    showBannerInfo("alert-success", 1, "恭喜您，登录成功!", 2000, 500, true);
                    $("#middle-frame").attr("src", "hello");
                    setTimeout(function () {
                        $("#into-personal-center").click();
                    }, 2500);
                } else {
                    let $un = $(".l-un");
                    showBannerInfo("alert-danger", 1, "账户 [ " + v1 + " ] 不存在或密码不匹配", 2000, 500, true);
                    $un.attr("placeholder", "账户 [ " + v1 + " ] 不存在或密码不匹配");
                    $un.val("");
                }
            }
            // // noinspection EqualityComparisonWithCoercionJS
            // if (r === "1") {
            //     $.session.set("un", v1);
            //
            // } else {
            //
            // }
        }
    });
}

function login_onRegister(u1, u2) {
    console.log("心累;");
    $(".middle-info-div").empty();
    $(".d_title").css("display", "none");
    l_post(u1, u2, false);
}

function goPersonalCenter() {

    // $("#middle-frame").attr("th:src", "hello");

    let $frame = $("#frame-div-container");
    if ($frame.css("display") === "block") {
        $(".main").removeClass("a");
        $frame.slideUp(3000);

    } else {
        $(".main").addClass("a");
        $frame.slideDown(3000);
    }

    let $username = $.session.get("un");
    if ($username !== null) {
        $(".self").text($.session.get("un"));
    }
    // $.session("un")

}

function login_out() {
    let user_info_cookie = Cookies.get("USER_INFO_COOKIE");
    if (user_info_cookie !== null) {
        $.get("loginOut", {u1: user_info_cookie});
        Cookies.remove("USER_INFO_COOKIE");
        // $.session.remove("");
        // $.session.clear();
        window.location.reload();
    }
}