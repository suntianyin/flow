<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/validator/validator.js"></script>
    <title>批量资源上下架</title>
</head>
<body>
<div class='load-circle' style="display: none;"></div>
<div id="layout" class="layout">
    <div class="btnbartitle">
        <div>批量资源上下架<span id="CenterTitle"></span></div>
    </div>
    <!--中间-->
    <div class="layoutPanel layout-center">
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a onclick="javascript:history.back(-1);"
                   class="tools_btn"><span><i class="fa fa-backward"></i>&nbsp返回</span></a>
            </div>
        </div>
        <!--列表-->
        <div id="grid_List">
            <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
                <table border="0" class="form-find" style="height: 45px;">
                    <tr>
                        <th>收件人：</th>
                        <td>
                            <input id="toEmail" name="toEmail" type="text" class="txt" style="width: 200px"/>
                            （仅可输入一个收件人）
                        </td>
                    </tr>
                    <tr align="center">
                        <th>METAID：</th>
                        <td>
                            <textarea id="metaId" name="metaId" rows="10" cols="50"></textarea>
                            <span>（METAID之间以回车键换行分隔，不要有空行）</span>
                        </td>
                        <th>资源上下架：</th>
                        <td>
                            <select id="saleStatus" name="saleStatus" class="txtselect">
                                <option value="1">上架</option>
                                <option value="0">下架</option>
                            </select>
                            <input id="saleUpOrDown" type="button" class="btnSearch" value="批量资源上下架"/>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
<script type="text/javascript">
    //批量资源上下架
    $("#saleUpOrDown").click(function () {
        var ids = $("#metaId").val();
        if (ids == "") {
            tipDialog("METAID不能为空", 3, -1);
            return;
        }
        var toEmail = $("#toEmail").val();
        if (toEmail == "") {
            tipDialog("收件人不能为空", 3, -1);
            return;
        } else {
            var res = isEmail(toEmail);
            if (res == false) {
                tipDialog("请检查收件邮箱", 3, -1);
                return;
            }
        }
        var saleStatus = $("#saleStatus").val();
        var saleStatusDec;
        if (saleStatus == 1) {
            saleStatusDec = "上架";
        } else if (saleStatus == 0) {
            saleStatusDec = "下架";
        }
        confirmDialog("温馨提示", "请确认信息<br>资源上下架：" + saleStatusDec, function (res) {
            if (res) {
                var formData = new FormData();
                formData.append('metaIds', ids);
                formData.append('toEmail', toEmail);
                formData.append('saleStatus', saleStatus);
                $.ajax({
                    url: RootPath() + "/book/saleUpOrDown",
                    type: "POST",
                    data: formData,
                    cache: false,
                    processData: false,
                    contentType: false,
                    success: function (data) {
                        if (data == "success") {
                            tipDialog("正在操作，请注意查收邮件！", 3, 1);
                        } else if (data == "error") {
                            tipCss('批量操作', '失败，联系管理员！');
                        }
                    },
                    error: function (data) {
                        Loading(false);
                        alertDialog(data.responseText, -1);
                    }
                });
            }
        });
    });

</script>
</html>
