<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
<#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <link href="${ctx}/css/select2/select2.min.css" rel="stylesheet" />
    <script src="${ctx}/js/select2/select2.min.js"></script>
    <meta charset="UTF-8">
    <title>书单审核</title>
    <script type="text/javascript">

        $(function () {
            $("input[type=radio][name='batchState'][value=${(batch.batchState.getCode())!''}]").attr("checked",'checked');
        });

        //下拉列表 模糊查询
        $(document).ready(function() {
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

            if (isNull($('#id').val())){
                tipDialog("唯一标识id 数据异常！", 3, -1);
                return;
            }
            if (isNull($('#batchId').val())){
                tipDialog("批次编号id 数据异常！", 3, -1);
                return;
            }

            window.setTimeout(function () {
                var formObj = $("#form1");
                var postData = JSON.stringify(formObj.serializeJson());
                $.ajax({
                    url: "${ctx}/processing/batch/auditBatch",
                    type: "POST",
                    contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                    data: postData,
                    async: false,
                    success: function (data) {
                        if (data.status == 200){
                            tipDialog(data.msg, 3, 1);
                            top.frames[tabiframeId()].location.reload();
                            closeDialog();
                        }else{
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
            <div align="center"><span id="CenterTitle">书单审核</span></div>
        </div>
        <form id="form1" enctype="multipart/form-data" method="post">
            <table border="0" class="form-find" style="height: 45px;">
                <tr>
                    <td></td>
                    <td align="center">
                        <input id="id" name="id" value="${(batch.id)!''}" class="txt" hidden />
                        <input id="batchId" name="batchId" value="${(batch.batchId)!''}" class="txt" hidden />
                        <input type="radio" name="batchState" value="2" id="accessId" /><label for="accessId">审核通过</label>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <input type="radio" name="batchState" value="3" id="failAccessId" /><label for="failAccessId">审核失败</label>
                        &nbsp;&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>失败原因:</td>
                    <td>
                        <textarea id="memo" name="memo" class="txt" style="width: 250px; height: 60px;">${(batch.memo)!''}</textarea>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
</body>
</html>