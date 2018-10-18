<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width" />
    <title>资源管理-资源类型页面</title>
    <#include "../common/meta.ftl">
</head>
<body>
<div>        
<script src="${ctx}/resources/js/jquery/jQuery.md5.js"></script>
<div class="bd" style="border-bottom: none; margin: 1px;">
    <div class="tipstitle_title settingtable bold bd todayInfoPanelTab rightPanelTitle_normal">
        <div class="tab_list_top" style="position: absolute">            
            <div id="TabPermissionTree" class="tab_list bd actived" onclick="Tabchange('PermissionTree')">拥有权限</div>
        </div>
    </div>
</div>
<div class="ScrollBar" style="margin: 1px">    
    <!--拥有权限-->
    <div id="PermissionTree" class="tabPanel">
        <div class="border" style="float: left; width: 260px; margin-right: 1px; height: 418px;">
            <div class="btnbartitle">
                <div>模块权限</div>
            </div>
            <div id="ModulePermission" style="height: 390px; overflow: auto">
            	<div class="ScrollBar" id="RoleList" style="height: 375px;">
        			<ul class="sys_spec_text">
        				<#list list as list>
        				<#if list.type == 0>
        					<li title="${list.url!'' }" class="selected"><a id="${list.id!'' }"><img src="${ctx}/images/role.png">${list.name!'' }</a><i></i></li>
        				</#if>
        				</#list>
        			</ul>
    			</div>
            </div>
        </div>
        <div class="border" style="float: left; width: 260px; margin-right: 1px; height: 418px;">
            <div class="btnbartitle">
                <div>按钮权限</div>
            </div>
            <div id="ButtonePermission" style="height: 390px; overflow: auto">
            	<div class="ScrollBar" id="RoleList" style="height: 375px;">
        			<ul class="sys_spec_text">
                        <#list list as list>
                            <#if list.type == 1>
        					<li title="${list.url!'' }" class="selected"><a id="${list.id!'' }"><img src="${ctx}/images/role.png">${list.name!'' }</a><i></i></li>
        				</#if>
        				</#list>
        			</ul>
    			</div>
            </div>
        </div>
        <div class="border" style="float: left; width: 260px; height: 418px;">
            <div class="btnbartitle">
                <div>视图权限</div>
            </div>
            <div id="ViewPermission" style="height: 390px; overflow: auto">
            	<div class="ScrollBar" id="RoleList" style="height: 375px;">
        			<ul class="sys_spec_text">
                        <#list list as list>
                            <#if list.type == 2>
        					<li title="${list.url!'' }" class="selected"><a id="${list.id !''}"><img src="${ctx}/images/role.png">${list.name!'' }</a><i></i></li>
        				</#if>
        				</#list>
        			</ul>
    			</div>
            </div>
        </div>
    </div>
</div>
</div>
</body>
</html>
