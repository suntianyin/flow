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
        // 提交按钮
        function btn_save() {
            if (confirm("确认保存吗？") == true) {
                $("#dataForm").submit();
                alert("保存成功！");
                $(".load-circle").hide();
            }
        }
    </script>
</head>
<body>
<div class='load-circle' style="display: none;"></div>
<form id="dataForm" action="update" method="post">
    <table id="table-list"
           class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
        <thead>
        <tr class="gradeA odd" role="row">
            <td>
                <input type="button" onclick="window.history.back();" value="返回">
            </td>
            <td>
                <input type="button" onclick="btn_save()" value="保存"/>
            </td>
        </tr>
        <tr role="row">
            <th>字段名</th>
            <th>meta_data库</th>
            <th width="781.5px">temp库</th>
        </tr>
        </thead>
        <tbody>
        <#list compareEntityList as compareEntity>
            <#if compareEntity.info == '0'>
                <tr class="gradeA odd" role="row" style="background-color: red">
                    <td>${compareEntity.fieldName}</td>
                    <#if compareEntity.fieldName == '流式目录' || compareEntity.fieldName == '标签' || compareEntity.fieldName == '样式url'>
                        <td>${compareEntity.metaValue}</td>
                        <td><textarea name="${compareEntity.fieldName}" id="${compareEntity.fieldName}" cols="100" rows="5" readonly="readonly" hidden="hidden">${compareEntity.tempValue}</textarea>${compareEntity.tempValue}</td>
                    <#elseif compareEntity.fieldName == '是否有cebx' || compareEntity.fieldName = '是否有流式内容' || compareEntity.fieldName = '是否发布' ||  compareEntity.fieldName = '流式内容是否优化' || compareEntity.fieldName = '更新时间' || compareEntity.fieldName = '创建时间' || compareEntity.fieldName = '是否公版'>
                        <td>${compareEntity.metaValue}</td>
                        <td><input type="text" name="${compareEntity.fieldName}" value="${compareEntity.tempValue}" readonly="readonly" hidden="hidden">${compareEntity.tempValue}</td>
                    <#elseif compareEntity.fieldName == '作者简介' || compareEntity.fieldName = '内容提要' || compareEntity.fieldName = '序言' || compareEntity.fieldName = '后记' || compareEntity.fieldName = '版式目录'>
                        <td>${compareEntity.metaValue}</td>
                        <td><textarea name="${compareEntity.fieldName}" id="${compareEntity.fieldName}" cols="100" rows="10">${compareEntity.tempValue}</textarea></td>
                    <#else >
                        <td>${compareEntity.metaValue}</td>
                        <td><input type="text" name="${compareEntity.fieldName}" value="${compareEntity.tempValue}"></td>
                    </#if>
                </tr>
            <#else>
                <tr class="gradeA odd" role="row">
                    <td>${compareEntity.fieldName}</td>
                    <#if compareEntity.fieldName == '流式目录' || compareEntity.fieldName == '标签' || compareEntity.fieldName == '样式url'>
                        <td>${compareEntity.metaValue}</td>
                        <td><textarea name="${compareEntity.fieldName}" id="${compareEntity.fieldName}" cols="100" rows="5" readonly="readonly" hidden="hidden">${compareEntity.tempValue}</textarea>${compareEntity.tempValue}</td>
                    <#elseif compareEntity.fieldName == '是否有cebx' || compareEntity.fieldName = '是否有流式内容' || compareEntity.fieldName = '是否发布' ||  compareEntity.fieldName = '流式内容是否优化' || compareEntity.fieldName = '更新时间' || compareEntity.fieldName = '创建时间' || compareEntity.fieldName = '是否公版'>
                        <td>${compareEntity.metaValue}</td>
                        <td><input type="text" name="${compareEntity.fieldName}" value="${compareEntity.tempValue}" readonly="readonly" hidden="hidden">${compareEntity.tempValue}</td>
                    <#elseif compareEntity.fieldName == '作者简介' || compareEntity.fieldName = '内容提要' || compareEntity.fieldName = '序言' || compareEntity.fieldName = '后记' || compareEntity.fieldName = '版式目录'>
                        <td>${compareEntity.metaValue}</td>
                        <td><textarea name="${compareEntity.fieldName}" id="${compareEntity.fieldName}" cols="100" rows="10">${compareEntity.tempValue}</textarea></td>
                    <#else >
                        <td>${compareEntity.metaValue}</td>
                        <td><input type="text" name="${compareEntity.fieldName}" value="${compareEntity.tempValue}"></td>
                    </#if>
                </tr>
            </#if>
        </#list>
        <tr class="gradeA odd" role="row">
            <td>
                <input type="button" onclick="window.history.back();" value="返回">
            </td>
            <td>
                <input type="button" onclick="btn_save()" value="保存"/>
            </td>
        </tr>
        </tbody>
    </table>
</form>
</body>
</html>
