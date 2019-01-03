function jqPage(pathurl, totalPages, currentPages, keywords, sortColumns) {
    $.jqPaginator('#pagination', {
        first: '<li class="first"><a href="javascript:void(0);">首页</a></li>',
        prev: '<li class="prev"><a href="javascript:void(0);">上一页</a></li>',
        next: '<li class="next"><a href="javascript:void(0);">下一页</a></li>',
        last: '<li class="last"><a href="javascript:void(0);">最后一页</a></li>',
        page: '<li class="page"><a href="javascript:void(0);">{{page}}</a></li>',
        totalPages: totalPages,
        visiblePages: 3,
        currentPage: currentPages,
        onPageChange: function (num, type) {
            if (type != "init") {
                window.location.href = pathurl + "?page=" + num + "&keywords=" + keywords + "&sortColumns=" + sortColumns;
            }
        }
    });
}

var loading = function () {
    $('.load-circle').show();
}

var close_loading = function () {
    //alert("关闭");
    $('.load-circle').empty();
}

function jqPaging(pathurl, totalPages, currentPages) {
    $.jqPaginator('#pagination', {
        first: '<li class="first"><a href="javascript:void(0);">首页</a></li>',
        prev: '<li class="prev"><a href="javascript:void(0);">上一页</a></li>',
        next: '<li class="next"><a href="javascript:void(0);">下一页</a></li>',
        last: '<li class="last"><a href="javascript:void(0);">最后一页</a></li>',
        page: '<li class="page"><a href="javascript:void(0);">{{page}}</a></li>',
        totalPages: totalPages,
        visiblePages: 5,
        currentPage: currentPages,
        onPageChange: function (num, type) {
            if (type != "init") {
                loading();
                window.location.href = pathurl + "&page=" + num + "&pageNumber=" + num;
            }
        }
    });
}