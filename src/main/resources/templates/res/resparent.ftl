<!DOCTYPE html>
<html>
<head>
    <title>分配权限</title>
    <#include "../common/meta.ftl">
</head>
<body>
<div>        
<script>
	function setSelect(resid,resname){
		$(window.parent.frames["Form"].document).find("body").find(".cha").text(resname);
		$(window.parent.frames["Form"].document).find("body").find(".yuan").text("");
		$(window.parent.frames["Form"].document).find("body").find("input[type='hidden'][name='parentId']").val(resid);
    	$(window.parent.frames["Form"].document).find("body").find("input[type='hidden'][name='parentId']").text(resid);    	
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
        //模糊查询父类资源（注：这个方法是利用jquery查询）
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
        	var resid = $('.sys_spec_text .selected a').attr('id');
        	var resname = $('.sys_spec_text .selected a').text();        	
        	setSelect(resid, resname);        	
        	closeDialog();        	
        }, 200);
    }
</script>
<div class="note-prompt" style="margin: 1px;">
    温馨提示：选中复选框即可为资源添加父类资源，取消选中则取消父类资源的添加。
</div>
<input id="roleid" name="roleid" class="txt" value="${roleid !''}" type="hidden"/>
<div class="border" style="margin: 1px;">
    <div class="btnbartitle">
        <div style="float: left">角色查询：
            <input id="txt_Search" type="text" class="btnbartitleinput" style="width: 120px;" />
        </div>        
    </div>
    <div class="ScrollBar" id="MemberList" style="height: 225px;">
        <ul class="sys_spec_text">
        <li title="自身"><a id="0"><img src="${ctx}/images/role.png">自身</a><i></i></li>
         <#list list as list>
        <li title="${list.name!'' }"><a id="${list.id?c }"><img src="${ctx}/images/role.png">${list.name !''}</a><i></i></li>
        </#list>
        </ul>
    </div>
</div>
</div>
</body>
</html>
