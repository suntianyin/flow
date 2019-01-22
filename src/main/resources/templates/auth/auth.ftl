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
    <title>版权协议管理</title>
    <script type="text/javascript">

        $(function(){
            $("#copyrightOwnerId").val("${(copyrightOwnerId)!'' }");
            var copyrightOwnerId = $("#copyrightOwnerId").val();
            $("#agreementType").val("${(agreementType)!'' }");
            var agreementType=$("#agreementType").val();
            var startDate = $("#startDate").val().trim();
            var startDate1 = $("#startDate1").val().trim();
            var endDate = $("#endDate").val().trim();
            var endDate1 = $("#endDate1").val().trim();
            $("#authType").val("${(authType)!'' }");
            var authType = $("#authType").val();
            var contentManagerName = $("#contentManagerName").val().trim();
            var pathurl = "?copyrightOwnerId=" + copyrightOwnerId + "&startDate=" + startDate+ "&startDate1=" + startDate1+ "&endDate=" + endDate+ "&endDate1=" + endDate1+ "&authType=" + authType+ "&contentManagerName=" + contentManagerName+ "&agreementType=" + agreementType;
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
            var agreementType=$("#agreementType").val().trim();
            var startDate = $("#startDate").val().trim();
            var startDate1 = $("#startDate1").val().trim();
            var endDate = $("#endDate").val().trim();
            var endDate1 = $("#endDate1").val().trim();
            var authType = $("#authType").val().trim();
            var contentManagerName = $("#contentManagerName").val().trim();
            window.location.href = "index?copyrightOwnerId=" + copyrightOwnerId + "&startDate=" + startDate+ "&startDate1=" + startDate1+ "&endDate=" + endDate+ "&endDate1=" + endDate1+ "&authType=" + authType+ "&contentManagerName=" + contentManagerName+ "&agreementType=" + agreementType;
        }

        function updateAuth(id) {
            var url = "/auth/edit/index?caid=" + id;
            openDialog(url, "updateAuthor", "编辑授权协议信息", 1100, 520, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

        function btn_addAuth() {
            var url = "/auth/add/index"
            openDialog(url, "addAuthor", "添加授权协议信息", 1100, 520, function (iframe) {
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

            var note = "注：您确定要删除当前授权协议信息？";

            var url = "/auth/deleteById?caid=" + id;
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
        function authFileAdd(id) {
            var url = "/auth/authFileAdd?caid="+id;
            openDialog(url, "authFileAdd", "上传版权协议文件", 500, 200, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }
        //下载文件
        function authFileDownload(id) {
            window.location.href="${ctx}/auth/authFileDownload?caid="+id;
        }
    </script>
</head>
<body>
<div id="layout" class="layout">
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>版权协议信息<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a id="lr-add" title="添加版权协议" onclick="btn_addAuth()" class="tools_btn"><span><i
                        class="fa fa-plus"></i>&nbsp添加版权协议</span></a>
                <div class="tools_separator"></div>
            </div>
        </div>
        <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
            <table border="0" class="form-find" style="height: 45px;">
                <tr>
                    <th>版权所有者：</th>
                    <td>
                        <select id="copyrightOwnerId" name="copyrightOwnerId" class="js-example-basic-single" underline="true" style="width: 290px; height: 24px;">
                            <option value="">--请选择版权所有者--</option>
                            <#if copyrightOwners??>
                                <#list copyrightOwners as list>
                                    <option value="${(list.id)!''}">${(list.name)!''}</option>
                                </#list>
                            </#if>
                        </select>
                    </td>
                    <th>协议类型：</th>
                    <td>
                        <select id="agreementType" name="agreementType"  underline="true" style="width: 107px; height: 24px;">
                            <option  value="">无</option>
                            <option  value="0">电子书</option>
                            <option  value="1">年鉴</option>
                            <option  value="2">工具书</option>
                            <option  value="3">特色资源</option>
                            <option  value="4">作者签约协议</option>
                            <option  value="5">报纸</option>
                            <option  value="6">图片库</option>
                        </select>
                    </td>
                    <th>协议起始时间：</th>
                    <td>
                        <input id="startDate" name="startDate" type="date" value="${(startDate?string("yyyy-MM-dd"))! '' }" class="txt" style="width: 200px"/>至<input id="startDate1" name="startDate1" type="date" value="${(startDate1?string("yyyy-MM-dd"))! '' }" class="txt" style="width: 200px"/>
                    </td>

                    <th>协议到期时间：</th>
                    <td>
                        <input id="endDate" name="endDate" type="date" value="${(endDate?string("yyyy-MM-dd"))! '' }" class="txt" style="width: 200px"/>至<input id="endDate1" name="endDate1" type="date" value="${(endDate1?string("yyyy-MM-dd"))! '' }" class="txt" style="width: 200px"/>
                    </td>
                    <th>授权范围：</th>
                    <td>
                        <select id="authType" name="authType" underline="true" style="width: 107px">
                            <option  value="">无</option>
                            <option  value="0">仅2B</option>
                            <option  value="1">2B+云联盟</option>
                            <option  value="2">2B2C</option>
                            <option  value="3">未知</option>
                        </select>
                    </td>
                    <th>内容合作经理：</th>
                    <td>
                        <input id="contentManagerName" name="contentManagerName" type="text" value="${contentManagerName!'' }" class="txt" style="width: 120px"/>
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
                        <th>协议编号</th>
                        <th>协议类型</th>
                        <th>版权所有者</th>
                        <th>协议起始时间</th>
                        <th>协议到期时间</th>
                        <th>授权范围</th>
                        <th>分成比例</th>
                        <th>分成规则</th>
                        <th>是否有自动顺延</th>
                        <th>顺延时间(年)</th>
                        <th>内容合作经理</th>
                        <th>协议签署时间</th>
                        <th>协议状态</th>
                        <th>操作人</th>
                        <th>操作时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#if CopyrightAgreementList??>
                        <#list CopyrightAgreementList as list>
                        <tr class="gradeA odd" role="row">
                            <td align="center">${(list.agreementNum)!''}</td>
                            <td align="center">${(list.agreementType.getDesc())!''}</td>
                            <td align="center">${(list.copyrightOwner)! '' }</td>
                            <td align="center">${(list.startDate?datetime)! '' }</td>
                            <td align="center">${(list.endDate?datetime)!'' }</td>
                            <td align="center">${(list.authType.desc)! '' }</td>
                            <td align="center">${(list.assignPercent)! '' }</td>
                            <td align="center">${((list.assignRule==1)?string('售价分成','定价分成') )}</td>
                            <td align="center">${((list.isAutoPostpone==1)?string('是','否'))}</td>
                            <td align="center">${(list.yearNum)! '' }</td>
                            <td align="center">${(list.contentManagerName) !''}</td>
                            <td align="center">${(list.signDate?datetime)! '' }</td>
                            <td align="center">${((list.status==1)?string('版权期内','版权到期'))}</td>
                            <td align="center">${(list.operator)! '' }</td>
                            <td align="center">${(list.operatedate?datetime)! '' }</td>
                            <td align="center">
                                <a style="cursor:pointer;" onclick="updateAuth('${list.caid! "" }');">编辑&nbsp;</a>
                                <a style="cursor:pointer;" onclick="deleteById('${list.caid! "" }');">删除&nbsp;</a>
                                <a style="cursor:pointer;" onclick="authFileAdd('${list.caid! "" }');">上传附件&nbsp;</a>
                                <a style="cursor:pointer;" onclick="authFileDownload('${list.caid! "" }');">下载附件&nbsp;</a>
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
