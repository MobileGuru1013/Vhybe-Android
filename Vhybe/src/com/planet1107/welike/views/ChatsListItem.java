package com.planet1107.welike.views;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.planet1107.welike.R;
import com.planet1107.welike.activities.ChatActivity;
import com.planet1107.welike.connect.Chat;
import com.planet1107.welike.connect.Connect;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

public class ChatsListItem extends RelativeLayout implements OnClickListener {

	Chat mChat;	
	Connect sharedConnect;
	
	public ChatsListItem(Context context) {
		
		super(context);
	}

	public ChatsListItem(Context context, AttributeSet attrs) {
	
		super(context, attrs);
	}

	public ChatsListItem(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
	}

	public void setChat(Chat chat) {
		
		mChat = chat;	
		reloadView();
	}

	protected void onFinishInflate () {
		
	
        TextView textViewChatname = (TextView) findViewById(R.id.textViewUserUsername);
        ImageView imageViewChatimage = (ImageView)findViewById(R.id.imageViewUserUserImage);
       RelativeLayout RR_Parent=(RelativeLayout)findViewById(R.id.RR_Parent);
       RR_Parent.setClickable(true);
        textViewChatname.setClickable(true);
        imageViewChatimage.setClickable(true);
        textViewChatname.setOnClickListener(this);
        RR_Parent.setOnClickListener(this);
        imageViewChatimage.setOnClickListener(this);
		sharedConnect = Connect.getInstance(getContext());
	}
	
	@Override
	public void onClick(View v) {
		
		
			showChat();
		
		
	}
	
	private void reloadView() {		
		if(mChat.userdetail.userAvatarPath.equals("null")||mChat.userdetail.userAvatarPath.equals("")){
			((ImageView)findViewById(R.id.imageViewUserUserImage)).setImageResource(R.drawable.avatar_empty);
		}else{
        UrlImageViewHelper.setUrlDrawable(((ImageView)findViewById(R.id.imageViewUserUserImage)), mChat.userdetail.userAvatarPath);
		}
        ((TextView)findViewById(R.id.textViewUserUsername)).setText(mChat.userdetail.userFullName);
        ((TextView)findViewById(R.id.textViewMessage)).setText(mChat.text);
        ((TextView)findViewById(R.id.textViewTimestamp)).setText(mChat.timestamp);
        ((TextView)findViewById(R.id.textViewReadCount)).setText(mChat.unread+"");
	}
	
	public void showChat() {		
		Intent intentChat = new Intent(getContext(), ChatActivity.class);
		intentChat.putExtra("ChatUser", mChat.userdetail);
		getContext().startActivity(intentChat);
	}
}
