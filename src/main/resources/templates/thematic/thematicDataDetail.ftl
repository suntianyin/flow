<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>


    <title>论题信息数据</title>
    <script type="text/javascript">
        <#--$(function () {-->
            <#--$(document).keypress(function (e) {-->
                <#--//回车键事件-->
                <#--if (e.which == 13) {-->
                    <#--$("#metaid").focus();-->
                    <#--$("#metaid").select();-->
                    <#--$("#btnSearch").click();-->
                <#--}-->
            <#--});-->
            <#--//排序-->
            <#--var sortColumns = "";-->
            <#--window.simpleTable = new SimpleTable('form-list', '1', '10', sortColumns);-->

            <#--var pathurl = "bookMeta?s_metaid=" + metaid-->
                    <#--+ "&s_title=" + title + "&s_creator=" + creator-->
                    <#--+ "&s_publisher=" + publisher + "&s_isbn=" + isbn + "&s_isbnVal=" + isbnVal;-->
            <#--var totalPages = 1;-->
            <#--var currentPages = 1;-->
            <#--<#if page??>-->
                <#--totalPages = ${page.toal?c};-->
                <#--currentPages = ${page.pageNum?c}+1;-->
            <#--</#if>-->
            <#--jqPaging(pathurl, totalPages, currentPages);-->
        <#--});-->

        function checkThematicData(id) {
            loading();
            window.location.href = "thematicCheck?" + "id=" + id;
        }
    </script>
</head>
<body>
<!--工具栏-->
<div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
    <input type="button" onclick="javascript:window.location.href='thematicInfoDisplay'" value="返回" class="tools_btn">
</div>
<div class="panel-body">
    <div class="row">
        <table id="table-list"
               class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
            <thead>
            <tr role="row">
                <th>论题信息数据id</th>
                <#--<th>论题id</th>-->
                <th>metaId</th>
                <th>书名</th>
                <th>作者</th>
                <th>ISBN</th>
                <th>ISBN13</th>
                <th>操作人</th>
                <th>创建时间</th>
                <th>最后更新时间</th>
            </tr>
            </thead>
            <tbody>
            <#list thematicSeriesDataList as thematicSeriesData>
            <tr class="gradeA odd" role="row">
                <td>${thematicSeriesData.id! ''}</td>
                <#--<td>${thematicSeriesData.thematicId! ''}</td>-->
                <td>${thematicSeriesData.metaId! ''}</td>
                <td>${thematicSeriesData.title! ''}</td>
                <td>${thematicSeriesData.author! ''}</td>
                <td>${thematicSeriesData.isbn! ''}</td>
                <td>${thematicSeriesData.isbn13! ''}</td>
                <td>${thematicSeriesData.operator! ''}</td>
                <td>${thematicSeriesData.createTime?string('yyyy-MM-dd HH:mm:ss')! ''}</td>
                <td>${thematicSeriesData.updateTime?string('yyyy-MM-dd HH:mm:ss')! ''}</td>
            </tr>
            </#list>
            </tbody>
        </table>
    </div>
    <#--<ul class="pagination" style="float:right;" id="pagination"></ul>-->
</div>
</table>
</body>
</html>
