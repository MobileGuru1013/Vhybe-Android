package com.planet1107.welike.nodechat;

/**
 * Created by Aman on 06-05-2015.
 */
public class ChatModel {
   String  id, SenderMessage ,ReceiverMessage, Date ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderMessage() {
        return SenderMessage;
    }

    public void setSenderMessage(String senderMessage) {
        SenderMessage = senderMessage;
    }

    public String getReceiverMessage() {
        return ReceiverMessage;
    }

    public void setReceiverMessage(String receiverMessage) {
        ReceiverMessage = receiverMessage;
    }

    public ChatModel(String senderMessage, String receiverMessage,String Date) {
        SenderMessage = senderMessage;
        ReceiverMessage = receiverMessage;
        this.Date=Date;
    }

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
	}
}
