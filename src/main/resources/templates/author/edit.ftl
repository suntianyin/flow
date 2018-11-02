<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>

    <title>图书元数据</title>
    <script type="text/javascript">
        // // 提交按钮
        // function btn_save() {
        //     if (confirm("确认保存吗？") == true) {
        //         $("#dataForm").submit();
        //         alert("保存成功！");
        //         $(".load-circle").hide();
        //     }
        // }
        //保存事件
        function AcceptClick() {
            $("#loading").show();
            Loading(true, "正在提交数据...");
            window.setTimeout(function () {
                var formObj = $("#form1");
                var postData = JSON.stringify(formObj.serializeJson());
                $.ajax({
                    url: "${ctx}/author/update",
                    type: "POST",
                    data: postData,
                    contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                    async: false,
                    success: function (data) {
                        tipDialog("修改作者信息成功！", 3, 1);
                        // top.frames[tabiframeId()].location.reload();
                        // closeDialog();
                        $(".load-circle").hide();
                        window.location.href = "index?id=" + '' + "&title=" + '';
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
<div class='load-circle' style="display: none;"></div>
<form id="form1">
    <table id="table-list"
           class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
        <thead>
        <tr class="gradeA odd" role="row">
            <td>
                <input type="button" onclick="window.history.back();" value="返回">
            </td>
            <td>
                <input type="button" onclick="AcceptClick()" value="保存"/>
            </td>
        </tr>
        <tr role="row">
            <th>字段名</th>
            <th>字段值</th>
        </tr>
        </thead>
        <tbody>
        <#list entityInfoList as entityInfo>
        <tr class="gradeA odd" role="row">

            <#if entityInfo.info == '姓名类型'>
                <td>${entityInfo.info! ''}</td>
                <td>
                    <select id="titleType" name="titleType" underline="true">
                        <option value="0">姓名</option>
                        <option value="1">字</option>
                        <option value="2">号</option>
                        <option value="3">别名</option>
                        <option value="4">笔名</option>
                        <option value="5">艺名</option>
                        <option value="6">网名</option>
                    </select>
                </td>
            <#elseif entityInfo.info == '性别'>
            <td>${entityInfo.info! ''}</td>
            <td>
                <select id="sexCode" name="sexCode" underline="true">
                    <option value="0">男</option>
                    <option value="1">女</option>
                </select>
            </td>
            <#elseif entityInfo.info == '作者类型'>
            <td>${entityInfo.info! ''}</td>
            <td>
                <select id="type" name="type" underline="true">
                    <option value="0">个人</option>
                    <option value="1">团体</option>
                    <option value="2">会议</option>
                    <option value="3">佛道人物</option>
                    <option value="4">古代帝王</option>
                </select>
            </td>
            <#elseif entityInfo.info == '是否卒于50年'>
            <td>${entityInfo.info! ''}</td>
            <td>
                <select id="dieOver50" name="dieOver50" underline="true">
                    <option value="0">否</option>
                    <option value="1">是</option>
                </select>
            </td>
            <#elseif entityInfo.info == '最后更新时间'||entityInfo.info == '创建时间'>
            <td>
            <input type="date" name="${entityInfo.fieldName! ''}" value="${entityInfo.metaValue! ''}" readonly="readonly" hidden="hidden">
            </td>
            <#elseif entityInfo.info == '操作人'||entityInfo.info == '作者id'>
            <td>
            <input type="text" name="${entityInfo.fieldName! ''}" value="${entityInfo.metaValue! ''}" readonly="readonly" hidden="hidden">
            </td>
            <#elseif entityInfo.info == '简介'>
            <td>${entityInfo.info! ''}</td>
                <td><textarea name="${entityInfo.fieldName! ''}" cols="100"
                              rows="10">${entityInfo.metaValue! ''}</textarea></td>
            <#else >
                                    <td>${entityInfo.info! ''}</td>
            <td><input type="text" name="${entityInfo.fieldName! ''}" value="${entityInfo.metaValue! ''}"></td>
            </#if>
        </tr>
        </#list>
        <tr class="gradeA odd" role="row">
            <td>
                <input type="button" onclick="window.history.back();" value="返回">
            </td>
            <td>
                <input type="button" onclick="AcceptClick()" value="保存"/>
            </td>
        </tr>
        </tbody>
    </table>
</form>
</body>
</html>
