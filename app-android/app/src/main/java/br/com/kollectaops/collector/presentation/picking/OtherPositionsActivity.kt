package br.com.kollectaops.collector.presentation.picking

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.kollectaops.collector.R
import br.com.kollectaops.collector.data.remote.dto.PosicaoSkuDto
import br.com.kollectaops.collector.data.remote.service.ApiService
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherPositionsViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    val positions = androidx.lifecycle.MutableLiveData<List<PosicaoSkuDto>>(emptyList())
    val loading   = androidx.lifecycle.MutableLiveData<Boolean>(false)
    val error     = androidx.lifecycle.MutableLiveData<String?>(null)

    fun load(skuId: Long) {
        viewModelScope.launch {
            loading.postValue(true)
            try {
                val resp = apiService.getPosicoesSkU(skuId)
                if (resp.isSuccessful) positions.postValue(resp.body() ?: emptyList())
                else error.postValue("Erro ao buscar posições")
            } catch (e: Exception) {
                error.postValue("Sem conexão")
            } finally {
                loading.postValue(false)
            }
        }
    }
}

@AndroidEntryPoint
class OtherPositionsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SKU_ID    = "sku_id"
        const val EXTRA_SKU_LABEL = "sku_label"
    }

    private val viewModel: OtherPositionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val skuId    = intent.getLongExtra(EXTRA_SKU_ID, -1L)
        val skuLabel = intent.getStringExtra(EXTRA_SKU_LABEL) ?: ""

        // Build simple layout programmatically — no extra XML needed
        val root = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(resources.getColor(R.color.bg, theme))
        }

        val tvTitle = TextView(this).apply {
            text = getString(R.string.title_other_positions)
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(resources.getColor(R.color.text_primary, theme))
        }
        val tvSku = TextView(this).apply {
            text = skuLabel
            textSize = 12f
            setTextColor(resources.getColor(R.color.text_secondary, theme))
            setPadding(0, 8, 0, 24)
        }
        val tvEmpty = TextView(this).apply {
            text = "Buscando posições..."
            textSize = 13f
            setTextColor(resources.getColor(R.color.text_secondary, theme))
            visibility = View.VISIBLE
        }
        val rvPositions = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@OtherPositionsActivity)
        }
        val btnBack = android.widget.Button(this).apply {
            text = getString(R.string.btn_back)
            setOnClickListener { finish() }
        }

        root.addView(tvTitle)
        root.addView(tvSku)
        root.addView(tvEmpty)
        root.addView(rvPositions)
        root.addView(btnBack)
        setContentView(root)

        viewModel.positions.observe(this) { list ->
            tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            if (list.isEmpty()) tvEmpty.text = "Sem posições disponíveis"
            rvPositions.adapter = PositionsAdapter(list)
        }
        viewModel.loading.observe(this) { loading ->
            if (loading) tvEmpty.text = "Buscando posições..."
        }
        viewModel.error.observe(this) { err ->
            if (err != null) tvEmpty.text = err
        }

        if (skuId > 0) viewModel.load(skuId)
    }

    inner class PositionsAdapter(private val items: List<PosicaoSkuDto>) :
            RecyclerView.Adapter<PositionsAdapter.VH>() {

        inner class VH(v: TextView) : RecyclerView.ViewHolder(v)

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): VH {
            val tv = TextView(parent.context).apply {
                setPadding(0, 16, 0, 16)
                textSize = 16f
                setTypeface(android.graphics.Typeface.MONOSPACE)
                setTextColor(resources.getColor(R.color.primary, theme))
            }
            return VH(tv)
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = items[position]
            (holder.itemView as TextView).text = "${item.codigo}  ·  ${item.quantidade} un."
        }
    }
}
