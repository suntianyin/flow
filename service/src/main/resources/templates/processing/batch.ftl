<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>批次信息</title>
    <script type="text/javascript">

        $(function(){
            var manager = $("#manager").val().trim();
            var copyrightOwner = $("#copyrightOwner").val().trim();
            var outUnit = $("#outUnit").val().trim();
            var batchState = $("#batchState").val();
            var beginTime = $("#beginTime").val();
            var endTime = $("#endTime").val();
            var pathurl = "index?manager=" + manager + "&copyrightOwner=" + copyrightOwner + "&outUnit=" + outUnit + "&batchState=" + batchState + "&beginTime=" + beginTime + "&endTime=" + endTime ;;
            var totalPages = ${pages!1};
            var currentPages = ${pageNum!1};

            $("#batchState").val("${(batchState.getCode())!''}");
            jqPaging(pathurl,totalPages,currentPages);

//            $("#batch-import").click(function () {
//                var fileObj = document.getElementById("importFile").files[0]; // js 获取文件对象
//                if (typeof (fileObj) == "undefined" || fileObj.size <= 0) {
//                    alert("请选择文件");
//                    return;
//                }
//                var formFile = new FormData();
////                formFile.append("action", "${ctx}/author/batch/import");
//                formFile.append("file", fileObj); //加入文件对象
//
//                //第一种  XMLHttpRequest 对象
//                //var xhr = new XMLHttpRequest();
//                //xhr.open("post", "/Admin/Ajax/VMKHandler.ashx", true);
//                //xhr.onload = function () {
//                //    alert("上传完成!");
//                //};
//                //xhr.send(formFile);
//
//                //第二种 ajax 提交
//
//                var data = formFile;
//                $.ajax({
//                    url: "${ctx}/author/batch/import",
//                    data: data,
//                    type: "POST",
//                    dataType: "text",
//                    cache: false,//上传文件无需缓存
//                    processData: false,//用于对data参数进行序列化处理 这里必须false
//                    contentType: false, //必须
//                    success: function (result) {
//                        if (result == '成功'){
//                            tipDialog("批量导入成功", 3, 1);
//                            btn_Search();
//                            return;
//                        }else {
//                            tipDialog("批量导入失败", 3, -1);
//                            return;
//                        }
//                    },
//                    error: function (result) {
//                        tipDialog("批量导入失败", 3, -1);
//                    }
//                })
//            })

        });

        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        }

        //检索
        function btn_Search() {
            var manager = $("#manager").val().trim();
            var copyrightOwner = $("#copyrightOwner").val().trim();
            var outUnit = $("#outUnit").val().trim();
            var batchState = $("#batchState").val();
            var beginTime = $("#beginTime").val();
            var endTime = $("#endTime").val();
            window.location.href = "index?manager=" + manager + "&copyrightOwner=" + copyrightOwner + "&outUnit=" + outUnit + "&batchState=" + batchState + "&beginTime=" + beginTime + "&endTime=" + endTime ;
        }

        //编辑批次
        function updateBatch(id) {
            var url = "/processing/batch/edit/index?id=" + id;
            openDialog(url, "updateBatch", "编辑批次", 500, 400, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }

        //查看书单
        function checkBibliotheca(batchId) {

            if (isNull(batchId)){
                tipDialog("批次号不能为空", 3, -1);
                return;
            }
            window.location.href = "${ctx}/processing/bibliotheca/index?batchId=" + $.trim(batchId);
        }
        //书单查重
        function checkDuplication(id, batchId) {
            loading();
            if (isNull(id)){
                tipDialog("数据异常", 3, -1);
                return;
            }
            if (isNull(batchId)){
                tipDialog("批次号不能为空", 3, -1);
                return;
            }
            window.location.href = "${ctx}/processing/bibliotheca/bibliothecaDuplication/index?id=" + $.trim(id);
        }

        function updateBatchState(id) {
            var url = "/processing/batch/editBatchState/index?id=" + id;
            openDialog(url, "updateBatchState", "编辑批次状态", 350, 100, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }

        function auditBatch(id) {
            var url = "/processing/batch/auditBatch/index?id=" + id;
            openDialog(url, "auditBatch", "书单审核", 350, 150, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }

        function btn_addBatch() {
            var url = "/processing/batch/add/index";
            console.log(url);
            openDialog(url, "addBatch", "新增批次", 500, 400, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }
    </script>
</head>
<body>
<div class='load-circle' style="display: none;"></div>
<div id="layout" class="layout">
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>批次管理<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a id="lr-add" title="新增批次" onclick="btn_addBatch()" class="tools_btn"><span><i
                        class="fa fa-plus"></i>&nbsp新增</span></a>
                <div class="tools_separator"></div>
            </div>
            <#--<div class="PartialButton">
                &lt;#&ndash;<a id="lr-add" title="批量导入" onclick="btn_batchAddAuthor()" class="tools_btn"><span><i
                        class="fa fa-plus"></i>&nbsp批量导入</span></a>&ndash;&gt;
                &lt;#&ndash;<form id="batchImportForm" enctype="multipart/form-data">&ndash;&gt;
                    <input id="importFile" type="file" class="tools_btn" accept=".csv" name="file""/>
                <div class="tools_separator"></div>
            </div>
            <div class="PartialButton">
                &lt;#&ndash;<a id="lr-add" title="批量导入" onclick="btn_batchAddAuthor()" class="tools_btn"><span><i
                        class="fa fa-plus"></i>&nbsp批量导入</span></a>&ndash;&gt;
                &lt;#&ndash;<form id="batchImportForm" enctype="multipart/form-data">&ndash;&gt;
                    <button id="batch-import" title="批量导入" class="tools_btn"><span><i
                            class="fa fa-plus"></i>&nbsp批量导入</span></button>
                <div class="tools_separator"></div>
            </div>-->
        </div>
        <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
            <table border="0" class="form-find" style="height: 45px;">
                <tr>
                    <th>内容合作经理：</th>
                    <td>
                        <input id="manager" type="text" value="${manager!'' }" class="txt" style="width: 200px"/>
                    </td>

                    <th>版权所有者：</th>
                    <td>
                        <input id="copyrightOwner" type="text" value="${copyrightOwner!'' }" class="txt" style="width: 200px"/>
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

                    <th>外协单位：</th>
                    <td>
                        <input id="outUnit" type="text" value="${outUnit!'' }" class="txt" style="width: 200px"/>
                    </td>

                    <th>创建时间：</th>
                    <td>
                        <input id="beginTime" name="beginTime" type="date" value="${(beginTime?string("yyyy-MM-dd"))! '' }" class="txt" style="width: 200px"/>至<input id="endTime" name="endTime" type="date" value="${(endTime?string("yyyy-MM-dd"))! '' }" class="txt" style="width: 200px"/>
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
                        <th>书单审核人</th>
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
                            <td>${(list.auditor)! '' }</td>
                            <td>${(list.checker) !''}</td>
                            <td>${(list.memo)! '' }</td>
                            <td>${(list.createTime?datetime)! '' }</td>
                            <td>
                                <a href="javascript:void(0);" onclick="updateBatch('${(list.id)!''}')">编辑</a>
                                <#--<a href="javascript:void(0);" ">删除</a>-->
                                <a href="javascript:void(0);" onclick="checkBibliotheca('${(list.batchId)!''}')">查看书单</a>
                                <a href="javascript:void(0);" onclick="auditBatch('${(list.id)!''}')">书单审核</a>
                                <a href="javascript:void(0);" onclick="updateBatchState('${(list.id)!''}')">修改状态</a>
                                <a href="javascript:void(0);" onclick="checkDuplication('${(list.id)!''}','${(list.batchId)!''}')">查重</a>
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
