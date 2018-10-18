<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes">
    <link rel="stylesheet" href="${templetPath}css/base.css">
    
</head>
<body>
<div class="header"> 
  <img src="${templetPath}images/logo.png" class="logo" / >
</div>
	
<div class="mainbody" id="indexPage">
    <div class="arrowp leftp" id="leftBtn"></div>
    <div class="carouselp" id="imgSwipe">
       
       <div class="showbox">
       	  <!-- colPageSize artPageSize是模板一页分别对应的个数 -->
       	  <#macro showData collist artlist>

       	  	<#list 1..pageCount as pageIndex>
       	  		<#assign colStep=(colPageSize-1)*pageIndex_index />
       	  		<#assign artStep=(artPageSize-1)*pageIndex_index /> 
       	  	  <ul>
       	  		<li class="blocks1">
	               <!--第1个栏目-->
	               <section>
	               	 <#if collist[(colStep + pageIndex_index)]?exists>
		            	 <img src="${templetPath}images/demo_pic1.jpg" class="img1"/>
		                 <span class="bigtext">${collist[(colStep + pageIndex_index)].title?if_exists}</span>
	               	 </#if>
	               </section>
	               <section>
	               	 <!--第2个栏目-->
	               	 <#if collist[(colStep + pageIndex_index+1)]?exists>
		                 <div class="littlepic">
			            	 <img src="${templetPath}images/demo_pic2.jpg" />
			            	 <span>${collist[(colStep + pageIndex_index+1)].title?if_exists}</span> 
		                 </div>
	               	 </#if>
	                 <!--第3个栏目-->
	                 <#if collist[(colStep + pageIndex_index+2)]?exists>
		                 <div class="littlepic">
			            	 <img src="${templetPath}images/demo_pic3.jpg" />
			            	 <span>${collist[(colStep + pageIndex_index+2)].title?if_exists}</span>
		                 </div>
	               	 </#if>
	               </section>
	            </li>
	            <li class="blocks2">
	            <!--第1个文章-->
	              <article>
	                <#if artlist[(artStep + pageIndex_index)]?exists>
	                	<p>${artlist[(artStep + pageIndex_index)].title?if_exists}</p>
	                	<p>${artlist[(artStep + pageIndex_index)].summary?if_exists}</p>
	               	 </#if>
	              </article>
	            </li>
	            <li class="blocks3">
	            <!--第2个文章-->
	            	<#if artlist[(artStep + pageIndex_index+1)]?exists>
	                	<p>${artlist[(artStep + pageIndex_index+1)].title?if_exists}
	                	${artlist[(artStep + pageIndex_index+1)].summary?if_exists}</p>
		                <img src="${templetPath}images/demo_pic4.jpg" / >
	               	 </#if>
	            </li>
	            <li class="blocks4">
	            <!--第4个栏目-->
	            <span>
					<#if collist[(colStep + pageIndex_index+3)]?exists>
	                	${collist[(colStep + pageIndex_index+3)].title?if_exists}
	               	 </#if>
				</span>
				</li>
	            <li class="blocks5" id="picArea">
	            <!--第3个文章-->
	           		 <#if artlist[(artStep + pageIndex_index+2)]?exists>
	                	<div>
		                	<img src="${templetPath}images/demo_pic5.jpg" / >
		                	<img src="${templetPath}images/demo_pic1.jpg" / >
	                	</div>
	               	 </#if>
	            </li>
	          </ul>
       	  	</#list>
       	  </#macro>
       	  <@showData collist=collist artlist=artlist />
       	  
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
</body>
</html>