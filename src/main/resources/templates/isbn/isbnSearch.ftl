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
        //检索
        function btn_Search() {
            var isbn = document.getElementById("isbn").value;
            var result = "";
            var isbns = isbn.split("\n");
            for (var i = 0; i < isbns.length; i++) {
                result += isbns[i] + ",";
            }
            var url = "/isbn/isbnSearch?isbn=" + result;
            loading();
            $.ajax({
                url: RootPath() + url,
                type: "get",
                dataType: "json",
                cache: false,
                contentType: false,
                success: function (data) {
                    $("#tbody").empty();
                    for (var i = 0; i < data.length; i++) {
                        for (var j = 0; j < data[i].length; j++) {
                            $("#tbody").append(
                                    "<tr>" +
                                    "<td align='center'>" + data[i][j].isbn13 + "</td>" +
                                    "<td align='center'>" + data[i][j].isbn + "</td>" +
                                    "<td align='center'>" + data[i][j].isbn10 + "</td>" +
                                    "<td align='center'>" + data[i][j].metaId + "</td>" +
                                    "<td align='center'>" + data[i][j].title + "</td>" +
                                    "<td align='center'>" + data[i][j].author + "</td>" +
                                    "<td align='center'>" + data[i][j].publisher + "</td>" +
                                    "<td align='center'>" + data[i][j].hasCebx + "</td>" +
                                    "<td align='center'>" + data[i][j].hasFlow + "</td>" +
                                    "</tr>");
                        }
                    }
                    $(".load-circle").hide();
                },
                error: function (data) {
                    alert("meta库中没有该数据！");
                    $(".load-circle").hide();
                }
            });
        }

        function exportData() {
            var isbn = document.getElementById("isbn").value;
            var result = "";
            var isbns = isbn.split("\n");
            for (var i = 0; i < isbns.length; i++) {
                result += isbns[i] + ",";
            }
            var url = "exportData?isbn=" + result;
            window.location.href = url;
        }
    </script>
</head>
<body>
<div class='load-circle' style="display: none;"></div>
<div id="layout" class="layout">
    <!--中间-->
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>ISBN搜索<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a id="lr-xmlAdd" href="javascript:;" title="导出数据" onclick="exportData()"
                   class="tools_btn"><span><i class="fa fa-plus"></i>&nbsp;导出数据</span></a>
            </div>
        </div>
        <!--列表-->
        <div id="grid_List">
            <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
                <table border="0" class="form-find" style="height: 45px;">
                    <tr align="center">
                        <th>ISBN：</th>
                        <td>
                            <textarea id="isbn" name="isbn" rows="10" cols="50"></textarea>
                        </td>
                        <td>
                            <input id="btnSearch" type="button" class="btnSearch" value="搜 索" onclick="btn_Search()"/>（ISBN之间以回车键换行分隔）
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
                            <th>ISBN13</th>
                            <th>ISBN</th>
                            <th>ISBN10</th>
                            <th>metaId</th>
                            <th>书名</th>
                            <th>作者</th>
                            <th>出版单位</th>
                            <th>是否有cebx</th>
                            <th>是否是流式</th>
                        </tr>
                        </thead>
                        <form id="excelDataForm" action="exportData" method="post">
                            <tbody id="tbody">
                            </tbody>
                        </form>
                    </table>
                </div>
                <ul class="pagination" style="float:right;" id="pagination"></ul>
            </div>
        </div>
    </div>
</div>
</body>
</html>
