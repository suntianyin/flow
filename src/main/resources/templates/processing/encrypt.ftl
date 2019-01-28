<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>加密任务</title>
    <script type="text/javascript">

        $(function () {
            var pathurl = "index";
            var totalPages = ${pages!1};
            var currentPages = ${pageNum!1};
            jqPaging(pathurl, totalPages, currentPages);
        });
        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        }
        //查看资源
        function detailEncrypt(id) {
            if (isNull(id)) {
                tipDialog("加密任务id不能为空", 3, -1);
                return;
            }
            window.location.href = "${ctx}/processing/encrypt/encryptResourceIndex?id=" + $.trim(id);
        }
        //查看日志
        function parsing(id,batchId) {
            var path=$("#"+id).val();
            if (isNull(path)) {
                tipDialog("资源路径不能为空", 3, -1);
                return;
            }
            var formData = new FormData();
            formData.append('id',id);
            formData.append('path',path);
            formData.append('batchId',batchId);
            var url = "${ctx}/processing/bibliotheca/parsing";
            var note = "注：请核实资源路径："+path+"是否正确？";
            confirmDialog("温馨提示", note, function (r) {
                if (r) {
                    Loading(true, "正在提交数据...");
                    window.setTimeout(function () {
                        try {
                            $.ajax({
                                url: url,
                                type: "POST",
                                contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                                async: false,
                                processData: false,
                                contentType: false,
                                data:formData,
                                success: function (data) {
                                    if (data.status == 200) {
                                        tipDialog(data.msg, 3, 1);
                                    } else {
                                        tipDialog(data.msg, 3, -1);
                                    }
                                    location.reload();
                                },
                                error: function (data) {
                                    Loading(false);
                                    tipDialog("服务器异常！", 3, -1);
                                }
                            });
                        } catch (e) {
                        }
                    }, 200);
                }
            });
        }
        //加密
        function encrypt(){
            var encrypt = $('input:checkbox:checked');
            var fileInfo = "";
            $.each(encrypt, function () {
                fileInfo += $(this).val() + ";";
            });
            if (fileInfo == "" || fileInfo == "on;") {
                tipDialog("请选择数据", 3, -2);
                return;
            }
            alert(fileInfo)
        }
        //全选/全取消
        function selectAll(checkBox, checkName) {
            if (checkBox.checked) {
                $("input[type='checkbox'][name='" + checkName + "']").prop("checked", true);//全选
            } else {
                $("input[type='checkbox'][name='" + checkName + "']").prop("checked", false);  //取消全选
            }
        }
    </script>
</head>
<body>
<div class='load-circle' style="display: none;"></div>
<div id="layout" class="layout">
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>任务管理<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a id="lr-add" title="加密" onclick="encrypt()" class="tools_btn"><span><i
                                class="fa fa-plus"></i>&nbsp加密</span></a>
                <div class="tools_separator"></div>
            </div>
        </div>
        <div class="panel-body">
            <div class="row">
                <table id="table-list"
                       class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                    <thead>
                    <tr role="row">
                        <th><input type="checkbox" id="allCheckedLtYes" onclick="selectAll(this, 'ltYes1')"></th>
                        <th >任务名称</th>
                        <th >批次号</th>
                        <th >加密数量</th>
                        <th >成功数量</th>
                        <th >任务状态</th>
                        <th >创建时间</th>
                        <th >完成时间</th>
                        <th >进度显示</th>
                        <th >操作人</th>
                        <th >操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#if encryptList??>
                        <#list encryptList as list>
                            <tr class="gradeA odd" role="row">
                            <td align="center"><input type="checkbox" name="ltYes1" value="${(list.id)!''}"/></td>
                            <td align="center">${(list.taskName)!''}</td>
                            <td align="center">${(list.batch)! '' }</td>
                            <td align="center">${(list.encryptNum)! '' }</td>
                            <td align="center">${(list.finishNum)!'' }</td>
                            <td align="center">${(list.taskState.getDesc())! '' }</td>
                            <td align="center">${(list.createTime?datetime)! '' }</td>
                            <td align="center">${(list.finishTime?datetime)! '' }</td>
                            <td ><meter value="${(list.pre)!'' }" ></meter> </td>
                            <td align="center">${(list.operator)! '' }</td>
                            <td align="center">
                        <a href="javascript:void(0);" onclick="detailEncrypt('${(list.id)!''}')">查看资源</a>
                        <a href="javascript:void(0);" onclick="detailLog('${(list.id)!''}')">查看日志</a>
                            </td>
                            </tr>
                        </#list>
                    </#if>
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
