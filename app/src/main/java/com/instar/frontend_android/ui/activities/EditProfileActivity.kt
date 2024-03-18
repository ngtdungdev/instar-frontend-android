package com.instar.frontend_android.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.instar.frontend_android.R

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val btn_editProfile1 = findViewById<ImageView>(R.id.btn_back)

        btn_editProfile1.setOnClickListener {
            val newPage = Intent(this@EditProfileActivity, ProfileActivity::class.java)
            startActivity(newPage)
        }
    }
}