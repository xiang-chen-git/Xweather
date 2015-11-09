package com.spring.weather;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.spring.weather.fragment.SettingsFragment;

public class SettingsActivity extends ActionBarActivity {
	LinearLayout main;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		getSupportActionBar().setIcon(R.drawable.ic_launcher_in);
		getSupportFragmentManager().beginTransaction().add(R.id.container, new SettingsFragment()).commit();
		ActionBar ab = getActionBar();
		ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#34495E")));
		ab.setDisplayShowHomeEnabled(true);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
	        finish();
			break;
	}
		return false;
	}
}
