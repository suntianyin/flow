<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
<#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <meta charset="UTF-8">
    <title>添加作者信息</title>
    <script type="text/javascript">

        //保存事件
        function AcceptClick() {
            $("#loading").show();
            Loading(true, "正在提交数据...");
            window.setTimeout(function () {
                var formObj = $("#form1");
                var postData = JSON.stringify(formObj.serializeJson());
                $.ajax({
                    url: "${ctx}/author/add",
                    type: "POST",
                    contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                    data: postData,
                    async: false,
                    success: function (data) {
                        tipDialog("添加作者成功！", 3, 1);
                        top.frames[tabiframeId()].location.reload();
                        closeDialog();
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
            <div>添加作者信息<span id="CenterTitle"></span></div>
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
                    <td>作者姓名:</td>
                    <td>
                        <input id="title" name="title" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>姓名类型:</td>
                    <td>
                        <select id="titleType" name="titleType" underline="true">
                            <option value="0">姓名</option>
                            <option value="1">字</option>
                            <option value="2">号</option>
                            <option value="3">别名</option>
                            <option value="4">笔名</option>
                            <option value="5">艺名</option>
                            <option value="6">网名</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>开始使用时间:</td>
                    <td>
                        <input id="startDate" name="startDate" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>结束使用时间:</td>
                    <td>
                        <input id="endDate" name="endDate" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>国籍：</td>
                    <td>
                        <input id="nationalityCode" name="nationalityCode" type="text" class="txt"
                               style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>身份证号：</td>
                    <td>
                        <input id="personId" name="personId" type="text" class="txt"
                               style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>出生日期:</td>
                    <td>
                        <input id="birthday" name="birthday" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>死亡日期:</td>
                    <td>
                        <input id="deathDay" name="deathDay" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>性别:</td>
                    <td>
                        <select id="sexCode" name="sexCode" underline="true">
                            <option value="0">男</option>
                            <option value="1">女</option>
                            <option value="2">未知</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>作者类型:</td>
                    <td>
                        <select id="type" name="type" underline="true">
                            <option value="0">个人</option>
                            <option value="1">团体</option>
                            <option value="2">会议</option>
                            <option value="3">佛道人物</option>
                            <option value="4">古代帝王</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>民族:</td>
                    <td>
                        <input id="nationalCode" name="nationalCode" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>学历:</td>
                    <td>
                        <input id="qualificationCode" name="qualificationCode" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>所在朝代名称:</td>
                    <td>
                        <input id="dynastyName" name="dynastyName" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>籍贯:</td>
                    <td>
                        <input id="originCode" name="originCode" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>职业分类名称:</td>
                    <td>
                        <input id="careerClassCode" name="careerClassCode" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>任职机构名称:</td>
                    <td>
                        <input id="serviceAgency" name="serviceAgency" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>头像:</td>
                    <td>
                        <input id="headPortraitPath" name="headPortraitPath" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <tr>
                    <td>简介:</td>
                    <td>
                        <textarea id="summary" name="summary" class="txt" style="width: 300px; height: 60px;"></textarea>
                    </td>
                </tr>
                <tr>
                    <td>是否卒于50年前:</td>
                    <td>
                        <select id="dieOver50" name="dieOver50" underline="true">
                            <option value="0">否</option>
                            <option value="1">是</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>国图作者id:</td>
                    <td>
                        <input id="nlcAuthorId" name="nlcAuthorId" type="text" class="txt" style="width: 300px"/>
                    </td>
                </tr>
                <#--<tr>
                    <td>操作人:</td>
                    <td>
                        <input id="operator" name="operator" type="text" class="txt" style="width: 200px"/>
                    </td>
                </tr>-->
                <#--<tr>
                    <td>创建时间：</td>
                    <td>
                        <input id="createTime" name="createTime" type="date" class="txt" style="width: 200px"/>
                    </td>
                </tr>
                <tr>
                    <td>最后更新时间：</td>
                    <td>
                        <input id="updateTime" name="updateTime" type="date" pattern="yyyy-MM-dd HH:mm:ss" class="txt"
                               style="width: 200px"/>
                    </td>
                </tr>-->
            </table>
        </form>
    </div>
</div>
</body>
</html>