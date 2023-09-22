package likelion.project.agijagi.sellermypage.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.ItemProductListBinding
import likelion.project.agijagi.model.ProductModel
import likelion.project.agijagi.sellermypage.ProductListFragment
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class ProductListAdapter(val context: Context) :
    ListAdapter<ProductModel, ProductListAdapter.ProductListViewHolder>(diffUtil) {

    val productStatusArray = context.resources.getStringArray(R.array.product_list_status)

    inner class ProductListViewHolder(val productListBinding: ItemProductListBinding) :
        RecyclerView.ViewHolder(productListBinding.root) {

        fun bind(item: ProductModel) {
            with(productListBinding) {
                val dec = DecimalFormat("#,###")
                item.thumbnail_image?.let { thumb ->
                    if (thumb.isNotBlank()) {
                        FirebaseStorage.getInstance().reference.child(thumb).downloadUrl.addOnSuccessListener {
                            Glide.with(context)
                                .load(it)
                                .into(imageViewProductListProduct)
                        }
                    }
                }

                dropdownTextViewProductListStatus.setText(item.state, false)
                dropdownTextViewProductListStatus.setOnItemClickListener { _, _, position, _ ->
                    val productState = productStatusArray[position]
                    item.state = productState
                    ProductListFragment().updateProductState(item, productState)
                }

                textViewProductListBrand.text = item.brand
                textViewProductListName.text = item.name
                textViewProductListPrice.text = "${dec.format(item.price.toLong())}원"

                val dateFormat1 = SimpleDateFormat("yyMMddHHmmssSSS", Locale.getDefault())
                val dateFormat2 = SimpleDateFormat("yy.MM.dd", Locale.getDefault())
                val dateData = item.updateDate
                val parseData = dateFormat1.parse(dateData)
                val productDate = dateFormat2.format(parseData)
                textViewProductListDate.text = productDate
            }

            productListBinding.root.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("prodId", item.productId)
                }
                it.findNavController()
                    .navigate(
                        R.id.action_productListFragment_to_sellerProductDetailFragment,
                        bundle
                    )
            }
        }

        init {
            productListBinding.run {
                // dropdownMenu customAdapter
                val arrayAdapter = ArrayAdapter(
                    (context as MainActivity),
                    R.layout.item_product_list_dropdown,
                    productStatusArray
                )
                dropdownTextViewProductListStatus.setAdapter(arrayAdapter)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val itemProductListBinding =
            ItemProductListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = ProductListViewHolder(itemProductListBinding)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ProductModel>() {
            override fun areItemsTheSame(
                oldItem: ProductModel,
                newItem: ProductModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ProductModel,
                newItem: ProductModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}