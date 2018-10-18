<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/meta.ftl">
    <title>用户管理</title>
</head>
<body>
<div>
    <script type="text/javascript">
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
                url: "userdata?keywords=" + keywords, page: 1
            }).trigger('reloadGrid');
        }

        //加载表格
        function GetGrid() {
            var SelectRowIndx;
            $("#gridTable").jqGrid({
                url: "userdata",
                datatype: "json",
                height: $(window).height() - 178,
                autowidth: true,
                colModel: [
                    {label: '主键', name: 'id', index: 'id', width: 80, align: 'left', hidden: true},
                    {label: '用户名', name: 'userName', index: 'user_name', width: 100, align: 'left'},
                    {label: '姓名', name: 'realName', index: 'real_name', width: 100, align: 'left'},
                    {
                        label: '性别', name: 'gender', index: 'gender', width: 60, align: 'center',
                        formatter: function (cellvalue, options, rowObject) {
                            if (cellvalue == '1') return "男";
                            if (cellvalue == '2') return "女";
                        }
                    },
                    {
                        label: '出生日期', name: 'birthdate', index: 'birthdate', width: 100, align: 'left',
                        formatter: function (cellvalue, options, rowObject) {
                            return formatDate(cellvalue, 'yyyy-MM-dd');
                        }
                    },
                    {label: '电话', name: 'telephone', index: 'telephone', width: 100, align: 'left'},
                    {label: '邮箱', name: 'email', index: 'email', width: 100, align: 'left'},
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
                sortname: 'user_name',
                sortorder: 'desc',
                rownumbers: true,
                shrinkToFit: false,
                gridview: true,
                multiselect: true,
                onSelectRow: function () {
                    SelectRowIndx = GetJqGridRowIndx("#" + this.id);
                }
            });
            columnModelData("user", "#gridTable");
            //自应高度
            $(window).resize(function () {
                $("#gridTable").setGridHeight($(window).height() - 178);
            });
        }

        //新增
        function btn_add() {
            //判断用户是否拥有新增用户的授权

            var url = "/admin/auth/authuser/useradd";
            openDialog(url, "Form", "新增用户", 600, 250, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }

        //编辑
        function btn_edit() {
            var KeyValue = GetJqGridRowValue("#gridTable", "id");
            if (IsChecked(KeyValue)) {
                //var url = "/CommonModule/User/Form?KeyValue=" + KeyValue;
                var url = "/admin/auth/authuser/useredit?userid=" + KeyValue;
                openDialog(url, "Form", "编辑用户", 600, 300, function (iframe) {
                    top.frames[iframe].AcceptClick();
                });
            }
        }

        //删除
        function btn_delete() {
            var KeyValue = GetJqGridRowValue("#gridTable", "id");
            if (IsDelData(KeyValue)) {
                var delparm = 'KeyValue=' + KeyValue;
                var url = "/admin/auth/authuser/userdelete";
                delConfig(url, delparm, KeyValue.split(",").length);
            }
        }

        //用户角色
        function btn_userrole() {
            var UserId = GetJqGridRowValue("#gridTable", "id");
            var realname = GetJqGridRowValue("#gridTable", "realName");
            console.log(UserId);
            console.log(IsChecked(UserId));
            if (IsChecked(UserId)) {
                //var url = "/CommonModule/User/UserRole?CompanyId=" + CompanyId + '&UserId=' + UserId;
                var url = "/admin/auth/authuser/userrole?userid=" + UserId;
                Dialog(url, "UserRole", "用户角色 - " + realname, 625, 350);
                /* openDialog(url, "UserRole", "用户角色 - " + realname, 625, 350, function (iframe) {
                    top.frames[iframe].AcceptClick();
                }); */
            }
        }

        //明细
        function btn_detail() {
            var KeyValue = GetJqGridRowValue("#gridTable", "id");
            //var gender = GetJqGridRowValue("#gridTable", "gender");
            if (IsChecked(KeyValue)) {
                //var url = "/CommonModule/User/Detail?KeyValue=" + KeyValue + '&gender=' + escape(gender);
                var url = "/admin/auth/authuser/userdetail?userid=" + KeyValue;
                Dialog(url, "Detail", "用户明细", 800, 410);
            }
        }


        //修改密码
        function btn_repass() {
            var KeyValue = GetJqGridRowValue("#gridTable", "id");
            if (IsChecked(KeyValue)) {
                var url = "/admin/auth/authuser/userrepass?userid=" + KeyValue;
                openDialog(url, "Form", "修改密码", 600, 300, function (iframe) {
                    top.frames[iframe].AcceptClick();
                });
            }
        }

        //绑定邮箱
        function btn_bindemail() {
            var KeyValue = GetJqGridRowValue("#gridTable", "id");
            if (IsChecked(KeyValue)) {
                var url = "/admin/auth/authuser/userbindemail?userId=" + KeyValue;
                openDialog(url, "Form", "绑定/解绑邮箱", 600, 300, function (iframe) {
                    top.frames[iframe].AcceptClick();
                });
            }
        }

        //解冻用户
        function btn_enable() {
            var userNameStr = GetJqGridRowValue("#gridTable", "userName");
            var enableStr = GetJqGridRowValue("#gridTable", "enabled");
            if ((enableStr.search("checknomark")!=-1) && (enableStr.search("checkokmark")!=-1)) {
                return tipDialog('您选择的用户中，含有不需要解冻的用户，请去掉', 4, 'warning');
            }
            if ((enableStr.search("checknomark")==-1) && (enableStr.search("undefined")==-1) ) {
                return tipDialog('您选择的用户不需要解冻，请重新选择', 4, 'warning');
            }
            if (IsDelData(userNameStr)) {
                var parm = 'userNameStr=' + userNameStr;
                var url = '/admin/auth/authuser/updateEnable';
                confirmDialog("温馨提示", "注：您确定要解冻 "
                        + userNameStr.split(",").length + " 个用户吗？", function (s) {
                    if (s) {
                        Loading(true, "正在解冻。。。");
                        window.setTimeout(function () {
                            $.ajax({
                                url: RootPath() + url,
                                type: 'get',
                                data: parm,
                                async: false,
                                success: function (data) {
                                    if (data == "success") {
                                        Loading(false);
                                        tipDialog("解冻成功！", 3, 1);
                                        location.reload();
                                    }else if (data == "error"){
                                        Loading(false);
                                        tipDialog("解冻失败！请联系管理员", 3, 1);
                                    }
                                },
                                error: function (data) {
                                    Loading(false);
                                    alertDialog(data.responseText, -1);
                                }
                            })
                        }, 200);
                    }
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
                <div>用户列表<span id="CenterTitle"></span></div>
            </div>
            <!--工具栏-->
            <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
                <div class="PartialButton">
                    <a id="lr-replace" ids="" title="刷新当前" onclick="Replace()" class="tools_btn"><span><i
                            class="fa fa-refresh"></i>&nbsp刷新</span></a>
                    <div class="tools_separator"></div>

                        <a id="lr-add" ids="" title="新增" onclick="btn_add()" class="tools_btn"><span><i
                                class="fa fa-plus"></i>&nbsp新增</span></a>

                        <a id="lr-edit" ids="" title="编辑" onclick="btn_edit()" class="tools_btn"><span><i
                                class="fa fa-pencil-square-o"></i>&nbsp编辑</span></a>

                        <a id="lr-delete" ids="" title="删除" onclick="btn_delete()" class="tools_btn"><span><i
                                class="fa fa-trash-o"></i>&nbsp删除</span></a>

                    <a id="lr-detail" ids="" title="详细信息" onclick="btn_detail()" class="tools_btn"><span><i
                            class="fa fa-reorder"></i>&nbsp详细</span></a>

                        <a id="lr-UserRole" ids="" title="查看角色" onclick="btn_userrole()" class="tools_btn"><span><i
                                class="fa fa-paw"></i>&nbsp查看角色</span></a>

                    <a id="lr-repass" ids="" title="修改密码" onclick="btn_repass()" class="tools_btn"><span><i
                            class="fa fa-lock"></i>&nbsp修改密码</span></a>

                    <div class="tools_separator"></div>
                    <a id="lr-leave" ids="" title="关闭当前窗口(Esc)" onclick="btn_back()" class="tools_btn"><span><i
                            class="fa fa-arrow-left"></i>&nbsp离开</span></a>
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
                            <td><input id="keywords" type="text" class="txt" style="width: 200px"/></td>
                            <td><input id="btnSearch" type="button" class="btnSearch" value="搜 索"
                                       onclick="btn_Search()"/></td>
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
