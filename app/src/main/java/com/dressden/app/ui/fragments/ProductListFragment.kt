package com.dressden.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dressden.app.R
import com.dressden.app.data.models.Product
import com.dressden.app.ui.adapters.ProductAdapter
import com.dressden.app.ui.viewmodels.ProductViewModel
import com.dressden.app.utils.animations.ViewAnimationUtils
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    private val viewModel: ProductViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView(view)
        setupObservers()
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.productRecyclerView)
        
        // Calculate the number of columns based on screen width
        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val columnWidth = 180 // Width of each card in dp
        val numColumns = (screenWidthDp / columnWidth).toInt()

        recyclerView.layoutManager = GridLayoutManager(requireContext(), numColumns)
        
        productAdapter = ProductAdapter(
            onAddToCart = { product ->
                viewModel.addToCart(product)
                showAddToCartConfirmation(product)
            },
            onFavoriteToggle = { product, isFavorite ->
                viewModel.toggleFavorite(product, isFavorite)
            }
        )
        
        recyclerView.adapter = productAdapter
    }

    private fun setupObservers() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products)
            ViewAnimationUtils.fadeIn(recyclerView)
        }

        viewModel.addToCartResult.observe(viewLifecycleOwner) { success ->
            if (!success) {
                showError(getString(R.string.error_add_to_cart))
            }
        }
    }

    private fun showAddToCartConfirmation(product: Product) {
        Snackbar.make(
            requireView(),
            getString(R.string.added_to_cart, product.name),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showError(message: String) {
        Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }
}
