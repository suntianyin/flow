<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width" />
    <#include "../common/meta.ftl">
    <title>用户管理表单页面</title>    
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
                url: "updatepassword",
                type: "POST",
                data: postData,                
                async: false,                
                success: function (data) { 
                		if(data == "success"){
                			Loading(false);
                            tipDialog("更新成功！", 3, 1);
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
            <input id="id" name="id" class="txt" value="${au.id!''}" type="hidden"/>
	            <tr>
	                <th class="formTitle">密码：</th>
	                <td class="formValue">
	                    <input id="password" name="password" type="password" class="txt required" value="${au.password!''}" datacol="yes" err="密码" checkexpession="NotNull" />
	                </td>	                
	            </tr>          
            </table>
        </div>
    </div>
</form>

</div>
</body>
</html>
