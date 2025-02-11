package com.dressden.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dressden.app.data.models.Product
import com.dressden.app.data.repository.ProductRepository
import com.dressden.app.utils.notifications.NotificationManagerUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val notificationManager: NotificationManagerUtil
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _addToCartResult = MutableLiveData<Boolean>()
    val addToCartResult: LiveData<Boolean> = _addToCartResult

    private val _sortOrder = MutableStateFlow(SortOrder.NAME_ASC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    private val _filterCategory = MutableStateFlow<String?>(null)
    val filterCategory: StateFlow<String?> = _filterCategory

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val products = productRepository.getProducts()
                _products.value = applyFilters(products)
                
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            try {
                val result = productRepository.addToCart(product)
                _addToCartResult.value = result
                
                if (result) {
                    notificationManager.showOrderStatusNotification(
                        product.id,
                        "added to cart"
                    )
                }
            } catch (e: Exception) {
                _addToCartResult.value = false
                _error.value = e.message
            }
        }
    }

    fun toggleFavorite(product: Product, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                productRepository.updateFavorite(product.id, isFavorite)
                // Refresh products to reflect the change
                loadProducts()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateSortOrder(order: SortOrder) {
        viewModelScope.launch {
            _sortOrder.value = order
            _products.value = applyFilters(products.value ?: emptyList())
        }
    }

    fun updateFilterCategory(category: String?) {
        viewModelScope.launch {
            _filterCategory.value = category
            _products.value = applyFilters(products.value ?: emptyList())
        }
    }

    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            _searchQuery.value = query
            _products.value = applyFilters(products.value ?: emptyList())
        }
    }

    private fun applyFilters(products: List<Product>): List<Product> {
        var filteredProducts = products

        // Apply category filter
        filterCategory.value?.let { category ->
            filteredProducts = filteredProducts.filter { it.category == category }
        }

        // Apply search filter
        if (searchQuery.value.isNotEmpty()) {
            filteredProducts = filteredProducts.filter {
                it.name.contains(searchQuery.value, ignoreCase = true) ||
                it.description.contains(searchQuery.value, ignoreCase = true)
            }
        }

        // Apply sorting
        filteredProducts = when (sortOrder.value) {
            SortOrder.NAME_ASC -> filteredProducts.sortedBy { it.name }
            SortOrder.NAME_DESC -> filteredProducts.sortedByDescending { it.name }
            SortOrder.PRICE_LOW -> filteredProducts.sortedBy { it.price }
            SortOrder.PRICE_HIGH -> filteredProducts.sortedByDescending { it.price }
            SortOrder.NEWEST -> filteredProducts.sortedByDescending { it.createdAt }
        }

        return filteredProducts
    }

    enum class SortOrder {
        NAME_ASC,
        NAME_DESC,
        PRICE_LOW,
        PRICE_HIGH,
        NEWEST
    }
}
