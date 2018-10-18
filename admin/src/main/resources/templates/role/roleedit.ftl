<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width" />
    <#include "../common/meta.ftl">
    <title>角色管理表单页面</title>
</head>
<body>
<div>
    <script type="text/javascript">
        //保存事件
        function AcceptClick() {
            if (!CheckDataValid('#form1')) {
                return false;
            }
            Loading(true, "正在提交数据...");
            window.setTimeout(function () {
                var formObj = $("#form1");
                var postData = formObj.serializeJson();

                $.ajax({
                    url: "roleupdate",
                    type: "POST",
                    data: postData,
                    async: false,

                    success: function (data) {
                        Loading(false);
                        tipDialog("更新角色成功！", 3, 1);
                        top.frames[tabiframeId()].location.reload();
                        closeDialog();
                    },
                    error: function (data) {
                        Loading(false);
                        alertDialog(data.responseText, -1);
                    }
                });

            }, 200);
        }
    </script>
    <form id="form1">
        <div id="message" style="display: none; padding: 1px; padding-bottom: 0px;"></div>
        <div class="bd" style="border-bottom: none; margin: 1px;">
            <div class="tipstitle_title settingtable bold bd todayInfoPanelTab rightPanelTitle_normal">
                <div class="tab_list_top" style="position: absolute">
                    <div id="Tabbasic" class="tab_list bd actived">基本信息</div>
                </div>
            </div>
        </div>
        <div class="ScrollBar" style="margin: 1px; overflow: hidden;">
            <!--基本信息-->
            <div id="basic" class="tabPanel">
                <table class="form">
                    <input id="id" name="id" class="txt" value="${ar.id !''}" type="hidden"/>
                    <tr>
                        <th class="formTitle">角色名称：</th>
                        <td class="formValue">
                            <input id="name" type="text" name="name" value="${ar.name !''}" class="txt required" datacol="yes" err="角色名称" checkexpession="NotNull" />
                        </td>
                    </tr>
                    <tr>
                        <th class="formTitle">角色编码：</th>
                        <td class="formValue">
                            <input id="roleCode" type="text" name="roleCode" value="${ar.roleCode !''}" class="txt required" datacol="yes" err="角色编码" checkexpession="isRoleCodeAndNotNull" readOnly="true" />
                        </td>
                    </tr>
                    <tr>
                        <th class="formTitle">创建用户Id：</th>
                        <td class="formValue">
                            <input id="crtUserId" name="crtUserId" type="text" value="${ar.crtUserId !''}" class="txt" />
                        </td>
                    </tr>
                    <tr>
                        <th class="formTitle">状态：</th>
                        <td class="formValue">
                            <select id="enabled" name="enabled" class="txtselect">
                                <#if ar.enabled == 0>
                                    <option value="0" selected ="selected">不可用</option>
                                    <option value="1">可用</option>
                                </#if>
                                <#if ar.enabled == 1>
                                    <option value="0">不可用</option>
                                    <option value="1" selected ="selected">可用</option>
                                </#if>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th class="formTitle">机构ID：</th>
                        <td class="formValue">
                            <select id="orgId" name="orgId" class="txtselect">
                                <#list list as list>
                                    <option theValue=(ar.orgId == list.id )?String('selected ="selected"':'')<#--${ar.orgId == list.id ?'selected ="selected"':'' }--> value="${list.id?c }">${list.orgName }</option>
                                </#list>
                            </select>
                                <#--<input id="orgId" name="orgId" type="text" value="${ar.orgId!''}" class="txt" datacol="yes" err="机构" />-->
                        </td>
                    </tr>
                    <tr>
                        <th class="formTitle">创建日期：</th>
                        <td class="formValue">
                            <#--<fmt:formatDate var="crtDate" value="${ar.crtDate!''}" pattern="yyyy-MM-dd"/>-->
                            <input id="crtDate" name="crtDate" type="text" value="${(ar.crtDate?string("yyyy-MM-dd"))!''}" class="txt Wdate" readOnly="true"/>
                        </td>
                    </tr>
                    <tr>
                        <th class="formTitle">描述：</th>
                        <td class="formValue">
                            <textarea id="description" name="description" maxlength="200" class="txtArea" rows="5">${ar.description !''}</textarea>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </form>
</div>
</body>
</html>
