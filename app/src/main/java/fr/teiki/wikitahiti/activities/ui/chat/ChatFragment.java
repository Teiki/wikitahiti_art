package fr.teiki.wikitahiti.activities.ui.chat;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.teiki.wikitahiti.activities.BaseActivity;
import fr.teiki.wikitahiti.activities.ui.chat.utils.ChatAdapter;
import fr.teiki.wikitahiti.api.MessageHelper;
import fr.teiki.wikitahiti.api.UserHelper;
import fr.teiki.wikitahiti.models.Message;
import fr.teiki.wikitahiti.models.User;
import fr.teiki.wikitahiti.wikitahiti.R;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Antoine GALTIER on 07/04/2020.
 */

public class ChatFragment extends Fragment implements ChatAdapter.Listener {

	// FOR DESIGN
	// Getting all views needed
	@BindView(R.id.activity_mentor_chat_recycler_view)
	RecyclerView recyclerView;
	@BindView(R.id.activity_mentor_chat_text_view_recycler_view_empty)
	TextView textViewRecyclerViewEmpty;
	@BindView(R.id.activity_mentor_chat_message_edit_text)
	TextInputEditText editTextMessage;
	@BindView(R.id.activity_mentor_chat_image_chosen_preview)
	ImageView imageViewPreview;

	// FOR DATA
	//Declaring Adapter and data
	private ChatAdapter mentorChatAdapter;
	@Nullable
	private User modelCurrentUser;
	private String currentChatName;
	//Uri of image selected by user
	private Uri uriImageSelected;

	// STATIC DATA FOR CHAT (3)
	private static final String CHAT_NAME_ANDROID = "android";
	private static final String CHAT_NAME_BUG = "bug";
	private static final String CHAT_NAME_FIREBASE = "firebase";

	//ANG Reference to the baseactivity interface functions
	private BaseActivity myBaseActivity;

	//STATIC DATA FOR PICTURE
	private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
	private static final int RC_IMAGE_PERMS = 100;
	private static final int RC_CHOOSE_PHOTO = 200;




	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.activity_mentor_chat, container, false);
		ButterKnife.bind(this, root);

		myBaseActivity = (BaseActivity) getActivity();
		
		configureRecyclerView(CHAT_NAME_ANDROID);
		getCurrentUserFromFirestore();
				
		return root;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		//Forward results from permission asking to EasyPermissions plugin
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.handleResponse(requestCode, resultCode, data);
	}

	// --------------------
	// ACTIONS
	// --------------------

	@OnClick({ R.id.activity_mentor_chat_android_chat_button, R.id.activity_mentor_chat_firebase_chat_button, R.id.activity_mentor_chat_bug_chat_button})
	public void onClickChatButtons(ImageButton imageButton) {
		// 8 - Re-Configure the RecyclerView depending chosen chat
		switch (Integer.parseInt(imageButton.getTag().toString())){
			case 10:
				configureRecyclerView(CHAT_NAME_ANDROID);
				break;
			case 20:
				configureRecyclerView(CHAT_NAME_FIREBASE);
				break;
			case 30:
				configureRecyclerView(CHAT_NAME_BUG);
				break;
		}
	}

	@OnClick(R.id.activity_mentor_chat_add_file_button)
	//Ask permission when accessing to this listener
	@AfterPermissionGranted(RC_IMAGE_PERMS)
	public void onClickAddFile() {
		if (!EasyPermissions.hasPermissions(Objects.requireNonNull(getContext()), PERMS)) {
			EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
			//return;
		} else {
			this.chooseImageFromPhone();
		}
	}

	@OnClick(R.id.activity_mentor_chat_send_button)
	public void onClickSendMessage() {
		// Check if current user properly downloaded from Firestore
		if (modelCurrentUser != null){
			//Get textMessage
			String textMessage = (editTextMessage.getText() != null?editTextMessage.getText().toString():"");
			// Check if the ImageView is set
			if (imageViewPreview.getDrawable() != null) {
				// SEND A IMAGE + TEXT IMAGE
				uploadPhotoInFirebaseAndSendMessage(textMessage);
				imageViewPreview.setImageDrawable(null);
			} else if (!TextUtils.isEmpty(editTextMessage.getText())){
				// SEND A TEXT MESSAGE
				MessageHelper.createMessageForChat(textMessage,
						this.currentChatName,
						modelCurrentUser).addOnFailureListener(myBaseActivity.onFailureListener());
			}
			// Reset text field
			this.editTextMessage.setText("");
		}
	}

	// --------------------
	// REST REQUESTS
	// --------------------
	// 4 - Get Current User from Firestore
	private void getCurrentUserFromFirestore(){
		UserHelper.getUser(Objects.requireNonNull(myBaseActivity.getCurrentUser()).getUid()).addOnSuccessListener(documentSnapshot -> modelCurrentUser = documentSnapshot.toObject(User.class));
	}

	// Upload a picture in Firebase and send a message
	private void uploadPhotoInFirebaseAndSendMessage(final String message) {
		String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
		// UPLOAD TO GCS
		StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
		mImageRef.putFile(uriImageSelected)
				.addOnSuccessListener(Objects.requireNonNull(getActivity()), taskSnapshot -> {
					String pathImageSavedInFirebase = Objects.requireNonNull(taskSnapshot.getMetadata()).getPath();
					// SAVE MESSAGE IN FIRESTORE
					MessageHelper.createMessageWithImageForChat(
							pathImageSavedInFirebase,
							message,
							currentChatName,
							modelCurrentUser).addOnFailureListener(myBaseActivity.onFailureListener());
				})
				.addOnFailureListener(myBaseActivity.onFailureListener());
	}

	// --------------------
	// UI
	// --------------------
	// Configure RecyclerView with a Query
	private void configureRecyclerView(String chatName){
		//Track current chat name
		currentChatName = chatName;
		//Configure Adapter & RecyclerView
		mentorChatAdapter = new ChatAdapter(
				generateOptionsForAdapter(MessageHelper.getAllMessageForChat(currentChatName)),
				Glide.with(this),
				this, Objects.requireNonNull(myBaseActivity.getCurrentUser()).getUid());
		mentorChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				recyclerView.smoothScrollToPosition(mentorChatAdapter.getItemCount()); // Scroll to bottom on new messages
			}
		});
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setAdapter(mentorChatAdapter);
	}

	// 6 - Create options for RecyclerView from a Query
	private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
		return new FirestoreRecyclerOptions.Builder<Message>()
				.setQuery(query, Message.class)
				.setLifecycleOwner(this)
				.build();
	}


	// --------------------
	// FILE MANAGEMENT
	// --------------------

	private void chooseImageFromPhone(){
		if (!EasyPermissions.hasPermissions(Objects.requireNonNull(getContext()), PERMS)) {
			EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
			return;
		}
		// 3 - Launch an "Selection Image" Activity
		Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RC_CHOOSE_PHOTO);
	}

	// Handle activity response (after user has chosen or not a picture)
	private void handleResponse(int requestCode, int resultCode, Intent data){
		if (requestCode == RC_CHOOSE_PHOTO) {
			if (resultCode == RESULT_OK) { //SUCCESS
				this.uriImageSelected = data.getData();
				Glide.with(this) //SHOWING PREVIEW OF IMAGE
						.load(this.uriImageSelected)
						.apply(RequestOptions.circleCropTransform())
						.into(this.imageViewPreview);
			} else {
				Toast.makeText(getContext(), R.string.toast_title_no_image_chosen, Toast.LENGTH_SHORT).show();
			}
		}
	}

	// --------------------
	// CALLBACK
	// --------------------

	@Override
	public void onDataChanged() {
		// 7 - Show TextView in case RecyclerView is empty
		textViewRecyclerViewEmpty.setVisibility(mentorChatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
	}
}