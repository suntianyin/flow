<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>修改音频数据</title>
    <script type="text/javascript">
        $(function () {
            $("#codeid").val("${audioMeta.classCode!'' }");
            $("#titleid").val("${audioMeta.titleType!'' }");
            $("#resourceid").val("${audioMeta.resourceType!'' }")
        });

        //保存事件
        function AcceptClick() {
            $("#loading").show();
            Loading(true, "正在提交数据...");
            window.setTimeout(function () {
                var formObj = $("#form1");
                var postData = JSON.stringify(formObj.serializeJson());
                $.ajax({
                    url: "${ctx}/audioMeta/doedit",
                    type: "POST",
                    data: postData,
                    async: false,
                    contentType: "application/json;charset=UTF-8",
                    success: function (data) {
                        tipDialog("修改音频成功！", 3, 1);
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
            <div>修改音频数据<span id="CenterTitle"></span></div>
        </div>
        <form id="form1">
        <#--<form id="form1" enctype="multipart/form-data" action="${ctx}/publisher/doedit" method="post" >-->

            <table border="0" class="form-find" style="height: 45px;">

                <tr>
                    <td>中文名称:</td>
                    <input id="id" name="id" type="hidden" value="${audioMeta.id}" class="txt" style="width: 200px"/>
                    <td>
                        <input id="chTitle" name="chTitle" type="text" value="${audioMeta.chTitle!'' }" class="txt"
                               style="width: 200px"/>
                    </td>
                </tr>
                <tr>
                    <td>英文名称:</td>
                    <td>
                        <input id="enTitle" name="enTitle" type="text" value="${audioMeta.enTitle!''  }" class="txt"
                               style="width: 200px"/>
                    </td>
                </tr>
                <tr>
                    <td>所属系列:</td>
                    <td>
                        <input id="series" name="series" type="text" value="${audioMeta.series!''  }" class="txt"
                               style="width: 200px"/>
                    </td>
                </tr>
                <tr>
                    <td>关键词:</td>
                    <td>
                    <#--<select id="titleid" name="titleType" underline="true">-->
                    <#--<option value="0">现名</option>-->
                    <#--<option value="1">原名</option>-->
                    <#--<option value="2">全资子公司</option>-->
                    <#--<option value="3">副牌</option>-->
                    <#--<option value="4">正牌</option>-->
                    <#--<option value="5">合并</option>-->
                    <#--<option value="6">上级机构</option>-->
                    <#--<option value="7">又称</option>-->
                    <#--<option value="8">同一机构</option>-->
                    <#--<option value="9">简称</option>-->
                    <#--<option value="10">全称</option>-->
                    <#--<option value="11">包含</option>-->
                    <#--<option value="12">同一出版社另一牌名</option>-->
                    <#--</select>-->
                        <input id="keyWord" name="keyWord" type="text" value="${audioMeta.keyWord!''  }" class="txt"
                               style="width: 200px"/>
                    </td>
                </tr>
                <tr>
                    <td>主要人物:</td>
                    <td>
                        <input id="human" name="human" type="text" class="txt" style="width: 200px"
                               value="${audioMeta.human! ''}"/>
                    </td>
                </tr>
                <tr>
                    <td>内容描述（类似剧情摘要）:</td>
                    <td>
                        <input id="description" name="description" type="text" class="txt" style="width: 200px"
                               value="${audioMeta.description! ''}"/>

                    </td>
                </tr>
                <tr>
                    <td>责任者：</td>
                    <td>
                        <input id="creator" name="creator" type="text" value="${audioMeta.creator!'' }" class="txt"
                               style="width: 200px"/>
                    </td>
                </tr>
                <tr>
                    <td>分类：</td>
                    <td>
                        <input id="subject" name="subject" type="text" class="txt" style="width: 200px"
                               value="${audioMeta.subject!'' }"/>
                    </td>
                </tr>
                <tr>
                    <td>标签：</td>
                    <td>
                    <#--<select id="codeid" name="classCode" underline="true">-->
                    <#--<option value="a">综合</option>-->
                    <#--<option value="b">少儿</option>-->
                    <#--<option value="c">青年</option>-->
                    <#--<option value="c">青年</option>-->
                    <#--<option value="d">大学</option>-->
                    <#--<option value="e">人名</option>-->
                    <#--<option value="z">其他</option>-->
                    <#--</select>-->
                        <input id="label" name="label" type="text" value="${audioMeta.label!'' }" class="txt"
                               style="width: 200px"/>

                    </td>
                </tr>
                <tr>
                    <td>来源：</td>
                    <td>
                    <#--<select id="resourceid" name="resourceType" underline="true">-->
                    <#--<option value="Ebook">图书</option>-->
                    <#--<option value="RefBook">工具书</option>-->
                    <#--<option value="YearBook">年鉴</option>-->
                    <#--<option value="PicLib">图片库</option>-->
                    <#--<option value="Newspaper">报纸</option>-->
                    <#--<option value="videoMeta ">视频</option>-->
                    <#--<option value="audio">音频</option>-->
                    <#--</select>-->
                        <input id="source" name="source" type="text" value="${audioMeta.source!'' }" class="txt"
                               style="width: 200px"/>
                    </td>
                </tr>
                <tr>
                    <td>场景：</td>
                    <td>
                        <input id="scene" name="scene" type="text" value="${audioMeta.scene!'' }" class="txt"
                               style="width: 200px"/>
                    </td>
                </tr>
                <tr>
                    <td>适应年龄段（或人群）：</td>
                    <td>
                        <input id="userAge" name="userAge" type="text" value="${audioMeta.userAge!'' }" class="txt"
                               style="width: 200px"/>
                    </td>
                </tr>
                <tr>
                    <td>方言：</td>
                    <td>
                        <input id="dialect" name="dialect" type="text" value="${audioMeta.dialect!'' }" class="txt"
                               style="width: 200px"/>
                    </td>
                </tr>
                <tr>
                    <td>配音语种：</td>
                    <td>
                    <#--<input id="dialogueLanguage" name="dialogueLanguage" type="text"-->
                    <#--value="${audioMeta.dialogueLanguage!'' }" class="txt" style="width: 200px"/>-->
                        <select id="dialogueLanguage" name="dialogueLanguage" underline="true">
                            <option value="en_US">英文</option>
                            <option value="zh_CN">中文（大陆）</option>
                            <option value="zh_HK">中文（香港）</option>
                            <option value="zh_TW">中文（台湾）</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>编码格式：</td>
                    <td>
                    <#--<input id="codeFormat" name="codeFormat" type="text" value="${audioMeta.codeFormat!'' }"-->
                    <#--class="txt" style="width: 200px"/>-->
                        <select id="codeFormat" name="codeFormat" underline="true">
                            <option value="PCM编码">PCM编码</option>
                            <option value="WAV格式">WAV格式</option>
                            <option value="MP3编码">MP3编码</option>
                            <option value="OGG编码">OGG编码</option>
                            <option value="MPC编码">MPC编码</option>
                            <option value="mp3PRO编码">mp3PRO编码</option>
                            <option value="WMA格式">WMA格式</option>
                            <option value="RA格式">RA格式</option>
                            <option value="APE格式">APE格式</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>版权方：</td>
                    <td>
                        <input id="rightOwner" name="rightOwner" type="text" value="${audioMeta.rightOwner!'' }"
                               class="txt" style="width: 200px"/>
                    </td>
                </tr>
                <tr>
                    <td>授权期限：</td>
                    <td>
                        <input id="authorizationPeriod" name="authorizationPeriod" type="date" class="txt"
                               style="width: 200px"
                               value="${(audioMeta.authorizationPeriod?string("yyyy-MM-dd"))!'' }"
                        />
                    </td>
                </tr>
            <#--<tr>-->
            <#--<td>最后更新时间：</td>-->
            <#--<td>-->
            <#--<input id="updateTime" name="updateTime" type="text" class="txt Wdate" onfocus="WdatePicker({maxDate:'%y-%M-%d'})"/>-->
            <#--</td>-->
            <#--</tr>-->
                <tr>
                    <td>音频格式：</td>
                    <td>
                    <#--<input id="type" name="type" type="text" class="txt " style="width: 200px"-->
                    <#--value="${audioMeta.type!'' }"/>-->
                        <select id="type" name="type" underline="true">
                            <option value="CD">CD</option>
                            <option value="WAVE">WAVE</option>
                            <option value="AIFF">AIFF</option>
                            <option value="MPEG">MPEG</option>
                            <option value="MP3">MP3</option>
                            <option value="MPEG-4">MPEG-4</option>
                            <option value="MIDI">MIDI</option>
                            <option value="WMA">WMA</option>
                            <option value="RealAudio">RealAudio</option>
                            <option value="VQF">VQF</option>
                            <option value="OggVorbis">OggVorbis</option>
                            <option value="AMR">AMR</option>
                            <option value="APE">APE</option>
                            <option value="FLAC">FLAC</option>
                            <option value="AAC">AAC</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>音频时长：(分)</td>
                    <td>
                        <input id="audioTime" name="audioTime" type="text" class="txt " style="width: 200px"
                               value="${audioMeta.audioTime!'' }"
                        />
                    </td>
                </tr>
                <tr>
                    <td>评分：</td>
                    <td>
                        <input id="score" name="score" type="text" class="txt " style="width: 200px"
                               value="${audioMeta.score!'' }"
                        />
                    </td>
                </tr>
                <tr>
                    <td>出版国家：</td>
                    <td>
                        <input id="publicationCountry" name="publicationCountry" type="text" class="txt "
                               style="width: 200px"
                               value="${audioMeta.publicationCountry!'' }"
                        />
                    </td>
                </tr>
                <tr>
                    <td>出版公司：</td>
                    <td>
                        <input id="publicationCompany" name="publicationCompany" type="text" class="txt "
                               style="width: 200px"
                               value="${audioMeta.publicationCompany!'' }"/>
                    </td>
                </tr>
                <tr>
                    <td>出版日期：</td>
                    <td>
                        <input id="publicationYear" name="publicationYear" type="date" class="txt" style="width: 200px"
                               value="${(audioMeta.publicationYear?string("yyyy-MM-dd"))!'' }"
                        />
                    </td>
                </tr>
                <tr>
                    <td>大小：(MB)</td>
                    <td>
                        <input id="audioSize" name="audioSize" type="text" class="txt " style="width: 200px"
                               value="${audioMeta.viedoSize!'' }"/>
                    </td>
                </tr>
                <tr>
                    <td>制作日期：</td>
                    <td>
                        <input id="createTime" name="createTime" type="date" class="txt" style="width: 200px"
                               value="${(audioMeta.createTime?string("yyyy-MM-dd"))!'' }"
                        />
                    </td>
                </tr>
                <tr>
                    <td>存放路径：</td>
                    <td>
                        <input id="savePath" name="savePath" type="text" class="txt " style="width: 200px"
                               value="${audioMeta.savePath!'' }"/>
                    </td>
                </tr>
                <tr>
                    <td>播放路径：</td>
                    <td>
                        <input id="playPath" name="playPath" type="text" class="txt " style="width: 200px"
                               value="${audioMeta.playPath!'' }"/>
                    </td>
                </tr>
                <tr>
                    <td>其他信息：</td>
                    <td>
                        <input id="note" name="note" type="text" class="txt " style="width: 200px"
                               value="${audioMeta.note!'' }"/>
                    </td>
                </tr>
            <#--<tr>-->
            <#--<td>记录创建时间：</td>-->
            <#--<td>-->
            <#--<input id="insertTime" name="insertTime" type="text" class="txt Wdate"-->
            <#--value="${(videoMeta.insertTime?string("yyyy-MM-dd"))!'' }"-->
            <#--onfocus="WdatePicker({maxDate:'%y-%M-%d'})"/>-->
            <#--</td>-->
            <#--</tr>-->
            <#--<tr>-->
            <#--<td>记录创建人：</td>-->
            <#--<td>-->
            <#--<input id="operator" name="operator" type="text" class="txt "-->
            <#--value="${videoMeta.operator!'' }"/>-->
            <#--</td>-->
            <#--</tr>-->
            <#--<tr>-->
            <#--<td>记录修改时间：</td>-->
            <#--<td>-->
            <#--<input id="updateTime" name="updateTime" type="text" class="txt Wdate"-->
            <#--value="${(videoMeta.updateTime?string("yyyy-MM-dd"))!'' }"-->
            <#--onfocus="WdatePicker({maxDate:'%y-%M-%d'})"/>-->
            <#--</td>-->
            <#--</tr>-->
            <#--<tr>-->
            <#--<td>-->
            <#--<input id="btnSubmit" type="submit" class="btnSubmit" value="提 交"/>-->
            <#--</td>-->
            <#--</tr>-->
                </from>
            </table>
    </div>
<#--<div id="loading"><img src="${ctx}/resources/static/images/ui-anim_basic_16x16.gif" alt="" /> <span style="font-weight: bolder;">请稍候...</span></div>-->
</div>
</body>
</html>