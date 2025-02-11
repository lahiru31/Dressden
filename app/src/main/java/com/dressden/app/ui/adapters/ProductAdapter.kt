package com.dressden.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dressden.app.R
import com.dressden.app.data.models.Product
import com.dressden.app.ui.views.ProductCardView

class ProductAdapter(
    private val onAddToCart: (Product) -> Unit,
    private val onFavoriteToggle: (Product, Boolean) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val productCardView = ProductCardView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return ProductViewHolder(productCardView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(
        private val productCardView: ProductCardView
    ) : RecyclerView.ViewHolder(productCardView) {

        init {
            productCardView.setOnAddToCartClickListener { product ->
                onAddToCart(product)
            }
            
            productCardView.setOnFavoriteClickListener { product, isFavorite ->
                onFavoriteToggle(product, isFavorite)
            }
        }

        fun bind(product: Product) {
            productCardView.setProduct(product)
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
