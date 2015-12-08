package network.core.controller;

import java.util.HashMap;
import java.util.Map;

import network.core.Message;
import network.core.connections.ClientConnection;
import network.core.connections.NetworkGameState;
import network.core.containers.NetworkContainer;
import network.framework.format.Request;

public class MessageHandler {
	
	private Map<Request, ExecuteHandler> myExecuters;
	
	public MessageHandler() {
		myExecuters = new HashMap<Request, ExecuteHandler>();
		init();
	}
	
	public ExecuteHandler getHandler(Request type) {
		return myExecuters.get(type);
	}
	
	private void init() {
		// TODO Handle add request
		myExecuters.put(Request.ADD, null);
		
		// TODO Handle delete request
		myExecuters.put(Request.DELETE, null);
		
		// TODO Handle modify request
		myExecuters.put(Request.MODIFY, null);
		
		// TODO Handle transition request
		myExecuters.put(Request.TRANSITION, null);
	}
	
	interface ExecuteHandler {	
		void executeMessage(Message message, NetworkContainer<ClientConnection> clients, NetworkContainer<NetworkGameState> games);		
	}

}
