<!doctype html>
<html>
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes">
    <link rel="stylesheet" href="${templetPath}css/base.css">
    
</head>
<body>
  <header> 
  	 <#if (publishPath?exists)>
  	 	<img src="${templetPath}images/ico_goback.png" class="goback" onclick="goBack('${publishPath}index.html')"/>
  	 <#else>
  	 	<img src="${templetPath}images/ico_goback.png" class="goback" />
  	 </#if>
     <h2>${title?if_exists}（<span id="pageIndex">1</span>/${colPageSize}）<h2>
  </header>
  
  <div class="mainbody">
      <div class="arrowp leftp" id="leftBtn"></div>
      <div class="carouselp" id="imgSwipe">
         
         <div class="childbox">
         	<#list collist as column>
	            <ul>
	              <li>
	                 <div class="picleft">
	                 	<#if (column.image?exists) && column.image != ""><!--onclick="openOuterImage('${column.image}');"-->
		                 	<#assign imgsnode=column.image?split(";") />
		                 	<#if (imgsnode[0]?exists) && imgsnode[0] != "">
			            	 <img style="max-width:100%;max-height:100%;height:auto;width:auto;" src="${imgsnode[0]}" class="borderimg" />
			            	</#if>
	               	 	<#else><!--onclick="openOuterImage('${templetPath}images/demo_pic6.jpg');"-->
	                      <img style="max-width:100%;max-height:100%;height:auto;width:auto;" src="${templetPath}images/demo_pic6.jpg" class="borderimg"  /> 
	               	 	</#if>
	                 </div>
	                  
	                 <div id="node${column_index}" class="introtxt" onclick="openOuterContent('node${column_index}');" style="overflow-x: hidden;">
	                    <p>${column.title?if_exists}</p>
	                    <p>${column.summary?if_exists}</p>
	                    <p style="padding-bottom: 6%; word-wrap:break-word;">${column.content?if_exists}</p>
	                  </div>
	              </li>
	           </ul>
           </#list>
         </div>
         <ul class="swipe_num">
         	<li class="active"></li>
         	<#if (pageCount != 1)>
		       	<#list 1..(pageCount-1) as i>
		       		<li></li>
		       	</#list>
	       	</#if>
       	</li>
         </ul>
      </div>
      <div class="arrowp rightp" id="rightBtn"></div>
  
  </div>

<script src="${templetPath}js/zepto.js"></script>
<script src="${templetPath}js/swipe.js"></script>
<script>
  function imgSwipe(id) {
	  if(id == "picArea"){
		 var objimg = new Swipe(document.getElementById(id), { 
			  startSlide: 0,
			  auto: 3000,
			  speed: 300,
			  callback: function(index, element) {
				 /* var oSwipe = $('#' + id);
				  var oLi = oSwipe.children().last().find("li");
				  oLi.removeClass("active").eq(index).addClass("active"); */
			  }
		  });
	  }
	  else{
		  obj = new Swipe(document.getElementById(id), { 
			  startSlide: 0,
			  speed: 300,
			  callback: function(index, element) {
				  var oSwipe = $('#' + id);
				  var oLi = oSwipe.children().last().find("li");
				  oLi.removeClass("active").eq(index).addClass("active"); 
				  $("#pageIndex").html(index+1);
			  }
		  });
		  $("#leftBtn").click(function(){
			 obj.prev(); 
		  });
		  $("#rightBtn").click(function(){
			 obj.next();
		  });
	  } 
  }
 
  imgSwipe("imgSwipe");
  imgSwipe("picArea");
</script>

	<!--弹出层-->
	<div class="blackbg" id="textOutBox" style="display:none;">
	  <!--文字弹出层-->
	  <div class="textarea">
	    <a class="close" onclick="closeOuter('textOutBox');"><img src="${templetPath}images/ico_close.png"></a>
	    <div id="content" style="text-align:left;">
	    </div>
	  </div>
	</div>
	<script>
		function openOuterContent(id){
			$("#content").html($("#"+id).html());
			$(".blackbg").css("display","block");
			if($("#content").prev()){
				var pheight = $("#content").parent().height()/2;
				var sheight= $("#content").prev().height()/2;
				$(".blackbg a").css("margin-top",pheight-sheight);
			}
		}
		
		function openOuterImage(image){
			$("#content").html('<img  style="max-width:95%;max-height:95%;height:auto;width:auto;" src="'+image+'" width="100%" height="100%"/>');
			$(".blackbg").css("display","block");
		}
		function closeOuter(id){
			$("#"+id).css("display","none");
		}
		function goBack(location){
			var href = window.location.href;
			var parm="";
			if(href.indexOf("?") != -1){
				var strParams = href.substring(href.indexOf("?")+1);
				var arrParams = strParams.split("&");
				var size = arrParams.length;
				for(var i = 0; i < size; i++){
					var param = arrParams[i];
					if(param.indexOf("index") != -1){
						parm = param;
					}
				}
			}
			if(location.indexOf("?") != -1){
				window.location.href = location+"&"+parm;
			}else{
				window.location.href = location+"?"+parm;
			}
		}
	</script>
</body>
</html>