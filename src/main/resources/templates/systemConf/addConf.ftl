<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
<#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <meta charset="UTF-8">
    <title>添加系统参数数据</title>
    <script type="text/javascript">

        //保存事件
        function AcceptClick() {
            $("#loading").show();
            Loading(true, "正在提交数据...");
            window.setTimeout(function () {
                var formObj = $("#form1");
                var postData = JSON.stringify(formObj.serializeJson());
                $.ajax({
                    url: "${ctx}/systemConf/dosave",
                    type: "POST",
                    data: postData,
                    async: false,
                    contentType: "application/json;charset=UTF-8",
                    success: function (data) {
                        tipDialog("添加系统参数成功！", 3, 1);
                        top.frames[tabiframeId()].location.reload();
                        closeDialog();
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
<div id="grid_List">
    <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
        <div class="btnbartitle">
            <div>添加系统参数数据<span id="CenterTitle"></span></div>
        </div>
        <form id="form1" enctype="multipart/form-data" action="${ctx}/systemConf/dosave" method="post">
            <table border="0" class="form-find" style="height: 45px;">

                <tr>
                    <td>配置名称:</td>
                    <td>
                        <input id="confName" name="confName" type="text" class="txt"
                               style="width: 200px"/>
                    </td>
                </tr>
                <tr>
                    <td>配置键:</td>
                    <td>
                        <input id="confKey" name="confKey" type="text" class="txt"
                               style="width: 200px"/>
                    </td>
                </tr>
                <tr>
                    <td>配置值:</td>
                    <td>
                        <input id="confValue" name="confValue" type="text" class="txt"
                               style="width: 200px"/>
                    </td>
                </tr>
                <tr>
                    <td>内容描述:</td>
                    <td>
                        <input id="description" name="description" type="text" class="txt" style="width: 200px"
                        />

                    </td>
                </tr>
                </from>
            </table>
        </form>
    </div>
</div>
</body>
</html>