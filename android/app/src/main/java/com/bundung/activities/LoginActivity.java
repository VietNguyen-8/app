package com.bundung.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bundung.R;
import com.bundung.models.User;
import com.bundung.network.ApiConfig;
import com.bundung.network.ApiResponse;
import com.bundung.network.ApiService;
import com.bundung.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize
        initViews();
        apiService = ApiConfig.getApiService();
        sessionManager = new SessionManager(this);

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToRole(sessionManager.getRole());
            finish();
            return;
        }

        // Login button click
        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (username.isEmpty()) {
            etUsername.setError("Vui lòng nhập tên đăng nhập");
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        // Show loading
        showLoading(true);

        // API call
        ApiService.LoginRequest request = new ApiService.LoginRequest(username, password);
        Call<ApiResponse<User>> call = apiService.login(request);

        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        User user = apiResponse.getData();
                        
                        // Save session
                        sessionManager.saveLoginSession(user);
                        
                        // Show welcome message
                        Toast.makeText(LoginActivity.this, 
                            "Chào mừng " + user.getFullName(), 
                            Toast.LENGTH_SHORT).show();
                        
                        // Navigate to appropriate screen
                        navigateToRole(user.getRole());
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, 
                            apiResponse.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, 
                        getString(R.string.login_error), 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(LoginActivity.this, 
                    getString(R.string.login_network_error), 
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToRole(String role) {
        Intent intent;
        
        switch (role) {
            case User.ROLE_WAITER:
                intent = new Intent(this, WaiterActivity.class);
                break;
            case User.ROLE_KITCHEN:
                intent = new Intent(this, KitchenActivity.class);
                break;
            case User.ROLE_OWNER:
                intent = new Intent(this, OwnerActivity.class);
                break;
            default:
                Toast.makeText(this, "Role không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
        }
        
        startActivity(intent);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        etUsername.setEnabled(!show);
        etPassword.setEnabled(!show);
    }
}