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
    <script src="${templetPath}js/idangerous.swiper.hashnav.js"></script>
    
	
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
				$(".wrap-area").css({"width": parseInt(viewWidth*r1), "height":parseInt(viewWidth*r1*hradio)});
			}
			else{
				$(".tblank").css({ "height": parseInt(viewHeight*(1-r2)/2) });
				$(".wrap-area").css({"width":parseInt(viewHeight*r2/hradio), "height":parseInt(viewHeight*r2)});
			}	
		}
		$.addStyle2 = function(){
			var pagWidth = $(".pagination").width();
			$(".pagination").css({ "left": parseInt((viewWidth-pagWidth)/2) });
		}
		$.getSomeText = function(id, rw ,rh){
			var _box = $("#"+id);
			var _obj = _box.find("p");
			
			
			var w1 = _box.width()*rw;	
			var h1 = _box.height()*rh;
			var _tSize = parseInt(_obj.css("font-size"));
			
			_obj.each(function(index, element) {
				var _str = $.trim($(this).html());
				var _nLength = $.get_length(_str);								
				var _nHor = parseInt(w1/_tSize);	//每行可以放多少字		
				var _nVer = parseInt(h1/(_tSize*1.5));  //行数
				
				var nPos = $.get_length(_str,_nHor*_nVer-6);
				var _textart = $(this).html();
				if(_textart != null && $.trim(_textart) != ''){
					$(this).html(_str.substring(0,nPos)+"...");
					$(this).css({"padding-top":(_box.height()-_tSize*1.5*_nVer)/2-2,"height":"auto"});
				}
            });
		}
		$.get_length = function(str,char_num){
			var char_length = 0;
			for (var i = 0; i < str.length; i++){
				var son_char = str.charAt(i);
				encodeURI(son_char).length > 2 ? char_length += 1 : char_length += 0.5;
				if(char_num && char_length>=char_num ){
					return i; 
				}
			}
			return char_length;
		};
		
		
	    $(function(){
			$.addStyle();
			var swiperParent = new Swiper('#swiperParent',{
				pagination: '.pagination',
				hashNav:true,
				
			});
			
			var _href = location.href;
			if(_href.indexOf("#")){
				var actId = _href.split("#")[1]
			}
			
			
			$.addStyle2();
			var swiperPicture= new Swiper('#swiperPicture',{
				pagination: '.pagination2'	
			});
			$(".pagination2").css({"left":"50%", "margin-left":-$(".pagination2").width()/2})
			$.getSomeText("limitCont1" ,.94,.94);
			$.getSomeText("limitCont2" ,.98,.96);
			
			$("#swiperPicture img").click(function(){
				var _text = '<img src="'+ $(this)[0].src +'" />';
				$("#blackFlr").html(_text);
				$("#blackFlr").show();
			})
			$("#blackFlr").click(function(){
				$(this).hide();
			})
			
		
			
		})
		//图片高度在加载完成后执行
		window.onload = function(){
			$(".dtable td").css({ "height": $(".dtable").parent().height()});
			$(".dtable td").first().css({ "width": $(".dtable td img").width()});
		}
		
		
	</script>
</head>
<body>
<div class="tblank"></div>
<div class="wrap-area" id="swiperParent">
	<div class="swiper-wrapper">

	<!--欢迎页 -->
	<#macro showDataWel collist artlist welColumn flag>
		<#if (welColumn?exists) && (welColumn.title?exists)>
		  <div  class="swiper-slide" data-hash="slide0">
			<table  border="0" cellspacing="0" cellpadding="0" class="scenter"><tr><td>${welColumn.title}</td></tr></table>
		  </div>
		</#if>
		<#if flag!=1>
   	  	  	<#if (collist?exists && collist?size > 0) || (artlist?exists && artlist?size > 0)>
   	  	  	<@showData collist=collist artlist=artlist />
   	  	  	</#if>
   	  	</#if>
	</#macro>
	
	<!--第一页 ${pageCount}-->

	<#macro showData collist artlist>
		<#list 1..1 as pageIndex>
			<#assign colStep=colPageSize*pageIndex_index />
			<#assign artStep=artPageSize*pageIndex_index /> 
			<div  class="swiper-slide" style="position:relative" data-hash="slide1">
			  <!--第1个（栏目） -->
			  <#if (collist[colStep]?exists)>
			  <div class="size1" onclick="location.href='${collist[colStep].link}?index=${pageIndex}'">
					  <dl>
						  <dt><table  border="0" cellspacing="0" cellpadding="0" class="dtable"><tr><td>
							<#if (collist[colStep].image?exists)>
								<#assign imgart2node=collist[colStep].image?split(";") />
								<#if (imgart2node[0]?exists) && imgart2node[0] != "">
									<img src="${imgart2node[0]}" /></td>
								</#if>
							</#if>
							<#if (collist[colStep].title?exists)>
								<td class="txt"><span>${collist[colStep].title}</span>
							</#if>
							</td></tr></table>
					      </dt>
					      <dd  id="limitCont1">
					      	<#if (collist[colStep].summary?exists)>
					      		<p>${collist[colStep].summary}</p>
					      	</#if></dd>
					  </dl>
			  </div>
			  </#if>
			  <p class="size-blank"></p>
			  <!--第2个（栏目） -->
			  <#if collist[colStep+1]?exists>
			  <div class="size2"  onclick="location.href='${collist[colStep+1].link}?index=${pageIndex}'">
			  	  <#if (collist[colStep+1]?exists)>
			  	  	  <#if (collist[colStep+1].title?exists)>
					    <p>${collist[colStep+1].title}</p>
					  </#if>
					  <#if (collist[colStep+1].summary?exists)>
					  <ul>
						  <li  id="limitCont2"><p>${collist[colStep+1].summary}</p></li> 
					  </ul>
					  </#if>
				  </#if>
			  </div>
			  </#if>
			  <p class="size-blank2"></p>
			  <!--第3个（栏目） -->
			  <#if collist[colStep+2]?exists>
			  <#if (collist[colStep+2].link?exists)>
			  	<div class="size3 color1"  onclick="location.href='${collist[colStep+2].link}?index=${pageIndex}'">
			  <#else>
			    <div class="size3 color1">
			  </#if>
			    <#if (collist[colStep+2]?exists)>
			      <#if (collist[colStep+2].title?exists)>
				    <div class="table"><p>${collist[colStep+2].title}</p></div>
				  </#if>
				</#if>
			  </div>
			  </#if>
			  <p class="size-blank3"></p>
			  <!--第4个（栏目） -->
			  <#if collist[colStep+3]?exists>
			  <#if (collist[colStep+3].link?exists)>
			  	<div class="size3 color2" onclick="location.href='${collist[colStep+3].link}?index=${pageIndex}'">
			  <#else>
			    <div class="size3 color2">
			  </#if>
			 
				<#if (collist[colStep+3]?exists)>
			      <#if (collist[colStep+3].title?exists)>
				    <div class="table"><p>${collist[colStep+3].title}</p></div>
				  </#if>
				</#if>
			  </div>
			  </#if>
			  <p class="size-blank"></p>
			  <!--第5个（文章） -->
			  <#if artlist[artStep]?exists>
			  <div class="size2">
			  	  <#if (artlist[artStep].image?exists) && artlist[artStep].image != "">
				  <div id="swiperPicture" class="swiper-container2">
					  <div class="swiper-wrapper">
						<#assign imgsnode=artlist[artStep].image?split(";") />
	            		<#list imgsnode as image>
		            		<#if (image?exists) && (image!='')>
		            		<div class="swiper-slide"><img src="${image}" / ></div>
		            		</#if>
	            		</#list>
					  </div>
				  </div>
				  </#if>
				  <div class="pagination2"></div>
			  </div>
			  </#if>
			  
			  <p class="size-blank2"></p>
			  <!--第6个（栏目） -->
			  <#if collist[colStep+4]?exists>
			  <#if (collist[colStep+4].link?exists)>
			  	<div class="size3 color3" onclick="location.href='${collist[colStep+4].link}?index=${pageIndex}'">
			  <#else>
			    <div class="size3 color3">
			  </#if>
			  
				 <#if (collist[colStep+4]?exists)>
			      <#if (collist[colStep+4].title?exists)>
				    <div class="table"><p>${collist[colStep+4].title}</p></div>
				  </#if>
				</#if>
			  </div>
			  </#if>
			  <p class="size-blank3"></p>
			  <!--第7个（栏目） -->
			  <#if collist[colStep+5]?exists>
			  <#if (collist[colStep+5].link?exists)>
			  	<div class="size3 color4" onclick="location.href='${collist[colStep+5].link}?index=${pageIndex}'">
			  <#else>
			    <div class="size3 color4">
			  </#if>
			  
				 <#if (collist[colStep+5]?exists)>
			      <#if (collist[colStep+5].title?exists)>
				    <div class="table"><p>${collist[colStep+5].title}</p></div>
				  </#if>
				</#if>
			  </div>
			  </#if>
			</div>  
		</#list>
	</#macro>
	  <#if (welColumn?exists)>
	  	<@showDataWel collist=collist artlist=artlist welColumn=welColumn flag=flag/>
	  <#else>
	 	 <@showData collist=collist artlist=artlist />
	  </#if>
	  
</div>		
	   
</div>

<div class="pagination"></div>
<div id="blackFlr"></div>
</body>
</html>