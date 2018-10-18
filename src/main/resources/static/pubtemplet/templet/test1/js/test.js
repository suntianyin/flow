/**
 * 手机新浪网-浪首交互功能
 * @author		王锋(wangfeng8@)
 * @requires	Zepto
 * @date		2013-05-23
 * @modify		huangke2@staff.sina.com.cn 2014-03-13
 */

(function(){
	
		/**
		 * 追加onscroll事件
		 * @method	addOnscroll
		 * @param	fn  执行函数	
		 */
		function addOnscroll(fn){
			if (typeof window.onscroll == 'function') {
				var tempFn = window.onscroll;
				window.onscroll = function(){
					tempFn();
					fn();
				}
			} else {
				window.onscroll = function(){
					fn();
				}
			}
		}


        /**
		 * 组图轮播
		 * @method	imgSwipe
		 * @param	{String} CLASS字符串，如'j_imgswipe'
		 * @return	
		 */
        function imgSwipe(id){
			var obj = null,
				oSwipe = $('#' + id),
				oImg = oSwipe.children().eq(0).find('a').find('img'),
				oLi = oSwipe.children().eq(1).find('li'),
				len = oImg.length,
				maxCount = len - 2, 	// 加载结束量
				count = 1; 				// 加载开始量

			obj = new Swipe(document.getElementById(id), {
				startSlide: 0,
				auto: 4000,
				speed: 300,
				callback: function(index, element){
					oLi.removeClass('active').eq(index).addClass('active');	// 焦点图当前状态

					// 控制图片延迟加载的次数
					if (count <= maxCount) {	
						if (index >= 1 && index <= maxCount) {	// 判断加载范围
							var currImg = oImg.eq(index + 1);
							if (currImg.length > 0) {
								var src = currImg.attr('data-src');
								currImg.attr('src', src);
							}
						}

						count++;
					}
				}
			});
		}

		/**
		 * 图片延迟加载
		 * @method	imgDelay
		 * @param	
		 * @return	
		 */
		function imgDelay(){
			var area = $('.j_imgdelay'),
				areaLen = area.length,
				loadCount = 0;  //图片延迟加载区统计;

			//如果不存在需要延迟的图片模块，退出
			if (areaLen == 0) {
				return false;
			}

			var viewHeight = document.documentElement.clientHeight;
			//提前(一屏的高度)加载图片
			viewHeight = viewHeight * 2;

			function loadingImg(){
				//统计加载区加载完毕后，清除定时器	
				if (loadCount >= areaLen) {
					return false;
				}

				for (var i=0; i<areaLen; i++) {
					var currObj = area.eq(i);

					//当前加载后退出;
					if (currObj.data('imgdelay') === 1) {
						continue;
					}

					var	scrollY	= document.body.scrollTop || document.documentElement.scrollTop,
						maxHeight = viewHeight + scrollY;

					if (currObj.offset().top < maxHeight) {

						//加载当前图片区下所有图片
						var oImg = currObj.find('img'),
							imgLen = oImg.length;

						for (var j = 0; j < imgLen; j++) {
							var currImg = oImg.eq(j);
							currImg.attr('src', currImg.data('src'));
						}

						//标记当前图片区已经加载过
						currObj.data('imgdelay', '1');

						//统计图片区加载次数
						loadCount = loadCount + 1;
					}
				}
			}

			var timer;	
			addOnscroll(function(){
				if (timer) {
					clearTimeout(timer);
                    timer = null;
				}

				timer = setTimeout(loadingImg, 200);
			});
		}

		

		/**
		 * 绑定事件
		 */
		setTimeout(function(){
            imgSwipe('imgSwipe');               // 组图
			//imgDelay();			                // 图片延迟加载
		}, 300);

		

		

		
})();