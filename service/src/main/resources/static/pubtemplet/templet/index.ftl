<!doctype html>
<html>
<head>
    <meta charset="utf-8"/> 
    <title>阅知系统</title>
    <link rel="stylesheet" href="css/readcss.css">
    <script src="js/jquery-1.10.2.min.js"></script>
</head>
<body class="index">

<div class="header">
  <div class="version"><span>阅知${currentVersion}</span></div>
</div>

<div class="r2kwrap">
   <ul  class="download">
      <#if androidLarge??>
      <li  class="android">
         <a href="${androidLarge.path}"></a>	
      	 <div class="qrcode">
            <img src="${(androidLarge.qrcode)?default("")}" height="94" width="94"/>
            拍摄二维码下载
         </div>
         <div class="update">
            <span>版本号：</span>${androidLarge.version}
         </div>
      </li>
        </#if>
      	<#if iPad??>
      <li  class="apple">
      	 <a href="itms-services://?action=download-manifest&url=${iPad.path}" class="apple"></a>
      	 <div class="qrcode">
            <img src="${(iPad.qrcode)?default("")}" height="94" width="94"/>
            拍摄二维码下载
         </div>
         <div class="update">
            <span>版本号：</span>${iPad.version}
         </div>
      </li>
         </#if>
      	<#if iPhone??>
      <li class="iphone">
      	 <a href="itms-services://?action=download-manifest&url=${iPhone.path}"></a>
      	 <div class="qrcode">
            <img src="${(iPhone.qrcode)?default("")}" height="94" width="94"/>
            拍摄二维码下载
         </div>
         <div class="update">
            <span>版本号：</span>${iPhone.version}
         </div>
      </li>

         </#if>
      <li class="ebook">
      	 <a href="">微书苑</a>
      	 <div class="qrcode">
            <img src="images/icon_code.jpg" height="94" width="94"/>
            拍摄二维码关注
         </div>
         <div class="update">
            <span>版本号：</span>1.0.0
         </div>
      </li>
      <li  class="bigscreen">
         <a href="${asstUrl}"></a>	
      	 <div class="qrcode">
            <img src="images/asst.png" height="94" width="94"/>
            拍摄二维码下载
         </div>
         <div class="update">
            <span>更新时间：</span>2014.11.11 
         </div>
      </li>
   </ul>
   <script>
      $(".download").css("width",($(".download li").width()+1)*($(".download li").length))
   </script>
</div>

<div class="bottominfo">
	<a href="${r2kUrl}">后台地址</a>
	<a href="#">关于我们</a>
	<a href="#">联系我们</a>
</div>
<div class="footer">
版权所有 北京方正阿帕比技术有限公司
京ICP证10038239号 
</div>

</body>
</html>