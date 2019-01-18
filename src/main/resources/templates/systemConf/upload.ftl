<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <#include "../common/meta.ftl">
    <script src="${ctx}/js/validator/validator.js"></script>
    <title>上传文件</title>
    <script type="text/javascript">
        //解析xml文件
        /*function uploadFile() {
            var url = "/upload/uploadInfo?toEmail=" + $('#toEmail').val();
            openDialog(url, "uploadInfo", "上传文件", 500, 200, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }*/
        function uploadFile() {
            if (!CheckDataValid('#table')) {
                return false;
            }
            if (false) {
                tipCss($(this), Validatemsg);
                return false;
            }
            var toEmail = $("#toEmail").val();
            if (toEmail == "") {
                tipDialog("收件人不能为空", 3, -1);
                return;
            } else {
                var res = isEmail(toEmail);
                if (res == false) {
                    tipDialog("请检查收件邮箱", 3, -1);
                    return;
                }
            }
            window.setTimeout(function () {
                var fileData = $('#file')[0].files;
                var uploadPath = $('#uploadPath').val();
                var formData = new FormData();
                for (var i = 0; i < fileData.length; i++) {
                    formData.append('files', fileData[i]);
                }
                formData.append('uploadPath', uploadPath);
                formData.append('toEmail', toEmail);
                $('#uploadFile').attr("disabled", true);
                tipDialog("文件正在上传，请注意查收邮件！", 3, 1);
                $.ajax({
                    url: RootPath() + "/upload/uploadFile",
                    type: "POST",
                    data: formData,
                    cache: false,
                    processData: false,
                    contentType: false
                });
            }, 200);
        }
    </script>
</head>
<body>
<div class='load-circle' style="display: none;"></div>
<div id="layout" class="layout">
    <!--中间-->
    <div class="layoutPanel layout-center">
        <!--列表-->
        <div id="grid_List">
            <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
                <table id="table" border="0" class="form-find" style="height: 45px;">
                    <tr>
                        <th>收件人：</th>
                        <td>
                            <input id="toEmail" name="toEmail" type="text" class="txt" style="width: 200px"/>
                            （仅可输入一个收件人）
                        </td>
                        <th>上传目录：</th>
                        <td>
                            <input id="uploadPath" name="uploadPath" type="text" style="width: 200px"
                                   class="txt required"
                                   datacol="yes" checkexpession="NotNull"/>
                        </td>
                        <td>
                            <input id="file" name="files" type="file" multiple="multiple" webkitdirectory
                                   class="txt" datacol="yes" checkexpession="NotNull"/>
                        </td>
                        <td>
                            <input id="uploadFile" type="button" class="btnSearch" value="上传文件"
                                   onclick="uploadFile()"/>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
