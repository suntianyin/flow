<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE9"/>
    <#include "../common/metabootstraps.ftl">
    <link href="${ctx}/css/login.css" rel="stylesheet"/>
    <link href="${ctx}/css/captcha.css" rel="stylesheet"/>
    <script src="${ctx}/js/captcha.js" type="text/javascript"></script>
    <title>登录页面</title>
    <script type="text/javascript">
        //自适应屏幕分辨率
        function reFontSize() {
            $("html").css("font-size", ($(window).width() * 100 / 1920) + "px");
        }

        $(function () {
            reFontSize();
            $(window).resize(function () {
                reFontSize();
            });
        });


        function resetPassword() {
            $(".form-account input").val("");
            $(".form-password input").val("");
        }

        function setErrMsg(msg, delay) {
            delay = (delay || 3) * 1000;
            var errDiv = $('.errormsg');
            errDiv.html(msg);
            setTimeout(function () {
                errDiv.html('');
            }, delay)
        }
    </script>
</head>
<body class="loginbody">
<div class="center">
    <div class="logo">
        <img src="${ctx}/images/login.png"/>
    </div>
    <div class="content">
        <div class="loginname">用户登录</div>
        <div class="loginform">
            <form action="${ctx}/login" method="post">
                <#--<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>-->
                <div id="isNull" style="color: red; display: none">
                    用户名不能为空！
                </div>
                <#--<div class="errormsg" style="color: red">-->
                    <#--<c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION }">-->
                    <#--${SPRING_SECURITY_LAST_EXCEPTION.message}-->
                    <#--</c:if>-->
                <#--</div>-->
                <div class="form-account">
                    <input type="text" class="form-control" name="username" placeholder="账户"/>
                </div>
                <div class="form-password">
                    <input type="password" class="form-control" name="password" placeholder="请输入您的密码"/>
                    <div class="reset-password" onclick="resetPassword()">清空用户名和密码</div>
                </div>
                <#--<div class="form-captcha">-->
                    <#--<input type="text" class="form-control" name="captcha" placeholder="如绑定邮箱，请进行邮箱验证"/>-->
                    <#--<div class="captcha-btn-box" style="line-height: 0.36rem; border:0">-->
                        <#--<a class=" active" id="send-btn">免费获取验证码</a>-->
                        <#--<a id="sending-btn">正在发送……</a>-->
                        <#--<a id="countdown-btn" disabled><span></span>秒后重新发送</a>-->
                        <#--<a id="resend-btn">重新发送</a>-->
                    <#--</div>-->
                <#--</div>-->
                <div class="form-bottom">
                    <input class="button" type="submit" value=" 登录 "/>
                </div>
            </form>
        </div>
        <#--<%--<div class="errormsg" style="color: red">-->
        <#--&lt;%&ndash;security 需要做国际化，这里没有做&ndash;%&gt;-->
        <#--<c:if test="${SPRING_SECURITY_LAST_EXCEPTION.message == 'Bad credentials'}">-->
            <#--密码有误！-->
        <#--</c:if>-->
        <#--<c:if test="${SPRING_SECURITY_LAST_EXCEPTION.message != 'Bad credentials'}">-->
        <#--${SPRING_SECURITY_LAST_EXCEPTION.message}-->
        <#--</c:if>-->
    <#--</div>--%>-->
        <!-- <div class="ceshi">测试账户：admin&nbsp;&nbsp;密码：admin </div> -->
    </div>
</div>


<div class="copyright">
    <div class="quote">智慧&nbsp;&nbsp;&nbsp;开放&nbsp;&nbsp;&nbsp;共享</div>
    <div class="comp">北京方正阿帕比技术有限公司</div>
</div>

</body>
</html>

