package com.example.shoujiweishi;

import android.app.Activity;
import android.os.Bundle;

public class SafeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe);
    }
}
