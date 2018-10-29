<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>
    <title>视频数据</title>
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

        //编辑视频数据
        function updateVideoMeta(id) {
            var url = "/videoMeta/edit?id=" + id;
            openDialog(url, "updateVideoMeta", "编辑视频数据", 500, 400, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

        //显示详情
        function showVideoMeta(id) {
            var url = "/videoMeta/videoMetaShow?id=" + id;
            AddTabMenu('videoMetaShow', url, '视频数据详情', null, 'true', 'true');
        }
        function isNull(str) {
            if (str == undefined || str == null || $.trim(str) == '') {
                return true;
            }
            return false;
        };
        function deleteVideoMeta(id) {
            if (isNull(id)){
                tipDialog("数据异常！", 3, -1);
                return;
            }

            var note = "注：您确定要删除当前视频信息？";
            if (!isNull(id)){
                note = "注：您确定要删除 编号为：" + id + " 的视频信息？";
            }
            var url = "/videoMeta/removeVideoMeta?id=" + id;
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
        //        function btn_addPublisher() {
        //            window.location.href = "save";
        //        }

        function btn_addVideoMeta() {
            var url = "/videoMeta/save";
            openDialog(url, "addVideoMeta", "添加视频数据", 500, 400, function (iframe) {
                top.frames[iframe].AcceptClick()
            });
        }

    </script>
</head>
<body>
<div id="layout" class="layout">
    <div class="layoutPanel layout-center">
        <div class="btnbartitle">
            <div>视频信息<span id="CenterTitle"></span></div>
        </div>
        <!--工具栏-->
        <div class="tools_bar" style="border-top: none; margin-bottom: 0px;">
            <div class="PartialButton">
                <a id="lr-add" title="新增" onclick="btn_addVideoMeta()" class="tools_btn"><span><i
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
                        <th>视频编号</th>
                        <th>中文名称</th>
                        <th>英文名称</th>
                        <th>所属系列</th>
                        <th>关键词</th>
                        <th>主要人物</th>
                        <th>内容描述（类似剧情摘要）</th>
                        <th>责任者</th>
                        <th>分类</th>
                        <th>标签</th>
                        <th>来源</th>
                        <th>场景</th>
                        <th>适应年龄段（或人群）</th>
                        <th>方言</th>
                        <th>配音语种</th>
                        <#--<th>字幕语种</th>-->
                        <#--<th>编码格式</th>-->
                        <#--<th>版权方</th>-->
                        <#--<th>授权期限</th>-->
                        <#--<th>视频格式</th>-->
                        <#--<th>视频时长</th>-->
                        <#--<th>评分</th>-->
                        <#--<th>出版国家</th>-->
                        <#--<th>出版公司</th>-->
                        <#--<th>出版日期</th>-->
                        <#--<th>大小</th>-->
                        <#--<th>色彩</th>-->
                        <#--<th>制作日期</th>-->
                        <#--<th>存放路径</th>-->
                        <#--<th>预览图路径</th>-->
                        <#--<th>播放路径</th>-->
                        <#--<th>其他信息</th>-->
                        <th>记录创建时间</th>
                        <th>记录创建人</th>
                        <th>记录修改时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list videoMetaList as list>
                    <tr class="gradeA odd" role="row">
                        <td>${list.id}</td>
                        <td>${list.chTitle! '' }</td>
                        <td>${list.enTitle! '' }</td>
                        <td>${list.series! '' }</td>
                        <td>${list.keyWord! '' }</td>
                        <td>${list.human! '' }</td>
                        <td>${list.description! '' }</td>
                        <td>${list.creator! '' }</td>
                        <td>${list.subject! '' }</td>
                        <td>${list.label! '' }</td>
                        <td>${list.source !''}</td>
                        <td>${list.scene! '' }</td>
                        <td>${list.userAge! '' }</td>
                        <td>${list.dialect! '' }</td>
                        <td>${list.dialogueLanguage! '' }</td>
                        <#--<td>${list.captionLanguages! '' }</td>-->
                        <#--<td>${list.codeFormat! '' }</td>-->
                        <#--<td>${list.rightOwner! '' }</td>-->
                        <#--<td>${(list.authorizationPeriod?string("yyyy-MM-dd"))! '' }</td>-->
                        <#--<td>${list.type! '' }</td>-->
                        <#--<td>${list.videoTime! '' }</td>-->
                        <#--<td>${list.score! '' }</td>-->
                        <#--<td>${list.publicationCountry! '' }</td>-->
                        <#--<td>${list.publicationCompany! '' }</td>-->
                        <#--<td>${(list.publicationYear?string("yyyy-MM-dd"))! '' }</td>-->
                        <#--<td>${list.videoSize! '' }</td>-->
                        <#--<td>${list.color! '' }</td>-->
                        <#--<td>${(list.createTime?string("yyyy-MM-dd"))! '' }</td>-->
                        <#--<td>${list.savePath! '' }</td>-->
                        <#--<td>${list.previewPath! '' }</td>-->
                        <#--<td>${list.playPath! '' }</td>-->
                        <#--<td>${list.note! '' }</td>-->
                        <td>${(list.insertTime?string("yyyy-MM-dd"))!'' }</td>
                        <td>${list.operator! '' }</td>
                        <td>${(list.updateTime?string("yyyy-MM-dd"))!'' }</td>
                        <td>
                            <a href="javascript:void(0);" onclick="showVideoMeta('${list.id}')">详情</a>
                            <a href="javascript:void(0);" onclick="updateVideoMeta('${list.id}')">修改</a>
                            <a href="javascript:void(0);" onclick="deleteVideoMeta('${list.id}')">删除</a>
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
