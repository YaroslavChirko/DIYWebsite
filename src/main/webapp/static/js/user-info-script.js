
function makeEditable(){
	document.getElementById('edit_btn').setAttribute('disabled', 'disabled');
	document.getElementById('name').removeAttribute('disabled');
	document.getElementById('update_sub').removeAttribute('hidden');
}