<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width" />
    <#include "../common/meta.ftl">
    <title>菜单管理表单页面</title>
</head>
<body>
<div>        
<script type="text/javascript">
	function configuration(thisObj){
		var url = "/admin/auth/authmenu/menuparent";
		openDialog(url, "AllotPermission", "父类菜单列表", 630, 300, function (iframe) {			
	        top.frames[iframe].AcceptClick();            
	    });
	}
    //保存事件
    function AcceptClick() {
        if (!CheckDataValid('#form1')) {
            return false;
        }
        Loading(true, "正在提交数据...");
        window.setTimeout(function () {
        	var formObj = $("#form1");
            var postData = formObj.serializeJson();

            $.ajax({                
                url: "menuupdate",
                type: "POST",
                data: postData,                
                async: false,

                success: function (data) {  
                		if(data == "success"){
                			tipDialog("更新菜单成功！", 3, 1);                        
                            top.frames[tabiframeId()].location.reload();
                            closeDialog();
                		}else{
                			Loading(false);
                			alertDialog("只有创建用户才有修改权限！", -1);
                		}                                                
                },
                error: function (data) {
                    Loading(false);
                    alertDialog(data.responseText, -1);
                }
            });
            
        }, 200);
    }
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
            	<input id="id" name="id" class="txt" value="${mn.id!'' }" type="hidden"/>
                <tr>	                
	                <th class="formTitle">菜单名称：</th>
	                <td class="formValue">
	                    <input id="name" name="name" type="text" value="${mn.name!'' }" class="txt required" datacol="yes" err="资源名称" checkexpession="NotNull" />
	                </td>	                
	            </tr>
	            <tr>
	                <th class="formTitle">父类名称：</th>
	                <td class="formValue">	                    
	                    <input id="parentId" name="parentId" type="hidden" value="${mn.parentId !''}" class="txt" />
	                    <a class="yuan">${mpname!'' }</a>
	                    <a class="cha" href="javascript:void(0);" onclick="configuration(this);" style="color: blue;">查找资源</a>
	                </td>
	            </tr>
	            <tr>
	                <th class="formTitle">创建用户Id：</th>
	                <td class="formValue">
	                    <input id="crtUserId" name="crtUserId" value="${mn.crtUserId !''}" type="text" class="txt" readOnly="true"/>
	                </td>
	            </tr>	            
	            <tr>	                
					<th class="formTitle">状态：</th>
	                <td class="formValue">
	                    <select id="enabled" name="enabled" class="txtselect">
                            <#if mn.enabled == 0>
	                			<option value="0" selected ="selected">不可用</option>
	                            <option value="1">可用</option>
	                		</#if>
	                		<#if mn.enabled == 1>
	                			<option value="0">不可用</option>
	                            <option value="1" selected ="selected">可用</option>
	                		</#if>
                        </select>
	                </td>
	            </tr>
	            <tr>
	                <th class="formTitle">描述：</th>
	                <td class="formValue">
	                    <textarea id="description" name="description" maxlength="200" class="txtArea" rows="5">${mn.description!'' }</textarea>
	                </td>	                
	            </tr>
            </table>
        </div>
    </div>
</form>
</div>
</body>
</html>
