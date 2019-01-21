<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <script src="${ctx}/js/jsPage.js"></script>
    <#include "../common/meta.ftl">
    <link href="${ctx}/css/org.css" rel="stylesheet"/>
    <title>编辑图书元数据</title>
</head>
<body>
<form id="form1" method="post">
    <div class="ScrollBar" style="margin: 1px; overflow: hidden;">
        <div id="basic" class="tabPanel">
            <table class="form">
                <tr>
                    <th class="formTitle">metaId：</th>
                    <td class="formValue">
                        <input id="metaId" name="metaId" value="${bookMeta.metaId!''}" type="text" class="txt required"
                               readonly datacol="yes" checkexpession="NotNull"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">语种：</th>
                    <td class="formValue">
                        <input id="language" name="language" value="${bookMeta.language!''}" type="text" class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">书名：</th>
                    <td class="formValue">
                        <input id="title" name="title" value="${bookMeta.title!''}" type="text" class="txt required"
                               datacol="yes" checkexpession="NotNull"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">副标题：</th>
                    <td class="formValue">
                        <input id="subTitle" name="subTitle" value="${bookMeta.subTitle!''}" type="text" class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">主要责任者：</th>
                    <td class="formValue">
                        <input id="creator" name="creator" value="${bookMeta.creator!''}" type="text" class="txt required"
                               datacol="yes" checkexpession="NotNull"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">作者简介：</th>
                    <td class="formValue">
                        <textarea id="authorIntro" name="authorIntro"
                                  maxlength="200" class="txtArea" rows="5">${bookMeta.authorIntro!'' }</textarea>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">责任关系词：</th>
                    <td class="formValue">
                        <input id="creatorWord" name="creatorWord" value="${bookMeta.creatorWord!''}" type="text"
                               class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">次要责任者：</th>
                    <td class="formValue">
                        <input id="contributor" name="contributor" value="${bookMeta.contributor!''}" type="text"
                               class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">次要责任者关系词：</th>
                    <td class="formValue">
                        <input id="contributorWord" name="contributorWord" value="${bookMeta.contributorWord!''}"
                               type="text" class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">翻译：</th>
                    <td class="formValue">
                        <input id="translator" name="translator" value="${bookMeta.translator!''}" type="text"
                               class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">原书名：</th>
                    <td class="formValue">
                        <input id="originTitle" name="originTitle" value="${bookMeta.originTitle!''}" type="text"
                               class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">其它题名：</th>
                    <td class="formValue">
                        <input id="alternativeTitle" name="alternativeTitle" value="${bookMeta.alternativeTitle!''}"
                               type="text" class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">版次：</th>
                    <td class="formValue">
                        <input id="editionOrder" name="editionOrder" value="${bookMeta.editionOrder!''}" type="text"
                               class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">版次说明：</th>
                    <td class="formValue">
                        <input id="editionNote" name="editionNote" value="${bookMeta.editionNote!''}" type="text"
                               class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">出版地：</th>
                    <td class="formValue">
                        <input id="place" name="place" value="${bookMeta.place!''}" type="text" class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">出版社：</th>
                    <td class="formValue">
                        <input id="publisher" name="publisher" value="${bookMeta.publisher!''}" type="text" class="txt required"
                               datacol="yes" checkexpession="NotNull"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">出版日期：</th>
                    <td class="formValue">
                        <input id="issuedDate" name="issuedDate" value="${bookMeta.issuedDate!''}" type="date"
                               class="txt required"
                               datacol="yes" checkexpession="NotNull"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">结束出版日期：</th>
                    <td class="formValue">
                        <input id="endIssuedDate" name="endIssuedDate" value="${bookMeta.endIssuedDate!''}" type="text"
                               class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">出版日期说明：</th>
                    <td class="formValue">
                        <input id="issuedDateDesc" name="issuedDateDesc" value="${bookMeta.issuedDateDesc!''}"
                               type="text" class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">字数：</th>
                    <td class="formValue">
                        <input id="contentNum" name="contentNum" value="${bookMeta.contentNum!0}" type="text"
                               class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">内容提要：</th>
                    <td class="formValue">
                        <textarea id="abstract_" name="abstract_" maxlength="200"
                                  class="txtArea" rows="5">${bookMeta.abstract_!'' }</textarea>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">主题/关键词：</th>
                    <td class="formValue">
                        <input id="subject" name="subject" value="${bookMeta.subject!''}" type="text" class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">序言：</th>
                    <td class="formValue">
                        <textarea id="preface" name="preface" maxlength="200"
                                  class="txtArea" rows="5">${bookMeta.preface!'' }</textarea>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">ISBN：</th>
                    <td class="formValue">
                        <input id="isbn" name="isbn" value="${bookMeta.isbn!''}" type="text" class="txt required"
                               datacol="yes" checkexpession="NotNull"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">纸书价格：</th>
                    <td class="formValue">
                        <input id="paperPrice" name="paperPrice" value="${bookMeta.paperPrice!''}" type="text"
                               class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">电子书价格：</th>
                    <td class="formValue">
                        <input id="ebookPrice" name="ebookPrice" value="${bookMeta.ebookPrice!''}" type="text"
                               class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">外汇价格：</th>
                    <td class="formValue">
                        <input id="foreignPrice" name="foreignPrice" value="${bookMeta.foreignPrice!''}" type="text"
                               class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">外汇价格类型：</th>
                    <td class="formValue">
                        <input id="foreignPriceType" name="foreignPriceType" value="${bookMeta.foreignPriceType!''}"
                               type="text" class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">价格说明：</th>
                    <td class="formValue">
                        <input id="paperPriceDesc" name="paperPriceDesc" value="${bookMeta.paperPriceDesc!''}"
                               type="text" class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">装帧：</th>
                    <td class="formValue">
                        <input id="binding" name="binding" value="${bookMeta.binding!''}" type="text" class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">印次：</th>
                    <td class="formValue">
                        <input id="pressOrder" name="pressOrder" value="${bookMeta.pressOrder!''}" type="text"
                               class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">责任编辑：</th>
                    <td class="formValue">
                        <input id="editor" name="editor" value="${bookMeta.editor!''}" type="text" class="txt"
                               datacol="yes"/>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</form>
<div id="loading"><img src="${ctx}/images/ui-anim_basic_16x16.gif"/>
    <span style="font-weight: bolder;">请稍候...</span>
</div>
</body>
<script>
    //保存事件
    function AcceptClick() {
        if (!CheckDataValid('#form1')) {
            return false;
        }
        if (false) {
            tipCss($(this), Validatemsg);
            return false;
        }
        Loading(true, "正在提交数据...");
        $("#loading").show();
        window.setTimeout(function () {
            var postData = JSON.stringify($('#form1').serializeJson());
            $.ajax({
                url: RootPath() + "/processing/bibliotheca/updateBookMeta",
                type: "POST",
                data: postData,
                contentType: "application/json;charset=utf-8",
                success: function (data) {
                    $("#loading").hide();
                    if (data == "success") {
                        top.frames[tabiframeId()].location.reload();
                        tipDialog("保存成功！", 3, 1);
                        closeDialog();
                    } else {
                        tipCss('图书元数据', '保存失败!联系管理员');
                    }
                },
                error: function (data) {
                    $("#loading").hide();
                    Loading(false);
                    alertDialog(data.responseText, -1);
                }
            });
        }, 200);
    }
</script>
</html>
