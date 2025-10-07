package com.bundung.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bundung.R;
import com.bundung.adapters.OrderAdapter;
import com.bundung.models.Order;
import com.bundung.network.ApiConfig;
import com.bundung.network.ApiResponse;
import com.bundung.network.ApiService;
import com.bundung.utils.Constants;
import com.bundung.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KitchenActivity extends AppCompatActivity implements OrderAdapter.OnOrderActionListener {

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvOrders;

    private OrderAdapter orderAdapter;
    private ApiService apiService;
    private SessionManager sessionManager;

    private String currentStatus = Constants.STATUS_NEW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        // Initialize
        initViews();
        setupToolbar();
        setupTabs();
        setupRecyclerView();

        apiService = ApiConfig.getApiService();
        sessionManager = new SessionManager(this);

        // Load orders
        loadOrders(currentStatus);

        // Swipe to refresh
        swipeRefresh.setOnRefreshListener(() -> loadOrders(currentStatus));
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        rvOrders = findViewById(R.id.rvOrders);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bếp - " + sessionManager.getCurrentUser().getFullName());
        }
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_new_orders));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_cooking));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_completed));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        currentStatus = Constants.STATUS_NEW;
                        break;
                    case 1:
                        currentStatus = Constants.STATUS_COOKING;
                        break;
                    case 2:
                        currentStatus = Constants.STATUS_DONE;
                        break;
                }
                loadOrders(currentStatus);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                loadOrders(currentStatus);
            }
        });
    }

    private void setupRecyclerView() {
        orderAdapter = new OrderAdapter(this, currentStatus, this);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(orderAdapter);
    }

    private void loadOrders(String status) {
        Call<ApiResponse<List<Order>>> call = apiService.getOrders(status, null, "bep");

        call.enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Order>> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        List<Order> orders = apiResponse.getData();
                        orderAdapter.setOrders(orders);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(KitchenActivity.this,
                    "Lỗi: " + t.getMessage(),
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStartCooking(Order order) {
        updateOrderStatus(order.getId(), Constants.STATUS_COOKING);
    }

    @Override
    public void onMarkDone(Order order) {
        updateOrderStatus(order.getId(), Constants.STATUS_DONE);
    }

    @Override
    public void onPayOrder(Order order) {
        // Not used in Kitchen
    }

    private void updateOrderStatus(int orderId, String newStatus) {
        ApiService.UpdateStatusRequest request = new ApiService.UpdateStatusRequest(orderId, newStatus);
        Call<ApiResponse<Void>> call = apiService.updateOrderStatus(request);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        Toast.makeText(KitchenActivity.this,
                            "Cập nhật thành công",
                            Toast.LENGTH_SHORT).show();
                        loadOrders(currentStatus);
                    } else {
                        Toast.makeText(KitchenActivity.this,
                            apiResponse.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(KitchenActivity.this,
                    "Lỗi: " + t.getMessage(),
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        } else if (item.getItemId() == R.id.action_refresh) {
            loadOrders(currentStatus);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.menu_logout)
            .setMessage(R.string.logout_confirm)
            .setPositiveButton(R.string.yes, (dialog, which) -> {
                sessionManager.logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            })
            .setNegativeButton(R.string.no, null)
            .show();
    }
}