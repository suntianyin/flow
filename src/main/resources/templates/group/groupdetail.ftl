<!DOCTYPE html>
<html>
<head>    
    <title>用户组管理-明细页面</title>
    <#include "../common/meta.ftl">
</head>
<body>
<div>        
<script src="${ctx}/js/jquery/jQuery.md5.js"></script>
<div class="border" style="margin: 1px; height: 40px; line-height: 40px; padding-left: 5px; background: #FFFDCD;">    
    <span id="name" style="font-weight: bold; font-family: 'Roboto', sans-serif; color: #666;">${ag.name}</span>
</div>
<div class="bd" style="border-bottom: none; margin: 1px;">
    <div class="tipstitle_title settingtable bold bd todayInfoPanelTab rightPanelTitle_normal">
        <div class="tab_list_top" style="position: absolute">
            <div id="Tabbasic" class="tab_list bd actived" onclick="Tabchange('basic')">基本信息</div>
            <div id="TabPermissionTree" class="tab_list bd " onclick="Tabchange('PermissionTree')">拥有权限</div>
        </div>
    </div>
</div>
<div class="ScrollBar" style="margin: 1px">
    <!--基本信息-->
    <div id="basic" class="tabPanel">
        <table id="tb_basic" class="form">
            <tr>
                <th class="formTitle">用户组Id：</th>
                <td class="formValue">
                    <input id="id" type="text" class="txt" value="${ag.id!''}" readOnly="true"/>
                </td>  
                <th class="formTitle">用户组名称：</th>
                <td class="formValue">
                    <input id="name" type="text" class="txt" value="${ag.name!''}" readOnly="true"/>
                </td>              
            </tr>            
            <tr>                
                <th class="formTitle">机构ID：</th>
                <td class="formValue">
                    <input id="OrgId" type="text" class="txt" value="${ag.orgId!''}" readOnly="true"/>
                 </td>
                 <th class="formTitle">机构名称：</th>
                 <td class="formValue">
                    <input id="OrgName" type="text" class="txt" value="${orgname!''}" readOnly="true"/>
                 </td>
            </tr>            
            <tr>
                <th class="formTitle">父类Id：</th>
                <td class="formValue">
                    <input id="parentId" type="text" class="txt" value="${ag.parentId!''}" readOnly="true"/>
                </td>
                <th class="formTitle">状态：</th>
                <td class="formValue">
                	<#if ag.enabled == 0><input id="Enabled" type="text" class="txt" value="不可用" readOnly="true"/></#if>
                	<#if ag.enabled == 1><input id="Enabled" type="text" class="txt" value="可用" readOnly="true"/></#if>
                </td>                
            </tr>            
            <tr>                
                <th class="formTitle">创建日期：</th>
                <td class="formValue">
                    <input id="CrtDate" type="text" class="txt" value="${(ag.crtDate?string("yyyy-MM-dd hh:mm:ss"))!''}" readOnly="true"/>
                </td>
                <th class="formTitle">修改日期：</th>
                <td class="formValue">
                    <input id="ModifyDate" type="text" class="txt" value="${(ag.modifyDate?string("yyyy-MM-dd hh:mm:ss"))!''}" readOnly="true"/>
                </td>                
            </tr>            
            <tr>
            	<th class="formTitle">创建用户名：</th>
                <td class="formValue">
                    <input id="CrtUserName" type="text" class="txt" value="${ag.id!''}" readOnly="true"/>
                </td>
                <th class="formTitle">创建用户Id：</th>
                <td class="formValue">
                    <input id="CrtUserId" type="text" class="txt" value="${ag.crtUserId!''}" readOnly="true"/>
                </td>
            </tr>            
            <tr>
            	<th class="formTitle">修改用户Id：</th>
                <td class="formValue">
                    <input id="ModifyUserName" type="text" class="txt" value="${ag.modifyUserId!''}" readOnly="true"/>
                </td>
                <th class="formTitle">修改用户名：</th>
                <td class="formValue">
                    <input id="ModifyUserName" type="text" class="txt" value="${ag.id!''}" readOnly="true"/>
                </td>
            </tr
	        <tr>                
                <th class="formTitle">描述：</th>
                <td class="formValue">
                	<textarea id="description" name="description" maxlength="200" class="txtArea" rows="5">${ag.description!''}</textarea>
                </td>
            </tr>
        </table>
    </div>
    <!--拥有权限-->
    <div id="PermissionTree" class="tabPanel" style="display: none;">
        <div class="border" style="float: left; width: 260px; margin-right: 1px; height: 318px;">
            <div class="btnbartitle">
                <div>按钮权限</div>
            </div>
            <div id="ModulePermission" style="height: 290px; overflow: auto">
            	<div class="ScrollBar" id="RoleList" style="height: 275px;">
        			<ul class="sys_spec_text">
                        <#list list as list>
        				<#if list.type == 0 >
        				<li title="${list.url!''}" class="selected"><a id="${list.id!'' }"><img src="${ctx}/images/role.png">${list.name !''}</a><i></i></li>
        				</#if>
        				</#list>
        			</ul>
    			</div>
            </div>
        </div>
        <div class="border" style="float: left; width: 260px; margin-right: 1px; height: 318px;">
            <div class="btnbartitle">
                <div>模块权限</div>
            </div>
            <div id="ButtonePermission" style="height: 290px; overflow: auto">
            	<div class="ScrollBar" id="RoleList" style="height: 275px;">
        			<ul class="sys_spec_text">
                        <#list list as list>
                            <#if list.type == 1 >
        				<li title="${list.url!'' }" class="selected"><a id="${list.id !''}"><img src="${ctx}/images/role.png">${list.name !''}</a><i></i></li>
        				</#if>
        				</#list>
        			</ul>
    			</div>
            </div>
        </div>
        <div class="border" style="float: left; width: 260px; height: 318px;">
            <div class="btnbartitle">
                <div>接口权限</div>
            </div>
            <div id="ViewPermission" style="height: 290px; overflow: auto">
            	<div class="ScrollBar" id="RoleList" style="height: 275px;">
        			<ul class="sys_spec_text">
                        <#list list as list>
                            <#if list.type == 2>
        				<li title="${list.url!'' }" class="selected"><a id="${list.id !''}"><img src="${ctx}/images/role.png">${list.name !''}</a><i></i></li>
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
