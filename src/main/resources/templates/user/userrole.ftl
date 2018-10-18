<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width" />
    <title>用户管理-用户角色</title>
    <#include "../common/meta.ftl">
    <script type="text/javascript">
        $(document).ready(function () {
            Loadlayout();
        });
    </script>
</head>
<body>
<div>        
<script>

    $(function () {
        GetList();
    });
    //加载角色
    function GetList() {

        //模糊查询角色（注：这个方法是利用jquery查询）
        $("#txt_Search").keyup(function () {
            if ($(this).val() != "") {
                var Search = $(this).val();
                window.setTimeout(function () {
                    $(".sys_spec_text li")
                     .hide()
                     .filter(":contains('" + (Search) + "')")
                     .show();
                }, 100);
            } else {
                $(".sys_spec_text li").show();
            }
        }).keyup();
    }

</script>
<div class="note-prompt" style="margin: 1px;">
    温馨提示：列出了该用户所具有的所有角色信息。    
</div>
<div class="border" style="margin: 1px;">
    <div class="btnbartitle">
        <div style="float: left">角色查询：
            <input id="txt_Search" type="text" class="btnbartitleinput" style="width: 120px;" />
        </div>
    </div>
    <div class="ScrollBar" id="RoleList" style="height: 275px;">
        <ul class="sys_spec_text">  
            <#list listthis as listthis>
                <li title="${listthis.name !''}" class="selected"><a id="${listthis.id !''}"><img src="${ctx}/static/images/role.png">${listthis.name !''}</a><i></i></li>
            </#list>
        </ul>
    </div>
</div>
</div>
</body>
</html>
