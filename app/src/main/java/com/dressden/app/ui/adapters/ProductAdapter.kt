package com.dressden.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dressden.app.R
import com.dressden.app.data.models.Product
import com.dressden.app.databinding.ViewProductCardBinding
import com.dressden.app.utils.animations.ViewAnimationUtils

class ProductAdapter(
    private val onProductClick: (Product) -> Unit,
    private val onFavoriteClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ViewProductCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(
        private val binding: ViewProductCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onProductClick(getItem(position))
                }
            }

            binding.favoriteButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    ViewAnimationUtils.animateFavoriteButton(binding.favoriteButton)
                    onFavoriteClick(getItem(position))
                }
            }
        }

        fun bind(product: Product) {
            binding.apply {
                // Load product image
                Glide.with(productImage)
                    .load(product.thumbnail)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.placeholder_product)
                    .error(R.drawable.error_product)
                    .into(productImage)

                // Set product details
                productName.text = product.name
                productPrice.text = product.getFormattedPrice()
                
                // Show sale price if available
                if (product.isOnSale) {
                    originalPrice.isVisible = true
                    originalPrice.text = product.price.toString()
                    originalPrice.paint.isStrikeThruText = true
                    discountBadge.isVisible = true
                    discountBadge.text = "${product.discountPercentage}% OFF"
                } else {
                    originalPrice.isVisible = false
                    discountBadge.isVisible = false
                }

                // Show rating if available
                if (product.reviewCount > 0) {
                    ratingBar.isVisible = true
                    ratingBar.rating = product.rating
                    ratingCount.isVisible = true
                    ratingCount.text = "(${product.reviewCount})"
                } else {
                    ratingBar.isVisible = false
                    ratingCount.isVisible = false
                }

                // Show stock status
                when {
                    !product.isInStock -> {
                        stockStatus.isVisible = true
                        stockStatus.text = root.context.getString(R.string.product_out_of_stock)
                        stockStatus.setTextColor(root.context.getColor(R.color.error))
                    }
                    product.isLowStock() -> {
                        stockStatus.isVisible = true
                        stockStatus.text = "Only ${product.stockQuantity} left"
                        stockStatus.setTextColor(root.context.getColor(R.color.warning))
                    }
                    else -> {
                        stockStatus.isVisible = false
                    }
                }

                // Show badges
                newBadge.isVisible = product.isNewArrival
                featuredBadge.isVisible = product.isFeatured
                bestsellerBadge.isVisible = product.isBestseller

                // Show variants indicator
                variantsIndicator.isVisible = product.hasVariants
            }
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

    companion object {
        private const val TAG = "ProductAdapter"
    }
}
