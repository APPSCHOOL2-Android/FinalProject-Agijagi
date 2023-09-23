package likelion.project.agijagi.wishlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import likelion.project.agijagi.R
import likelion.project.agijagi.databinding.ItemWishListBinding
import likelion.project.agijagi.model.UserModel
import likelion.project.agijagi.sellermypage.model.OrderManagementModel
import likelion.project.agijagi.wishlist.WishListFragment.Companion.dataSet
import java.text.DecimalFormat

class WishListAdapter(val itemClick: (WishListModel) -> Unit) :
    ListAdapter<WishListModel, WishListAdapter.ShoppingListViewHolder>(diffUtil) {
    val db = FirebaseFirestore.getInstance()

    inner class ShoppingListViewHolder(val bind: ItemWishListBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun bind(item: WishListModel) {
            with(bind) {
                val dec = DecimalFormat("#,###")

                textviewWishListBrand.text = item.brand
                textviewWishListName.text = item.name
                textviewWishListPrice.text = "${dec.format(item.price.toLong())}원"

                Glide.with(itemView)
                    .load(item.thumbnail)
                    .placeholder(R.drawable.wish_list_logo)
                    .into(bind.imageviewWishList)

                buttonWishListFavorite.isSelected = item.isCheck

                buttonWishListFavorite.setOnClickListener {
                    item.isCheck = item.isCheck != true

                    db.collection("buyer").document(UserModel.roleId)
                        .collection("wish")
                        .document(item.prodId).delete()

                    dataSet.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }
                // 추후 기성품, 주문 제작 상품 구분 필요
                root.setOnClickListener {
                    itemClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val itemWishListBinding =
            ItemWishListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ShoppingListViewHolder(itemWishListBinding)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<WishListModel>() {
            override fun areItemsTheSame(
                oldItem: WishListModel,
                newItem: WishListModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: WishListModel,
                newItem: WishListModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

//230916011944877
//
//230916012212464
//
//230916012555664
//
//230916013523863