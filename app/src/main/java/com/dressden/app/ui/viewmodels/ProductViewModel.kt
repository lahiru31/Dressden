package com.dressden.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dressden.app.data.models.Product
import com.dressden.app.data.repository.ProductRepository
import com.dressden.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Initial)
    val uiState: StateFlow<ProductUiState> = _uiState

    // Filters
    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow<String?>(null)
    private val _sortBy = MutableStateFlow<String?>(null)
    private val _sortOrder = MutableStateFlow<String?>(null)
    private val _priceRange = MutableStateFlow(0.0 to Double.MAX_VALUE)
    private val _selectedBrand = MutableStateFlow<String?>(null)
    private val _selectedSize = MutableStateFlow<String?>(null)
    private val _selectedColor = MutableStateFlow<String?>(null)

    // Product Lists
    val products = combine(
        _selectedCategory,
        _searchQuery,
        _sortBy,
        _sortOrder
    ) { category, query, sortBy, sortOrder ->
        loadProducts(category, query, sortBy, sortOrder)
    }.asLiveData()

    val featuredProducts = productRepository.getFeaturedProducts().asLiveData()
    val newArrivals = productRepository.getNewArrivals().asLiveData()
    val bestsellers = productRepository.getBestsellers().asLiveData()

    // Filter Options
    val availableBrands = productRepository.getAllBrands().asLiveData()
    val availableSizes = productRepository.getAllSizes().asLiveData()
    val availableColors = productRepository.getAllColors().asLiveData()

    // Selected Product
    private val _selectedProduct = MutableLiveData<Product?>()
    val selectedProduct: LiveData<Product?> = _selectedProduct

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            try {
                // Load initial data
                _uiState.value = ProductUiState.Success
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error(e.message ?: "Failed to load initial data")
            }
        }
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String?) {
        _searchQuery.value = query
    }

    fun setSortBy(sortBy: String?, sortOrder: String? = "asc") {
        _sortBy.value = sortBy
        _sortOrder.value = sortOrder
    }

    fun setPriceRange(min: Double, max: Double) {
        _priceRange.value = min to max
    }

    fun setBrand(brand: String?) {
        _selectedBrand.value = brand
    }

    fun setSize(size: String?) {
        _selectedSize.value = size
    }

    fun setColor(color: String?) {
        _selectedColor.value = color
    }

    fun selectProduct(productId: String) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            when (val result = productRepository.getProduct(productId)) {
                is Resource.Success -> {
                    _selectedProduct.value = result.data
                    _uiState.value = ProductUiState.Success
                }
                is Resource.Error -> {
                    _uiState.value = ProductUiState.Error(result.message)
                }
            }
        }
    }

    fun clearSelectedProduct() {
        _selectedProduct.value = null
    }

    fun addReview(productId: String, rating: Float, comment: String) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            when (val result = productRepository.addReview(productId, rating, comment)) {
                is Resource.Success -> {
                    _selectedProduct.value = result.data
                    _uiState.value = ProductUiState.Success
                }
                is Resource.Error -> {
                    _uiState.value = ProductUiState.Error(result.message)
                }
            }
        }
    }

    fun refreshProducts() {
        loadProducts(_selectedCategory.value, _searchQuery.value, _sortBy.value, _sortOrder.value, true)
    }

    private fun loadProducts(
        category: String?,
        query: String?,
        sortBy: String?,
        sortOrder: String?,
        forceRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            try {
                productRepository.getProducts(
                    category = category,
                    query = query,
                    sortBy = sortBy,
                    sortOrder = sortOrder,
                    forceRefresh = forceRefresh
                )
                _uiState.value = ProductUiState.Success
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error(e.message ?: "Failed to load products")
            }
        }
    }

    sealed class ProductUiState {
        object Initial : ProductUiState()
        object Loading : ProductUiState()
        object Success : ProductUiState()
        data class Error(val message: String) : ProductUiState()
    }

    companion object {
        private const val TAG = "ProductViewModel"
    }
}
