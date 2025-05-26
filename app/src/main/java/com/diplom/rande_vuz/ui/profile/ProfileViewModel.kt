package com.diplom.rande_vuz.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diplom.rande_vuz.models.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileViewModel : ViewModel() {
    private val dbRef = FirebaseDatabase.getInstance().getReference("users")
    private val _user = MutableLiveData<UserData?>()
    val user: LiveData<UserData?> = _user

    fun loadUser() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        dbRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(UserData::class.java)
                _user.value = data
            }
            override fun onCancelled(error: DatabaseError) {
                _user.value = null
            }
        })
    }
}