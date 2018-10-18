<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <script src="${ctx}/js/jsPage.js"></script>
    <#include "../common/meta.ftl">
    <link href="${ctx}/css/org.css" rel="stylesheet"/>
    <title>发布到爱读爱看</title>
</head>
<body>
<form id="form1" enctype="multipart/form-data" method="post">
    <div class="ScrollBar" style="margin: 1px; overflow: hidden;">
        <div id="basic" class="tabPanel">
            <table class="form">
                <input id="ids" name="ids" value="${ids}" type="hidden">
                <tr>
                    <th class="formTitle">是否流式展示：</th>
                    <td class="formValue">
                        <select id="original" name="original" class="txtselect">
                            <option value="false" selected="selected">否</option>
                            <option value="true">是</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">是否流式优化：</th>
                    <td class="formValue">
                        <select id="optimized" name="optimized" class="txtselect">
                            <option value="true" selected="selected">是</option>
                            <option value="false">否</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">来源库：</th>
                    <td class="formValue">
                        <select id="platform" name="platform" class="txtselect">
                            <option value="flowplatform" selected="selected">流式服务</option>
                            <option value="aiduaikan">爱读爱看</option>
                            <option value="shuyuan">书苑</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">目标库：</th>
                    <td class="formValue">
                        <input id="libId" name="libId" value="flowplatform" type="text" class="txt"/>
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
        $("#loading").show();
        window.setTimeout(function () {
            var formData = new FormData();
            formData.append('ids', $('#ids').val());
            formData.append('original', $('#original').val());
            formData.append('optimized', $('#optimized').val());
            formData.append('platform', $('#platform').val());
            formData.append('libId', $('#libId').val());
            $.ajax({
                url: RootPath() + "/book/batchPubInsert",
                type: "POST",
                data: formData,
                cache: false,
                processData: false,
                contentType: false,
                success: function (data) {
                    $("#loading").hide();
                    tipDialog(data + "本图书发布成功！", 3, 1);
                    closeDialog();
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
