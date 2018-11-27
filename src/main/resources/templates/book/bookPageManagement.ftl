<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>cebx流式内容管理</title>
    <script type="text/javascript">

        function btn_Crawled() {
            window.location.href = "${ctx}/book/pageCrawled";
        }
        function btn_Assembly() {
            window.location.href = "${ctx}/book/pageAssembly";
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
        <input id="Crawled" type="button" class="btnSearch" value="采集加密流式内容" onclick="btn_Crawled()"/>
        <input id="Assembly" type="button" class="btnSearch" value="分页流式内容拼装" onclick="btn_Assembly()"/>
    </div>
</div>
</body>
</html>
