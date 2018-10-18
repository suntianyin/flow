<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>角色管理</title>
    <#include "../common/meta.ftl">
</head>
<body>
<div>

    <script type="text/javascript">
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
                url: "roledata?keywords=" + keywords, page: 1
            }).trigger('reloadGrid');
        }
        //加载表格
        function GetGrid() {
            var SelectRowIndx;
            $("#gridTable").jqGrid({
                url: "roledata",
                datatype: "json",
                height: $(window).height() - 178,
                autowidth: true,
                colModel: [
                    { label: '主键', name: 'id', index: 'id', width: 80, hidden: true},
                    { label: '编码', name: 'roleCode', index: 'role_code', width: 80, hidden:true },
                    { label: '角色', name: 'name', index: 'name', width: 100 },
                    { label: '创建人', name: 'crtUserId', index: 'crt_user_id', width: 120, align: 'left', hidden: true},
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
                    { label: '描述', name: 'description', index: 'description', width: 200 },
                    {
                        label: '有效', name: 'enabled', index: 'enabled', width: 45, align: 'center',
                        formatter: function (cellvalue, options, rowObject) {
                            if (cellvalue == '1') return "<img src='${ctx }/images/checkokmark.gif'/>";
                            if (cellvalue == '0') return "<img src='${ctx }/images/checknomark.gif'/>";
                        }
                    }
                ],
                viewrecords: true,
                rowNum: 25,
                rowList: [5, 10, 20, 30],
                pager: "#gridPager",
                sortname: 'role_code',
                sortorder: 'desc',
                rownumbers: true,
                shrinkToFit: false,
                gridview: true,
                multiselect: true,
                onSelectRow: function () {
                    SelectRowIndx = GetJqGridRowIndx("#" + this.id);
                }
            });
            columnModelData("role","#gridTable");
            //自应高度
            $(window).resize(function () {
                $("#gridTable").setGridHeight($(window).height() - 178);
            });
        }
        //新增
        function btn_add() {
            var url = "/admin/auth/authrole/roleadd";
            openDialog(url, "Form", "新增角色", 500, 300, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }
        //编辑
        function btn_edit() {
            var KeyValue = GetJqGridRowValue("#gridTable", "id");
            if (IsChecked(KeyValue)) {
                var url = "/admin/auth/authrole/roleedit?roleid="+KeyValue;
                openDialog(url, "Form", "编辑角色", 500, 300, function (iframe) {
                    top.frames[iframe].AcceptClick();
                });
            }
        }
        //明细
        function btn_detail() {
            var KeyValue = GetJqGridRowValue("#gridTable", "id");
            if (IsChecked(KeyValue)) {
                var url = "/admin/auth/authrole/roledetail?roleid="+KeyValue;
                Dialog(url, "Detail", "角色明细", 800, 410);
            }
        }
        //删除
        function btn_delete() {
            var KeyValue = GetJqGridRowValue("#gridTable", "id");
            if (IsDelData(KeyValue)) {
                var delparm = 'KeyValue=' + KeyValue;
                var url = "/admin/auth/authrole/roledelete";
                delConfig(url, delparm, KeyValue.split(",").length);
            }
        }
        //角色配置测试
        function btn_userrole() {
            var KeyValue = GetJqGridRowValue("#gridTable", "id");
            var Name = GetJqGridRowValue("#gridTable", "name");
            if (IsChecked(KeyValue)) {
                var url = "/admin/auth/authrole/rolerestest?roleid="+KeyValue;
                openDialog(url, "AllotPermission", "角色授权 - " + Name, 830, 500, function (iframe) {
                    top.frames[iframe].AcceptClick();
                });
            }
        }
        //角色配置权限
        function btn_AllotPermission() {
            var KeyValue = GetJqGridRowValue("#gridTable", "id");
            var Name = GetJqGridRowValue("#gridTable", "name");
            if (IsChecked(KeyValue)) {
                var url = "/admin/auth/authrole/roleres?roleid="+KeyValue;
                openDialog(url, "AllotPermission", "角色授权 - " + Name, 630, 300, function (iframe) {
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
                    角色列表 <span id="CenterTitle"></span>
                </div>
            </div>
            <!--工具栏-->
            <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
                <div class="PartialButton">
                    <a id="lr-replace" title="刷新当前(Ctrl+Q)" onclick="Replace()" class="tools_btn"><span><i class="fa fa-refresh"></i>&nbsp刷新</span></a>
                    <div class="tools_separator"></div>

                        <a id="lr-add" title="新增" onclick="btn_add()" class="tools_btn"><span><i class="fa fa-plus"></i>&nbsp新增</span></a>


                        <a id="lr-edit" title="编辑" onclick="btn_edit()" class="tools_btn"><span><i class="fa fa-pencil-square-o"></i>&nbsp编辑</span></a>


                        <a id="lr-delete" title="删除" onclick="btn_delete()" class="tools_btn"><span><i class="fa fa-trash-o"></i>&nbsp删除</span></a>


                        <a id="lr-detail" title="详细信息" onclick="btn_detail()" class="tools_btn"><span><i class="fa fa-reorder"></i>&nbsp详细</span></a>

                    <div class="tools_separator"></div>
                    <a id="lr-Authentication" class="tools_btn dropdown">
                        <div style="float: left; position: relative;" onclick="showdiv()"; >
                            <div class="text" style="float:left"><i class="fa fa-gavel"></i>&nbsp权限设置</div>
                            <div class="dropdown-icon"><img src="${ctx }/images/dropdown-icon.png"></div>
                            <div class="dropdown-data dropdown-data-none" style="top: 68px;">
                                <ul>

                                        <li id="lr-UserRole" onclick="btn_userrole()">角色配置测试</li>

                                        <li id="lr-UserPermission" onclick="btn_AllotPermission()">角色配置权限</li>

                                </ul>
                            </div>
                        </div>
                    </a>
                    <div class="tools_separator"></div>
                    <a id="lr-leave" ids="" title="关闭当前窗口" onclick="btn_back()" class="tools_btn"><span><i class="fa fa-arrow-left"></i>&nbsp离开</span></a>
                </div>
            </div>
            <!--列表-->
            <div id="grid_List">
                <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
                    <table border="0" class="form-find" style="height: 45px;">
                        <tr>
                            <th>关键字：</th>
                            <td><input id="records" name="id" class="txt" value="${records!''}" type="hidden"/></td>
                            <td><input id="total" name="id" class="txt" value="${total!''}" type="hidden"/></td>
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
