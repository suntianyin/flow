<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>

    <title>douban元数据</title>
    <script type="text/javascript">
        $(function () {
            //排序
            var sortColumns = "";
            window.simpleTable = new SimpleTable('form-list', '1', '10', sortColumns);
            //分页
            var metaId = $("#metaId").val();
            var title = $("#title").val();
            var author = $("#author").val();
            var publisher = $("#publisher").val();
            var isbn = $("#isbn").val();
            var isbnVal = $("#isbnVal").val();

            var pathurl = "searchIndex?metaId=" + metaId
                    + "&title=" + title + "&author=" + author
                    + "&publisher=" + publisher + "&isbn=" + isbn + "&isbnVal=" + isbnVal;
            var totalPages = 1;
            var currentPages = 1;
            <#if page??>
                totalPages = ${pages?c};
                currentPages = ${pageNum?c};
            </#if>
            jqPaging(pathurl, totalPages, currentPages);
        });

        //检索
        function btn_Search() {
            var author = $("#author").val();
            var title = $("#title").val();
            var publisher = $("#publisher").val();
            var isbn = $("#isbn").val();
            var isbnVal = $("#isbnVal").val();
            loading();
            window.location.href = "searchIndex?pageNumber=1"
                    + "&title=" + title + "&author=" + author
                    + "&publisher=" + publisher + "&isbn=" + isbn
                    + "&isbnVal=" + isbnVal;
        }

        //douban元数据查看
        function btn_detail(doubanId) {
            loading();
            window.location.href = "doubanDetail?doubanId=" + doubanId;
        }

        //douban元数据编辑
        // 标记tag标签的id，保证不同tag的id不一样
        var index = 250;
        function btn_edit(doubanId) {
            AddTabMenu2('R' + index, '${ctx}/douban/doubanEdit?doubanId='+doubanId, doubanId, 'true');
            index++;
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
                        <th>作者：</th>
                        <td>
                            <input id="author" type="text" value="${author! ''}" class="txt" style="width: 200px"/>
                        </td>

                        <th>书名：</th>
                        <td>
                            <input id="title" type="text" value="${title! ''}" class="txt" style="width: 200px"/>
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
                                        <option value="isbn10">isbn10</option>
                                        <option value="isbn13" selected="selected">isbn13</option>
                                    </#if>
                                    <#if isbn =="isbn10">
                                        <option value="isbn10" selected="selected">isbn10</option>
                                        <option value="isbn13">isbn13</option>
                                    </#if>
                                    <#if isbn =="isbn13">
                                        <option value="isbn10">isbn10</option>
                                        <option value="isbn13" selected="selected">isbn13</option>
                                    </#if>
                                <#else>
                                    <option value="isbn" >isbn</option>
                                    <option value="isbn10">isbn10</option>
                                    <option value="isbn13" selected="selected">isbn13</option>
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
                            <th>作者</th>
                            <th>书名</th>
                            <th>出版社</th>
                            <th>ISBN10</th>
                            <th>ISBN13</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#if doubanMetaModelList?? >
                            <#list doubanMetaModelList as list>
                                <tr class="gradeA odd" role="row">
                                    <td align="center">${list.author! '' }</td>
                                    <td align="center">${list.title! '' }</td>
                                    <td align="center">${list.publisher! '' }</td>
                                    <td align="center">${list.isbn10! '' }</td>
                                    <td align="center">${list.isbn13! '' }</td>
                                    <td align="center">
                                        <a style="cursor:pointer;" onclick="btn_detail('${list.doubanId! "" }');">查看&nbsp;</a>
                                        <a style="cursor:pointer;"
                                           onclick="btn_edit('${list.doubanId! "" }');">编辑&nbsp;</a>
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
