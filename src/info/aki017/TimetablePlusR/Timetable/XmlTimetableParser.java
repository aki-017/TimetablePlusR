package info.aki017.TimetablePlusR.Timetable;

import info.aki017.TimetablePlusR.Trace;
import info.aki017.TimetablePlusR.TimetableItem.TimetableItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.R.xml;
import android.util.Xml;

//TODO : 自分でも読めない
//TODO : 後で書く
public class XmlTimetableParser implements TimetableParser {
	private static String TAG = "PullParser";

	public XmlTimetableParser() {
	}

	@Override
	public boolean parse(InputStream in) {
		Timetable timetable = Timetable.getInstance();
		timetable.clear();
		
		XmlPullParser xmlPullParser = Xml.newPullParser();
		try {
			xmlPullParser.setInput(in, "UTF-8");
			// xmlPullParser.setInput(new
			// StringReader("<Timetable><item><direction>南草津</direction><way>笠山</way><time>6:50</time></item></Timetable>"));
		} catch (XmlPullParserException e) {
			Trace.e(TAG,"InputError");
			e.printStackTrace();
			return false;
		}

		try {
			int eventType;
			eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT) {
					Trace.d(TAG, "Start document");
				} else if (eventType == XmlPullParser.END_DOCUMENT) {
					Trace.e(TAG, "End document");
					Trace.e(TAG, "何かがおかしい");
					return false;
				} else if (eventType == XmlPullParser.START_TAG) {
					Trace.v("start tag :" + xmlPullParser.getName());
					if (xmlPullParser.getName().equalsIgnoreCase("Timetable")) {
						Trace.d(TAG,"Start Timetable :" + xmlPullParser.getAttributeValue( xmlPullParser.getNamespace(),"Station"));
						
						if(!getTimetable(xmlPullParser,xmlPullParser.getAttributeValue( xmlPullParser.getNamespace(),"Station").replaceAll(" ", ""))) return false;
					}
				} else if (eventType == XmlPullParser.END_TAG) {
					Trace.v("end tag");
				} else if (eventType == XmlPullParser.TEXT) {
					Trace.v(xmlPullParser.getText());
				}
				eventType = xmlPullParser.next();
			}
		} catch (Exception e) {
			Trace.d(TAG, "Error"+e.getMessage());
			Trace.e(e);
			return false;
		}
		return true;
	}

	private boolean getTimetable(XmlPullParser xmlPullParser,String station)
			throws XmlPullParserException, IOException {
		Trace.d(TAG, "getTimetable" + xmlPullParser.getDepth());
		Timetable timetable = Timetable.getInstance();

		int eventType;
		while ((eventType = xmlPullParser.next()) != XmlPullParser.END_TAG) {
			if (eventType == XmlPullParser.START_TAG) {
				if (xmlPullParser.getName().equalsIgnoreCase("ITEM")) {
					timetable.add(getItemData(xmlPullParser,station));
				}
				//Trace.d(TAG, "Start tag " + xmlPullParser.getName());
			} else if (eventType == XmlPullParser.END_TAG) {
				if (xmlPullParser.getName().equalsIgnoreCase("Timetable")) return false;
			}
		}
		return true;
	}

	private TimetableItem getItemData(XmlPullParser xmlPullParser,String station)
			throws XmlPullParserException, IOException {
		Trace.d(TAG, "getItemData" + xmlPullParser.getDepth());
		TimetableItem timetableItem = new TimetableItem();
		timetableItem.setNo(Integer.parseInt(xmlPullParser.getAttributeValue(xmlPullParser.getNamespace(), "Number")));
		int next;
		// Itemが閉じるまで
		while (((next = xmlPullParser.next()) != XmlPullParser.END_TAG)) {
			Trace.d(TAG, "getItemData_" + xmlPullParser.getDepth());
			// Itemの要素
			if (next == XmlPullParser.START_TAG) {
				Trace.d(TAG, "getItemData_ " + xmlPullParser.getName());
				if (xmlPullParser.getName().equalsIgnoreCase("DIRECTION")) {
					if (xmlPullParser.next() == XmlPullParser.TEXT)
						timetableItem.setDirection(xmlPullParser.getText());
						Trace.w(TAG,""+xmlPullParser.getText()+" -> "+timetableItem.getDirection().getName()+"");
				} else if (xmlPullParser.getName().equalsIgnoreCase("WAY")) {
					if (xmlPullParser.next() == XmlPullParser.TEXT) {
						timetableItem.setWay(xmlPullParser.getText().replaceAll(" ", ""));
						Trace.w(TAG,""+xmlPullParser.getText()+" -> "+timetableItem.getWay().getName()+"");
					}
				} else if (xmlPullParser.getName().equalsIgnoreCase("TIME")) {
					if (xmlPullParser.next() == XmlPullParser.TEXT) {
						String[] t = xmlPullParser.getText().replaceAll(" ", "").split(":");
						timetableItem.setTime(Integer.parseInt(t[0]) * 60
								+ Integer.parseInt(t[1]));
					}
				} else {
					Trace.d(TAG, "getItemData_ " + xmlPullParser.getName());
					xmlPullParser.next();
				}
				while (xmlPullParser.next() != XmlPullParser.END_TAG) {
				}

			}
		}
		timetableItem.setStation(station);
		Trace.d(TAG, timetableItem.getStation());
		Trace.d(TAG, timetableItem.getDirection().getName());
		Trace.d(TAG, timetableItem.getWay().getName());
		Trace.d(TAG, timetableItem.getTimeText());
		return timetableItem;
	}

	public static void getXmlFromInternet(URL url, OutputStream out)
			throws IOException {
		URLConnection connection = url.openConnection();
		InputStream in = connection.getInputStream();
		Trace.v("Start Download");
		int b;
		while ((b = in.read()) != -1) {
			out.write(b);
		}

		out.close();
		in.close();
	}

}
