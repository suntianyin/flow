var resourceBar = echarts.init(document.getElementById('resource-bar'));
function resourceEcharts(){
    $.get("resourceranking",
            {},
            function(result) {
                var xAxisData = [];
                var seriesDate = [];
                result.data.map(function(one, index){
                    xAxisData.push(one.resName||'--');
                    seriesDate.push(one.clickAll);
                });
                
                resourceOption = {
                        title: {
                            text: '图书点击量top20',
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
                resourceBar.setOption(resourceOption);
            },"json");
    
}