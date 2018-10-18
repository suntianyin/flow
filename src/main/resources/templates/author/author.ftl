<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>作者信息</title>
    <script type="text/javascript">

        $(function(){
            var pathurl = "?id="+$("#authorId").val().trim()+"&title="+$("#authorTitle").val().trim();
            var totalPages = ${pages!''};
            var currentPages = ${pageNum!''};
            jqPaging(pathurl,totalPages,currentPages);

            $("#batch-import").click(function () {
                var fileObj = document.getElementById("importFile").files[0]; // js 获取文件对象
                if (typeof (fileObj) == "undefined" || fileObj.size <= 0) {
                    alert("请选择文件");
                    return;
                }
                var formFile = new FormData();
//                formFile.append("action", "${ctx}/author/batch/import");
                formFile.append("file", fileObj); //加入文件对象

                //第一种  XMLHttpRequest 对象
                //var xhr = new XMLHttpRequest();
                //xhr.open("post", "/Admin/Ajax/VMKHandler.ashx", true);
                //xhr.onload = function () {
                //    alert("上传完成!");
                //};
                //xhr.send(formFile);

                //第二种 ajax 提交

                var data = formFile;
                $.ajax({
                    url: "${ctx}/author/batch/import",
                    data: data,
                    type: "POST",
                    dataType: "text",
                    cache: false,//上传文件无需缓存
                    processData: false,//用于对data参数进行序列化处理 这里必须false
                    contentType: false, //必须
                    success: function (result) {
                        if (result == '成功'){
                            tipDialog("批量导入成功", 3, 1);
                            btn_Search();
                            return;
                        }else {
                            tipDialog("批量导入失败", 3, -1);
                            return;
                        }
                    },
                    error: function (result) {
                        tipDialog("批量导入失败", 3, -1);
                    }
                })
            })

        });

        //检索
        function btn_Search() {
            var id = $("#authorId").val().trim();
            var title = $("#authorTitle").val().trim();
            window.location.href = "index?id=" + id + "&title=" + title;
        }

        function updateAuthor(id) {
            var url = "/author/edit/index?id=" + id;
            openDialog(url, "updateAuthor", "编辑作者信息", 500, 400, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

        function btn_addAuthor() {
            var url = "/author/add/index"
            openDialog(url, "addAuthor", "添加作者信息", 500, 400, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

    </script>
</head>
<body>
<div id="layout" class="layout">
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>作者信息<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a id="lr-add" title="新增" onclick="btn_addAuthor()" class="tools_btn"><span><i
                        class="fa fa-plus"></i>&nbsp新增</span></a>
                <div class="tools_separator"></div>
            </div>
            <div class="PartialButton">
                <#--<a id="lr-add" title="批量导入" onclick="btn_batchAddAuthor()" class="tools_btn"><span><i
                        class="fa fa-plus"></i>&nbsp批量导入</span></a>-->
                <#--<form id="batchImportForm" enctype="multipart/form-data">-->
                    <input id="importFile" type="file" class="tools_btn" accept=".csv" name="file""/>
                <div class="tools_separator"></div>
            </div>
            <div class="PartialButton">
                <#--<a id="lr-add" title="批量导入" onclick="btn_batchAddAuthor()" class="tools_btn"><span><i
                        class="fa fa-plus"></i>&nbsp批量导入</span></a>-->
                <#--<form id="batchImportForm" enctype="multipart/form-data">-->
                    <button id="batch-import" title="批量导入" class="tools_btn"><span><i
                            class="fa fa-plus"></i>&nbsp批量导入</span></button>
                <div class="tools_separator"></div>
            </div>
        </div>
        <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
            <table border="0" class="form-find" style="height: 45px;">
                <tr>
                    <th>作者ID：</th>
                    <td>
                        <input id="authorId" type="text" value="${authorId!'' }" class="txt" style="width: 200px"/>
                    </td>

                    <th>作者名：</th>
                    <td>
                        <input id="authorTitle" type="text" value="${authorTitle!'' }" class="txt" style="width: 200px"/>
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
                        <th>作者id</th>
                        <th>作者姓名</th>
                        <th>姓名类型</th>
                        <th>开始使用时间</th>
                        <th>结束使用时间</th>
                        <th>国籍</th>
                        <th>身份证号</th>
                        <th>出生日期</th>
                        <th>死亡日期</th>
                        <th>性别</th>
                        <th>作者类型</th>
                        <th>民族</th>
                        <th>学历</th>
                        <th>所在朝代名称</th>
                        <th>籍贯</th>
                        <th>职业分类名称</th>
                        <th>任职机构名称</th>
                        <th>头像</th>
                        <th>简介</th>
                        <th>是否卒于50年</th>
                        <th>国图作者id</th>
                        <th>操作人</th>
                        <th>创建时间</th>
                        <th>最后更新时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#if authorList??>
                        <#list authorList as list>
                        <tr class="gradeA odd" role="row">
                            <td>${(list.id)!''}</td>
                            <td>${(list.title)! '' }</td>
                            <td>${(list.titleType.getDesc())! '' }</td>
                            <td>${(list.startDate)! '' }</td>
                            <td>${(list.endDate)!'' }</td>
                            <td>${(list.nationalityCode)! '' }</td>
                            <td>${(list.personId)! '' }</td>
                            <td>${(list.birthday)! '' }</td>
                            <td>${(list.deathDay)! '' }</td>
                            <td>${(list.sexCode.getDesc())! '' }</td>
                            <td>${(list.type.getDesc()) !''}</td>
                            <td>${(list.nationalCode)! '' }</td>
                            <td>${(list.qualificationCode)! '' }</td>
                            <td>${(list.dynastyName)! '' }</td>
                            <td>${(list.originCode)! '' }</td>
                            <td>${(list.careerClassCode)! '' }</td>
                            <td>${(list.serviceAgency)! '' }</td>
                            <td>${(list.headPortraitPath)! '' }</td>
                            <td>${(list.summary)! '' }</td>
                            <td>${(list.dieOver50.getDesc())! '' }</td>
                            <td>${(list.nlcAuthorId)! '' }</td>
                            <td>${(list.operator)! '' }</td>
                            <td>${(list.createTime?datetime)! '' }</td>
                            <td>${(list.updateTime?datetime)! '' }</td>
                            <td>
                                <a href="javascript:void(0);" onclick="updateAuthor('${list.id}')">修改</a>
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
