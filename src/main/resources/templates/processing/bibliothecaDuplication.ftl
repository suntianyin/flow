<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>书目查重</title>

    <style type="text/css">
        .meta-color {
            color: #484891;
        }
    </style>

    <script type="text/javascript">

        $(function () {
            /*if (isNull($('#batchId').val())){
                tipDialog("批次号不能为空，信息异常", 3, -1);
                return;
            }*/
            var batchId = $.trim($('#batchId').val());
            var title = $.trim($("#title").val());
            var publisher = $.trim($("#publisher").val());
            var duplicateFlag = $.trim($("#duplicateFlag").val());
            var bibliothecaState = $.trim($("#bibliothecaState").val());

            var pathurl = "index?batchId=" + batchId + "&title=" + title + "&publisher=" + publisher + "&duplicateFlag=" + duplicateFlag + "&bibliothecaState=" + bibliothecaState;
            var totalPages = ${pages!1};
            var currentPages = ${pageNum!1};

            $("#duplicateFlag").val("${(duplicateFlag.getCode())!''}");
            $("#bibliothecaState").val("${(bibliothecaState.getCode())!''}");

            jqPaging(pathurl, totalPages, currentPages);
        });

        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        };

        // 确认操作
        // dataType : yes, no , noMatch => cebx:  是，否，未匹配
        // dataType : gtEqYes, gtEqNo, ltYes, ltNo, noMatch **************************************************
        // btnType : duplicate,  make => 按钮操作：　确认重复，确认不重复
        function btn_sureOperation(dataType, btnType, id) {

            if (isNull(dataType) || isNull(btnType)) {
                tipDialog("数据异常", 3, -1);
                return;
            }

            var checkID = [];//定义一个空数组，用于存放选中的值
            // id 为空，则走批量操作
            if (isNull(id)) {
                if (dataType == 'ltYes') {
                    $("input[type='checkbox'][name='ltYes']:checked").each(function (i) {
                        checkID[i] = $(this).val();
                    })
                } else if (dataType == 'ltNo') {
                    $("input[type='checkbox'][name='ltNo']:checked").each(function (i) {
                        checkID[i] = $(this).val();
                    })
                } else if (dataType == 'noMatch') {
                    $("input[type='checkbox'][name='noMatch']:checked").each(function (i) {
                        checkID[i] = $(this).val();
                    })
                } else {
                    tipDialog("数据异常", 3, -1);
                    return;
                }

            } else {
                checkID[0] = id;
            }

            if (checkID.length == 0) {
                tipDialog("请选择数据！", 3, -1);
                return;
            }
            var url = "/processing/bibliotheca/sureOperation?dataType=" + dataType + "&btnType=" + btnType;
            btn_identifyDuplicate(btnType, url, checkID);
        }

        function btn_identifyDuplicate(btnType, url, checkID) {
            var note = "";
            if (btnType == 'make') {
                note = "注：您确定要对选中项进行 确认不重复 操作？";
            } else {
                note = "注：您确定要对选中项进行 确认重复 操作？";
            }
            confirmDialog("温馨提示", note, function (r) {
                if (r) {
                    Loading(true, "正在提交数据...");
                    window.setTimeout(function () {
                        try {
                            $.ajax({
                                url: RootPath() + url,
                                type: "POST",
                                contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                                data: JSON.stringify(checkID),
                                async: false,
                                success: function (data) {
//                                    Loading(false);
                                    if (data.status == 200) {
                                        tipDialog(data.msg, 3, 1);
                                    } else {
                                        tipDialog(data.msg, 3, -1);
                                    }
                                    loading();
                                    location.reload();
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

    </script>
</head>
<body>
<div class='load-circle' style="display: none;"></div>
<div id="layout" class="layout">
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>书目查重<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a onclick="javascript:window.location.href='${ctx}/processing/batch/index'" class="tools_btn"><span><i
                                class="fa fa-backward"></i>&nbsp返回</span></a>
                <div class="tools_separator"></div>
            </div>
        </div>
        <div class="panel-body">
            <div class="row">
                <div>
                    <span style="float: left;">
                        <label>疑似重复的记录(背景色：重复率>95%绿色，重复率<95%蓝色)如下：</label>
                    </span>

                </div>
                <table id="table-list"
                       class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                    <thead>
                    <tr role="row">
                        <th><input type="checkbox" id="allCheckedLtYes" onclick="selectAll(this, 'ltYes')"></th>
                        <th>ISBN</th>
                        <th class="meta-color">ISBN</th>
                        <th>标题</th>
                        <th class="meta-color">标题</th>
                        <th>作者</th>
                        <th class="meta-color">作者</th>
                        <th>出版社</th>
                        <th class="meta-color">出版社</th>
                        <th>出版时间</th>
                        <th class="meta-color">出版时间</th>
                        <th>批次</th>
                        <th class="meta-color">批次</th>
                        <th class="meta-color">书目状态</th>
                        <th class="meta-color">资源唯一标识</th>
                        <th class="meta-color">ISBN13</th>
                        <th class="meta-color">是否有cebx</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#if ltRateCheckFlagYesList??>
                        <#list ltRateCheckFlagYesList as list>
                            <#if list.rateFlag==1>
                                <tr class="gradeA odd" role="row" style="background: #BBFFBB">
                                <td><input type="checkbox" name="ltYes"
                                value="${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}"/></td>
                                <td>${(list.bibliotheca.isbn)!''}</td>
                                <td class="meta-color">${(list.bookMeta.isbn)! '' }</td>
                                <td>${(list.bibliotheca.title)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.title)! '' }</td>
                                <td>${(list.bibliotheca.author)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.creator) !''}</td>
                                <td>${(list.bibliotheca.publisher)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.publisher)! '' }</td>
                                <td>${(list.bibliotheca.publishTime)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.issuedDate?date("yyyy-MM-dd"))!'' }</td>
                                <td>${(list.bibliotheca.batchId)! '' }</td>
                                <td class="meta-color">${(list.bibliotheca2.batchId)! '' }</td>
                                <td class="meta-color">${(list.bibliotheca2.bibliothecaState.desc)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.metaId)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.isbn13)! '' }</td>
                                <td class="meta-color">
                                <#if (list.bookMeta.hasCebx??) && list.bookMeta.hasCebx == 1>
                                    <label style="color: #f9500d;">是</label>
                                <#else>
                                    <label style="color: #aaaaaa;">否</label>
                                </#if>
                                </td>
                                <td>
                                <#if (list.bibliotheca.bibliothecaState)??>
                                    <#if list.bibliotheca.bibliothecaState.desc == "重复">
                                        <span style="color: #7c7c7c;"><i>确认重复</i></span>
                                        <a href="javascript:void(0);"
                                        onclick="btn_sureOperation('ltYes', 'make', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认不重复</a>
                                    <#elseif list.bibliotheca.bibliothecaState.desc == "不重复">
                                        <a href="javascript:void(0);"
                                        onclick="btn_sureOperation('ltYes', 'duplicate', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认重复</a>
                                        <span style="color: #7c7c7c;"><i>确认不重复</i></span>
                                    <#else >
                                        <a href="javascript:void(0);"
                                        onclick="btn_sureOperation('ltYes', 'duplicate', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认重复</a>
                                        <a href="javascript:void(0);"
                                        onclick="btn_sureOperation('ltYes', 'make', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认不重复</a>
                                    </#if>
                                <#else >
                                    <a href="javascript:void(0);"
                                    onclick="btn_sureOperation('ltYes', 'duplicate', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认重复</a>
                                    <a href="javascript:void(0);"
                                    onclick="btn_sureOperation('ltYes', 'make', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认不重复</a>
                                </#if>
                                </td>
                                </tr>
                            <#else>
                                <tr class="gradeA odd" role="row" style="background: #84C1FF">
                                <td><input type="checkbox" name="ltYes"
                                value="${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}"/></td>
                                <td>${(list.bibliotheca.isbn)!''}</td>
                                <td class="meta-color">${(list.bookMeta.isbn)! '' }</td>
                                <td>${(list.bibliotheca.title)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.title)! '' }</td>
                                <td>${(list.bibliotheca.author)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.creator) !''}</td>
                                <td>${(list.bibliotheca.publisher)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.publisher)! '' }</td>
                                <td>${(list.bibliotheca.publishTime)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.issuedDate?date("yyyy-MM-dd"))!'' }</td>
                                <td>${(list.bibliotheca.batchId)! '' }</td>
                                <td class="meta-color">${(list.bibliotheca2.batchId)! '' }</td>
                                <td class="meta-color">${(list.bibliotheca2.bibliothecaState.desc)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.metaId)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.isbn13)! '' }</td>
                                <td class="meta-color">
                                <#if (list.bookMeta.hasCebx??) && list.bookMeta.hasCebx == 1>
                                    <label style="color: #f9500d;">是</label>
                                <#else>
                                    <label style="color: #aaaaaa;">异常</label>
                                </#if>
                                </td>
                                <td>
                                <#if (list.bibliotheca.bibliothecaState)??>
                                    <#if list.bibliotheca.bibliothecaState.desc == "重复">
                                        <span style="color: #7c7c7c;"><i>确认重复</i></span>
                                        <a href="javascript:void(0);"
                                        onclick="btn_sureOperation('ltYes', 'make', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认不重复</a>
                                    <#elseif list.bibliotheca.bibliothecaState.desc == "不重复">
                                        <a href="javascript:void(0);"
                                        onclick="btn_sureOperation('ltYes', 'duplicate', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认重复</a>
                                        <span style="color: #7c7c7c;"><i>确认不重复</i></span>
                                    <#else >
                                        <a href="javascript:void(0);"
                                        onclick="btn_sureOperation('ltYes', 'duplicate', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认重复</a>
                                        <a href="javascript:void(0);"
                                        onclick="btn_sureOperation('ltYes', 'make', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认不重复</a>
                                    </#if>
                                <#else >
                                    <a href="javascript:void(0);"
                                    onclick="btn_sureOperation('ltYes', 'duplicate', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认重复</a>
                                    <a href="javascript:void(0);"
                                    onclick="btn_sureOperation('ltYes', 'make', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认不重复</a>
                                </#if>
                                </td>
                                </tr>
                            </#if>
                        </#list>
                    </#if>
                    </tbody>
                </table>
                <span style="float: left">
                        <input type="button" style="padding: 10px;padding-top: 3px; padding-bottom: 3px" value="确认重复"
                               onclick="btn_sureOperation('ltYes','duplicate','')"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <input type="button" style="padding: 10px;padding-top: 3px; padding-bottom: 3px" value="确认不重复"
                               onclick="btn_sureOperation('ltYes','make','')"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    </span>
            </div>


            <div class="row">
                <div>
                    <span style="float: left;">
                        <label>数据库中有匹配记录(背景色：重复率>95%绿色，重复率<95%蓝色)，需要进行加工制作的记录如下：</label>
                    </span>

                </div>

                <table id="table-list"
                       class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                    <thead>
                    <tr role="row">
                        <th><input type="checkbox" id="allCheckedLtNo" onclick="selectAll(this, 'ltNo')"></th>
                        <th>ISBN</th>
                        <th class="meta-color">ISBN</th>
                        <th>标题</th>
                        <th class="meta-color">标题</th>
                        <th>作者</th>
                        <th class="meta-color">作者</th>
                        <th>出版社</th>
                        <th class="meta-color">出版社</th>
                        <th>出版时间</th>
                        <th class="meta-color">出版时间</th>
                        <th>批次</th>
                        <th class="meta-color">批次</th>
                        <th class="meta-color">书目状态</th>
                        <th class="meta-color">资源唯一标识</th>
                        <th class="meta-color">ISBN13</th>
                        <th class="meta-color">是否有cebx</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#if ltRateCheckFlagNoList??>
                        <#list ltRateCheckFlagNoList as list>
                            <#if list.rateFlag==1>
                                <tr class="gradeA odd" role="row" style="background: #BBFFBB" >
                                <td><input type="checkbox" name="ltNo"
                                value="${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}"/></td>
                                <td>${(list.bibliotheca.isbn)!''}</td>
                                <td class="meta-color">${(list.bookMeta.isbn)! '' }</td>
                                <td>${(list.bibliotheca.title)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.title)! '' }</td>
                                <td>${(list.bibliotheca.author)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.creator) !''}</td>
                                <td>${(list.bibliotheca.publisher)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.publisher)! '' }</td>
                                <td>${(list.bibliotheca.publishTime)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.issuedDate?date("yyyy-MM-dd"))!'' }</td>
                                <td>${(list.bibliotheca.batchId)! '' }</td>
                                <td class="meta-color">${(list.bibliotheca2.batchId)! '' }</td>
                                <td class="meta-color">${(list.bibliotheca2.bibliothecaState.desc)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.metaId)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.isbn13)! '' }</td>
                                <td class="meta-color">
                                <#if (list.bookMeta.hasCebx??) && list.bookMeta.hasCebx == 0>
                                    <label style="color: #f9500d;">否</label>
                                <#else>
                                    <label style="color: #aaaaaa;">异常</label>
                                </#if>
                                </td>
                                <td>
                                <#if (list.bibliotheca.bibliothecaState)??>
                                    <#if list.bibliotheca.bibliothecaState.desc == "重复">
                                        <span style="color: #7c7c7c;"><i>确认重复</i></span>
                                        <a href="javascript:void(0);"
                                        onclick="btn_sureOperation('ltNo', 'make', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认不重复</a>
                                    <#elseif list.bibliotheca.bibliothecaState.desc == "不重复">
                                        <a href="javascript:void(0);"
                                        onclick="btn_sureOperation('ltNo', 'duplicate', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认重复</a>
                                        <span style="color: #7c7c7c;"><i>确认不重复</i></span>
                                    <#else >
                                        <a href="javascript:void(0);"
                                        onclick="btn_sureOperation('ltNo', 'duplicate', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认重复</a>
                                        <a href="javascript:void(0);"
                                        onclick="btn_sureOperation('ltNo', 'make', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认不重复</a>
                                    </#if>
                                <#else >
                                    <a href="javascript:void(0);"
                                    onclick="btn_sureOperation('ltNo', 'duplicate', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认重复</a>
                                    <a href="javascript:void(0);"
                                    onclick="btn_sureOperation('ltNo', 'make', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认不重复</a>
                                </#if>
                                </td>
                                </tr>
                            <#else>
                                <tr class="gradeA odd" role="row" style="background: #84C1FF">
                                <td><input type="checkbox" name="ltNo"
                                value="${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}"/></td>
                                <td>${(list.bibliotheca.isbn)!''}</td>
                                <td class="meta-color">${(list.bookMeta.isbn)! '' }</td>
                                <td>${(list.bibliotheca.title)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.title)! '' }</td>
                                <td>${(list.bibliotheca.author)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.creator) !''}</td>
                                <td>${(list.bibliotheca.publisher)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.publisher)! '' }</td>
                                <td>${(list.bibliotheca.publishTime)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.issuedDate?date("yyyy-MM-dd"))!'' }</td>
                                <td>${(list.bibliotheca.batchId)! '' }</td>
                                <td class="meta-color">${(list.bbibliotheca2.batchId)! '' }</td>
                                <td class="meta-color">${(list.bibliotheca2.bibliothecaState.desc)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.metaId)! '' }</td>
                                <td class="meta-color">${(list.bookMeta.isbn13)! '' }</td>
                                <td class="meta-color">
                                <#if (list.bookMeta.hasCebx??) && list.bookMeta.hasCebx == 0>
                                    <label style="color: #f9500d;">否</label>
                                <#else>
                                    <label style="color: #aaaaaa;">是</label>
                                </#if>
                                </td>
                                <td>
                                <#if (list.bibliotheca.bibliothecaState)??>
                                    <#if list.bibliotheca.bibliothecaState.desc == "重复">
                                        <span style="color: #7c7c7c;"><i>确认重复</i></span>
                                        <a href="javascript:void(0);"
                                        onclick="btn_sureOperation('ltNo', 'make', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认不重复</a>
                                    <#elseif list.bibliotheca.bibliothecaState.desc == "不重复">
                                        <a href="javascript:void(0);"
                                        onclick="btn_sureOperation('ltNo', 'duplicate', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认重复</a>
                                        <span style="color: #7c7c7c;"><i>确认不重复</i></span>
                                    <#else >
                                        <a href="javascript:void(0);"
                                        onclick="btn_sureOperation('ltNo', 'duplicate', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认重复</a>
                                        <a href="javascript:void(0);"
                                        onclick="btn_sureOperation('ltNo', 'make', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认不重复</a>
                                    </#if>
                                <#else >
                                    <a href="javascript:void(0);"
                                    onclick="btn_sureOperation('ltNo', 'duplicate', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认重复</a>
                                    <a href="javascript:void(0);"
                                    onclick="btn_sureOperation('ltNo', 'make', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认不重复</a>
                                </#if>
                                </td>
                                </tr>
                            </#if>
                        </#list>
                    </#if>
                    </tbody>
                </table>
                <span style="float: left">
                        <input type="button" style="padding: 10px;padding-top: 3px; padding-bottom: 3px" value="确认重复"
                               onclick="btn_sureOperation('ltNo','duplicate','')"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <input type="button" style="padding: 10px;padding-top: 3px; padding-bottom: 3px" value="确认不重复"
                               onclick="btn_sureOperation('ltNo','make','')"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    </span>
            </div>


            <div class="row">
                <div>
                    <span style="float: left;">
                        <label>数据库中无匹配记录，需要进行加工制作的记录如下：</label>
                    </span>

                </div>
                <table id="table-list"
                       class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                    <thead>
                    <tr role="row">
                        <th><input type="checkbox" id="allCheckedNoMatch" onclick="selectAll(this, 'noMatch')"></th>
                        <th>ISBN</th>
                        <th class="meta-color">ISBN</th>
                        <th>标题</th>
                        <th class="meta-color">标题</th>
                        <th>作者</th>
                        <th class="meta-color">作者</th>
                        <th>出版社</th>
                        <th class="meta-color">出版社</th>
                        <th>出版时间</th>
                        <th class="meta-color">出版时间</th>
                        <th>批次</th>
                        <th class="meta-color">批次</th>
                        <th class="meta-color">书目状态</th>
                        <th class="meta-color">资源唯一标识</th>
                        <th class="meta-color">ISBN13</th>
                        <th class="meta-color">是否有cebx</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#if noMetaDataList??>
                        <#list noMetaDataList as list>
                            <tr class="gradeA odd" role="row">
                            <td><input type="checkbox" name="noMatch"
                            value="${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}"/></td>
                            <td>${(list.bibliotheca.isbn)!''}</td>
                            <td class="meta-color">${(list.bookMeta.isbn)! '' }</td>
                            <td>${(list.bibliotheca.title)! '' }</td>
                            <td class="meta-color">${(list.bookMeta.title)! '' }</td>
                            <td>${(list.bibliotheca.author)! '' }</td>
                            <td class="meta-color">${(list.bookMeta.creator) !''}</td>
                            <td>${(list.bibliotheca.publisher)! '' }</td>
                            <td class="meta-color">${(list.bookMeta.publisher)! '' }</td>
                            <td>${(list.bibliotheca.publishTime)! '' }</td>
                            <td class="meta-color">${(list.bookMeta.issuedDate?date("yyyy-MM-dd"))!'' }</td>
                            <td>${(list.bibliotheca.batchId)! '' }</td>
                            <td class="meta-color">${(list.bibliotheca2.batchId)! '' }</td>
                            <td class="meta-color">${(list.bibliotheca2.bibliothecaState.desc)! '' }</td>
                            <td class="meta-color">${(list.bookMeta.metaId)! '' }</td>
                            <td class="meta-color">${(list.bookMeta.isbn13)! '' }</td>
                        <td class="meta-color">
                            <label>否</label>
                        </td>
                            <td>
                            <#if (list.bibliotheca.bibliothecaState)??>
                                <#if list.bibliotheca.bibliothecaState.desc == "重复">
                                    <span style="color: #7c7c7c;"><i>确认重复</i></span>
                                    <a href="javascript:void(0);"
                                    onclick="btn_sureOperation('noMatch', 'make', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认不重复</a>
                                <#elseif list.bibliotheca.bibliothecaState.desc == "不重复">
                                    <a href="javascript:void(0);"
                                    onclick="btn_sureOperation('noMatch', 'duplicate', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认重复</a>
                                    <span style="color: #7c7c7c;"><i>确认不重复</i></span>
                                <#else >
                                    <a href="javascript:void(0);"
                                    onclick="btn_sureOperation('noMatch', 'duplicate', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认重复</a>
                                    <a href="javascript:void(0);"
                                    onclick="btn_sureOperation('noMatch', 'make', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认不重复</a>
                                </#if>
                            <#else >
                                <a href="javascript:void(0);"
                                onclick="btn_sureOperation('noMatch', 'duplicate', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认重复</a>
                                <a href="javascript:void(0);"
                                onclick="btn_sureOperation('noMatch', 'make', '${(list.bibliotheca.id)!''},${(list.bookMeta.metaId)!''}')">确认不重复</a>
                            </#if>
                            </td>
                            </tr>
                        </#list>
                    </#if>
                    </tbody>
                </table>
                <span style="float: left">
                        <input type="button" style="padding: 10px;padding-top: 3px; padding-bottom: 3px" value="确认重复"
                               onclick="btn_sureOperation('noMatch','duplicate','')"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <input type="button" style="padding: 10px;padding-top: 3px; padding-bottom: 3px" value="确认不重复"
                               onclick="btn_sureOperation('noMatch','make','')"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    </span>
            </div>
        </div>
    </div>
</div>
</body>
</html>
