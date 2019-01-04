<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>书单录入</title>
    <script type="text/javascript">

        $(function () {
            var manager = $("#manager").val().trim();
            var batchId = $("#batchId").val().trim();
            var copyrightOwner = $("#copyrightOwner").val().trim();
            var batchState = $("#batchState").val();
            var beginTime = $("#beginTime").val();
            var endTime = $("#endTime").val();
            var pathurl = "index?manager=" + manager + "&copyrightOwner=" + copyrightOwner + "&batchId=" + batchId + "&batchState=" + batchState + "&beginTime=" + beginTime + "&endTime=" + endTime;
            ;
            var totalPages = ${pages!1};
            var currentPages = ${pageNum!1};

            $("#batchState").val("${(batchState.getCode())!''}");
            jqPaging(pathurl, totalPages, currentPages);
        });

        //检索
        function btn_Search() {
            var manager = $("#manager").val().trim();
            var batchId = $("#batchId").val().trim();
            var copyrightOwner = $("#copyrightOwner").val().trim();
            var batchState = $("#batchState").val();
            var beginTime = $("#beginTime").val();
            var endTime = $("#endTime").val();
            window.location.href = "index?manager=" + manager + "&batchId=" + batchId + "&copyrightOwner=" + copyrightOwner + "&batchState=" + batchState + "&beginTime=" + beginTime + "&endTime=" + endTime;
        }

        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        }

        //编辑书单
        function updateBooklist(batchId) {
            if (isNull(batchId)) {
                tipDialog("批次号不能为空", 3, -1);
                return;
            }
            window.location.href = "${ctx}/processing/bibliotheca/outUnit/index?batchId=" + $.trim(batchId);
        }

        //书目解析
        function parsing(id, path ,batchId) {
            if (isNull(path)) {
                tipDialog("资源路径不能为空", 3, -1);
                return;
            }
            var formData = new FormData();
            formData.append('id',id);
            formData.append('path',path);
            formData.append('batchId',batchId);
            var url = "${ctx}/processing/bibliotheca/parsing";
            var note = "注：请核实资源路径：" + path + "是否正确？";
            confirmDialog("温馨提示", note, function (r) {
                if (r) {
                    Loading(true, "正在提交数据...");
                    window.setTimeout(function () {
                        try {
                            $.ajax({
                                url: url,
                                type: "POST",
                                contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                                async: false,
                                processData: false,
                                contentType: false,
                                data:formData,
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

        //提交书单审核
        function uploadBooklistAudit(batchId) {
            if (isNull(batchId)) {
                tipDialog("批次号不能为空", 3, -1);
                return;
            }
            var url = "/processing/batch/uploadBooklistAudit?batchId=" + batchId;
            confirmDialog("温馨提示", "注：您确定要对当前批次：" + batchId + " 提交书单审核？", function (r) {
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

        //提交制作审核
        //         function uploadMakeAudit(batchId) {
        //
        //             if (isNull(batchId)){
        //                 tipDialog("批次号不能为空", 3, -1);
        //                 return;
        //             }
        //
        //             var url = "/processing/batch/uploadMakeAudit?batchId=" + batchId;
        //             confirmDialog("温馨提示", "注：您确定要对当前批次："+ batchId + " 提交制作审核？", function (r) {
        //                 if (r) {
        //                     Loading(true, "正在提交数据...");
        //                     window.setTimeout(function () {
        //                         try {
        //                             $.ajax({
        //                                 url: RootPath() + url,
        //                                 type: "get",
        //                                 contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
        //                                 async: false,
        //                                 success: function (data) {
        // //                                    Loading(false);
        //                                     if (data.status == 200){
        //                                         tipDialog(data.msg, 3, 1);
        //                                         /*top.frames[tabiframeId()].location.reload();
        //                                         closeDialog();*/
        //                                     }else{
        //                                         tipDialog(data.msg, 3, -1);
        //                                     }
        //                                     location.reload();
        //                                 },
        //                                 error: function (data) {
        //                                     Loading(false);
        //                                     tipDialog("服务器异常！",3, -1);
        //                                 }
        //                             });
        //                         } catch (e) {
        //                         }
        //                     }, 200);
        //                 }
        //             });
        //         }

        function auditBatch(id) {
            var url = "/processing/batch/auditBatch/index?id=" + id;
            openDialog(url, "auditBatch", "书单审核", 350, 100, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }
    </script>
</head>
<body>
<div id="layout" class="layout">
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>书单录入<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
        </div>
        <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
            <table border="0" class="form-find" style="height: 45px;">
                <tr>
                    <th>内容合作经理：</th>
                    <td>
                        <input id="manager" type="text" value="${manager!'' }" class="txt" style="width: 200px"/>
                    </td>

                    <th>批次号：</th>
                    <td>
                        <input id="batchId" type="text" value="${batchId!'' }" class="txt" style="width: 200px"/>
                    </td>

                    <th>版权所有者：</th>
                    <td>
                        <input id="copyrightOwner" type="text" value="${copyrightOwner!'' }" class="txt"
                               style="width: 200px"/>
                    </td>

                    <th>批次状态：</th>
                    <td>
                        <select id="batchState" name="batchState" underline="true" style="height: 24px;">
                            <option value="">--请选择批次状态--</option>
                            <option value="0">待录入书单</option>
                            <option value="1">书单录入完成</option>
                            <option value="2">书单已审核</option>
                            <option value="3">书单审核失败</option>
                            <option value="4">已查重</option>
                            <option value="5">制作中</option>
                            <option value="6">制作完成待审核</option>
                            <option value="7">生产完成</option>
                        </select>
                    </td>

                    <#--<th>外协单位：</th>
                    <td>
                        <input id="outUnit" type="text" value="${outUnit!'' }" class="txt" style="width: 200px"/>
                    </td>-->

                    <th>创建时间：</th>
                    <td>
                        <input id="beginTime" name="beginTime" type="date"
                               value="${(beginTime?string("yyyy-MM-dd"))! '' }" class="txt"
                               style="width: 200px"/>至<input id="endTime" name="endTime" type="date"
                                                             value="${(endTime?string("yyyy-MM-dd"))! '' }" class="txt"
                                                             style="width: 200px"/>
                    </td>

                    <td>
                        <input id="btnSearch" type="button" class="btnSearch" value="查 询" onclick="btn_Search()"/>
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
                        <th>内容合作经理</th>
                        <th>批次号</th>
                        <th>外协单位</th>
                        <th>资源类型</th>
                        <th>版权所有者</th>
                        <th>文档大概数量</th>
                        <th>批次状态</th>
                        <th>创建人</th>
                        <th>资源路径</th>
                        <#--<th>书单审核人</th>-->
                        <th>书单查重人</th>
                        <th>备注</th>
                        <th>创建时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#if batchList??>
                        <#list batchList as list>
                            <tr class="gradeA odd" role="row">
                            <td>${(list.manager)!''}</td>
                            <td>${(list.batchId)! '' }</td>
                            <td>${(list.outUnit)! '' }</td>
                            <td>${(list.sourceType.getDesc())!'' }</td>
                            <td>${(list.copyrightOwner)! '' }</td>
                            <td>${(list.documentNum)! '' }</td>
                            <td>${(list.batchState.getDesc())! '' }</td>
                            <td>${(list.creator)! '' }</td>
                            <td>${(list.resourcePath)! '' }</td>
                        <#--<td>${(list.auditor)! '' }</td>-->
                            <td>${(list.checker) !''}</td>
                            <td>${(list.memo)! '' }</td>
                            <td>${(list.createTime?datetime)! '' }</td>
                            <td>
                        <a href="javascript:void(0);" onclick="updateBooklist('${(list.batchId)!''}')">编辑书单</a>
                            <#if (list.batchState.code)??>
                                <#if list.batchState.code == 1>
                                    <a href="javascript:void(0);" onclick="parsing('${(list.id)!''}','${(list.resourcePath)!''}','${(list.batchId)!''}')">书目解析</a>
                                <#else>
                                    <span style="color: #7c7c7c;"><i>书目解析</i></span>
                                </#if>
                            </#if>
                            <#if (list.batchState.code)??>
                                <#if list.batchState.code == 1||list.batchState.code == 7||list.batchState.code == 8 >
                                    <a href="javascript:void(0);" onclick="uploadBooklistAudit('${list.batchId}')">提交书单审核</a>
                                <#else>
                                    <span style="color: #7c7c7c;"><i>提交书单审核</i></span>
                                </#if>
                            </#if>
                            </td>
                            </tr>
                        </#list>
                    </#if>
                    </tbody>
                </table>
            </div>
            <ul class="pagination">
                <li>每页 ${pageSize!0} 条记录，共 ${pages!0} 页，共 ${total!0} 条记录</li>
            </ul>
            <ul class="pagination" style="float:right;" id="pagination"></ul>
        </div>
    </div>
</div>
</body>
</html>
