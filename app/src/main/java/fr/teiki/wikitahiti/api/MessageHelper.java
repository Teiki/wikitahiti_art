package fr.teiki.wikitahiti.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import fr.teiki.wikitahiti.models.Message;
import fr.teiki.wikitahiti.models.User;

/**
 * Created by Antoine GALTIER on 07/04/2020.
 */

public class MessageHelper {
	private static final String COLLECTION_NAME = "messages";

	// --- GET ---

	public static Query getAllMessageForChat(String chat){
		return ChatHelper.getChatCollection()
				.document(chat)
				.collection(COLLECTION_NAME)
				.orderBy("dateCreated")
				.limit(50);
	}

	public static Task<DocumentReference> createMessageForChat(String textMessage, String chat, User userSender){
		//Create the Message object
		Message message = new Message(textMessage, userSender);
		//Store Message to Firestore
		return ChatHelper.getChatCollection()
				.document(chat)
				.collection(COLLECTION_NAME)
				.add(message);
	}

	public static Task<DocumentReference> createMessageWithImageForChat(String urlImage, String textMessage, String chat, User userSender){

		// 1 - Creating Message with the URL image
		Message message = new Message(textMessage, urlImage, userSender);

		// 2 - Storing Message on Firestore
		return ChatHelper.getChatCollection()
				.document(chat)
				.collection(COLLECTION_NAME)
				.add(message);
	}
}
