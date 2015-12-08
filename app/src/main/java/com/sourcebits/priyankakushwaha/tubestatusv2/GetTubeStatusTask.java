package com.sourcebits.priyankakushwaha.tubestatusv2;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetTubeStatusTask extends AsyncTask<String[], Void, List<LineStatus>> {
    private MainActivity activity;
    private String url;
    private XmlPullParserFactory xmlFactoryObject;
    private ProgressDialog pDialog;

    public GetTubeStatusTask(MainActivity activity, String url) {
        this.activity = activity;
        this.url = url;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(activity);
        pDialog.setTitle("Get tube status from XML");
        pDialog.setMessage("Loading...");
        pDialog.show();
    }

    @Override
    protected List<LineStatus> doInBackground(String[]... params) {
        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            InputStream stream = connection.getInputStream();

            xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = xmlFactoryObject.newPullParser();

            xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xpp.setInput(stream, null);

            //Parsing code
            List<LineStatus> result = parseXML(xpp);
            stream.close();

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AsyncTask", "exception");
            return null;
        }
    }

    public List<LineStatus> parseXML(XmlPullParser xpp) {

        final String KEY_LINE_STATUS = "LineStatus";
        // List of StackSites that we will return

        List<LineStatus> lineStatus;
        lineStatus = new ArrayList<LineStatus>();

        // temp holder for current LineStatus while parsing

        LineStatus curLineStatus = null;

        // temp holder for current text value while parsing

        String curText = "";
        String lineName;
        String statusDesc;
        String statusDetail;

        try {

            // get initial eventType
            int eventType = xpp.getEventType();

            // Loop through pull events until we reach END_DOCUMENT
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // Get the current tag
                String tagname = xpp.getName();

                // React to different event types appropriately
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase(KEY_LINE_STATUS)) {
                            // If we are starting a new <site> block we need
                            //a new StackSite object to represent it
                            curLineStatus = new LineStatus();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        //grab the current text so we can use it in END_TAG event
                        curText = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("LineStatus")) {

                            lineStatus.add(curLineStatus);

                        } else if (tagname.equalsIgnoreCase("Line")) {
                           lineName = xpp.getAttributeValue(null, "Name");
                            curLineStatus.setLineName(lineName);


                        } else if (tagname.equalsIgnoreCase("Status")) {
                             statusDesc = xpp.getAttributeValue(null, "Description");
                            curLineStatus.setStatusDescription(statusDesc);
                        } else if (tagname.equalsIgnoreCase("LineStatus")) {
                           statusDetail = xpp.getAttributeValue(null, "StatusDetails");
                            curLineStatus.setStatusDetails(statusDetail);
                        }
                        break;

                    default:
                        break;
                }
                //move on to next iteration
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // return the populated list.
        return lineStatus;
    }


    @Override
    protected void onPostExecute(List<LineStatus> result) {
        //call back data to main thread
        pDialog.dismiss();
        activity.callBackData(result);

    }
}
