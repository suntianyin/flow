<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>模板数据导入</title>

    <style type="text/css">
        .meta-color {
            color: #ff892a;
        }
    </style>

    <script type="text/javascript">
        <#--$(function () {-->
            <#--/*if (isNull($('#batchId').val())){-->
                <#--tipDialog("批次号不能为空，信息异常", 3, -1);-->
                <#--return;-->
            <#--}*/-->
            <#--var batchId = $.trim($('#batchId').val());-->
            <#--var title = $.trim($("#title").val());-->
            <#--var publisher = $.trim($("#publisher").val());-->
            <#--var duplicateFlag = $.trim($("#duplicateFlag").val());-->
            <#--var bibliothecaState = $.trim($("#bibliothecaState").val());-->

            <#--var pathurl = "index?batchId=" + batchId + "&title=" + title + "&publisher=" + publisher + "&duplicateFlag=" + duplicateFlag + "&bibliothecaState=" + bibliothecaState;-->
            <#--var totalPages = ${pages!1};-->
            <#--var currentPages = ${pageNum!1};-->

            <#--$("#duplicateFlag").val("${(duplicateFlag.getCode())!''}");-->
            <#--$("#bibliothecaState").val("${(bibliothecaState.getCode())!''}");-->

            <#--jqPaging(pathurl, totalPages, currentPages);-->
        <#--});-->

        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        };

        function btn_list(btnType) {
            var books ="";
            books = $('input:checkbox:checked');
            var fileInfo = "";
            $.each(books, function () {
                fileInfo += $(this).val() + ";";
            });
            var formData = new FormData();
            formData.append('fileInfo', fileInfo);
            var note = "";
            if (btnType == 'import') {
                note = "注：您确定要对选中项进行 确认上传 操作？";
                confirmDialog("温馨提示", note, function (r) {
                    if (r) {
                        Loading(true, "正在提交数据...");
                        window.setTimeout(function () {
                            try {
                                $.ajax({
                                    url: RootPath() + "/book/excelImportMeta",
                                    type: "POST",
                                    cache: false,
                                    processData: false,
                                    contentType: false,
                                    data: formData,
                                    async: false,
                                    success: function (data) {
//                                    Loading(false);
                                        if (data.status == 200) {
                                            tipDialog(data.msg, 3, 1);
                                            $.each(books, function () {
                                            var ui = document.getElementById($(this).val());
                                            if(ui!=null) {
                                                ui.style.display = "none";
                                                $(this).attr("checked",false);
                                                // ui.disabled="disabled";
                                            }
                                            });
                                        } else {
                                            tipDialog(data.msg, 3, -1);
                                        }
                                        // loading();
                                        // location.reload();
                                    },
                                    error: function (data) {
                                        Loading(false);
                                        tipDialog("服务器异常！", 3, -1);
                                    }
                                });
                            } catch (e) {
                            }
                        }, 200);
                    }
                });
            } else if (btnType == 'noImport') {
                note = "注：您确定要对选中项进行 确认不上传 操作？";
                confirmDialog("温馨提示", note, function (r) {
                    if (r) {
                        $.each(books, function () {
                        var ui = document.getElementById($(this).val());
                        ui.style.display = "none";
                        });
                    }
                });
            }
        }
        function btn_sureOperation(btnType, list) {
            var fileInfo =list.split(",",1);
            var formData = new FormData();
            formData.append('fileInfo', fileInfo);
            var note = "";
            if (btnType == 'import') {
                note = "注：您确定要对选中项进行 确认上传 操作？";
                confirmDialog("温馨提示", note, function (r) {
                    if (r) {
                        Loading(true, "正在提交数据...");
                        window.setTimeout(function () {
                            try {
                                $.ajax({
                                    url: RootPath()+"/book/excelImportMeta",
                                    type: "POST",
                                    cache: false,
                                    processData: false,
                                    contentType: false,
                                    data: formData,
                                    async: false,
                                    success: function (data) {
//                                    Loading(false);
                                        if (data.status == 200) {
                                            tipDialog(data.msg, 3, 1);
                                            var ui = document.getElementById(list);
                                            ui.style.display="none";

                                        } else {
                                            tipDialog(data.msg, 3, -1);
                                        }
                                        // loading();
                                        // location.reload();
                                    },
                                    error: function (data) {
                                        Loading(false);
                                        tipDialog("服务器异常！", 3, -1);
                                    }
                                });
                            } catch (e) {
                            }
                        }, 200);
                    }
                });
            } else if(btnType == 'noImport'){
                note = "注：您确定要对选中项进行 确认不上传 操作？";
                confirmDialog("温馨提示", note, function (r) {
                    if (r) {
                        var ui = document.getElementById(list);
                        ui.style.display="none";
                    }
                });
            }
        }

        //全选/全取消
        function selectAll(checkBox, checkName) {
            //$('input[type=checkbox]').prop('checked', $(checkbox).prop('checked'));
            //$('input:checkbox:checked').prop('checked', $(checkbox).prop('checked'));
            if (checkBox.checked) {
                $("input[type='checkbox'][name='" + checkName + "']").prop("checked", true);//全选
            } else {
                $("input[type='checkbox'][name='" + checkName + "']").prop("checked", false);  //取消全选
            }
        }
        function batchImport() {
            loading();
            $("#form1").submit();
        }

    </script>
</head>
<body>
<div class='load-circle' style="display: none;"></div>
<div id="layout" class="layout">
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>模板数据导入<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <form id="form1" action="${ctx}/book/bookExcelAdd/import" method="post" enctype="multipart/form-data">
            <table>
                <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
                    <div class="PartialButton">
                        <a onclick="javascript:window.location.href='${ctx}/book/bookMeta'"
                           class="tools_btn"><span><i
                                class="fa fa-backward"></i>&nbsp返回</span></a>
                        <div class="tools_separator"></div>
                    </div>
                    <div class="PartialButton">
                        <input id="importFile" type="file" class="tools_btn" accept=".xlsx" name="file""/>
                        <div class="tools_separator"></div>
                    </div>
                    <div class="PartialButton">
                        <button id="batch-import" title="批量导入" class="tools_btn" onclick="batchImport()"><span><i
                                class="fa fa-plus"></i>&nbsp批量导入模板数据</span></button>
                        <div class="tools_separator"></div>
                    </div>
                </div>
            </table>
        </form>
        <div class="panel-body">
            <div class="row">
                <div>
                    <span style="float: left;">
                        <label>疑似重复的记录如下：(黑色-上传模版数据;黄色-数据库现有数据)</label>
                    </span>
                    <span style="float: right">
                        <input type="button" style="padding: 10px;padding-top: 3px; padding-bottom: 3px" value="确认不上传"
                               onclick="btn_list('noImport')"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <input type="button" style="padding: 10px;padding-top: 3px; padding-bottom: 3px" value="确认上传"
                               onclick="btn_list('import')"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    </span>
                </div>
                <table id="table-list"
                       class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                    <thead>
                    <tr role="row" >
                        <th><input type="checkbox" id="allCheckedLtYes" onclick="selectAll(this, 'ltYes1')" ></th>
                        <th>ISBN</th>
                        <th class="meta-color">ISBN</th>
                        <th>ISBN10</th>
                        <th class="meta-color">ISBN10</th>
                        <th>ISBN13</th>
                        <th class="meta-color">ISBN13</th>
                        <th>标题</th>
                        <th class="meta-color">标题</th>
                        <th>副标题</th>
                        <th class="meta-color">副标题</th>
                        <th>作者</th>
                        <th class="meta-color">作者</th>
                        <#--<th>责任关系词</th>-->
                        <#--<th class="meta-color">责任关系词</th>-->
                        <th>出版社</th>
                        <th class="meta-color">出版社</th>
                        <th>出版时间</th>
                        <th class="meta-color">出版时间</th>
                        <#--<th>出版地</th>-->
                        <#--<th class="meta-color">出版地</th>-->
                        <#--<th>丛书关系</th>-->
                        <#--<th class="meta-color">丛书关系</th>-->
                        <#--<th>版次</th>-->
                        <#--<th class="meta-color">版次</th>-->
                        <#--<th>中图法分类号</th>-->
                        <#--<th class="meta-color">中图法分类号</th>-->
                        <#--<th>翻译</th>-->
                        <#--<th class="meta-color">翻译</th>-->
                        <#--<th>原书名</th>-->
                        <#--<th class="meta-color">原书名</th>-->
                        <#--<th>阿帕比作者id</th>-->
                        <#--<th class="meta-color">阿帕比作者id</th>-->
                        <#--<th>语种</th>-->
                        <#--<th class="meta-color">语种</th>-->
                        <#--<th>序言</th>-->
                        <#--<th class="meta-color">序言</th>-->
                        <#--<th>纸书价格</th>-->
                        <#--<th class="meta-color">纸书价格</th>-->
                        <#--<th>电子书价格</th>-->
                        <#--<th class="meta-color">电子书价格</th>-->
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#if bookMetaFromExcels??>
                        <#list bookMetaFromExcels as list>
                            <#if list.state==1>
                                <tr class="gradeA odd" role="row" id="${(list.bookMetaTemp.metaId+','+list.bookMeta.metaId)!''}" >
                                    <td><input type="checkbox" name="ltYes1"
                                               value="${(list.bookMetaTemp.metaId+','+list.bookMeta.metaId)!''}"/>
                                    </td>
                                    <td>${(list.bookMetaTemp.isbn)!''}</td>
                                    <td class="meta-color">${(list.bookMeta.isbn)! '' }</td>
                                    <td>${(list.bookMetaTemp.isbn10)!''}</td>
                                    <td class="meta-color">${(list.bookMeta.isbn10)! '' }</td>
                                    <td>${(list.bookMetaTemp.isbn13)!''}</td>
                                    <td class="meta-color">${(list.bookMeta.isbn13)! '' }</td>
                                    <td>${(list.bookMetaTemp.title)! '' }</td>
                                    <td class="meta-color">${(list.bookMeta.title)! '' }</td>
                                    <td>${(list.bookMetaTemp.subTitle)! '' }</td>
                                    <td class="meta-color">${(list.bookMeta.subTitle)! '' }</td>
                                    <td>${(list.bookMetaTemp.creator)! '' }</td>
                                    <td class="meta-color">${(list.bookMeta.creator) !''}</td>
                                    <#--<td>${(list.bookMetaTemp.creatorWord)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.creatorWord) !''}</td>-->
                                    <td>${(list.bookMetaTemp.publisher)! '' }</td>
                                    <td class="meta-color">${(list.bookMeta.publisher)! '' }</td>
                                    <td>${(list.bookMetaTemp.issuedDate)! '' }</td>
                                    <td class="meta-color">${(list.bookMeta.issuedDate?date("yyyy-MM-dd"))!'' }</td>
                                    <#--<td>${(list.bookMetaTemp.place)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.place)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.relation)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.relation)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.editionOrder)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.editionOrder)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.classCode)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.classCode)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.translator)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.translator)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.originTitle)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.originTitle)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.creatorid)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.creatorid)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.originTitle)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.originTitle)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.language)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.language)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.preface)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.preface)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.paperPrice)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.paperPrice)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.ebookPrice)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.ebookPrice)! '' }</td>-->
                                    <td>
                                <#--<#if (list.bibliotheca.bibliothecaState)??>-->
                                    <#--<#if list.bibliotheca.bibliothecaState.desc == "重复">-->
                                        <#--<span style="color: #7c7c7c;"><i>确认不上传</i></span>-->
                                        <#--<a href="javascript:void(0);"-->
                                           <#--onclick="btn_sureOperation('ltYes', 'make', '${(list.bookMetaTemp.id)!''},${(list.bookMeta.metaId)!''}')">确认上传</a>-->
                                    <#--<#elseif list.bibliotheca.bibliothecaState.desc == "不重复">-->
                                        <#--<a href="javascript:void(0);"-->
                                           <#--onclick="btn_sureOperation('ltYes', 'duplicate', '${(list.bookMetaTemp.id)!''},${(list.bookMeta.metaId)!''}')">确认不上传</a>-->
                                        <#--<span style="color: #7c7c7c;"><i>确认上传</i></span>-->
                                    <#--<#else >-->
                                        <#--<a href="javascript:void(0);"-->
                                           <#--onclick="btn_sureOperation('ltYes', 'duplicate', '${(list.bookMetaTemp.id)!''},${(list.bookMeta.metaId)!''}')">确认不上传</a>-->
                                        <#--<a href="javascript:void(0);"-->
                                           <#--onclick="btn_sureOperation('ltYes', 'make', '${(list.bookMetaTemp.id)!''},${(list.bookMeta.metaId)!''}')">确认上传</a>-->
                                    <#--</#if>-->
                                <#--<#else >-->
                                    <a href="javascript:void(0);"
                                       onclick="btn_sureOperation('noImport', '${(list.bookMetaTemp.metaId+','+list.bookMeta.metaId)!''}')">确认不上传</a>
                                    <a href="javascript:void(0);"
                                       onclick="btn_sureOperation('import', '${(list.bookMetaTemp.metaId+','+list.bookMeta.metaId)!''}')">确认上传</a>
                                <#--</#if>-->
                                    </td>
                                </tr>
                                    </td>
                                </tr>
                            </#if>
                        </#list>
                    </#if>
                    </tbody>
                </table>
                <div>
                    <span style="float: left;">
                        <label>不重复的记录如下：</label>
                    </span>
                    <span style="float: right">
                        <input type="button" style="padding: 10px;padding-top: 3px; padding-bottom: 3px" value="确认不上传"
                               onclick="btn_list('noImport')"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <input type="button" style="padding: 10px;padding-top: 3px; padding-bottom: 3px" value="确认上传"
                               onclick="btn_list('import')"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    </span>
                </div>
                <table id="table-list"
                       class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                    <thead>
                    <tr role="row">
                        <th><input type="checkbox" id="allCheckedLtYes" onclick="selectAll(this, 'ltYes2')" ></th>
                        <th>ISBN</th>
                        <th class="meta-color">ISBN</th>
                        <th>ISBN10</th>
                        <th class="meta-color">ISBN10</th>
                        <th>ISBN13</th>
                        <th class="meta-color">ISBN13</th>
                        <th>标题</th>
                        <th class="meta-color">标题</th>
                        <th>副标题</th>
                        <th class="meta-color">副标题</th>
                        <th>作者</th>
                        <th class="meta-color">作者</th>
                        <#--<th>责任关系词</th>-->
                        <#--<th class="meta-color">责任关系词</th>-->
                        <th>出版社</th>
                        <th class="meta-color">出版社</th>
                        <th>出版时间</th>
                        <th class="meta-color">出版时间</th>
                        <#--<th>出版地</th>-->
                        <#--<th class="meta-color">出版地</th>-->
                        <#--<th>丛书关系</th>-->
                        <#--<th class="meta-color">丛书关系</th>-->
                        <#--<th>版次</th>-->
                        <#--<th class="meta-color">版次</th>-->
                        <#--<th>中图法分类号</th>-->
                        <#--<th class="meta-color">中图法分类号</th>-->
                        <#--<th>翻译</th>-->
                        <#--<th class="meta-color">翻译</th>-->
                        <#--<th>原书名</th>-->
                        <#--<th class="meta-color">原书名</th>-->
                        <#--<th>阿帕比作者id</th>-->
                        <#--<th class="meta-color">阿帕比作者id</th>-->
                        <#--<th>语种</th>-->
                        <#--<th class="meta-color">语种</th>-->
                        <#--<th>序言</th>-->
                        <#--<th class="meta-color">序言</th>-->
                        <#--<th>纸书价格</th>-->
                        <#--<th class="meta-color">纸书价格</th>-->
                        <#--<th>电子书价格</th>-->
                        <#--<th class="meta-color">电子书价格</th>-->
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#if bookMetaFromExcels??>
                        <#list bookMetaFromExcels as list>
                            <#if list.state==0>
                                <tr class="gradeA odd" role="row" id="${(list.bookMetaTemp.metaId+','+list.bookMeta.metaId)!''}" >
                                    <td><input type="checkbox" name="ltYes2"
                                               value="${(list.bookMetaTemp.metaId+','+list.bookMeta.metaId)!''}"/>
                                    </td>
                                    <td>${(list.bookMetaTemp.isbn)!''}</td>
                                    <td class="meta-color">${(list.bookMeta.isbn)! '' }</td>
                                    <td>${(list.bookMetaTemp.isbn10)!''}</td>
                                    <td class="meta-color">${(list.bookMeta.isbn10)! '' }</td>
                                    <td>${(list.bookMetaTemp.isbn13)!''}</td>
                                    <td class="meta-color">${(list.bookMeta.isbn13)! '' }</td>
                                    <td>${(list.bookMetaTemp.title)! '' }</td>
                                    <td class="meta-color">${(list.bookMeta.title)! '' }</td>
                                    <td>${(list.bookMetaTemp.subTitle)! '' }</td>
                                    <td class="meta-color">${(list.bookMeta.subTitle)! '' }</td>
                                    <td>${(list.bookMetaTemp.creator)! '' }</td>
                                    <td class="meta-color">${(list.bookMeta.creator) !''}</td>
                                    <#--<td>${(list.bookMetaTemp.creatorWord)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.creatorWord) !''}</td>-->
                                    <td>${(list.bookMetaTemp.publisher)! '' }</td>
                                    <td class="meta-color">${(list.bookMeta.publisher)! '' }</td>
                                    <td>${(list.bookMetaTemp.issuedDate)! '' }</td>
                                    <td class="meta-color">${(list.bookMeta.issuedDate?date("yyyy-MM-dd"))!'' }</td>
                                    <#--<td>${(list.bookMetaTemp.place)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.place)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.relation)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.relation)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.editionOrder)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.editionOrder)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.classCode)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.classCode)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.translator)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.translator)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.originTitle)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.originTitle)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.creatorid)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.creatorid)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.originTitle)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.originTitle)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.language)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.language)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.preface)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.preface)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.paperPrice)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.paperPrice)! '' }</td>-->
                                    <#--<td>${(list.bookMetaTemp.ebookPrice)! '' }</td>-->
                                    <#--<td class="meta-color">${(list.bookMeta.ebookPrice)! '' }</td>-->
                                    <td>
                                <#--<#if (list.bibliotheca.bibliothecaState)??>-->
                                    <#--<#if list.bibliotheca.bibliothecaState.desc == "重复">-->
                                        <#--<span style="color: #7c7c7c;"><i>确认不上传</i></span>-->
                                        <#--<a href="javascript:void(0);"-->
                                           <#--onclick="btn_sureOperation('ltYes', 'make', '${(list.bookMetaTemp.id)!''},${(list.bookMeta.metaId)!''}')">确认上传</a>-->
                                    <#--<#elseif list.bibliotheca.bibliothecaState.desc == "不重复">-->
                                        <#--<a href="javascript:void(0);"-->
                                           <#--onclick="btn_sureOperation('ltYes', 'duplicate', '${(list.bookMetaTemp.id)!''},${(list.bookMeta.metaId)!''}')">确认不上传</a>-->
                                        <#--<span style="color: #7c7c7c;"><i>确认上传</i></span>-->
                                    <#--<#else >-->
                                        <#--<a href="javascript:void(0);"-->
                                           <#--onclick="btn_sureOperation('ltYes', 'duplicate', '${(list.bookMetaTemp.id)!''},${(list.bookMeta.metaId)!''}')">确认不上传</a>-->
                                        <#--<a href="javascript:void(0);"-->
                                           <#--onclick="btn_sureOperation('ltYes', 'make', '${(list.bookMetaTemp.id)!''},${(list.bookMeta.metaId)!''}')">确认上传</a>-->
                                    <#--</#if>-->
                                <#--<#else >-->
                                    <a href="javascript:void(0);"
                                       onclick="btn_sureOperation('noImport', '${(list.bookMetaTemp.metaId+','+list.bookMeta.metaId)!''}')">确认不上传</a>
                                    <a href="javascript:void(0);"
                                       onclick="btn_sureOperation('import', '${(list.bookMetaTemp.metaId+','+list.bookMeta.metaId)!''}')">确认上传</a>
                                    </td>
                                </tr>
                                    </td>
                                </tr>
                            </#if>
                        </#list>
                    </#if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
