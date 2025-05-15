package com.diplom.rande_vuz.ui.lenta

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diplom.rande_vuz.databinding.ItemLentaBinding
import com.diplom.rande_vuz.models.Profile
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.diplom.rande_vuz.R

class ProfileAdapter : ListAdapter<Profile, ProfileAdapter.ViewHolder>(ProfileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLentaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemLentaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(profile: Profile) {
            with(binding) {
                tvName.text = profile.userData.name
                tvAgeUniversity.text = "${profile.age} год, ${profile.userData.vuzName}"

                llTags.removeAllViews()
                profile.tags.forEach { tag ->
                    val textView = TextView(binding.root.context).apply {
                        text = tag

                        setTextColor(ContextCompat.getColor(context, android.R.color.white))
                        background = ContextCompat.getDrawable(context, R.drawable.bg_tag)

                        val padding = context.resources.getDimensionPixelSize(R.dimen.tag_padding)
                        setPadding(padding, padding, padding, padding)

                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            marginEnd = context.resources.getDimensionPixelSize(R.dimen.tag_margin)
                        }
                    }
                    llTags.addView(textView)
                }

                tvDescriptionValue.text = profile.userData.description
                tvWorkplaceValue.text = profile.userData.work
                tvSkillsValue.text = profile.userData.skills
                tvActivitiesValue.text = profile.userData.extracurricular
            }
        }
    }

    class ProfileDiffCallback : DiffUtil.ItemCallback<Profile>() {
        override fun areItemsTheSame(oldItem: Profile, newItem: Profile) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Profile, newItem: Profile) = oldItem == newItem
    }
}