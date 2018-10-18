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

    </script>
</head>
<body>
<form action="thematicEditSave" method="post">
<table id="table-list" class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
    <thead>
    <tr class="gradeA odd" role="row" align="center">
        <td colspan="2">
            <h4>论题信息编辑</h4>
        </td>
    </tr>
    </thead>
    <tbody>
    <tr class="gradeA odd" role="row">
        <td>论题id</td>
        <td>[系统自动生成]<input type="hidden" name="id" value="${thematicSeries.id}"></td>
    </tr>
    <tr class="gradeA odd" role="row">
        <td>名称</td>
        <td><input type="text" id="title" name="title" value="${thematicSeries.title}"></td>
    </tr>
    <tr class="gradeA odd" role="row">
        <td>数据来源</td>
        <td><input type="text" id="dataSource" name="dataSource" value="${thematicSeries.dataSource}"></td>
    </tr>
    <tr class="gradeA odd" role="row">
        <td>整理人</td>
        <td><input type="text" id="collator" name="collator" value="${thematicSeries.collator}"></td>
    </tr>
    <tr class="gradeA odd" role="row">
        <td>操作人</td>
        <td><input type="text" id="operator" name="operator" value="${thematicSeries.operator}"></td>
    </tr>
    <tr class="gradeA odd" role="row">
        <td><input type="submit" value="修改" class="tools_btn"></td>
        <td><input type="submit" value="返回" onclick="javascript:window.history.back();" class="tools_btn"></td>
    </tr>
    </tbody>
</table>
</form>
</body>
</html>
