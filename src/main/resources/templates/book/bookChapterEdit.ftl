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
    <style>
        html {
            overflow-y: hidden;
        }

        .ke-icon-save {
            background-image: url("${ctx}/editor/themes/default/save.png") !important;
            background-size: 31px 31px;
            width: 30px;
            height: 30px;
            text-align: center;
        }

        .ke-icon-source {
            background-image: url("${ctx}/editor/themes/default/html.png") !important;
            background-size: 30px 30px;
            width: 30px !important;
            height: 30px !important;
        }

        .ke-icon-fontsize {
            background-position: 0px, 0px !important;
            background-image: url("${ctx}/editor/themes/default/font.png") !important;
            background-size: 23px 23px;
            width: 30px !important;
            height: 30px !important;
        }

        .ke-icon-selectall {
            background-position: 0px, 0px !important;
            background-image: url("${ctx}/editor/themes/default/all.png") !important;
            background-size: 21px 21px;
            width: 30px !important;
            height: 30px !important;
        }

    </style>
</head>
<body onload="initChapter()">
<div class='load-circle'></div>
<div class="cover">
    <div id="page-wrapper" style="top:3%;">
        <div class="panel panel-default">
            <div class="panel-heading" style="text-align: center; font-size: 18px;height: 40px;">
                <span id="title" style="font-size: larger"></span>
                </br>
                <span id="bookChapterCheck" style="color:red;display: none">该书流式内容存在问题，请联系管理员</span>
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
                    <input id="chapterComid" type="hidden"/>
                    <input id="chapterNum" type="hidden"/>
                    <input id="wordSum" type="hidden"/>
                    <textarea id="htmlEditor" style="width:99.9%; height: 725%;resize: none;">

                    </textarea>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script type="text/javascript">
    //编辑器
    var htmlEditor;

    //目录树
    var zTreeObj;

    //加载目录和首页
    function initChapter() {
        //加载目录树
        initCataTree();
        //加载图书首页
        initFirstPage();
        $('.load-circle').hide();
    }

    //加载编辑器
    $(function () {
        initEditor();
    })

    //加载图书首页
    function initFirstPage() {
        var url = RootPath() + "/book/bookChapterEditCover?metaid=" + '${metaId}'
        $.ajax({
            url: url,
            type: "get",
            dataType: "json",
            async: false,
            success: function (data) {
                if (data != null) {
                    $('#bookChapterCheck').hide();
                    $('#htmlEditor').text(data.content);
                    $('#chapterComid').val(data.comId);
                    $('#chapterNum').val(data.chapterNum);
                    $('#wordSum').val(data.wordSum);
                }
            },
            error: function (data) {
                $('#bookChapterCheck').show();
                $('#htmlEditor').text('暂无');
            }
        });
    }

    //初始化编辑器
    function initEditor() {
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

        var url = RootPath() + "/book/bookChapterEditMeta?metaid=" + '${metaId}'
        $.ajax({
            url: url,
            type: "get",
            dataType: "json",
            async: false,
            success: function (data) {
                if (data != null) {
                <#--var cssUrl = '${bookMetaVo.cssUrl?replace('\n','')}'.substring(12);-->
                    var cssUrl = data.cssUrl.replace('\n', '').substring(12);
                    KindEditor.ready(function (K) {
                        htmlEditor = K.create('#htmlEditor', {
                            cssPath: cssUrl,
                            cssData: 'body {font-size:16px;}',
                            items: ['save', 'source', 'fontsize', 'selectall']
                        });
                    });
                    //加载书名
                    $("#title").text(data.title);
                } else if (data == null) {
                    Loading(false);
                    alertDialog("获取失败，联系管理员！", -1);
                }
            },
            error: function (data) {
                Loading(false);
                alertDialog("获取失败，联系管理员！", -1);
            }
        });
    }

    //初始化目录树
    function initCataTree() {

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
        var zNodes;
        var url = RootPath() + "/book/bookChapterEditCata?metaid=" + '${metaId}'
        $.ajax({
            url: url,
            type: "get",
            dataType: "text",
            async: false,
            success: function (data) {
                if (data != null || data != '') {
                    zNodes = eval('(' + data + ')');
                    $(document).ready(function () {
                        zTreeObj = $.fn.zTree.init($("#treeCata"), setting, zNodes);
                    });
                } else if (data == "error") {
                    alertDialog("获取失败，联系管理员！", -1);
                }
            },
            error: function (data) {
                Loading(false);
                alertDialog("获取失败，联系管理员！", -1);
            }
        });

    }

    //获取章节内容
    function getChapter(metaid, chapterNum) {
        if (metaid != null && chapterNum != null) {
            var url = RootPath() + "/book/getBookChapter?metaid=" + metaid + "&chapterNum=" + chapterNum;
            $.ajax({
                url: url,
                type: "get",
                dataType: "json",
                async: false,
                success: function (data) {
                    htmlEditor.html("");
                    if (data.body != null) {
                        $('#chapterComid').val(data.body.comId);
                        $('#wordSum').val(data.body.wordSum);
                        $('#chapterNum').val(data.body.chapterNum);
                        htmlEditor.html(data.body.content);
                    }else {
                        $('#bookChapterCheck').show();
                        htmlEditor.html('暂无');
                    }
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
        var metaId = '${metaId}';
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
                } else {
                    tipDialog("更新失败，联系管理员！", 3, -1);
                }
            },
            error: function (data) {
                Loading(false);
                alertDialog("更新失败，联系管理员！", -1);
            }
        });
    }

</script>
</html>