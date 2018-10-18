Date.prototype.Format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

//默认时间为本月
var currDate = new Date();
var currYear = currDate.getFullYear();
var currMonth = currDate.getMonth()+1;
var currDay = currDate.getDate();

function obtainDate(select) {
    var pairDate = {
            start:null,
            end:null
        }
    switch (select) {
    case '1':{//今天
        pairDate.start = currDate.Format("yyyy-MM-dd");
        pairDate.end = currDate.Format("yyyy-MM-dd");
        break;
    }
    case '2':{//最近7天
        pairDate.start = new Date(currDate.getTime()-7*86400000).Format('yyyy-MM-dd'); ;
        pairDate.end = currDate.Format("yyyy-MM-dd");
        break;
    }case '3':{//本月
        pairDate.start = new Date(currYear, currMonth-1,1).Format('yyyy-MM-dd');
        pairDate.end = currDate.Format("yyyy-MM-dd");
        break;
    }
    case '4':{//上月
        pairDate.start = new Date(currYear, currMonth-2,1).Format('yyyy-MM-dd');
        pairDate.end = new Date(new Date(currYear, currMonth-1,1).getTime()-86400000).Format('yyyy-MM-dd');
        break;
    }
    case '5':{//今年
        pairDate.start = new Date(currYear, 0, 1).Format("yyyy-MM-dd");
        pairDate.end = currDate.Format("yyyy-MM-dd");
        break;
    }
    case '6':{//去年
        pairDate.start = new Date(currYear-1, 0, 1).Format("yyyy-MM-dd");
        pairDate.end = new Date(currYear-1, 12, 31).Format("yyyy-MM-dd");
        break;
    }
    case '7':{//全部
        pairDate.start =null;
        pairDate.end = null;
        break;
    }
    default:
        pairDate.start = currDate.Format("yyyy-MM-dd");
        pairDate.end = currDate.Format("yyyy-MM-dd");
        break;
    }
    return pairDate;
}

function fenye(element, func, currentPage, pageSize, totalCount) {
    var totalPage = (totalCount % pageSize == 0) ? (totalCount / pageSize) : (Math.floor(totalCount / pageSize) + 1);
    $.jqPaginator(element, {
        first: '<li class="first"><a href="javascript:void(0);">首页</a></li>',
        prev: '<li class="prev"><a href="javascript:void(0);">上一页</a></li>',
        next: '<li class="next"><a href="javascript:void(0);">下一页</a></li>',
        last: '<li class="last"><a href="javascript:void(0);">最后一页</a></li>',
        page: '<li class="page"><a href="javascript:void(0);">{{page}}</a></li>',
        totalPages: totalPage,
        visiblePages: 3,
        currentPage: currentPage,
        onPageChange: function (num, type) {
            if(type !="init"){                          
                eval(func + "(" + num + ")");
            }
        }
    });
}
//自适应屏幕分辨率
function reFontSize(){
    $("html").css("fontSize",($(window).width()*100/1080) +"px");   
    //$("html").css("fontSize","100px");    
}

function add0(m){return m<10?'0'+m:m }
function format(shijianchuo){
    if (!shijianchuo) {
        return '--';
    }
    //shijianchuo是整数，否则要parseInt转换
    var time = new Date(shijianchuo);
    var y = time.getFullYear();
    var m = time.getMonth()+1;
    var d = time.getDate();
    var h = time.getHours();
    var mm = time.getMinutes();
    var s = time.getSeconds();
    return y+'-'+add0(m)+'-'+add0(d)+' '+add0(h)+':'+add0(mm)+':'+add0(s);
}

function checkTime() {
    var start = $("#startDate").val()
    var end = $("#endDate").val()
    if (!start || start == "") {
        return false;
    }
    if (!end || end == "") {
        return false;
    }
    if (Date.parse(start) > Date.parse(end)) {
        alertDialog("起始日期不能大于终止日期！", 0);
        return false;
    }
    return true;
}

//计算两个整数的百分比值 
function getPercent(num, total) { 
    num = parseFloat(num); 
    total = parseFloat(total); 
    if (isNaN(num) || isNaN(total)) { 
    return "-"; 
    } 
    return total <= 0 ? "0%" : (Math.round(num / total * 10000) / 100.00 + "%"); 
} 

//根据年月判断月份的最后一天
function getDaysInOneMonth(data){  
	year = data.substr(0,4);
	month = data.substr(5,2);
	month = parseInt(month, 10);  
	var d= new Date(year, month, 0);  
	return d.getDate();  
} 