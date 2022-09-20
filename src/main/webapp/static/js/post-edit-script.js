
let formHolder = document.getElementById('form-holder');
let postBody = document.getElementById('post-body');
let postElementsContainer = document.getElementById('post-elements-container');
let editButton = document.getElementById('editBtn');
let filesList = [];

async function setEditable(){
	editButton.setAttribute('disabled', 'disabled');
	const imageUrlsArr = extractImageSrcs(postBody.innerHTML);
	const bodyTextFormatted = getAndFormatBodyText();
	
	postBody.innerHTML = `<textarea name="body" id="post-body" rows="20" cols="60">${bodyTextFormatted}</textarea>`;
	
	
	let formHolderChildren = formHolder.innerHTML;
	formHolder.innerHTML = '';
	let form = document.createElement('form');
	form.setAttribute('method', 'post');
	form.setAttribute('enctype', 'multipart/form-data');
	form.innerHTML = formHolderChildren;
	
	const fileInput = document.createElement('input');
	fileInput.setAttribute('type', 'file');
	fileInput.setAttribute('name', 'images');
	fileInput.setAttribute('id', 'images');
	fileInput.setAttribute('accept', 'image/png, image/gif, image/jpeg, image/jpg');
	fileInput.setAttribute('multiple', 'multiple');
	fileInput.setAttribute('class', 'form-control-file form-control-sm custom-file-input');
	
	
	
	let dataTransfer = await getImages(imageUrlsArr, fileInput);
	if(dataTransfer.files != undefined){
		fileInput.files = dataTransfer.files;
	}
	form.appendChild(fileInput);
	
	
	
	let imagesViewDiv = document.createElement('div');
	imagesViewDiv.setAttribute('id', 'images_view');
	imagesViewDiv.setAttribute('class', 'row');
	form.appendChild(imagesViewDiv);
	redrawImages(imagesViewDiv, filesList, updateLocalFilesArr);
	
	fileInput.onchange = (e) => {
		filesList.push(...fileInput.files);
		
		filesList = removeDuplicates(filesList);
		
		redrawImages(imagesViewDiv, filesList, updateLocalFilesArr);
		let dataTransfer = updateFilesList(filesList);
		fileInput.files = dataTransfer.files;
		
	}
	
	
	
	
	let submit = document.createElement('button');
	submit.setAttribute('class', 'btn btn-primary');
	submit.innerText = 'ConfirmChanges';
	form.append(submit);
	
	formHolder.appendChild(form);
	
}



function getAndFormatBodyText(){
	let bodyText = postBody.innerHTML;
	let bodyArr = bodyText.split(/<img.+?(?:><\/img>|\/>|>)/);
	let bodyRes = '';
	for(let i = 0; i<bodyArr.length-1; i++){
		bodyRes += bodyArr[i]+`[[${i+1}]]`;
	}
	bodyRes += bodyArr[bodyArr.length-1];
	
	const regex = /(?:<\/?br.*?\/?>|<\/?p.*?\/?>|<\/?i.*?\/?>|<\/?h1.*?\/?>|<\/?h2.*?\/?>|<\/?h3.*?\/?>|<\/?h4.*?\/?>|<\/?h5.*?\/?>|<\/?h6.*?\/?>)/g;
	return bodyRes.replaceAll(regex,'');
}

function extractImageSrcs(bodyText){
	const rx = /<img .+? src\s?=['"](.+?)['"].+?(?:><\/img>|\/>|>)/g;
	let res = [];
	let current;
	let m = 0;
	while((current=rx.exec(bodyText)) != null && m < 100){
		res.push(current[1])
		m++;
	}
	return res;
}


async function getImages(imageUrls){
	const dataTransfer = new DataTransfer(); 
	let headers = new Headers();
	headers.append('Accept', 'image/*');
	
	const options = {method: 'GET', 
					 mode: 'cors',
					 headers: headers};
	
	for (let imageUrl of imageUrls){
		
		let req = new Request(imageUrl, options);
		
		try{
			let image = await fetch(req);
			let imageData = await image.blob();
			let urlParts = imageUrl.split("/");
			let imageName = urlParts[urlParts.length-1];
			let currentFile = new File([imageData], imageName);
			dataTransfer.items.add(currentFile);
			filesList.push(currentFile);
		}catch(err){
			console.log('No file found: '+imageUrl);
		}
		
	}
	
	return dataTransfer
}

function updateLocalFilesArr(changedFiles){
	filesList = changedFiles;
}

