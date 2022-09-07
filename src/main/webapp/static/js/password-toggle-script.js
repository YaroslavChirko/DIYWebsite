
let isHidden = true;
let types = ['text', 'password']

function togglePass(){
	let passField = document.getElementById("pass");
	
	passField.removeAttribute('type');
	passField.setAttribute('type', types[isHidden?1:0]);
	isHidden = !isHidden;
}