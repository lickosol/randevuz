package com.diplom.rande_vuz.models

import com.google.firebase.database.PropertyName
import java.io.Serializable

data class UserData(
    var name: String = "",
    var birthDate: String = "",
    @get:PropertyName("vuz_name")
    @set:PropertyName("vuz_name")
    var vuzName: String = "",
    var specialization: String = "",
    var skills: String = "",
    var extracurricular: String = "",
    var work: String = "",
    var goal: Any = "",
    var description: String = "",
    var email: String = ""
) : Serializable