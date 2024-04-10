package com.instar.frontend_android.ui.services

import com.google.firebase.database.*
import com.instar.frontend_android.ui.DTO.VideoCall

class VideoCallService {
    private val database = FirebaseDatabase.getInstance()
    private val videoCallRef = database.getReference("video-call")

    // Đăng nhập người dùng vào hệ thống
    fun loginUser(userId: String) {
        // Kiểm tra xem người dùng đã tồn tại trong hệ thống hay chưa
        videoCallRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Nếu người dùng đã tồn tại, cập nhật trạng thái thành "ONLINE"
                    videoCallRef.child(userId).child("status").setValue(VideoCall.Status.ONLINE.name)
                } else {
                    // Nếu người dùng chưa tồn tại, thêm thông tin của họ vào Firebase Realtime Database
                    val videoCall = VideoCall(videoCallRef.push().key , userId, VideoCall.Status.ONLINE)
                    videoCallRef.child(userId).setValue(videoCall)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        })
    }

    // Tạo một cuộc gọi video mới và lưu vào Firebase Realtime Database
    fun createVideoCall(videoCall: VideoCall) {
        val videoCallId = videoCallRef.push().key // Tạo ID mới cho cuộc gọi video
        videoCall.id = videoCallId
        videoCallRef.child(videoCallId!!).setValue(videoCall)
    }

    // Lắng nghe sự thay đổi trạng thái của cuộc gọi video
    fun listenForVideoCallStatus(videoCallId: String, callback: (VideoCall) -> Unit): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val videoCall = snapshot.getValue(VideoCall::class.java)
                videoCall?.let {
                    callback(videoCall)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        }

        val videoCallNode = videoCallRef.child(videoCallId)
        videoCallNode.addValueEventListener(listener)

        return listener
    }

    // Cập nhật trạng thái của cuộc gọi video
    fun updateVideoCallStatus(videoCallId: String, newStatus: VideoCall.Status) {
        val videoCallNode = videoCallRef.child(videoCallId)
        videoCallNode.child("status").setValue(newStatus.name)
    }

    // Xóa lắng nghe sự thay đổi trạng thái của cuộc gọi video
    fun removeVideoCallStatusListener(videoCallId: String, listener: ValueEventListener) {
        val videoCallNode = videoCallRef.child(videoCallId)
        videoCallNode.removeEventListener(listener)
    }
}
