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
            var jdItemId = $("#jdItemId").val();
            var publisher = $("#publisher").val();
            var isbn13 = $("#isbn13").val();

            var pathurl = "searchIndex?metaId=" + metaId
                    + "&title=" + title + "&jdItemId=" + jdItemId
                    + "&publisher=" + publisher + "&isbn13=" + isbn13;
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
            var jdItemId = $("#jdItemId").val();
            var title = $("#title").val();
            var publisher = $("#publisher").val();
            var isbn13 = $("#isbn13").val();
            loading();
            window.location.href = "searchIndex?pageNumber=1"
                    + "&title=" + title + "&jdItemId=" + jdItemId
                    + "&publisher=" + publisher + "&isbn13=" + isbn13;
        }

        //jd元数据查看
        function btn_detail(jdItem) {
            loading();
            window.location.href = "jdDetail?jdItemId=" + jdItem;
        }

        //jd元数据编辑
        // 标记tag标签的id，保证不同tag的id不一样
        var index = 260;
        function btn_edit(jdItemId) {
            AddTabMenu2('R' + index, '${ctx}/jd/jdEdit?jdItemId='+jdItemId, jdItemId, 'true');
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
                        <th>id：</th>
                        <td>
                            <input id="jdItemId" type="text" value="${jdItemId! ''}" class="txt" style="width: 200px"/>
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

                        <th>ISBN13：</th>
                        <td>
                            <input id="isbn13" type="text" value="${isbn13!''}" class="txt" style="width: 200px"/>
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
                            <th>id</th>
                            <th>书名</th>
                            <th>出版社</th>
                            <th>出版日期</th>
                            <th>ISBN13</th>
                            <th>更新时间</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#if jdMetaModelList?? >
                            <#list jdMetaModelList as list>
                                <tr class="gradeA odd" role="row">
                                    <td align="center">${list.jdItemId! '' }</td>
                                    <td align="center">${list.title! '' }</td>
                                    <td align="center">${list.publisher! '' }</td>
                                    <td align="center">${list.issuedDate! '' }</td>
                                    <td align="center">${list.isbn13! '' }</td>
                                    <td align="center">${list.updateTime?string("yyyy-MM-dd HH:mm:ss")! '' }</td>
                                    <td align="center">
                                        <a style="cursor:pointer;" onclick="btn_detail('${list.jdItemId! "" }');">查看&nbsp;</a>
                                        <a style="cursor:pointer;"
                                           onclick="btn_edit('${list.jdItemId! "" }');">编辑&nbsp;</a>
                                    </td>
                                </tr>
                            </#list>
                        </#if>
                        </tbody>
                    </table>
                </div>
                共${total}条；共${totalPageNum}页<ul class="pagination" style="float:right;" id="pagination"></ul>
            </div>
        </div>
    </div>
</div>
</body>
</html>
