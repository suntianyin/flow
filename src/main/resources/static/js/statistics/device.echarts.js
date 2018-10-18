/**
 * Created by liuyutong on 2017/8/3.
 */
//总数查询
var pieChart;
var pie_option;
function overview(startDate, endDate, select) {
	pieChart = echarts.init(document.getElementById('pie_chart'));
    $.get("overview", {startDate: startDate, endDate: endDate}, function(result){
    	$(".org-num").text(result.data.totalOrg);
    	$(".active-num").text(result.data.activeCount);
    	$(".live-num").text(result.data.liveCount);
        
    	$(".org-add-num").text(result.data.totalOrgAdd);
    	$(".active-add-num").text(result.data.activeCountAdd);
    	$(".live-add-num").text(result.data.liveCountAdd);
    	
        pie_option = {
        	    tooltip: {
					show:true,
        	        trigger: 'item',
        	        formatter: "{b}: {c} (台)"
        	    },
        	    legend: {
					show:true,
        	        orient: 'vertical',
        	        x: '75%',
        	        y: '20%',
        	        data:['活跃设备','离线设备']
        	    },
        	    series: [
        	        {
        	            type:'pie',
        	            selectedMode: 'single',
        	            radius: [0, '70%'],
        	            label: {
							show:true,
        	                normal: {
        	                	formatter:'{d}%',
        	                	textStyle: {
        	                		color: '#000',
									fontSize:12
        	                	},
								position:'outside'
        	                }
        	            },
        	            labelLine: {
							show:true,
        	            	normal: {
        	            		lineStyle:{
        	            			color:'#000'
        	            		}
        	            	}
        	            },
						silent:false,
        	            data:[
        	                {value:result.data.liveCountAdd, name:'活跃设备'},
        	                {value:result.data.totalDevice-result.data.liveCountAdd, name:'离线设备'}
        	            ]
        	        }
        	       
        	    ]
        	};
		/*if (select && select != '1') {
			//饼图暂时失效
			disablePieChart();
		}else{*/
        	pieChart.setOption(pie_option);
		//}
    },'json');
}

function disablePieChart() {
	pieChart.setOption({
		tooltip: {
			show:false
		},
		legend: {
			show:false
		},
		series: [
			{
				selectedMode: 'single',
				label: {
					normal: {
						formatter:'暂无数据！',
						textStyle: {
							color: '#000',
							fontSize:30
						},
						position:'center'
					}
				},
				silent:true,
				data:[
					{value:100, name:'离线设备',itemStyle:{
                        normal:{
                            color:'#2f4554'
                        }
                    }}
				]
			}

		]
	});
}

//设备数量月度分布
var device;
var device_option;
function deviceStatistics(year){
	device = echarts.init(document.getElementById('device_statistics'));
	device_option = {
			        title: {
			            text: '设备数量月度分布',
			            textStyle:{
			            	fontSize:15
			            }
			        },
			        tooltip: {
			            trigger: 'axis',
			            axisPointer: {
			                type: 'cross',
			                label: {
			                    backgroundColor: '#283b56'
			                }
			            }
			        },
			        grid: {
				        left: '3%',
				        right: '4%',
				        bottom: '3%',
				        containLabel: true
				    },
			        legend: {
			            data:['设备数量', '活跃设备数量'],
			            left:'50%'
			        },
			        dataZoom: {
			            show: false,
			            start: 0,
			            end: 100
			        },
			        xAxis: [
			            {
			                type: 'category',
			                boundaryGap: true,
			                data: ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
			            }
			        ],
			        yAxis: [
			            {
			                type: 'value',
			                scale: true,
			                name: '数量'
			            }
			        ],
			        series: [
			            {
			                name:'设备数量',
			                type:'bar',
//			                data:data.monthlyCount
			            },
			            {
			                name:'活跃设备数量',
			                type:'line',
//			                data:data.activeCount
			            }
			        ]
			    };
		device.setOption(device_option);
		
	$.get("devicebarchart", {year: year}, function(result){
		device.setOption({
			series: [
	            {
	                name:'设备数量',
	                type:'bar',
	                data:result.monthlyCount
	            },
	            {
	                name:'活跃设备数量',
	                type:'line',
	                data:result.liveCount
	            }
	        ]
		});
	},'json'); 
}

//机构数量月度分布
var org;
var org_option;
function orgStatistics(year){
	org = echarts.init(document.getElementById('org_statistics'));
	$.get("orgbarchart", {year: year}, function(result){
		org_option = {
				title: {
					text: '机构数量月度分布',
					textStyle:{
		            	fontSize:15
		            },
				},
			    color: ['#3398DB'],
			    tooltip : {
			        trigger: 'axis',
			        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
			            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
			        }
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
			            data : ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月'],
			            axisTick: {
			                alignWithLabel: true
			            }
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value',
		                name: '数量'
			        }
			    ],
			    series : [
			        {
			            name:'机构数量',
			            type:'bar',
			            barWidth: '60%',
			            data:result.monthlyCount
			        }
			    ]
			};
		org.setOption(org_option);
	},'json'); 
	
}