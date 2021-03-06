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
        function btn_back() {
            loading();
            history.go(-1);
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
            <div class="PartialButton">
                <a id="lr-leave" title="返回" onclick="btn_back()" style="cursor:pointer;"><span><i class="fa fa-arrow-left"></i>&nbsp;返回</span></a>
            </div>
        </td>
    </tr>
    <tr role="row">
        <th>字段名</th>
        <th>字段值</th>
    </tr>
    </thead>
    <tbody>
    <#list bookSearchModelMap?keys as key>
        <tr class="gradeA odd" role="row">
            <td>${key! ''}</td>
            <td>${bookSearchModelMap[key]! ''}</td>
        </tr>
    </#list>
    <tr class="gradeA odd" role="row">
        <td>
            <div class="PartialButton">
                <a id="lr-leave" title="返回" onclick="btn_back()" style="cursor:pointer;"><span><i class="fa fa-arrow-left"></i>&nbsp;返回</span></a>
            </div>
        </td>
        <td></td>
    </tr>
    </tbody>
</table>
</body>
</html>
