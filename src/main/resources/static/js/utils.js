//序列化表单
(function($){  
    $.fn.serializeJson=function(){  
        var serializeObj={};  
        var array=this.serializeArray();  
        var str=this.serialize();  
        $(array).each(function(){  
            if(serializeObj[this.name]){  
                if($.isArray(serializeObj[this.name])){  
                    serializeObj[this.name].push(this.value);  
                }else{  
                    serializeObj[this.name]=[serializeObj[this.name],this.value];  
                }  
            }else{  
                serializeObj[this.name]=this.value;   
            }  
        });  
        return serializeObj;  
    };  
})(jQuery);


function jqPagination(pathurl, totalCount, currentPages, pageSize) {
    var totalPages = (totalCount % pageSize == 0) ? (totalCount / pageSize) : (Math.floor(totalCount / pageSize) + 1);
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
                loading();
                window.location.href = pathurl + "&pageNumber=" + num + "&pageSize=" + pageSize;
            }
        }
    });
}