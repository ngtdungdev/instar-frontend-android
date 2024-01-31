package com.instar.frontend_android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.instar.frontend_android.R;
import com.instar.frontend_android.ui.customviews.ViewEditText;
import com.instar.frontend_android.ui.customviews.ViewEffect;

public class LoginEmailActivity extends AppCompatActivity {
    private ImageButton imageBack;
    private TextView btnText;

    private TextView title;
    private TextView textNote;
    private View layout;
    private View layoutEmail;
    private EditText emailText;
    private TextView labelEmail;
    private ImageButton btnRemove;
    private Button btnFindAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);
        imageBack = findViewById(R.id.imageBack);
        layout = findViewById(R.id.layout);
        layoutEmail = findViewById(R.id.email);
        emailText = layoutEmail.findViewById(R.id.editText);
        labelEmail = layoutEmail.findViewById(R.id.textView);
        btnRemove = layoutEmail.findViewById(R.id.btnImage);
        title =  findViewById(R.id.emailTitle).findViewById(R.id.Title);
        textNote =  findViewById(R.id.emailTitle).findViewById(R.id.textNote);
        btnText = findViewById(R.id.emailTitle).findViewById(R.id.btnText);
        btnFindAccount = findViewById(R.id.btnFindAccount);
        loadActivity();
        initView();
    }
    public void loadActivity() {
        title.setText("Tìm tài khoản");
        textNote.setText("Nhập tên người dùng, email hoặc số di động của bạn");
        btnText.setText("Bạn không thể đặt lại mật khẩu?");
        labelEmail.setText("Tên người dùng, email/số di động");
        emailText.setHint("Tên người dùng, email/số di động");
        btnFindAccount.setText("Tìm tài khoản");
    }
    public void initView() {
        ViewEditText viewEditText = new ViewEditText();
        viewEditText.EditTextRemove(layoutEmail,emailText,labelEmail,btnRemove);
        ViewEffect.ViewText(btnText);
        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnFindAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginEmailActivity.this, LoginSmsActivity.class);
                startActivity(intent);
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                emailText.clearFocus();
                if (emailText.getText().toString().isEmpty()) {
                    setEmail();
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

    public void setEmail() {
        labelEmail.setVisibility(View.GONE);
        layoutEmail.setBackground(getDrawable(R.drawable.border_component_login_dow));
        emailText.setHint("Tên người dùng, email/số di động");
    }

    public void effectClick() {
        ViewEffect.ImageBack(imageBack);
    }
}