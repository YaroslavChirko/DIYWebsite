
let file = [];
let fileInput = document.getElementById('images');
let imagesView = document.getElementById('images_view');
let fileReader = new FileReader();

fileInput.onchange = (e) => {
	file.push(...fileInput.files);
	
	file = removeDuplicates(file);
	
	redrawImages(imagesView, file, updateLocalFilesArr);
	let dataTransfer = updateFilesList(file);
	fileInput.files = dataTransfer.files;
	
}

function updateLocalFilesArr(changedFiles){
	file = changedFiles;
}