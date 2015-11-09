package com.spring.weather.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.spring.weather.R;
import com.spring.weather.app.Utils;

public class ThemeDialog extends Dialog {
	private Button  cancel,save;
	Context context;
	String color;
	public ThemeDialog(Context context) {
		super(context);
		this.context = context;
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_theme);
    setTitle("Select theme");
    setCancelable(false);
    
    cancel = (Button) findViewById(R.id.bt_cancel);
    save = (Button) findViewById(R.id.bt_save);
    final RadioButton rb1 = (RadioButton) findViewById(R.id.color_1);
	final RadioButton rb2 = (RadioButton) findViewById(R.id.color_2);
	final RadioButton rb3 = (RadioButton) findViewById(R.id.color_3);
	final RadioButton rb4 = (RadioButton) findViewById(R.id.color_4);
	final RadioButton rb5 = (RadioButton) findViewById(R.id.color_5);
	
	//get color
	color = Utils.getColor(context);
	
	if(color.equalsIgnoreCase("#1ABC9C")){
		rb1.setChecked(true);
	}if(color.equalsIgnoreCase("#3498DB")){
		rb2.setChecked(true);
	}if(color.equalsIgnoreCase("#34495E")){
		rb3.setChecked(true);
	}if(color.equalsIgnoreCase("#8E44AD")){
		rb4.setChecked(true);
	}else if(color.equalsIgnoreCase("#E67E22")){
		rb5.setChecked(true);
	}
	
    cancel.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			dismiss();
		}

	
    	
    });
    save.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			
			if(rb1.isChecked()){
				color ="#1ABC9C";
			}if(rb2.isChecked()){
				color="#3498DB";
			}if(rb3.isChecked()){
				color="#34495E";
			}if(rb4.isChecked()){
				color="#8E44AD";
			}else if(rb5.isChecked()){
				color="#E67E22";
			}
			Utils.setColor(context,color);
			dismiss();
		}

	
    	
    });
	}
}