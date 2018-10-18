<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>

    <title>论题信息录入</title>
    <script type="text/javascript">
        function submitForm() {
            var title = $("#title").val();
            var operator = $("#operator").val();
            console.log("title:"+title);
            console.log("operator:"+operator);
            if (title == '' || title == null) {
                alert("标题不能为空！");
            }
            if (operator == '' || operator == null) {
                alert("整理人不能为空！");
            }
            if (title != '' && operator != '' && title != null && operator != null) {
                alert("上传成功！");
                $("#dataForm").submit();
            }
        }
    </script>
</head>
<body>
<form id="dataForm" action="thematicSave" method="post">
    <table id="table-list"
           class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
        <thead>
        <tr class="gradeA odd" role="row" align="center">
            <td colspan="2">
                <h3>论题信息录入</h3>
            </td>
        </tr>
        </thead>
        <tbody>
        <tr class="gradeA odd" role="row">
            <td>论题id</td>
            <td>[系统自动生成]</td>
        </tr>
        <tr class="gradeA odd" role="row">
            <td>论题名称</td>
            <td><input type="text" id="title" name="title"></td>
        </tr>
        <tr class="gradeA odd" role="row">
            <td>论题数据来源</td>
            <td><input type="text" id="dataSource" name="dataSource"></td>
        </tr>
        <tr class="gradeA odd" role="row">
            <td>论题整理人</td>
            <td><input type="text" id="collator" name="collator"></td>
        </tr>
        <tr class="gradeA odd" role="row">
            <td>论题操作人</td>
            <td><input type="text" id="operator" name="operator" readonly="readonly" value="admin"></td>
        </tr>
        <tr class="gradeA odd" role="row">
            <td colspan="2" align="center"><input type="button" value="添加" onclick="submitForm()">&nbsp;&nbsp;&nbsp;&nbsp;<input type="button"
                                                                                                          value="返回"
                                                                                                          onclick="javascript:window.history.back();">
            </td>
        </tr>
        </tbody>
    </table>
</form>
</body>
</html>
