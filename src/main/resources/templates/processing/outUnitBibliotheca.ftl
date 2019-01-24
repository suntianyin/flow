<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>外协书目信息</title>
    <script type="text/javascript">

        $(function () {
            /*if (isNull($('#batchId').val())){
                tipDialog("批次号不能为空，信息异常", 3, -1);
                return;
            }*/
            var batchId = $('#batchId').val().trim();
            var title = $("#title").val().trim();
            var publisher = $("#publisher").val().trim();
            // var duplicateFlag = $("#duplicateFlag").val();
            $("#bibliothecaState").val("${(bibliothecaState.getCode())!''}");
            var bibliothecaState = $("#bibliothecaState").val();
            var pathurl = "outUnit/index?batchId=" + batchId + "&title=" + title + "&publisher=" + publisher + "&bibliothecaState=" + bibliothecaState;
            var totalPages = ${pages!1};
            var currentPages = ${pageNum!1};

        <#--$("#duplicateFlag").val("${(duplicateFlag.getCode())!''}");-->

            jqPaging(pathurl, totalPages, currentPages);

        });

        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        };

        //检索
        function btn_Search() {
            if (isNull($('#batchId').val())) {
                tipDialog("批次号不能为空，信息异常", 3, -1);
                return;
            }
            var batchId = $('#batchId').val().trim();
            var title = $("#title").val().trim();
            var publisher = $("#publisher").val().trim();
            // var duplicateFlag = $("#duplicateFlag").val().trim();
            var bibliothecaState = $("#bibliothecaState").val();
            window.location.href = "${ctx}/processing/bibliotheca/outUnit/index?batchId=" + batchId + "&title=" + title + "&publisher=" + publisher + "&bibliothecaState=" + bibliothecaState;
        }

        function updateBibliotheca(id) {
            var url = "/processing/bibliotheca/edit/index?id=" + id;
            openDialog(url, "updateBatch", "编辑书目", 440, 400, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }

        //编辑图书元数据
        function editBookMeta(metaId) {
            var url = "/processing/bibliotheca/editBookMeta?metaId=" + metaId;
            openDialog(url, "editBookMeta", "编辑图书元数据", 600, 650, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }

        //上传CEBXM文件
        function uploadCebxm(metaId) {
            var url = "/processing/bibliotheca/uploadCebxm?metaId=" + metaId;
            openDialog(url, "uploadCebxm", "上传文件", 440, 400, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }

        //上传图片文件
        function uploadImage(metaId) {
            var url = "/processing/bibliotheca/uploadImage?metaId=" + metaId;
            openDialog(url, "uploadImage", "上传图片文件", 440, 400, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }

        // 删除书目
        function removeBibliotheca(id, identifier) {
            if (isNull(id)) {
                tipDialog("数据异常！", 3, -1);
                return;
            }

            var note = "注：您确定要删除当前书目信息？";
            if (!isNull(identifier)) {
                note = "注：您确定要删除 编号为：" + identifier + " 的书目信息？";
            }

            var url = "${ctx}/processing/bibliotheca/removeBibliotheca?id=" + id;
            confirmDialog("温馨提示", note, function (r) {
                if (r) {
                    Loading(true, "正在提交数据...");
                    window.setTimeout(function () {
                        try {
                            $.ajax({
                                url: url,
                                type: "GET",
                                contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                                async: false,
                                success: function (data) {
//                                    Loading(false);
                                    if (data.status == 200) {
                                        tipDialog(data.msg, 3, 1);
                                        /*top.frames[tabiframeId()].location.reload();
                                        closeDialog();*/
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

        function updateBatchState(id) {
            var url = "/processing/batch/editBatchState/index?id=" + id;
            openDialog(url, "updateBatchState", "编辑批次状态", 350, 100, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }

        //单本书目添加
        function btn_addBibliotheca() {
            var batchId = $('#batchId').val();
            if (isNull(batchId)) {
                tipDialog("批次号不能为空", 3, -1);
                return;
            }
            var url = "/processing/bibliotheca/add/index?batchId=" + batchId;
            openDialog(url, "addBibliotheca", "新增书目", 440, 400, function (iframe) {
                top.frames[iframe].AcceptClick();
            });
        }


        //制作成功
        function btn_makeSuccess() {

            var checkID = [];//定义一个空数组，用于存放选中的值

            $("input[type='checkbox'][name='bibliotheca']:checked").each(function (i) {
                checkID[i] = eval('(' + $(this).val() + ')');
            })

            var url = "/processing/bibliotheca/makeSuccess";
            confirmDialog("温馨提示", "注：您确定要对选中项提交 制作成功？", function (r) {
                if (r) {
                    Loading(true, "正在提交数据...");
                    window.setTimeout(function () {
                        try {
                            $.ajax({
                                url: RootPath() + url,
                                type: "POST",
                                contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                                data: JSON.stringify(checkID),
//                                data: checkID,
                                async: false,
                                success: function (data) {
//                                    Loading(false);
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

        //制作失败
        function btn_makeFail() {

            var checkID = [];//定义一个空数组，用于存放选中的值

            $("input[type='checkbox'][name='bibliotheca']:checked").each(function (i) {
                checkID[i] = eval('(' + $(this).val() + ')');
            })

            var url = "/processing/bibliotheca/makeFail";
            confirmDialog("温馨提示", "注：您确定要对选中项提交 制作失败？", function (r) {
                if (r) {
                    Loading(true, "正在提交数据...");
                    window.setTimeout(function () {
                        try {
                            $.ajax({
                                url: RootPath() + url,
                                type: "POST",
                                contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                                data: JSON.stringify(checkID),
//                                data: checkID,
                                async: false,
                                success: function (data) {
//                                    Loading(false);
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

        //书目 批量导入
        function btn_batchImport() {
            var batchId = $('#batchId').val();
            if (isNull(batchId)) {
                tipDialog("批次号不能为空", 3, -1);
                return;
            }

            var fileObj = document.getElementById("importFile").files[0]; // js 获取文件对象
            if (typeof (fileObj) == "undefined" || fileObj.size <= 0) {
                alert("请选择文件");
                return;
            }
            var formFile = new FormData();
            formFile.append("file", fileObj); //加入文件对象
            var data = formFile;

            var url = "${ctx}/processing/bibliotheca/batch/import?batchId=" + batchId;
            confirmDialog("温馨提示", "注：您确定要对当前文件进行书目批量导入？", function (r) {
                if (r) {
                    Loading(true, "正在提交数据...");
                    window.setTimeout(function () {
                        try {
                            $.ajax({
                                url: url,
                                data: data,
                                type: "POST",
                                cache: false,//上传文件无需缓存
                                processData: false,//用于对data参数进行序列化处理 这里必须false
                                contentType: false, //必须
                                async: false,
                                success: function (data) {
                                    if (data.status == 200) {
                                        tipDialog(data.msg, 3, 1);
                                        btn_Search();
                                        return;
                                    } else {
                                        tipDialog(data.msg, 3, -1);
                                        return;
                                    }
                                },
                                error: function (data) {
                                    tipDialog("批量导入失败", 3, -1);
                                }
                            });
                        } catch (e) {
                        }
                    }, 200);
                }
            });
        }

        function btn_exportOutUnitData() {
            if (isNull($('#batchId').val())) {
                tipDialog("批次号不能为空，信息异常", 3, -1);
                return;
            }
            var batchId = $('#batchId').val().trim();
            var title = $("#title").val().trim();
            var publisher = $("#publisher").val().trim();
            // var duplicateFlag = $("#duplicateFlag").val();
            var bibliothecaState = $("#bibliothecaState").val();

            confirmDialog("温馨提示", "注：您确定要对当前查询结果导出到Excel文件？", function (r) {
                if (r) {
                    Loading(true, "正在提交数据...");
                    window.setTimeout(function () {
                        try {
                            window.location.href = "${ctx}/processing/bibliotheca/outUnit/exportData?batchId=" + batchId + "&title=" + title + "&publisher=" + publisher + "&bibliothecaState=" + bibliothecaState;
                        } catch (e) {
                        }
                    }, 200);
                }
            });
        }

        //全选/全取消
        function selectAll(checkbox) {
            if ($("#checkAll").prop("checked")) {
                $("input[type='checkbox'][name='bibliotheca']").prop("checked", true);//全选
            } else {
                $("input[type='checkbox'][name='bibliotheca']").prop("checked", false);  //取消全选
            }
        }

        //标引
        function indexing(id) {
            window.location.href = "${ctx}/processing/bibliotheca/indexing?id=" + id;
        }

        //书目信息pdf查看
        function pdf(id) {
            var url = "${ctx}/processing/bibliotheca/pdf?id=" + id;
            window.open(url);
        }

    </script>
</head>
<body>
<div id="layout" class="layout">
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>外协书目信息<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a onclick="javascript:window.location.href='${ctx}/processing/batch/booklist/index'" class="tools_btn"><span><i
                        class="fa fa-backward"></i>&nbsp;返回</span></a>
                <div class="tools_separator"></div>
            </div>
            <#if (timeState)??>
                <div class="PartialButton">
                    <a id="lr-add1" class="tools_btn"><span style="color: #7c7c7c;"><i
                            class="fa fa-plus"></i>&nbsp;新增</span></a>
                    <div class="tools_separator"></div>
                </div>
            <#else>
                <div class="PartialButton">
                    <a id="lr-add" title="新增书目" onclick="btn_addBibliotheca()" class="tools_btn"><span><i
                            class="fa fa-plus"></i>&nbsp;新增</span></a>
                    <div class="tools_separator"></div>
                </div>
            </#if>
            <div class="PartialButton">
                <a id="lr-make" title="制作成功" onclick="btn_makeSuccess()" class="tools_btn"><span><i
                        class="fa fa-adjust"></i>&nbsp;制作成功</span></a>
                <div class="tools_separator"></div>
            </div>
            <div class="PartialButton">
                <a id="lr-make" title="制作失败" onclick="btn_makeFail()" class="tools_btn"><span><i
                        class="fa fa-adjust"></i>&nbsp;制作失败</span></a>
                <div class="tools_separator"></div>
            </div>
            <div class="PartialButton">
                <input id="importFile" type="file" class="tools_btn"
                       accept="application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                       name="file""/>
                <div class="tools_separator"></div>
            </div>
            <div class="PartialButton">
                <button id="batch-import" title="批量导入" class="tools_btn" onclick="btn_batchImport()">
                    <span><i class="fa fa-plus"></i>&nbsp;批量导入</span></button>
                <div class="tools_separator"></div>
            </div>

            <div class="PartialButton">
                <button id="batch-import" title="导出查询结果" class="tools_btn" onclick="btn_exportOutUnitData()">
                    <span><i class="fa fa-outdent"></i>&nbsp;导出查询结果</span></button>
                <div class="tools_separator"></div>
            </div>
        </div>
        <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
            <table border="0" class="form-find" style="height: 45px;">
                <tr>
                    <th>标题：</th>
                    <td>
                        <input id="batchId" name="batchId" value="${batchId!''}" hidden class="txt"/>
                        <input id="title" name="title" type="text" value="${title!'' }" class="txt"
                               style="width: 200px"/>
                    </td>

                    <th>出版社：</th>
                    <td>
                        <input id="publisher" name="publisher" type="text" value="${publisher!'' }" class="txt"
                               style="width: 200px"/>
                    </td>

                <#--<th>是否重复：</th>-->
                <#--<td>-->
                <#--<select id="duplicateFlag" name="duplicateFlag" underline="true" style="height: 24px;">-->
                <#--<option value="">--请选择批次状态--</option>-->
                <#--<option value="0">否</option>-->
                <#--<option value="1">是</option>-->
                <#--</select>-->
                <#--</td>-->

                    <th>书目状态：</th>
                    <td>
                        <select id="bibliothecaState" name="bibliothecaState" underline="true" style="height: 24px;">
                            <option value="">--请选择书目状态--</option>
                            <#if bibliothecaStateList??>
                                <#list bibliothecaStateList as list>
                                    <option value="${(list.getCode())!''}">${(list.getDesc())!''}</option>
                                </#list>
                            </#if>
                        </select>
                    </td>

                    <td>
                        <input id="btnSearch" type="button" class="btnSearch" value="查 询" onclick="btn_Search()"/>
                    </td>
                </tr>
            </table>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;共 <label>${total!0}</label>
            条书目信息,已分拣<label>${num1!0}</label>条,重复<label>${num2!0}</label>条,制作成功<label>${num3!0}</label>条,制作失败<label>${num4!0}</label>条。
        </div>
        <div class="panel-body">
            <div class="row">

                <table id="table-list"
                       class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
                    <thead>
                    <tr role="row">
                        <th><input type="checkbox" id="checkAll" onclick="selectAll(this)"></th>
                        <th>编号</th>
                        <th>资源唯一标识</th>
                        <th>批次号</th>
                        <th>标题</th>
                        <th>作者</th>
                        <th>出版社</th>
                        <th>ISBN</th>
                        <th>出版时间</th>
                        <th>版次</th>
                        <th>纸书价格</th>
                        <th>电子书价格</th>
                        <th>原始文件名</th>
                        <th>文档格式</th>
                        <th>备注</th>
                    <#--<th>是否重复</th>-->
                        <th>书目状态</th>
                    <#--<th>是否制作成功</th>-->
                        <th>书目录入人</th>
                        <th>书目录入时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#if bibliothecaList??>
                        <#list bibliothecaList as list>
                            <tr class="gradeA odd" role="row">
                                <td><input type="checkbox" name="bibliotheca"
                                           value="{'id':'${(list.id)!''}', 'bibliothecaState':'${(list.bibliothecaState.code)!''}'}"/>
                                </td>
                                <td>${(list.identifier)!''}</td>
                                <td>${(list.metaId)! '' }</td>
                                <td>${(list.batchId)! '' }</td>
                                <td>${(list.title)! '' }</td>
                                <td>${(list.author)! '' }</td>
                                <td>${(list.publisherName)! '' }</td>
                                <td>${(list.isbn)! '' }</td>
                                <td>${(list.publishTime)! '' }</td>
                                <td>${(list.edition) !''}</td>
                                <td>${(list.paperPrice)! '' }</td>
                                <td>${(list.eBookPrice)! '' }</td>
                                <td>${(list.originalFilename)!'' }</td>
                                <td>${(list.documentFormat)! '' }</td>
                                <td>${(list.memo)! '' }</td>
                            <#--<td>${(list.duplicateFlag.getDesc())! '' }</td>-->
                                <td>${(list.bibliothecaState.getDesc())! '' }</td>
                            <#--<td>${(list.completedFlag.getDesc())! '' }</td>-->
                                <td>${(list.creator)! '' }</td>
                                <td>${(list.createTime?datetime)! '' }</td>
                                <td>
                                    <#if (timeState)??>
                                        <span style="color: #7c7c7c;">编辑</span>
                                        <span style="color: #7c7c7c;">删除</span>
                                    <#else>
                                        <a href="javascript:void(0);"
                                           onclick="updateBibliotheca('${(list.id)!''}')">编辑</a>
                                        <a href="javascript:void(0);"
                                           onclick="removeBibliotheca('${(list.id)!''}','${(list.identifier)!''}')">删除</a>
                                        <a href="javascript:void(0);"
                                           onclick="pdf('${(list.id)!''}')">pdf预览</a>
                                    </#if>
                                    <#if list.bibliothecaState.getCode()== 5 && list.convertStatus?? && list.convertStatus==2>
                                        <a href="javascript:void(0);"
                                           onclick="indexing('${(list.id)!''}')">标引</a>
                                    <#else>
                                        <span style="color: #7c7c7c;">标引</span>
                                    </#if>
                                   <#if list.metaId??>
                                        <a href="javascript:void(0);"
                                           onclick="editBookMeta('${(list.metaId)!''}')">编辑图书元数据</a>
                                       <a href="javascript:void(0);"
                                          onclick="uploadCebxm('${(list.metaId)!''}')">上传CEBXM</a>
                                       <a href="javascript:void(0);"
                                          onclick="uploadImage('${(list.metaId)!''}')">上传封面</a>
                                   <#else>
                                        <span style="color: #7c7c7c;">编辑图书元数据</span>
                                   </#if>
                                </td>
                            </tr>
                        </#list>
                    </#if>
                    </tbody>
                </table>
            </div>
        <#--<ul class="pagination">
            <li>每页 ${pageSize!0} 条记录，共 ${pages!0} 页，共 ${total!0} 条记录</li>
        </ul>
        <ul class="pagination" style="float:right;" id="pagination"></ul>-->
        </div>
    </div>
</div>
</body>
</html>
