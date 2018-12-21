<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>协议资源管理</title>
    <script type="text/javascript">

        $(function () {
            var booklistNum = $("#booklistNum").val().trim();
            var title = $("#title").val().trim();
            var creator = $("#creator").val().trim();
            var metaId = $("#metaId").val().trim();
            var copyrightOwner = $("#copyrightOwner").val("${(copyrightOwner)!'' }");
            var isbn = $("#isbn").val().trim();
            var status = $("#status").val("${(status)!'' }");
            var startDate = $("#startDate").val().trim();
            var startDate1 = $("#startDate1").val().trim();
            var endDate = $("#endDate").val().trim();
            var endDate1 = $("#endDate1").val().trim();
            var pathurl = "?booklistNum=" + booklistNum + "&startDate=" + startDate + "&startDate1=" + startDate1 + "&endDate=" + endDate + "&endDate1=" + endDate1 + "&title=" + title + "&creator=" + creator + "&metaId=" + metaId + "&copyrightOwner=" + copyrightOwner + "&isbn=" + isbn + "&status=" + status;
            var totalPages = ${pages!''};
            var currentPages = ${pageNum!''};
            jqPaging(pathurl, totalPages, currentPages);
        });

        //检索
        function btn_Search() {
            var booklistNum = $("#booklistNum").val().trim();
            var title = $("#title").val().trim();
            var creator = $("#creator").val().trim();
            var metaId = $("#metaId").val().trim();
            var copyrightOwner = $("#copyrightOwner").val().trim();
            var isbn = $("#isbn").val().trim();
            var status = $("#status").val();
            var startDate = $("#startDate").val().trim();
            var startDate1 = $("#startDate1").val().trim();
            var endDate = $("#endDate").val().trim();
            var endDate1 = $("#endDate1").val().trim();
            window.location.href = "${ctx}/resource/index?booklistNum=" + booklistNum + "&startDate=" + startDate + "&startDate1=" + startDate1 + "&endDate=" + endDate + "&endDate1=" + endDate1 + "&title=" + title + "&creator=" + creator + "&metaId=" + metaId + "&copyrightOwner=" + copyrightOwner + "&isbn=" + isbn + "&status=" + status;
        }



        function updateResource(id) {
            var url = "/resource/edit/index?resrId=" + id;
            openDialog(url, "updateAuthor", "编辑协议资源信息", 500, 270, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

        function btn_addResource() {
            var url = "/resource/add/index"
            openDialog(url, "addAuthor", "添加协议资源信息", 500, 230, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        };

        function deleteById(id) {
            if (isNull(id)) {
                tipDialog("数据异常！", 3, -1);
                return;
            }

            var note = "注：您确定要删除当前授权协议信息？";

            var url = "/resource/deleteById?resrId=" + id;
            confirmDialog("温馨提示", note, function (r) {
                if (r) {
                    Loading(true, "正在提交数据...");
                    window.setTimeout(function () {
                        try {
                            $.ajax({
                                url: RootPath() + url,
                                type: "GET",
                                contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                                async: false,
                                success: function (data) {
                                    tipDialog("删除成功", 3, 1);
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
        //授权清单导出
        function btn_exportData(){
            var booklistNum = $("#booklistNum").val().trim();
            var title = $("#title").val().trim();
            var creator = $("#creator").val().trim();
            var metaId = $("#metaId").val().trim();
            var copyrightOwner = $("#copyrightOwner").val().trim();
            var isbn = $("#isbn").val().trim();
            var status = $("#status").val();
            var startDate = $("#startDate").val().trim();
            var startDate1 = $("#startDate1").val().trim();
            var endDate = $("#endDate").val().trim();
            var endDate1 = $("#endDate1").val().trim();
            confirmDialog("温馨提示", "注：您确定要对当前查询结果导出到Excel文件？", function (r) {
                if (r) {
                    Loading(true, "正在提交数据...");
                    window.setTimeout(function () {
                        try {
                            window.location.href = "${ctx}/resource/exportData?booklistNum=" + booklistNum + "&startDate=" + startDate + "&startDate1=" + startDate1 + "&endDate=" + endDate + "&endDate1=" + endDate1 + "&title=" + title + "&creator=" + creator + "&metaId=" + metaId + "&copyrightOwner=" + copyrightOwner + "&isbn=" + isbn + "&status=" + status;
                        } catch (e) {
                        }
                    }, 200);
                }
            });
        }
        //授权清单 批量导入
        function btn_DataImport() {
            // var batchId = $('#batchId').val();
            // if (isNull(batchId)) {
            //     tipDialog("批次号不能为空", 3, -1);
            //     return;
            // }

            var fileObj = document.getElementById("importFile").files[0]; // js 获取文件对象
            if (typeof (fileObj) == "undefined" || fileObj.size <= 0) {
                alert("请选择文件");
                return;
            }
            var formFile = new FormData();
            formFile.append("file", fileObj); //加入文件对象
            var data = formFile;

            var url = "${ctx}/resource/dataImport";
            confirmDialog("温馨提示", "注：您确定要对当前文件进行授权清单导入？", function (r) {
                if (r) {
                    Loading(true, "正在提交数据...");
                    window.setTimeout(function () {
                        try {
                            $.ajax({
                                url: url,
                                data: data,
                                type: "POST",
                                cache: false,//上传文件无需缓存
                                processData: false,//用于对data参数进行序列化处理 这里必须false
                                contentType: false, //必须
                                async: false,
                                success: function (data) {
                                    if (data.status == 200) {
                                        tipDialog(data.msg, 3, 1);
                                        btn_Search();
                                        return;
                                    } else {
                                        tipDialog(data.msg, 3, -1);
                                        return;
                                    }
                                },
                                error: function (data) {
                                    tipDialog("批量导入失败", 3, -1);
                                }
                            });
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
            <div>协议资源信息<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <#--<div class="PartialButton">-->
                <#--<a id="lr-add" title="添加协议资源" onclick="btn_addResource()" class="tools_btn">-->
                    <#--<span><i class="fa fa-plus"></i>&nbsp;添加协议资源</span></a>-->
                <#--<div class="tools_separator"></div>-->
            <#--</div>-->
            <div class="PartialButton">
                <button id="batch-import" title="导出授权资源" class="tools_btn" onclick="btn_exportData()">
                    <span><i class="fa fa-outdent"></i>&nbsp;导出授权资源</span></button>
                <div class="tools_separator"></div>
            </div>
            <div class="PartialButton">
                <input id="importFile" type="file" class="tools_btn"
                       accept="application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                       name="file""/>
                <div class="tools_separator"></div>
            </div>
            <div class="PartialButton">
                <button id="batch-import" title="批量导入" class="tools_btn" onclick="btn_DataImport()">
                    <span><i class="fa fa-plus"></i>&nbsp;批量导入</span></button>
                <div class="tools_separator"></div>
            </div>
        </div>
        <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
            <table border="0" class="form-find" style="height: 45px;">
                <tr>
                    <th>授权书单编号：</th>
                    <td>
                        <input id="booklistNum" name="booklistNum" type="text" value="${booklistNum!'' }" class="txt"
                               style="width: 180px"/>
                    </td>

                    <th>唯一标识（Metaid）：</th>
                    <td>
                        <input id="metaId" type="text" name="metaId" value="${metaId!'' }" class="txt"
                               style="width: 180px"/>
                    </td>

                    <th>标题：</th>
                    <td>
                        <input id="title" type="text" name="title" value="${title!'' }" class="txt"
                               style="width: 180px"/>
                    </td>
                    <th>作者：</th>
                    <td>
                        <input id="creator" type="text" name="creator" value="${creator!'' }" class="txt"
                               style="width: 180px"/>
                    </td>
                    <th>版权所有者：</th>
                    <td>
                        <select id="copyrightOwner" name="copyrightOwner" underline="true" style="width: 307px; height: 24px;">
                            <option value="">--请选择出版社--</option>
                            <#if CopyrightOwner??>
                                <#list CopyrightOwner as list>
                                    <option value="${(list.id)!''}">${(list.name)!''}</option>
                                </#list>
                            </#if>
                        </select>
                    </td>
                </tr>
                <tr>

                    <th>Isbn：</th>

                    <td>
                        <input id="isbn" name="isbn" type="text" value="${isbn!'' }" class="txt" style="width: 200px"/>
                    </td>
                    <th>版权授权状态：</th>
                    <td>
                        <select id="status" name="status" underline="true" style="width: 150px">
                            <option value="">无</option>
                            <option value="0">未授权</option>
                            <option value="1">已授权</option>
                            <option value="2">特殊下架</option>
                            <option value="3">特殊上架</option>
                        </select>
                    </td>

                    <th>获取授权时间：</th>
                    <td>
                        <input id="startDate" name="startDate" type="date"
                               value="${(startDate?string("yyyy-MM-dd"))! '' }" class="txt"
                               style="width: 200px"/>至<input id="startDate1" name="startDate1" type="date"
                                                             value="${(startDate1?string("yyyy-MM-dd"))! '' }"
                                                             class="txt" style="width: 200px"/>
                    </td>
                    <th>版权结束时间：</th>
                    <td>
                        <input id="endDate" name="endDate" type="date" value="${(endDate?string("yyyy-MM-dd"))! '' }"
                               class="txt" style="width: 200px"/>至<input id="endDate1" name="endDate1" type="date"
                                                                         value="${(endDate1?string("yyyy-MM-dd"))! '' }"
                                                                         class="txt" style="width: 200px"/>
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
                        <th>授权书单编号</th>
                        <th>版权所有者</th>
                        <th>批次号</th>
                        <th>唯一标识（Metaid）</th>
                        <th>标题</th>
                        <th>作者</th>
                        <th>出版社</th>
                        <th>出版时间</th>
                        <th>Isbn</th>
                        <th>纸书价格（元）</th>
                        <th>电子书价格（元）</th>
                        <th>版权授权状态</th>
                        <th>获取授权时间</th>
                        <th>版权结束时间</th>
                        <th>操作人</th>
                        <th>操作时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#if ResourceList??>
                        <#list ResourceList as list>
                            <tr class="gradeA odd" role="row">
                            <td align="center">${(list.booklistNum)!''}</td>
                            <td align="center">${(list.copyrightOwner)!''}</td>
                            <td align="center">${(list.batchNum)! '' }</td>
                            <td align="center">${(list.metaId)! '' }</td>
                            <td align="center">${(list.title)!'' }</td>
                            <td align="center">${(list.creator)! '' }</td>
                            <td align="center">${(list.publisher)! '' }</td>
                            <td align="center">${(list.issuedDate)?datetime! '' }</td>
                            <td align="center">${(list.isbn)! ''}</td>
                            <td align="center">${(list.paperPrice)! ''}</td>
                            <td align="center">${(list.ePrice)! ''}</td>
                            <td align="center">${(list.status.getDesc())! ''}</td>
                            <td align="center">${(list.authStartDate?datetime)! '' }</td>
                            <td align="center">${(list.authEndDate?datetime)! '' }</td>
                            <td align="center">${(list.operator)! '' }</td>
                            <td align="center">${(list.operateDate?datetime)! '' }</td>
                            <td align="center">
                        <a style="cursor:pointer;" onclick="updateResource('${list.resrId! "" }');">编辑&nbsp;</a>
                        <a style="cursor:pointer;" onclick="deleteById('${list.resrId! "" }');">删除&nbsp;</a>
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
</body>
</html>
