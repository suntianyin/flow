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
        $(function () {

            $("#batch-import").click(function () {
                var fileObj = document.getElementById("importFile").files[0]; // js 获取文件对象
                if (typeof (fileObj) == "undefined" || fileObj.size <= 0) {
                    alert("请选择文件");
                    return;
                }
                var formFile = new FormData();
//                formFile.append("action", "${ctx}/author/batch/import");
                formFile.append("file", fileObj); //加入文件对象

                //第一种  XMLHttpRequest 对象
                //var xhr = new XMLHttpRequest();
                //xhr.open("post", "/Admin/Ajax/VMKHandler.ashx", true);
                //xhr.onload = function () {
                //    alert("上传完成!");
                //};
                //xhr.send(formFile);

                //第二种 ajax 提交

                var data = formFile;
                $.ajax({
                    url: "${ctx}/book/batch/import",
                    data: data,
                    type: "POST",
                    dataType: "text",
                    cache: false,//上传文件无需缓存
                    processData: false,//用于对data参数进行序列化处理 这里必须false
                    contentType: false, //必须
                    success: function (result) {
                        if (result == '成功') {
                            tipDialog("批量导入成功", 3, 1);
                            btn_Search();
                            return;
                        } else {
                            tipDialog("批量导入失败", 3, -1);
                            return;
                        }
                    },
                    error: function (result) {
                        tipDialog("批量导入失败", 3, -1);
                    }
                })
            })

        });
        $(function () {
            var pathurl = "bookPageManagement?";
            var totalPages = ${pages?c};
            var currentPages = ${pageNum?c};
            jqPaging(pathurl, totalPages, currentPages);
        });

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
                async: false,
                success: function (data) {
                    alertDialog(data.msg, data.status);
                    window.location.href = "bookPageManagement";
                },
                error: function (data) {
                    alertDialog(data.msg, data.status);
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
                async: false,
                success: function (data) {
                    alertDialog(data.msg, data.status);
                    window.location.href = "bookPageManagement";
                },
                error: function (data) {
                    alertDialog(data.msg, data.status);
                    $(".load-circle").hide();
                }
            });
        }
        function btn_Again() {
            // window.location.href = "autoFetchPageData";
            var url = "/book/autoFetchPageDataAgain";
            loading();
            $.ajax({
                url: RootPath() + url,
                type: "get",
                dataType: "json",
                cache: false,
                contentType: false,
                async: false,
                success: function (data) {
                    alertDialog(data.msg, data.status);
                    window.location.href = "bookPageManagement";
                },
                error: function (data) {
                    alertDialog(data.msg, data.status);
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
                async: false,
                success: function (data) {
                    alertDialog(data.msg, data.status);
                    window.location.href = "bookPageManagement";
                },
                error: function (data) {
                    alertDialog("章节拼装失败，请联系管理员", -1);
                    $(".load-circle").hide();
                }
            });
        }

        function btn_flush() {
            window.location.href = "bookPageManagement";
        }
        function btn_fetch(){
            window.location.href = "fetch";
        }

        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        };

        function pageCrawledQueues(id) {
            var note = "注：您确定要删除当前采集加密ID？";
            if (!isNull(id)) {
                note = "注：您确定要删除 采集加密ID为：" + id + " 的信息？";
            }
            confirmDialog("温馨提示", note, function (r) {
                if (r) {
                    window.location.href = "${ctx}/book/pageCrawledQueuesDelete?id=" + id;
                }
            })
        }

        function pageAssemblyQueues(id) {
            window.location.href = "pageAssemblyQueuesDelete?id=" + id;
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
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <input id="importFile" type="file" class="tools_btn" accept=".xlsx" name="file""/>
                <div class="tools_separator"></div>
            </div>

            <div class="PartialButton">
                <button id="batch-import" title="批量导入" class="tools_btn"><span><i
                        class="fa fa-plus"></i>&nbsp批量导入</span></button>
                <div class="tools_separator"></div>
            </div>
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
        <input id="Crawled" type="button" class="btnSearch" value="刷新" onclick="btn_flush()"/>
        <input id="fetch" type="button" class="btnSearch" value="整理去重" onclick="btn_fetch()"/>
        <input id="Crawled" type="button" class="btnSearch" value="采集加密流式内容" onclick="btn_Crawled()"/>
        <input id="again" type="button" class="btnSearch" value="重新抽取失败内容" onclick="btn_Again()"/>
        <input id="Assembly" type="button" class="btnSearch" value="分页流式内容拼装" onclick="btn_Assembly()"/>
        <p>重新抽取失败内容条数：${num}</p>
        <div class="panel-body">
            <div class="row">
                <table id="table-list"
                       class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                    <thead>
                    <tr role="row">
                        <th width="50%">采集加密ID</th>
                        <th width="50%">操作</th>
                    <#--<th width="25%">章节拼装ID</th>-->
                    <#--<th width="25%">操作</th>-->
                    </tr>
                    </thead>
                    <tbody>
                        <#list pageCrawledQueues as list>
                            <tr class="gradeA odd" role="row">
                                <td align="center">${(list.id)!'' }</td>
                                <td align="center"><input type="button" value="删除"
                                                          onclick="pageCrawledQueues('${list.id }')"/></td>
                            </tr>
                        </#list>
                    <#--<#list pageAssemblyQueues as list>-->
                    <#--<td align="center">${(list.id)!'' }</td>-->
                    <#--<td align="center"><input type="button" value="删除" onclick="pageAssemblyQueues('${list.id }')"/></td>-->
                    <#--</#list>-->

                    </tbody>
                </table>

            </div>
            <ul class="pagination">
                <li>每页 ${pageSize!0} 条记录，共 ${pages!0} 页，共 ${total!0} 条记录</li>
            </ul>
            <ul class="pagination" style="float:right;" id="pagination"></ul>
        </div>
    </div>
</div>
</body>
</html>
