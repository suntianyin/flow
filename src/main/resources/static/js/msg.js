var Msg = {
		//置顶
		topMsgs : function(chkName, url){
			if(CheckUtils.checkSelect(chkName)){
				var boxs = $('input[type="checkbox"][name="'+chkName+'"]');
				var canBeTop = true;
				$("#batForm").empty();
				for(var i = 0, len = boxs.length; i < len; i++){
					if(boxs[i].checked ){
						var status = $(boxs[i]).attr("status");
						if(status == '0'){
							var msgId = $(boxs[i]).val();
							$("#batForm").append("<input type='hiden' name='msgIds["+i+"]' value='"+msgId+"'>");
						}else{
							canBeTop = false;
						}
					}  
				}
				if(canBeTop){
					var formObj = $("#batForm");
		            var postData = formObj.serializeJson();
					$.ajax({                
		                url: url,
		                type: "POST",
		                data: postData,
		                async: false,		                
		                success: function (data) {
		                	top.frames[tabiframeId()].location.reload();
		                },
		                error: function (data) {	                    
		                }
		            });
				}else{					
					alertDialog("不是“未发送”状态的消息不能置顶", -1);
				}
			}else{				
				alertDialog("不能空选", -1);
			}
		},
		//重发
		resend : function(chkName, url){
			if(CheckUtils.checkSelect(chkName)){
				var boxs = $('input[type="checkbox"][name="'+chkName+'"]');
				var canBeSend = true;
				$("#batForm").empty();
				for(var i = 0, len = boxs.length; i < len; i++){
					if(boxs[i].checked ){
						var status = $(boxs[i]).attr("status");
						if(status != '0' && status != '-3'){
							var msgId = $(boxs[i]).val();
							$("#batForm").append("<input type='hiden' name='msgIds["+i+"]' value='"+msgId+"'>");
						}else{
							canBeSend = false;
						}
					}  
				}
				if(canBeSend){
					var formObj = $("#batForm");
		            var postData = formObj.serializeJson();
					$.ajax({                
		                url: url,
		                type: "POST",		                                
		                async: false,
		                data: postData,
		                success: function (data) {
		                	top.frames[tabiframeId()].location.reload();
		                },
		                error: function (data) {	                    
		                }
		            });
				}else{					
					alertDialog("“未发送”或“删除”状态的消息不能重发", -1);
				}
			}else{				
				alertDialog("不能空选", -1);
			}
		}
};