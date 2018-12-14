<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <#include "../common/meta.ftl">
    <link href="${ctx}/css/org.css" rel="stylesheet"/>
    <title>上传授权书单文件</title>
    <script>
        //保存事件
        function AcceptClick() {
            if (!CheckDataValid('#form1')) {
                tipCss("文件","文件不能为空");
                return false;
            }
            Loading(true, "正在提交数据...");
            window.setTimeout(function () {
                var fileData = $('#file')[0].files;
                var formData = new FormData();
                for (var i = 0; i < fileData.length; i++) {
                    formData.append('files', fileData[i]);
                }
                formData.append('id',$('#id').val().trim());
                $.ajax({
                    url: RootPath() + "/bookList/bookListFileInsert",
                    type: "POST",
                    data: formData,
                    cache: false,
                    processData: false,
                    contentType: false,
                    success: function (data) {
                        if (data == "success") {
                            tipDialog("保存成功！", 3, 1);
                            top.frames[tabiframeId()].location.reload();
                            closeDialog();
                        } else if (data == "error") {
                            tipCss('授权书单', '保存失败，联系管理员！');
                        } else if (data == "exist") {
                            var note = "注：您确定要覆盖当前授权书单附件？";
                            confirmDialog("温馨提示", note, function (r) {
                                if (r) {
                                    Loading(true, "正在提交数据...");
                                    window.setTimeout(function () {
                                        try {
                                            $.ajax({
                                                url:  RootPath() + "/bookList/bookListFileInsert2",
                                                type: "POST",
                                                contentType: false,//缺失会出现URL编码，无法转成json对象
                                                cache: false,
                                                processData: false,
                                                data: formData,
                                                success: function (data) {
                                                    if (data == "success") {
                                                        tipDialog("保存成功！", 3, 1);
                                                        top.frames[tabiframeId()].location.reload();
                                                        closeDialog();
                                                    } else if (data == "error") {
                                                        tipCss('授权书单', '保存失败，联系管理员！');
                                                    }
                                                },
                                                error: function (data) {
                                                    Loading(false);
                                                    tipDialog("服务器异常！",3, -1);
                                                }
                                            });
                                        } catch (e) {
                                        }
                                    }, 200);
                                }
                            });
                            tipCss('授权书单', '已存在!');
                        }
                    },
                    error: function (data) {
                        Loading(false);
                        alertDialog("保存失败！", -1);
                    }
                });
            }, 200);
        }
    </script>
</head>
<body>
<form id="form1" enctype="multipart/form-data" method="post">
    <div class="ScrollBar" style="margin: 1px; overflow: hidden;">
        <div id="basic" class="tabPanel">
            <table class="form">
                <tr>
                    <input id="id" name="id" type="hidden"  value="${id!""}"/>
                    <th class="formTitle" style="width: 250px;">上传文件(请打包上传一份文件)：</th>
                    <td class="formValue">
                        <input id="file" name="files" type="file"  datacol="yes" checkexpession="NotNull"/>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</form>
</body>

</html>
