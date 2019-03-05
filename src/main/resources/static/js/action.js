

$(document).bind("ajaxError", function(event, xhr, options, exc){
    console.log(event);
    console.log(xhr);
    console.log(options);
    console.log(exc);
    showBannerInfo("alert-warning", 0, "服务开小差咯～", 4000,1000,true);
});

// var TmpSecretId;
// var TmpSecretKey;


function upload_file_attachment() {

    $(".file_up_modal").click();
    $(".file-upload-destination").focus();

}

function selectFile() {
    $("#file").click();
}

function createAlertDiv(alert_type, str, isRight, auto_hide) {

    mod_style(2);
    var $div = $("<div>");
    $div.addClass("alert " + alert_type);
    if (isRight) {
        $div.addClass("pull-right");
    }
    $div.attr("role", "alert");
    if (auto_hide) {
        $div.attr("hidden", "hidden");
    }
    $div.css({
        "margin-top": "10px",
        "margin-bottom": "0",
        "clear": "both"
    });

    $div.html(str);
    return $div;

}

function createAlertDivClosed(alert_type, str_strong, str_oth) {
    mod_style(2);
    var $div = $("<div>");
    $div.addClass("alert " + alert_type + " alert-dismissible pull-right");
    $div.attr("role", "alert");
    $div.attr("hidden", "hidden");
    $div.css({
        "margin-top": "10px",
        "margin-bottom": "0",
        "clear": "both"
    });

    var $button = $("<button>");
    $button.addClass("close");
    $button.attr({
        "type": "button",
        "data-dismiss": "alert",
        "aria-label": "Close"
    });

    var $span = $("<span>");
    $span.attr("aria-hidden", "true");
    $span.text("×");

    var $strong = $("<strong>");
    $strong.text(str_strong);

    var $font = $("<span>");
    $font.text(str_oth);

    $button.append($span);
    $div.append($button);
    $div.append($strong);
    $div.append($font);
    return $div;

    
}

function showBannerInfo(alert_type, hide_type, str, show_time, hide_time, auto_hide) {

    mod_style(1);
    var $div_banner = createAlertDiv(alert_type, str, false, auto_hide);
    var $info_div = $(".middle-info-div");
    $info_div.prepend($div_banner);
    $div_banner.show(1000);

    setTimeout(function () {
        switch (hide_type) {
            case 0:
                $div_banner.hide(hide_time);
                break;
            case 1:
                $div_banner.slideUp(hide_time);
                break;
            case 2:
                $div_banner.fadeOut(hide_time);
                break;
            default:
                $div_banner.hide(1000);
                break;
        }

    }, show_time);
}

