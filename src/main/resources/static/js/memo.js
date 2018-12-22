$(document).bind("ajaxError", function (xhr) {
    doParentShowBanner("alert-danger", 0, "异常：" + xhr.status + ", " + xhr.statusText, 4000, 1000, true);
});

function doParentShowBanner(alert_type, hide_type, str, show_time, hide_time, auto_hide) {

    parent.parent.showBannerInfo(alert_type, hide_type, str, show_time, hide_time, auto_hide)
}

function newMemo() {

    let $memo_title = $("#newMemoTitle");
    let $memo_content = $("#newMemoContent");
    let $memo_type = $("#newMemoType2");
    // alert($memo_title.val());

    if (($memo_title.val() !== null && $memo_title.val() !== "") || ($memo_content.val() !== null && $memo_content.val() !== "")) {

        let record = {
            memoTitle: $memo_title.val(),
            memoContent: $memo_content.val(),
            memoType: $memo_type.prop("checked"),
        };
        $.post({
            url: "api/memo/newMemo",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(record),
            cache: false,
            success: function (resp) {
                console.log(resp);
                let code = resp.code;
                if (code === "SUCCESS") {
                    doParentShowBanner("alert-success", 0, "便签添加成功", 4000, 1000, true);
                } else {
                    doParentShowBanner("alert-warning", 0, "便签添加失败", 4000, 1000, true);
                }
                memo_nav_change(0);
                parent.showAmountFlags();
            }
        });
    }
}

function memo_nav_change(index) {

    let $memo_content_div = $(".memo-content-div");
    let $finished = $(".memo-nav-finished");
    let $unfinished = $(".memo-nav-unfinished");
    switch (index) {
        case 0:
            $unfinished.addClass("active");
            $finished.removeClass("active");
            $.get({
                url: "api/memo/getMemo",
                data: {
                    finished: false
                },
                dataType: 'json',
                cache: false,
                success: function (resp) {
                    $memo_content_div.empty();
                    if (resp.code === "SUCCESS") {
                        let memos = resp.data0;
                        if (memos !== null) {
                            $.each(memos, function (index, obj) {
                                $memo_content_div.append(createSingleMemo(obj, false));
                            })
                        }
                    }

                }
            });
            break;
        case 1:
            $finished.addClass("active");
            $unfinished.removeClass("active");
            // alert("???");
            $.get({
                url: "api/memo/getMemo",
                data: {
                    finished: true
                },
                dataType: 'json',
                cache: false,
                success: function (resp) {
                    $memo_content_div.empty();
                    if (resp.code === "SUCCESS") {
                        let memos = resp.data0;
                        if (memos !== null) {
                            $.each(memos, function (index, obj) {
                                $memo_content_div.append(createSingleMemo(obj, true));

                            })
                        }
                    }

                }
            });
            break;
        case 2:
            $("#newMemoTitle").focus();
            break;
        case 3:
            $("#search-memo-input").focus();
            break;
        default:
            break;
    }
}


function createSingleMemo(obj, isFinished) {
    let $divOut = $("<div>");
    $divOut.addClass("panel");
    if (!isFinished) {
        switch (obj.memoType) {
            case 0:
                $divOut.addClass("panel-info");
                break;
            case 1:
                $divOut.addClass("panel-warning");
                break;

        }
    } else {
        $divOut.addClass("panel-success");
    }
    let $divIn1 = createDivIn1(obj, isFinished);
    let $divIn2 = createDivIn2(obj);
    $divOut.append($divIn1);
    $divOut.append($divIn2);
    return $divOut;
}

function createDivIn1(obj, isFinished) {
    let $divIn1 = $("<div>");
    $divIn1.addClass("panel-heading");

    let $h5 = $("<h5>");
    $h5.addClass("panel-title");
    $h5.css("display", "inline");
    $h5.text(obj.memoTitle + "（ 创建于：" + obj.memoPostDate + " ）");
    $divIn1.append($h5);

    let $button_mod = $("<button>");
    let $button_del = $("<button>");
    $button_del.css("color", "white");
    $button_mod.css("color", "white");
    //     <button type="button" class="btn btn-default btn-sm pull-right" style="background-color: transparent">
    //         标记为完成<span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
    //         </button>
    $button_mod.attr("type", "button");
    $button_del.attr("type", "button");
    $button_mod.addClass("btn btn-default btn-sm pull-right");
    $button_del.addClass("btn btn-default btn-sm pull-right");
    $button_mod.css("background-color", "transparent");
    $button_del.css("background-color", "transparent");


    $button_del.html("删除<span class='glyphicon glyphicon-remove' aria-hidden='true'>" + "</span>");


    if (isFinished) {
        $button_mod.html("<span class='glyphicon glyphicon-chevron-left' aria-hidden='true'>" + "</span>取消完成");
        $button_mod.on("click", function () {
            mod_memo(obj.id, 0, false, 1);
        });
        $divIn1.append($button_del);
        $divIn1.append($button_mod);

        $button_del.on("click", function () {
            mod_memo(obj.id, -1, true, 1);
        });

    } else {
        $button_mod.html("标记完成<span class='glyphicon glyphicon-chevron-right' aria-hidden='true'>" + "</span>");
        $button_mod.on("click", function () {
            mod_memo(obj.id, 1, false, 0);
        });
        $divIn1.append($button_mod);
        $divIn1.append($button_del);


        $button_del.on("click", function () {
            mod_memo(obj.id, -1, true, 0);
        });
    }
    return $divIn1;
}

function createDivIn2(obj) {

    let $divIn2 = $("<div>");
    $divIn2.addClass("panel panel-body");
    $divIn2.html(obj.memoContent);
    return $divIn2;
}

function mod_memo(id, toState, isRemoved, index) {

    $.post({
        url: "api/memo/modMyMemo",
        dataType: "json",
        data: {
            id: id,
            toState: toState,
            isRemoved: isRemoved
        },
        cache: false,
        success: function (resp) {
            let code = resp.code;
            if (code === "SUCCESS") {
                if (isRemoved) {
                    doParentShowBanner("alert-success", 1, "便签删除成功", 3000, 1000, true);
                    parent.showAmountFlags();

                    memo_nav_change(index);
                } else {
                    doParentShowBanner("alert-success", 1, "便签信息修改成功", 3000, 1000, true);
                    if (toState === 1) {
                        memo_nav_change(0);
                    } else {
                        memo_nav_change(1);
                    }
                }
            } else {
                doParentShowBanner("alert-danger", 1, "便签删除失败: " + resp.message, 3000, 1000, true);

            }
        }
    });
}

function search_memo() {
    let $search_input = $("#search-memo-input");
    if ($search_input.val() === null || $search_input.val() === "") {
        return;
    }
    let $memo_content_div = $(".memo-content-div");
    $memo_content_div.empty();
    $.get({
            url: "api/memo/searchMemo",
            dataType: "json",
            data: {
                key: $search_input.val()
            },
            cache: false,
            success: function (resp) {
                if (resp.code === "SUCCESS") {
                    $.each(resp.data0, function (index, obj) {
                        $memo_content_div.append(createSingleMemo(obj, true));
                    });
                    doParentShowBanner("alert-info", 0, "查询结束，共" + resp.data0.length + "条记录。", 3000, 1000, true);
                } else {
                    alert(resp.message);
                }
            }
        }
    )
    ;
}