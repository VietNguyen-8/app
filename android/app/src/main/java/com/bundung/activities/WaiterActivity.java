package com.bundung.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bundung.R;
import com.bundung.adapters.MenuAdapter;
import com.bundung.models.MenuItem;
import com.bundung.network.ApiConfig;
import com.bundung.network.ApiResponse;
import com.bundung.network.ApiService;
import com.bundung.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaiterActivity extends AppCompatActivity implements MenuAdapter.OnMenuItemClickListener {

    private MaterialToolbar toolbar;
    private EditText etTableNumber;
    private RecyclerView rvMenu;
    private TextView tvEmptyMenu, tvCartBadge;
    private ExtendedFloatingActionButton fabSubmitOrder;

    private MenuAdapter menuAdapter;
    private ApiService apiService;
    private SessionManager sessionManager;

    private int cartItemCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter);

        // Initialize
        initViews();
        setupToolbar();
        setupRecyclerView();
        
        apiService = ApiConfig.getApiService();
        sessionManager = new SessionManager(this);

        // Load menu
        loadMenu();

        // FAB click
        fabSubmitOrder.setOnClickListener(v -> submitOrder());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etTableNumber = findViewById(R.id.etTableNumber);
        rvMenu = findViewById(R.id.rvMenu);
        tvEmptyMenu = findViewById(R.id.tvEmptyMenu);
        fabSubmitOrder = findViewById(R.id.fabSubmitOrder);
        tvCartBadge = findViewById(R.id.tvCartBadge);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            String fullName = sessionManager.getCurrentUser().getFullName();
            getSupportActionBar().setTitle("Phục vụ - " + fullName);
        }
    }

    private void setupRecyclerView() {
        menuAdapter = new MenuAdapter(this, this);
        rvMenu.setLayoutManager(new GridLayoutManager(this, 2));
        rvMenu.setAdapter(menuAdapter);
    }

    private void loadMenu() {
        Call<ApiResponse<List<MenuItem>>> call = apiService.getMenu();
        
        call.enqueue(new Callback<ApiResponse<List<MenuItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MenuItem>>> call, Response<ApiResponse<List<MenuItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<MenuItem>> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        List<MenuItem> items = apiResponse.getData();
                        menuAdapter.setMenuItems(items);
                        
                        if (items.isEmpty()) {
                            rvMenu.setVisibility(View.GONE);
                            tvEmptyMenu.setVisibility(View.VISIBLE);
                        } else {
                            rvMenu.setVisibility(View.VISIBLE);
                            tvEmptyMenu.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<MenuItem>>> call, Throwable t) {
                Toast.makeText(WaiterActivity.this, 
                    "Không thể tải menu: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitOrder() {
        String tableNumberStr = etTableNumber.getText().toString().trim();
        
        // Validation
        if (tableNumberStr.isEmpty()) {
            Toast.makeText(this, R.string.please_enter_table, Toast.LENGTH_SHORT).show();
            etTableNumber.requestFocus();
            return;
        }

        List<MenuItem> selectedItems = menuAdapter.getSelectedItems();
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, R.string.please_select_items, Toast.LENGTH_SHORT).show();
            return;
        }

        // Confirm dialog
        new AlertDialog.Builder(this)
            .setTitle("Xác nhận gửi đơn")
            .setMessage("Gửi đơn bàn " + tableNumberStr + " với " + selectedItems.size() + " món?")
            .setPositiveButton(R.string.yes, (dialog, which) -> sendOrder(tableNumberStr, selectedItems))
            .setNegativeButton(R.string.no, null)
            .show();
    }

    private void sendOrder(String tableNumberStr, List<MenuItem> selectedItems) {
        int tableNumber = Integer.parseInt(tableNumberStr);
        int serverId = sessionManager.getUserId();

        // Build order items
        List<ApiService.AddOrderRequest.OrderItem> orderItems = new ArrayList<>();
        for (MenuItem item : selectedItems) {
            orderItems.add(new ApiService.AddOrderRequest.OrderItem(
                item.getId(),
                item.getQuantity(),
                item.getNote()
            ));
        }

        ApiService.AddOrderRequest request = new ApiService.AddOrderRequest(
            tableNumber,
            serverId,
            orderItems
        );

        Call<ApiResponse<ApiService.OrderIdResponse>> call = apiService.addOrder(request);
        
        call.enqueue(new Callback<ApiResponse<ApiService.OrderIdResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiService.OrderIdResponse>> call, Response<ApiResponse<ApiService.OrderIdResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ApiService.OrderIdResponse> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(WaiterActivity.this, 
                            R.string.order_success, 
                            Toast.LENGTH_SHORT).show();
                        
                        // Reset form
                        etTableNumber.setText("");
                        loadMenu(); // Reload to reset quantities
                        updateCartBadge(0);
                    } else {
                        Toast.makeText(WaiterActivity.this, 
                            apiResponse.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WaiterActivity.this, 
                        R.string.order_error, 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ApiService.OrderIdResponse>> call, Throwable t) {
                Toast.makeText(WaiterActivity.this, 
                    "Lỗi: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onQuantityChanged(MenuItem item, int newQuantity) {
        // Update cart badge
        List<MenuItem> selectedItems = menuAdapter.getSelectedItems();
        int totalQuantity = 0;
        for (MenuItem selectedItem : selectedItems) {
            totalQuantity += selectedItem.getQuantity();
        }
        updateCartBadge(totalQuantity);
    }

    private void updateCartBadge(int count) {
        cartItemCount = count;
        if (count > 0) {
            tvCartBadge.setVisibility(View.VISIBLE);
            tvCartBadge.setText(String.valueOf(count));
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
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
            loadMenu();
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