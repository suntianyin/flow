<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <link href="${ctx}/css/select2/select2.min.css" rel="stylesheet"/>
    <script src="${ctx}/js/select2/select2.min.js"></script>
    <title>授权书单管理</title>
    <script type="text/javascript">

        $(function(){
            $("#copyrightOwnerId").val("${(copyrightOwnerId)!'' }");
            var copyrightOwnerId =$("#copyrightOwnerId").val();
            var authEndDate = $("#authEndDate").val().trim();
            var authEndDate1 = $("#authEndDate1").val().trim();
            var submitDate = $("#submitDate").val().trim();
            var submitDate1 = $("#submitDate1").val().trim();
            var bookListNum = $("#bookListNum").val().trim();
            var authorizeNum = $("#authorizeNum").val().trim();
            var coopertor = $("#coopertor").val().trim();
            var pathurl = "?copyrightOwnerId=" + copyrightOwnerId + "&authEndDate=" + authEndDate+ "&authEndDate1=" + authEndDate1+ "&submitDate=" + submitDate+ "&submitDate1=" + submitDate1+ "&bookListNum=" + bookListNum+ "&authorizeNum=" + authorizeNum+ "&coopertor=" + coopertor;
            var totalPages = ${pages!''};
            var currentPages = ${pageNum!''};
            jqPaging(pathurl,totalPages,currentPages);
        });
        //下拉列表 模糊查询
        $(document).ready(function () {
            $('.js-example-basic-single').select2();
        });
        //检索
        function btn_Search() {
            var copyrightOwnerId = $("#copyrightOwnerId").val().trim();
            var authEndDate = $("#authEndDate").val().trim();
            var authEndDate1 = $("#authEndDate1").val().trim();
            var submitDate = $("#submitDate").val().trim();
            var submitDate1 = $("#submitDate1").val().trim();
            var bookListNum = $("#bookListNum").val().trim();
            var authorizeNum = $("#authorizeNum").val().trim();
            var coopertor = $("#coopertor").val().trim();
            window.location.href = "index?copyrightOwnerId=" + copyrightOwnerId + "&authEndDate=" + authEndDate+ "&authEndDate1=" + authEndDate1+ "&submitDate=" + submitDate+ "&submitDate1=" + submitDate1+ "&bookListNum=" + bookListNum+ "&authorizeNum=" + authorizeNum+ "&coopertor=" + coopertor;
        }

        function updateBookList(id) {
            var url = "/bookList/edit/index?id=" + id;
            openDialog(url, "updateBookList", "编辑授权书单信息", 500, 450, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

        function btn_addBookList() {
            var url = "/bookList/add/index"
            openDialog(url, "addBookList", "添加授权书单信息", 500, 450, function (iframe) {
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
            if (isNull(id)){
                tipDialog("数据异常！", 3, -1);
                return;
            }

            var note = "注：您确定要删除当前授权书单信息？";

            var url = "/bookList/deleteById?id=" + id;
            confirmDialog("温馨提示", note, function (r) {
                if (r) {
                    Loading(true, "正在提交数据...");
                    window.setTimeout(function () {
                        try {
                            $.ajax({
                                url: RootPath()+ url,
                                type: "GET",
                                contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                                async: false,
                                success: function (data) {
                                    tipDialog("删除成功", 3, 1);
                                    location.reload();
                                },
                                error: function (data) {
                                    Loading(false);
                                    tipDialog("服务器异常！",3, -1);
                                }
                            });
                        } catch (e) {
                        }
                    }, 200);
                }
            });
        }
        //上传文件
        function bookListFileAdd(id) {
            var url = "/bookList/bookListFileAdd?id="+id;
            openDialog(url, "bookListFileAdd", "上传授权书单文件", 500, 200, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }
        //下载文件
        function bookListFileDownload(id) {
            window.location.href="${ctx}/bookList/bookListFileDownload?id="+id;
        }
        function bookDetail (bookListNum,batchNum) {
            if (isNull(bookListNum)){
                tipDialog("无书单号！", 3, -1);
                return;
            }
            if (isNull(batchNum)){
                tipDialog("无批次号！", 3, -1);
                return;
            }
            window.location.href="${ctx}/bookList/bookDetail?bookListNum="+bookListNum+'&batchNum='+batchNum;
        }
    </script>
</head>
<body>
<div id="layout" class="layout">
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>授权书单信息<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a id="lr-add" title="添加授权书单" onclick="btn_addBookList()" class="tools_btn"><span><i
                        class="fa fa-plus"></i>&nbsp添加授权书单</span></a>
                <div class="tools_separator"></div>
            </div>
        </div>
        <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
            <table border="0" class="form-find" style="height: 45px;">
                <tr>
                    <th>版权所有者：</th>
                    <td>
                        <select id="copyrightOwnerId" name="copyrightOwnerId" class="js-example-basic-single" underline="true" style="width: 257px; height: 24px;">
                            <option value="">--请选择版权所有者--</option>
                            <#if copyrightOwners??>
                                <#list copyrightOwners as list>
                                    <option value="${(list.id)!''}">${(list.name)!''}</option>
                                </#list>
                            </#if>
                        </select>
                    </td>

                    <th>版权结束时间：</th>
                    <td>
                        <input id="authEndDate" name="authEndDate" type="date" value="${(authEndDate?string("yyyy-MM-dd"))! '' }" class="txt" style="width: 180px"/>至<input id="authEndDate1" name="authEndDate1" type="date" value="${(authEndDate1?string("yyyy-MM-dd"))! '' }" class="txt" style="width: 180px"/>
                    </td>

                    <th>获得授权时间：</th>
                    <td>
                        <input id="submitDate" name="submitDate" type="date" value="${(submitDate?string("yyyy-MM-dd"))! '' }" class="txt" style="width: 180px"/>至<input id="submitDate1" name="submitDate1" type="date" value="${(submitDate1?string("yyyy-MM-dd"))! '' }" class="txt" style="width: 180px"/>
                    </td>
                    <th>授权书单编号：</th>
                    <td>
                        <input id="bookListNum" type="text" value="${bookListNum!'' }" class="txt" style="width: 100px"/>
                    </td>
                    <th>已授权资源数量：</th>
                    <td>
                        <input id="authorizeNum" type="text" value="${authorizeNum!'' }" class="txt" style="width: 100px"/>
                    </td>
                    <th>内容合作经理：</th>
                    <td>
                        <input id="coopertor" type="text" value="${coopertor!'' }" class="txt" style="width: 100px"/>
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
                        <th>协议编号</th>
                        <th>书单批次编号</th>
                        <th>版权所有者</th>
                        <th>文档大概数量</th>
                        <th>可加工的数量</th>
                        <th>需授权资源数量</th>
                        <th>已授权资源数量</th>
                        <th>版权结束时间</th>
                        <th>申请授权时间</th>
                        <th>内容合作经理</th>
                        <th>操作人</th>
                        <th>操作时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#if bookList??>
                        <#list bookList as list>
                        <tr class="gradeA odd" role="row">
                            <td align="center">${(list.bookListNum)!''}</td>
                            <td align="center">${(list.agreementNum)! '' }</td>
                            <td align="center">${(list.batchNum)! '' }</td>
                            <td align="center">${(list.copyrightOwner)!'' }</td>
                            <td align="center">${(list.aboutNum)! '' }</td>
                            <td align="center">${(list.validMakeNum)! '' }</td>
                            <td align="center">${(list.applyNum)!''}</td>
                            <td align="center">${(list.authorizeNum)!''}</td>
                            <td align="center">${(list.authEndDate?datetime)! '' }</td>
                            <td align="center">${(list.submitDate?datetime) !''}</td>
                            <td align="center">${(list.coopertor)!''}</td>
                            <td align="center">${(list.opertor)! '' }</td>
                            <td align="center">${(list.operteDate?datetime)! '' }</td>
                            <td align="center">
                                <a style="cursor:pointer;" onclick="updateBookList('${list.id! "" }');">编辑&nbsp;</a>
                                <a style="cursor:pointer;" onclick="deleteById('${list.id! "" }');">删除&nbsp;</a>
                                <a style="cursor:pointer;" onclick="bookListFileAdd('${list.id! "" }');">上传附件&nbsp;</a>
                                <a style="cursor:pointer;" onclick="bookListFileDownload('${list.id! "" }');">下载附件&nbsp;</a>
                                <a style="cursor:pointer;" onclick="bookDetail('${list.bookListNum! "" }','${list.batchNum! "" }');">授权资源查看&nbsp;</a>
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
