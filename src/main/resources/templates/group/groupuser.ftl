<!DOCTYPE html>
<html>
<head>
    <title>组分配用户</title>
    <#include "../common/meta.ftl">
</head>
<body>
<div>
    <script>
        $(function () {
            GetList();
        });
        //加载用户
        function GetList() {
            $("#MemberList li").click(function () {
                if (!$(this).attr('disabled')) {
                    if (!!$(this).hasClass("selected")) {
                        $(this).removeClass("selected");
                        $(this).find('a').removeClass("a_selected");
                    } else {
                        $(this).addClass("selected").siblings("li");
                        $(this).find('a').addClass("a_selected");
                    }
                }
            });
            //自定义复选框 全选/反选
            $("#CheckButton").click(function () {
                if (!!$(this).hasClass("checkAllOff")) {
                    $(this).attr('title', '反选');
                    $(this).text('反选');
                    $(this).attr('class', 'checkAllOn');
                    $('.sys_spec_text li').addClass('selected');
                } else {
                    $(this).attr('title', '全选');
                    $(this).text('全选');
                    $(this).attr('class', 'checkAllOff');
                    $('.sys_spec_text li').removeClass('selected');
                }
            });
            //模糊查询用户（注：这个方法是利用jquery查询）
            $("#txt_Search").keyup(function () {
                if ($(this).val() != "") {
                    var Search = $(this).val();
                    window.setTimeout(function () {
                        $(".sys_spec_text li")
                                .hide()
                                .filter(":contains('" + (Search) + "')")
                                .show();
                    }, 200);
                } else {
                    $(".sys_spec_text li").show();
                }
            }).keyup();
        }
        //保存事件
        function AcceptClick() {
            Loading(true, "正在提交数据...");
            window.setTimeout(function () {
                var userid = "";
                var groupid = $("#groupid").val();
                $('.sys_spec_text .selected a').each(function () { userid += $(this).attr('id') + ","; });
                userid = userid.substr(0,userid.length-1);
                var postData = {"userid":userid,"groupid":groupid};

                $.ajax({
                    url: "groupuserupdate",
                    type: "POST",
                    data: postData,
                    async: false,

                    success: function (data) {
                        tipDialog("更新用户组成员成功！", 3, 1);
                        parent.location.reload();
                        //top.frames[tabiframeId()].location.reload();
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
    <div class="note-prompt" style="margin: 1px;">
        温馨提示：选中复选框即可添加用户，取消选中则取消用户。
    </div>
    <input id="groupid" name="groupid" class="txt" value="${groupid!'' }" type="hidden"/>
    <div class="border" style="margin: 1px;">
        <div class="btnbartitle">
            <div style="float: left">用户查询：
                <input id="txt_Search" type="text" class="btnbartitleinput" style="width: 120px;" />
            </div>
            <div style="float: right">
                <label id="CheckButton" class="checkAllOff" title="全选">全选</label>
            </div>
        </div>
        <div class="ScrollBar" id="MemberList" style="height: 225px;">
            <ul class="sys_spec_text">
                <#list listremain as listremain>
                    <li title="${listremain.userName!'' }"><a id="${listremain.id!'' }"><img src="${ctx}/images/role.png">${listremain.userName!'' }</a><i></i></li>
                </#list>
                 <#list listthis as listthis>
                    <li title="${listthis.userName!'' }" class="selected"><a id="${listthis.id!'' }"><img src="${ctx}/images/role.png">${listthis.userName!'' }</a><i></i></li>
                 </#list>
            </ul>
        </div>
    </div>
</div>
</body>
</html>
