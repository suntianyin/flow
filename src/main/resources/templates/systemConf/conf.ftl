<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>系统参数管理</title>
    <script type="text/javascript">

        //                function updatePublisher(id) {
        //        //            console.log("********************")
        //                    window.location.href = "edit?id="+id;
        //                }

        $(function () {
            var pathurl = "index?";
            var totalPages = ${pages?c};
            var currentPages = ${pageNum?c};
            jqPaging(pathurl, totalPages, currentPages);
        });

        //编辑系统参数管理数据
        function updateSystemConf(id) {
            var url = "/systemConf/edit?id=" + id;
            openDialog(url, "updateSystemConf", "编辑系统参数数据", 500, 400, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

        //显示系统参数管理详情
        function showSystemConf(id) {
            var url = "/systemConf/systemConfShow?id=" + id;
            AddTabMenu('SystemConfShow', url, '系统参数数据详情', null, 'true', 'true');
        }
        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        };
        function deleteSystemConf(id) {
            if (isNull(id)){
                tipDialog("数据异常！", 3, -1);
                return;
            }

            var note = "注：您确定要删除当前系统参数信息？";
            if (!isNull(id)){
                note = "注：您确定要删除 编号为：" + id + " 的系统参数信息？";
            }
            var url = "/systemConf/removeSystemConf?id=" + id;
            confirmDialog("温馨提示", note, function (r) {
                if (r) {
                    Loading(true, "正在提交数据...");
                    window.setTimeout(function () {
                        try {
                            $.ajax({
                                url: RootPath()+ url,
                                type: "GET",
                                contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                                async: false,
                                success: function (data) {
                                    if (data.status == 200){
                                        tipDialog(data.msg, 3, 1);
                                    }else{
                                        tipDialog(data.msg, 3, -1);
                                    }
                                    location.reload();
                                },
                                error: function (data) {
                                    Loading(false);
                                    tipDialog("服务器异常！",3, -1);
                                }
                            });
                        } catch (e) {
                        }
                    }, 200);
                }
            });
        }
        //添加系统参数管理
        function btn_addSystemConf() {
            var url = "/systemConf/save";
            openDialog(url, "addSystemConf", "添加系统参数数据", 500, 400, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

    </script>
</head>
<body>
<div id="layout" class="layout">
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>配置管理<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a id="lr-add" title="新增" onclick="btn_addSystemConf()" class="tools_btn"><span><i
                        class="fa fa-plus"></i>&nbsp新增</span></a>
                <div class="tools_separator"></div>
            </div>
        </div>
        <div class="panel-body">
            <div class="row">
                <table id="table-list"
                       class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                    <thead>
                    <tr role="row">
                        <th>编号</th>
                        <th>配置名称</th>
                        <th>配置键</th>
                        <th>配置值</th>
                        <th>内容描述</th>
                        <th>记录创建人</th>
                        <th>记录创建时间</th>
                        <th>记录修改时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list systemConfList as list>
                    <tr class="gradeA odd" role="row">
                        <td align="center">${list.id}</td>
                        <td align="center">${list.confName! '' }</td>
                        <td align="center">${list.confKey! '' }</td>
                        <td align="center">${list.confValue! '' }</td>
                        <td align="center">${list.description! '' }</td>
                        <td align="center">${list.operator! '' }</td>
                        <td align="center">${(list.createTime?string("yyyy-MM-dd"))!'' }</td>
                        <td align="center">${(list.updateTime?string("yyyy-MM-dd"))!'' }</td>
                        <td align="center">
                            <a href="javascript:void(0);" onclick="showSystemConf('${list.id}')">详情</a>
                            <a href="javascript:void(0);" onclick="updateSystemConf('${list.id}')">修改</a>
                            <a href="javascript:void(0);" onclick="deleteSystemConf('${list.id}')">删除</a>
                        </td>
                    </tr>
                    </#list>
                    </tbody>
                </table>
            </div>
            <ul class="pagination">
                <li>每页 ${pageSize!0} 条记录，共 ${pages!0} 页，共 ${total!0} 条记录</li>
            </ul>
            <ul class="pagination" style="float:right;" id="pagination"></ul>
        </div>
    </div>
</div>
</body>
</html>
