<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>图书元数据发布</title>
    <script type="text/javascript">

        // 提交按钮
        function btn_publish() {
            if (confirm("确认发布吗？") == true) {
                loading();
                $.ajax({
                    url: 'commit?metaId=${metaId}',
                    type: "get",
                    dataType: "json",
                    cache: false,
                    success: function (data) {
                        alert("发布成功！");
                        $(".load-circle").hide();
                        window.location.href = "search?pageNumber=1&s_metaId=${metaId}";
                    },
                    error: function (data) {
                        alert("发布失败！");
                        $(".load-circle").hide();
                    }
                });
            }
        }

        // 修改按钮
        function btn_update() {
            loading();
            window.location.href = 'edit?metaId=${metaId}';
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
            <button onclick="btn_publish()">发布</button>
        </td>
        <td>
            <button onclick="btn_update()">修改</button>
        </td>
    </tr>
    <tr role="row">
        <th>字段名</th>
        <th>meta_data库</th>
        <th>temp库</th>
    </tr>
    </thead>
    <tbody>
    <#list compareEntityList as compareEntity>
        <#if compareEntity.info == '0'>
        <tr class="gradeA odd" role="row" style="background-color: red">
            <td>${compareEntity.fieldName}</td>
            <td>${compareEntity.metaValue}</td>
            <td>${compareEntity.tempValue}</td>
        </tr>
        <#else>
        <tr class="gradeA odd" role="row">
            <td>${compareEntity.fieldName}</td>
            <td>${compareEntity.metaValue}</td>
            <td>${compareEntity.tempValue}</td>
        </tr>
        </#if>
    </#list>
    <tr class="gradeA odd" role="row">
        <td>
            <input type="button" onclick="window.history.back();" value="返回">
        </td>
        <td>
            <button onclick="btn_publish()">发布</button>
        </td>
        <td>
            <button onclick="btn_update()">修改</button>
        </td>
    </tr>
    </tbody>
</table>
</body>
</html>
