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
    <title>修改授权协议信息</title>
    <script type="text/javascript">

        $(function () {
            $("#authType").val("${(copyrightAgreement.authType.getCode())!'' }");
            $("#agreementType").val("${(copyrightAgreement.agreementType.getCode())!'' }");
            $("#assignRule").val("${(copyrightAgreement.assignRule)!'' }");
            $("#isinternetCommunication").val("${(copyrightAgreement.isinternetCommunication)!'' }");
            $("#isCopy").val("${(copyrightAgreement.isCopy)!'' }");
            $("#isPublishing").val("${(copyrightAgreement.isPublishing)!'' }");
            $("#isHireRight").val("${(copyrightAgreement.isHireRight)!'' }");
            $("#isExhibitionRight").val("${(copyrightAgreement.isExhibitionRight)!'' }");
            $("#isPerformanceRight").val("${(copyrightAgreement.isPerformanceRight)!'' }");
            $("#isScreeningRight").val("${(copyrightAgreement.isScreeningRight)!'' }");
            $("#isBroadcastingRight").val("${(copyrightAgreement.isBroadcastingRight)!'' }");
            $("#isAdaptationRight").val("${(copyrightAgreement.isAdaptationRight)!'' }");
            $("#isTranslationRight").val("${(copyrightAgreement.isTranslationRight)!'' }");
            $("#isEditRight").val("${(copyrightAgreement.isEditRight)!'' }");
            $("#isTransfeRright").val("${(copyrightAgreement.isTransfeRright)!'' }");
            $("#isContentworkRight").val("${(copyrightAgreement.isContentworkRight)!'' }");
            $("#isAgentmaintainlegalRight").val("${(copyrightAgreement.isAgentmaintainlegalRight)!'' }");
            $("#isOnlyOwner").val("${(copyrightAgreement.isOnlyOwner)!'' }");
            $("#isHasAuthorRight").val("${(copyrightAgreement.isHasAuthorRight)!'' }");
            $("#isAutoPostpone").val("${(copyrightAgreement.isAutoPostpone)!'' }");
            $('#startDate').val(time("${(startDate)!'' }"));
            $('#endDate').val(time("${(endDate)!'' }"));
            $('#signDate').val(time("${(signDate)!'' }"));
            $('#obtainDate').val(time("${(obtainDate)!'' }"));
            $("#copyrightOwnerId").val("${(copyrightAgreement.copyrightOwnerId)!'' }");

        });
        //下拉列表 模糊查询
        $(document).ready(function () {
            $('.js-example-basic-single').select2();
        });
        function time(now1){
            var now=new Date(now1);
            //格式化日，如果小于9，前面补0
            var day = ("0" + now.getDate()).slice(-2);
            //格式化月，如果小于9，前面补0
            var month = ("0" + (now.getMonth() + 1)).slice(-2);
            //拼装完整日期格式
            var today = now.getFullYear()+"-"+(month)+"-"+(day) ;
            return today;
        }
        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        }
        //保存事件
        function AcceptClick() {
                $("#loading").show();
                Loading(true, "正在提交数据...");
            if (isNull($('#agreementNum').val())) {
                tipDialog("协议编号 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#copyrightOwnerId').val())) {
                tipDialog("版权所有者 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#contentManagerName').val())){
                tipDialog("内容合作经理 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#startDate').val())) {
                tipDialog("协议起始时间 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#endDate').val())) {
                tipDialog("协议到期时间 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#authType').val())) {
                tipDialog("授权范围 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#assignPercent').val())) {
                tipDialog("分成比例 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#assignRule').val())) {
                tipDialog("分成规则 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#isinternetCommunication').val())) {
                tipDialog("是否有网路传播权 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#isCopy').val())) {
                tipDialog("是否有数字化复制权 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#isPublishing').val())) {
                tipDialog("是否有发行权 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#isHireRight').val())) {
                tipDialog("是否有出租权 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#isExhibitionRight').val())) {
                tipDialog("是否有展览权 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#isPerformanceRight').val())) {
                tipDialog("是否有表演权 不能为空！", 3, -1);
                return;
            }if (isNull($('#isScreeningRight').val())) {
                tipDialog("是否有放映权 不能为空！", 3, -1);
                return;
            }if (isNull($('#isBroadcastingRight').val())) {
                tipDialog("是否有广播权 不能为空！", 3, -1);
                return;
            }if (isNull($('#isAdaptationRight').val())) {
                tipDialog("是否有改编权 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#isTranslationRight').val())) {
                tipDialog("是否有翻译权 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#isEditRight').val())) {
                tipDialog("是否有汇编权 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#isTransfeRright').val())) {
                tipDialog("是否有转授权 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#isContentworkRight').val())) {
                tipDialog("是否有再制作权 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#isAgentmaintainlegalRight').val())) {
                tipDialog("是否授权代理维权 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#isOnlyOwner').val())) {
                tipDialog("是否独家授权 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#isHasAuthorRight').val())) {
                tipDialog("是否提供作者授权 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#isAutoPostpone').val())) {
                tipDialog("是否自动顺延 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#signDate').val())) {
                tipDialog("协议签署时间 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#obtainDate').val())) {
                tipDialog("获得授权协议时间 不能为空！", 3, -1);
                return;
            }
                window.setTimeout(function () {
                    var formObj = $("#form1");
                    var postData = JSON.stringify(formObj.serializeJson());
                    $.ajax({
                        url: "${ctx}/auth/update",
                        type: "POST",
                        data: postData,
                        contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                        async: false,
                        success: function (data) {
                            tipDialog("修改授权协议成功！", 3, 1);
                            top.frames[tabiframeId()].location.reload();
                            closeDialog();
                        },
                        error: function (data) {
                            Loading(false);
                            alertDialog("输入信息有误，请核实后提交", -1);
                        }
                    });
                }, 200);
        }

    </script>

</head>
<body>
<div id="grid_List">
    <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
        <div class="btnbartitle">
        <div>修改授权协议信息<span id="CenterTitle"></span></div>
           </div>
        <form id="form1">
        <#--<form id="form1" enctype="multipart/form-data" action="${ctx}/publisher/doedit" method="post" >-->

            <table border="0" class="form-find" style="height: 45px;">
                <tr>
                    <input id="caid" name="caid" value="${(copyrightAgreement.caid)!''}" type="hidden" class="txt" style="width: 300px"/>
                    <td>协议编号:</td>
                    <td>
                        <input id="agreementNum" name="agreementNum" value="${(copyrightAgreement.agreementNum)!''}" type="text" class="txt" style="width: 300px"/>
                        &nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                    <td>协议类型：</td>
                    <td>
                        <select id="agreementType" name="agreementType"  underline="true" style="width: 307px; height: 24px;">
                            <option  value="0">电子书</option>
                            <option  value="1">年鉴</option>
                            <option  value="2">工具书</option>
                            <option  value="3">特色资源</option>
                            <option  value="4">作者签约协议</option>
                            <option  value="5">报纸</option>
                            <option  value="6">图片库</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>协议名称:</td>
                    <td>
                        <input id="agreementTitle" name="agreementTitle" value="${(copyrightAgreement.agreementTitle)!''}" type="text" class="txt" style="width: 300px"/>
                    </td>
                    <td>版权所有者:</td>
                    <td>
                        <select id="copyrightOwnerId" name="copyrightOwnerId" class="js-example-basic-single" underline="true" style="width: 307px; height: 24px;">
                            <option value="">--请选择版权所有者--</option>
                            <#if copyrightOwners??>
                                <#list copyrightOwners as list>
                                    <option value="${(list.id)!''}">${(list.name)!''}</option>
                                </#list>
                            </#if>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>

                <tr>
                    <td>内容合作经理:</td>
                    <td>
                        <input id="contentManagerName" name="contentManagerName" value="${(copyrightAgreement.contentManagerName)!''}" type="text" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>

                    <td>协议起始时间：</td><#--${(copyrightAgreement.startDate?datetime)}-->
                    <td>
                        <input id="startDate" name="startDate" type="date" class="txt"
                               style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>

                <tr>
                    <td>协议到期时间：</td>
                    <td>
                        <input id="endDate" name="endDate" type="date" class="txt" value="${(copyrightAgreement.endDate?datetime)!''}"
                               style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>

                    <td>授权范围:</td>
                    <td>
                        <select id="authType" name="authType" underline="true" style="width: 300px">
                            <option  value="0">仅2B</option>
                            <option  value="1">2B+云联盟</option>
                            <option  value="2">2B2C</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>

                <tr>
                    <td>分成比例:</td>
                    <td>
                        <input id="assignPercent" name="assignPercent" value="${(copyrightAgreement.assignPercent)!''}" type="text" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>

                    <td>分成规则:</td>
                    <td>
                        <select id="assignRule" name="assignRule" underline="true" style="width: 300px">
                            <option  value="0">定价分成</option>
                            <option  value="1">售价分成</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>

                <tr>
                    <td>是否有网路传播权:</td>
                    <td>
                        <select id="isinternetCommunication" name="isinternetCommunication" underline="true" style="width: 300px">
                            <option value="0">否</option>
                            <option  value="1">是</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>

                    <td>是否有数字化复制权:</td>
                    <td>
                        <select id="isCopy" name="isCopy" underline="true" style="width: 300px">
                            <option value="0">否</option>
                            <option value="1">是</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>

                <tr>
                    <td>是否有发行权:</td>
                    <td>
                        <select id="isPublishing" name="isPublishing" underline="true" style="width: 300px">
                            <option value="0">否</option>
                            <option  value="1">是</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>

                    <td>是否有出租权:</td>
                    <td>
                        <select id="isHireRight" name="isHireRight" underline="true" style="width: 300px">
                            <option value="0">否</option>
                            <option  value="1">是</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>

                <tr>
                    <td>是否有展览权:</td>
                    <td>
                        <select id="isExhibitionRight" name="isExhibitionRight" underline="true" style="width: 300px">
                            <option value="0">否</option>
                            <option value="1">是</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>

                    <td>是否有表演权:</td>
                    <td>
                        <select id="isPerformanceRight" name="isPerformanceRight" underline="true" style="width: 300px">
                            <option value="0">否</option>
                            <option  value="1">是</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>是否有放映权:</td>
                    <td>
                        <select id="isScreeningRight" name="isScreeningRight" underline="true" style="width: 300px">
                            <option value="0">否</option>
                            <option value="1">是</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>

                    <td>是否有广播权:</td>
                    <td>
                        <select id="isBroadcastingRight" name="isBroadcastingRight" underline="true" style="width: 300px">
                            <option value="0">否</option>
                            <option  value="1">是</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>是否有改编权:</td>
                    <td>
                        <select id="isAdaptationRight" name="isAdaptationRight" underline="true" style="width: 300px">
                            <option value="0">否</option>
                            <option  value="1">是</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>

                    <td>是否有翻译权:</td>
                    <td>
                        <select id="isTranslationRight" name="isTranslationRight" underline="true" style="width: 300px">
                            <option value="0">否</option>
                            <option  value="1">是</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>

                <tr>
                    <td>是否有汇编权:</td>
                    <td>
                        <select id="isEditRight" name="isEditRight" underline="true" style="width: 300px">
                            <option value="0">否</option>
                            <option  value="1">是</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>

                    <td>是否有转授权:</td>
                    <td>
                        <select id="isTransfeRright" name="isTransfeRright" underline="true" style="width: 300px">
                            <option value="0">否</option>
                            <option selected="selected" value="1">是</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>

                <tr>
                    <td>是否有再制作权:</td>
                    <td>
                        <select id="isContentworkRight" name="isContentworkRight" underline="true" style="width: 300px">
                            <option value="0">否</option>
                            <option  value="1">是</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>

                    <td>有其他相关著作权:</td>
                    <td>
                        <input id="isOtherRight" name="isOtherRight" type="text" class="txt" value="${(copyrightAgreement.isOtherRight)!''}"
                               style="width: 300px"/>
                    </td>
                </tr>

                <tr>
                    <td>是否授权代理维权:</td>
                    <td>
                        <select id="isAgentmaintainlegalRight" name="isAgentmaintainlegalRight" underline="true" style="width: 300px">
                            <option value="0">否</option>
                            <option value="1">是</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>

                    <td>是否独家授权:</td>
                    <td>
                        <select id="isOnlyOwner" name="isOnlyOwner" underline="true" style="width: 300px">
                            <option value="0">否</option>
                            <option  value="1">是</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>

                <tr>
                    <td>是否提供作者授权:</td>
                    <td>
                        <select id="isHasAuthorRight" name="isHasAuthorRight" underline="true" style="width: 300px">
                            <option  value="0">否</option>
                            <option  value="1">是</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>

                    <td>是否自动顺延:</td>
                    <td>
                        <select id="isAutoPostpone" name="isAutoPostpone" underline="true" style="width: 300px">
                            <option value="0">否</option>
                            <option value="1">是</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>

                <tr>
                    <td>顺延时间(年):</td>
                    <td>
                        <input id="yearNum" name="yearNum" type="text" value="${(copyrightAgreement.yearNum)!''}" class="txt" style="width: 300px"/>
                    </td>

                    <td>协议签署时间:</td>
                    <td>
                        <input id="signDate" name="signDate" type="date" value="${(copyrightAgreement.signDate?datetime)!''}" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>

                <tr>
                    <td>获得授权协议时间：</td>
                    <td>
                        <input id="obtainDate" name="obtainDate" type="date" value="${(copyrightAgreement.obtainDate?datetime)!''}" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
             </table>
        </form>
    </div>
</div>
</body>
</html>