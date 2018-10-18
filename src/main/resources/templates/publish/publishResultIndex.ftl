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
        $(function () {
            //排序
            var sortColumns = "";
            window.simpleTable = new SimpleTable('form-list', '1', '10', sortColumns);
            var pathurl = "/flowadmin/publishResult/index?1=1";
            var totalPages = 1;
            var currentPages = 1;
            <#if page??>
                totalPages = ${pages?c};
                currentPages = ${pageNum?c};
            </#if>
            jqPaging(pathurl, totalPages, currentPages);
        });
        function btn_back() {
            window.location.href="${ctx}/publish/homeIndex";
        }
    </script>
</head>
<body>
<div class='load-circle' style="display: none;"></div>
<div id="layout" class="layout">
    <!--中间-->
    <div class="layoutPanel layout-center">
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a id="lr-xmlAdd" href="javascript:;" title="返回" onclick="btn_back()"
                   class="tools_btn"><span><i class="fa fa-plus"></i>&nbsp;返回</span></a>
            </div>
        </div>
        <!--列表-->
        <div class="panel-body">
            <div class="row">
                <table id="table-list"
                       class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                    <thead>
                    <tr role="row">
                        <th>记录id</th>
                        <th>操作数据类型</th>
                        <th>操作数据id</th>
                        <th>操作人</th>
                        <th>操作时间</th>
                        <th>操作结果</th>
                        <th>数据来源</th>
                        <th>创建时间</th>
                        <th>更新时间</th>
                        <th>是否同步</th>
                    </tr>
                    </thead>
                    <tbody>
                        <#if publishResultList?? >
                            <#list publishResultList as list>
                                <tr class="gradeA odd" role="row">
                                    <td align="center">${list.id! ''}</td>
                                    <td align="center">${list.operateDataType! '' }</td>
                                    <td align="center">${list.metaId! '' }</td>
                                    <td align="center">${list.operator! '' }</td>
                                    <td align="center">${list.operateTime?string('yyyy-MM-dd HH:mm:ss')! ''}</td>
                                    <td align="center">${list.operateResult! '' }</td>
                                    <td align="center">${list.dataSource! '' }</td>
                                    <td align="center">${(list.createTime?string('yyyy-MM-dd HH:mm:ss'))! ''}</td>
                                    <td align="center">${(list.updateTime?string('yyyy-MM-dd HH:mm:ss'))! ''}</td>
                                    <td align="center">${list.hasSync! '' }</td>
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
