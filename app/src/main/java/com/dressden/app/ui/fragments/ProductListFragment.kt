package com.dressden.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.dressden.app.R
import com.dressden.app.databinding.FragmentProductListBinding
import com.dressden.app.ui.adapters.ProductAdapter
import com.dressden.app.ui.viewmodels.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by viewModels()
    private val args: ProductListFragmentArgs by navArgs()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        setupFilterChips()
        observeData()
        handleArgs()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onProductClick = { product ->
                findNavController().navigate(
                    ProductListFragmentDirections.actionProductListToProductDetails(product.id)
                )
            },
            onFavoriteClick = { product ->
                // Handle favorite click
            }
        )

        binding.recyclerView.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshProducts()
        }
    }

    private fun setupFilterChips() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { group, checkedIds ->
            when (group.findViewById<Chip>(checkedIds.firstOrNull() ?: -1)?.id) {
                R.id.chipPrice -> showSortDialog("price")
                R.id.chipName -> showSortDialog("name")
                R.id.chipRating -> {
                    viewModel.setSortBy("rating", "desc")
                }
            }
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                binding.swipeRefresh.isRefreshing = state is ProductViewModel.ProductUiState.Loading
                binding.progressBar.isVisible = state is ProductViewModel.ProductUiState.Loading
                binding.errorLayout.root.isVisible = state is ProductViewModel.ProductUiState.Error

                if (state is ProductViewModel.ProductUiState.Error) {
                    binding.errorLayout.errorMessage.text = state.message
                    binding.errorLayout.retryButton.setOnClickListener {
                        viewModel.refreshProducts()
                    }
                }
            }
        }

        viewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products)
            binding.emptyView.isVisible = products.isEmpty()
        }

        // Observe filter options
        viewModel.availableBrands.observe(viewLifecycleOwner) { brands ->
            // Update brand filter options
        }

        viewModel.availableSizes.observe(viewLifecycleOwner) { sizes ->
            // Update size filter options
        }

        viewModel.availableColors.observe(viewLifecycleOwner) { colors ->
            // Update color filter options
        }
    }

    private fun handleArgs() {
        args.categoryId?.let { categoryId ->
            viewModel.setCategory(categoryId)
        }
    }

    private fun showSortDialog(sortBy: String) {
        val dialog = BottomSheetDialog(requireContext())
        // Inflate and setup sort dialog
        dialog.show()
    }

    private fun showFilterDialog() {
        val dialog = BottomSheetDialog(requireContext())
        // Inflate and setup filter dialog
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_product_list, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        setupSearchView(searchView)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                showFilterDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Optionally implement real-time search
                return false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ProductListFragment"
    }
}
