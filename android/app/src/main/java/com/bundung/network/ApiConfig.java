package com.bundung.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiConfig {
    
    // ⚠️ QUAN TRỌNG: Thay đổi IP này thành IP máy chủ XAMPP trong mạng LAN
    // Cách lấy IP:
    // - Windows: Mở CMD và gõ "ipconfig" → tìm IPv4 Address
    // - Mac/Linux: Mở Terminal và gõ "ifconfig" → tìm inet
    private static final String BASE_URL = "http://192.168.1.100/bun_dung_api/";
    
    // Hoặc dùng 10.0.2.2 nếu test trên Android Emulator (trỏ đến localhost của máy host)
    // private static final String BASE_URL = "http://10.0.2.2/bun_dung_api/";
    
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Logging interceptor for debugging
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttp client with timeout settings
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            // Gson for parsing JSON
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            // Build Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}