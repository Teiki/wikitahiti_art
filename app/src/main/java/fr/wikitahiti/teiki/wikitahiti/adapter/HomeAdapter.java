package fr.wikitahiti.teiki.wikitahiti.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import fr.wikitahiti.teiki.wikitahiti.R;

public class HomeAdapter extends BaseAdapter {

    private final Context mContext;
    private String[] interestingPoints;

    // 1
    public HomeAdapter(Context context) {
        this.mContext = context;
        this.interestingPoints = context.getResources().getStringArray(R.array.main_buttons);
    }

    // 2
    @Override
    public int getCount() {
        return interestingPoints.length;
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView dummyTextView = new TextView(mContext);
        dummyTextView.setText(interestingPoints[position]);
        return dummyTextView;
    }

	public void refreshData(String[] buttonList) {
		this.interestingPoints = buttonList;
	}
}