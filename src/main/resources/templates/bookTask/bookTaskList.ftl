<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>图书任务列表</title>
    <script type="text/javascript">

        $(function () {
            $(document).keypress(function (e) {
                //回车键事件
                if (e.which == 13) {
                    $("#metaid").focus();
                    $("#metaid").select();
                    $("#btnSearch").click();
                }
            });
            //排序
            var sortColumns = "";
            window.simpleTable = new SimpleTable('form-list', '1', '10', sortColumns);
            //分页
            var pathUrl = "showTaskList";
            var totalPages = 1;
            var currentPages = 1;
            <#if page??>
                totalPages = ${pages?c};
                currentPages = ${pageNum?c};
            </#if>
            jqPaging(pathUrl, totalPages, currentPages);
        });

        //检索
        function btn_Search() {
            var metaId = $("#metaId").val();
            var title = $("#title").val();
            var creator = $("#creator").val();
            var publisher = $("#publisher").val();
            var isbn = $("#isbn").val();
            var isbnVal = $("#isbnVal").val();
            var hasFlow = $("#hasFlow").val();
            var drid = $("#drid").val();
            loading()
            window.location.href = "bookMeta?metaId=" + metaId
                    + "&title=" + title + "&creator=" + creator
                    + "&publisher=" + publisher + "&isbn=" + isbn
                    + "&isbnVal=" + isbnVal + "&hasFlow=" + hasFlow
                    + "&drid=" + drid;

        }

        //显示详情
        function onShowTaskInfoClick(taskId) {
            var url = "/bookTask/showTaskInfo?taskId=" + taskId;
            AddTabMenu('showTaskInfo', url, '任务列表详情', null, 'true', 'true');
        }

    </script>
</head>
<body>
<div class='load-circle' style="display: none;"></div>
<div id="layout" class="layout">
    <!--中间-->
    <div class="layoutPanel layout-center">
        <!--列表-->
        <div id="grid_List">
            <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
                <table border="0" class="form-find" style="height: 45px;">
                    <tr>
                    <#--<th>DRID：</th>
                        <td>
                            <#if drid??>
                                <input id="drid" type="text" value="${drid?c }" class="txt" style="width: 200px"/>
                            <#else >
                                <input id="drid" type="text" value="" class="txt" style="width: 200px"/>
                            </#if>
                        </td>
                        <td>
                            <input id="btnSearch" type="button" class="btnSearch" value="搜 索" onclick="btn_Search()"/>
                        </td>-->
                    </tr>
                </table>
            </div>
            <div class="panel-body">
                <div class="row">
                    <table id="table-list"
                           class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                        <thead>
                        <tr role="row">
                            <th>任务ID</th>
                            <th>任务路径</th>
                            <th>创建时间</th>
                            <th>修改时间</th>
                            <th>状态</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#if bookTaskList?? >
                            <#list bookTaskList as list>
                                <tr class="gradeA odd" role="row">
                                    <td>${list.id!'' }</td>
                                    <td>${list.taskPath! '' }</td>
                                    <td>${list.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                    <td>
                                        <#if list.updateTime ??>
                                            ${list.updateTime?string('yyyy-MM-dd HH:mm:ss')}
                                        </#if>
                                    </td>
                                    <td>
                                        <#if list.status ??>
                                            <#if list.status == 0>
                                                任务创建失败
                                            <#elseif list.status == 1>
                                                进行中
                                            <#else >
                                                已完成
                                            </#if>
                                        <#else>
                                            任务创建失败
                                        </#if>
                                    </td>
                                    <td>
                                        <a style="cursor:pointer;"
                                           onclick="onShowTaskInfoClick('${list.id!'' }');">详情&nbsp;</a>
                                    </td>
                                </tr>
                            </#list>
                        </#if>
                        </tbody>
                    </table>
                </div>
                <ul class="pagination" style="float:right;" id="pagination"></ul>
            </div>
        </div>
    </div>
</div>
</body>
</html>
