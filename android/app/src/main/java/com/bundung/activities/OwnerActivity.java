package com.bundung.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bundung.R;
import com.bundung.models.Statistics;
import com.bundung.network.ApiConfig;
import com.bundung.network.ApiResponse;
import com.bundung.network.ApiService;
import com.bundung.utils.Constants;
import com.bundung.utils.SessionManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvTotalRevenue, tvTotalOrders, tvAverageOrder;
    private PieChart pieChart;

    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        // Initialize
        initViews();
        setupToolbar();

        apiService = ApiConfig.getApiService();
        sessionManager = new SessionManager(this);

        // Load today's statistics
        String today = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault()).format(new Date());
        loadStatistics(today);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvAverageOrder = findViewById(R.id.tvAverageOrder);
        pieChart = findViewById(R.id.pieChart);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chủ quán - " + sessionManager.getCurrentUser().getFullName());
        }
    }

    private void loadStatistics(String date) {
        Call<ApiResponse<Statistics>> call = apiService.getStatistics(date);

        call.enqueue(new Callback<ApiResponse<Statistics>>() {
            @Override
            public void onResponse(Call<ApiResponse<Statistics>> call, Response<ApiResponse<Statistics>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Statistics> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        Statistics stats = apiResponse.getData();
                        displayStatistics(stats);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Statistics>> call, Throwable t) {
                Toast.makeText(OwnerActivity.this,
                    "Lỗi: " + t.getMessage(),
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayStatistics(Statistics stats) {
        // Total revenue
        tvTotalRevenue.setText(Constants.formatPrice(stats.getTotalRevenue()));

        // Total orders
        tvTotalOrders.setText(String.valueOf(stats.getTotalOrders()));

        // Average per order
        double average = stats.getTotalOrders() > 0 ? stats.getTotalRevenue() / stats.getTotalOrders() : 0;
        tvAverageOrder.setText(Constants.formatPrice(average));

        // Pie chart for payment methods
        if (stats.getRevenueByPayment() != null) {
            setupPieChart(stats.getRevenueByPayment());
        }
    }

    private void setupPieChart(Map<String, Double> revenueByPayment) {
        List<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : revenueByPayment.entrySet()) {
            String label = entry.getKey().equals("tien_mat") ? "Tiền mặt" : "Ví điện tử";
            entries.add(new PieEntry(entry.getValue().floatValue(), label));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Phương thức thanh toán");
        dataSet.setColors(Color.parseColor("#4CAF50"), Color.parseColor("#2196F3"));
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
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
            String today = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault()).format(new Date());
            loadStatistics(today);
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