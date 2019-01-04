<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <link href="${ctx}/css/select2/select2.min.css" rel="stylesheet"/>
    <script src="${ctx}/js/select2/select2.min.js"></script>
    <title>修改协议资源信息</title>
    <script type="text/javascript">
        $(function () {
            $("#status").val("${(resource.status.getCode())!'' }");
            $("#publisher").val("${(resource.publisher)!'' }");
            $('#issuedDate').val(time("${(issuedDate)!'' }"));

        });
        //下拉列表 模糊查询
        $(document).ready(function () {
            $('.js-example-basic-single').select2();
        });
        function Check() {
            var isbn = document.getElementById("isbn").value;
            // alert(isbn);
            $.ajax({
                url: "${ctx}/processing/bibliotheca/checkIsbn?isbn=" + isbn,
                type: "GET",
                contentType: false,
                async: false,
                success: function (data) {
                    if (data.status == 200) {
                        tipDialog(data.msg, 3, 1);
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
        function time(now1){
            var now=new Date(now1);
            //格式化日，如果小于9，前面补0
            var day = ("0" + now.getDate()).slice(-2);
            //格式化月，如果小于9，前面补0
            var month = ("0" + (now.getMonth() + 1)).slice(-2);
            //拼装完整日期格式
            var today = now.getFullYear()+"-"+(month)+"-"+(day) ;
            return today;
        }
        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        }
        //保存事件
        function AcceptClick() {
                $("#loading").show();
                Loading(true, "正在提交数据...");
            if (isNull($('#metaId').val())) {
                tipDialog("唯一标识（Metaid） 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#title').val())) {
                tipDialog("标题 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#creator').val())) {
                tipDialog("作者 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#publisher').val())) {
                tipDialog("出版社 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#issuedDate').val())) {
                tipDialog("出版时间 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#isbn').val())) {
                tipDialog("Isbn 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#paperPrice').val())) {
                tipDialog("paperPrice 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#ePrice').val())) {
                tipDialog("ePrice 不能为空！", 3, -1);
                return;
            }
            if (isNull($('#status').val())) {
                tipDialog("版权授权状态 不能为空！", 3, -1);
                return;
            }
                window.setTimeout(function () {
                    var formObj = $("#form1");
                    var postData = JSON.stringify(formObj.serializeJson());
                    $.ajax({
                        url: "${ctx}/resource/update",
                        type: "POST",
                        data: postData,
                        contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                        async: false,
                        success: function (data) {
                            tipDialog("修改授权资源成功！", 3, 1);
                            top.frames[tabiframeId()].location.reload();
                            closeDialog();
                        },
                        error: function (data) {
                            Loading(false);
                            alertDialog("输入信息有误，请核实后提交", -1);
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
        <div>修改协议资源信息<span id="CenterTitle"></span></div>
           </div>
        <form id="form1">
        <#--<form id="form1" enctype="multipart/form-data" action="${ctx}/publisher/doedit" method="post" >-->

            <table border="0" class="form-find" style="height: 45px;">


                <tr>
                    <td>唯一标识(MetaId):</td>
                    <td>
                        <input id="resrId" name="resrId" value="${resource.resrId!''}" type="hidden" class="txt" style="width: 300px"/>
                        <input id="metaId" name="metaId" value="${resource.metaId!''}" type="text" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span
                                style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>标题:</td>
                    <td>
                        <input id="title" name="title" type="text" class="txt" value="${resource.title!''}"
                               style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>作者:</td>
                    <td>
                        <input id="creator" name="creator" type="text" value="${resource.creator!''}" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span
                                style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>出版社:</td>
                    <td>
                        <select id="publisher" name="publisher" underline="true" class="js-example-basic-single" style="width: 307px; height: 24px;">
                            <option value="">--请选择出版社--</option>
                            <#if publishers??>
                                <#list publishers as list>
                                    <option value="${(list.id)!''}">${(list.title)!''}</option>
                                </#list>
                            </#if>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>出版时间:</td>
                    <td>
                        <input id="issuedDate" name="issuedDate" type="date" class="txt" style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span
                                style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>Isbn:</td>
                    <td>
                        <input id="isbn" name="isbn" value="${resource.isbn!''}"  type="text" class="txt" onblur="Check()"
                               style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>纸书价格（元）:</td>
                    <td>
                        <input id="paperPrice" name="paperPrice" value="${resource.paperPrice!''}"  type="text" class="txt"
                               style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>电子书价格（元）:</td>
                    <td>
                        <input id="ePrice" name="ePrice" value="${resource.ePrice!''}"  type="text" class="txt"
                               style="width: 300px"/>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
                <tr>
                    <td>版权授权状态:</td>
                    <td>
                        <select id="status" name="status" underline="true" style="width: 150px">
                            <option value="0">未授权</option>
                            <option value="1">已授权</option>
                            <option value="2">特殊下架</option>
                            <option value="3">特殊上架</option>
                        </select>&nbsp;&nbsp;&nbsp;<span style="color:red">*</span>
                    </td>
                </tr>
             </table>
        </form>
    </div>
</div>
</body>
</html>