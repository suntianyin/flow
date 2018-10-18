<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>批量上传结果</title>
</head>
<body>
<div id="layout" class="layout">
    <!--中间-->
    <div class="layoutPanel layout-center">
        <!--列表-->
        <div id="grid_List">
            <div class="panel-body">
                <div class="row">
                    <table id="table-list"
                           class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                        <thead>
                        <#if batchResList?? >
                            图书上传结果数据${batchResList?size }条。备注：上传失败的图书可能已经上传，请不要重复点击批量上传按钮。
                        <#else>
                            图书上传结果数据0条
                        </#if>
                        <tr role="row">
                            <th>文件名</th>
                            <th>图书ID</th>
                            <th>上传结果</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#if batchResList?? >
                            <#list batchResList as list>
                                <tr class="gradeA odd" role="row">
                                    <td>${list.fileName!'' }</td>
                                    <td>${list.metaId!'' }</td>
                                    <td>
                                        <#if list.status??>
                                            <#if list.status == 0>
                                                失败
                                            <#else>
                                                成功
                                            </#if>
                                        <#else>
                                            失败
                                        </#if>
                                    </td>
                                </tr>
                            </#list>
                        </#if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script>
    //保存事件
    function AcceptClick() {
        closeDialog();
    }
</script>
</html>
