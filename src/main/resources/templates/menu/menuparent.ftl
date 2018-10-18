<!DOCTYPE html>
<html>
<head>
    <title>分配权限</title>
    <#include "../common/meta.ftl">
</head>
<body>
<div>        
<script>
	function setSelect(menuid,menuname){
		$(window.parent.frames["Form"].document).find("body").find(".cha").text(menuname);
		$(window.parent.frames["Form"].document).find("body").find(".yuan").text("");
		$(window.parent.frames["Form"].document).find("body").find("input[type='hidden'][name='parentId']").val(menuid);
    	$(window.parent.frames["Form"].document).find("body").find("input[type='hidden'][name='parentId']").text(menuid);    	
	}
    $(function () {
        GetList();
    });
    //加载用户
    function GetList() {        
        $("#MemberList li").click(function () {
        	$(this).addClass("selected");
        	$(this).siblings("li").removeClass("selected");
        });        
        //模糊查询父类菜单（注：这个方法是利用jquery查询）
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
        	var menuid = $('.sys_spec_text .selected a').attr('id');
        	var menuname = $('.sys_spec_text .selected a').text();        	
        	setSelect(menuid, menuname);        	
        	closeDialog();        	
        }, 200);
    }
</script>
<div class="note-prompt" style="margin: 1px;">
    温馨提示：选中复选框即可为菜单添加父类菜单，取消选中则取消父类菜单的添加。
</div>
<div class="border" style="margin: 1px;">
    <div class="btnbartitle">
        <div style="float: left">角色查询：
            <input id="txt_Search" type="text" class="btnbartitleinput" style="width: 120px;" />
        </div>        
    </div>
    <div class="ScrollBar" id="MemberList" style="height: 225px;">
        <ul class="sys_spec_text">
        <li title="自身"><a id="0"><img src="${ctx}/images/role.png">自身</a><i></i></li>
        <#--<c:forEach items="${list }" var="list">-->
        <#list list as list>
        <li title="${list.name!'' }"><a id="${list.id!'' }"><img src="${ctx}/images/role.png">${list.name!'' }</a><i></i></li>
        </#list>
            <#--</c:forEach>        -->
        </ul>
    </div>
</div>
</div>
</body>
</html>
