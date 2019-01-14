<!DOCTYPE html>
<html>
    <head>
        <title>Show PDF</title>
        <meta charset="utf-8"/>
        <script type="text/javascript" src='${ctx}/js/pdf.js'></script>
        <style type="text/css">
            html,body {
                width: 100%;
                margin: 0;
                padding: 0;
                background-color: #444;
            }

            body {
                display: flex;
                justify-content: center;
                flex-wrap: wrap;
            }
        </style>
    </head>
    <body></body>
    <script type="text/javascript">
        
        
	PDFJS.getDocument('${ctx}/pdf${path}').then(pdf=>{
		var numPages = pdf.numPages;
		var start = 1;
		renderPageAsync(pdf, numPages, start);
	});
	
	function renderPage(pdf, numPages, current){
		console.log("renderPage");
		pdf.getPage(current++).then(page=>{
			//console.log('page', page);
			//page.getTextContent().then(v=>console.log(v));
      		var scale = 1.5;
      		var viewport = page.getViewport(scale);
      		// Prepare canvas using PDF page dimensions.
      		var canvas = document.createElement("canvas");
      		var context = canvas.getContext('2d');
      		document.body.appendChild(canvas);
      		
      		canvas.height = viewport.height;
      		canvas.width = viewport.width;

      		// Render PDF page into canvas context.
      		var renderContext = {
        			canvasContext: context,
        			viewport: viewport
      		};
      		page.render(renderContext);
      		
      		//next
      		if(current<=numPages)return renderPage(pdf, numPages, current);
		});
	}
	
	async function renderPageAsync(pdf, numPages, current){
		console.log("renderPage async");
		for(let i=1; i<=numPages; i++){
			// page
			let page = await pdf.getPage(i);
			
      		let scale = 1.5;
      		let viewport = page.getViewport(scale);
      		// Prepare canvas using PDF page dimensions.
      		let canvas = document.createElement("canvas");
      		let context = canvas.getContext('2d');
      		document.body.appendChild(canvas);
      		
      		canvas.height = viewport.height;
      		canvas.width = viewport.width;

      		// Render PDF page into canvas context.
      		let renderContext = {
        			canvasContext: context,
        			viewport: viewport
      		};
      		page.render(renderContext);
		}
	}

    
    </script>
</html>
