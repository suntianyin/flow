var checkBlankRegex = /^[\s\S]+$/; // 非空正则表达式
var numwordRegex = /^[0-9a-zA-Z]+$/; // 获取焦点事件
var checkNumRegex = /^\d+$/; // 数字正则表达式
var chkOrgCode = true;
var chkOrgName = true;
var chkDevNum = true;

// 验证orgCode在数据库中是否存在
function checkOrgCode() {
	var orgCodeVal = $("#orgCode").val();
	if (checkBlankRegex.test(orgCodeVal)) {
		if (numwordRegex.test(orgCodeVal)) {
			$.ajax({
				type : 'get',
				url : ctx + "/admin/auth/authorg/checkOrgCode",
				data : {
					"orgCode" : orgCodeVal
				},
				success : function(data) {
					if (data == '1') {
						chkOrgCode = true;
					} else if (data == '0') {
						$("#orgCode_msg").html("").html("机构Id已存在，请重新输入。");
						chkOrgCode = false;
					}
				}
			});
		} else {
			$("#orgCode_msg").html("").html("机构Id只能为数字或字母。");
			chkOrgCode = false;
		}
	} else {
		$("#orgCode_msg").html("").html("机构Id不能为空。");
		chkOrgCode = false;
	}
}

function focusIn(id) {
	$("#" + id + "_msg").html("");
}

// 验证orgName在数据库中是否存在
function checkOrgName(orgName) {
	var orgNameVal = $("#" + orgName).val();
	if (checkBlankRegex.test(orgNameVal)) {
		var lenByte = orgNameVal.replace(/[^\x00-\xff]/gi, 'xx').length;
		if (lenByte >= 200) {
			$("#" + orgName + "_msg").html("").html("机构名称长度超过200字符。");
			chkOrgName = false;
		} else {
			$("#"+orgName+"_msg").html("");
			  var flag = true;
			  if(orgName == "update_orgName"){
				  if(orgNameVal == $("#update_temp_orgName").attr("value")){
					  flag = false;
				  }
			  }
			  if(flag){
				  $.ajax({
						type : 'post',
						url : ctx + "/admin/auth/authorg/checkOrgName",
						data : {
							"orgName" : orgNameVal
						},
						success : function(data) {
							if (data == '1') {
								chkOrgName = true;
							} else if (data == '0') {
								$("#" + orgName + "_msg").html("").html(
										"机构名称已存在，请重新输入。");
								chkOrgName = false;
							} else if (data == '-1') {
								$("#" + orgName + "_msg").html("").html("系统错误。");
								chkOrgName = false;
							}
						}
					});
			  }
		}
	} else {
		$("#" + orgName + "_msg").html("").html("机构名称不能为空。");
		chkOrgName = false;
	}
}

// 复选框选中更改隐藏域里对应的值
function checkBoxChange() {
	var flag = false;
	var boxs = $('input[name="resPack"]');
	var boxFlag = false;
	var array = new Array();
	if (boxs != null && boxs.length > 0) {
		for (var i = 0, len = boxs.length; i < len; i++) {
			if (boxs[i].checked) {
				boxFlag = true;

				var resPack = new Object();
				var resPackId = $(boxs[i]).val();
				resPack.resPackId = resPackId;
				if ($("#date_" + resPackId).length > 0) {
					resPack.endDate = $("#date_" + resPackId).val();
				}
				array.push(resPack);
			}
		}
		if (!boxFlag) {
			$("#new_authType_msg").html("").html("请选择授权类型！");
		} else {
			// 设置授权
			$('#orderData').attr('value', JSON.stringify(array));
		}
	}
	return boxFlag;
}

function checkDeviceNum(deviceNum) {
	var deviceNumVal = $("#" + deviceNum).val();
	if (!checkBlankRegex.test(deviceNumVal)) {
		$("#" + deviceNum + "_msg").html("").html("屏数量不能为空。");
		deviceNumFlag = false;
		chkDevNum = false;
	} else {
		if (checkNumRegex.test(deviceNumVal)) {
			var deviceNumToInt = parseInt(deviceNumVal);
			var old_deviceNumToInt = parseInt($("#old_deviceNum").val());
			if($("#old_deviceNum").length > 0 && old_deviceNumToInt > deviceNumToInt){
				$("#" + deviceNum + "_msg").html("").html("屏数量不能减少。");
				chkDevNum = false;
			}else if(deviceNumVal > 100000) {
				$("#" + deviceNum + "_msg").html("").html("屏数量超过限制。");
				chkDevNum = false;
			} else {
				$("#" + deviceNum + "_msg").html("");
				chkDevNum = true;
			}
		} else {
			$("#" + deviceNum + "_msg").html("").html("输入格式不正确，请输入数字。");
			deviceNumFlag = false;
			chkDevNum = false;
		}
	}
}

// 选择省
function selectProvince(id, cityId, districtId) {
	//alert(0);
	var txt = $('#' + id).attr('value');
	var pid = (txt.split('_'))[0];
	var url = ctx + '/admin/auth/org/selectProvince';
	$.ajax({
		url : url,
		type : 'post',
		data : {
			'provinceId' : pid
		},
		success : function(data) {
			data = eval('('+data+')');
			if (data != null) {
				fillCity(cityId, data.citylist);
				fillDistrict(districtId, data.districtlist);
			}
		}
	});
}
// 选择市
function selectCity(cityId, districtId) {
	var txt = $('#' + cityId).attr('value');
	var pid = (txt.split('_'))[0];
	var url = ctx + '/admin/auth/org/selectCity';
	$.ajax({
		url : url,
		type : 'post',
		data : {
			'provinceId' : pid
		},
		success : function(data) {
			data = eval('('+data+')');
			if (data != null) {
				fillDistrict(districtId, data.districtlist);
			}
		}
	});
}
// 初始化select
function initSelect(provinceId, cityId, districtId, hideCodeId, updateCode) {
	if (updateCode != undefined) {
		var param = {
			'provinceId' : updateCode
		};
		$('#' + hideCodeId).attr('value', updateCode);
	} else {
		var param = {};
		$('#' + hideCodeId).attr('value', '101010100');
	}
	var url = ctx + '/admin/auth/org/initSelect';
	$.ajax({
		url : url,
		type : 'post',
		data : param,
		success : function(data) {
			data = eval('('+data+')');
			if (data != null) {
				fillProvince(provinceId, data.provincelist, data.provinceCode);
				fillCity(cityId, data.citylist, data.cityCode);
				fillDistrict(districtId, data.districtlist, data.districtCode);
			}
		}
	});
}
// 填充省下拉列表
function fillProvince(provinceId, provincelist, provinceCode) {
	var provincenode = $('#' + provinceId).empty();
	$(provincelist).each(function(index, ele) {
		var opt = $('<option>');
		var txt = ele.id + '_' + ele.areaCode;
		if (txt == provinceCode) {
			$(opt).attr({
				'value' : txt,
				'selected' : 'selected'
			}).html(ele.areaName);
		} else {
			$(opt).attr({
				'value' : txt
			}).html(ele.areaName);
		}
		$(provincenode).append(opt);
	});
}
// 填充市下拉列表
function fillCity(cityId, citylist, cityCode) {
	var citynode = $('#' + cityId).empty();
	$(citylist).each(function(index, ele) {
		var opt = $('<option>');
		var txt = ele.id + '_' + ele.areaCode;
		if (txt == cityCode) {
			$(opt).attr({
				'value' : txt,
				'selected' : 'selected'
			}).html(ele.areaName);
		} else {
			$(opt).attr({
				'value' : txt
			}).html(ele.areaName);
		}
		$(citynode).append(opt);
	});
}
// 填充县下拉列表
function fillDistrict(districtId, districtlist, districtCode) {
	var districtnode = $('#' + districtId).empty();
	$(districtlist).each(function(index, ele) {
		var opt = $('<option>');
		var txt = ele.id + '_' + ele.areaCode;
		if (txt == districtCode) {
			$(opt).attr({
				'value' : txt,
				'selected' : 'selected'
			}).html(ele.areaName);
		} else {
			$(opt).attr({
				'value' : txt
			}).html(ele.areaName);
		}
		$(districtnode).append(opt);
	});
}
// 为隐藏的areaCode赋值
function getAreaCode(provinceId, cityId, districtId, hideCodeId) {
	var areaCode;
	var provinceTxt = $('#' + provinceId).attr('value').split('_');
	var cityTxt = $('#' + cityId).attr('value').split('_');
	var districtTxt = $('#' + districtId).attr('value').split('_');
	var provinceCode = provinceTxt[1];
	var cityCode = cityTxt[1];
	var districtCode = districtTxt[1];
	if (provinceCode < 10105) {
		areaCode = provinceCode + districtCode + cityCode;
	} else {
		areaCode = provinceCode + cityCode + districtCode;
	}
	$('#' + hideCodeId).attr('value', areaCode);
	return areaCode;
}

function showWexinRelated(weixin, id) {
	var weixinVal = $(weixin).val();
	if (weixin.checked && weixinVal == 3) {
		$("#" + id).show();
	} else {
		$("#" + id).hide();
	}
}