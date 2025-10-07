package com.bundung.utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Constants {
    
    // Order Status
    public static final String STATUS_NEW = "moi";
    public static final String STATUS_COOKING = "dang_nau";
    public static final String STATUS_DONE = "hoan_thanh";
    public static final String STATUS_PAID = "da_thanh_toan";
    
    // User Roles
    public static final String ROLE_WAITER = "phuc_vu";
    public static final String ROLE_KITCHEN = "bep";
    public static final String ROLE_OWNER = "chu_quan";
    
    // Payment Methods
    public static final String PAYMENT_CASH = "tien_mat";
    public static final String PAYMENT_EWALLET = "vi_dien_tu";
    
    // Date Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DISPLAY_DATE_FORMAT = "dd/MM/yyyy";
    public static final String DISPLAY_TIME_FORMAT = "HH:mm";
    
    /**
     * Format giá tiền VND
     */
    public static String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(price) + " đ";
    }
    
    /**
     * Format ngày giờ
     */
    public static String formatDateTime(String datetime, String pattern) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            Date date = inputFormat.parse(datetime);
            return outputFormat.format(date);
        } catch (Exception e) {
            return datetime;
        }
    }
    
    /**
     * Lấy thời gian trôi qua (VD: "5 phút trước")
     */
    public static String getTimeAgo(String datetime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault());
            Date past = format.parse(datetime);
            Date now = new Date();
            
            long diff = now.getTime() - past.getTime();
            long diffMinutes = diff / (60 * 1000);
            long diffHours = diff / (60 * 60 * 1000);
            long diffDays = diff / (24 * 60 * 60 * 1000);
            
            if (diffMinutes < 60) {
                return diffMinutes + " phút trước";
            } else if (diffHours < 24) {
                return diffHours + " giờ trước";
            } else {
                return diffDays + " ngày trước";
            }
        } catch (Exception e) {
            return datetime;
        }
    }
    
    /**
     * Lấy màu theo trạng thái đơn
     */
    public static int getStatusColor(String status) {
        switch (status) {
            case STATUS_NEW:
                return android.R.color.holo_orange_light;
            case STATUS_COOKING:
                return android.R.color.holo_orange_dark;
            case STATUS_DONE:
                return android.R.color.holo_green_light;
            case STATUS_PAID:
                return android.R.color.holo_blue_light;
            default:
                return android.R.color.darker_gray;
        }
    }
    
    /**
     * Lấy tên hiển thị của trạng thái
     */
    public static String getStatusDisplay(String status) {
        switch (status) {
            case STATUS_NEW:
                return "Đơn mới";
            case STATUS_COOKING:
                return "Đang nấu";
            case STATUS_DONE:
                return "Hoàn thành";
            case STATUS_PAID:
                return "Đã thanh toán";
            default:
                return status;
        }
    }
}