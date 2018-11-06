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
        var test_list;
        $(function () {
            $.ajax({
                url: "${ctx}/publisher/getRelatePublisherMap",
                dataType:'json',
                type: "get",
                contentType: "application/json;charset=utf-8",//缺失会出现URL编码，无法转成json对象
                async: false,
                success: function (data) {
                   console.log(data)
                    test_list = data;
                },
                error: function (data) {
                    alertDialog(data.responseText, -1);
                }
            });
            return test_list;
        })
        //测试用的数据，这里可以用AJAX获取服务器数据
        // var test_list1 = ["小张", "小苏", "小杨", "老张", "老苏", "老杨", "老爷爷", "小妹妹", "老奶奶", "大鹏", "大明", "大鹏展翅", "你好", "hello", "hi"];
        var old_value = "";
        var highlightindex = -1;   //高亮
        //自动完成
        function AutoComplete(auto, search, mylist) {
            if ($("#" + search).val() != old_value || old_value == "") {
                var autoNode = $("#" + auto);   //缓存对象（弹出框）
                var carlist = new Array();
                var carId= new Array();
                var n = 0;
                var m=0;
                old_value = $("#" + search).val();
                for (var i in mylist) {
                    // carId[m++]=mylist[i].fieldName;
                    if (mylist[i].metaValue.indexOf(old_value) >= 0) {
                        carlist[n++] = mylist[i].metaValue;

                    }
                }
                if (carlist.length == 0) {
                    autoNode.hide();
                    return;
                }
                autoNode.empty();  //清空上次的记录
                for (i in carlist) {
                    var wordNode = carlist[i];   //弹出框里的每一条内容
                    var newDivNode = $("<li>").attr("id", i);    //设置每个节点的id值
                    newDivNode.attr("style", "font:14px/25px arial;width: 200px;height:25px;padding:0 8px;cursor: pointer;background-color: white;");
                    newDivNode.html(wordNode).appendTo(autoNode);  //追加到弹出框
                    //鼠标移入高亮，移开不高亮
                    newDivNode.mouseover(function () {
                        if (highlightindex != -1) {        //原来高亮的节点要取消高亮（是-1就不需要了）
                            autoNode.children("li").eq(highlightindex).css("background-color", "white");
                        }
                        //记录新的高亮节点索引
                        highlightindex = $(this).attr("id");
                        // $(this).css("background-color", "#ebebeb");
                        $(this).css("background-color", "#2c9be2");
                    });
                    newDivNode.mouseout(function () {
                        $(this).css("background-color", "white");
                    });
                    //鼠标点击文字上屏
                    newDivNode.click(function () {
                        //取出高亮节点的文本内容
                        // var comText = autoNode.hide().children("li").eq(highlightindex).text();
                        var comText =$("#" + highlightindex).text();
                        highlightindex = -1;
                        // console.log(comText);
                        //文本框中的内容变成高亮节点的内容
                        $("#" + search).val(comText);
                        // $("#relatePublisherID").val($("#" + highlightindex).id);
                    })
                    if (carlist.length > 0) {    //如果返回值有内容就显示出来
                        autoNode.show();
                    } else {               //服务器端无内容返回 那么隐藏弹出框
                        autoNode.hide();
                        //弹出框隐藏的同时，高亮节点索引值也变成-1
                        highlightindex = -1;
                    }
                }
            }
            //点击页面隐藏自动补全提示框
            document.onclick = function (e) {
                var e = e ? e : window.event;
                var tar = e.srcElement || e.target;
                if (tar.id != search) {
                    if ($("#" + auto).is(":visible")) {
                        $("#" + auto).css("display", "none")
                    }
                }
            }
        }

        $(function () {
            old_value = $("#relatePublisherID").val();
            console.log(test_list)
            $("#relatePublisherID").focus(function () {
                if ($("#relatePublisherID").val() == "") {
                    AutoComplete("auto_div", "relatePublisherID", test_list);
                }
            });
            $("#relatePublisherID").keyup(function () {
                AutoComplete("auto_div", "relatePublisherID", test_list);
            });
        });
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
                <form autocomplete="off">
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

                            <#--<th>关联出版社：</th>-->
                            <#--<td>-->
                                <#--<select id="relatePublisherID" name="relatePublisherID" class="txtselect">-->
                                    <#--<option value="" selected="selected">请选择出版社名称</option>-->
                            <#--<#list map?keys as key>-->
                            <#--<option value="${key!''}">${map[key]!''}</option>-->
                            <#--</#list>-->
                                <#--</select>-->
                            <#--</td>-->
                            <th>关联出版社：</th>
                            <td>
                                <div class="search">
                                    <input type="text" id="relatePublisherID" class="txt" style="width: 200px" value="${relatePublisherID!''}"/>
                                    <ul id="auto_div" style="position: absolute;
	display: none;
	background: #fff;
	border: 1px #f7f7f7 solid;
	border-radius: 5px;
	width: 10%;
	margin: 0;
	padding: 0;
	color: #323232;
	font-size: 0.9rem;">
                                    </ul>
                                </div>
                            </td>
                            <td>
                                <input id="btnSearch" type="button" class="btnSearch" value="搜 索"
                                       onclick="btn_Search()"/>
                            </td>
                        </tr>
                    </table>
                </form>
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
