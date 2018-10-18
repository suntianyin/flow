<!DOCTYPE html>
<html>
<head>
    <title>分配权限</title>
    <#include "../common/meta.ftl">
</head>
<body>
<div>        
<script>
    $(function () {
        GetList();
        //查找所有资源和该用户下已经选择的资源
        var zNodes = ${json};        
		$.fn.zTree.init($("#treeDemo"), setting, zNodes);
		var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
		treeObj.expandAll(true);
        nodes = treeObj.getCheckedNodes(true);       
         for(var i = 0; i < nodes.length; i++){
        	 var resId = nodes[i].id;
   			var resName = nodes[i].name;
   			$("#MemberList ul").append('<li id=R'+resId+'><a id='+resId+'><img src="${ctx}/images/role.png">'+resName+'</a><i></i></li>');
        }
    });
    var setting = {
    		check: {
		 			chkboxType : {"Y": "", "N": "s" },//取消勾选影响子节点
		 			enable : true,
		 			autoCheckTrigger : true//设置自动关联勾选时是否触发 beforeCheck / onCheck 事件回调函数
   	 		},
	   		data: {
				simpleData: {
					enable: true,
					rootPId:null
				}
			},  
	   	 	callback: {	   	 		
				onCheck: onCheck
			}
   	    };
  	function onCheck(event, treeId, treeNode){
  		if(treeNode.checked == true){  			
  			var resId = treeNode.id;
       		var resName = treeNode.name;
       		$("#MemberList ul").append('<li id=R'+resId+'><a id='+resId+'><img src="${ctx}/images/role.png">'+resName+'</a><i></i></li>');
 		}
  		if(treeNode.checked == false){
  			var resId = treeNode.id;
  			$("#MemberList ul #R"+resId).remove();
  		}  		
  	}  	
    //加载资源
    function GetList() {        
        //模糊查询资源（注：这个方法是利用jquery查询）
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
        	var resid = "";
            var roleid = $("#roleid").val();
            $('.sys_spec_text a').each(function () { resid += $(this).attr('id') + ","; });
            resid = resid.substr(0,resid.length-1);
            var postData = {"resid":resid,"roleid":roleid};

            $.ajax({                
                url: "roleresupdate",
                type: "POST",
                data: postData,                
                async: false,
                success: function (data) {
                        tipDialog("更新角色权限成功！", 3, 1);
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
    温馨提示：选中复选框即可为角色添加权限，取消选中则取消权限的添加。
</div>
<input id="roleid" name="roleid" class="txt" value="${roleid !''}" type="hidden"/>
<div id="layout" class="layout" onselectstart="return false;" style="-moz-user-select: none;">
	<!--左边-->
    <div class="layoutPanel layout-west" style="position: absolute; z-index: 99; height: 431px; left: 0px; width: 200px; overflow-y: scroll;">
        <div class="btnbartitle">
            <div>角色
                 <ul id="treeDemo" class="ztree"></ul>
            </div>
        </div>
        <div class="ScrollBar" id="AccessTree"></div>
    </div>
	<!--中间-->
    <div id="layoutRightPanel" class="layoutPanel layout-center" style="position: relative; z-index: 99; height: 431px; left: 201px; width: 625px;">
	    <input id="roleid" name="roleid" class="txt" value="${roleid !''}" type="hidden"/>
		<div class="border" style="margin: 1px;">
		    <div class="btnbartitle">
		        <div style="float: left">资源查询：
		            <input id="txt_Search" type="text" class="btnbartitleinput" style="width: 120px;" />
		        </div>		        
		    </div>
		    <div class="ScrollBar" id="MemberList" style="height: 401px;">
		        <ul class="sys_spec_text">		        		        
		        </ul>
		    </div>
		</div>
    </div>
</div>
</div>
</body>
</html>
