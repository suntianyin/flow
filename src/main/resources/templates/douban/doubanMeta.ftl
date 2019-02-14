<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>

    <title>根据ISBN查询生成元数据</title>
    <script type="text/javascript">
        //检索
        function btn_Search() {
            var isbn = document.getElementById("isbn").value;
            var result = "";
            var isbns = isbn.split("\n");
            for (var i = 0; i < isbns.length; i++) {
                result += isbns[i] + ",";
            }
            var url = "/douban/getBookMeta?isbn13=" + result;
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
                        if (data[i].status == 0) {

                            var hasFlow = "";
                            var hasCebx = "";
                            if (data[i].body.hasFlow == 1) {
                                hasFlow = "是";
                            } else {
                                hasFlow = "否";
                            }
                            if (data[i].body.hasCebx == 1) {
                                hasCebx = "是";
                            } else {
                                hasCebx = "否";
                            }

                            if (data[i].body.hasPublish == 0) {
                                $("#tbody").append(
                                        "<tr>" +
                                        "<td align='center'><input type='checkbox' class='select' value='" + data[i].body.metaId + "'/></td>" +
                                        "<td align='center'><a href='#' onclick='checkDoubanData(this.innerHTML)'>" + data[i].body.metaId + "</a></td>" +
                                        "<td align='center'>" + data[i].body.isbn13 + "</td>" +
                                        "<td align='center'>" + data[i].body.isbn + "</td>" +
                                        "<td align='center'>" + data[i].body.title + "</td>" +
                                        "<td align='center'>" + data[i].body.creator + "</td>" +
                                        "<td align='center'>" + data[i].body.publisher + "</td>" +
                                        "<td align='center'>" + data[i].body.issueddate + "</td>" +
                                        "<td align='center'>" + data[i].body.updateTime + "</td>" +
                                        "<td align='center'>" + hasFlow + "</td>" +
                                        "<td align='center'>" + hasCebx + "</td>" +
                                        "<td align='center'>" + data[i].body.paperPrice + "</td>" +
                                        "</tr>"
                                );
                            } else {
                                var hasFlow = "";
                                var hasCebx = "";
                                if (data[i].body.hasFlow == 1) {
                                    hasFlow = "是";
                                } else {
                                    hasFlow = "否";
                                }
                                if (data[i].body.hasCebx == 1) {
                                    hasCebx = "是";
                                } else {
                                    hasCebx = "否";
                                }
                                $("#tbody").append(
                                        "<tr>" +
                                        /*"<td><a href='#' onclick='checkDoubanData(this.innerHTML)'>" + data[i].body.metaId + "</a></td>" +*/
                                        "<td align='center'></td>" +
                                        "<td align='center'>" + data[i].body.metaId + "</td>" +
                                        "<td align='center'>" + data[i].body.isbn13 + "</td>" +
                                        "<td align='center'>" + data[i].body.isbn + "</td>" +
                                        "<td align='center'>" + data[i].body.title + "</td>" +
                                        "<td align='center'>" + data[i].body.creator + "</td>" +
                                        "<td align='center'>" + data[i].body.publisher + "</td>" +
                                        "<td align='center'>" + data[i].body.issueddate + "</td>" +
                                        "<td align='center'>" + data[i].body.updateTime + "</td>" +
                                        "<td align='center'>" + hasFlow + "</td>" +
                                        "<td align='center'>" + hasCebx + "</td>" +
                                        "<td align='center'>" + data[i].body.paperPrice + "</td>" +
                                        "</tr>"
                                );
                            }
                        }else if (data[i].status == 1) {
                            alert(data[i].body);
                        }
                    }
                    $(".load-circle").hide();
                },
                error: function (data) {
                    alert("查询失败，网络传输出现异常");
                    $(".load-circle").hide();
                }
            });
        }

        // 标记tag标签的id
        var index = 100;

        function checkDoubanData(metaId) {
            AddTabMenu('R' + index, '/publish/compareTempAndStandard?metaId=' + metaId, metaId, "", 'true', 'true');
            // loading();
            // 保证不同tag的id不一样
            index++;
        }

        // 批量发布功能
        function batchPublish() {
            var metaIds = "";
            var checkBoxes = $("[type=checkbox]");
            for (var i = 0; i < checkBoxes.length; i++) {
                if ($(checkBoxes[i]).prop("checked")) {
                    metaIds = metaIds + $(checkBoxes[i]).val() + ","
                }
            }
            if (metaIds == "" || metaIds == ",") {
                alert("没有需要发布的数据...");
                return;
            }

            $.ajax({
                url: "/flowadmin/publish/batchCommit?metaIds=" + metaIds,
                type: "get",
                dataType: "json",
                cache: false,
                contentType: false,
                success: function (data) {
                    if (data.status == 1) {
                        alert(data.msg);
                    }
                    if (data.status == 0) {
                        alert(data.msg);
                    }
                },
                error: function (data) {
                    alert("批量上传失败");
                }
            });
        }

        var flag = true;

        function selectAllItems() {
            if (flag) {
                $(".select").prop("checked", true);
                flag = false;
            } else {
                $(".select").prop("checked", false);
                flag = true;
            }
        }
    </script>
</head>
<body>
<div class='load-circle' style="display: none;"></div>
<div id="layout" class="layout">
    <!--中间-->
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>根据ISBN查询生成元数据<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a id="lr-xmlAdd" href="javascript:;" title="批量发布" onclick="batchPublish()"
                   class="tools_btn"><span><i class="fa fa-plus"></i>&nbsp;批量发布</span></a>
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
                            <th><input type="checkbox" onclick="selectAllItems()" id="unSelect" value=""/></th>
                            <th>图书ID</th>
                            <th>ISBN13</th>
                            <th>ISBN</th>
                            <th>书名</th>
                            <th>作者</th>
                            <th>出版单位</th>
                            <th>出版日期</th>
                            <th>更新时间</th>
                            <th>是否有流式</th>
                            <th>是否有版式</th>
                            <th>价格</th>
                        </tr>
                        </thead>
                        <tbody id="tbody">
                        </tbody>
                    </table>
                </div>
                <ul class="pagination" style="float:right;" id="pagination" onclick="loading()"></ul>
            </div>
        </div>
    </div>
</div>
</body>
</html>
