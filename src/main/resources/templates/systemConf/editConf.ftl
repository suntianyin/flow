<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>修改系统参数数据</title>
    <script type="text/javascript">

        //保存事件
        function AcceptClick() {
            $("#loading").show();
            Loading(true, "正在提交数据...");
            window.setTimeout(function () {
                var formObj = $("#form1");
                var postData = JSON.stringify(formObj.serializeJson());
                $.ajax({
                    url: "${ctx}/systemConf/doedit",
                    type: "POST",
                    data: postData,
                    async: false,
                    contentType: "application/json;charset=UTF-8",
                    success: function (data) {
                        tipDialog("修改系统参数成功！", 3, 1);
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
            <div>修改系统参数数据<span id="CenterTitle"></span></div>
        </div>
        <form id="form1">
            <table border="0" class="form-find" style="height: 45px;">
                <tr>
                    <td>配置名称:</td>
                    <td>
                        <input id="id" name="id" type="hidden" value="${systemConf.id}" class="txt" style="width: 200px"/>
                        <input id="confName" name="confName" type="text" class="txt"
                               style="width: 200px" value="${systemConf.confName! ''}"/>
                    </td>
                </tr>
                <tr>
                    <td>配置键:</td>
                    <td>
                        <input id="confKey" name="confKey" type="text" class="txt"
                               style="width: 200px" value="${systemConf.confKey! ''}"/>
                    </td>
                </tr>
                <tr>
                    <td>配置值:</td>
                    <td>
                        <input id="confValue" name="confValue" type="text" class="txt"
                               style="width: 200px" value="${systemConf.confValue! ''}"/>
                    </td>
                </tr>
                    <td>内容描述:</td>
                    <td>
                        <input id="description" name="description" type="text" class="txt" style="width: 200px"
                               value="${systemConf.description! ''}"/>
                    </td>
                </tr>
                        <input id="createTime" name="createTime" type="hidden" class="txt Wdate"
                               value="${(systemConf.createTime?string("yyyy-MM-dd"))!'' }"
                               onfocus="WdatePicker({maxDate:'%y-%M-%d'})"/>
                        <input id="operator" name="operator" type="hidden" class="txt "
                               value="${systemConf.operator!'' }"/>
                        <input id="updateTime" name="updateTime" type="hidden" class="txt Wdate"
                               value="${(systemConf.updateTime?string("yyyy-MM-dd"))!'' }"
                               onfocus="WdatePicker({maxDate:'%y-%M-%d'})"/>
                </from>
            </table>
    </div>
</div>
</body>
</html>