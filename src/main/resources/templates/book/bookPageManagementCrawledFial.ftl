<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>采集失败流式内容</title>
    <script type="text/javascript">
        $(function () {
            var metaId = $("#metaId").val();
            var pathurl = "bookFailure?metaId=" + metaId;
            var totalPages = ${pages?c};
            var currentPages = ${pageNum?c};
            jqPaging(pathurl, totalPages, currentPages);
        });

        function btn_Again() {
            var url = "/book/autoFetchPageDataAgain2";
            loading();
            $.ajax({
                url: RootPath() + url,
                type: "get",
                dataType: "json",
                cache: false,
                contentType: false,
                async: false,
                success: function (data) {
                    alertDialog(data.msg, data.status);
                    window.location.href = "bookFailure";
                },
                error: function (data) {
                    alertDialog(data.msg, data.status);
                    $(".load-circle").hide();
                }
            });
        }

        function btn_ShutdownNow() {
            var url = "/book/shutdownNow2";
            loading();
            $.ajax({
                url: RootPath() +url,
                type: "get",
                dataType: "json",
                cache: false,
                contentType: false,
                async: false,
                success: function (data) {
                    alertDialog(data.msg, data.status);
                    window.location.href = "pageCrawled";
                },
                error: function (data) {
                    alertDialog(data.msg, data.status);
                    $(".load-circle").hide();
                }
            });
        }
        function btn_flush() {
            window.location.href = "bookFailure";
        }
        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        };

        //单个删除
        function pageCrawledQueuesDelete(id) {
            var note = "注：您确定要删除当前采集加密ID？";
            if (!isNull(id)) {
                note = "注：您确定要删除 采集加密ID为：" + id + " 的信息？";
            }
            confirmDialog("温馨提示", note, function (r) {
                if (r) {
                    window.location.href = "${ctx}/book/pageCrawledQueuesDelete?id=" + id;
                }
            })
        }
        function btn_Search() {
            var metaId = $("#metaId").val();
            loading()
            window.location.href = "bookFailure?metaId=" + metaId;
        }
    </script>
</head>
<body>
<div id="layout" class="layout">
    <!--中间-->
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>采集失败流式内容<span id="CenterTitle"></span></div>
        </div>
        </div>
    <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
        <div class="PartialButton">
            <a  title="返回" onclick="javascript:window.location.href='${ctx}/book/bookPageManagement'" class="tools_btn"><span><i
                            class="fa fa-backward"></i>&nbsp;返回</span></a>
            <div class="tools_separator"></div>
            <a  title="刷新" onclick="btn_flush()" class="tools_btn"><span><i
                            ></i>&nbsp;刷新</span></a>
            <div class="tools_separator"></div>
            <a  title="重新采集失败内容" onclick="btn_Again()" class="tools_btn"><span><i
                            ></i>&nbsp;重新采集失败内容</span></a>
            <div class="tools_separator"></div>
            <a  title="停止采集" onclick="btn_ShutdownNow()" class="tools_btn"><span><i
                            ></i>&nbsp;停止采集</span></a>
        </div>
    </div>
    <div id="grid_List">
        <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
            <table border="0" class="form-find" style="height: 45px;">
                <tr>
                    <th>图书ID：</th>
                    <td>
                        <input id="metaId" type="text" value="${metaId!'' }" class="txt" style="width: 200px"/>
                    </td>
                    <td>
                        <input id="btnSearch" type="button" class="btnSearch" value="搜 索" onclick="btn_Search()"/>
                    </td>
                </tr>
            </table>
        </div>
        <div class="panel-body">
            <div class="row">
                <table id="table-list"
                       class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                    <thead>
                    <tr role="row">
                        <th width="25%">ID</th>
                        <th width="25%">页面</th>
                        <th width="25%">描述</th>
                        <#--<th width="25%">操作</th>-->
                    </tr>
                    </thead>
                    <tbody>
                        <#list pageCrawledTemps as list>
                        <tr class="gradeA odd" role="row">
                            <td align="center">${(list.id)!'' }</td>
                            <td align="center">${(list.page)!'' }</td>
                            <td align="center">${(list.desce)!'' }</td>
                            <#--<td align="center"><input type="button" value="删除"-->
                                                      <#--onclick="delete('${list.id }')"/></td>-->
                        </tr>
                        </#list>
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
