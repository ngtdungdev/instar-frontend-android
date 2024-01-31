package com.instar.frontend_android.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.instar.frontend_android.R;
import com.instar.frontend_android.ui.customviews.ViewEditText;
import com.instar.frontend_android.ui.customviews.ViewEffect;

public class LoginSmsActivity extends AppCompatActivity {
    private ImageButton imageBack;
    private TextView btnText;

    private TextView title;
    private TextView textNote;
    private View layout;
    private View layoutSms;
    private EditText SmsText;
    private TextView labelSms;
    private ImageButton btnEyes;
    private TextView btnNewSms;
    private Button btnSms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sms);
        imageBack = findViewById(R.id.imageBack);
        layout = findViewById(R.id.layout);
        layoutSms = findViewById(R.id.sms);
        SmsText = layoutSms.findViewById(R.id.editText);
        labelSms = layoutSms.findViewById(R.id.textView);
        btnEyes = layoutSms.findViewById(R.id.btnImage);
        title =  findViewById(R.id.smsTitle).findViewById(R.id.Title);
        textNote =  findViewById(R.id.smsTitle).findViewById(R.id.textNote);
        btnText = findViewById(R.id.smsTitle).findViewById(R.id.btnText);
        btnNewSms = findViewById(R.id.btnNewSms);
        btnSms = findViewById(R.id.btnSms);
        loadActivity();
        initView();
    }
    public void loadActivity() {
        title.setText("Xác nhận tài khoản");
        textNote.setText("Chúng tôi đã gửi mã đến email của bạn.Hãy nhập mã đó để xác nhận tài khoản");
        btnText.setText("Không thể xác nhận tài khoản?");
        labelSms.setText("Nhập mã");
        btnNewSms.setText("Gửi lại mã");
        SmsText.setHint("Nhập mã");
        btnSms.setText("Tiếp tục");
        SmsText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btnEyes.setBackgroundResource(R.drawable.ic_instagram_eyes_off);
    }
    public void initView() {
        ViewEditText viewEditText = new ViewEditText();
        viewEditText.EditTextEyes(layoutSms,SmsText,labelSms,btnEyes);
        ViewEffect.ViewText(btnText);
        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                SmsText.clearFocus();
                if (SmsText.getText().toString().isEmpty()) {
                    setSms();
                }
            }
        });
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        effectClick();
    }

    public void setSms() {
        labelSms.setVisibility(View.GONE);
        layoutSms.setBackground(getDrawable(R.drawable.border_component_login_dow));
        SmsText.setHint("Nhập mã");
    }

    public void effectClick() {
        ViewEffect.ViewText(btnNewSms);
        ViewEffect.ImageBack(imageBack);
    }
}
