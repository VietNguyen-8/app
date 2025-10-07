package com.bundung.network;

import com.bundung.models.MenuItem;
import com.bundung.models.Order;
import com.bundung.models.Statistics;
import com.bundung.models.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // ============================================
    // AUTHENTICATION
    // ============================================
    
    @POST("auth/login.php")
    Call<ApiResponse<User>> login(@Body LoginRequest request);

    // ============================================
    // MENU
    // ============================================
    
    @GET("menu/get_menu.php")
    Call<ApiResponse<List<MenuItem>>> getMenu();

    @GET("menu/get_menu.php")
    Call<ApiResponse<List<MenuItem>>> getMenuByCategory(@Query("category") String category);

    // ============================================
    // ORDERS
    // ============================================
    
    @GET("orders/get_orders.php")
    Call<ApiResponse<List<Order>>> getOrders(
            @Query("status") String status,
            @Query("server_id") Integer serverId,
            @Query("role") String role
    );

    @POST("orders/add_order.php")
    Call<ApiResponse<OrderIdResponse>> addOrder(@Body AddOrderRequest request);

    @POST("orders/update_status.php")
    Call<ApiResponse<Void>> updateOrderStatus(@Body UpdateStatusRequest request);

    @POST("orders/complete_order.php")
    Call<ApiResponse<Void>> completeOrder(@Body CompleteOrderRequest request);

    // ============================================
    // STATISTICS
    // ============================================
    
    @GET("stats/get_statistics.php")
    Call<ApiResponse<Statistics>> getStatistics(@Query("date") String date);

    @GET("stats/get_statistics.php")
    Call<ApiResponse<Statistics>> getStatisticsByRange(
            @Query("from_date") String fromDate,
            @Query("to_date") String toDate
    );

    // ============================================
    // REQUEST/RESPONSE CLASSES
    // ============================================
    
    class LoginRequest {
        private String username;
        private String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    class AddOrderRequest {
        private int table_number;
        private int server_id;
        private List<OrderItem> items;

        public AddOrderRequest(int tableNumber, int serverId, List<OrderItem> items) {
            this.table_number = tableNumber;
            this.server_id = serverId;
            this.items = items;
        }

        public static class OrderItem {
            private int menu_item_id;
            private int quantity;
            private String note;

            public OrderItem(int menuItemId, int quantity, String note) {
                this.menu_item_id = menuItemId;
                this.quantity = quantity;
                this.note = note;
            }
        }
    }

    class UpdateStatusRequest {
        private int order_id;
        private String status;

        public UpdateStatusRequest(int orderId, String status) {
            this.order_id = orderId;
            this.status = status;
        }
    }

    class CompleteOrderRequest {
        private int order_id;
        private String payment_method;

        public CompleteOrderRequest(int orderId, String paymentMethod) {
            this.order_id = orderId;
            this.payment_method = paymentMethod;
        }
    }

    class OrderIdResponse {
        private int order_id;

        public int getOrderId() {
            return order_id;
        }
    }
}