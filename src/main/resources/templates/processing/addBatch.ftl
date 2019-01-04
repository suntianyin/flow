<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
<#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <link href="${ctx}/css/select2/select2.min.css" rel="stylesheet"/>
    <script src="${ctx}/js/select2/select2.min.js"></script>
    <meta charset="UTF-8">
    <title>新增批次</title>
    <script type="text/javascript">

        //下拉列表 模糊查询
        $(document).ready(function () {
            $('.js-example-basic-single').select2();
        });

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

            if (isNull($('#manager').val())) {
                tipDialog("内容合作经理 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#batchId').val())) {
                tipDialog("批次号 不能为空！", 3, -1);
                return;
            }
            // if (isNull($('#outUnit').val())){
            //     tipDialog("外协单位 不能为空！", 3, -1);
            //     return;
            // }
            if (isNull($('#sourceType').val())) {
                tipDialog("资源类型 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#copyrightOwner').val())) {
                tipDialog("版权所有者 不能为空！", 3, -1);
                return;
            }

            window.setTimeout(function () {
                var formObj = $("#form1");
                var postData = JSON.stringify(formObj.serializeJson());
                $.ajax({
                    url: "${ctx}/processing/batch/add",
                    type: "POST",
                    contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                    data: postData,
                    async: false,
                    success: function (data) {
                        if (data.status == 200) {
                            tipDialog(data.msg, 3, 1);
                            top.frames[tabiframeId()].location.reload();
                            closeDialog();
                        } else {
                            tipDialog(data.msg, 3, -1);
                        }

                    },
                    error: function (data) {
                        Loading(false);
                        alertDialog(data.responseText, -1);
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
            <div align="center"><span id="CenterTitle">创建批次</span></div>
        </div>
        <form id="form1" enctype="multipart/form-data" method="post">
            <table border="0" class="form-find" style="height: 45px;">
            <#--<tr>
                <td>作者id:</td>
                <td>
                    <input id="id" name="id" type="text" class="txt" style="width: 200px"/>
                </td>
            </tr>-->
                <tr>
                    <td>内容合作经理:</td>
                    <td>
                        <input id="manager" name="manager" type="text" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span
                            style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>批次号:</td>
                    <td>
                        <input id="batchId" name="batchId" type="text" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span
                            style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>外协单位:</td>
                    <td>
                        <select id="outUnit" name="outUnit" underline="true" style="width: 307px; height: 24px;">
                            <option value="">--请选择外协单位--</option>
                            <#if outUnitList??>
                                <#list outUnitList as list>
                                    <option value="${(list.unitId)!''}">${(list.unitName)!''}</option>
                                </#list>
                            </#if>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>资源类型:</td>
                    <td>
                        <select id="sourceType" name="sourceType" underline="true" style="width: 307px; height: 24px;">
                            <option value="0">电子书</option>
                            <option value="1">纸质书</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>版权所有者:</td>
                    <td>
                        <select id="copyrightOwner" name="copyrightOwner" class="js-example-basic-single"
                                underline="true"
                                style="width: 307px;">
                            <option value="">--请选择版权所有者--</option>
                            <#if copyrightOwnerList??>
                                <#list copyrightOwnerList as list>
                                    <option value="${(list.id)!''}">${(list.name)!''}</option>
                                </#list>
                            </#if>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>文档格式:</td>
                    <td>
                        <input id="documentFormat" name="documentFormat" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>文档大概数量:</td>
                    <td>
                        <input id="documentNum" name="documentNum" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>资源路径:</td>
                    <td>
                        <input id="resourcePath" name="resourcePath" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>备注:</td>
                    <td>
                        <textarea id="memo" name="memo" class="txt" style="width: 300px; height: 60px;"></textarea>
                    </td>
                </tr>

            <#--<tr>
                <td>操作人:</td>
                <td>
                    <input id="operator" name="operator" type="text" class="txt" style="width: 200px"/>
                </td>
            </tr>-->
            <#--<tr>
                <td>创建时间：</td>
                <td>
                    <input id="createTime" name="createTime" type="date" class="txt" style="width: 200px"/>
                </td>
            </tr>
            <tr>
                <td>最后更新时间：</td>
                <td>
                    <input id="updateTime" name="updateTime" type="date" pattern="yyyy-MM-dd HH:mm:ss" class="txt"
                           style="width: 200px"/>
                </td>
            </tr>-->
            </table>
        </form>
    </div>
</div>
</body>
</html>