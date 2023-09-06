package likelion.project.agijagi.sellermypage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import likelion.project.agijagi.MainActivity
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.ItemProductListBinding
import likelion.project.agijagi.sellermypage.model.ProductListModel

class ProductListAdapter(val context: Context) :
    ListAdapter<ProductListModel, ProductListAdapter.ProductListViewHolder>(diffUtil) {

    val productStatusArray = context.resources.getStringArray(R.array.product_list_status)

    inner class ProductListViewHolder(val productListBinding: ItemProductListBinding) :
        RecyclerView.ViewHolder(productListBinding.root) {

        fun bind(item: ProductListModel) {
            with(productListBinding) {
                dropdownTextViewProductListStatus.setText(item.state, false)
                textViewProductListBrand.text = item.brand
                textViewProductListName.text = item.name
                textViewProductListPrice.text = item.price
                textViewProductListDate.text = item.date
            }

            productListBinding.root.setOnClickListener {
                it.findNavController().navigate(R.id.action_productListFragment_to_sellerProductDetailFragment)
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
        val diffUtil = object : DiffUtil.ItemCallback<ProductListModel>() {
            override fun areItemsTheSame(
                oldItem: ProductListModel,
                newItem: ProductListModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ProductListModel,
                newItem: ProductListModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}