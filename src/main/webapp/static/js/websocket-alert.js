
let categoriesUrl = 'ws://'+document.location.host + '/DIYWebsite/websocket/posts';
let webSocket;

function connectToUpdates(category, id=null){
	let socketConnectionUrl = categoriesUrl;
	let socketCallback;
	if(id){
		socketConnectionUrl += '/read/'+category+'/'+id;
		socketCallback = (msg) => onMessageUpdate(msg);
	}else{
		socketConnectionUrl += '/'+category;
		socketCallback = (msg) => onMessageAlert(msg);
	}
	webSocket = new WebSocket(socketConnectionUrl);
	webSocket.onmessage = socketCallback;
	
}

function onMessageAlert(msg){
	alert(msg.data);
}

function onMessageUpdate(msg){
	//alert(msg.data);
	console.log('updating:{'+msg+'}');
	$("#comments-display").load(location.href + "#comment-holder");
}

function disconnect(){
	webSocket.close();
	webSocket = null;
}