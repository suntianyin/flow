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

<header></header>
	
<div class="mainbody" id="indexPage" style="visibility:hidden;">
    <div class="arrowp leftp" id="leftBtn"></div>
    <div class="carouselp" id="imgSwipe">
       <div class="showbox">
       	  <#macro showDataWel collist artlist welColumn flag>
	       	  <!-- colPageSize artPageSize是模板一页分别对应的个数 -->
	       	  <#if (welColumn?exists) && (welColumn.title?exists)>
		       	  <ul>
			        <li class="welcome">${welColumn.title}</li>
			      </ul>
		      </#if>
       	  	  <#if flag!=1>
       	  	  	<#if (collist?exists && collist?size > 0) || (artlist?exists && artlist?size > 0)>
       	  	  	<@showData collist=collist artlist=artlist />
       	  	  	</#if>
       	  	  </#if>
       	  </#macro>
       	  <#macro showData collist artlist >
       	  	<#list 1..pageCount as pageIndex>
	   	  		<#assign colStep=colPageSize*pageIndex_index />
       	  		<#assign artStep=artPageSize*pageIndex_index /> 
       	  	
       	  	  <ul>
               <!--第1个栏目-->
       	  		<li class="blocks1">
	               <section>
	               	 <#if collist[colStep]?exists>
	               	 	<#if (collist[colStep].image?exists)>
	               	 		<#assign imgcol1node=collist[colStep].image?split(";") />
		                 	<#if (imgcol1node[0]?exists) && imgcol1node[0] != "">
		               	 		<#if (collist[colStep].link?exists)>
			            	 		<img src="${imgcol1node[0]}" class="img1" onclick="window.location='${collist[colStep].link}?index=${pageIndex}'"/>
			            	 	<#else>
			            	 		<img src="${imgcol1node[0]}" class="img1" onclick=""/>
			            	 	</#if>
		            	 	</#if>
	               	 	<#else>
	               	 		<#if (collist[colStep].link?exists)>
		            	 		<img src="${templetPath}images/demo_pic1.jpg" class="img1" onclick="window.location='${collist[colStep].link}?index=${pageIndex}'"/>
		            	 	<#else>
		            	 		<img src="${templetPath}images/demo_pic1.jpg" class="img1" onclick=""/>
		            	 	</#if>
	               	 	</#if>
	               	 	
	               	 	<#if (collist[colStep].link?exists)>
		                 <span class="bigtext" onclick="window.location='${collist[colStep].link}?index=${pageIndex}'">${collist[colStep].title?if_exists}</span>
	            	 	<#else>
		                 <span class="bigtext" onclick="">${collist[colStep].title?if_exists}</span>
	            	 	</#if>
	               	 </#if>
	               </section>
	               <section>
	               	 <!--第2个栏目-->
	               	 <#if collist[colStep+1]?exists>
		                 <div class="littlepic">
		                 	<#if (collist[colStep+1].image?exists)>
		                 		<#assign imgcol2node=collist[colStep+1].image?split(";") />
		                 		<#if (imgcol2node[0]?exists) && imgcol2node[0] != "">
			                 		<#if (collist[colStep+1].link?exists)>
				            	 		<img src="${imgcol2node[0]}" onclick="window.location='${collist[colStep+1].link}?index=${pageIndex}'"/>
				            	 	<#else>
				            	 		<img src="${imgcol2node[0]}" onclick=""/>
				            	 	</#if>
			            	 	</#if>
		               	 	<#else>
		               	 		<#if (collist[colStep+1].link?exists)>
					            	 <img src="${templetPath}images/demo_pic2.jpg" onclick="window.location='${collist[colStep+1].link}?index=${pageIndex}'"/>
			            	 	<#else>
			            			 <img src="${templetPath}images/demo_pic2.jpg" onclick=""/>
			            	 	</#if>
		               	 	</#if>
		               	 	<#if (collist[colStep+1].link?exists)>
			            	 <span onclick="window.location='${collist[colStep+1].link}?index=${pageIndex}'">${collist[colStep+1].title?if_exists}</span> 
		            	 	<#else>
			            	 <span onclick="">${collist[colStep+1].title?if_exists}</span> 
		            	 	</#if>
		                 </div>
	               	 </#if>
	                 <!--第3个栏目-->
	                 <#if collist[colStep+2]?exists>
		                 <div class="littlepic">
		                 	<#if (collist[colStep+2].image?exists)>
		                 		<#assign imgcol3node=collist[colStep+2].image?split(";") />
		                 		<#if (imgcol3node[0]?exists) && imgcol3node[0] != "">
			                 		<#if (collist[colStep+2].link?exists)>
					            	 <img src="${imgcol3node[0]}" onclick="window.location='${collist[colStep+2].link}?index=${pageIndex}'"/>
				            	 	<#else>
					            	 <img src="${imgcol3node[0]}" onclick=""/>
				            	 	</#if>
			            	 	</#if>
		               	 	<#else>
		               	 		<#if (collist[colStep+2].link?exists)>
				            	 <img src="${templetPath}images/demo_pic3.jpg" onclick="window.location='${collist[colStep+2].link}?index=${pageIndex}'"/>
			            	 	<#else>
			            	 	 <img src="${templetPath}images/demo_pic3.jpg" onclick=""/>
			            	 	</#if>
		               	 	</#if>	
		               	 	<#if (collist[colStep+2].link?exists)>
			            	 <span onclick="window.location='${collist[colStep+2].link}?index=${pageIndex}'">${collist[colStep+2].title?if_exists}</span> 
		            	 	<#else>
			            	 <span onclick="">${collist[colStep+2].title?if_exists}</span> 
		            	 	</#if>
		                 </div>
	               	 </#if>
	               </section>
	            </li>
	            <!--第1个文章-->
	            <#if (artlist[artStep]?exists)&&(artlist[artStep].content?exists) && artlist[artStep].content != "">
	            <li class="blocks2" id="art${artStep}" onclick="openOuter('arthid${artStep}','content');">
	            	<input id="arthid${artStep}" type="hidden" value="${artlist[artStep].content?if_exists}"/> 
	              <article>
	                	${artlist[artStep].content?if_exists}
	              </article>
	            </li>
	            <#else>
	            <li class="blocks2" id="art${artStep}">
	              <article>
	              </article>
	            </li>
	            </#if>
	               	 
	            <!--第2个文章-->
	            <#if (artlist[artStep+1]?exists)&&(artlist[artStep+1].content?exists) && artlist[artStep+1].content != "">
	            <li class="blocks3" >
	            	<input id="arthid${artStep+1}" type="hidden" value="${artlist[artStep+1].content?if_exists}"/> 
	               	<p id="art${artStep+1}" onclick="openOuter('arthid${artStep+1}','content');">${artlist[artStep+1].content?if_exists}</p>
	            <#else>
	            <li class="blocks3" >
	            </#if>
                <#if (artlist[artStep+1]?exists)>
                 	<#if (artlist[artStep+1].image?exists)><!-- onclick="openOuter('${artlist[artStep+1].image}','image');" -->
            			<#assign imgart2node=artlist[artStep+1].image?split(";") />
		                <#if (imgart2node[0]?exists) && imgart2node[0] != "">
            				<img src="${imgart2node[0]}" />
            			</#if>
	           	 	<#else><!-- onclick="openOuter('${templetPath}images/demo_pic4.jpg','image');" -->
	            		<img src="${templetPath}images/demo_pic4.jpg" />
            	 	</#if>
           	 	</#if>
	            </li>
	            <!--第4个栏目-->
	            <li class="blocks4">
				<#if collist[colStep+3]?exists>
					<#if (collist[colStep+3].link?exists)>
			            <span onclick="window.location='${collist[colStep+3].link}?index=${pageIndex}'">
			                ${collist[colStep+3].title?if_exists}
						</span>
            	 	<#else>
		            	<span onclick="">${collist[colStep+3].title?if_exists}</span>
            	 	</#if>
               	 </#if>
				</li>
	            <!--第3个文章-->
	            <#if artlist[artStep+2]?exists>
	            <li class="blocks5" id="${pageIndex_index}"  onclick="manyImgSwipe('${artlist[artStep+2].image?if_exists}');">
	            <#else>
	            <li class="blocks5" id="${pageIndex_index}">
	            </#if>
	            	<#if artlist[artStep+2]?exists>
	            		<div>
		            		<#if (artlist[artStep+2].image?exists) && artlist[artStep+2].image != "">
	            			<div id="picArea${pageIndex_index}" style="z-index:1; height:100%; visibility: visible;">
	            				<div style=" height:100%; ">
			            		<#assign imgsnode=artlist[artStep+2].image?split(";") />
			            		<#list imgsnode as image>
				            		<#if (image?exists) && (image!='')>
				            		<img src="${image}" / >
				            		</#if>
			            		</#list>
			            		</div>
			            	</div>
			            	
		            		<div class="littleswipe" id="lSwipe${pageIndex_index}">
			            		<ul class="swipe_num">
							       	<li class="active"></li>
							       	<#assign imgsnum=(imgsnode?size-2) />
							       	<#if (imgsnum gt 0)>
								       	<#list 1..imgsnum as num>
								       	<li></li>
								       	</#list>
							       	</#if>
							    </ul>
							</div>
		            		</#if>
	                	</div>
	               	 </#if>
	            </li>
	          </ul>
       	  	</#list>
       	  </#macro>
       	  
       	  <#if (welColumn?exists)>
       	  	<@showDataWel collist=collist artlist=artlist welColumn=welColumn flag=flag/>
       	  <#else>
       	 	 <@showData collist=collist artlist=artlist />
       	  </#if>
       </div>
       <ul class="swipe_num" id="page-swipe">
       	<li class="active"></li>
       	<#if flag!=1>
	       	<#if (collist?exists && collist?size > 0) || (artlist?exists && artlist?size > 0)>
		       	<#if (welColumn?exists) && (welColumn.title?exists)>
		       	<li ></li>
		       	</#if>
		       	<#if (pageCount != 1)>
			       	<#list 1..(pageCount-1) as i>
			       		<li></li>
			       	</#list>
		       	</#if>
	       	</#if>
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
	  var location = window.location.href;
	  var index = 0;
	  if(location.indexOf("?") != -1){
	  	var strParams = location.substring(location.indexOf("?")+1);
	  	var arrParams = strParams.split("&");
	  	var size = arrParams.length;
	  	for(var i = 0; i < size; i++){
	  		if(arrParams[i].indexOf("index") != -1){
	  			var param = arrParams[i].split("=");
	  			index = parseInt(param[1]);
	  			break;
	  		}
	  	}
	  }
	  var obj = new Swipe(document.getElementById(id), { 
		  startSlide: index,
		  speed: 300,
		  callback: function(index, element) {
			  var oSwipe = $('#' + id);
			  var oLi = oSwipe.children().last().find("li");
			  oLi.removeClass("active").eq(index).addClass("active"); 
		  }
	  });
	  $("#page-swipe li").removeClass("active");
	  $("#page-swipe li").each(function(inx){
	  	if(inx == index){
	  		$(this).addClass("active");
	  	}
	  });
	  $("#leftBtn").click(function(){
		 obj.prev(); 
	  });
	  $("#rightBtn").click(function(){
		 obj.next();
	  });
	  $("#indexPage").css("visibility","visible");
	  /*if(index != -1){
	  	obj.slide(index, 0);
	  }*/
  }
  function artImgSwipe(){
  	$(".blocks5").each(function(id){
  		var objimg = new Swipe(document.getElementById("picArea"+id),{ 
			  startSlide: 0,
			  auto: 3000,
			  speed: 300,
			  callback: function(index, element) {
			  	 var oSwipe = $('#picArea' + id);
			  	 var oLi = oSwipe.parent().find("li");
			  	 oLi.removeClass("active").eq(index).addClass("active"); 	
			  }
		  });
		if($("#lSwipe"+id))
			$("#lSwipe"+id).css("margin-left",-$("#lSwipe"+id).find("ul").width()/2);  
  	})
  }
  
  imgSwipe("imgSwipe");
  artImgSwipe();
  
  function manyImgSwipe(imgs){
  	if(imgs != null){
  		var pics = imgs.split(';');
  		var str = "<div class='holipiclist'>";
  		for(var i = 0, len = pics.length; i < len-1; i++){
  			str += "<img src='"+pics[i]+"' style='position: relative; float: left; height: 100%; width: 500px;' />";
  		}

  		$("#pics").html("").html(str+"</div>");
  		if($("#pics").prev()){
			var pheight = $("#pics").parent().height()/2;
			var sheight= $("#pics").prev().height()/2;
			$(".blackbg a").css("margin-top",pheight-sheight);
		}
  		var objimg = new Swipe(document.getElementById("pics"),{ 
			  startSlide: 0,
			  speed: 300
		  });
  	}
  }
  manyImgSwipe("pics");
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
	<!--多图片弹出层-->
	<div class="blackbg" id="picOutBox" style="display:none;">
	  <div class="textarea">
	    <a class="close" onclick="closeOuter('picOutBox');"><img src="${templetPath}images/ico_close.png"></a>
	    <div id="pics">
	    </div>
	  </div>
	</div>
	<script>
		function openOuter(id,type){
			$("#content").html("");
			/*
			if(type == 'content'){
				$("#content").html(id);
			}else if(type == 'image'){
				$("#content").html('<img style="max-width:95%;max-height:95%;height:auto;width:auto;" src="'+id+'" width="100%" height="100%"/>');
			}*/
			if(type == 'content'){
				$("#content").html($("#"+id).attr("value"));
			}else if(type == 'image'){
				$("#content").html('<img style="max-width:95%;max-height:95%;height:auto;width:auto;" src="'+id+'" width="100%" height="100%"/>');
			}
			$("#textOutBox").css("display","block");
			if($("#content").prev()){
				var pheight = $("#content").parent().height()/2;
				var sheight= $("#content").prev().height()/2;
				$(".blackbg a").css("margin-top",pheight-sheight);
			}
		}
		function closeOuter(id){
			$("#"+id).css("display","none");
		}
		$(function(){
			$(".blocks5 img").click(function(){
				$("#picOutBox").css("display","block");
			});
			getSomeText(2,.9,.94);
			getSomeText(3,.94,.94);
		})
		function getSomeText(num,rw,rh){
			var _box = $(".blocks"+num);
			var _obj = _box.find("article");
			if(!_obj.length){
				_obj = _box.find("p");
			}
			
			var w1 = _obj.width()*rw;	
			var h1 = _box.height()*rh;
			var _tSize = parseInt(_obj.css("font-size"));
			
			_obj.each(function(index, element) {
				var _str = $.trim($(this).html());
				var _nLength = get_length(_str);								
				var _nHor = parseInt(w1/_tSize);	//每行可以放多少字		
				var _nVer = parseInt(h1/(_tSize*1.5));  //行数
				
				var nPos = get_length(_str,_nHor*_nVer-6);
				var _textart = $(this).html();
				if(_textart != null && $.trim(_textart) != ''){
					$(this).html(_str.substring(0,nPos)+"...");
					$(this).css({"padding-top":(_box.height()-_tSize*1.5*_nVer)/2-2,"height":"auto"});
				}
            });
		}

		function get_length(str,char_num){
			var char_length = 0;
			for (var i = 0; i < str.length; i++){
				var son_char = str.charAt(i);
				encodeURI(son_char).length > 2 ? char_length += 1 : char_length += 0.5;
				if(char_num && char_length>=char_num ){
					return i; 
				}
			}
			return char_length;
		}
	</script>

</body>
</html>