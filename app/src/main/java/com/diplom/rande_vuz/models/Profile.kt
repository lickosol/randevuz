package com.diplom.rande_vuz.models

import java.io.Serializable

data class Profile(
    val id: Int,
    val userData: UserData,
    val tags: List<String>,
    val age: Int // Можно вычислять из birthDate, но пока заглушка
) : Serializable