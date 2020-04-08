package fr.teiki.wikitahiti.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import fr.teiki.wikitahiti.api.MessageHelper;
import fr.teiki.wikitahiti.api.UserHelper;
import fr.teiki.wikitahiti.chat.ChatAdapter;
import fr.teiki.wikitahiti.models.Message;
import fr.teiki.wikitahiti.models.User;
import fr.teiki.wikitahiti.wikitahiti.R;

/**
 * Created by Antoine GALTIER on 07/04/2020.
 */

public class ChatActivity extends BaseActivity implements ChatAdapter.Listener {

	// FOR DESIGN
	// 1 - Getting all views needed
	@BindView(R.id.activity_mentor_chat_recycler_view)
	RecyclerView recyclerView;
	@BindView(R.id.activity_mentor_chat_text_view_recycler_view_empty)
	TextView textViewRecyclerViewEmpty;
	@BindView(R.id.activity_mentor_chat_message_edit_text)
	TextInputEditText editTextMessage;
	@BindView(R.id.activity_mentor_chat_image_chosen_preview)
	ImageView imageViewPreview;

	// FOR DATA
	// 2 - Declaring Adapter and data
	private ChatAdapter mentorChatAdapter;
	@Nullable
	private User modelCurrentUser;
	private String currentChatName;

	// STATIC DATA FOR CHAT (3)
	private static final String CHAT_NAME_ANDROID = "android";
	private static final String CHAT_NAME_BUG = "bug";
	private static final String CHAT_NAME_FIREBASE = "firebase";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.configureRecyclerView(CHAT_NAME_ANDROID);
		this.configureToolbar();
		this.getCurrentUserFromFirestore();
	}

	@Override
	public int getFragmentLayout() { return R.layout.activity_mentor_chat; }

	// --------------------
	// ACTIONS
	// --------------------

	@OnClick(R.id.activity_mentor_chat_send_button)
	public void onClickSendMessage() { }

	@OnClick({ R.id.activity_mentor_chat_android_chat_button, R.id.activity_mentor_chat_firebase_chat_button, R.id.activity_mentor_chat_bug_chat_button})
	public void onClickChatButtons(ImageButton imageButton) {
		// 8 - Re-Configure the RecyclerView depending chosen chat 
		switch (Integer.parseInt(imageButton.getTag().toString())){
			case 10:
				this.configureRecyclerView(CHAT_NAME_ANDROID);
				break;
			case 20:
				this.configureRecyclerView(CHAT_NAME_FIREBASE);
				break;
			case 30:
				this.configureRecyclerView(CHAT_NAME_BUG);
				break;
		}
	}

	@OnClick(R.id.activity_mentor_chat_add_file_button)
	public void onClickAddFile() { }

	// --------------------
	// REST REQUESTS
	// --------------------
	// 4 - Get Current User from Firestore
	private void getCurrentUserFromFirestore(){
		UserHelper.getUser(Objects.requireNonNull(getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
			@Override
			public void onSuccess(DocumentSnapshot documentSnapshot) {
				modelCurrentUser = documentSnapshot.toObject(User.class);
			}
		});
	}

	// --------------------
	// UI
	// --------------------
	// 5 - Configure RecyclerView with a Query
	private void configureRecyclerView(String chatName){
		//Track current chat name
		this.currentChatName = chatName;
		//Configure Adapter & RecyclerView
		this.mentorChatAdapter = new ChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessageForChat(this.currentChatName)), Glide.with(this), this, this.getCurrentUser().getUid());
		mentorChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				recyclerView.smoothScrollToPosition(mentorChatAdapter.getItemCount()); // Scroll to bottom on new messages
			}
		});
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(this.mentorChatAdapter);
	}

	// 6 - Create options for RecyclerView from a Query
	private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
		return new FirestoreRecyclerOptions.Builder<Message>()
				.setQuery(query, Message.class)
				.setLifecycleOwner(this)
				.build();
	}

	// --------------------
	// CALLBACK
	// --------------------

	@Override
	public void onDataChanged() {
		// 7 - Show TextView in case RecyclerView is empty
		textViewRecyclerViewEmpty.setVisibility(this.mentorChatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
	}
}