<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <title>流式内容检查</title>
    <script type="text/javascript">
        //乱码检查
        function codeDetect() {
            //window.location.href = "codeDetect";
            var url = RootPath() + "/book/codeDetect";
            $.ajax({
                url: url,
                success: function (data) {
                    if (data == "success") {
                        tipDialog("乱码检查已开始，请注意查看邮件！", 3, 1);
                        $('#codeDetect').attr("disabled", true);
                    } else {
                        tipDialog("乱码检查失败，联系管理员！", 3, -1);
                    }
                },
                error: function () {
                    Loading(false);
                    alertDialog("乱码检查失败，联系管理员！", -1);
                }
            });
        }

        //关键词检查
        function sourceDetect() {
            //window.location.href = "sourceDetect";
            var url = RootPath() + "/book/sourceDetect";
            $.ajax({
                url: url,
                success: function (data) {
                    if (data == "success") {
                        tipDialog("关键词检查已开始，请注意查看邮件！", 3, 1);
                        $('#sourceDetect').attr("disabled", true);
                    } else {
                        tipDialog("关键词检查失败，联系管理员！", 3, -1);
                    }
                },
                error: function () {
                    Loading(false);
                    alertDialog("关键词检查失败，联系管理员！", -1);
                }
            });
        }
    </script>
</head>
<body>
<div class='load-circle' style="display: none;"></div>
<div id="layout" class="layout">
    <!--中间-->
    <div class="layoutPanel layout-center">
        <!--列表-->
        <div id="grid_List">
            <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
                <table border="0" class="form-find" style="height: 45px;">
                    <tr>
                        <td>
                            <input id="codeDetect" type="button" class="btnSearch" value="乱码检查"
                                   onclick="codeDetect()"/>
                        </td>
                        <td>
                            <input id="sourceDetect" type="button" class="btnSearch" value="关键词检查"
                                   onclick="sourceDetect()"/>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
