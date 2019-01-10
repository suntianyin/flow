<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <#include "../common/meta.ftl">
    <link href="${ctx}/css/org.css" rel="stylesheet"/>
    <title>上传文件</title>
    <script>
        //保存事件
        function AcceptClick() {
            if (!CheckDataValid('#form1')) {
                return false;
            }
            if (false) {
                tipCss($(this), Validatemsg);
                return false;
            }
            $("#loading").show();
            window.setTimeout(function () {
                var fileData = $('#file')[0].files;
                var uploadPath = $('#uploadPath').val();
                var formData = new FormData();
                for (var i = 0; i < fileData.length; i++) {
                    formData.append('files', fileData[i]);
                }
                formData.append('uploadPath', uploadPath);
                $.ajax({
                    url: RootPath() + "/upload/uploadFile",
                    type: "POST",
                    data: formData,
                    cache: false,
                    processData: false,
                    contentType: false,
                    success: function (data) {
                        $("#loading").hide();
                        if (data == "success") {
                            tipDialog("上传成功！", 3, 1);
                            top.frames[tabiframeId()].location.reload();
                            closeDialog();
                        } else if (data == "error") {
                            tipCss('上传文件', '上传失败，联系管理员！');
                        }
                    },
                    error: function (data) {
                        Loading(false);
                        alertDialog(data.responseText, -1);
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
                    <th class="formTitle">上传目录：</th>
                    <td class="formValue">
                        <input id="uploadPath" name="uploadPath" type="text" class="txt required"
                               datacol="yes" checkexpession="NotNull"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">上传文件：</th>
                    <td class="formValue">
                        <input id="file" name="files" type="file" multiple="multiple" webkitdirectory
                               datacol="yes" checkexpession="NotNull"/>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</form>
<div id="loading"><img src="${ctx}/images/ui-anim_basic_16x16.gif"/>
    <span style="font-weight: bolder;">请稍候...</span>
</div>
</body>
</html>
