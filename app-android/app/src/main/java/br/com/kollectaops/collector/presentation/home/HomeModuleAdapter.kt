package br.com.kollectaops.collector.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.kollectaops.collector.databinding.ItemModuleBinding

class HomeModuleAdapter(private val modules: List<HomeModule>) :
    RecyclerView.Adapter<HomeModuleAdapter.ModuleViewHolder>() {

    inner class ModuleViewHolder(private val binding: ItemModuleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(module: HomeModule) {
            binding.tvModuleLabel.setText(module.labelRes)
            binding.ivModuleIcon.setImageResource(module.iconRes)
            binding.root.setOnClickListener { module.onClick() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding = ItemModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        holder.bind(modules[position])
    }

    override fun getItemCount() = modules.size
}
