var columnBar = echarts.init(document.getElementById('column-bar'));
function columnEcharts(){
    $.get("columnranking",
            {},
            function(result) {
                var xAxisData = [];
                var seriesDate = [];
                result.data.map(function(one, index){
                    xAxisData.push(one.colName||'--');
                    seriesDate.push(one.clickAll);
                });
                
                columnOption = {
                        title: {
                            text: '栏位点击量排行',
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
                columnBar.setOption(columnOption);
            },"json");
    
}