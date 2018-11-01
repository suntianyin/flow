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

        $(function () {
            var pathurl = "index?";
            var totalPages = ${pages?c};
            var currentPages = ${pageNum?c};
            jqPaging(pathurl, totalPages, currentPages);
        });

        //编辑出版社元数据
        // function btn_edit(id) {
        //     var url = "/publisher/edit?id=" + id;
        //     openDialog(url, "updatePublisher", "编辑出版社数据", 500, 400, function (iframe) {
        //         top.frames[iframe].AcceptClick()
        //     });
        // }

        //出版社元数据查看
        function btn_detail(id) {
            loading();
            window.location.href = "publisherDetail?id=" + id;
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

        //检索
        function btn_Search() {
            var id = $("#id").val().trim();
            var title = $("#title").val().trim();
            var relatePublisherID = $("#relatePublisherID").val().trim();
            window.location.href = "index?id=" + id + "&title=" + title + "&relatePublisherID=" + relatePublisherID;
        }
        var title=$("#title");//定位到input框
        title.change(function(){
            console.log(title.val());
            alert(title.val())
            // this.query_search($n3.val());//query_search为模糊查询的方法
        })

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
        <!--列表-->
        <div id="grid_List">
            <div class="bottomline QueryArea" style="margin: 1px; margin-top: 0px; margin-bottom: 0px;">
                <table border="0" class="form-find" style="height: 45px;">
                    <tr>
                        <th>出版社id：</th>
                        <td>
                            <input id="id" type="text" value="${id! ''}" class="txt" style="width: 200px"/>
                        </td>

                        <th>出版社名称：</th>
                        <td>
                            <input id="title" type="text" value="${title! ''}" class="txt" style="width: 200px"/>
                        </td>

                        <th>关联出版社：</th>
                        <td>
                            <select id="relatePublisherID" name="relatePublisherID" class="txtselect">
                            <#--<#list RelatePublisherIDset as a>-->
                            <#--<option value="${RelatePublisherIDset[a_index]!''}">${RelatePublisherIDset[a_index]!''}</option>-->
                            <#--</#list>-->
                                <option value="" selected="selected"></option>
                                <#list map?keys as key>
                                    <option value="${key!''}">${map[key]!''}</option>
                                </#list>
                            </select>
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
                            <th>出版社id</th>
                            <th>关联出版社</th>
                            <th>isbn</th>
                            <th>出版社名称</th>
                            <th>名称类型</th>
                        <#--<th>开始使用时间</th>-->
                        <#--<th>结束使用时间</th>-->
                        <#--<th>注册国国籍</th>-->
                            <th>创办时间</th>
                            <th>出版社分类</th>
                        <#--<th>出版文献资源类型</th>-->
                        <#--<th>所属出版集团名称</th>-->
                        <#--<th>唯一标识</th>-->
                        <#--<th>社长姓名</th>-->
                        <#--<th>副社长姓名</th>-->
                        <#--<th>图书质量等级</th>-->
                        <#--<th>简介</th>-->
                        <#--<th>出版地</th>-->
                            <th>操作人</th>
                        <#--<th>创建时间</th>-->
                            <th>最后更新时间</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                    <#list publisherList as list>
                    <tr class="gradeA odd" role="row">
                        <td align="center">${(list.id)}</td>
                        <td align="center">${(list.relatePublisherID)! '' }</td>
                        <td align="center">${(list.isbn)! '' }</td>
                        <td align="center">${(list.title)! '' }</td>
                        <td align="center">${(list.titleType.getDesc())! '' }</td>
                    <#--<td align="center">${list.startDate! '' }</td>-->
                    <#--<td align="center">${list.endDate! '' }</td>-->
                    <#--<td>${list.nationalityCode! '' }</td>-->
                        <td align="center">${(list.founderDate)! '' }</td>
                        <td align="center">${(list.classCode.getDesc())! '' }</td>
                    <#--<td>${list.resourceType !''}</td>-->
                    <#--<td>${list.publishingGroup! '' }</td>-->
                    <#--<td>${list.publishingGroupID! '' }</td>-->
                    <#--<td>${list.president! '' }</td>-->
                    <#--<td>${list.vicePresident! '' }</td>-->
                    <#--<td>${list.qualityLevel! '' }</td>-->
                    <#--<td align="center">${list.summary! '' }</td>-->
                    <#--<td>${list.place! '' }</td>-->
                        <td align="center">${(list.operator)! '' }</td>
                    <#--<td>${(list.createTime?string("yyyy-MM-dd"))!'' }</td>-->
                        <td align="center">${(list.updateTime?string("yyyy-MM-dd"))!'' }</td>
                    <#--<td>-->
                    <#--<a href="javascript:void(0);" onclick="updatePublisher('${list.id}')">修改</a>-->
                    <#--</td>-->
                        <td align="center">
                            <a style="cursor:pointer;" onclick="btn_detail('${list.id! "" }');">查看&nbsp;</a>
                            <#--<a style="cursor:pointer;"-->
                               <#--onclick="btn_edit('${list.id! "" }');">编辑&nbsp;</a>-->
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
