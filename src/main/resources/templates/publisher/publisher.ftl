<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>出版社数据</title>
    <script type="text/javascript">

//                function updatePublisher(id) {
//        //            console.log("********************")
//                    window.location.href = "edit?id="+id;
//                }

        $(function(){
            var pathurl = "?page=";
            var totalPages = ${pages!''};
            var currentPages = ${pageNum!''};
            jqPaging(pathurl,totalPages,currentPages);
        });

        //编辑图书元数据
        function updatePublisher(id) {
            var url = "/publisher/edit?id=" + id;
            openDialog(url, "updatePublisher", "编辑出版社数据", 500, 400, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

//        function btn_addPublisher() {
//            window.location.href = "save";
//        }

        function btn_addPublisher() {
            var url = "/publisher/save";
            openDialog(url, "addPublisher", "编辑出版社数据", 500, 400, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

    </script>
</head>
<body>
<div id="layout" class="layout">
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>出版社信息<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a id="lr-add" title="新增" onclick="btn_addPublisher()" class="tools_btn"><span><i
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
                        <th>出版社id</th>
                        <th>关联出版社</th>
                        <th>isbn</th>
                        <th>出版社名称</th>
                        <th>名称类型</th>
                        <th>开始使用时间</th>
                        <th>结束使用时间</th>
                        <th>注册国国籍</th>
                        <th>创办时间</th>
                        <th>出版社分类</th>
                        <th>出版文献资源类型</th>
                        <th>所属出版集团名称</th>
                        <#--<th>唯一标识</th>-->
                        <th>社长姓名</th>
                        <th>副社长姓名</th>
                        <th>图书质量等级</th>
                        <th>简介</th>
                        <th>出版地</th>
                        <th>操作人</th>
                        <th>创建时间</th>
                        <th>最后更新时间</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list publisherList as list>
                    <tr class="gradeA odd" role="row">
                        <td>${list.id}</td>
                        <td>${list.relatePublisherID! '' }</td>
                        <td>${list.isbn! '' }</td>
                        <td>${list.title! '' }</td>
                        <td>${list.titleType! '' }</td>
                        <td>${list.startDate! '' }</td>
                        <td>${list.endDate! '' }</td>
                        <td>${list.nationalityCode! '' }</td>
                        <td>${list.founderDate! '' }</td>
                        <td>${list.classCode! '' }</td>
                        <td>${list.resourceType !''}</td>
                        <td>${list.publishingGroup! '' }</td>
                        <#--<td>${list.publishingGroupID! '' }</td>-->
                        <td>${list.president! '' }</td>
                        <td>${list.vicePresident! '' }</td>
                        <td>${list.qualityLevel! '' }</td>
                        <td>${list.summary! '' }</td>
                        <td>${list.place! '' }</td>
                        <td>${list.operator! '' }</td>
                        <td>${(list.createTime?string("yyyy-MM-dd"))!'' }</td>
                        <td>${(list.updateTime?string("yyyy-MM-dd"))!'' }</td>
                        <td>
                            <a href="javascript:void(0);" onclick="updatePublisher('${list.id}')">修改</a>
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
