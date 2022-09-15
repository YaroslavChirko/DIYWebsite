function removeDuplicates(files){
	return files.filter((el, index, self)=>{
		let duplicate = false;
		self.forEach((other,oIndex) => {
			if(el!=null && other != null && oIndex != index && el.name === other.name && el.size === other.size){
				duplicate = true;
			}
		})
		self[index] = null;
		if(!duplicate) return el;
		
	});
}

function redrawImages(imagesViewDiv, files, filesUpdateCallback){
	//null the contents of imagesView
	imagesViewDiv.innerHTML = '';

	//add divs with inner imgs to show files on the page
	for(let i = 0; i<files.length; i++){
		let div = document.createElement('div');
		div.setAttribute('class', 'col-4 col-lg-3 col-xl-2');
		div.setAttribute('name', 'pic-holder');
		
		let img = document.createElement('img');
		img.setAttribute('width', '160px');
		img.setAttribute('height', 'auto');
		img.setAttribute('src', URL.createObjectURL(files[i]));
		img.setAttribute('onclick', `removePicture('images_view', 'images', ${i}, ${filesUpdateCallback})`);//add local callback function call to update array
		div.appendChild(img);
		imagesViewDiv.appendChild(div);
	}
}

function updateFilesList(prevFiles){
	const dataTransfer = new DataTransfer()
	
	for(let i = 0; i<prevFiles.length; i++){
		dataTransfer.items.add(prevFiles[i]);
	}
	
	return dataTransfer;
}

function removePicture(imagesViewDivStr, filesInputStr, picIndex, filesUpdateCallback){
	let imagesViewDiv = document.getElementById(imagesViewDivStr);
	let fileInput = document.getElementById(filesInputStr);
	let files = [];
	files.push(...fileInput.files);
	//remove from the file
	
	files.splice(picIndex,1);
	
	let dataTransfer = updateFilesList(files);
	fileInput.files = dataTransfer.files;
	filesUpdateCallback(files);
	redrawImages(imagesViewDiv, files, filesUpdateCallback);
}