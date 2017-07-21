package com.pointters.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.pointters.listener.OnEditTextChangeListener;

/**
 * Created by Vishal Sharma on 19-Nov-16.
 */

public class MyTextWatcher implements TextWatcher {
    private EditText editText;
    private OnEditTextChangeListener onEditTextChangeListener;

    public MyTextWatcher(EditText editText, OnEditTextChangeListener onEditTextChangeListener) {
        this.editText = editText;
        this.onEditTextChangeListener = onEditTextChangeListener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onEditTextChangeListener.onTextChange(s.toString(), editText);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}