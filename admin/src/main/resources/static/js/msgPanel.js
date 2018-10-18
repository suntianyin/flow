(function($){
	$.extend({
		//打开消息提示框
		showMsg: function(container, msg, buttons, closeBtn, closeFunc) {
			var setting = {
					container: container,
					title: "提示信息",
					text: msg,
					closeButton: closeBtn || true,
					buttons: buttons || [],
					close: closeFunc
			}
			$.dialog(setting);
		},
		/**
		 * {
		 * 	container: "#container|.container...",
		 * 	title: "Message Dialog",
		 * 	source: "#source|.source...",		//对话框中的内容来源，如果source存在，则不使用text中的配置
		 * 	text: "show the message!",			//对话框中的文本
		 * 	width: 800px,						//对话框的宽度
		 *  height: 800px,						//对话框内容部分高度
		 * 	buttons: [
		 * 		{
		 * 			id: "btnId1",
		 * 			text: "open",
		 * 			click: function(){}
		 * 		},...
		 *  ],
		 *  closeButton: true|false,
		 *  close: function(){}
		 * }
		 */
		//打开对话框
		dialog: function(setting) {
			var dialogOpts = setting || {};
			var container = dialogOpts.container;
			if(!container) {
				return;
			}
			var title = dialogOpts.title;
			var source = dialogOpts.source;
			var text = dialogOpts.text;
			var buttons = dialogOpts.buttons;
			var closeButton = dialogOpts.closeButton;
			var close = dialogOpts.close;
			var width = dialogOpts.width;
			var height = dialogOpts.height;
			var dialogContainer = $("<div class='modal fade' id='container-dialog' tabindex='-1' role='dialog' aria-labelledby='msgModalLabel' aria-hidden='true'></div>");
			var modalDialog = $("<div class='modal-dialog'></div>");
			if(width) {
				$(modalDialog).css({"width":width});
			}
			$(dialogContainer).append(modalDialog);
			var modalContent = $("<div class='modal-content'></div>");
			$(modalDialog).append(modalContent);
			if(title) {
				var modalHeader = $("<div class='modal-header'><button type='button' class='close' data-dismiss='modal' aria-hidden='true'>&times;</button><h4 class='modal-title' id='msgModalLabel'>"+title+"</h4></div>");
				$(modalContent).append(modalHeader);
			}
			var modalBody = $("<div class='modal-body'></div>");
			if(height) {
				modalBody.css({"height":height});
			}
			var sourceContent = {};
			if(source) {
				sourceContent = $(source).children().clone();
				$(modalBody).append(sourceContent);
			}else if(text) {
				var textContent = $("<p>"+text+"</p>");
				$(modalBody).append(textContent);
			}
			$(modalContent).append(modalBody);
			var modalFooter = $("<div class='modal-footer'></div>");
			if(buttons != null && buttons.length > 0) {
				var btnsLength = buttons.length;
				for(var i = 0; i < btnsLength; i++) {
					var buttonConf = buttons[i];
					var button = $("<button id='"+buttonConf.id+"' type='button' class='btn btn-primary' data-dismiss='modal'>"+buttonConf.text+"</button>");
					if(buttonConf.click) {
						$(button).click(function(){buttonConf.click(dialogContainer, sourceContent);});
					}
					$(modalFooter).append(button);
				}
			}
			if(closeButton) {
				var closeBtn = $("<button type='button' class='btn btn-default' data-dismiss='modal'>关闭</button>");
				$(modalFooter).append(closeBtn);
			}
			$(modalContent).append(modalFooter);
			var closeFunc = function(){
				if(close) {
					close();
				}
				$(container).empty();
			}
			$(dialogContainer).on("hide.bs.modal", closeFunc);
			$(container).empty();
			$(container).append(dialogContainer);
			$(dialogContainer).modal("show");
		},
		//关闭对话框
		closeDialog: function() {
			$("#container-dialog").modal("hide");
			$.clearAfterClose();
		},
		clearAfterClose : function() {
			$(".modal-backdrop").remove();
			$("#container-dialog").remove();
		},
		hide : function(obj) {
			$(obj).removeClass("ele-show");
			$(obj).addClass("ele-hide");
		},
		show : function(obj) {
			$(obj).removeClass("ele-hide");
			$(obj).addClass("ele-show");
		},
		formatQ: function(q) {
			var tmpQ = q;
			if(tmpQ.charAt(0) == "\"") {
				tmpQ = tmpQ.substring(1);
			}
			if(tmpQ.charAt(tmpQ.length - 1) == "\"") {
				tmpQ = tmpQ.substring(0, tmpQ.length - 1);
			}
			var formatQ = "_all:" + tmpQ;
			return formatQ;
		},
		initContainerHeight: function(container) {
			$(container).css({"height":$(window).height()+"px"});
		},
		schemaEditor: function(metadata, jsonSchema, mapping, type) {
			var editorContainer = $("<div id='schemaEditor' />");
			var editorHeader = $("<div class='page-header' />");
			$(editorContainer).append(editorHeader);
			$(editorHeader).append("<div class='page-title'>单击左边进行编辑，点击右边显示结果</div>");
			var editorTools = $("<div class='page-tools btn-group' />");
			$(editorHeader).append(editorTools);
			var btnSave = $("<button class='btn btn-primary'>保存</button>")
			$(editorTools).append(btnSave);
			var btnCancel = $("<button class='btn btn-primary' >取消</button>");
			$(btnCancel).click(function(){
				editorContainer.remove();
			});
			$(editorTools).append(btnCancel);
			var metadataPanel = $("<div id='metadata-panel' class='panel panel-success' />")
			$(editorContainer).append(metadataPanel);
			$(metadataPanel).append("<div class='panel-heading'><div class='panel-title'>schema元信息</div></div>");
			var metadataPanelBody = $("<div class='panel-body form-horizontal' />");;
			$(metadataPanel).append(metadataPanelBody);
			var schemaId = metadata.id;
			if(schemaId != null) {
				$(metadataPanelBody).append("<input id='schemaId' type='hidden' value='"+metadata.id+"'>");
				$(metadataPanelBody).append("<input id='schemaResType' type='hidden' value='"+type+"'>");
			}
			$(metadataPanelBody).append("<div class='form-group'><label class='col-sm-2 control-label'>schema名称:</label><div class='col-sm-3'><input id='schemaName' type='text' class='form-control' value='"+(metadata.name?metadata.name:"")+"' ></div></div>");
			if(schemaId == null) {
				$(metadataPanelBody).append("<div class='form-group'><label class='col-sm-2 control-label'>schema类型:</label><div class='col-sm-3'><input id='schemaResType' type='text' class='form-control' value='' ></div></div>");
			}
			if(schemaId != null) {
				var verListGroup = $("<div class='form-group' />");
				$(metadataPanelBody).append(verListGroup);
				$(verListGroup).append("<label class='col-sm-2 control-label'>schema版本:</label>");
				var versListCol = $("<div class='col-sm-3' />");
				$(verListGroup).append(versListCol);
				var versList = $("<select id='curVersion' class='dropList form-control' />");
				$(versListCol).append(versList);
				var defaultVer = metadata.defaultVersion;
				if(metadata.schemaVersions != null) {
					for(var ver in metadata.schemaVersions) {
						var verOpt = $("<option />");
						$(verOpt).val(ver);
						$(verOpt).text(ver);
						if(defaultVer === ver) {
							$(verOpt).attr("selected", "selected");
						}
						$(versList).append(verOpt);
					}
				}
			}
			$(metadataPanelBody).append("<div class='form-group'><label class='col-sm-2 control-label'>当前版本:</label><div class='col-sm-3'><input type='text' id='newVersion' class='form-control' value='' placeholder='自定义schema版本号，如果为空，默认以保存时的时间戳为版本号' ></div></div>");
			var editorPanel = $("<div id='editor-panel' class='panel panel-default' />");
			$(editorContainer).append(editorPanel);
			var editorPanelBody = $("<div class='panel-body' />");
			$(editorPanel).append(editorPanelBody);
			var editor = $("<div id='jsonEditor' class='form-horizontal'></div>");
			$(editorPanelBody).append(editor);
			var textPanel = $("<div id='text-panel' class='panel panel-default' />");
			$(editorContainer).append(textPanel);
			var textPanelBody = $("<div class='panel-body' />");
			$(textPanel).append(textPanelBody);
			var text = $("<div id='jsonText'></div>");
			$(textPanelBody).append(text);
			var closeFunc = function() {
				$(btnCancel).click();
			}
			$(btnSave).click(function(){
				$(text).click();
				var jsonText = $(text).text();
				var jsonMapping = JSON.parse(jsonText);
				saveSchema(jsonMapping, type, closeFunc);
			});
			$(versList).change(function() {
				var ver = $(this).val();
				var sv = metadata.schemaVersions[ver];
				var jsonMappingStr = sv.json? sv.json: sv.mapping;
				var jsonMapping = JSON.parse(jsonMappingStr);
				mappingEditor(jsonSchema, jsonMapping, type, editor, text);
			});
			mappingEditor(jsonSchema, mapping, type, editor, text);
			$("body").append(editorContainer);
			$(editorContainer).css({"width":$(window).width(),"height":$(window).height()});
		},
		dataEditor: function(mapping, data) {
			var editorContainer = $("<div id='dataEditor' />");
			var editorHeader = $("<div class='page-header' />");
			$(editorContainer).append(editorHeader);
			$(editorHeader).append("<div class='page-title'>单击左边进行编辑，点击右边显示结果</div>");
			var editorTools = $("<div class='page-tools btn-group' />");
			$(editorHeader).append(editorTools);
			var btnSave = $("<button class='btn btn-primary'>保存</button>")
			$(editorTools).append(btnSave);
			var btnCancel = $("<button class='btn btn-primary' >取消</button>");
			$(btnCancel).click(function(){
				editorContainer.remove();
			});
			$(editorTools).append(btnCancel);
			var editorPanel = $("<div id='editor-panel' class='panel panel-default' />");
			$(editorContainer).append(editorPanel);
			var editorPanelBody = $("<div class='panel-body' />");
			$(editorPanel).append(editorPanelBody);
			var editor = $("<div id='jsonEditor' class='form-horizontal'></div>");
			$(editorPanelBody).append(editor);
			var textPanel = $("<div id='text-panel' class='panel panel-default' />");
			$(editorContainer).append(textPanel);
			var textPanelBody = $("<div class='panel-body' />");
			$(textPanel).append(textPanelBody);
			var text = $("<div id='jsonText'></div>");
			$(textPanelBody).append(text);
			dataEditor(mapping, data._source, editor, text);
			var closeFunc = function() {
				editorContainer.remove();
			}
			$(btnSave).click(function(){
				$(text).click();
				var jsonText = $(text).text();
				saveData(data._id, data._type, data.parentColumn, jsonText, closeFunc);
			});
			$("body").append(editorContainer);
			$(editorContainer).css({"width":$(window).width(),"height":$(window).height()});
		}
	});
})(jQuery);