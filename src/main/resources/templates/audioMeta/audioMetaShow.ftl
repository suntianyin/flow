<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <title>音频元数据详情</title>
</head>
<body>
<table id="table-list"
       class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
    <tbody>
        <#list entityList as list>
        <tr class="gradeA odd" role="row">
            <td>${list.filedName }</td>
            <td>${list.filedDesc!'' }</td>
            <td>${list.filedValue!'' }</td>
        </tr>
        </#list>
    </tbody>
</table>
</body>
</html>