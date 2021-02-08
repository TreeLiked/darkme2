// noinspection SpellCheckingInspection
async function uploadFile(rawFile, fileInfo, progressCallback, errorCallback) {
    console.log(rawFile);
    if (!rawFile) return;

    $.get({
        url: "api/file/gfn",
        cache: false,
        async: false,
        dataType: "json",
        data: {
            fileName: rawFile.name
        },
        success: function (result) {
            console.log(result);
            if (result.success) {
                getCos().sliceUploadFile({
                    Bucket: Bucket1,
                    Region: Region,
                    Key: result.data,
                    Body: rawFile,
                    TaskReady: function (tid) {
                        TaskId = tid;
                    },
                    onProgress: function (progressData) {
                        console.log(progressData);
                        progressCallback(progressData, result.data);
                    }
                }, function (err, data) {
                    errorCallback("分片上传错误，" + err);
                });
            } else {
                errorCallback(result.message);
            }
        }
    });
}

async function saveFile(fileNoWithSuffix, rawFile, uploadOption, callback) {
    let record = {
        name: rawFile.name,
        attach: uploadOption.attach,
        destUser: uploadOption.destUserId ? {
            id: uploadOption.destUserId,
        } : null,
        size: rawFile.size,
        saveDays: uploadOption.saveDays,
        no: fileNoWithSuffix,
        open: uploadOption.open,
        password: uploadOption.password
    };
    $.post({
        url: "api/file/create",
        cache: false,
        async: false,
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(record),
        success: function (resp) {
            callback(resp);
        }
    });
}

async function checkFilePassword(fileId, password, callback) {
    $.get({
        url: "api/file/checkPassword?id=" + fileId + "&pwd=" + password,
        cache: false,
        async: true,
        dataType: "json",
        success: r => callback(r)
    });
}

async function doFileDownload(fileId, password, callback) {
    $.get({
        url: "api/file/doDownload?id=" + fileId + "&pwd=" + password,
        cache: false,
        async: true,
        dataType: "json",
        success: r => callback(r)
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

