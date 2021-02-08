function byte_to_KMG(bytes) {
    let str;
    let g2 = bytes / Math.pow(1024, 3);
    let g2_ = Math.floor(g2);
    if (g2_ > 0) {
        g2 = g2.toFixed(2);
        str = g2 + " GB"
    } else {
        let m2 = bytes / Math.pow(1024, 2);
        let m2_ = Math.floor(m2);
        if (m2_ > 0) {
            m2 = m2.toFixed(2);
            str = m2 + " MB";
        } else {
            let k2 = (bytes / 1024).toFixed(2);
            str = k2 + " KB";
        }
    }
    return str;
}

function timestampFormat(timestamp) {
    function zeroize(num) {
        return (String(num).length == 1 ? '0' : '') + num;
    }

    let curTimestamp = parseInt(new Date().getTime() / 1000); //当前时间戳
    let timestampDiff = curTimestamp - timestamp; // 参数时间戳与当前时间戳相差秒数

    let curDate = new Date(curTimestamp * 1000); // 当前时间日期对象
    let tmDate = new Date(timestamp * 1000);  // 参数时间戳转换成的日期对象

    let Y = tmDate.getFullYear(), m = tmDate.getMonth() + 1, d = tmDate.getDate();
    let H = tmDate.getHours(), i = tmDate.getMinutes(), s = tmDate.getSeconds();

    if (timestampDiff < 60) { // 一分钟以内
        return "刚刚";
    } else if (timestampDiff < 3600) { // 一小时前之内
        return Math.floor(timestampDiff / 60) + "分钟前";
    } else if (curDate.getFullYear() === Y && curDate.getMonth() + 1 === m && curDate.getDate() === d) {
        return '今天 ' + zeroize(H) + ':' + zeroize(i);
    } else {
        var newDate = new Date((curTimestamp - 86400) * 1000); // 参数中的时间戳加一天转换成的日期对象
        if (newDate.getFullYear() === Y && newDate.getMonth() + 1 === m && newDate.getDate() === d) {
            return '昨天 ' + zeroize(H) + ':' + zeroize(i);
        } else if (curDate.getFullYear() === Y) {
            return zeroize(m) + '月' + zeroize(d) + '日 ' + zeroize(H) + ':' + zeroize(i);
        } else {
            return Y + '年' + zeroize(m) + '月' + zeroize(d) + '日 ' + zeroize(H) + ':' + zeroize(i);
        }
    }
}

// 输入格式：yyyy-MM-DD
function diffDays(date1, date2) {
    //Date.parse() 解析一个日期时间字符串，并返回1970/1/1 午夜距离该日期时间的毫秒数
    const time1 = Date.parse(new Date(sDate1));
    const time2 = Date.parse(new Date(sDate2));
    const nDays = Math.abs(parseInt((time2 - time1) / 1000 / 3600 / 24));
    return nDays;

}

function isEmpty(arg) {

    return arg === undefined && arg == null && arg === "";
}