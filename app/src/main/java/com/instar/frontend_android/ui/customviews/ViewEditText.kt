package com.instar.frontend_android.ui.customviews


import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.instar.frontend_android.R

class ViewEditText {
    interface OnItemClick {
        fun onEyesChange(view: View)
        fun onRemoveChange(view: View)
    }

    private var isFocusEditText:Boolean = false
    fun setFocus(isFocus: Boolean) {
        isFocusEditText = isFocus
    }
    fun returnFocus(): Boolean {
        return isFocusEditText
    }

    private var listenerFocus: OnItemClick? = null

    fun setOnItemFocusClick(listener: OnItemClick) {
        listenerFocus = listener
    }


    fun setEditTextUser(editText: EditText, button: ImageButton, number: Int) {
        editText.setOnFocusChangeListener { v, hasFocus ->
            editText.hint = ""
            if (editText.text.toString().isNotEmpty()) button.visibility = View.VISIBLE
            listenerFocus?.onRemoveChange(v)
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (editText.text.toString().isEmpty()) {
                    button.visibility = View.GONE
                } else button.visibility = View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun EditTextTag(editText: EditText, button: ImageButton) {
        setEditTextUser( editText, button, 1)
        button.setOnClickListener {
            editText.setText("")
            button.visibility = View.GONE
        }
    }


    fun setEditText(layout: View, editText: EditText, label: TextView, button: ImageButton, number: Int) {
        editText.setOnFocusChangeListener { v, hasFocus ->
            setFocus(true)
            label.visibility = View.VISIBLE
            editText.hint = ""
            layout.setBackgroundResource(R.drawable.border_component_login_up)
            if (!editText.text.toString().isEmpty()) button.visibility = View.VISIBLE
            if (listenerFocus != null) {
                when (number) {
                    1 -> listenerFocus?.onRemoveChange(v)
                    2 -> listenerFocus?.onEyesChange(v)
                }
            }
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (editText.text.toString().isEmpty()) {
                    button.visibility = View.GONE
                } else button.visibility = View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun EditTextRemove(layout: View, editText: EditText, label: TextView, button: ImageButton) {
        setEditText(layout, editText, label, button, 1)
        button.setOnClickListener {
            editText.setText("")
            button.visibility = View.GONE
        }
    }

    fun EditTextEyes(layout: View, editText: EditText, label: TextView, button: ImageButton) {
        setEditText(layout, editText, label, button, 2)
        button.setOnClickListener {
            val cursorPosition = editText.selectionStart
            if ((editText.inputType and InputType.TYPE_MASK_VARIATION) != InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                button.setBackgroundResource(R.drawable.ic_instagram_eyes_off)
                editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                button.setBackgroundResource(R.drawable.ic_instagram_eyes)
                editText.inputType = InputType.TYPE_CLASS_TEXT
            }
            editText.setSelection(cursorPosition)
        }
    }
}

