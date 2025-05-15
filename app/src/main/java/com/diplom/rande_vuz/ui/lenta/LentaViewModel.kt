package com.diplom.rande_vuz.ui.lenta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diplom.rande_vuz.models.Profile
import com.diplom.rande_vuz.models.UserData

class LentaViewModel : ViewModel() {
    private val _profiles = MutableLiveData<List<Profile>>()
    val profiles: LiveData<List<Profile>> get() = _profiles

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val mockList = List(20) { index ->
            Profile(
                id = index,
                userData = UserData(
                    name = "Катя ${index + 1}",
                    vuzName = "БФУ им. Канта",
                    work = "Школа №${index + 1}",
                    skills = "Английский, Турецкий",
                    extracurricular = "ЭКО-активизм",
                    description = "Ищу подработку в виде репетиторства..."
                ),
                tags = listOf("репетиторство", "дружба"),
                age = 21
            )
        }
        _profiles.value = mockList
    }
}