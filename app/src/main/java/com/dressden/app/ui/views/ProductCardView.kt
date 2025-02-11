package com.dressden.app.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.dressden.app.R
import com.dressden.app.data.models.Product
import com.dressden.app.utils.animations.ViewAnimationUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class ProductCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val productImage: ImageView
    private val productName: MaterialTextView
    private val productPrice: MaterialTextView
    private val productDescription: MaterialTextView
    private val addToCartButton: MaterialButton
    private val favoriteButton: MaterialButton
    
    private var isFavorite = false
    private var onAddToCartClickListener: ((Product) -> Unit)? = null
    private var onFavoriteClickListener: ((Product, Boolean) -> Unit)? = null
    private var currentProduct: Product? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_product_card, this, true)

        // Initialize views
        productImage = findViewById(R.id.productImage)
        productName = findViewById(R.id.productName)
        productPrice = findViewById(R.id.productPrice)
        productDescription = findViewById(R.id.productDescription)
        addToCartButton = findViewById(R.id.addToCartButton)
        favoriteButton = findViewById(R.id.favoriteButton)

        // Set up click listeners
        setupClickListeners()
    }

    private fun setupClickListeners() {
        addToCartButton.setOnClickListener {
            currentProduct?.let { product ->
                ViewAnimationUtils.bounce(addToCartButton)
                onAddToCartClickListener?.invoke(product)
            }
        }

        favoriteButton.setOnClickListener {
            currentProduct?.let { product ->
                isFavorite = !isFavorite
                updateFavoriteButton()
                ViewAnimationUtils.pulse(favoriteButton)
                onFavoriteClickListener?.invoke(product, isFavorite)
            }
        }
    }

    fun setProduct(product: Product) {
        currentProduct = product
        
        // Load product image using Glide
        Glide.with(context)
            .load(product.imageUrl)
            .placeholder(R.drawable.placeholder_product)
            .error(R.drawable.error_product)
            .into(productImage)

        // Set text fields
        productName.text = product.name
        productPrice.text = context.getString(R.string.price_format, product.price)
        productDescription.text = product.description

        // Animate the card when product is set
        ViewAnimationUtils.fadeIn(this)
    }

    private fun updateFavoriteButton() {
        val iconRes = if (isFavorite) {
            R.drawable.ic_favorite
        } else {
            R.drawable.ic_favorite_border
        }
        favoriteButton.setIconResource(iconRes)
    }

    fun setOnAddToCartClickListener(listener: (Product) -> Unit) {
        onAddToCartClickListener = listener
    }

    fun setOnFavoriteClickListener(listener: (Product, Boolean) -> Unit) {
        onFavoriteClickListener = listener
    }

    fun setFavorite(favorite: Boolean) {
        if (isFavorite != favorite) {
            isFavorite = favorite
            updateFavoriteButton()
        }
    }
}
