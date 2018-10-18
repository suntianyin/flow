<!DOCTYPE html>
<html>
<head>    
	<meta name="viewport" content="width=device-width" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<#include "../common/meta.ftl">
    <title>用户组管理</title>    
</head>
<body>
<div>        
<script type="text/javascript">
	//弹出选择按钮
	function showdiv(){
		var dropdown = $(".dropdown-data");
		if(dropdown.hasClass("dropdown-data-none")){
			dropdown.removeClass("dropdown-data-none");
		}
		else{
			dropdown.addClass("dropdown-data-none");
		}
	}
    $(document).ready(function () {
        GetGrid();
        //绑定键盘按下事件  
        $(document).keypress(function (e) {
            //回车键事件  
            if (e.which == 13) {
                $("#keywords").focus();
                $("#keywords").select();
                $("#btnSearch").click();
            }
        });
    });
    //搜索
    function btn_Search() {
    	var keywords = $("#keywords").val();
        $("#gridTable").jqGrid('setGridParam', {
            url: "groupdata?keywords=" + keywords, page: 1
        }).trigger('reloadGrid');
    }
    //加载表格
    function GetGrid() {
    	var SelectRowIndx;
        $("#gridTable").jqGrid({
            url: "groupdata",
            datatype: "json",
            height: $(window).height() - 178,
            autowidth: true,
            colModel: [
                { label: "主键", name: "id", index: "id", hidden: true },                
                { label: "用户组名称", name: "name", index: "name", width: 150 },
                /* { label: "父类Id", name: "parentId", index: "parent_id", width: 150 }, */
                /* { label: "所属机构Id", name: "orgId", index: "org_id"}, */
                {
                    label: '有效', name: 'enabled', index: 'enabled', width: 45, align: 'center',
                    formatter: function (cellvalue, options, rowObject) {
                        if (cellvalue == '1') return "<img src='${ctx }/images/checkokmark.gif'/>";
                        if (cellvalue == '0') return "<img src='${ctx }/images/checknomark.gif'/>";
                    }
                },
                {
                    label: '创建日期', name: 'crtDate', index: 'crt_date', width: 150, align: 'left',
                    formatter: function (cellvalue, options, rowObject) {
                        return formatDate(cellvalue, 'yyyy-MM-dd hh:mm:ss');
                    }
                },
                {
                    label: '修改日期', name: 'modifyDate', index: 'modify_date', width: 150, align: 'left',
                    formatter: function (cellvalue, options, rowObject) {
                        return formatDate(cellvalue, 'yyyy-MM-dd hh:mm:ss');
                    }
                },
                { label: '描述', name: 'description', index: 'description', width: 200 }
            ],
            viewrecords: true,
            rowNum: 25,
            rowList: [5, 10, 20, 30],
            pager: "#gridPager",
            sortname: 'name',
            sortorder: 'desc',
            rownumbers: true,
            shrinkToFit: false,
            gridview: true,
            multiselect: true,
            onSelectRow: function () {
                SelectRowIndx = GetJqGridRowIndx("#" + this.id);
            }
        });               
		columnModelData("group","#gridTable");
        //自应高度
        $(window).resize(function () {
            $("#gridTable").setGridHeight($(window).height() - 132);
        });
    }
    //新增
    function btn_add() {
        var url = "/admin/auth/authgroup/groupadd";
        openDialog(url, "Form", "新增用户组", 400, 200, function (iframe) {
            top.frames[iframe].AcceptClick()
        });
    }
    //编辑
    function btn_edit() {
        var KeyValue = GetJqGridRowValue("#gridTable", "id");
        if (IsChecked(KeyValue)) {
            var url = "/admin/auth/authgroup/groupedit?groupid="+KeyValue;
            openDialog(url, "Form", "编辑用户组", 450, 300, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }
    }
    //明细
    function btn_detail() {
        var KeyValue = GetJqGridRowValue("#gridTable", "id");        
        if (IsChecked(KeyValue)) {
            var url = "/admin/auth/authgroup/groupdetail?groupid="+KeyValue;
            Dialog(url, "Detail", "用户组明细", 800, 410);
        }
    }
    //删除
    function btn_delete() {
        var KeyValue = GetJqGridRowValue("#gridTable", "id");
        if (IsDelData(KeyValue)) {
            var delparm = 'KeyValue=' + KeyValue;
            var url = "/admin/auth/authgroup/groupdelete";
            delConfig(url, delparm, KeyValue.split(",").length);
        }
    }
    //用户组角色配置
    function btn_grouprole() {
        var KeyValue = GetJqGridRowValue("#gridTable", "id");
        var Name = GetJqGridRowValue("#gridTable", "name");
        if (IsChecked(KeyValue)) {            
            var url = "/admin/auth/authgroup/grouprole?groupid="+KeyValue;
            openDialog(url, "AllRole", "用户组角色 - " + Name, 630, 300, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }
    }
  	//用户组用户配置
    function btn_AllUser() {
        var KeyValue = GetJqGridRowValue("#gridTable", "id");
        var Name = GetJqGridRowValue("#gridTable", "name");
        if (IsChecked(KeyValue)) {            
            var url = "/admin/auth/authgroup/groupuser?groupid="+KeyValue;
            openDialog(url, "AllMember", "用户组用户 - " + Name, 630, 300, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }
    }
    //刷新
    function windowload() {
        $("#gridTable").trigger("reloadGrid"); //重新载入 
    }  
</script>
<div id="layout" class="layout">
    <!--中间-->
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>
               	 用户组列表 <span id="CenterTitle"></span>
            </div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
				<a id="lr-replace" title="刷新当前" onclick="Replace()" class="tools_btn"><span><i class="fa fa-refresh"></i>&nbsp刷新</span></a>
	            <div class="tools_separator"></div>

	            <a id="lr-add" title="新增" onclick="btn_add()" class="tools_btn"><span><i class="fa fa-plus"></i>&nbsp新增</span></a>   


	            <a id="lr-edit" title="编辑" onclick="btn_edit()" class="tools_btn"><span><i class="fa fa-pencil-square-o"></i>&nbsp编辑</span></a>

	            <a id="lr-delete" title="删除" onclick="btn_delete()" class="tools_btn"><span><i class="fa fa-trash-o"></i>&nbsp删除</span></a>


	            <a id="lr-detail" title="详细信息" onclick="btn_detail()" class="tools_btn"><span><i class="fa fa-reorder"></i>&nbsp详细</span></a>

	            <div class="tools_separator"></div>
	            <a id="lr-Authentication" class="tools_btn dropdown">
	            	<div style="float: left; position: relative;" onclick="showdiv()">			            
			            <div class="text" style="float:left"><i class="fa fa-gavel"></i>&nbsp权限设置</div>
			            <div class="dropdown-icon"><img src="${ctx }/images/dropdown-icon.png"></div>
			            <div class="dropdown-data dropdown-data-none" style="top: 68px;">
			                <ul>
			                	<#--<#if test='${id.contains("authgroup/grouprole") }'>-->
				                <li id="lr-GroupRole" onclick="btn_grouprole()">用户组角色</li>			                
				                <#--</#if>-->
				                <#--<#if test='${id.contains("authgroup/groupuser") }'>-->
				                <li id="lr-GroupUser" onclick="btn_AllUser()">用户组用户</li>
				                <#--</#if>-->
			                </ul>
		            	</div>
		            </div>		            
	            </a>
	            <div class="tools_separator"></div>
            	<a id="lr-leave" title="关闭当前窗口" onclick="btn_back()" class="tools_btn"><span><i class="fa fa-arrow-left"></i>&nbsp离开</span></a>
            </div>
        </div>
        <!--列表-->
        <div id="grid_List">
            <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
                <table border="0" class="form-find" style="height: 45px;">
                    <tr>
                        <th>关键字：</th>
                        <td><input id="records" name="id" class="txt" value="${records !''}" type="hidden"/></td>
                        <td><input id="total" name="id" class="txt" value="${total !''}" type="hidden"/></td>
                        <td><input id="keywords" type="text" class="txt" style="width: 200px" /></td>
                        <td><input id="btnSearch" type="button" class="btnSearch" value="搜 索" onclick="btn_Search()" /></td>
                    </tr>
                </table>
            </div>
            <table id="gridTable"></table>
            <div id="gridPager"></div>
        </div>
        <!--视图-->
        <div id="grid_View" class="ScrollBar"></div>
    </div>
</div>
</div>
</body>
</html>
