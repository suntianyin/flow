<!doctype html>
<html>
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes">
    <link rel="stylesheet" href="${templetPath}css/vertical.css">
    <link rel="stylesheet" href="${templetPath}css/idangerous.swiper.css">
    <script src="${templetPath}js/jquery-1.10.2.min.js"></script>
    <script src="${templetPath}js/idangerous.swiper-2.1.min.js"></script>
	
    <script>
    	var viewWidth = $(window).width();
		var viewHeight = $(window).height();
		$.addStyle = function(){
			
			//var hradio = 1516/746;
			var hradio = 1670/830;
			var r1 = 830/1080;
			var r2 = 1670/1920;
			if(viewHeight/viewWidth >= hradio){
				$(".tblank").css({ "height": parseInt((viewHeight - viewWidth*r1*hradio)/2) });
				$(".wrap-area").css({"width": parseInt(viewWidth*r1), "height":parseInt(viewWidth*r1*hradio*0.92)});
			}
			else{
				$(".tblank").css({ "height": parseInt(viewHeight*(1-r2)/2) });
				$(".wrap-area").css({"width":parseInt(viewHeight*r2/hradio), "height":parseInt(viewHeight*r2*0.92)});
			}
			
			$(".art-img").each(function(){
				if($(this).find("img").length == 0){
					$(this).parent().find(".art-cont").css({"height": "92%"});
				}
			})
		}
		$.addStyle2 = function(){
			var pagWidth = $(".pagination").width();
			$(".pagination").css({ "left": parseInt((viewWidth-pagWidth)/2) });
		}
	    $(function(){
			$.addStyle();
			var swiperParent = new Swiper('#swiperParent2',{
				pagination: '.pagination'
			});
			$.addStyle2();
			

		})
		function goBack(link){
			var _link = link;
			var parm = "";
			if(getQueryString("index")){
				parm = getQueryString("index");
			}
			location.href = _link+"#slide"+parm;

		}
		var getQueryString = function(name){
			var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
			var r = location.search.substr(1).match(reg);
			if (r!=null) return unescape(r[2]);
			return null;
		}
		window.onload = function(){
			$(".art-img").each(function(){
				if($(this).find("img").length > 0){
					var _imgH = $(this).find("img").height();
					$(this).css({"height":_imgH });
					$(this).parent().find(".art-cont").css({"height": $(this).parent().height()*0.92-_imgH});
				}
			})
			var swiperPicture= new Swiper('#swiperPicture2',{
				pagination: '.pagination2'	
			});
			
			$(".pagination2").css({"left":"50%", "margin-left":-$(".pagination2").width()/2})
		}
		
	</script>
    <style>
	
	</style>
</head>
<body>
 <div class="tblank"></div>
 <div class="wrap-area" id="swiperParent2">
   
   <div class="swiper-wrapper">
   	  <!--第一页 -->
   	  <#list collist as column>
      <div  class="swiper-slide">
          <div class="art-img">
            <#if (column.image?exists) && column.image != "">
             	<#assign imgsnode=column.image?split(";") />
             	<#if (imgsnode[0]?exists) && imgsnode[0] != "">
             	
             	
             	
             	
				  <div id="swiperPicture2" class="swiper-container2">
					  <div class="swiper-wrapper">
						
	            		<#list imgsnode as image>
		            		<#if (image?exists) && (image!='')>
		            		<div class="swiper-slide"><img src="${image}" / ></div>
		            		</#if>
	            		</#list>
					  </div>
				  </div>
				  
            	  <!--<img src="${imgsnode[0]}" />-->
            	</#if>
       	 	</#if>
       	 	<div class="pagination2"></div>
          </div>
          
          <div class="art-cont">
		  ${column.content?if_exists}
		  </div>
       </div>
       </#list>
   </div>
 </div>
 
 <div class="pagination" style="bottom:13.6%"></div>

<div class="bottom">
<#if (publishPath?exists)>
  	 	<a class="goback" onclick="goBack('${publishPath}index.html')"></a>
  	 <#else>
  	 	<a class="goback" ></a>
  	 </#if>
     <h2>${title?if_exists}（<span id="pageIndex">1</span>/${colPageSize}）<h2>

</div>
</body>
</html>