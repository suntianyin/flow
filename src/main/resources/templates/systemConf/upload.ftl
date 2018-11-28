<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <title>上传文件</title>
    <script type="text/javascript">
        //解析xml文件
        function uploadFile() {
            var url = "/upload/uploadInfo";
            openDialog(url, "uploadInfo", "上传文件", 500, 200, function (iframe) {
                top.frames[iframe].AcceptClick()
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
                            <input id="uploadFile" type="button" class="btnSearch" value="上传文件"
                                   onclick="uploadFile()"/>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
