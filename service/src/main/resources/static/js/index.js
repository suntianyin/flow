$(function () {
    $(window).load(function () {
        window.setTimeout(function () {
            $('#ajax-loader').fadeOut();
        }, 200);
    });
});
//服务器当前日期
function ServerCurrentTime() {
    var now = new Date();
    var year = now.getFullYear();
    var month = now.getMonth();
    var date = now.getDate();
    var day = now.getDay();
    var hour = now.getHours();
    var minu = now.getMinutes();
    var sec = now.getSeconds();
    var week;
    month = month + 1;
    if (month < 10) month = "0" + month;
    if (date < 10) date = "0" + date;
    if (hour < 10) hour = "0" + hour;
    if (minu < 10) minu = "0" + minu;
    if (sec < 10) sec = "0" + sec;
    var arr_week = new Array("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六");
    week = arr_week[day];
    var time = "";
    time = year + "年" + month + "月" + date + "日" + " " + hour + ":" + minu + ":" + sec;
    $("#CurrentTime").text(time);
    var timer = setTimeout("ServerCurrentTime()", 1000);
}
//样式
function readyIndex() {
    $(".main_menu li div").click(function () {
        $(".main_menu li div").removeClass('main_menu leftselected');
        $(this).addClass('main_menu leftselected');
    }).hover(function () {
        $(this).addClass("hoverleftselected");
    }, function () {
        $(this).removeClass("hoverleftselected");
    });
    //点击TOP按钮显示标签
    $("#topnav .droppopup a").hover(function () {
        $("#topnav .droppopup a").removeClass('onnav');
        $(this).addClass('onnav');
        var Y = $(this).attr("offsetLeft");
        $(this).find('.popup').show().css('top', ($(this).offset().top + 71)).css('left', $(this).offset().left - ($(this).find('.popup').width() / 2 - 36));
    }, function () {
        $("#topnav .droppopup a").removeClass('onnav');
        $(this).find('.popup').hide();
    });
    $(".popup li").click(function () {
        $('.popup').hide();
    })
}
/**安全退出**/
function IndexOut() {
    var msg = "<div class='ui_alert'>确认要退出该系统？</div>"
    top.$.dialog({
        id: "confirmDialog",
        lock: true,
        icon: "hits.png",
        content: msg,
        title: "温馨提示",
        button: [
        {
            name: '退出',
            callback: function () {
               Loading(true, "正在退出系统...");
               window.setTimeout(function () {
            	   // var token = $("meta[name='_csrf']").attr("content");
                   // var header = $("meta[name='_csrf_header']").attr("content");
            	   $.ajax({                
                       url: "logout",
                       type: "POST",                                       
                       async: false,
                       beforeSend: function(request) {
                           // request.setRequestHeader(header, token);
                       },
                       success: function (data) { 
                    	   parent.location.reload();
                    	   window.opener = null;
                           var wind = window.open('', '_self');
                           wind.close();                        
                       },
                       error: function (data) {
                           Loading(false);
                           alertDialog("Session已经失效，请刷新重新登录！", -1);
                           //alertDialog(data.responseText, -1);
                       }
                   });
                    
                }, 200);
            }
        },
        {
            name: '取消'
        }
        ]
    });
}

//个性化皮肤设置
function SkinIndex() {
    Dialog("/admin/auth/skinindex", "SkinIndex", "个性化设置", 580, 350);
}