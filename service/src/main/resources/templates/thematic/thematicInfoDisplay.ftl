<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>论题信息数据</title>
    <script type="text/javascript">
        function fileChange(inputTag, id) {
            // 获取文件名称
            var fileName = $("#"+id+"").val();
            // 获取文件类型
            var fileType = fileName.substr(fileName.lastIndexOf("."), fileName.length);
            if (fileType == '.xls' || fileType == '.xlsx') {
                loading();
                var formData = new FormData($(inputTag).parent()[0]);
                var id = id;
                $.ajax({
                    type: 'post',
                    url: "upload?id=" + id,
                    data: formData,
                    cache: false,
                    processData: false,
                    contentType: false,
                }).success(function (data) {
                    alert("上传成功！");
                    formData = null;
                    $(".load-circle").hide();
                }).error(function (data) {
                    alert("上传失败");
                    formData = null;
                    $(".load-circle").hide();
                });
            } else {
                alert("*上传文件类型错误,支持类型: .xls .xlsx");
            }
        }

        // 查看论题信息
        function checkThematicSeries(id) {
            window.location.href = "thematicCheck?" + "id=" + id;
            loading();
        }

        function checkThematicSeriesByPage(id) {
            window.location.href = "thematicInfoDisplayByPage?" + "id=" + id;
            loading();
        }

        // 编辑论题信息
        function editThematicSeries(id) {
            window.location.href = "thematicEdit?" + "id=" + id;
            loading();
        }
        
        function epubChapterAdd() {
            window.location.href="thematicAdd";
            loading();
        }
    </script>
    <style>
        .fileinput-button {
            position: relative;
            display: inline-block;
            overflow: hidden;
        }

        .fileinput-button input {
            position: absolute;
            right: 0px;
            top: 0px;
            opacity: 0;
            -ms-filter: 'alpha(opacity=0)';
            /*font-size: 200px;*/
        }
    </style>
</head>
<body>
<div class='load-circle' style="display: none;"></div>
<!--工具栏-->
<div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
    <div class="PartialButton">
        <div class="PartialButton">
            <a id="lr-epubAdd" title="添加论题信息" onclick="epubChapterAdd()"
               class="tools_btn"><span><i class="fa fa-plus"></i>&nbsp;添加论题信息</span></a>
        </div>
    </div>
</div>
<table id="table-list"
       class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
    <thead>
    <tr role="row">
        <th>论题id</th>
        <th>名称</th>
        <th>数据来源</th>
        <th>整理人</th>
        <th>操作人</th>
        <th>创建时间</th>
        <th>最后更新时间</th>
        <th colspan="3">操作</th>
    </tr>
    </thead>
    <tbody>
    <#list thematicSeriesList as thematicSeries>
    <tr class="gradeA odd" role="row">
        <td align="center">${thematicSeries.id! ''}</td>
        <td align="center">${thematicSeries.title! ''}</td>
        <td align="center">${thematicSeries.dataSource! ''}</td>
        <td align="center">${thematicSeries.collator! ''}</td>
        <td align="center">${thematicSeries.operator! ''}</td>
        <td align="center">${thematicSeries.createTime?string('yyyy-MM-dd HH:mm:ss')! ''}</td>
        <td align="center">${thematicSeries.updateTime?string('yyyy-MM-dd HH:mm:ss')! ''}</td>
        <td align="center">
            <form id="uploadForm" enctype="multipart/form-data">
                上传excel文件：<input id="${thematicSeries.id}" type="file" name="file" onchange="fileChange(this,'${thematicSeries.id}')"/>
            </form>
        </td>
        <td align="center"><input type="button" value="查看论题数据" onclick="checkThematicSeries('${thematicSeries.id}')" /></div>
        </td>
        <td align="center"><input type="button" value="修改论题信息" onclick="editThematicSeries('${thematicSeries.id}')" /></div>
        </td>
    </tr>
    </#list>
    </tbody>
</table>
</body>
</html>
