<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>修改版权所有者信息</title>
    <script type="text/javascript">
        $(function () {
            $("#status").val("${(copyrightOwner.status)!'' }");
        });


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
                        url: "${ctx}/copyrightOwner/update",
                        type: "POST",
                        data: postData,
                        contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                        async: false,
                        success: function (data) {
                            tipDialog("修改版权所有者成功！", 3, 1);
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
        <div>修改版权所有者信息<span id="CenterTitle"></span></div>
           </div>
        <form id="form1">
        <#--<form id="form1" enctype="multipart/form-data" action="${ctx}/publisher/doedit" method="post" >-->
            <table border="0" class="form-find" style="height: 45px;">
                <tr>
                    <input id="id" name="id" value="${(copyrightOwner.id)!''}" type="hidden" class="txt" style="width: 300px"/>
                    <td>版权所有者名称:</td>
                    <td>
                        <input id="name" name="name" type="text" value="${(copyrightOwner.name)!''}" class="txt" style="width: 300px"/>
                        &nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>版权所有者拼音:</td>
                    <td>
                        <input id="pinyin" name="pinyin" type="text" value="${(copyrightOwner.pinyin)!''}" class="txt" style="width: 300px"/>
                        &nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>版权所有者简介:</td>
                    <td>
                        <textarea id="remark" name="remark" type="text"  maxlength="80" class="txtArea" rows="5" style="width: 300px"/>${(copyrightOwner.remark)!''}</textarea>
                    </td>
                </tr>
                <tr>
                    <td>版权所有者状态:</td>
                    <td>
                        <select id="status" name="status" underline="true" style="width: 307px; height: 24px;">
                            <option value="0">启用</option>
                            <option value="1">禁用</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
             </table>
        </form>
    </div>
</div>
</body>
</html>