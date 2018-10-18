<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <script src="${ctx}/js/jsPage.js"></script>
    <#include "../common/meta.ftl">
    <link href="${ctx}/css/org.css" rel="stylesheet"/>
    <title>上传epub文件</title>
</head>
<body>
<form id="form1" enctype="multipart/form-data" method="post">
    <div class="ScrollBar" style="margin: 1px; overflow: hidden;">
        <div id="basic" class="tabPanel">
            <table class="form">
                <tr>
                    <th class="formTitle">metaId：</th>
                    <td class="formValue">
                        <input id="metaId" name="metaId" value="${metaId!''}" type="text" class="txt required"
                               readonly datacol="yes" checkexpession="NotNull"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">出版日期：</th>
                    <td class="formValue">
                        <input id="publishDate" name="publishDate" value="${publishDate!''}" type="text" class="txt required"
                               readonly datacol="yes" checkexpession="NotNull"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">上传文件：</th>
                    <td class="formValue">
                        <input id="file" name="file" type="file" accept=".epub" datacol="yes" checkexpession="NotNull"/>
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
        $("#loading").show();
        window.setTimeout(function () {
            var fileData = $('#file')[0].files;
            var formData = new FormData();
            formData.append('metaId', $('#metaId').val());
            formData.append('publishDate', $('#publishDate').val());
            formData.append('file', fileData[0]);
            $.ajax({
                url: RootPath() + "/book/epubChapterInsert",
                type: "POST",
                data: formData,
                cache: false,
                processData: false,
                contentType: false,
                success: function (data) {
                    $("#loading").hide();
                    if (data == "success") {
                        top.frames[tabiframeId()].location.reload();
                        tipDialog("保存成功！", 3, 1);
                        closeDialog();
                    } else if (data == "id_0") {
                        tipCss('图书', 'metaId不存在!');
                    } else if (data == "id_null") {
                        tipCss('图书', 'metaId不能为空!');
                    } else if (data == "exist") {
                        tipCss('图书', '图书章节内容已存在!');
                    } else {
                        tipCss('图书', '保存失败!联系管理员');
                    }
                },
                error: function (data) {
                    $("#loading").hide();
                    Loading(false);
                    alertDialog(data.responseText, -1);
                }
            });
        }, 200);
    }
</script>
</html>
