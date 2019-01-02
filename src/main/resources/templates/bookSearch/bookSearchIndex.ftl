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
            //分页
            var metaId = $("#metaId").val();
            var title = $("#title").val();
            var creator = $("#creator").val();
            var publisher = $("#publisher").val();
            var isbn = $("#isbn").val();
            var isbnVal = $("#isbnVal").val();
            var hasCebx = $("#hasCebx").val();
            var hasFlow = $("#hasFlow").val();
            var flowSource = $("#flowSource").val();
            var isPublicCopyRight = $("#isPublicCopyRight").val();
            var saleStatus = $("#saleStatus").val();

            var pathurl = "index?metaId=" + metaId
                    + "&title=" + title + "&creator=" + creator
                    + "&flowSource=" + flowSource
                    + "&publisher=" + publisher + "&isbn=" + isbn + "&isbnVal=" + isbnVal
                    + "&hasCebx=" + hasCebx + "&hasFlow=" + hasFlow + "&isPublicCopyRight=" + isPublicCopyRight
                    + "&saleStatus=" + saleStatus;
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
            var metaId = $("#metaId").val();
            var title = $("#title").val();
            var creator = $("#creator").val();
            var publisher = $("#publisher").val();
            var isbn = $("#isbn").val();
            var isbnVal = $("#isbnVal").val();
            var hasCebx = $("#hasCebx").val();
            var hasFlow = $("#hasFlow").val();
            var flowSource = $("#flowSource").val();
            var isPublicCopyRight = $("#isPublicCopyRight").val();
            var saleStatus = $("#saleStatus").val();
            loading();
            window.location.href = "index?pageNumber=1&metaId=" + metaId
                    + "&title=" + title + "&creator=" + creator
                    + "&flowSource=" + flowSource
                    + "&publisher=" + publisher + "&isbn=" + isbn
                    + "&isbnVal=" + isbnVal + "&hasCebx=" + hasCebx
                    + "&hasFlow=" + hasFlow + "&isPublicCopyRight=" + isPublicCopyRight
                    + "&saleStatus=" + saleStatus;
        }

        //元数据查看
        function btn_detail(metaId) {
            loading();
            window.location.href = "detail?" + "metaId=" + metaId;
        }

        var index1 = 220;

        //流式预览
        function btn_flow(metaId, hasFlow) {
            loading();
            if (hasFlow == null || hasFlow == 0) {
                $(".load-circle").hide();
                tipDialog("图书章节内容不存在！", 3, -1);
                return;
            }
            AddTabMenu2('R' + index1, "${ctx}/book/bookChapterEdit?metaid=" + metaId, metaId, "", 'true', 'true');
            $(".load-circle").hide();
            index1++;
        }

        //cebx预览
        // 标记tag标签的id，保证不同tag的id不一样
        var index2 = 200;

        function btn_cebx(metaId) {
            AddTabMenu2('R' + index2, "http://www.apabi.com/jigou/?pid=book.detail&metaid=" + metaId + "&cult=CN", metaId, "", 'true', 'true');
            index2++;
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
                        <th>图书ID：</th>
                        <td>
                            <input id="metaId" type="text" value="${metaId! ''}" class="txt" style="width: 200px"/>
                        <#--<textarea id="metaIdValue" class="txt" style="width: 200px" cols="20" rows="10"></textarea>-->
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
                    </tr>
                    <tr>
                        <th>是否有cebx：</th>
                        <td>
                            <input type="hidden" value="${hasCebx! ''}">
                            <select id="hasCebx" name="hasCebx" class="txtselect">
                                <#if hasCebx =="">
                                    <option value="1">是</option>
                                    <option value="0">否</option>
                                    <option value="" selected="selected">全部</option>
                                </#if>
                                <#if hasCebx =="0">
                                    <option value="1">是</option>
                                    <option value="0" selected="selected">否</option>
                                    <option value="">全部</option>
                                </#if>
                                <#if hasCebx =="1">
                                    <option value="1" selected="selected">是</option>
                                    <option value="0">否</option>
                                    <option value="">全部</option>
                                </#if>
                            </select>
                        </td>

                        <th>是否有流式：</th>
                        <td>
                            <input type="hidden" value="${hasFlow! ''}">
                            <select id="hasFlow" name="hasFlow" class="txtselect">
                                <#if hasFlow =="1">
                                    <option value="1" selected="selected">是</option>
                                    <option value="0">否</option>
                                    <option value="">全部</option>
                                </#if>
                                <#if hasFlow =="0">
                                    <option value="1">是</option>
                                    <option value="0" selected="selected">否</option>
                                    <option value="">全部</option>
                                </#if>
                                <#if hasFlow =="">
                                    <option value="1">是</option>
                                    <option value="0">否</option>
                                    <option value="" selected="selected">全部</option>
                                </#if>
                            </select>
                        </td>

                        <th>是否是公版书：</th>
                        <td>
                            <input type="hidden" value="${isPublicCopyRight! ''}">
                            <select id="isPublicCopyRight" name="isPublicCopyRight" class="txtselect">
                                <#if isPublicCopyRight == "">
                                    <option value="1">是</option>
                                    <option value="0">否</option>
                                    <option value="" selected="selected">全部</option>
                                </#if>
                                <#if isPublicCopyRight == "0">
                                    <option value="1">是</option>
                                    <option value="0" selected="selected">否</option>
                                    <option value="">全部</option>
                                </#if>
                                <#if isPublicCopyRight == "1">
                                    <option value="1" selected="selected">是</option>
                                    <option value="0">否</option>
                                    <option value="">全部</option>
                                </#if>
                            </select>
                        </td>

                        <th>上架状态：</th>
                        <td>
                            <input type="hidden" value="${saleStatus! ''}">
                            <select id="saleStatus" name="saleStatus" class="txtselect">
                                <#if saleStatus =="">
                                    <option value="1">是</option>
                                    <option value="0">否</option>
                                    <option value="" selected="selected">全部</option>
                                </#if>
                                <#if saleStatus =="0">
                                    <option value="1">是</option>
                                    <option value="0" selected="selected">否</option>
                                    <option value="">全部</option>
                                </#if>
                                <#if saleStatus =="1">
                                    <option value="1" selected="selected">是</option>
                                    <option value="0">否</option>
                                    <option value="">全部</option>
                                </#if>
                            </select>
                        </td>

                        <th>流式内容来源：</th>
                        <td>
                            <input type="hidden" value="${flowSource! ''}">
                            <select id="flowSource" name="flowSource" class="txtselect">
                                <#if flowSource =="">
                                    <option value="epub">epub</option>
                                    <option value="cebx">cebx</option>
                                    <option value="" selected="selected">全部</option>
                                </#if>
                                <#if flowSource =="cebx">
                                    <option value="epub">epub</option>
                                    <option value="cebx" selected="selected">cebx</option>
                                    <option value="">全部</option>
                                </#if>
                                <#if flowSource =="epub">
                                    <option value="epub" selected="selected">epub</option>
                                    <option value="cebx">cebx</option>
                                    <option value="">全部</option>
                                </#if>
                            </select>
                        </td>



                        <td>
                            <input id="btnSearch" type="button" class="btnSearch" value="搜 索" onclick="btn_Search()"/>
                        </td>
                    </tr>
                </table>
            </div>
            <tr class="panel-body">
                <div class="row">
                    <table id="table-list"
                           class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                        <thead>
                        <tr role="row">
                            <th>图书ID</th>
                            <th>书名</th>
                            <th>作者</th>
                            <th>出版社</th>
                            <th>流式内容来源</th>
                            <th>出版日期</th>
                            <th>ISBN</th>
                            <th>ISBN10</th>
                            <th>ISBN13</th>
                            <th>是否流式</th>
                            <th>是否cebx</th>
                            <th>是否是公版</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#if bookSearchModelList?? >
                            <#list bookSearchModelList as list>
                                <tr class="gradeA odd" role="row">
                                    <td align="center">${list.metaId! ''}</td>
                                    <td align="center">${list.title! '' }</td>
                                    <td align="center">${list.creator! '' }</td>
                                    <td align="center">${list.publisher! '' }</td>
                                    <td align="center">${list.flowSource! '' }</td>
                                    <td align="center">${list.issuedDate! '' }</td>
                                    <td align="center">${list.isbn! '' }</td>
                                    <td align="center">${list.isbn10! '' }</td>
                                    <td align="center">${list.isbn13! '' }</td>
                                    <td align="center">
                                        <#if (list.hasFlow! 0) == 0>
                                            否
                                        <#else >
                                            是
                                        </#if>
                                    </td>
                                    <td align="center">
                                        <#if (list.hasCebx! 0) == 0>
                                            否
                                        <#else >
                                            是
                                        </#if>
                                    </td>
                                    <td align="center">
                                        <#if (list.isPublicCopyRight! 0) == 0 >
                                            否
                                        <#else >
                                            是
                                        </#if>
                                    </td>
                                    <td align="center">
                                        <a style="cursor:pointer;" onclick="btn_detail('${list.metaId! "" }');">元数据查看&nbsp;</a>
                                        <a style="cursor:pointer;"
                                           onclick="btn_flow('${list.metaId! "" }','${list.hasFlow! "" }');">流式预览&nbsp;</a>
                                        <a style="cursor:pointer;" onclick="btn_cebx('${list.metaId! "" }');">cebx预览&nbsp;</a>
                                    </td>
                                </tr>
                            </#list>
                        </#if>
                        </tbody>
                    </table>
                </div>

                共${totalCount}条；共${totalPageNum}页<ul class="pagination" style="float:right;" id="pagination"></ul>
            </div>
        </div>
    </div>
</div>
</body>
</html>
