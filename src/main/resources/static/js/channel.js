    var Channel = {
   		//设置频道是否隐藏，如果菜单隐藏则菜单不能设置为主页
   		setIsHide : function(){
   			var msg = "";
   			var isHide = $("#isHide").val();
   			if(isHide == 1){		//设置为隐藏
   				var isHome = $("#isHome").val();
   				if(isHome == 1){
   					msg = "本菜单已经设置为主页，不能设置为隐藏";
   					isHide = 1;
   					$("#isHideChannel").removeAttr("checked");
   					$.showMsg("#columnMsg", msg);
   				}else{
   					isHide = 0;
   					$("#isHideChannel").attr("checked","checked");
   				}
   			}else{//设置为不隐藏
   				isHide = 1;
   				$("#isHideChannel").removeAttr("checked");
   			}
   			$("#isHide").val(isHide);
   		},
		//设置栏目主页
   		setColumnHome : function(templateId){
   			var msg = "";
   			var isHome = $("#isHome").val();
   			if(isHome == 1){
   				Channel.setIsHome();
   			}else{
   				if(!Channel.checkColumnHome(templateId)){//不存在主页时才能设置主页
   					Channel.setIsHome();
   				}else{
   					$("#isHomeChannel").removeAttr("checked");
   					$("#isHome").val(0);
   					msg = "当前栏目已经设置主页，请不要重复设置，或者取消原主页再设置";
   					$.showMsg("#columnMsg", msg);
   				}
   			}
   		},
   		//检查该栏目下是否已有主页
   		checkColumnHome : function(templateId){
   			var hasHome = false;
   			$.ajax({
   				type:"get",
   				url:"checkcolumnhomebytemp?templateid="+templateId,
   				async:false,
   				success:function(data){
   					if(data != 0){
   						hasHome = true;
   					}   					
   				}
   			});
   			return hasHome;
   		},
   		//设置是否为主页，如果要设置为主页，菜单不能设置为隐藏
   		setIsHome : function(){
   			var msg = "";
   			var isHome = $("#isHome").val();
   			if(isHome == 0){	//设置为主页
   				var isHide = $("#isHide").val();
   				if(isHide == 0){
   					msg = "本菜单已经设置为隐藏，不能设置为主页";
   					isHome = 0;
   					$("#isHomeChannel").removeAttr("checked");
   					$.showMsg("#columnMsg", msg);
   				}else{
   					isHome = 1;
   					$("#isHomeChannel").attr("checked","checked");
   				}
   			}else{//设置为非主页
   				isHome = 0;
   				$("#isHomeChannel").removeAttr("checked");
   			}
   			$("#isHome").val(isHome);
   		}
    }