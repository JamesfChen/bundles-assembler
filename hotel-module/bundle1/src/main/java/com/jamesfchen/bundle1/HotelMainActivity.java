package com.jamesfchen.bundle1;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.perf.metrics.AddTrace;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Jun/13/2021  Sun
 */
public class HotelMainActivity extends Activity {
    @AddTrace(name = "HotelMaiinActivity_onCreate", enabled = true /* optional */)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("hahahhaha hotel main");
        tv.setTextColor(Color.BLACK);
        setContentView(tv);
    }

}
