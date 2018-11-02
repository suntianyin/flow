<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>

    <title>出版社元数据</title>
    <script type="text/javascript">
        function btn_back() {
            window.location.href="${ctx}/publisher/index";
        }
        // 修改按钮
        function btn_update() {
            loading();
            window.location.href = 'edit?id=${id!''}';
        }
    </script>
</head>
<body>
<div class='load-circle' style="display: none;"></div>
<table id="table-list"
       class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
    <thead>
    <tr class="gradeA odd" role="row">
        <td>
            <input type="button" onclick="window.history.back();" value="返回">
        </td>
        <td>
            <button onclick="btn_update()">修改</button>
        </td>
    </tr>
    <tr role="row">
        <th width="100px">字段名</th>
        <th width="100px">字段值</th>
    </tr>
    </thead>
    <tbody>
    <#list publisherMap?keys as key>
        <tr class="gradeA odd" role="row">
            <td>${key! ''}</td>
            <td>${publisherMap[key]! ''}</td>
        </tr>
    </#list>
    <tr class="gradeA odd" role="row">
        <td>
            <input type="button" onclick="window.history.back();" value="返回">
        </td>
        <td>
            <button onclick="btn_update()">修改</button>
        </td>
    </tr>
    </tbody>
</table>
</body>
</html>
