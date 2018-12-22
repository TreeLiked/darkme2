// noinspection SpellCheckingInspection
function uploadFile(atta, destination) {
    let file = document.getElementById("file").files[0];
    if (!file) return;

    let filename = file.name;
    let suffix = filename.substring(filename.indexOf(".") + 1);
    if (filename.indexOf(".") > 30) {
        filename = filename.substring(0, 30);
        filename = filename + "." + suffix;
    }
    $.get({
        url: "api/file/generateFileNo",
        cache: false,
        async: false,
        dataType: "json",
        data: {
            fileName: filename
        },
        success: function (resp) {
            console.log(resp);

            let longFlag = resp.message === "1";
            let fileNo = resp.data0;
            let bucketId = resp.data1;
            if (resp.code === "SUCCESS") {


                let $info_area = $(".right_info_area");
                let $onProgressAlert = createAlertDiv("alert-info", "正在上传文件: " + file.name, true, true);
                $info_area.append($onProgressAlert);
                $onProgressAlert.fadeIn(1000);

                let $bar_parent = $(".progress");
                $bar_parent.show();
                let $bar_parent_width = $bar_parent.width();

                let $bar = $(".progress-bar");
                //初始化bar
                $bar.css("width", "0px");
                $bar.text(0 + "%");

                let $detail_info = createAlertDiv("alert-warning", "", true, true);
                $info_area.append($detail_info);
                $detail_info.fadeIn(1000);
                let detailArray = new Array(3);

                let haveCreatedSuccessDiv = false;

                if (longFlag) {
                    Bucket = Bucket60;
                } else {
                    Bucket = Bucket1;
                }
                getCos().sliceUploadFile({
                    Bucket: Bucket,
                    Region: Region,
                    Key: bucketId,
                    Body: file,
                    TaskReady: function (tid) {
                        TaskId = tid;
                    },
                    onProgress: function (progressData) {
                        let flag = false;
                        $.each(progressData, function (name, value) {
                            // let a = parseInt(value);
                            if (name === "loaded") {
                                detailArray[0] = byte_to_KMG(value);
                            }

                            if (!flag) {
                                if (name === "total") {
                                    detailArray[1] = byte_to_KMG(value);
                                    flag = true;
                                }
                            }
                            if (name === "speed") {
                                let str3;
                                let m3 = Math.floor(value / Math.pow(1024, 2));
                                if (m3 > 0) {
                                    m3 = m3.toFixed(2);
                                    str3 = m3 + " MB/s"
                                } else {
                                    let k3 = (value / Math.pow(1024, 1)).toFixed(2);
                                    str3 = k3 + " KB/s";
                                }
                                detailArray[2] = str3;
                            }
                            if (name === "percent") {
                                let percent = value.toString().charAt(0);
                                if (percent !== "1") {
                                    // $("#up_percent").text("上传百分比:\t" + percent + "%");
                                    $bar.css("width", value * $bar_parent_width + "px");
                                    percent = value.toFixed(2) * 100;
                                    $bar.text(percent + "%");

                                } else {
                                    $bar.css("width", $bar_parent_width + "px");
                                    $bar.text(100 + "%");
                                    $bar.removeClass("active");
                                    $bar_parent.fadeOut(1000);

                                    $onProgressAlert.slideUp(1500);
                                    $detail_info.slideUp(1500);
                                    if (!haveCreatedSuccessDiv) {
                                        let record = {
                                            fileName: filename,
                                            fileAttach: atta,
                                            fileDestination: destination,
                                            fileSize: byte_to_KMG(file.size),
                                            fileSaveDays: longFlag ? 3650 : 1
                                        };
                                        $.post({
                                            url: "api/file/generateNewFile?fileNo=" + fileNo,
                                            cache: false,
                                            async: false,
                                            dataType: "json",
                                            contentType: "application/json",
                                            data: JSON.stringify(record),
                                            success: function (resp) {
                                                if (resp.code === "SUCCESS") {
                                                    console.log(resp);
                                                    let fileObj = resp.data0;
                                                    let bringNo = fileObj.fileBringId;
                                                    let days = fileObj.fileSaveDays;
                                                    let str = "长期有效";
                                                    if (days === '1') {
                                                        str = "1天内有效"
                                                    }
                                                    let $success_info = createAlertDivClosed("alert-success", file.name, "上传成功（编号：" + bringNo + "，" + str + "）");
                                                    $info_area.append($success_info);
                                                    $success_info.show(1000);
                                                    haveCreatedSuccessDiv = true;
                                                } else {
                                                    showBannerInfo("alert-danger", 0, "SOMETHING WENT WRONG", 2000, 1000);
                                                }
                                            }
                                        });
                                        // parent.showAmountFlags();
                                    }
                                    return false;
                                }
                            }
                        });
                        $detail_info.html("速度: " + detailArray[2] + "   已经上传: " + detailArray[0] + "   总计大小: " + detailArray[1]);
                    }
                }, function (err, data) {
                    console.log(err, data);
                });
            } else {
                alert("服务异常");
            }
        }
    });
}

function searchFile(text) {
    $.get({
        url: "api/file/getObjectDetail",
        data: {bringNo: text},
        dataType: "json",
        cache: false,
        success: function (resp) {
            if (resp.code === "SUCCESS") {
                console.log(resp);
                let flag = resp.message;
                switch (flag) {
                    case '0':
                        showBannerInfo("alert-danger", 1, "不存在该编号的文件 || 该编号已经过期", 4000, 1000, true);
                        break;
                    case "-1":
                        showBannerInfo("alert-warning", 1, "您没有权限下载此文件", 4000, 1000, true);
                        break;
                    case "1":
                        let obj = resp.data0;
                        console.log(obj);
                        mod_style(1);
                        // showBannerInfo("alert-info", 0, "<a href='doDownload?bucketId=" + obj.fileBucketId + "&filename=" + obj.fileName + "&file_save_days=" + obj.fileSaveDays + "'>" + "文件名：" + obj.fileName + "&emsp;| 大小：" + obj.fileSize + "&emsp;| 备注： " + (obj.fileAttach != undefined ? obj.fileAttach : "无") + "&emsp;| 上传时间：" + obj.filePostDate + "&emsp;| 文件所有者：" + obj.filePostAuthor + "&emsp;&emsp;点击这条通知开始下载（10s后消失)</a>", 10000, 1000, true);
                        showBannerInfo("alert-info", 0, "<a href='api/file/doDownload?id=" + obj.id + "'>" + "文件名：" + obj.fileName + "&emsp;| 大小：" + obj.fileSize + "&emsp;| 备注： " + (obj.fileAttach != undefined ? obj.fileAttach : "无") + "&emsp;| 上传时间：" + obj.filePostDate + "&emsp;| 文件所有者：" + obj.filePostAuthor + "&emsp;&emsp;点击这条通知开始下载（10s后消失)</a>", 10000, 1000, true);
                        break;
                    default:
                        break;
                }
            }
        }
    });
}


function rmFile(id, name) {
    // alert(author);
    // if (author === null || author !== $.session.get("un")) {
    //     parent.parent.showBannerInfo("alert-warning", 0, "您没有权限删除该文件", 4000, 1000, true);
    // } else {
    $.get({
        url: "api/file/rmFile",
        data: {
            id: id,
        },
        dataType: "json",
        success: function (resp) {
            console.log(resp);
            if (resp.code === "SUCCESS") {
                parent.showBannerInfo("alert-success", 0, "文件 " + name + " 删除成功", 4000, 1000, true);
                window.location.reload();
            } else {
                parent.showBannerInfo("alert-danger", 0, "文件 " + name + " 删除失败", 4000, 1000, true);
            }
            // if (result === "success") {
            //     parent.parent.showBannerInfo("alert-success", 0, "文件 " + name + " 删除成功", 4000, 1000, true);
            //     window.location.reload();
            //     parent.showAmountFlags();
            // } else {
            //     parent.parent.showBannerInfo("alert-danger", 0, "文件 " + name + " 删除失败", 4000, 1000, true);
            // }
        }
    });
    // }
}


function getCos() {
    let cos = null;
    $.post({
        url: "api/file/hello-world",
        dataType: "json",
        cache: true,
        async: false,
        success: function (resp) {
            //     alert(data.substring(0, data.indexOf(",")));
            //     alert(data.substring(data.indexOf(".") + 1));
            cos = new COS({
                SecretId: resp.data0,
                SecretKey: resp.data1
            });
        },
        error: function () {
            showBannerInfo("alert-danger", 0, "初始化错误", 3000, 1000, true);
        }
    });
    return cos;
}

