package com.diplom.rande_vuz.ui.lenta

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diplom.rande_vuz.models.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LentaViewModel : ViewModel() {

    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private val _users = MutableLiveData<List<UserData>>()
    val users: LiveData<List<UserData>> get() = _users

    init {
        loadUsers()
    }

    private fun loadUsers() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("LentaViewModel", "Current user UID: $currentUserId")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = mutableListOf<UserData>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(UserData::class.java)
                    val uid = userSnapshot.key

                    Log.d("LentaViewModel", "UID: $uid | user: $user")

                    // ВРЕМЕННО: добавляем всех пользователей (включая текущего)
                    if (user != null) {
                        userList.add(user)
                    }
                    // ПОСЛЕ ОТЛАДКИ включи фильтрацию:
                    // if (uid != null && uid != currentUserId && user != null) {
                    //     userList.add(user)
                    // }
                }
                Log.d("LentaViewModel", "Total users loaded: ${userList.size}")
                _users.value = userList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("LentaViewModel", "Database error: ${error.message}")
            }
        })
    }
}
