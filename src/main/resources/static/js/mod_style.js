function mod_style(mod_type) {

    var $bar_height = $(".navbar").height();
    if (mod_type === 4 || mod_type ===5) {
        var margin_top = (parseInt($(".page-header").css("margin-top")) + $bar_height).toString() + "px";
        var $lr_div = $(".lr_div_middle");
    }
    switch (mod_type) {
        case 1:
            $(".middle-info-div").css("margin-top", $bar_height);
            break;
        case 2:
            $(".right_info_area").css("margin-top", $bar_height);
            break;
        case 3:
            $(".d_title").css("margin-top", $bar_height);
            break;
        case 4:
            // alert(margin_top);
            var $login_div = $(".login_div");
            // alert($login_div.css("height"));
            var lr_div_height1 = (parseInt($login_div.css("height")) + parseInt(margin_top)) + "px";

            $lr_div.css({
                "width": "100%",
                "height": lr_div_height1
            });
            $login_div.css("margin-top", margin_top);
            break;
        case 5:
            var $register_div = $(".register_div");
            var lr_div_height0 = (parseInt($register_div.css("height")) + parseInt(margin_top)) + "px";
            $lr_div.css({
                "width": "100%",
                "height": lr_div_height0
            });
            $register_div.css("margin-top", margin_top);
            break;
        default:
            break;

    }
}