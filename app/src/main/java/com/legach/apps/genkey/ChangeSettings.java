package com.legach.apps.genkey;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class ChangeSettings extends ActionBarActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        }

    }