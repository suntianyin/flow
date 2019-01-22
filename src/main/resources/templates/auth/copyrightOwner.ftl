<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>版权所有者管理</title>
    <script type="text/javascript">

        $(function(){
            var name = $("#name").val().trim();
            var pinyin = $("#pinyin").val().trim();
            var pathurl = "?name=" + name + "&pinyin=" + pinyin;
            var totalPages = ${pages!''};
            var currentPages = ${pageNum!''};
            jqPaging(pathurl,totalPages,currentPages);
        });

        //检索
        function btn_Search() {
            var name = $("#name").val().trim();
            var pinyin = $("#pinyin").val().trim();
            window.location.href = "index?name=" + name + "&pinyin=" + pinyin;
        }

        function updateAuth(id) {
            var url = "/copyrightOwner/edit/index?id=" + id;
            openDialog(url, "updateCopyrightOwner", "编辑版权所有者信息", 520, 220, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

        function btn_addAuth() {
            var url = "/copyrightOwner/add/index"
            openDialog(url, "addCopyrightOwner", "添加版权所有者信息", 520, 260, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }
        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        };
        function deleteById(id) {
            if (isNull(id)){
                tipDialog("数据异常！", 3, -1);
                return;
            }
            var note = "注：您确定要删除当前版权所有者信息？";
            var url = "/copyrightOwner/deleteById?id=" + id;
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
                                    tipDialog("删除成功", 3, 1);
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
    </script>
</head>
<body>
<div id="layout" class="layout">
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>版权所有者管理<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a id="lr-add" title="添加版权所有者" onclick="btn_addAuth()" class="tools_btn"><span><i
                        class="fa fa-plus"></i>&nbsp添加版权所有者</span></a>
                <div class="tools_separator"></div>
            </div>
        </div>
        <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
            <table border="0" class="form-find" style="height: 45px;">
                <tr>
                    <th>版权所有者名称：</th>
                    <td>
                        <input id="name" name="name" type="text" value="${name!'' }" class="txt" style="width: 200px"/>
                    </td>
                    <th>版权所有者拼音：</th>
                    <td>
                        <input id="pinyin" name="pinyin" type="text" value="${pinyin!'' }" class="txt" style="width: 200px"/>
                    </td>

                    <td>
                        <input id="btnSearch" type="button" class="btnSearch" value="搜 索" onclick="btn_Search()"/>
                    </td>
                </tr>
            </table>
        </div>
        <div class="panel-body">
            <div class="row">
                <table id="table-list"
                       class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                    <thead>
                    <tr role="row">
                        <#--<th>版权所有者ID</th>-->
                        <th>版权所有者名称</th>
                        <th>状态</th>
                        <th>版权所有者拼音</th>
                        <th>版权所有者简介</th>
                        <th>操作人</th>
                        <th>操作时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#if CopyrightOwnerList??>
                        <#list CopyrightOwnerList as list>
                        <tr class="gradeA odd" role="row">
                            <#--<td align="center">${(list.id)!''}</td>-->
                            <td align="center">${(list.name)! '' }</td>
                            <td align="center">${((list.status==0)?string('启用','禁用')) }</td>
                            <td align="center">${(list.pinyin)! '' }</td>
                            <td align="center">${(list.remark)! '' }</td>
                            <td align="center">${(list.operator) !''}</td>
                            <td align="center">${(list.operateDate)?datetime! '' }</td>
                            <td align="center">
                                <a style="cursor:pointer;" onclick="updateAuth('${list.id! "" }');">编辑&nbsp;</a>
                                <a style="cursor:pointer;" onclick="deleteById('${list.id! "" }');">删除&nbsp;</a>
                            </td>
                        </tr>
                        </#list>
                    </#if>
                    </tbody>
                </table>
            </div>
            <ul class="pagination" style="float:right;" id="pagination"></ul>
        </div>
    </div>
</div>
</body>
</html>
