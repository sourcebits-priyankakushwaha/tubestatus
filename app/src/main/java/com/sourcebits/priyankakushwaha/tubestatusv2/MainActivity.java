package com.sourcebits.priyankakushwaha.tubestatusv2;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private final static String url = "http://cloud.tfl.gov.uk/TrackerNet/LineStatus";
    private ListView lineList;
    TubeAdapter tubeadapter;

    LineStatus obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new GetTubeStatusTask(this, url).execute();
    }

    public void callBackData( List<LineStatus> resultValue) {

        lineList = (ListView)findViewById(R.id.lv_listview); // activity_main.xml list view is attached with java code
        tubeadapter= new TubeAdapter(this, R.layout.single_row, (ArrayList<LineStatus>) resultValue);
        lineList.setAdapter(tubeadapter);


    }

}