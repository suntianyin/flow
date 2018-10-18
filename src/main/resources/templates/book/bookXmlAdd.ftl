<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <#include "../common/meta.ftl">
    <link href="${ctx}/css/org.css" rel="stylesheet"/>
    <title>解析xml</title>
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
            Loading(true, "正在提交数据...");
            window.setTimeout(function () {
                var fileData = $('#file')[0].files;
                var formData = new FormData();
                for (var i = 0; i < fileData.length; i++) {
                    formData.append('files', fileData[i]);
                }
                $.ajax({
                    url: RootPath() + "/book/bookXmlInsert",
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
                            tipCss('图书', '保存失败，联系管理员！');
                        } else if (data == "exist") {
                            tipCss('图书', '图书已存在!');
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
                    <th class="formTitle">解析xml文件：</th>
                    <td class="formValue">
                        <input id="file" name="files" type="file" accept=".xml" datacol="yes" checkexpession="NotNull"/>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</form>
</body>

</html>
