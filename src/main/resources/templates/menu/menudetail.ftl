<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width" />
    <title>菜单管理-明细页面</title>
    <#include "../common/meta.ftl">
</head>
<body>
<div>        
<script src="${ctx}/js/jquery/jQuery.md5.js"></script>
<div class="border" style="margin: 1px; height: 40px; line-height: 40px; padding-left: 5px; background: #FFFDCD;">    
    <span id="name" style="font-weight: bold; font-family: 'Roboto', sans-serif; color: #666;">${mn.name!''}</span>
</div>
<div class="bd" style="border-bottom: none; margin: 1px;">
    <div class="tipstitle_title settingtable bold bd todayInfoPanelTab rightPanelTitle_normal">
        <div class="tab_list_top" style="position: absolute">
            <div id="Tabbasic" class="tab_list bd actived" onclick="Tabchange('basic')">基本信息</div>            
        </div>
    </div>
</div>
<div class="ScrollBar" style="margin: 1px">
    <!--基本信息-->
    <div id="basic" class="tabPanel">
        <table id="tb_basic" class="form">
        	<tr>	                
                <th class="formTitle">菜单名称：</th>
                <td class="formValue">
                    <input id="name" name="name" type="text" value="${mn.name!'' }" class="txt" readOnly="true" />
                </td>                
            </tr>
            <tr>
                <th class="formTitle">父类ID：</th>
                <td class="formValue">
                    <input id="parentId" name="parentId" value="${mn.parentId !''}" type="text" class="txt" readOnly="true"/>
                </td>
            </tr>
            <tr>
                <th class="formTitle">创建用户Id：</th>
                <td class="formValue">
                    <input id="crtUserId" type="text" class="txt" value="${mn.crtUserId!''}" readOnly="true"/>
                </td>
            </tr>
            <tr>
            	<th class="formTitle">创建用户名：</th>
                <td class="formValue">
                    <input id="crtUserName" type="text" class="txt" value="${mn.id!''}" readOnly="true"/>
                </td>                
            </tr>
            <tr>
                <th class="formTitle">修改日期：</th>
                <td class="formValue">
                <#--<fmt:formatDate var="modifyDate" value="${mn.modifyDate}" pattern="yyyy-MM-dd hh:mm:ss"/>-->
                    <input id="modifyDate" type="text" class="txt" value="${(mn.modifyDate?string("yyyy-MM-dd hh:mm:ss"))!''}" readOnly="true"/>
                </td>
            </tr>                                    
            <tr>                
                <th class="formTitle">创建日期：</th>
                <td class="formValue">
                	<#--<fmt:formatDate var="crtDate" value="${mn.crtDate}" pattern="yyyy-MM-dd hh:mm:ss"/>-->
                    <input id="crtDate" type="text" class="txt" value="${(mn.crtDate?string("yyyy-MM-dd hh:mm:ss"))!''}" readOnly="true"/>
                </td>                
            </tr>            
            <tr>
                <th class="formTitle">状态：</th>
                <td class="formValue">
                	<#if mn.enabled == 0><input id="enabled" type="text" class="txt" value="不可用" readOnly="true"/></#if>
                	<#if mn.enabled == 1><input id="enabled" type="text" class="txt" value="可用" readOnly="true"/></#if>
                </td>
            </tr>
            <tr>
                <th class="formTitle">描述：</th>
                <td class="formValue">
                    <textarea id="description" name="description" maxlength="200" class="txtArea" rows="5">${mn.description!'' }</textarea>
                </td>	                
            </tr>
            <tr>
           		<th class="formTitle">修改用户名：</th>
                <td class="formValue">
                    <input id="modifyUserName" type="text" class="txt" value="${mn.id!''}" readOnly="true"/>
                </td>
            </tr>            
            <tr>
            	<th class="formTitle">修改用户Id：</th>
                <td class="formValue">
                    <input id="modifyUserId" type="text" class="txt" value="${mn.modifyUserId!''}" readOnly="true"/>
                </td>                
            </tr>
        </table>
    </div>
</div>
</div>
</body>
</html>
