<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>流式内容管理</title>
    <script type="text/javascript">

        function btn_upload() {
            var metaId = document.getElementById("metaIds").value;
            var result = "";
            var metaIds = metaId.split("\n");
            for (var i = 0; i < metaIds.length; i++) {
                result += metaIds[i] + ",";
            }
            var url = "/book/getBookMetaIds?metaIds=" + result;
            loading();
            $.ajax({
                url: RootPath() + url,
                type: "get",
                dataType: "json",
                cache: false,
                contentType: false,
                async:false,
                success: function (data) {
                    alertDialog(data.msg,data.status);
                    $(".load-circle").hide();
                },
                error: function (data) {
                    alertDialog(data.msg,data.status);
                    $(".load-circle").hide();
                }
            });
        }
        function btn_Crawled() {
            // window.location.href = "autoFetchPageData";
            var url = "/book/autoFetchPageData";
            loading();
            $.ajax({
                url: RootPath() + url,
                type: "get",
                dataType: "json",
                cache: false,
                contentType: false,
                async:false,
                success: function (data) {
                    alertDialog(data.msg,data.status);
                    $(".load-circle").hide();
                },
                error: function (data) {
                    alertDialog(data.msg,data.status);
                    $(".load-circle").hide();
                }
            });
        }
        function btn_Assembly() {
            // window.location.href = "autoProcessBookFromPage2Chapter";
            var url = "/book/autoProcessBookFromPage2Chapter";
            loading();
            $.ajax({
                url: RootPath() + url,
                type: "get",
                dataType: "json",
                cache: false,
                contentType: false,
                async:false,
                success: function (data) {
                    alertDialog(data.msg,data.status);
                    $(".load-circle").hide();
                },
                error: function (data) {
                    alertDialog("上传失败，网络传输出现异常",-1);
                    $(".load-circle").hide();
                }
            });
        }

        //页码内容详情展示
        function bookPageContentDetail(metaid, pageid) {
            var url = "/book/bookPageContentDetail?metaid="+metaid+"&pageid="+pageid;
            openDialog(url, "bookPageContentDetail", "页面内容详情 - 第" + pageid + " 页", 800, 500, function (iframe) {
                top.frames[iframe].closeDialog();
            });
        }
    </script>
</head>
<body>
<div id="layout" class="layout">
    <!--中间-->
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>流式内容管理<span id="CenterTitle"></span></div>
        </div>
        <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
            <table border="0" class="form-find" style="height: 45px;">
                <tr align="center">
                    <th>METAID：</th>
                    <td>
                        <textarea id="metaIds" name="metaIds" rows="10" cols="50"></textarea>
                    </td>
                    <td>
                        <input id="btnSearch" type="button" class="btnSearch" value="上传" onclick="btn_upload()"/>（METAID之间以回车键换行分隔）
                    </td>
                </tr>
            </table>
        </div>
        <input id="Crawled" type="button" class="btnSearch" value="采集加密流式内容" onclick="btn_Crawled()"/>
        <input id="Assembly" type="button" class="btnSearch" value="分页流式内容拼装" onclick="btn_Assembly()"/>
    </div>
</div>
</body>
</html>
