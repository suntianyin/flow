<!DOCTYPE html>
<html>
<head>
    <title>添加用户组页面</title>
    <#include "../common/meta.ftl">
</head>
<body>
<div>        
<script type="text/javascript">
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
                url: "groupsave",
                type: "POST",
                data: postData,                
                async: false,

                success: function (data) {
                        tipDialog("新增用户组成功！", 3, 1);                        
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
<form id="form1" style="margin: 1px">
    <div id="message" style="display: none"></div>
    <table class="form">        
        <tr>
            <th class="formTitle">用户组名称：
            </th>
            <td class="formValue">
                <input id="name" name="name" type="text" class="txt required" datacol="yes" err="用户组名称" checkexpession="NotNull" />
            </td>
        </tr>
        <tr>
            <th class="formTitle">所属机构：</th>
            <td class="formValue">
                <select id="orgId" name="orgId" class="txtselect">
               		<#list list as list>
               			<option value="${list.id?c }">${list.orgName }</option>
               		</#list>
                        <#--<input id="orgId" name="orgId"type="text" class="txt" datacol="yes" err="机构"  />-->
	            </select>
            </td>
        </tr>
        <!-- <tr>
            <th class="formTitle">父级组id：</th>
            <td class="formValue">
                <input id="parentId" name="parentId" type="text" class="txt" />
            </td>
        </tr> -->
        <tr>
            <th class="formTitle">状态：</th>
            <td class="formValue">
                <select id="enabled" name="enabled" class="txtselect">
                   <option value="0">不可用</option>
                   <option value="1" selected="selected">可用</option>
                </select>
            </td>
        </tr>
        <tr>
            <th class="formTitle">描述：</th>
            <td class="formValue">
                <textarea id="description" name="description" maxlength="200" class="txtArea" rows="5"></textarea>
            </td>
        </tr>
    </table>
</form>
</div>
</body>
</html>
