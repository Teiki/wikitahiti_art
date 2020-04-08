package fr.teiki.wikitahiti.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.teiki.wikitahiti.api.UserHelper;
import fr.teiki.wikitahiti.models.User;
import fr.teiki.wikitahiti.wikitahiti.R;

/**
 * Created by Antoine GALTIER on 06/04/2020.
 */

public class ProfileActivity extends BaseActivity {

	//FOR DESIGN
	@BindView(R.id.profile_activity_imageview_profile)
	ImageView imageViewProfile;
	@BindView(R.id.profile_activity_edit_text_username)
	TextInputEditText textInputEditTextUsername;
	@BindView(R.id.profile_activity_text_view_email)
	TextView textViewEmail;
	@BindView(R.id.profile_activity_progress_bar)
	ProgressBar progressBar;
	@BindView(R.id.profile_activity_check_box_is_cool)
	CheckBox checkBoxIsCool;

	//FOR DATA
	//Identify each Http Request
	private static final int SIGN_OUT_TASK = 10;
	private static final int DELETE_USER_TASK = 20;
	private static final int UPDATE_USERNAME = 30;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getFragmentLayout());
		ButterKnife.bind(this);

		updateUIAccountInfo();
	}

	@Override
	public int getFragmentLayout() {
		return R.layout.activity_profile;
	}

	private void updateUIAccountInfo() {
		if (getCurrentUser() != null) {
			textViewEmail.setText(getCurrentUser().getEmail());
			textInputEditTextUsername.setText(getCurrentUser().getDisplayName());
		}
	}

	// --------------------
	// ACTIONS
	// --------------------

	@OnClick(R.id.profile_activity_button_update)
	public void onClickUpdateButton() { this.updateUsernameInFirebase(); }

	@OnClick(R.id.profile_activity_check_box_is_cool)
	public void onClickCheckBoxIsCool() { this.updateUserIsCool(); }

	@OnClick(R.id.profile_activity_button_sign_out)
	public void onClickSignOutButton() { this.signOutUserFromFirebase(); }

	@OnClick(R.id.profile_activity_button_delete)
	public void onClickDeleteButton() {
		new AlertDialog.Builder(this)
				.setMessage(R.string.popup_message_confirmation_delete_account)
				.setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						deleteUserFromFirebase();
					}
				})
				.setNegativeButton(R.string.popup_message_choice_no, null)
				.show();
	}

	// --------------------
	// REST REQUESTS
	// --------------------
	//

	private void signOutUserFromFirebase(){
		AuthUI.getInstance()
				.signOut(this)
				.addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
	}

	private void deleteUserFromFirebase(){
		if (getCurrentUser() != null) {
			UserHelper.deleteUser(getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());
			AuthUI.getInstance()
					.delete(this)
					.addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
		}
	}

	private void updateUsernameInFirebase(){

		this.progressBar.setVisibility(View.VISIBLE);
		String username = this.textInputEditTextUsername.getText().toString();

		if (getCurrentUser() != null){
			if (!username.isEmpty() &&  !username.equals(getString(R.string.info_no_username_found))){
				UserHelper.updateUsername(username, getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));
			}
		}
	}

	//Update User Cool (is or not)
	private void updateUserIsCool(){
		if (getCurrentUser() != null) {
			UserHelper.updateIsCool(getCurrentUser().getUid(), this.checkBoxIsCool.isChecked()).addOnFailureListener(this.onFailureListener());
		}
	}

	// --------------------
	// UI
	// --------------------

	// Arranging method that updating UI with Firestore data
	private void updateUIWhenCreating(){

		if (getCurrentUser() != null){

			if (getCurrentUser().getPhotoUrl() != null) {
				Glide.with(this)
						.load(getCurrentUser().getPhotoUrl())
						.apply(RequestOptions.circleCropTransform())
						.into(imageViewProfile);
			}

			String email = TextUtils.isEmpty(getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : getCurrentUser().getEmail();

			this.textViewEmail.setText(email);

			// Get additional data from Firestore (isCool & Username)
			UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
				@Override
				public void onSuccess(DocumentSnapshot documentSnapshot) {
					User currentUser = documentSnapshot.toObject(User.class);
					String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) : currentUser.getUsername();
					checkBoxIsCool.setChecked(currentUser.getIsCool());
					textInputEditTextUsername.setText(username);
				}
			});
		}
	}


	// Create OnCompleteListener called after tasks ended
	private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
		return aVoid -> {
			switch (origin){
				// 8 - Hiding Progress bar after request completed
				case UPDATE_USERNAME:
					progressBar.setVisibility(View.INVISIBLE);
					break;
				case SIGN_OUT_TASK:
					finish();
					break;
				case DELETE_USER_TASK:
					finish();
					break;
				default:
					break;
			}
		};
	}
}