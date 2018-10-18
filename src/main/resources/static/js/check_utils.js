var CheckUtils = {
		//判断复选框是否空选
		checkSelect : function(chkName){
			var boxFlag = false;
			var boxs = $('input[type="checkbox"][name="'+chkName+'"]');
			for(var i = 0, len = boxs.length; i < len; i++){
				if(boxs[i].checked ){
					boxFlag = true;
					break;
				}  
			}
			return boxFlag;
		},
		//判断是否为空
		isEmpty : function(src){
			if(src == null || $.trim(src) == ""){
				return true;
			}
			return false;
		},
		//判断是否为数值
		isNumber : function(obj){
			return $.isNumeric(obj);
		},
		//判断字符串是否超过长度
		isTooLong : function(src, maxLen){
			if(src.length > maxLen){
				return true;
			}
			return false;
		},
		versionToNumber: function(src) {
			var arr = src.split(".");
			var firstNumber = parseInt(arr[0]) * 1000 * 1000;
			var secondNumber = parseInt(arr[1]) * 1000;
			var thirdNumber = parseInt(arr[2]);
			return firstNumber + secondNumber + thirdNumber;
		}
};