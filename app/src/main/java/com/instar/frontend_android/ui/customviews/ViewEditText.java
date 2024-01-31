package com.instar.frontend_android.ui.customviews;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.instar.frontend_android.R;

public class ViewEditText {
    public ViewEditText() {

    }

    public interface OnItemRemoveClick {
        void onFocusChange(View view);
    }
    public interface OnItemEyesClick {
        void onFocusChange(View view);
    }
    private OnItemRemoveClick listenerRemove;
    private OnItemEyesClick listenerEyes;

    public void setOnItemRemoveClick(OnItemRemoveClick listener) {
        this.listenerRemove = listener;
    }
    public void setOnItemEyesClick(OnItemEyesClick listener) {
        this.listenerEyes = listener;
    }

    public void setEditText(View layout, EditText editText, TextView label, ImageButton button, int number) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                label.setVisibility(View.VISIBLE);
                editText.setHint("");
                layout.setBackgroundResource(R.drawable.border_component_login_up);
                if (!editText.getText().toString().isEmpty()) button.setVisibility(View.VISIBLE);
                if (listenerRemove != null || listenerEyes != null) {
                    switch (number) {
                        case 1: listenerRemove.onFocusChange(v); break;
                        case 2: listenerEyes.onFocusChange(v); break;
                    }
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.getText().toString().isEmpty()) {
                    button.setVisibility(View.GONE);
                } else button.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void EditTextRemove(View layout, EditText editText, TextView label, ImageButton button) {
        setEditText(layout,editText,label,button, 1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                button.setVisibility(View.GONE);
            }
        });
    }
    public void EditTextEyes(View layout, EditText editText, TextView label, ImageButton button) {
        setEditText(layout,editText,label,button,2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cursorPosition = editText.getSelectionStart();
                if ((editText.getInputType() & InputType.TYPE_MASK_VARIATION) != InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                    button.setBackgroundResource(R.drawable.ic_instagram_eyes_off);
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                } else {
                    button.setBackgroundResource(R.drawable.ic_instagram_eyes);
                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                editText.setSelection(cursorPosition);
            }
        });
    }
}
