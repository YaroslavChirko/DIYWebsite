
function addReplyForm(id){
	let commentsDisplay = document.getElementById('comments-display');
	let replyForm = document.getElementById(`reply-form-${id}`);
	let replyFormButton = document.getElementById(`reply-form-button-${id}`);
	let replyFormTextArea = document.createElement('textarea');
	let submitButton = document.createElement('button');
	
	for(let textarea of document.getElementsByClassName('reply-text-area')){
		textarea.remove();
	}
	
	for(let addReplyButton of document.getElementsByClassName('reply-form-btn')){
		addReplyButton.removeAttribute('hidden');
	}
	
	for(submitButton of document.getElementsByClassName('submit-reply-btn')){
		submitButton.remove();
	}
	
	replyFormButton.setAttribute('hidden', 'true');
	
	submitButton.setAttribute('class', 'btn btn-primary submit-reply-btn');
	submitButton.innerText = 'Submit';
	replyForm.prepend(submitButton);
	
	replyFormTextArea.setAttribute('name', 'comment-body');
	replyFormTextArea.setAttribute('rows', '5');
	replyFormTextArea.setAttribute('columns', '50');
	replyFormTextArea.setAttribute('id', 'reply-text-area');
	replyFormTextArea.setAttribute('class', 'reply-text-area');
	replyForm.prepend(replyFormTextArea);
	
	
}