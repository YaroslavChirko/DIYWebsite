
let categoriesUrl = 'ws://'+document.location.host + '/DIYWebsite/websocket/posts';
let webSocket;

function connectToUpdates(category){
	console.log(categoriesUrl+'/'+category);
	webSocket = new WebSocket(categoriesUrl+'/'+category);
	
	webSocket.onmessage = (msg) => onMessage(msg);
}

function onMessage(msg){
	alert(msg.data);
}

function disconnect(){
	webSocket.close();
	webSocket = null;
}