package fr.wikitahiti.teiki.wikitahiti.parser;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import fr.wikitahiti.teiki.wikitahiti.dao.InterestingPoint;

public class InterestPointParser {

	private AssetManager assetManager;


	public InterestPointParser(Context context) {
		assetManager = context.getAssets();
	}

	private String openJSON(String filename){
		int size;
		try {
			InputStream inputStream = assetManager.open(filename);
			size = inputStream.available();
			byte[] buffer = new byte[size];
			int read = inputStream.read(buffer);
			inputStream.close();
			if (read > 0)
				return new String(buffer, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public ArrayList<InterestingPoint> getLocalInterestingPoints(){
		ArrayList<InterestingPoint> res = new ArrayList<>();
		try {
			String [] list = assetManager.list("/");
			if (list != null) {
				for (String filename : list) {
					res.add(getDataFromJSON(openJSON(filename)));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}


	private InterestingPoint getDataFromJSON(String json){
		Type type = new TypeToken<InterestingPoint>(){}.getType();
		return new Gson().fromJson(String.valueOf(json), type);
	}
}
