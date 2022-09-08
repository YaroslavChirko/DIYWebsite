
let postTitle = document.getElementById('post-title');
let postBody = document.getElementById('post-body');

function setEditable(){
	console.log(getPostTitle());
	console.log(postBody.innerHTML);
	console.log(getAndFormatBodyText());
	console.log(extractImageSrcs(postBody.innerHTML));
}

function getPostTitle(){
	return postTitle.textContent;
}

function getAndFormatBodyText(){
	let bodyText = postBody.innerHTML;
	let bodyArr = bodyText.split(/<img.+?(?:><\/img>|\/>|>)/);
	let bodyRes;
	for(let i = 0; i<bodyArr.length-1; i++){
		bodyRes += bodyArr[i]+`[[${i}]]`;
	}
	
	return bodyRes;
}

function extractImageSrcs(bodyText){
	let rx = /<img .+? src\і?=['"](.+?)['"].+?(?:><\/img>|\/>|>)/g;
	let res = [];
	let current;
	let m = 0;
	while((current=rx.exec(bodyText)) != null && m < 100){
		res.push(current[1])
		m++;
	}
	return res;
}