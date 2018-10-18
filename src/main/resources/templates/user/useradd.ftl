<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width" />
    <#include "../common/meta.ftl">
    <link href="${ctx}/css/captcha.css" rel="stylesheet" />
    <script src="${ctx}/js/captcha.js"></script>
    <title>添加用户页面</title>

</head>
<body>
<script type="text/javascript">
    //保存事件
    function AcceptClick() {
        if (!CheckDataValid('#form1') ) {
            return false;
        }
        Loading(true, "正在提交数据...");
        window.setTimeout(function () {
            var formObj = $("#form1");
            var postData = formObj.serializeJson();
            //postData["BuildFormJson"] = JSON.stringify(GetWebControls("#CustomAttribute"));
            /* AjaxJson("usersave", postData, function (data) {
                tipDialog(data.Message, 3, data.Code);
                top.frames[tabiframeId()].windowload();
                closeDialog();
            }); */
            $.ajax({
                url: "usersave",
                type: "POST",
                data: postData,
                async: false,
                dataType: "json",

                success: function (data) {
                    var statusCode = data.code;
                    var msg = data.msg;
                    tipDialog(msg, 1, statusCode);
                    if (statusCode === 1) {
                        //Loading(false);
                        top.frames[tabiframeId()].location.reload();
                        closeDialog();
                    }
                },
                error: function (data) {
                    Loading(false);
                    alertDialog(data.responseText, -1);
                }
            });

        }, 200);
    }
    <#--$(function () {-->
        <#--$('#email').on("change", function () {-->
            <#--var captchaTr = $('.captchaTitle,.captchaValue');-->
            <#--if ($(this).val().trim() !== '') {-->
                <#--captchaTr.show();-->
            <#--}else {-->
                <#--captchaTr.hide();-->
            <#--}-->
        <#--});-->
        <#--$('#send-btn,#resend-btn').on("click", function () {-->
            <#--var email = $("#email").val();-->
            <#--if (isNull(email)){-->
                <#--tipDialog("请先输入邮箱！", 1, 0);-->
                <#--return false;-->
            <#--}-->
            <#--if (!isEmail(email)){-->
                <#--tipDialog("邮箱格式错误！", 1, 0);-->
                <#--return false;-->
            <#--}-->
            <#--$.ajax({-->
                <#--url: "${ctx}/admin/auth/authuser/userBindSendEmailCatpcha",-->
                <#--type: "get",-->
                <#--data: {-->
                    <#--email: encodeURI(email)-->
                <#--},-->
                <#--dataType: "json",-->
                <#--success: function (data) {-->
                    <#--var statusCode = data.code;-->
                    <#--var msg = data.msg;-->
                    <#--tipDialog(msg, 2, 1);-->
                    <#--switch (statusCode) {-->
                        <#--case -1 : {-->
                            <#--countDown(data.data);-->
                            <#--break;-->
                        <#--}-->
                        <#--case 0 : {-->
                            <#--countDown(5);-->
                            <#--break;-->
                        <#--}-->
                        <#--case 1 : {-->
                            <#--countDown(60);-->
                            <#--break;-->
                        <#--}-->
                    <#--}-->
                <#--},-->
                <#--error: function () {-->
                    <#--Loading(false);-->
                    <#--alertDialog("发送失败", -1);-->
                    <#--countDown(5);-->
                <#--},-->
                <#--complete: function () {-->
                    <#--doActive('#countdown-btn')-->
                <#--}-->
            <#--});-->
            <#--doActive('#sending-btn')-->
            <#--return false;-->
        <#--});-->
    <#--});-->

    // function checkCaptcha() {
    //     var email = $('#email').val();
    //     var code = $('#captcha').val();
    //     if (isNull(email)) return true;
    //     if (isNull(code)) {
    //         tipCss($('#captcha'), '请输入验证码！');
    //         return false;
    //     }
    //     return true;
    // }
</script>
<form id="form1">
    <div id="message" style="display: none; padding: 1px; padding-bottom: 0px;"></div>
    <div class="bd" style="border-bottom: none; margin: 1px;">
        <div class="tipstitle_title settingtable bold bd todayInfoPanelTab rightPanelTitle_normal">
            <div class="tab_list_top" style="position: absolute">
                <div id="Tabbasic" class="tab_list bd actived">基本信息</div>
            </div>
        </div>
    </div>
    <div class="ScrollBar" style="margin: 1px; overflow: hidden;">
        <!--基本信息-->
        <div id="basic" class="tabPanel">
            <table class="form">
                <tr>
                    <th class="formTitle">用户名：</th>
                    <td class="formValue">
                        <input id="userName" name="userName" onblur="FieldExist(this.id,'Base_User','UserName','用户名')" type="text" class="txt required" datacol="yes" err="用户名" checkexpession="NotNull" />
                    </td>
                    <th class="formTitle">真实姓名：</th>
                    <td class="formValue">
                        <input id="realName" name="realName" type="text" class="txt" />
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">性别：</th>
                    <td class="formValue">
                        <select id="gender" name="gender" class="txtselect">
                            <option value="1">男</option>
                            <option value="2">女</option>
                        </select>
                    </td>
                    <th class="formTitle">机构名称：</th>
                    <td class="formValue">
                        <select id="orgId" name="orgId" class="txtselect">
                        <#list list as list>
                            <option value="${list.id?c}">${list.orgName !''}</option>
                        </#list>
                        </select>
                        <#--<input id="orgId" name="orgId"type="text" class="txt" datacol="yes" err="机构"  />-->
                    </td>
                </tr>
                <tr>
                    <th class="formTitle">密码：</th>
                    <td class="formValue">
                        <input id="password" name="password" type="password" class="txt required" datacol="yes" err="密码" checkexpession="NotNull"/>
                    </td>
                    <th class="formTitle">出生日期：</th>
                    <td class="formValue">
                        <input id="birthdate" name="birthdate" type="text" class="txt Wdate" onfocus="WdatePicker({maxDate:'%y-%M-%d'})" />
                    </td>
                </tr>

                <tr>
                    <th class="formTitle">电话：</th>
                    <td class="formValue">
                        <input id="telephone" name="telephone"type="text" class="txt" datacol="yes" err="手机" checkexpession="MobileOrNull" />
                    </td>

                    <th class="formTitle">状态：</th>
                    <td class="formValue">
                        <select id="enabled" name="enabled" class="txtselect">
                            <option value="0">不可用</option>
                            <option value="1" selected="selected">可用</option>
                        </select>
                    </td>
                </tr>

                <tr>
                    <th class="formTitle">E-mail：</th>
                    <td class="formValue">
                        <input id="email" name="email" type="text" class="txt" datacol="yes" err="E-mail" checkexpession="EmailOrNull"/>
                        <span></span>
                    </td>

                    <#--<th class="formTitle captchaTitle">验证码：</th>-->
                    <#--<td class="formValue captchaValue">-->
                        <#--<input id="captcha" name="captcha" type="text" class="txt"/>-->
                        <#--<div class="captcha-btn-box">-->
                            <#--<a class="active" id="send-btn">免费获取验证码</a>-->
                            <#--<a id="sending-btn">正在发送……</a>-->
                            <#--<a id="countdown-btn" disabled><span></span>秒后重新发送</a>-->
                            <#--<a id="resend-btn">重新发送</a>-->
                        <#--</div>-->
                    <#--</td>-->
                </tr>

            </table>
        </div>
    </div>
</form>
</div>
</body>
</html>
