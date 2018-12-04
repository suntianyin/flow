<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>管理员书目信息</title>
    <script type="text/javascript">

        $(function () {
            var batchId = $('#batchId').val().trim();
            var title = $("#title").val().trim();
            var publisher = $("#publisher").val().trim();
            // var duplicateFlag = $("#duplicateFlag").val();
            var bibliothecaState = $("#bibliothecaState").val();
            var pathurl = "index?batchId=" + batchId + "&title=" + title + "&publisher=" + publisher  + "&bibliothecaState=" + bibliothecaState;
            var totalPages = ${pages!1};
            var currentPages = ${pageNum!1};

            <#--$("#duplicateFlag").val("${(duplicateFlag.getCode())!''}");-->
            $("#bibliothecaState").val("${(bibliothecaState.getCode())!''}");

            jqPaging(pathurl, totalPages, currentPages);

        });

        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        };

        //检索
        function btn_Search() {
            if (isNull($('#batchId').val())) {
                tipDialog("批次号不能为空，信息异常", 3, -1);
                return;
            }
            var batchId = $('#batchId').val().trim();
            var title = $("#title").val().trim();
            var publisher = $("#publisher").val().trim();
            // var duplicateFlag = $("#duplicateFlag").val().trim();
            var bibliothecaState = $("#bibliothecaState").val();
            window.location.href = "${ctx}/processing/bibliotheca/index?batchId=" + batchId + "&title=" + title + "&publisher=" + publisher  + "&bibliothecaState=" + bibliothecaState;
        }

        //编辑书目
        function updateBibliotheca(id) {
            var url = "/processing/bibliotheca/edit/index?id=" + id;
            openDialog(url, "updateBatch", "编辑书目", 440, 400, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }

        function updateBatchState(id) {
            var url = "/processing/batch/editBatchState/index?id=" + id;
            openDialog(url, "updateBatchState", "编辑批次状态", 350, 100, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }

        //书单分拣
        function updateBibliothecaExclude(id) {

            if (isNull(id)) {
                tipDialog("数据异常", 3, -1);
                return;
            }

            var url = "/processing/bibliotheca/updateBibliothecaExclude?id=" + id;
            confirmDialog("温馨提示", "注：您确定要对分拣当前书目？", function (r) {
                if (r) {
                    Loading(true, "正在提交数据...");
                    window.setTimeout(function () {
                        try {
                            $.ajax({
                                url: RootPath() + url,
                                type: "get",
                                contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                                async: false,
                                success: function (data) {
//                                    Loading(false);
                                    if (data.status == 200) {
                                        tipDialog(data.msg, 3, 1);
                                        /*top.frames[tabiframeId()].location.reload();
                                        closeDialog();*/
                                    } else {
                                        tipDialog(data.msg, 3, -1);
                                    }
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

        // 删除书目
        function removeBibliotheca(id, identifier) {
            if (isNull(id)) {
                tipDialog("数据异常！", 3, -1);
                return;
            }

            var note = "注：您确定要删除当前书目信息？";
            if (!isNull(identifier)) {
                note = "注：您确定要删除 编号为：" + identifier + " 的书目信息？";
            }

            var url = "${ctx}/processing/bibliotheca/removeBibliotheca?id=" + id;
            confirmDialog("温馨提示", note, function (r) {
                if (r) {
                    Loading(true, "正在提交数据...");
                    window.setTimeout(function () {
                        try {
                            $.ajax({
                                url: url,
                                type: "GET",
                                contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                                async: false,
                                success: function (data) {
                                    if (data.status == 200) {
                                        tipDialog(data.msg, 3, 1);
                                    } else {
                                        tipDialog(data.msg, 3, -1);
                                    }
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

        //单本书目添加
        function btn_addBibliotheca() {
            var batchId = $('#batchId').val();
            if (isNull(batchId)) {
                tipDialog("批次号不能为空", 3, -1);
                return;
            }
            var url = "/processing/bibliotheca/add/index?batchId=" + batchId;
            openDialog(url, "addBibliotheca", "新增书目", 440, 400, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }

        function btn_exportOutUnitData() {
            if (isNull($('#batchId').val())) {
                tipDialog("批次号不能为空，信息异常", 3, -1);
                return;
            }
            var batchId = $('#batchId').val().trim();
            var title = $("#title").val().trim();
            var publisher = $("#publisher").val().trim();
            // var duplicateFlag = $("#duplicateFlag").val();
            var bibliothecaState = $("#bibliothecaState").val();

            confirmDialog("温馨提示", "注：您确定要对当前查询结果导出到Excel文件？", function (r) {
                if (r) {
                    Loading(true, "正在提交数据...");
                    window.setTimeout(function () {
                        try {
                            window.location.href = "${ctx}/processing/bibliotheca/exportData?batchId=" + batchId + "&title=" + title + "&publisher=" + publisher  + "&bibliothecaState=" + bibliothecaState;
                        } catch (e) {
                        }
                    }, 200);
                }
            });
        }
    </script>
</head>
<body>
<div id="layout" class="layout">
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>管理员书目信息<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a onclick="javascript:window.location.href='${ctx}/processing/batch/index'" class="tools_btn"><span><i
                        class="fa fa-backward"></i>&nbsp返回</span></a>
                <div class="tools_separator"></div>
            </div>
            <div class="PartialButton">
                <#if BatchStateEnum=4||BatchStateEnum=5||BatchStateEnum=6>
                    <a id="lr-add" class="tools_btn"><span style="color: #7c7c7c;"><i class="fa fa-plus"></i>&nbsp;新增</span></a>
                <#else>
                    <a id="lr-add" title="新增书目" onclick="btn_addBibliotheca()" class="tools_btn"><span><i class="fa fa-plus"></i>&nbsp;新增</span></a>
                </#if>
                <div class="tools_separator"></div>
            </div>
            <div class="PartialButton">
                <button id="batch-import" title="导出查询结果" class="tools_btn" onclick="btn_exportOutUnitData()">
                    <span><i class="fa fa-outdent"></i>&nbsp;导出查询结果</span></button>
                <div class="tools_separator"></div>
            </div>
        </div>
        <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
            <table border="0" class="form-find" style="height: 45px;">
                <tr>
                    <th>标题：</th>
                    <td>
                        <input id="batchId" name="batchId" value="${batchId!''}" hidden class="txt"/>
                        <input id="title" name="title" type="text" value="${title!'' }" class="txt"
                               style="width: 200px"/>
                    </td>

                    <th>出版社：</th>
                    <td>
                        <input id="publisher" name="publisher" type="text" value="${publisher!'' }" class="txt"
                               style="width: 200px"/>
                    </td>

                <#--<th>是否重复：</th>-->
                <#--<td>-->
                <#--<select id="duplicateFlag" name="duplicateFlag" underline="true" style="height: 24px;">-->
                <#--<option value="">--请选择批次状态--</option>-->
                <#--<option value="0">否</option>-->
                <#--<option value="1">是</option>-->
                <#--</select>-->
                <#--</td>-->

                    <th>书目状态：</th>
                    <td>
                        <select id="bibliothecaState" name="bibliothecaState" underline="true" style="height: 24px;">
                            <option value="">--请选择书目状态--</option>
                            <#if bibliothecaStateList??>
                                <#list bibliothecaStateList as list>
                                    <option value="${(list.getCode())!''}">${(list.getDesc())!''}</option>
                                </#list>
                            </#if>
                        </select>
                    </td>

                    <td>
                        <input id="btnSearch" type="button" class="btnSearch" value="查 询" onclick="btn_Search()"/>
                    </td>
                </tr>
            </table>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;共 <label>${total!0}</label> 条书目信息,已分拣<label>${num1!0}</label>条,重复<label>${num2!0}</label>条,制作成功<label>${num3!0}</label>条,制作失败<label>${num4!0}</label>条。
        </div>
        <div class="panel-body">
            <div class="row">

                <table id="table-list"
                       class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                    <thead>
                    <tr role="row">
                        <th><input type="checkbox" id="allChecked"></th>
                        <th>编号</th>
                        <th>资源唯一标识</th>
                        <th>批次号</th>
                        <th>标题</th>
                        <th>作者</th>
                        <th>出版社</th>
                        <th>ISBN</th>
                        <th>出版时间</th>
                        <th>版次</th>
                        <th>纸书价格</th>
                        <th>电子书价格</th>
                        <th>原始文件名</th>
                        <th>文档格式</th>
                        <th>备注</th>
                    <#--<th>是否重复</th>-->
                        <th>书目状态</th>
                    <#--<th>是否制作成功</th>-->
                        <th>书目录入人</th>
                        <th>书目录入时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#if bibliothecaList??>
                        <#list bibliothecaList as list>
                        <tr class="gradeA odd" role="row">
                            <td><input type="checkbox" name="bibliotheca" value="${(list.id)!''}"/></td>
                            <td>${(list.identifier)!''}</td>
                            <td>${(list.metaId)! '' }</td>
                            <td>${(list.batchId)! '' }</td>
                            <td>${(list.title)! '' }</td>
                            <td>${(list.author)! '' }</td>
                            <td>${(list.publisher)! '' }</td>
                            <td>${(list.isbn)! '' }</td>
                            <td>${(list.publishTime)! '' }</td>
                            <td>${(list.edition) !''}</td>
                            <td>${(list.paperPrice)! '' }</td>
                            <td>${(list.eBookPrice)! '' }</td>
                            <td>${(list.originalFilename)!'' }</td>
                            <td>${(list.documentFormat)! '' }</td>
                            <td>${(list.memo)! '' }</td>
                        <#--<td>${(list.duplicateFlag.getDesc())! '' }</td>-->
                            <td>${(list.bibliothecaState.getDesc())! '' }</td>
                        <#--<td>${(list.completedFlag.getDesc())! '' }</td>-->
                            <td>${(list.creator)! '' }</td>
                            <td>${(list.createTime?datetime)! '' }</td>
                            <td>
                            <#if BatchStateEnum=4||BatchStateEnum=5||BatchStateEnum=6>
                                <span style="color: #7c7c7c;">编辑</span>
                                <span style="color: #7c7c7c;">删除</span>
                                <span style="color: #7c7c7c;">分拣</span>
                            <#else>
                                <a href="javascript:void(0);" onclick="updateBibliotheca('${(list.id)!''}')">编辑</a>
                                <a href="javascript:void(0);"
                                   onclick="removeBibliotheca('${(list.id)!''}','${(list.identifier)!''}')">删除</a>
                                <a href="javascript:void(0);"
                                   onclick="updateBibliothecaExclude('${(list.id)!''}')">分拣</a>
                            </#if>
                            </td>
                        </tr>
                        </#list>
                    </#if>
                    </tbody>
                </table>
            </div>
        <#--<ul class="pagination">
            <li>每页 ${pageSize!0} 条记录，共 ${pages!0} 页，共 ${total!0} 条记录</li>
        </ul>
        <ul class="pagination" style="float:right;" id="pagination"></ul>-->
        </div>
    </div>
</div>
</body>
</html>
