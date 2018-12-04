<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
<#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <link href="${ctx}/css/select2/select2.min.css" rel="stylesheet"/>
    <script src="${ctx}/js/select2/select2.min.js"></script>
    <meta charset="UTF-8">
    <title>单本书目录入</title>
    <script type="text/javascript">

        //下拉列表 模糊查询
        $(document).ready(function () {
            $('.js-example-basic-single').select2();
        });

        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        }

        function Check(){
            var  isbn= document.getElementById("isbn").value;
            // alert(isbn);
            $.ajax({
                url: "${ctx}/processing/bibliotheca/checkIsbn?isbn="+isbn,
                type: "GET",
                contentType: false,
                async: false,
                success: function (data) {
                    if (data.status == 200) {
                        tipDialog(data.msg, 3, 1);
                        // top.frames[tabiframeId()].location.reload();
                        // closeDialog();
                    } else {
                        tipDialog(data.msg, 3, -1);
                    }

                },
                error: function (data) {
                    Loading(false);
                    alertDialog(data.responseText, -1);
                }
            });
        }
        //保存事件
        function AcceptClick() {
            $("#loading").show();
            Loading(true, "正在提交数据...");

            if (isNull($('#identifier').val())) {
                tipDialog("编号 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#title').val())) {
                tipDialog("标题 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#originalFilename').val())) {
                tipDialog("原文件名 不能为空！", 3, -1);
                return;
            }

            window.setTimeout(function () {
                var formObj = $("#form1");
                var postData = JSON.stringify(formObj.serializeJson());
                $.ajax({
                    url: "${ctx}/processing/bibliotheca/add",
                    type: "POST",
                    contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                    data: postData,
                    async: false,
                    success: function (data) {
                        if (data.status == 200) {
                            tipDialog(data.msg, 3, 1);
                            top.frames[tabiframeId()].location.reload();
                            closeDialog();
                        } else {
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
            <div align="center"><span id="CenterTitle">单本书目录入</span></div>
        </div>
        <form id="form1" enctype="multipart/form-data" method="post">
            <table border="0" class="form-find" style="height: 45px;">
            <#--<tr>
                <td>作者id:</td>
                <td>
                    <input id="id" name="id" type="text" class="txt" style="width: 200px"/>
                </td>
            </tr>-->
                <tr>
                    <td>编号:</td>
                    <td>
                        <input type="text" id="batchId" name="batchId" hidden value="${batchId}"/>
                        <input id="identifier" name="identifier" type="text" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span
                            style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>标题:</td>
                    <td>
                        <input id="title" name="title" type="text" class="txt"
                               style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>原文件名:</td>
                    <td>
                        <input id="originalFilename" name="originalFilename" type="text" class="txt"
                               style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>书目状态:</td>
                    <td>
                        <select id="bibliothecaState" name="bibliothecaState" class="js-example-basic-single" underline="true"
                                style="width: 307px;" >
                            <option value="0">新建</option>
                            <option value="4">信息不全</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>作者:</td>
                    <td>
                        <input id="author" name="author" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>出版社:</td>
                    <td>
                        <select id="publisher" name="publisher" class="js-example-basic-single" underline="true"
                                style="width: 307px;">
                            <option value="">--请选择出版社--</option>
                            <#if publisherList??>
                                <#list publisherList as list>
                                    <option value="${(list.id)!''}">${(list.title)!''}</option>
                                </#list>
                            </#if>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>ISBN:</td>
                    <td>
                        <input id="isbn" name="isbn" type="text" class="txt" style="width: 300px" onblur="Check()"/>
                    </td>
                </tr>
                <tr>
                    <td>出版时间:</td>
                    <td>
                        <input id="publishTime" name="publishTime" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>版次:</td>
                    <td>
                        <input id="edition" name="edition" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>纸书价格:</td>
                    <td>
                        <input id="paperPrice" name="paperPrice" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>文档格式:</td>
                    <td>
                        <input id="documentFormat" name="documentFormat" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
</body>
</html>