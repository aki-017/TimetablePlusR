package info.aki017.TimetablePlusR;

import java.util.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DetailAdapter extends ArrayAdapter<TimetableItem>{
	private LayoutInflater mInflater;
	private TextView mDirection;
	private TextView mWay;
	private TextView mCountDown;
	private TextView mTimeData;
	public DetailAdapter(Context context, Timetable objects) {
		super(context, 0, objects);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.detail_row, null);
		}
		final TimetableItem item = this.getItem(position);
		
		if(item != null){
			//行き先表示
			mDirection = (TextView)convertView.findViewById(R.id.detail_direction);
			mDirection.setText(String.format("%s行", item.getDirection().getName()) );

			//運行時間表示
			mTimeData = (TextView)convertView.findViewById(R.id.detail_time);
			mTimeData.setText(item.getTimeText());
			
			//カウントダウン表示
			mCountDown = (TextView)convertView.findViewById(R.id.detail_cost);
			Calendar calendar = Calendar.getInstance();
			int hour = calendar.get(Calendar.MINUTE);
			int minute = calendar.get(Calendar.SECOND);
			mCountDown.setText(String.format("(後%3s分)",(item.getTime()-(hour*60+minute))));

		}
		return convertView ;
	}
}