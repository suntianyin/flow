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
            $(document).keypress(function (e) {
                //回车键事件
                if (e.which == 13) {
                    $("#metaId").focus();
                    $("#metaId").select();
                    $("#btnSearch").click();
                }
            });
            //排序
            var sortColumns = "";
            window.simpleTable = new SimpleTable('form-list', '1', '10', sortColumns);
            //分页
            var metaId = $("#metaId").val();
            var title = $("#title").val();
            var creator = $("#creator").val();
            var publisher = $("#publisher").val();
            var isbn = $("#isbn").val();
            var isbnVal = $("#isbnVal").val();
            var pathurl = "search?metaId=" + metaId
                    + "&title=" + title + "&creator=" + creator
                    + "&publisher=" + publisher + "&isbn=" + isbn + "&isbnVal=" + isbnVal;
            var totalPages = 1;
            var currentPages = 1;
            <#if page??>
                totalPages = ${totalPage?c};
                currentPages = ${pageNum?c};
            </#if>
            jqPaging(pathurl, totalPages, currentPages);
        });

        //检索
        function btn_Search() {
            var metaId = $("#metaId").val();
            var title = $("#title").val();
            var creator = $("#creator").val();
            var publisher = $("#publisher").val();
            var isbn = $("#isbn").val();
            var isbnVal = $("#isbnVal").val();
            loading();
            window.location.href = "search?pageNumber=1&metaId=" + metaId
                    + "&title=" + title + "&creator=" + creator
                    + "&publisher=" + publisher + "&isbn=" + isbn + "&isbnVal=" + isbnVal;
        }

        var index = 270;
        //对比
        function btn_compare(metaId) {
            AddTabMenu2('R' + index, '${ctx}/publish/compareTempAndStandard?metaId='+metaId, metaId, 'true');
            // window.location.href = "compareTempAndStandard?" + "metaId=" + metaId;
        }
        // 查看发布记录
        function checkExportData() {
            loading();
            window.location.href = "/flowadmin/publishResult/index";
        }

        // 全选
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
                url: "batchCommit?metaIds=" + metaIds,
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
                <a id="lr-xmlAdd" href="javascript:;" title="查看发布记录" onclick="checkExportData()"
                   class="tools_btn"><span><i class="fa fa-plus"></i>&nbsp;查看发布记录</span></a>
            </div>
            <div class="PartialButton">
                <a id="lr-xmlAdd" href="javascript:;" title="批量发布" onclick="batchPublish()"
                   class="tools_btn"><span><i class="fa fa-plus"></i>&nbsp;批量发布</span></a>
            </div>
        </div>
        <!--列表-->
        <div id="grid_List">
            <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
                <table border="0" class="form-find" style="height: 45px;">
                    <tr>
                        <th>图书ID：</th>
                        <td>
                            <input id="metaId" type="text" value="${metaId! ''}" class="txt" style="width: 200px"/>
                        </td>
                        <th>书名：</th>
                        <td>
                            <input id="title" type="text" value="${title! ''}" class="txt" style="width: 200px"/>
                        </td>
                        <th>作者：</th>
                        <td>
                            <input id="creator" type="text" value="${creator! ''}" class="txt" style="width: 200px"/>
                        </td>
                        <th>出版社：</th>
                        <td>
                            <input id="publisher" type="text" value="${publisher! ''}" class="txt"
                                   style="width: 200px"/>
                        </td>
                        <th>ISBN：</th>
                        <td>
                            <select id="isbn" name="isbn" class="txtselect">
                                <#if isbn??>
                                    <#if isbn =="isbn" || isbn =="">
                                        <option value="isbn" selected="selected">isbn</option>
                                        <option value="isbn10">isbn10</option>
                                        <option value="isbn13">isbn13</option>
                                    </#if>
                                    <#if isbn =="isbn10">
                                        <option value="isbn">isbn</option>
                                        <option value="isbn10" selected="selected">isbn10</option>
                                        <option value="isbn13">isbn13</option>
                                    </#if>
                                    <#if isbn =="isbn13">
                                        <option value="isbn">isbn</option>
                                        <option value="isbn10">isbn10</option>
                                        <option value="isbn13" selected="selected">isbn13</option>
                                    </#if>
                                <#else>
                                    <option value="isbn" selected="selected">isbn</option>
                                    <option value="isbn10">isbn10</option>
                                    <option value="isbn13">isbn13</option>
                                </#if>
                            </select>
                            <input id="isbnVal" type="text" value="${isbnVal!''}" class="txt" style="width: 200px"/>
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
                            <th><input type="checkbox" onclick="selectAllItems()" id="unSelect" value=""/></th>
                            <th>图书ID</th>
                            <th>书名</th>
                            <th>ISBN13</th>
                            <th>ISBN</th>
                            <th>作者</th>
                            <th>出版社</th>
                            <th>出版时间</th>
                            <th>版次</th>
                            <th>更新时间</th>
                            <th>价格</th>
                            <th>是否已发布</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#if apabiTempList?? >
                            <#list apabiTempList as list>
                                <tr class="gradeA odd" role="row">
                                    <td align='center'><input type='checkbox' class='select' value="${list.metaId! ''}"/></td>
                                    <td align="center">${list.metaId! ''}</td>
                                    <td align="center">${list.title! '' }</td>
                                    <td align="center">${list.isbn13! '' }</td>
                                    <td align="center">${list.isbn! '' }</td>
                                    <td align="center">${list.creator! '' }</td>
                                    <td align="center">${list.publisher! '' }</td>
                                    <td align="center">${list.issuedDate! ''}</td>
                                    <td align="center">${list.editionOrder! ''}</td>
                                    <td align="center">${(list.updateTime?string('yyyy-MM-dd HH:mm:ss'))! ''}</td>
                                    <td align="center">${list.paperPrice! ''}</td>
                                    <td align="center">
                                        <#if (list.hasPublish! 0) == 0>
                                            否
                                        <#else >
                                            是
                                        </#if>
                                    </td>
                                    <td align="center"><a style="cursor:pointer;" onclick="btn_compare('${list.metaId! "" }');">对比&nbsp;</a></td>
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
