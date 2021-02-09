let app = new Vue({
    el: '#app',
    data: function () {
        return {
            visible: false,

            // 当前激活导航标签页面
            currentActiveIndex: '2-1',

            // 对话框可见控制
            dialogVisibility: {
                // 通用帮助
                commonHelp: false,
                // 文件上传
                fileUpload: false,
                // 是否显示上传按钮
                fileUploadBtn: false,
                // 是否展示文件详情对话框
                fileDetailDialog: false,
                // 下载文件输入密码对话框
                fileDownloadPwdDialog: false,
                // 登录注册表单对话框
                lrDialog: false,
                loginDialog: true,
            },
            // 加载状态控制
            loading: {
                fileList: false,
                doFileUploadSubmitBtn: false,
                fileUploadUserSearching: false,
                checkFilePassword: false,
                loginLoading: false,
                // 用户文件列表加载
                userFileList: false,
                fileDelete: false,
            },

            // 通用帮助对话框文案
            commonHelp: {
                title: '',
                content: '',
            },

            input: {
                fileSearch: ''
            },

            form: {
                fileUpload: {
                    names: '没有选择文件',
                    open: true,
                    attach: '',
                    password: '',
                    destUserId: '',
                    saveDays: 1,
                },
                filePageParam: {
                    currentPage: 1,
                    pageSize: 24,
                    totalSize: 24,
                    searchKey: '',
                },
                // 登录或注册表单
                lrForm: {
                    name: '满月',
                    password: '000000',
                    // 如果是注册，第二次的重复输入密码
                    password2: '',
                }
            },

            savedData: {
                // 单次最大可以上传文件总大小1G
                maxUploadFileSize: 1073741824,
                // maxUploadFileSize: 2097152,
                fileSuffixes: ['ae', 'ai', 'audition', 'avi', 'bridge',
                    'css', 'csv', 'dbf', 'doc', 'dw', 'dwg', 'exe',
                    'file', 'fireworks', 'fla', 'flash', 'html', 'illustrator',
                    'indesign', 'iso', 'js', 'jpg', 'json', 'mp3',
                    'mp4', 'pdf', 'ps', 'png', 'ppt', 'prelude',
                    'premiere', 'psd', 'rtf', 'search', 'svg', 'txt', 'xls',
                    'xml', 'zip', 'zip-1', 'docx', 'rar', 'xlsx'
                ],
                imageSuffixes: [
                    'jpg', 'png', 'jpeg'
                ],
                userList: [],
                saveDays: [{"label": "1 天", "value": "1"}, {"label": "3 天", "value": "3"},
                    {"label": "7 天", "value": "7"}, {"label": "永久", "value": "3650"}
                ],
                fileList: [],
                // 用户的文件列表
                userFileList: [],
                userSentCount: 0,
                userReceivedCount: 0,
                // 用户文件列表与时间的map对象
                userTimedFileListMap: {},

                dialogFile: {},
                downloadFile: {},
                downloadFilePassword: '',
                user: {
                    id: '',
                    name: '',
                },
                isLogin: false,
                displayLoginForm: true,
            }
        }
    },

    watch: {
        // 监听登录状态的变化，
        'savedData.isLogin'(newVal, oldVal) {
            this.fetchOrResetMyFileList();
        },
    },

    mounted: function () {
        this.checkAndSetLoginStatus(null, true);
        if (localStorage.username) {
            //
            this.user.name = localStorage.username;
        }
        if (localStorage.userId) {
            //
            this.user.id = localStorage.userId;
        }
        this.getFileList("");
    },

    methods: {
        handleSelect(fullKey, keyPath) {

            console.log(fullKey, keyPath);
            const firstKey = keyPath[0];
            switch (firstKey) {
                case '1':
                    break;
                case '2':

                    // 文件
                    if (fullKey === '2-2') {
                        // 文件帮助
                        this.commonHelp.title = '文件中转使用帮助';
                        this.commonHelp.content = '1、未登录可用作文件中转，文件编号有效期：1-7（单位：天）<br>' +
                            '2、登录后文件长期保存，可选择有效期：永久（单位：天）<br>' +
                            '3、公开文件可以使用文件名或编号以进行搜索，非公开文件仅能通过编号搜索<br>' +
                            '4、文件如果指定给目标用户则该文件会出现在目标用户的文件列表中<br>' +
                            '5、公开非加密图像类型文件可以直接预览<br>' +
                            '6、单次上传文件总大小不得超过1G<br>' +
                            '7、<span style="color: #CD5C5C">请勿上传或传播任何非法内容，否则后果自负</span>';

                        this.dialogVisibility.commonHelp = true;
                    }
                    ;
                    break;
                case '3':
                    this.showWarningMsg("好友系统暂未开放，敬请期待");
                    // 备忘录
                    break;
                case '6':
                    // 已经登录状态
                    if (fullKey === '6-1') {
                        // 退出登录
                        dropLoginStatus();
                        this.savedData.isLogin = false;
                        Object.assign(this.$data.savedData.user, this.$options.data().savedData.user);
                        return;
                    }
                    break;
                case '7':
                    this.resetAndOpenLoginDialog();
                    break;

                default:
                    break;
            }
            this.currentActiveIndex = '2-1';
        },


        calFilePath(fileName) {
            let defaultSuffix = '/img/file.png';
            if (!fileName || fileName.length < 0) {
                return defaultSuffix;
            }
            const suffix = fileName.substr(fileName.lastIndexOf(".") + 1).toLocaleLowerCase();
            if (this.savedData.fileSuffixes.indexOf(suffix) === -1) {
                return defaultSuffix;
            }
            let temp = suffix.toLocaleLowerCase();
            if (temp === 'docx') {
                temp = 'doc';
            }
            if (temp === 'xlsx') {
                temp = 'xls';
            }
            if (temp === 'rar') {
                temp = 'zip';
            }
            defaultSuffix = '/img/' + temp + '.png';
            return defaultSuffix;
        },


        // 自定义文件上传
        customFileUpload(param) {
            console.log("customFileUpload..", param, this.$refs.fileUpload.uploadFiles);
            this.displayFileUploadOptDialog(false);
            const rawFile = param.file;

            const that = this;
            uploadFile(rawFile, "", async (progressData, fileNoWithSuffix) => {
                console.log("回调", progressData);
                // {loaded: 31839, total: 31839, speed: 64321.21, percent: 1}
                param.onProgress({percent: progressData.percent === 1 ? 100 : progressData.percent * 100});
                if (progressData.percent === 1 && progressData.loaded === progressData.total) {
                    param.onSuccess();
                    // 保存文件记录
                    saveFile(fileNoWithSuffix, rawFile, that.form.fileUpload, (saveResult) => {
                        console.log("SaveResult: ", saveResult);
                        if (saveResult && saveResult.success) {
                            that.$notify({
                                title: rawFile.name + ' 上传成功',
                                dangerouslyUseHTMLString: true,
                                message: '文件大小：' + byte_to_KMG(rawFile.size) + '<br>文件编号：' + saveResult.data.no,
                                type: 'success',
                                duration: 0
                            });
                        } else {
                            that.$notify({
                                title: rawFile.name + ' 上传失败',
                                dangerouslyUseHTMLString: true,
                                message: saveResult.message,
                                type: 'error',
                                duration: 0
                            });
                        }
                    });
                    if (that.$refs.fileUpload.uploadFiles) {
                        const arr = that.$refs.fileUpload.uploadFiles;
                        let hasUnFinished = false;
                        for (let i = 0; i < arr.length; ++i) {
                            if (arr[i].status !== "success") {
                                hasUnFinished = true;
                            }
                        }
                        if (!hasUnFinished) {
                            // 全部上传完成后移除
                            that.$refs.fileUpload.clearFiles();
                            that.dialogVisibility.fileUploadBtn = false;
                            that.refreshFileList();
                            that.fetchOrResetMyFileList(false);

                        }

                    } else {
                        that.dialogVisibility.fileUploadBtn = false;
                    }
                }
            }, (errorMsg) => {
            });
        },
        doUploadFile() {
            this.$refs.fileUpload.submit();
        },

        // 模糊查询用户
        async blurSearchUsers(key) {

            const that = this;
            if (key == null || key === '' || key.trim().length === 0) {
                this.savedData.userList = [];
                return;
            }
            await searchUserByKey(key, (result) => {
                if (result.success) {
                    that.savedData.userList = result.data.map((user) => {
                        return {
                            "label": user.name, "value": user.id,
                        };
                    });
                } else {
                    that.savedData.userList = [];
                    that.showErrorMsg(result.message);
                }
            });
        },

        // 刷新文件列表
        refreshFileList() {
            this.form.filePageParam.currentPage = 1;
            this.form.filePageParam.searchKey = '';
            this.getFileList("");
        },

        // 获取文件列表
        getFileList(blurSearchKey) {
            if (this.loading.fileList) {
                this.showWarningMsg("当前正在加载！");
                return;
            }
            this.loading.fileList = true;
            const that = this;
            getFileList(this.form.filePageParam, blurSearchKey, (result) => {
                if (result && result.success) {
                    const pageData = result.data;
                    that.savedData.fileList = pageData.data;
                    that.form.filePageParam.currentPage = pageData.currentPage;
                    that.form.filePageParam.pageSize = pageData.pageSize;
                    that.form.filePageParam.totalSize = pageData.total;
                } else {
                    that.showErrorMsg(result.message);
                }
                that.loading.fileList = false;
            });
        },

        // 搜索文件
        blurSearchFile(key) {
            const that = this;
            this.$nextTick(() => {
                that.getFileList(key);
            });
        },

        // 点击下载按钮
        downloadFile(file) {
            if (!file) {
                this.showErrorMsg("找不到文件");
                return;
            }
            this.savedData.downloadFile = file;
            this.savedData.downloadFilePassword = '';

            const that = this;

            let needPassword;
            if (file.password != null && file.password !== "") {
                // 需要密码
                needPassword = true;
                that.dialogVisibility.fileDownloadPwdDialog = true;
            } else {
                this.downloadFileNow(file.id, '');
            }
        },
        // 点击删除按钮
        deleteFile(file) {
            if (!file) {
                this.showErrorMsg("找不到文件");
                return;
            }
            const that = this;
            this.loading.fileDelete = true;
            removeFile(file.id, (res) => {
                that.loading.fileDelete = false;
                if (res.success) {
                    that.dialogVisibility.fileDetailDialog = false;
                    const index = that.savedData.fileList.indexOf(file);
                    if (index !== -1) {
                        // 删除这个元素
                        that.savedData.fileList.splice(index, 1);
                    }
                    // 重新获取我的文件
                    that.showSuccessMsg("文件 [ " + file.name + " ] 删除成功")
                    that.fetchOrResetMyFileList(false);
                } else {
                    that.showErrorMsg(res.message);
                }
            });
        },

        // 输入密码后确认下载
        confirmFileDownload() {
            const downloadFile = this.savedData.downloadFile;
            this.loading.checkFilePassword = true;
            if (!downloadFile) {
                this.showErrorMsg("找不到文件");
                this.loading.checkFilePassword = false;
                return;
            }
            let needPassword = false;
            let password;
            if (downloadFile.password != null && downloadFile.password !== "") {
                // 需要密码
                needPassword = true;
                password = this.savedData.downloadFilePassword;
                if (!password || password.trim().length === 0) {
                    this.showWarningMsg("请输入下载密码");
                    this.loading.checkFilePassword = false;
                    return;
                }
            }
            // 可以下载文件，需要校验密码
            const that = this;
            if (needPassword) {
                checkFilePassword(downloadFile.id, password, (result) => {
                    that.loading.checkFilePassword = false;
                    if (result.success) {
                        that.dialogVisibility.fileDownloadPwdDialog = false;
                        // 可以下载
                        that.downloadFileNow(downloadFile.id, password);
                    } else {
                        that.showErrorMsg(result.message);
                    }
                });
            } else {
                that.downloadFileNow(downloadFile.id, password);
            }
        },

        // 立即下载文件
        downloadFileNow(fileId, password) {
            // 仍然检查一遍密码
            doFileDownload(fileId, password, (res) => {
                if (res.success) {
                    window.open(res.data, '_blank');
                } else {
                    this.showErrorMsg(res.message);
                }
            });
        },

        // ------------- 用户操作 -------------
        // 我要登录
        doLogin() {
            const that = this;
            this.loading.loginLoading = true;
            login(this.form.lrForm.name, this.form.lrForm.password, (result) => {
                that.loading.loginLoading = false;
                console.log(result);
                if (result.success) {
                    that.dialogVisibility.lrDialog = false;
                    that.showSuccessMsg("登录成功 ～");
                    that.checkAndSetLoginStatus(result.data);
                } else {
                    that.savedData.isLogin = false;
                    that.showErrorMsg(result.message);
                }
            })
        },

        // 我要注册
        doRegister() {
            const that = this;
            const name = this.form.lrForm.name;
            if (!name || name.trim().length === 0) {
                this.showWarningMsg("请输入正确的用户名");
                return;
            }
            const password = this.form.lrForm.password;
            const password2 = this.form.lrForm.password2;

            if (!password || password.length === 0 || password.length > 16) {
                this.showWarningMsg("请输入正确的密码, 0-16位字母和数字的组合");
                return;
            }
            if (password2 !== password) {
                this.showWarningMsg("两次密码不一样，请重新输入");
                return;
            }

            this.loading.loginLoading = true;
            checkUsername(name, (checkRes) => {
                if (checkRes.success) {
                    // 如果是true则有相同的用户名
                    const hasSame = checkRes.data;
                    if (hasSame === true) {
                        that.showWarningMsg("用户名重复，请重新输入");
                        that.loading.loginLoading = false;
                    } else {
                        register(name, password, (result) => {
                            that.loading.loginLoading = false;
                            if (result.success) {
                                that.dialogVisibility.lrDialog = false;
                                that.checkAndSetLoginStatus(result.data);
                                that.showSuccessMsg("注册成功，已为您登录 ～");
                            } else {
                                that.savedData.isLogin = false;
                                that.showErrorMsg(result.message);
                            }
                        })
                    }
                } else {
                    that.showErrorMsg(checkRes.message);
                }
            });


        },

        // 检查登录状态
        checkAndSetLoginStatus(user, initCheck) {
            const that = this;
            if (user == null) {
                // 不是登录注册返回的user，而是默认加载检查登录状态
                checkLoginStatus((result) => {
                    if (result.success) {
                        that.checkAndSetLoginStatus(result.data, false);

                    } else {
                        if (!initCheck) {
                            that.showErrorMsg("登录/注册失败");
                        }
                    }
                });
            } else {
                that.savedData.user = user;
                that.savedData.isLogin = true;
            }
        },

        // 获取我的文件列表
        fetchOrResetMyFileList(showTip) {
            if (!this.savedData.isLogin) {
                this.savedData.userFileList = [];
                return;
            }
            this.loading.userFileList = true;
            const that = this;
            getMyFileList(null, '', (result) => {
                that.loading.userFileList = false;
                if (result.success) {
                    // 这里返回的是分页数据
                    const fileList = result.data.data;
                    that.savedData.userFileList = fileList;
                    that.savedData.userTimedFileListMap = {};
                    // 处理成map
                    let sent = 0, received = 0;
                    const userId = that.savedData.user.id;
                    fileList.forEach((file) => {
                        if (file.destUser && file.destUser.id === userId) {
                            // 收到的文件
                            received++;
                        }
                        if (file.user && file.user.id === userId) {
                            // 收到的文件
                            sent++;
                        }
                        const timeStr = that.formatTime(file.gmtCreated);
                        if (that.savedData.userTimedFileListMap.hasOwnProperty(timeStr)) {
                            that.savedData.userTimedFileListMap[timeStr].push(file);
                        } else {
                            // 没有包含这个时间
                            let arr = [];
                            arr.push(file);
                            that.savedData.userTimedFileListMap[timeStr] = arr;
                        }
                    });
                    that.savedData.userSentCount = sent;
                    that.savedData.userReceivedCount = received;

                    console.log(that.savedData.userTimedFileListMap);

                } else {
                    if (showTip) {
                        that.showErrorMsg(result.message);
                    }
                }
            });
        },

        // ------------- 状态改变 -------------
        // 上传的文件列表状态改变
        onFileUploadListChange(file, fileList) {
            console.log("onFileUploadListChange", file, fileList);
            if (fileList && fileList.length > 0) {
                this.form.fileUpload.names = fileList.map(v => v.name).join("、");
            }
            const exceed = eval(fileList.map(f => f.size).join("+")) >= this.savedData.maxUploadFileSize;
            if (exceed) {
                this.showErrorMsg("文件总大小超出限制");
                this.dialogVisibility.fileUploadBtn = false;
                return;
            }
            this.dialogVisibility.fileUploadBtn = fileList && fileList.length > 0;
        },
        handleCurrentChange(val) {
            this.form.filePageParam.currentPage = val;
            this.getFileList('');
        },
        handleSizeChange(val) {
            this.form.filePageParam.pageSize = val;
            this.form.filePageParam.currentPage = 1;
            this.getFileList('');
        },

        // 显示文件详情对话框
        handleDisplayFileDetail(file) {
            console.log("handleDisplayFileDetail ..", file);

            const that = this;
            if (this.fileHasPassword(file)) {
                if (file.attach) {
                    if (!this.savedData.isLogin || (file.destUser && this.savedData.user.id !== file.destUser.id)) {
                        // 只有当目标用户不是当前用户并且文件加密的时候无法查看文件备注
                        file.attach = "加密文件备注无法查看";
                    }
                }
            }
            this.savedData.dialogFile = file;

            this.$nextTick(() => {
                that.dialogVisibility.fileDetailDialog = true;
            });
        },

        // 对话框控制
        displayFileUploadOptDialog(display) {
            this.dialogVisibility.fileUpload = display;
        },
        fileHasPassword(file) {
            if (!file) {
                return false;
            }
            return file.password && file.password === 'YES';
        },

        // 对话框工具类
        showErrorMsg(msg) {
            this.$message.error(msg);
        },
        showWarningMsg(msg) {
            this.$message({message: msg, type: 'warning'});
        },
        showSuccessMsg(msg) {
            this.$message({message: msg, type: 'success'});
        },
        // 其他
        formatTime(date) {
            return timestampFormat(new Date(date).getTime() / 1000);
        },
        getFileSizeZh(size) {
            return byte_to_KMG(size);
        },
        // 是否是图片，如果是并且公开没有密码的话，允许预览
        isImage(file) {
            if (file.password != null && file.password !== "") {
                return false;
            }
            const fileName = file.name;
            const suffix = fileName.substr(fileName.lastIndexOf(".") + 1).toLocaleLowerCase();

            return this.savedData.imageSuffixes.indexOf(suffix) !== -1;

        },
        resetAndOpenLoginDialog() {
            // 默认每次都打开登录的对话框
            this.dialogVisibility.loginDialog = true;
            this.form.lrForm.name = '';
            this.form.lrForm.password = '';
            this.form.lrForm.password2 = '';
            this.dialogVisibility.lrDialog = !this.dialogVisibility.lrDialog;
        },
    },


})