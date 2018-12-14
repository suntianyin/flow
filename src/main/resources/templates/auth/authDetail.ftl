<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>

    <title>作者元数据</title>
    <script type="text/javascript">
        function btn_back() {
            window.location.href="${ctx}/author/index";
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
    <#--<tr class="gradeA odd" role="row">-->
        <#--<td>-->
            <#--<div class="PartialButton">-->
                <#--<a id="lr-leave" title="返回主页" onclick="btn_back()" class="tools_btn"><span><i class="fa fa-arrow-left"></i>&nbsp;返回</span></a>-->
            <#--</div>-->
        <#--</td>-->
    <#--</tr>-->
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
    <#list authorMap?keys as key>
        <tr class="gradeA odd" role="row">
            <td>${key! ''}</td>
            <td>${authorMap[key]! ''}</td>
        </tr>
    </#list>
    <tr class="gradeA odd" role="row">
    </tr>
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
