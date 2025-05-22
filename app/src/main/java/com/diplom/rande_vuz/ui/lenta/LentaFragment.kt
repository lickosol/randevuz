package com.diplom.rande_vuz.ui.lenta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diplom.rande_vuz.databinding.FragmentLentaBinding
import  com.diplom.rande_vuz.models.UserData

class LentaFragment : Fragment() {

    private var _binding: FragmentLentaBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLentaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Временные тестовые данные
        val testUsers = listOf(
            UserData(
                name = "Алиса",
                birthDate = "01.01.2000",
                vuzName = "МГУ",
                specialization = "Физика",
                skills = "Python, C++",
                extracurricular = "Танцы, волонтерство",
                work = "Летняя практика в Яндексе",
                goal = "Хочу найти команду для проекта",
                description = "Люблю науку и людей",
                email = "alisa@example.com"
            ),
            UserData(
                name = "Иван",
                birthDate = "12.03.1999",
                vuzName = "СПбГУ",
                specialization = "История",
                skills = "Письмо, исследование",
                extracurricular = "Дебаты, квиз",
                work = "Работаю ассистентом",
                goal = "Хочу познакомиться",
                description = "Открыт к новым людям",
                email = "ivan@example.com"
            )
        )

        // Подключаем RecyclerView
        val recyclerView = binding.rvProfiles
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        recyclerView.adapter = LentaAdapter(testUsers)

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}