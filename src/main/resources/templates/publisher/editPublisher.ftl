<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>修改出版社数据</title>
    <script type="text/javascript">
        $(function () {
            $("#codeid").val("${publisher.classCode!'' }");
            $("#titleid").val("${publisher.titleType!'' }");
            $("#resourceid").val("${publisher.resourceType!'' }")
        });

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
                        async: false,
                        contentType: "application/json;charset=UTF-8",
                        success: function (data) {
                            tipDialog("修改机构成功！", 3, 1);
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
        <div>修改出版社数据<span id="CenterTitle"></span></div>
           </div>
        <form id="form1">
        <#--<form id="form1" enctype="multipart/form-data" action="${ctx}/publisher/doedit" method="post" >-->

            <table border="0" class="form-find" style="height: 45px;">

            <tr>
                <td>关联出版社:</td>
                <input id="id" name="id" type="hidden" value="${publisher.id}" class="txt" style="width: 200px"/>
                <td>
                    <input id="relatePublisherID" name="relatePublisherID" type="text" value="${publisher.relatePublisherID!'' }" class="txt" style="width: 200px"/>
                </td>
            </tr>
            <tr>
                <td>isbn:</td>
                <td>
                    <input id="isbn" name="isbn" type="text" value="${publisher.isbn!''  }" class="txt" style="width: 200px"/>
                </td>
            </tr>
            <tr>
                <td>出版社名称:</td>
                <td>
                    <input id="title" name="title" type="text" value="${publisher.title!''  }" class="txt" style="width: 200px"/>
                </td>
            </tr>
            <tr>
                <td>名称类型:</td>
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
                    <#--<input id="titleType" name="titleType" type="text" value="${publisher.titleType!''  }" class="txt" style="width: 200px"/>-->
                </td>
            </tr>
            <tr>
                <td>开始使用时间:</td>
                <td>
                    <input id="startDate" name="startDate" type="text" class="txt" style="width: 200px" value="${publisher.startDate! ''}" />
                </td>
            </tr>
            <tr>
                <td>结束使用时间:</td>
                <td>
                    <input id="endDate" name="endDate" type="text" class="txt" style="width: 200px" value="${(publisher.endDate?string("yyyy-MM-dd"))! ''}" />

                </td>
            </tr>
            <tr>
                <td>注册国国籍：</td>
                <td>
                    <input id="nationalityCode" name="nationalityCode" type="text" value="${publisher.nationalityCode!'' }" class="txt" style="width: 200px"/>
                </td>
            </tr>
            <tr>
                <td>创办时间：</td>
                <td>
                    <input id="founderDate" name="founderDate" type="text" class="txt" style="width: 200px" value="${(publisher.founderDate?string("yyyy-MM-dd"))!'' }" />
                </td>
            </tr>
            <tr>
                <td>出版社分类：</td>
                <td>
                    <select id="codeid" name="classCode" underline="true">
                        <option value="a">综合</option>
                        <option value="b">少儿</option>
                        <option value="c">青年</option>
                        <option value="c">青年</option>
                        <option value="d">大学</option>
                        <option value="e">人名</option>
                        <option value="z">其他</option>
                    </select>
                    <#--<input id="classCode" type="text" value="${publisher.classCode!'' }" class="txt" style="width: 200px"/>-->

                </td>
            </tr>
            <tr>
                <td>出版文献资源类型：</td>
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
                    <#--<input id="resourceType" name="resourceType" type="text" value="${publisher.resourceType!'' }" class="txt" style="width: 200px"/>-->
                </td>
            </tr>
            <tr>
                <td>所属出版集团名称：</td>
                <td>
                    <input id="publishingGroup" name="publishingGroup" type="text" value="${publisher.publishingGroup!'' }" class="txt" style="width: 200px"/>
                </td>
            </tr>
            <#--<tr>-->
                <#--<td>唯一标识：</td>-->
                <#--<td>-->
                    <#--<input id="publishingGroupID" name="publishingGroupID" type="text" value="${publisher.publishingGroupID!'' }" class="txt" style="width: 200px"/>-->
                <#--</td>-->
            <#--</tr>-->
            <tr>
                <td>社长姓名：</td>
                <td>
                    <input id="president" name="president" type="text" value="${publisher.president!'' }" class="txt" style="width: 200px"/>
                </td>
            </tr>
            <tr>
                <td>副社长姓名：</td>
                <td>
                    <input id="vicePresident" name="vicePresident" type="text" value="${publisher.vicePresident!'' }" class="txt" style="width: 200px"/>
                </td>
            </tr>
            <tr>
                <td>图书质量等级：</td>
                <td>
                    <input id="qualityLevel" name="qualityLevel" type="text" value="${publisher.qualityLevel!'' }" class="txt" style="width: 200px"/>
                </td>
            </tr>
            <tr>
                <td>简介：</td>
                <td>
                    <input id="summary" name="summary" type="text" value="${publisher.summary!'' }" class="txt" style="width: 200px"/>
                </td>
            </tr>
            <tr>
                <td>出版地：</td>
                <td>
                    <input id="place" name="place" type="text" value="${publisher.place!'' }" class="txt" style="width: 200px"/>
                </td>
            </tr>
            <#--<tr>-->
                <#--<td>操作人：</td>-->
                <#--<td>-->
                    <#--<input id="operator" name="operator" type="text" value="${publisher.operator!'' }" class="txt" style="width: 200px"/>-->
                <#--</td>-->
            <#--</tr>-->
            <#--<tr>-->
                <#--<td>创建时间：</td>-->
                <#--<td>-->
                    <#--<input id="createTime" name="createTime" type="text" class="txt Wdate" value="${(publisher.createTime?string("yyyy-MM-dd"))!'' }" onfocus="WdatePicker({maxDate:'%y-%M-%d'})"/>-->
                <#--</td>-->
            <#--</tr>-->
            <#--<tr>-->
                <#--<td>最后更新时间：</td>-->
                <#--<td>-->
                    <#--<input id="updateTime" name="updateTime" type="text" class="txt Wdate" value="${(publisher.updateTime?string("yyyy-MM-dd"))!'' }" onfocus="WdatePicker({maxDate:'%y-%M-%d'})"/>-->
                <#--</td>-->
            <#--</tr>-->
            <#--<tr>-->
                <#--<td>-->
                    <#--<input id="btnSubmit" type="submit" class="btnSubmit" value="提 交" />-->
                <#--</td>-->
            <#--</tr>-->
            </from>
        </table>
    </div>
    <#--<div id="loading"><img src="${ctx}/resources/static/images/ui-anim_basic_16x16.gif" alt="" /> <span style="font-weight: bolder;">请稍候...</span></div>-->
</div>
</body>
</html>