package com.instar.frontend_android.interfaces

import com.instar.frontend_android.ui.DTO.User

interface InterfaceUtils {
    interface OnItemClickListener {
        fun onItemClick(user: User)
        fun onItemCloseClick(user: User)
    }
}