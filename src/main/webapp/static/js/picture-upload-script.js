
let file = [];
let fileInput = document.getElementById('images');
let imagesView = document.getElementById('images_view');
let fileReader = new FileReader();

fileInput.onchange = (e) => {
	file.push(...fileInput.files);
	
	removeDuplicates();
	
	redrawImages();
	updateFilesList();
	
}

function removeDuplicates(){
	file = file.filter((el, index, self)=>{
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

function redrawImages(){
	//null the contents of imagesView
	imagesView.innerHTML = '';

	//add divs with inner imgs to show files on the page
	for(let i = 0; i<file.length; i++){
		let div = document.createElement('div');
		div.setAttribute('class', 'col-4 col-lg-3 col-xl-2');
		div.setAttribute('name', 'pic-holder');
		
		let img = document.createElement('img');
		img.setAttribute('width', '160px');
		img.setAttribute('height', 'auto');
		img.setAttribute('src', URL.createObjectURL(file[i]));
		img.setAttribute('onclick', `removePicture(${i})`);
		div.appendChild(img);
		imagesView.appendChild(div);
	}
}

function updateFilesList(){
	const dataTransfer = new DataTransfer()
	
	for(let i = 0; i<file.length; i++){
		dataTransfer.items.add(file[i]);
	}
	
	fileInput.files = dataTransfer.files;
}

function removePicture(picIndex){
	//remove from the file
	file.splice(picIndex,1);
	redrawImages();
	updateFilesList();
}