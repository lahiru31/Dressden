package com.dressden.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dressden.app.data.api.ApiService;
import com.dressden.app.data.local.AppDatabase;
import com.dressden.app.data.models.Product;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import kotlinx.coroutines.flow.Flow;
import retrofit2.Response;

@Singleton
public class ProductRepository {
    private static final long CACHE_TIMEOUT = TimeUnit.MINUTES.toMillis(15);

    private final ApiService apiService;
    private final AppDatabase appDatabase;
    private long lastCacheUpdate = 0;

    @Inject
    public ProductRepository(ApiService apiService, AppDatabase appDatabase) {
        this.apiService = apiService;
        this.appDatabase = appDatabase;
    }

    public Flow<List<Product>> getProducts(String category) {
        refreshProductsIfNeeded();
        return category == null ? 
            appDatabase.productDao().getAllProducts() :
            appDatabase.productDao().getProductsByCategory(category);
    }

    public Flow<Product> getProductById(String productId) {
        return appDatabase.productDao().getProductById(productId);
    }

    public Flow<List<Product>> searchProducts(String query) {
        return appDatabase.productDao().searchProducts("%" + query + "%");
    }

    public Flow<List<Product>> getProductsByBrand(String brand) {
        return appDatabase.productDao().getProductsByBrand(brand);
    }

    private void refreshProductsIfNeeded() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCacheUpdate > CACHE_TIMEOUT) {
            refreshProducts();
        }
    }

    private void refreshProducts() {
        try {
            Response<List<Product>> response = apiService.getProducts().execute();
            if (response.isSuccessful() && response.body() != null) {
                appDatabase.productDao().deleteAllProducts();
                appDatabase.productDao().insertProducts(response.body());
                lastCacheUpdate = System.currentTimeMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Flow<List<Product>> getProductsByPriceRange(double minPrice, double maxPrice) {
        return appDatabase.productDao().getProductsByPriceRange(minPrice, maxPrice);
    }

    public Flow<List<Product>> getProductsByRating(float minRating) {
        return appDatabase.productDao().getProductsByRating(minRating);
    }

    public Flow<List<String>> getAllCategories() {
        return appDatabase.productDao().getAllCategories();
    }

    public Flow<List<String>> getAllBrands() {
        return appDatabase.productDao().getAllBrands();
    }

    public Flow<List<Product>> getDiscountedProducts() {
        return appDatabase.productDao().getDiscountedProducts();
    }

    public Flow<List<Product>> getNewArrivals() {
        long oneWeekAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7);
        return appDatabase.productDao().getNewArrivals(oneWeekAgo);
    }

    public Flow<List<Product>> getTrendingProducts() {
        return appDatabase.productDao().getProductsByRating(4.0f);
    }

    public Flow<List<Product>> getRelatedProducts(String category, String currentProductId) {
        return appDatabase.productDao().getRelatedProducts(category, currentProductId);
    }
}
