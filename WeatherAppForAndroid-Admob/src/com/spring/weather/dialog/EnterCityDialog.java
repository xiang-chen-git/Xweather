package com.spring.weather.dialog;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;


import com.spring.weather.R;
import com.spring.weather.adapter.AutocompleteCustomArrayAdapter;
import com.spring.weather.app.Db;
import com.spring.weather.app.Utils;
import com.spring.weather.fragment.SettingsFragment;
import com.spring.weather.model.City;

public class EnterCityDialog extends Dialog {
	private Button  cancel,set;
	AutoCompleteTextView input;
	Context context;
	 ArrayList <City> cityData = new ArrayList <City>();
	AutocompleteCustomArrayAdapter adapter;
	public EnterCityDialog(Context context) {
		super(context);
		this.context = context;
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_enter_city);
    setTitle("Enter City Name");
    setCancelable(false);
    
    input = (AutoCompleteTextView) findViewById(R.id.et_city);
    set = (Button) findViewById(R.id.dialog_btn_set);
    set.setEnabled(false);
    input.addTextChangedListener(new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        	if(s.length()>2){
        		cityData = Db.getWords(s.toString());
        		adapter = new AutocompleteCustomArrayAdapter(context,R.layout.autocomplete_text, cityData);
        		input.setAdapter(adapter);
        		adapter.getFilter().filter(s.toString());
        		set.setEnabled(true);
        	}else{
        		set.setEnabled(false);
        	}
        		
        		
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        	
        }
    });
    cancel = (Button) findViewById(R.id.dialog_btn_cancel);
    cancel.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dismiss();
		}
	});
    
    set.setOnClickListener(new View.OnClickListener() {
		
		@Override
			public void onClick(View v) {
			boolean foundCity = false;
				String inputText = input.getText().toString().toLowerCase();
				removeLastEmptyChar(inputText);
				if (cityData.size() != 0) {
					for (int i = 0; i < cityData.size(); i++) {

						if (inputText.equals(cityData.get(i).getName().toLowerCase())) {
							Log.i("IF", "TRUE");
							Utils.setLastCityData(cityData.get(i), context);
							SettingsFragment.setCityNameSummary(cityData.get(i).getName());
							foundCity = true;
							dismiss();
							
						} else {
							foundCity = false;
						}
					}
					if(!foundCity){
						displayAlertDialog();
					}
					
				} else {
					displayAlertDialog();
				}

			}
	});
	}
	protected void displayAlertDialog() {
		new AlertDialog.Builder(context)
	    .setTitle(R.string.app_name)
	    .setMessage("Make sure that you select city from the list")
	    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	dialog.dismiss();
	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	     .show();
	}
	public String removeLastEmptyChar(String str) {

		  if (str.length() > 0 && str.charAt(str.length()-1)==' ') {
		    str = str.substring(0, str.length()-1);
		  }
		  return str;
		}
}