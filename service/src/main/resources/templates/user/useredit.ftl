<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width" />
     <#include "../common/meta.ftl">
    <title>用户管理表单页面</title>
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
                    url: "userupdate",
                    type: "POST",
                    data: postData,
                    async: false,
                    success: function (data) {
                        if(data == "success"){
                            Loading(false);
                            tipDialog("更新成功！", 3, 1);
                            top.frames[tabiframeId()].location.reload();
                            //top.frames[tabiframeId()].windowload();
                            closeDialog();
                        }else{
                            Loading(false);
                            alertDialog("只有创建用户才有修改权限！", -1);
                        }
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
                    <input id="id" name="id" class="txt" value="${au.id!''}" type="hidden"/>
                    <tr>
                        <th class="formTitle">用户名：</th>
                        <td class="formValue">
                            <input id="userName" name="userName" type="text" value="${au.userName!''}" class="txt required" datacol="yes" err="用户名" checkexpession="NotNull" readOnly="true"/>
                        </td>
                        <th class="formTitle">真实姓名：</th>
                        <td class="formValue">
                            <input id="realName" name="realName" value="${au.realName!''}" type="text" class="txt" />
                        </td>
                    </tr>
                    <tr>
                        <th class="formTitle">性别：</th>
                        <td class="formValue">
                            <select id="gender" name="gender" class="txtselect">
                                <#if au.gender == 1>
                                    <option value="1" selected ="selected">男</option>
                                    <option value="2">女</option>
                                </#if>
                                <#if au.gender == 2>
                                    <option value="1">男</option>
                                    <option value="2" selected ="selected">女</option>
                                </#if>
                            </select>
                        </td>
                        <th class="formTitle">机构ID：</th>
                        <td class="formValue">
                            <select id="orgId" name="orgId" class="txtselect">
                                <#--<c:forEach items="${list }" var="list">-->
                                    <#list list as list>
                                    <option theValue=(au.orgId == list.id )?String('selected ="selected"':'')<#--${au.orgId == list.id ?String('selected ="selected"':'')--> } value="${list.id?c}">${list.orgName }</option>
                                    </#list>
                                <#--</c:forEach>-->
                            </select>
                            <#--<input id="orgId" name="orgId" type="text" value="${au.orgId!''}" class="txt" datacol="yes" err="机构" />-->
                        </td>
                    </tr>
                    <tr>
                        <th class="formTitle">密码：</th>
                        <td class="formValue">
                            <input id="password" name="password" type="password" class="txt required" value="${au.password!''}" datacol="yes" err="密码" checkexpession="NotNull" readOnly="true" disabled/>
                        </td>
                        <th class="formTitle">出生日期：</th>
                        <td class="formValue">
                            <#--<fmt:formatDate var="birthdate" value="${au.birthdate!''}" pattern="yyyy-MM-dd"/>-->
                            <input id="birthdate" name="birthdate" type="text" class="txt Wdate" value="${(au.birthdate?string("yyyy-MM-dd"))!''}" onfocus="WdatePicker({maxDate:'%y-%M-%d'})" />
                        </td>
                    </tr>
                    <tr>
                        <th class="formTitle">电话：</th>
                        <td class="formValue">
                            <input id="telephone" name="telephone" type="text" value="${au.telephone!''}" class="txt" datacol="yes" err="手机" checkexpession="MobileOrNull" />
                        </td>
                        <th class="formTitle">E-mail：</th>
                        <td class="formValue">
                            <input id="email" readonly="readonly" name="email" type="text" value="${au.email!''}" class="txt" datacol="yes" err="E-mail" checkexpession="EmailOrNull"/>
                        </td>
                    </tr>
                    <tr>
                        <th class="formTitle">创建日期：</th>
                        <td class="formValue">
                            <input id="crtDate" name="crtDate" type="text" value="${(au.crtDate?string("yyyy-MM-dd HH:mm:ss"))!''}" class="txt Wdate" readOnly="true" disabled/>
                        </td>
                        <th class="formTitle">状态：</th>
                        <td class="formValue">
                            <select id="enabled" name="enabled" class="txtselect">
                                <#if au.enabled == 0>
                                    <option value="0" selected ="selected">不可用</option>
                                    <option value="1">可用</option>
                                </#if>
                                <#if au.enabled == 1>
                                    <option value="0">不可用</option>
                                    <option value="1" selected ="selected">可用</option>
                                </#if>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th class="formTitle">创建用户Id：</th>
                        <td class="formValue">
                            <input id="crtUserId" name="crtUserId" type="text" value="${au.crtUserId!''}" class="txt" readOnly="true"/>
                        </td>
                        <th class="formTitle">创建用户名：</th>
                        <td class="formValue">
                            <input id="crtUserName" name="crtUserName" type="text" value="${crtUserName!''}" class="txt" readOnly="true"/>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </form>
</div>
</body>
</html>
