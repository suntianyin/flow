function topicRankList(){
    $.get('topicranking',{},function(result){
        $("#ranking_list").empty();
        var rows = result.data;
        var per = [];
        rows.map(function(one, index) {
            var count = index + 1;
            per.push("<section class='box'>");
            per.push("<section class='col_1' title='" + count + "'>" + count + "</section>");
            per.push("<section class='col_2'>" + one.name + "</section>");
            per.push("<section class='col_3'>" + one.allsum + "</section>");
            per.push("</section>");
        });
        $("#ranking_list").append(per.join(''));
    },'json');
}

var topicBar = echarts.init(document.getElementById('topic-bar'));
function topicEcharts() {
    $.get("topicranking",
            {},
            function(result) {
                var xAxisData = [];
                var seriesDate = [];
                result.data.map(function(one, index){
                    xAxisData.push(one.topicName||'--');
                    seriesDate.push(one.clickAll);
                });
                
                topicOption = {
                        title: {
                            text: '专题点击量top20',
                            left: 'center'
                        },
                        color: ['#9c9aff'],
                        tooltip : {
                            trigger: 'axis',
                            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                            },
                            formatter: '《{b}》<br/>点击量：{c}'
                        },
                        grid: {
                            left: '3%',
                            right: '4%',
                            bottom: '3%',
                            containLabel: true
                        },
                        xAxis : [
                            {
                                type : 'category',
                                data : xAxisData,
                                axisLabel: {
                                    show: false
                                }
                            }
                        ],
                        yAxis : [
                            {
                                type : 'value'
                            }
                        ],
                        series : [
                            {
                                type:'bar',
                                barWidth: '60%',
                                data:seriesDate,
                                label: {
                                    normal: {
                                        show: true,
                                        position: 'top',
                                        rotate : 45,
                                        formatter:function (params) {
                                            var name = params.name;
                                            return name.length > 10 ? (name.slice(0, 9)+'...') : name
                                        },
                                        textStyle: {                                    
                                            color: '#000'
                                        },
                                    },
                                    
                                }
                            }
                        ]
                    };
                topicBar.setOption(topicOption);
            },"json");
    
}