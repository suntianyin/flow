<html xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
     <#include "../common/metabootstraps.ftl">
    <title>编辑图书内容</title>
    <script charset="utf-8" src="${ctx}/editor/kindeditor-all.js"></script>
    <script charset="utf-8" src="${ctx}/editor/lang/zh-CN.js"></script>
    <script charset="utf-8" src="${ctx}/js/zTree3/js/jquery.ztree.all.js"></script>
    <link rel="stylesheet" href="${ctx}/js/zTree3/css/zTreeStyle/zTreeStyle.css" type="text/css">
    <script>
        KindEditor.plugin('save', function (K) {
            var editor = this, name = 'save';
            // 点击图标时执行
            editor.clickToolbar(name, function () {
                //alert($("#chapterComid").val());
                var url = RootPath() + "/book/bookChapterSave";
                var comId = $("#chapterComid").val();
                var content = htmlEditor.html();
                var chapterNum = $("#chapterNum").val();
                $.ajax({
                    url: url,
                    type: "POST",
                    dataType: "text",
                    data: {comId: comId, content: content, chapterNum: chapterNum},
                    success: function (data) {
                        if (data == "success") {
                            tipDialog("保存成功！", 3, 1);
                            //top.frames[tabiframeId()].location.reload();
                            closeDialog();
                        }
                    },
                    error: function (data) {
                        Loading(false);
                        alertDialog("更新失败，联系管理员！", -1);
                    }
                });
            });
        });
        KindEditor.lang({
            save: '保存'
        });
        KindEditor.options.filterMode = false;
        var cssUrl = '';
        <#if bookMetaVo.cssUrl??>
            cssUrl = '${bookMetaVo.cssUrl?replace('\n','')}'.substring(12);
        </#if>
        KindEditor.ready(function (K) {
            /*window.editor = K.create('#htmlEditor');*/
            htmlEditor = K.create('#htmlEditor', {
                cssPath: cssUrl,
                cssData: 'body {font-size:16px;}',
                items: ["save", "source"]
            });
        });

        //目录树
        var zTreeObj;

        function zTreeOnClick(event, treeId, treeNode) {
            getChapter('${metaId}', treeNode.nodeId);
        };
        // zTree 的参数配置
        var setting = {
            edit: {
                enable: true,
                showRenameBtn: true,
                showRemoveBtn: false
            },
            callback: {
                onClick: zTreeOnClick
            }
        };
        // zTree 的数据属性
        var zNodes = eval('(' + '${cataRows}' + ')');
        $(document).ready(function () {
            zTreeObj = $.fn.zTree.init($("#treeCata"), setting, zNodes);
        });

        //获取章节内容
        function getChapter(metaid, chapterNum) {
            if (metaid != null && chapterNum != null) {
                var url = RootPath() + "/book/getBookChapter?metaid=" + metaid + "&chapterNum=" + chapterNum;
                $.ajax({
                    url: url,
                    type: "get",
                    dataType: "text",
                    async: false,
                    success: function (data) {
                        Loading(false);
                        var obj = JSON.parse(data);
                        //htmlEditor.sync();
                        htmlEditor.html("");
                        htmlEditor.html(obj.body.content);
                        $('#chapterComid').val(obj.body.comId);
                        $('#wordSum').val(obj.body.wordSum);
                        $('#chapterNum').val(obj.body.chapterNum);
                    },
                    error: function (data) {
                        Loading(false);
                        alertDialog("获取失败，联系管理员！", -1);
                    }
                });
            }
        }

        //编辑目录
        function editCatalog() {
            $('.cata-input').toggle();
            $('.cata-a').toggle();
        }

        //更新目录
        function updateCata() {
            var url = RootPath() + "/book/cataTreeUpdate";
            var metaId = '${bookMetaVo.metaId}';
            var catalogArr = JSON.stringify(zTreeObj.getNodes());
            $.ajax({
                url: url,
                type: "POST",
                dataType: "text",
                data: {catalogArr: catalogArr, metaId: metaId},
                success: function (data) {
                    if (data == "success") {
                        tipDialog("保存成功！", 3, 1);
                        top.frames[tabiframeId()].location.reload();
                        closeDialog();
                    }else{
                        tipDialog("更新失败，联系管理员！", 3, -1);
                    }
                },
                error: function (data) {
                    Loading(false);
                    alertDialog("更新失败，联系管理员！", -1);
                }
            });
        }

        //保存目录
        function updateCatalog() {
            var url = RootPath() + "/book/catalogUpdate";
            var metaId = '${bookMetaVo.metaId}';
            var catalogArr = '';
            var index = 0;
            $("input[name='chapterName'], input[name='chapterNum']").each(function () {
                if (index % 2 == 0) {
                    catalogArr += $(this).val() + ",";
                } else {
                    catalogArr += $(this).val() + ";";
                }
                index++;
            });
            $.ajax({
                url: url,
                type: "POST",
                dataType: "text",
                data: {catalogArr: catalogArr, metaId: metaId},
                success: function (data) {
                    if (data == "success") {
                        tipDialog("保存成功！", 3, 1);
                        top.frames[tabiframeId()].location.reload();
                        closeDialog();
                    }

                },
                error: function (data) {
                    Loading(false);
                    alertDialog("更新失败，联系管理员！", -1);
                }
            });
        }

    </script>
    <style>
        html {
            overflow-y: hidden;
        }

        .ke-icon-save {
            background-image: url("${ctx}/editor/themes/default/save.png") !important;
            background-size: 30px 30px;
            width: 30px;
            height: 30px;
        }

        .ke-icon-source {
            background-image: url("${ctx}/editor/themes/default/html.png") !important;
            background-size: 30px 30px;
            width: 30px !important;
            height: 30px !important;
        }
    </style>
</head>
<body>
<div class="cover">
    <div id="page-wrapper" style="top:3%;">
        <div class="panel panel-default">
            <div class="panel-heading" style="text-align: center; font-size: 18px;height: 35px;">
                <span style="font-size: larger">${bookMetaVo.title!''}</span>
            </div>
            <div class="panel-body">
                <div style=" float:left;width: 20%;height: 90%;border: #2d2625 solid 1px;">
                    <span style="height:3%;background: #c9c9c9;display: block;padding: 15px;text-align: center;font-size: 20;">目录</span>
                    <span style="height:2%;border-top:1px solid #2d2625;display: block;padding: 15px;text-align: center;">
                        <a style="cursor:pointer;font-size: 15px;"
                           onclick="updateCata()">保存</a>
                    </span>
                    <div id="catalog" style="overflow:auto;height:85%;border-top: #2d2625 solid 1px;">
                        <ul id="treeCata" class="ztree"></ul>
                    </div>
                </div>
                <div style=" float:left;width: 78%;height: 90%;border: #2d2625 solid 1px;">
                    <#if bookChapter??>
                        <input id="chapterComid" value="${bookChapter.comId}" type="hidden"/>
                        <input id="chapterNum" value="${bookChapter.chapterNum}" type="hidden"/>
                        <input id="wordSum" value="${bookChapter.wordSum}" type="hidden"/>
                        <textarea id="htmlEditor" style="width:99.9%; height: 725px;resize: none;">
                            ${bookChapter.content}
                        </textarea>
                    </#if>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>