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
                    $("#metaid").focus();
                    $("#metaid").select();
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
            var hasFlow = $("#hasFlow").val();
            var drid = $("#drid").val();
            var pathUrl = "bookMeta?metaId=" + metaId
                    + "&title=" + title + "&creator=" + creator
                    + "&publisher=" + publisher + "&isbn=" + isbn
                    + "&isbnVal=" + isbnVal + "&hasFlow=" + hasFlow
                    + "&drid=" + drid;
            var totalPages = 1;
            var currentPages = 1;
            <#if page??>
                totalPages = ${pages?c};
                currentPages = ${pageNum?c};
            </#if>
            jqPaging(pathUrl, totalPages, currentPages);
        });

        //检索
        function btn_Search() {
            var metaId = $("#metaId").val();
            var title = $("#title").val();
            var creator = $("#creator").val();
            var publisher = $("#publisher").val();
            var isbn = $("#isbn").val();
            var isbnVal = $("#isbnVal").val();
            var hasFlow = $("#hasFlow").val();
            var drid = $("#drid").val();
            loading()
            window.location.href = "bookMeta?metaId=" + metaId
                    + "&title=" + title + "&creator=" + creator
                    + "&publisher=" + publisher + "&isbn=" + isbn
                    + "&isbnVal=" + isbnVal + "&hasFlow=" + hasFlow
                    + "&drid=" + drid;

        }

        //编辑图书元数据
        function editBookMeta(id) {
            var url = "/admin/bookMetaEdit?id=" + id;
            openDialog(url, "bookMetaEdit", "编辑图书meta数据", 500, 400, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

        //编辑图书内容
        function editBookChapter(id, hasflow) {
            if (hasflow == null || hasflow == 0) {
                tipDialog("图书章节内容不存在！", 3, -1);
                return;
            }
            AddTabMenu('editBookChapter', "/book/bookChapterEdit?metaid=" + id, '编辑图书内容', null, 'true', 'true');
        }

        //解析xml文件
        function bookXmlAdd() {
            var url = "/book/bookXmlAdd";
            openDialog(url, "bookXmlAdd", "解析xml文件", 500, 200, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }
        //模板数据导入
        function bookExcelAdd() {
            window.location.href = "${ctx}/book/bookExcelAdd";
        }

        //解析epub文件
        function epubookAdd() {
            var url = "/admin/epubookAdd";
            openDialog(url, "epubookAdd", "解析epub文件", 500, 200, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

        //提取epub章节内容
        function epubChapterAdd(metaId, publishDate) {
            var url = "/book/epubChapterAdd?metaId=" + metaId + "&publishDate=" + publishDate;
            openDialog(url, "epubChapterAdd", "解析epub文件", 500, 200, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

        //提取cebx章节内容
        function cebxChapterAdd(metaid) {
            var url = "/book/cebxChapterAdd?metaid=" + metaid;
            openDialog(url, "cebxChapterAdd", "解析cebx文件", 500, 200, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

        //显示详情
        function bookMetaShow(metaid) {
            var url = "/book/bookMetaShow?metaid=" + metaid;
            AddTabMenu('bookMetaShow', url, '图书元数据详情', null, 'true', 'true');
        }

        //批量发布到爱读爱看
        function batchPub() {
            var books = $('input:checkbox:checked');
            var ids = "";
            $.each(books, function () {
                ids += $(this).val() + ",";
            });
            if (ids == "" || ids == "on,") {
                tipDialog("请选择数据", 3, -2);
                return;
            }
            var url = "/book/batchPub?ids=" + ids;
            openDialog(url, "batchPub", "发布到爱读爱看", 500, 200, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

        //删除图书元数据
        function deleteBookMeta(id) {
            var url = "/admin/bookMetaDelete?id=" + id;
            confirmDialog("温馨提示", "注：您确定要删除该条记录？", function (r) {
                if (r) {
                    Loading(true, "正在删除数据...");
                    window.setTimeout(function () {
                        try {
                            $.ajax({
                                url: RootPath() + url,
                                type: "get",
                                dataType: "text",
                                async: false,
                                success: function (data) {
                                    Loading(false);
                                    tipDialog("删除成功！", 3, 1);
                                    location.reload();
                                },
                                error: function (data) {
                                    Loading(false);
                                    alertDialog("删除失败！", -1);
                                }
                            });
                        } catch (e) {
                        }
                    }, 200);
                }
            });
        }

        //删除图书内容
        function deleteBookChapter(id) {
            var url = "/book/bookChapterDelete?id=" + id;
            confirmDialog("温馨提示", "注：您确定要删除该条记录？", function (r) {
                if (r) {
                    Loading(true, "正在删除数据...");
                    window.setTimeout(function () {
                        try {
                            $.ajax({
                                url: RootPath() + url,
                                type: "get",
                                dataType: "text",
                                async: false,
                                success: function (data) {
                                    Loading(false);
                                    tipDialog("删除成功！", 3, 1);
                                    location.reload();
                                },
                                error: function (data) {
                                    Loading(false);
                                    alertDialog("删除失败！", -1);
                                }
                            });
                        } catch (e) {
                        }
                    }, 200);
                }
            });
        }

        //全选/全取消
        function selectAll(checkbox) {
            //$('input[type=checkbox]').prop('checked', $(checkbox).prop('checked'));
            //$('input:checkbox:checked').prop('checked', $(checkbox).prop('checked'));
            if ($("#checkAll").prop("checked")) {
                $("input[type='checkbox'][name='metaId']").prop("checked", true);//全选
            } else {
                $("input[type='checkbox'][name='metaId']").prop("checked", false);  //取消全选
            }
        }
    </script>
</head>
<body>
<div class='load-circle' style="display: none;"></div>
<div id="layout" class="layout">
    <!--中间-->
    <div class="layoutPanel layout-center">
    <#--<div class="btnbartitle">-->
    <#--<div>图书元数据<span id="CenterTitle"></span></div>-->
    <#--</div>-->
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a id="lr-xmlAdd" href="javascript:;" title="解析xml文件" onclick="bookXmlAdd()"
                   class="tools_btn"><span><i class="fa fa-plus"></i>&nbsp;解析xml文件</span></a>
            </div>
            <div class="tools_separator"></div>
            <div class="PartialButton">
                <a id="lr-xmlAdd" href="javascript:;" title="解析xml文件" onclick="bookExcelAdd()"
                   class="tools_btn"><span><i class="fa fa-plus"></i>&nbsp;模板数据导入</span></a>
            </div>
        </div>
        <!--列表-->
        <div id="grid_List">
            <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
                <table border="0" class="form-find" style="height: 45px;">
                    <tr>
                        <th>图书ID：</th>
                        <td>
                            <input id="metaId" type="text" value="${metaId!'' }" class="txt" style="width: 200px"/>
                        </td>
                        <th>书名：</th>
                        <td>
                            <input id="title" type="text" value="${title!'' }" class="txt" style="width: 200px"/>
                        </td>
                        <th>作者：</th>
                        <td>
                            <input id="creator" type="text" value="${creator!'' }" class="txt" style="width: 200px"/>
                        </td>
                        <th>出版社：</th>
                        <td>
                            <input id="publisher" type="text" value="${publisher!'' }" class="txt"
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
                        <th>是否流式：</th>
                        <td>
                            <select id="hasFlow" name="hasFlow" class="txtselect">
                                <#if hasFlow??>
                                    <#if hasFlow ==0>
                                        <option value="0" selected="selected">否</option>
                                        <option value="1">是</option>
                                        <option value="">全部</option>
                                    </#if>
                                    <#if hasFlow ==1>
                                        <option value="0">否</option>
                                        <option value="1" selected="selected">是</option>
                                        <option value="">全部</option>
                                    </#if>
                                <#else>
                                    <option value="0">否</option>
                                    <option value="1">是</option>
                                    <option value="" selected="selected">全部</option>
                                </#if>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th>DRID：</th>
                        <td>
                            <#if drid??>
                                <input id="drid" type="text" value="${drid?c }" class="txt" style="width: 200px"/>
                            <#else >
                                <input id="drid" type="text" value="" class="txt" style="width: 200px"/>
                            </#if>
                        </td>
                        <td>
                            <input id="btnSearch" type="button" class="btnSearch" value="搜 索" onclick="btn_Search()"/>
                        </td>
                        <td>
                            <input id="batch" type="button" class="btnSearch" value="批量发布到爱读爱看数据库"
                                   onclick="batchPub()"/>
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
                            <th><input id="checkAll" type="checkbox" onclick="selectAll(this);"/></th>
                            <th>图书ID</th>
                            <th>书名</th>
                            <th>作者</th>
                            <th>出版单位</th>
                            <th>出版日期</th>
                            <th>ISBN</th>
                            <th>ISBN10</th>
                            <th>ISBN13</th>
                            <th>是否流式</th>
                            <th>是否cebx</th>
                            <th>语言</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#if bookMetaList?? >
                            <#list bookMetaList as list>
                                <tr class="gradeA odd" role="row">
                                    <td><input type="checkbox" name="metaId" value="${list.metaId!'' }"></td>
                                    <td>${list.metaId!'' }</td>
                                    <td>${list.title! '' }</td>
                                    <td>${list.creator! '' }</td>
                                    <td>${list.publisher! '' }</td>
                                    <td>
                                        ${list.publishDate!''}
                                    </td>
                                    <td>${list.isbn! '' }</td>
                                    <td>${list.isbn10! '' }</td>
                                    <td>${list.isbn13! '' }</td>
                                    <td>
                                        <#if list.hasflow ??>
                                            <#if list.hasflow == 0>
                                                否
                                            <#else>
                                                是
                                            </#if>
                                        <#else>
                                            否
                                        </#if>
                                    </td>
                                    <td>
                                        <#if list.hascebx ??>
                                            <#if list.hascebx == 0>
                                                否
                                            <#else>
                                                是
                                            </#if>
                                        <#else>
                                            否
                                        </#if>
                                    </td>
                                    <td>${list.language !''}</td>
                                    <td>
                                        <a style="cursor:pointer;"
                                           onclick="bookMetaShow('${list.metaId!'' }');">详情&nbsp;</a>
                                        <a style="cursor:pointer;"
                                           onclick="editBookChapter('${list.metaId!'' }', '${list.hasflow!'' }');">编辑内容&nbsp;</a>
                                        <a style="cursor:pointer;"
                                           onclick="epubChapterAdd('${list.metaId!'' }','${list.publishDate!''}');">解析epub&nbsp;</a>
                                        <a style="cursor:pointer;"
                                           onclick="cebxChapterAdd('${list.metaId!'' }');">解析cebx&nbsp;</a>
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
