<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE9"/>
    <title>流式图书服务平台</title>
    <#include "common/meta.ftl">
    <script type="text/javascript">
        $(document).ready(function () {
            ServerCurrentTime();
            GetAccordionMenu();
            InitializeImpact();

            AddTabMenu('Imain', '/hello', '欢迎首页', "fa fa-home", 'true');
        });

        //初始化界面UI效果
        function InitializeImpact() {
            //设置自应高度
            resizeU();
            $(window).resize(resizeU);

            function resizeU() {
                var divkuangH = $(window).height();
                $(".mainPannel").height(divkuangH - 130);
                $(".navigation").height(divkuangH - 130);
                $("#ContentPannel").height(divkuangH - 130);
            }

            //手风琴效果
            var Accordion = function (el, multiple) {
                this.el = el || {};
                this.multiple = multiple || false;
                var links = this.el.find('.link');
                links.on('click', {el: this.el, multiple: this.multiple}, this.dropdown);
            }
            Accordion.prototype.dropdown = function (e) {
                //计算高度
                var accordionheight = ($("#accordion").children("ul li").length * 36);
                //var navigationheight = $(".navigation").height()
                //$('#accordion li').children('.b-children').height(navigationheight - accordionheight - 1);
                $(this).next().slideToggle();
                $(this).parent().toggleClass('open');
                if (!e.data.multiple) {
                    $(this).parent().parent().find('.submenu').not($(this).next()).slideUp().parent().removeClass('open');
                }
                ;
            }
            $(".submenu a").click(function () {
                $('.submenu a').removeClass('action');
                $(this).addClass('action');
            })
            var accordion = new Accordion($('#accordion'), false);
            $("#accordion li:first").find('div').trigger("click");//默认第一个展开
        }

        //导航一级菜单
        var accordionJson = "";

        function GetAccordionMenu() {
            var html = "";
            //模拟数据
            var data = [
                {
                    ModuleId: "M1",
                    ParentId: "M0",
                    FullName: "图书",
                    Location: ""
                },
                {
                    ModuleId: "M7",
                    ParentId: "M0",
                    FullName: "出版社",
                    Location: ""
                },
                {
                    ModuleId: "M2",
                    ParentId: "M0",
                    FullName: "云加工",
                    Location: ""
                },
                {
                    ModuleId: "R1",
                    ParentId: "M1",
                    FullName: "图书元数据",
                    Location: "/book/bookMeta"
                },
                {
                    ModuleId: "R2",
                    ParentId: "M1",
                    FullName: "根据ISBN查询生成元数据",
                    Location: "/douban/index"
                },
                {
                    ModuleId: "R3",
                    ParentId: "M7",
                    FullName: "出版社数据",
                    Location: "/publisher/"
                },
                {
                    ModuleId: "R4",
                    ParentId: "M1",
                    FullName: "流式分页",
                    Location: "/book/bookPage"
                },
                {
                    ModuleId: "R5",
                    ParentId: "M1",
                    FullName: "数据发布",
                    Location: "/publish/homeIndex"
                },
                {
                    ModuleId: "R7",
                    ParentId: "M1",
                    FullName: "论题信息展示",
                    Location: "/thematic/thematicInfoDisplay"
                },
                {
                    ModuleId: "R8",
                    ParentId: "M1",
                    FullName: "ISBN比对元数据库",
                    Location: "/isbn/isbnSearchIndex"
                },
                {
                    ModuleId: "R9",
                    ParentId: "M1",
                    FullName: "作者",
                    Location: "/author/index"
                },
                {
                    ModuleId: "R10",
                    ParentId: "M2",
                    FullName: "批次管理（管理员）",
                    Location: "/processing/batch/index"
                }
            ];
        <#--data = ${data}-->
            accordionJson = data;
            $.each(accordionJson, function (i) {
                if (accordionJson[i].ParentId == 'M0') {
//                    html += "<li title=" + accordionJson[i].FullName +" "+ "onclick=\"AddTabMenu('" + accordionJson[i].ModuleId + "', '" + accordionJson[i].Location + "', '" + accordionJson[i].FullName + "',  '','true');\">";
                    html += "<li title=" + accordionJson[i].FullName + ">";
                    html += "<div class=\"link\">";
                    html += "<span>" + accordionJson[i].FullName + "</span><i class=\"chevron-down\"></i>";
                    html += "</div>";
                    html += GetSubmenu(accordionJson[i].ModuleId, "b-children");
                    html += "</li>";
                }
            });
            $("#accordion").append(html);
        }

        //导航子菜单
        function GetSubmenu(ModuleId, _class) {
            var submenu = "<ul class=\"submenu " + _class + "\">";
            $.each(accordionJson, function (i) {
                if (accordionJson[i].ParentId == ModuleId) {
                    if (IsBelowMenu(accordionJson[i].ModuleId) > 0) {
                        submenu += "<li title=" + accordionJson[i].FullName + "><a class=\"link\"><span>" + accordionJson[i].FullName + "</span><i class=\"submenu-chevron-down\"></i></a>";
                        submenu += GetSubmenu(accordionJson[i].ModuleId, "c-children")
                        submenu += "</li>";
                    } else {
                        submenu += "<li title=" + accordionJson[i].FullName + " onclick=\"AddTabMenu('" + accordionJson[i].ModuleId + "', '" + accordionJson[i].Location + "', '" + accordionJson[i].FullName + "',  '','true');\"><a><span>" + accordionJson[i].FullName + "</span></a></li>";
                    }
                }
            });
            submenu += "</ul>";
            return submenu;
        }

        //判断是否有子节点
        function IsBelowMenu(ModuleId) {
            var count = 0;
            $.each(accordionJson, function (i) {
                if (accordionJson[i].ParentId == ModuleId) {
                    count++;
                    return false;
                }
            });
            return count;
        }
    </script>
</head>
<body>
<!-- header -->
<div class="header">
    <div class="logo fleft" style="font-size:32px;color:#fff;">流式图书服务平台
    </div>
    <div id="Headermenu">
        <ul id="topnav">
            <li id="metnav_1" class="list">
                <a id="nav_1" onclick="Replace();">
                    <span class="c1"></span>系统首页
                </a>
            </li>
            <li id="metnav_2" class="list">
                <a id="nav_2" onclick="SkinIndex();">
                    <span class="c2"></span>切换皮肤
                </a>
            </li>
            <li id="metnav_3" class="list">
                <a id="nav_3">
                    <span class="c3"></span>
                </a>
            </li>
            <li id="metnav_4" class="list" onclick="IndexOut();">
                <a id="nav_4">
                    <span class="c4"></span>安全退出
                </a>
            </li>
        </ul>
    </div>
</div>
<div class="taskbarTabs">
    <div id="navigationtitle">
        <div id="CurrentTime" style="float: left; padding-left: 12px;"></div>
    </div>
    <div style="float: left">
        <div id="dww-menu" class="mod-tab">
            <div class="mod-hd">
                <ul id="tabs_container" class="tab-nav"></ul>
            </div>
            <input id="ModuleId" type="hidden"/>
        </div>
    </div>
</div>
<div class="mainPannel">
    <!-- 左边树状结构 -->
    <div class="navigation">
        <ul id="accordion" class="accordion">
        </ul>
    </div>
    <div id="overlay_navigation"></div>
    <!-- 右边内容部分 -->
    <div id="ContentPannel"></div>
</div>
<!--底部版权申明-->
<div id="footer" class="cs-south" style="height: 25px;">
    <div class="number" style="width: 30%; text-align: left; float: left; line-height: 25px;">
        &nbsp;技术支持：<a href="http://www.apabi.com" target="_blank" style="color: white;">北京方正阿帕比技术有限公司</a>
    </div>
    <div class="number" style="width: 40%; text-align: center; float: left; line-height: 25px;">
        CopyRight © 2017 - 2017 By Apabi
    </div>
    <div class="number" style="width: 10%; text-align: center; float: right; line-height: 25px;">
        版本号1.0
    </div>
    <div class="clear"></div>
</div>

</body>
</html>

