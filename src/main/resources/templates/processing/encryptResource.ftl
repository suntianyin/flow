<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>加密任务详情</title>
    <script type="text/javascript">

        $(function () {
            var id = $("#id").val().trim();
            var pathurl = "${ctx}/processing/encrypt/encryptResourceIndex?id="+id;
            var totalPages = ${pages!1};
            var currentPages = ${pageNum!1};
            jqPaging(pathurl, totalPages, currentPages);
        });
        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        }


    </script>
</head>
<body>
<div class='load-circle' style="display: none;"></div>
<div id="layout" class="layout">
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>加密任务详情管理<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a  title="返回" onclick="javascript:window.location.href='${ctx}/processing/encrypt/index'" class="tools_btn"><span><i
                                class="fa fa-backward"></i>&nbsp;返回</span></a>
                <div class="tools_separator"></div>
            </div>
        </div>
        <div class="panel-body">
            <div class="row">
                <table id="table-list"
                       class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                    <thead>
                    <tr role="row">
                        <th >metaid</th>
                        <th >标题</th>
                        <th >作者</th>
                        <th >出版社</th>
                        <th >isbn</th>
                        <th >加密状态</th>
                        <th >完成时间</th>
                        <th >操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#if encryptResourceList??>
                        <#list encryptResourceList as list>
                            <input type="hidden" id="id" value="${(list.id)! '' }">
                            <tr class="gradeA odd" role="row">
                            <td align="center">${(list.metaid)!''}</td>
                            <td align="center">${(list.title)! '' }</td>
                            <td align="center">${(list.author)! '' }</td>
                            <td align="center">${(list.publisher)!'' }</td>
                            <td align="center">${(list.isbn)! '' }</td>
                            <td align="center">${(list.state.getDesc())! '' }</td>
                            <td align="center">${(list.finishTime?datetime)! '' }</td>
                            <td align="center">
                        <a href="javascript:void(0);" onclick="encrypt()">加密</a>
                            </td>
                            </tr>
                        </#list>
                    </#if>
                    </tbody>
                </table>
            </div>
            <ul class="pagination">
                <li>每页 ${pageSize!0} 条记录，共 ${pages!0} 页，共 ${total!0} 条记录</li>
            </ul>
            <ul class="pagination" style="float:right;" id="pagination"></ul>
        </div>
    </div>
</div>
</body>
</html>
