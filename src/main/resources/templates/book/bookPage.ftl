<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>图书分页数据</title>
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

        });

        //检索
        function btn_Search() {
            var metaid = $("#metaid").val();
            window.location.href = "bookPage?metaid=" + metaid;
        }

        //页码内容详情展示
        function bookPageContentDetail(metaid, pageid) {
            var url = "/book/bookPageContentDetail?metaid="+metaid+"&pageid="+pageid;
            openDialog(url, "bookPageContentDetail", "页面内容详情 - 第" + pageid + " 页", 800, 500, function (iframe) {
                top.frames[iframe].closeDialog();
            });
        }
    </script>
</head>
<body>
<div id="layout" class="layout">
    <!--中间-->
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>图书分页数据<span id="CenterTitle"></span></div>
        </div>

        <!--列表-->
        <div id="grid_List">
            <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
                <table border="0" class="form-find" style="height: 45px;">
                    <tr>
                        <th>图书ID：</th>
                        <td>
                            <input id="metaid" type="text" value="${tbMetaid!'' }" class="txt" style="width: 200px"/>
                        </td>

                        <td>
                            <input id="btnSearch" type="button" class="btnSearch" value="搜 索" onclick="btn_Search()"/>
                        </td>
                    </tr>
                </table>
            </div>
            <div  style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
                <table border="0" class="form-find" style="height: 45px;">
                    <tr>
                        <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<label style="color: blue;" class="txt"><b>图书信息</b></label></td>
                        <th>图书ID：</th>
                        <td>
                            <input id="tbMetaid" type="text" value="${tbMetaid!'' }" class="txt" style="width: 200px"/>
                        </td>
                        <th>书名：</th>
                        <td>
                            <input id="tbTitle" type="text" value="${tbTitle!'' }" class="txt" style="width: 200px;"/>
                        </td>
                        <th>作者：</th>
                        <td>
                            <input id="tbCreator" type="text" value="${tbCreator!'' }" class="txt" style="width: 200px"/>
                        </td>
                        <th>总页数：</th>
                        <td>
                            <input id="tbTotalPageNum" type="text" value="${tbTotalPageNum!'' }" class="txt" style="width: 200px"/>
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
                            <th width="33%">页码</th>
                            <th width="34%">页面内容</th>
                            <th width="33%">创建时间</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list bookPageList as list>
                        <tr class="gradeA odd" role="row">
                            <td>${(list.pageId)!'' }</td>
                            <td><input type="button" value="内容详情" onclick="bookPageContentDetail('${tbMetaid!'' }', ${list.pageId })"/></td>
                            <td>${(list.createTime?string('yyyy-MM-dd HH:mm:ss'))!'' }</td>

                        </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
                <#--<ul class="pagination" style="float:right;" id="pagination"></ul>-->
            </div>
        </div>
    </div>
</div>
</body>
</html>
