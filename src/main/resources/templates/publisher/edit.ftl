<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>

    <title>图书元数据</title>
    <script type="text/javascript">
        //保存事件
        function AcceptClick() {
            $("#loading").show();
            Loading(true, "正在提交数据...");
            window.setTimeout(function () {
                var formObj = $("#form1");
                var postData = JSON.stringify(formObj.serializeJson());
                $.ajax({
                    url: "${ctx}/publisher/doedit",
                    type: "POST",
                    data: postData,
                    contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                    async: false,
                    success: function (data) {
                        tipDialog("修改出版社信息成功！", 3, 1);
                        // top.frames[tabiframeId()].location.reload();
                        // closeDialog();
                        $(".load-circle").hide();
                        window.location.href = "index?id=" + '' + "&title=" + '' + "&relatePublisherID=" + '';
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
<div class='load-circle' style="display: none;"></div>
<form id="form1">
    <table id="table-list"
           class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
        <thead>
        <tr class="gradeA odd" role="row">
            <td>
                <input type="button" onclick="window.history.back();" value="返回">
            </td>
            <td>
                <input type="button" onclick="AcceptClick()" value="保存"/>
            </td>
        </tr>
        <tr role="row">
            <th>字段名</th>
            <th>字段值</th>
        </tr>
        </thead>
        <tbody>
        <#list entityInfoList as entityInfo>
        <tr class="gradeA odd" role="row">
            <#if entityInfo.info == '名称类型'>
                <td>${entityInfo.info! ''}</td>
                <td>
                    <select id="titleid" name="titleType" underline="true">
                        <option value="0">现名</option>
                        <option value="1">原名</option>
                        <option value="2">全资子公司</option>
                        <option value="3">副牌</option>
                        <option value="4">正牌</option>
                        <option value="5">合并</option>
                        <option value="6">上级机构</option>
                        <option value="7">又称</option>
                        <option value="8">同一机构</option>
                        <option value="9">简称</option>
                        <option value="10">全称</option>
                        <option value="11">包含</option>
                        <option value="12">同一出版社另一牌名</option>
                    </select>
                </td>
            <#elseif entityInfo.info == '出版社分类'>
            <td>${entityInfo.info! ''}</td>
            <td>
                <select id="codeid" name="classCode" underline="true">
                    <option value="a">综合</option>
                    <option value="b">少儿</option>
                    <option value="c">青年</option>
                    <option value="d">大学</option>
                    <option value="e">人名</option>
                    <option value="z">其他</option>
                </select>
            </td>
            <#elseif entityInfo.info == '出版文献资源类型'>
            <td>${entityInfo.info! ''}</td>
            <td>
                <select id="resourceid" name="resourceType" underline="true">
                    <option value="Ebook">图书</option>
                    <option value="RefBook">工具书</option>
                    <option value="YearBook">年鉴</option>
                    <option value="PicLib">图片库</option>
                    <option value="Newspaper">报纸</option>
                    <option value="video ">视频</option>
                    <option value="audio">音频</option>
                </select>
            </td>
            <#elseif entityInfo.info == '最后更新时间'||entityInfo.info == '创建时间'>
            <input type="date" name="${entityInfo.fieldName! ''}" value="${entityInfo.metaValue! ''}" readonly="readonly" hidden="hidden">
            <#elseif entityInfo.info == '操作人'||entityInfo.info == '出版社id'>
            <td>
                <input type="text" name="${entityInfo.fieldName! ''}" value="${entityInfo.metaValue! ''}" readonly="readonly" hidden="hidden">
            </td>
            <#elseif entityInfo.info == '简介'>
            <td>${entityInfo.info! ''}</td>
                <td><textarea name="${entityInfo.fieldName! ''}" cols="100"
                              rows="10">${entityInfo.metaValue! ''}</textarea></td>
            <#else >
                                    <td>${entityInfo.info! ''}</td>
            <td><input type="text" name="${entityInfo.fieldName! ''}" value="${entityInfo.metaValue! ''}"></td>
            </#if>
        </tr>
        </#list>
        <tr class="gradeA odd" role="row">
            <td>
                <input type="button" onclick="window.history.back();" value="返回">
            </td>
            <td>
                <input type="button" onclick="AcceptClick()" value="保存"/>
            </td>
        </tr>
        </tbody>
    </table>
</form>
</body>
</html>
