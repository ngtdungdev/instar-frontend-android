package com.instar.frontend_android.ui.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.instar.frontend_android.R;
import com.instar.frontend_android.ui.customviews.ViewEffect;

public class LoginActivity extends AppCompatActivity {
    private ImageButton btnNewAccount;
    private TextView textNewAccount;
    private ImageButton btnLoginAccount;
    private TextView textLoginAccount;
    private Button btnLoginFacebook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLoginFacebook = findViewById(R.id.btnLoginFacebook);
        btnNewAccount = findViewById(R.id.newAccount).findViewById(R.id.imageButton);
        textNewAccount = findViewById(R.id.newAccount).findViewById(R.id.textView);
        btnLoginAccount = findViewById(R.id.loginAccount).findViewById(R.id.imageButton);
        textLoginAccount = findViewById(R.id.loginAccount).findViewById(R.id.textView);
        loadActivity();
        initView();
    }

    public void loadActivity() {
        textNewAccount.setText("Tạo tài khoản mới");
        textLoginAccount.setText("Đăng nhập bằng tài khoản khác");
        btnLoginFacebook.setText("Tiếp tục bằng Facebook");
    }

    public void initView() {
        effectClick();
    }

    public void effectClick() {
        ViewEffect.ViewButton(btnNewAccount,textNewAccount);
        ViewEffect.ViewButton(btnLoginAccount,textLoginAccount);
    }
}
