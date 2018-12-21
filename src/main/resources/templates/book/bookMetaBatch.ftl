<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/validator/validator.js"></script>
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>批量获取图书元数据</title>
    <script type="text/javascript">

        //批量删除章节内容
        function batchDelete() {
            confirmDialog("温馨提示", "请确认是否要删除这些图书的流式内容？", function (res) {
                if (res) {
                    var ids = $("#metaId").val();
                    var formData = new FormData();
                    formData.append('metaIds', ids);
                    loading();
                    $.ajax({
                        url: RootPath() + "/book/bookChapterDeleteBatch",
                        type: "POST",
                        data: formData,
                        cache: false,
                        processData: false,
                        contentType: false,
                        success: function (data) {
                            $('.load-circle').hide();
                            if (data == "error") {
                                tipDialog("删除失败！", 3, -1);
                            } else {
                                tipDialog(data + "本图书删除成功！", 3, 1);
                            }
                        },
                        error: function (data) {
                            $('.load-circle').hide();
                            Loading(false);
                            alertDialog(data.responseText, -1);
                        }
                    });
                }
            });
        }

        //批量获取书苑元数据
        function batchAdd() {
            var ids = $("#metaId").val();
            var formData = new FormData();
            formData.append('metaIds', ids);
            loading();
            $.ajax({
                url: RootPath() + "/book/bookMetaBatch",
                type: "POST",
                data: formData,
                cache: false,
                processData: false,
                contentType: false,
                success: function (data) {
                    $('.load-circle').hide();
                    if (data == "error") {
                        tipDialog("获取失败！联系管理员", 3, -1);
                    } else {
                        tipDialog(data + "本图书获取成功！", 3, 1);
                    }
                },
                error: function (data) {
                    $('.load-circle').hide();
                    Loading(false);
                    alertDialog(data.responseText, -1);
                }
            });
        }

        //从接口获取页码和目录
        function getPageAndCata() {
            var dridMin = $("#dridMin").val();
            if (dridMin == "") {
                tipDialog("DRID不能为空", 3, -1);
                return;
            }
            var dridMax = $("#dridMax").val();
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
            var url = RootPath() + "/book/getPageAndCata?dridMin=" + dridMin + "&dridMax=" + dridMax + "&toEmail=" + toEmail;
            $.ajax({
                url: url,
                success: function (data) {
                    if (data == "success") {
                        tipDialog("已开始获取页码和目录！", 3, 1);
                        $('#pageAndCata').attr("disabled", true);
                    } else {
                        tipDialog("页码和目录获取失败，联系管理员！", 3, -1);
                    }
                },
                error: function () {
                    Loading(false);
                    alertDialog("页码和目录获取失败，联系管理员！", -1);
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
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
            <#--<a id="batchDelete" href="javascript:;" title="批量删除流式内容"
                   class="tools_btn"><span><i class="fa fa-minus"></i>&nbsp;批量删除流式内容</span></a>
                <a id="batchAdd" href="javascript:;" title="批量获取书苑元数据"
                   class="tools_btn"><span><i class="fa fa-plus"></i>&nbsp;批量获取书苑元数据</span></a>-->
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
                            <input id="batchDelete" type="button" class="btnSearch" value="批量删除流式内容"/>
                            <input id="batchAdd" type="button" class="btnSearch" value="批量获取书苑元数据"/>
                            <span>（METAID之间以回车键换行分隔，不要有空行）</span>
                        </td>
                    </tr>
                    <tr>
                        <th>DRID：</th>
                        <td>
                            <textarea id="drid" name="drid" rows="10" cols="50"></textarea>
                            <input id="getMetaByDrid" type="button" class="btnSearch" value="根据drid获取书苑数据"/>
                            （DRID之间以回车键换行分隔，不要有空行）
                        </td>
                    </tr>
                    <tr>
                        <th>DRID：</th>
                        <td>
                            <input id="dridMin" type="text" class="txt" style="width: 150px"/>-
                            <input id="dridMax" type="text" class="txt" style="width: 150px"/>
                            <input id="pageAndCata" type="button" class="btnSearch" value="获取目录和页码"
                                   onclick="getPageAndCata()"/>
                            （获取该范围的目录和页码，如果上限不输入，则取书苑最大值）
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
<script type="text/javascript">
    //批量删除流式内容
    $("#batchDelete").click(function () {
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
        confirmDialog("温馨提示", "请确认是否要删除这些图书的流式内容？", function (res) {
            if (res) {
                var formData = new FormData();
                formData.append('metaIds', ids);
                formData.append('toEmail', toEmail);
                $.ajax({
                    url: RootPath() + "/book/bookChapterDeleteEmail",
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

    //批量获取书苑元数据
    $("#batchAdd").click(function () {
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
        confirmDialog("温馨提示", "请确认是否要从书苑获取这些图书？", function (res) {
            if (res) {
                var formData = new FormData();
                formData.append('metaIds', ids);
                formData.append('toEmail', toEmail);
                $.ajax({
                    url: RootPath() + "/book/bookMetaBatchEmail",
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

    //根据drid，批量获取书苑元数据
    $("#getMetaByDrid").click(function () {
        var drids = $("#drid").val();
        if (drids == "") {
            tipDialog("DRID不能为空", 3, -1);
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
        confirmDialog("温馨提示", "请确认是否要从书苑获取这些图书？", function (res) {
            if (res) {
                var formData = new FormData();
                formData.append('drids', drids);
                formData.append('toEmail', toEmail);
                $.ajax({
                    url: RootPath() + "/book/getMetaByDrid",
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
