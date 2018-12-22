function change_page(index,dir_src) {

    if (dir_src === '0') {
        $("#hello-nav-text").show(0);
        $("#content-main").removeAttr("src");
    } else {
        $("#hello-nav-text").hide(0);
        $("#content-main").attr("src", dir_src);
    }
    clear_index(index);

    switch (index) {
        case 0:
            $("#sub_page0").addClass("active");
            break;
        case 1:
            $("#sub_page1").addClass("active");
            break;
        case 2:
            $("#sub_page2").addClass("active");
            break;
        case 3:
            $("#sub_page3").addClass("active");
            break;
        case 4:
            $("#sub_page4").addClass("active");
            break;
        default:
            break;
    }

}
function clear_index() {
    $("#sub_pages_ul li").each(function () {
        $(this).removeClass("active")
    });
}