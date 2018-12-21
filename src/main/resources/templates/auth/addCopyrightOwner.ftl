<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
<#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <meta charset="UTF-8">
    <title>添加版权所有者信息</title>
    <script type="text/javascript">
        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        }
        //保存事件
        function AcceptClick() {
            $("#loading").show();
            Loading(true, "正在提交数据...");
            if (isNull($('#name').val())) {
                tipDialog("版权所有者名称 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#pinyin').val())) {
                tipDialog("版权所有者拼音 不能为空！", 3, -1);
                return;
            }
            // if (isNull($('#remark').val())){
            //     tipDialog("版权所有者简介 不能为空！", 3, -1);
            //     return;
            // }

            window.setTimeout(function () {
                var formObj = $("#form1");
                var postData = JSON.stringify(formObj.serializeJson());
                $.ajax({
                    url: "${ctx}/copyrightOwner/add",
                    type: "POST",
                    contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                    data: postData,
                    async: false,
                    success: function (data) {
                        tipDialog("添加版权所有者成功！", 3, 1);
                        top.frames[tabiframeId()].location.reload();
                        closeDialog();
                    },
                    error: function (data) {
                        Loading(false);
                        alertDialog("输入信息有误，请核实后提交", -1);
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
            <div>添加版权所有者信息<span id="CenterTitle"></span></div>
        </div>
        <form id="form1" enctype="multipart/form-data" method="post">
            <table border="0" class="form-find" style="height: 45px;">
                <tr>
                    <td>版权所有者名称:</td>
                    <td>
                        <input id="name" name="name" type="text" class="txt" style="width: 300px"/>
                        &nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>版权所有者拼音:</td>
                    <td>
                        <input id="pinyin" name="pinyin" type="text" class="txt" style="width: 300px"/>
                        &nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>版权所有者简介:</td>
                    <td>
                        <textarea id="remark" name="remark" maxlength="80" class="txtArea" rows="5" type="text"  style="width: 300px"></textarea>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
</body>
</html>