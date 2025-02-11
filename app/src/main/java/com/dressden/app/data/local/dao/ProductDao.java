package com.dressden.app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.dressden.app.data.models.Product;

import java.util.List;

import kotlinx.coroutines.flow.Flow;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM products")
    Flow<List<Product>> getAllProducts();

    @Query("SELECT * FROM products WHERE category = :category")
    Flow<List<Product>> getProductsByCategory(String category);

    @Query("SELECT * FROM products WHERE id = :productId")
    Flow<Product> getProductById(String productId);

    @Query("SELECT * FROM products WHERE name LIKE :searchQuery OR description LIKE :searchQuery")
    Flow<List<Product>> searchProducts(String searchQuery);

    @Query("SELECT * FROM products WHERE brand = :brand")
    Flow<List<Product>> getProductsByBrand(String brand);

    @Query("SELECT * FROM products WHERE price BETWEEN :minPrice AND :maxPrice")
    Flow<List<Product>> getProductsByPriceRange(double minPrice, double maxPrice);

    @Query("SELECT * FROM products WHERE rating >= :minRating")
    Flow<List<Product>> getProductsByRating(float minRating);

    @Query("SELECT DISTINCT category FROM products")
    Flow<List<String>> getAllCategories();

    @Query("SELECT DISTINCT brand FROM products")
    Flow<List<String>> getAllBrands();

    @Query("SELECT * FROM products WHERE discountedPrice IS NOT NULL AND discountedPrice < price")
    Flow<List<Product>> getDiscountedProducts();

    @Query("SELECT * FROM products WHERE createdAt >= :timestamp ORDER BY createdAt DESC")
    Flow<List<Product>> getNewArrivals(long timestamp);

    @Query("SELECT * FROM products WHERE category = :category AND id != :currentProductId LIMIT 10")
    Flow<List<Product>> getRelatedProducts(String category, String currentProductId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProducts(List<Product> products);

    @Query("DELETE FROM products")
    void deleteAllProducts();

    @Transaction
    default void refreshProducts(List<Product> products) {
        deleteAllProducts();
        insertProducts(products);
    }
}
