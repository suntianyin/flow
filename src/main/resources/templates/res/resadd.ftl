<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width" />
    <#include "../common/meta.ftl">
    <title>添加资源页面</title>
</head>
<body>
<div>        
<script type="text/javascript">
	function cancelSelect(typeId){
		var locatePos = $("#"+typeId);
		locatePos.find("input[type='hidden'][name='parentId']").val("0");		
		locatePos.find(".cancelSelect").hide();
	}
	function configuration(thisObj){
		var url = "/admin/auth/authres/resparent";
		openDialog(url, "AllotPermission", "父类资源列表", 630, 300, function (iframe) {			
            top.frames[iframe].AcceptClick();            
        });
	}
    //保存事件
    function AcceptClick() {
        if (!CheckDataValid('#form12')) {
            return false;
        }
        Loading(true, "正在提交数据...");
        window.setTimeout(function () {
        	var formObj = $("#form12");
            var postData = formObj.serializeJson();

            $.ajax({                
                url: "ressave",
                type: "POST",
                data: postData,                
                async: false,
                success: function (data) {                       
                        tipDialog("新增资源成功！", 3, 1);                        
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
<form id="form12">
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
	                <th class="formTitle">资源名称：</th>
	                <td class="formValue">
	                    <input id="name" name="name" type="text" class="txt required" datacol="yes" err="资源名称" checkexpession="NotNull" />
	                </td>
	                <th class="formTitle">父类名称：</th>
	                <td class="formValue">
	                    <input id="parentId" name="parentId" type="hidden" class="txt" value="0"/>
	                    <a class="cha" href="javascript:void(0);" onclick="configuration(this);" style="color: blue;">查找资源</a> 						
	                </td>
	            </tr>
	            <tr>
	                <th class="formTitle">code：</th>
	                <td class="formValue">
	                    <input id="code" name="code" type="text" class="txt" />
                    </td>
	                <th class="formTitle">所属菜单：</th>
	                <td class="formValue">
	                	<select id="menuId" name="menuId" class="txtselect">
							<#list list as list>
	                			<option value="${list.id!'' }">${list.name!'' }</option>
	                		</#list>
                                <#--<input id="menuId" name="menuId"type="text" class="txt" datacol="yes" err="菜单"  />-->
	                	</select>
	                </td>
	            </tr>
	            <tr>
	                <th class="formTitle">url：</th>
	                <td class="formValue">
	                    <input id="url" name="url" type="text" class="txt required" datacol="yes" err="url" checkexpession="NotNull"/>
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
	                <th class="formTitle">类型：</th>
	                <td class="formValue">
	                    <select id="type" name="type" class="txtselect">
                            <option value="0">按钮</option>
                            <option value="1">模块</option>
                            <option value="2">接口</option>
                        </select>
	                </td>
	                <th class="formTitle">顺序：</th>
	                <td class="formValue">
	                    <input id="viewOrder" name="viewOrder" type="text" class="txt" datacol="yes" err="顺序" checkexpession="NumOrNull"/>
	                </td>
	            </tr>	            	    
	            <tr>
	            	<th class="formTitle">描述：</th>
	                <td class="formValue">
	                    <textarea id="description" name="description" maxlength="200" class="txtArea" rows="5"></textarea>
	                </td>
	            </tr>        
            </table>
        </div>
    </div>
</form>
</div>
</body>
</html>
