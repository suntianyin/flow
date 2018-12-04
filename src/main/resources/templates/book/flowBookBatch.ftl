<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title></title>
    <script type="text/javascript">

        //扫描文件
        function btn_Search() {
            loading();
            $("#form1").submit();
        }

        //批量发布
        function batchChapter() {
            var books = $('input:checkbox:checked');
            var fileInfo = "";
            $.each(books, function () {
                fileInfo += $(this).val() + ";";
            });

            if (fileInfo == "" || fileInfo == "on,") {
                tipDialog("请选择数据", 3, -2);
                return;
            }

            var filePath = $("#filePath").val();
            if (filePath == "") {
                tipDialog("请填写文件路径", 3, -2);
                return;
            }

            var fileType = $("#fileType").val();

            var formData = new FormData();
            formData.append('fileInfo', fileInfo);
            formData.append("filePath", filePath);
            formData.append("fileType", fileType);
            loading();
            $.ajax({
                url: RootPath() + "/book/batchChapter",
                type: "POST",
                data: formData,
                cache: false,
                processData: false,
                contentType: false,
                success: function (data) {
                    $('.load-circle').hide();
                    if (data == "id_null") {
                        tipDialog("请勾选数据！", 3, -2);
                    } else if (data == "path_null") {
                        tipDialog("请填写文件路径！", 3, -2);
                    } else if (data == "error") {
                        tipDialog("请联系管理员！", 3, -1);
                    } else if (data == "exist") {
                        tipDialog("请检查路径是否正确或已填写过！", 3, -1);
                    } else {
                        $("input[type='checkbox']").prop("disabled", true);
                        var url = "/book/batchChapterShow";
                        FullopenDialog(url, "batchChapterShow", "批量上传结果", function (iframe) {
                            top.frames[iframe].AcceptClick()
                        });
                    }
                },
                error: function (data) {
                    Loading(false);
                    alertDialog(data.responseText, -1);
                }
            });
        }

        //导出结果
        function exportResult() {
            var tb = document.getElementById("table-list");    //获取table对像
            var rows = tb.rows;
            var str = "";
            for (var i = 0; i < rows.length; i++) {    //--循环所有的行
                var cells = rows[i].cells;
                for (var j = 1; j < cells.length; j++) {   //--循环所有的列
                    str += cells[j].innerHTML.replace(/[\s\r\n\t,]/g, "") + ",";
                    //str += cells[j].text();
                }
                str += "\n";
            }
            str = "\ufeff" + str;
            var blob = new Blob([str], {type: 'text/csv,charset=UTF-8'});
            var csvUrl = URL.createObjectURL(blob);
            document.getElementById("export").href = csvUrl;
        }

        //全选/全取消
        function selectAll() {
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
        <!--列表-->
        <div id="grid_List">
            <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
                <form id="form1" action="${ctx}/book/flowBookBatch" method="get">
                    <table border="0" class="form-find" style="height: 45px;">
                        <tr>
                            <th>文件路径：</th>
                            <td>
                                <input id="filePath" name="filePath" type="text" value="${filePath!'' }" class="txt"
                                       style="width: 200px"/>
                            </td>
                            <th>文件类型：</th>
                            <td>
                                <select id="fileType" name="fileType" class="txtselect">
                                <#if fileType??>
                                    <#if fileType =="epub" || fileType =="">
                                        <option value="epub" selected="selected">epub</option>
                                        <option value="cebx">cebx</option>
                                    </#if>
                                    <#if fileType =="cebx">
                                        <option value="epub">epub</option>
                                        <option value="cebx" selected="selected">cebx</option>
                                    </#if>
                                <#else>
                                    <option value="epub" selected="selected">epub</option>
                                    <option value="cebx">cebx</option>
                                </#if>
                                </select>
                            </td>
                            <td>
                                <input id="btnSearch" type="submit" class="btnSearch" value="扫描文件"
                                       onclick="btn_Search()"/>
                            </td>
                            <td>
                                <input id="batch" type="button" class="btnSearch" value="批量发布"
                                       onclick="batchChapter()"/>
                            </td>
                            <td>
                                <a id="export" class="btnSearch" onclick="exportResult(this)" download="download.csv"
                                   href="#">导出结果</a>
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
            <div class="panel-body">
                <div class="row">
                    <table id="table-list"
                           class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                        <thead>
                        <#if bookMetaList?? >
                            <#if bookMetaList?size==0>
                                获取图书元数据0条，请检查目录是否正确
                            <#else >
                                获取图书元数据${bookMetaList?size }条
                            </#if>
                        </#if>
                        <tr role="row">
                            <th><input id="checkAll" type="checkbox" onclick="selectAll(this);"/></th>
                            <th>文件名</th>
                            <th>文件ISBN</th>
                            <th>图书ID</th>
                            <th>书名</th>
                            <th>ISBN</th>
                            <th>ISBN13</th>
                            <th>作者</th>
                            <th>出版单位</th>
                            <th>是否流式</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#if bookMetaList?? >
                            <#list bookMetaList as list>
                                <tr class="gradeA odd" role="row">
                                    <td>
                                        <#if list.hasFlow??>
                                            <#if list.hasFlow == 0>
                                                <#if list.metaId??>
                                                 <input type="checkbox" name="metaId" checked="true"
                                                        value="${list.metaId!'' },${list.fileName!'' }">
                                                <#else >
                                                 <input type="checkbox" disabled="disabled" value="${list.metaId!'' }">
                                                </#if>
                                            <#else>
                                                <input type="checkbox" disabled="disabled" value="${list.metaId!'' }">
                                            </#if>
                                        <#else>
                                            <#if list.metaId??>
                                                 <input type="checkbox" name="metaId" checked="true"
                                                        value="${list.metaId!'' },${list.fileName!'' }">
                                            <#else >
                                                 <input type="checkbox" disabled="disabled" value="${list.metaId!'' }">
                                            </#if>
                                        </#if>
                                    </td>
                                    <td>${list.fileName!'' }</td>
                                    <td>${list.fileIsbn!'-' }</td>
                                    <td>${list.metaId!'' }</td>
                                    <td>${list.title! '' }</td>
                                    <td>${list.isbn! '-' }</td>
                                    <td>${list.isbn13! '-' }</td>
                                    <td>${list.creator! '' }</td>
                                    <td>${list.publisher! '' }</td>
                                    <td>
                                        <#if list.hasFlow??>
                                            <#if list.hasFlow == 0>
                                                否
                                            <#else>
                                                是
                                            </#if>
                                        <#else>
                                            否
                                        </#if>
                                    </td>
                                </tr>
                            </#list>
                        </#if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
