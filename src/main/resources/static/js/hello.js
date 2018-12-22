function showAmountFlags() {
    $.get({
        url: "countAll",
        data: "json",
        cache: false,
        success: function (resp) {
            if (resp.code === "SUCCESS") {
                $("#file_amount_flag").html(resp.data0);
                $("#memo_amount_flag").html(resp.data1);
            }
        }
    });
}