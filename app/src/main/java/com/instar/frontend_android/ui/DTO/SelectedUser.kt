package com.instar.frontend_android.ui.DTO

class SelectedUser {
    var user: User? = null
    var selected: Boolean = false

    constructor(user: User, selected: Boolean) {
        this.user = user
        this.selected = selected
    }
}