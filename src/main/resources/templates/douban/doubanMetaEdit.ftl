<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <#include "../common/metabootstraps.ftl">
    <script src="${ctx}/js/jsPage.js"></script>
    <script src="${ctx}/js/datepicker/WdatePicker.js"></script>

    <title>图书元数据</title>
    <script type="text/javascript">
        // 提交按钮
        function btn_save() {
            if (confirm("确认保存吗？") == true) {
                $("#dataForm").submit();
                alert("保存成功！");
                $(".load-circle").hide();
            }
        }

        // 返回按钮
        function btn_back() {
            window.location.href = "${ctx}/douban/searchIndex?doubanId=${doubanMetaMap['豆瓣Id']! ''}";
        }

    </script>
</head>
<body>
<div class='load-circle' style="display: none;"></div>
<form id="dataForm" action="updateDoubanMeta" method="post">
    <table id="table-list"
           class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline gridBody">
        <thead>
        <tr class="gradeA odd" role="row">
            <td>
                <div class="PartialButton">
                    <a href="javascript:;" title="返回" onclick="btn_back()" class="tools_btn"><span><i
                            class="fa fa-arrow-left"></i>&nbsp;返回</span></a>
                </div>
            </td>
            <td>
                <div class="PartialButton">
                    <a href="javascript:;" title="保存" onclick="btn_save()"
                       class="tools_btn"><span><i class="fa fa-plus"></i>&nbsp;保存</span></a>
                </div>
            </td>
        </tr>
        <tr role="row">
            <th>字段名</th>
            <th>字段值</th>
        </tr>
        </thead>
        <tbody>
        <#list doubanMetaMap?keys as key>
            <tr class="gradeA odd" role="row">
            <#if key == '豆瓣Id'|| key == '创建时间' || key == '更新时间' || key == '是否已经抓取'>
                <#if key == '创建时间' || key == '更新时间'>
                    <td>${key}</td>
                    <td>
                        <input hidden="hidden" type="text" name="${key}" value="${doubanMetaMap[key]! ''}">
                        ${doubanMetaMap[key]! ''}
                    </td>
                <#else>
                    <td>${key}</td>
                    <td>
                        <input hidden="hidden" type="text" name="${key}" value="${doubanMetaMap[key]! ''}">
                        ${doubanMetaMap[key]! ''}
                    </td>
                </#if>
            <#elseif key == '内容简介' || key == '作者简介'>
                <td>${key}</td>
                <td>
                    <textarea rows="10" cols="80" name="${key}">${doubanMetaMap[key]! ''}</textarea>
                </td>
            <#else >
                <td>${key}</td>
                <td><input type="text" name="${key}" value="${doubanMetaMap[key]! ''}"></td>
            </#if>
            </tr>
        </#list>
        <tr class="gradeA odd" role="row">
            <td>
                <div class="PartialButton">
                    <a href="javascript:;" title="返回主页" onclick="btn_back()" class="tools_btn"><span><i
                            class="fa fa-arrow-left"></i>&nbsp;返回</span></a>
                </div>
            </td>
            <td>
                <div class="PartialButton">
                    <a href="javascript:;" title="保存数据" onclick="btn_save()"
                       class="tools_btn"><span><i class="fa fa-plus"></i>&nbsp;保存</span></a>
                </div>
            </td>
        </tr>
        </tbody>
    </table>
</form>
</body>
</html>
