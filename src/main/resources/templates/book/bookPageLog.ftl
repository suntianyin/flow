<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>采集日志数据查看</title>

    <style type="text/css">
        .comments {
            width: 100%; /*自动适应父布局宽度*/
            overflow: auto;
            word-break: break-all;
        }
    </style>
    <script type="text/javascript">
        $(function () {
            $(document).keypress(function (e) {
                //回车键事件
                if (e.which == 13) {
                    // $("#metaid").focus();
                    // $("#metaid").select();
                    $("#btnSearch").click();
                }
            });
        });

        //检索
        function btn_Search(type) {
            var time;
            var len;
            if (type == 1) {
                time = $("#time1").val();
                len = $("#len1").val();
            }
            if (type == 2) {
                time = $("#time2").val();
                len = $("#len2").val();
            }
            if (type == 3) {
                time = $("#time3").val();
                len = $("#len3").val();
            }

            window.location.href = "bookPageLog?len=" + len + "&time=" + time + "&type=" + type;
        }
    </script>
</head>
<body>
<div id="layout" class="layout">
    <!--中间-->
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>采集日志数据查看<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a title="返回" onclick="javascript:window.location.href='${ctx}/book/bookPageManagement'"
                   class="tools_btn"><span><i
                                class="fa fa-backward"></i>&nbsp;返回</span></a>
                <div class="tools_separator"></div>
            </div>
        </div>
        <!--列表-->
        <div id="grid_List">
            <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
                <div>&nbsp;&nbsp;&nbsp;&nbsp;采集日志</div>
                <table border="0" class="form-find" style="height: 45px;">
                    <tr>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;时间(格式:2019-01-01_01)：</th>
                        <td>
                            <input id="time1" type="text" value="${time!'' }" class="txt" style="width: 200px"/>
                        </td>
                        <th>行数(默认100)：</th>
                        <td>
                            <input id="len1" type="text" value="${len!'' }" class="txt" style="width: 200px"/>
                        </td>
                        <td>
                            <input id="btnSearch" type="button" class="btnSearch" value="搜 索" onclick="btn_Search(1)"/>
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
                            <th width="100%">内容详情</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr class="gradeA odd" role="row">
                            <td><textarea class="comments" rows="10" cols="10">${(log1)!'' }</textarea></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <div id="grid_List">
            <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
                <div>&nbsp;&nbsp;&nbsp;&nbsp;采集失败日志</div>
                <table border="0" class="form-find" style="height: 45px;">
                    <tr>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;时间(格式:2019-01-01_01)：</th>
                        <td>
                            <input id="time2" type="text" value="${time!'' }" class="txt" style="width: 200px"/>
                        </td>
                        <th>行数(默认100)：</th>
                        <td>
                            <input id="len2" type="text" value="${len!'' }" class="txt" style="width: 200px"/>
                        </td>
                        <td>
                            <input id="btnSearch" type="button" class="btnSearch" value="搜 索" onclick="btn_Search(2)"/>
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
                            <th width="100%">内容详情</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr class="gradeA odd" role="row">
                            <td><textarea class="comments" rows="10" cols="10">${(log2)!'' }</textarea></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <div id="grid_List">
            <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
                <div>&nbsp;&nbsp;&nbsp;&nbsp;拼装日志</div>
                <table border="0" class="form-find" style="height: 45px;">
                    <tr>
                        <th>&nbsp;&nbsp;&nbsp;&nbsp;时间(格式:2019-01-01_01)：</th>
                        <td>
                            <input id="time3" type="text" value="${time!'' }" class="txt" style="width: 200px"/>
                        </td>
                        <th>行数(默认100)：</th>
                        <td>
                            <input id="len3" type="text" value="${len!'' }" class="txt" style="width: 200px"/>
                        </td>
                        <td>
                            <input id="btnSearch" type="button" class="btnSearch" value="搜 索" onclick="btn_Search(3)"/>
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
                            <th width="100%">内容详情</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr class="gradeA odd" role="row">
                            <td><textarea class="comments" rows="10" cols="10">${(log3)!'' }</textarea></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
