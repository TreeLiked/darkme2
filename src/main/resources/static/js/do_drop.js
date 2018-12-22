function showLRHeader(type) {
    $(".d_title").css("display", "none");
    var $div_middle = $(".lr_div_middle");
    var $login_div = $(".login_div");
    var $register_div = $(".register_div");
    switch (type) {
        case 0:
            $login_div.css("display", "none");
            mod_style(5);

            if ($register_div.css("display") === "none") {
                $register_div.slideDown(500);
                $div_middle.slideDown(500);
            } else {
                $register_div.hide(1000);
                $div_middle.hide(1000);
            }
            break;
        case 1:
            $register_div.css("display", "none");
            mod_style(4);
            if ($login_div.css("display") === "none") {
                $login_div.slideDown(500);
                $div_middle.slideDown(500);
            } else {
                $login_div.hide(1000);
                $div_middle.hide(1000);
            }
            break;
        case 2:
            break;
        case 3:
            $register_div.css("display", "none");
            mod_style(4);
            if ($login_div.css("display") !== "none") {
                $login_div.hide(1000);
                $div_middle.hide(1000);
            }
            break;
        default:
            $register_div.css("display", "none");
            $register_div.css("display", "none");
            $div_middle.hide(1000);
            break;

    }
    if (type === 1) {

    } else if (type === 0) {

    }
}

function showIntro() {
    mod_style(3);
    $(".d_title").slideDown(500);
    setTimeout(hideTitle, 2000);
}

function hideTitle() {
    $(".d_title").slideUp(500);
}

function slideUpR() {
    $(".register_div").hide(1000);
    $(".lr_div_middle").hide(1000);
}

function slideUpL() {
    $(".login_div").hide(1000);
    $(".lr_div_middle").hide(1000);
}