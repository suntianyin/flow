<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>修改授权书单信息</title>
    <script type="text/javascript">
        $(function () {
            $("#copyrightOwnerId").val("${(bookList.copyrightOwnerId)!'' }");
            $('#authEndDate').val(time("${(authEndDate)!'' }"));
            $('#submitDate').val(time("${(submitDate)!'' }"));
            $('#obtainDate').val(time("${(obtainDate)!'' }"));
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
            // if (isNull($('#agreementNum').val())) {
            //     tipDialog("协议编号 不能为空！", 3, -1);
            //     return;
            // }
            if (isNull($('#bookListNum').val())) {
                tipDialog("授权书单编号 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#batchNum').val())){
                tipDialog("书单批次编号 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#resType').val())) {
                tipDialog("资源类型 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#copyrightOwnerId').val())) {
                tipDialog("版权所有者 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#aboutNum').val())) {
                tipDialog("文档大概数量 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#validMakeNum').val())) {
                tipDialog("可加工的数量 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#applyNum').val())) {
                tipDialog("需授权资源数量 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#authorizeNum').val())) {
                tipDialog("已授权资源数量 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#authEndDate').val())) {
                tipDialog("版权结束时间 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#submitDate').val())) {
                tipDialog("提交申请授权时间 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#obtainDate').val())) {
                tipDialog("获得授权时间 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#coopertor').val())) {
                tipDialog("内容合作经理 不能为空！", 3, -1);
                return;
            }
                window.setTimeout(function () {
                    var formObj = $("#form1");
                    var postData = JSON.stringify(formObj.serializeJson());
                    $.ajax({
                        url: "${ctx}/bookList/update",
                        type: "POST",
                        data: postData,
                        contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                        async: false,
                        success: function (data) {
                            tipDialog("修改授权书单成功！", 3, 1);
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
        <div>修改授权书单信息<span id="CenterTitle"></span></div>
           </div>
        <form id="form1">
            <table border="0" class="form-find" style="height: 45px;">
                <#--<tr>-->
                    <#--<input id="id" name="id" type="hidden" value="${bookList.id!''}" class="txt" style="width: 300px"/>-->
                    <#--<td>协议编号:</td>-->
                    <#--<td>-->
                        <#--<input id="agreementNum" name="agreementNum" type="text" value="${bookList.agreementNum!''}" class="txt" style="width: 300px"/>-->
                    <#--</td>-->
                <#--</tr>-->
                <tr>
                    <td>授权书单编号:</td>
                    <td>
                        <input id="id" name="id" type="hidden" value="${bookList.id!''}" class="txt" style="width: 300px"/>
                        <input id="bookListNum" name="bookListNum" type="text" value="${bookList.bookListNum!''}" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>书单批次编号:</td>
                    <td>
                        <input id="batchNum" name="batchNum" type="text" value="${bookList.batchNum!''}" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>资源类型:</td>
                    <td>
                        <select id="resType" name="resType" underline="true" style="width: 307px; height: 24px;">
                            <option value="电子书">电子书</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>版权所有者:</td>
                    <td>
                        <select id="copyrightOwnerId" name="copyrightOwnerId" underline="true" style="width: 307px; height: 24px;">
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
                    <td>文档大概数量:</td>
                    <td>
                        <input id="aboutNum" name="aboutNum" type="text" value="${bookList.aboutNum!''}" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>可加工的数量:</td>
                    <td>
                        <input id="validMakeNum" name="validMakeNum" type="text" value="${bookList.validMakeNum!''}" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>

                <tr>
                    <td>需授权资源数量：</td>
                    <td>
                        <input id="applyNum" name="applyNum" type="text" class="txt" value="${bookList.applyNum!''}"
                               style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>已授权资源数量：</td>
                    <td>
                        <input id="authorizeNum" name="authorizeNum" type="text" class="txt" value="${bookList.authorizeNum!''}"
                               style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>

                <tr>
                    <td>版权结束时间:</td>
                    <td>
                        <input id="authEndDate" name="authEndDate" type="date" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>提交申请授权时间:</td>
                    <td>
                        <input id="submitDate" name="submitDate" type="date" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>

                <tr>
                    <td>获得授权时间:</td>
                    <td>
                        <input id="obtainDate" name="obtainDate" type="date" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>内容合作经理:</td>
                    <td>
                        <input id="coopertor" name="coopertor" type="text" value="${bookList.coopertor!''}" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
             </table>
        </form>
    </div>
</div>
</body>
</html>