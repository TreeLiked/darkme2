// noinspection SpellCheckingInspection
function getFileList(pageParam, blurSearchKey, callback) {
    $.post({
        url: "api/file/getPublic?key=" + blurSearchKey,
        cache: false,
        async: true,
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(pageParam),
        success: (result) => callback(result)
    });
}

function getMyFileList(pageParam, blurSearchKey, callback) {
    $.post({
        url: "api/file/getPrivate?key=" + blurSearchKey,
        cache: false,
        async: true,
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(pageParam),
        success: (result) => callback(result)
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


async function removeFile(fileId, callback) {
    $.get({
        url: "api/file/rmFile",
        data: {
            id: fileId,
        },
        dataType: "json",
        contentType: "application/json",
        success: result => callback(result)
    });
}

