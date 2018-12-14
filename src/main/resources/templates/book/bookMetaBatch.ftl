<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
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

        //批量获取
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
            var drid = $("#drid").val();
            if (drid == "") {
                tipDialog("DRID为空", 3, -1);
                return;
            }
            var url = RootPath() + "/book/getPageAndCata?drid=" + drid;
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
                <a id="lr-xmlAdd" href="javascript:;" title="批量删除流式内容" onclick="batchDelete()"
                   class="tools_btn"><span><i class="fa fa-minus"></i>&nbsp;批量删除流式内容</span></a>
                <a id="lr-xmlAdd" href="javascript:;" title="批量获取书苑元数据" onclick="batchAdd()"
                   class="tools_btn"><span><i class="fa fa-plus"></i>&nbsp;批量获取书苑元数据</span></a>
            </div>
        </div>
        <!--列表-->
        <div id="grid_List">
            <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
                <table border="0" class="form-find" style="height: 45px;">
                    <tr align="center">
                        <th>METAID：</th>
                        <td>
                            <textarea id="metaId" name="metaId" rows="10" cols="50"></textarea>
                            （METAID之间以回车键换行分隔，不要有空行）
                        </td>
                        <th>DRID：</th>
                        <td>
                            <input id="drid" type="text" class="txt" style="width: 200px"/>
                        <td>
                            <input id="pageAndCata" type="button" class="btnSearch" value="获取目录和页码"
                                   onclick="getPageAndCata()"/>
                            （将获取大于等于该drid的数据的目录和页码）
                        </td>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
