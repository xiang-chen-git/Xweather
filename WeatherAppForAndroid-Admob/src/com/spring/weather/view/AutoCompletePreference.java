package com.spring.weather.view;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AutoCompleteTextView;

public class AutoCompletePreference extends EditTextPreference {

private static AutoCompleteTextView mEditText = null;

public AutoCompletePreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    mEditText = new AutoCompleteTextView(context, attrs);
    mEditText.setThreshold(0);
    //The adapter of your choice

    //AutocompleteCustomArrayAdapter adapter = new AutocompleteCustomArrayAdapter(context, R.layout.autocomplete_text, MainActivity.getCitydata());
    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, COUNTRIES);
   // mEditText.setAdapter(adapter);
}


@Override
protected void onBindDialogView(View view) {
    AutoCompleteTextView editText = mEditText;
    editText.setText(getText());

    ViewParent oldParent = editText.getParent();
    if (oldParent != view) {
        if (oldParent != null) {
            ((ViewGroup) oldParent).removeView(editText);
        }
        onAddEditTextToDialogView(view, editText);
    }
}

@Override
protected void onDialogClosed(boolean positiveResult) {
    if (positiveResult) {
        String value = mEditText.getText().toString();
        if (callChangeListener(value)) {
            setText(value);
        }
    }
}
}