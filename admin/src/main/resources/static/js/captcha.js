function countDown(initVal) {
    var span = $('#countdown-btn span');
    span.html(initVal);
    var interval = window.setInterval(function () {
        if (--initVal === 0) {
            doActive('#resend-btn');
            // $('#countdown-btn').hide();
            // $('#resend-btn').css("display", "block");
            clearInterval(interval);
        }
        span.html(initVal);
    },1000)
}
function doActive(obj) {
    $('.captcha-btn-box a').removeClass("active");
    $(obj).addClass("active");
}