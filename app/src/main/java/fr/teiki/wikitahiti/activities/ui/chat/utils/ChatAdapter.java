package fr.teiki.wikitahiti.activities.ui.chat.utils;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import fr.teiki.wikitahiti.models.Message;
import fr.teiki.wikitahiti.wikitahiti.R;

/**
 * Created by Antoine GALTIER on 07/04/2020.
 */

public class ChatAdapter extends FirestoreRecyclerAdapter<Message, MessageViewHolder> {

	public interface Listener {
		void onDataChanged();
	}

	//FOR DATA
	private final RequestManager glide;
	private final String idCurrentUser;

	//FOR COMMUNICATION
	private Listener myCallback;

	public ChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options, RequestManager glide, Listener callback, String idCurrentUser) {
		super(options);
		this.glide = glide;
		myCallback = callback;
		this.idCurrentUser = idCurrentUser;
	}

	@Override
	protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message model) {
		holder.updateWithMessage(model, this.idCurrentUser, this.glide);
	}

	@NonNull
	@Override
	public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new MessageViewHolder(LayoutInflater.from(parent.getContext())
				.inflate(R.layout.activity_mentor_chat_item, parent, false));
	}

	@Override
	public void onDataChanged() {
		super.onDataChanged();
		myCallback.onDataChanged();
	}
}