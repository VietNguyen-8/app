package com.bundung.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bundung.R;
import com.bundung.models.Order;
import com.bundung.models.OrderDetail;
import com.bundung.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orders;
    private OnOrderActionListener listener;
    private String currentStatus;

    public interface OnOrderActionListener {
        void onStartCooking(Order order);
        void onMarkDone(Order order);
        void onPayOrder(Order order);
    }

    public OrderAdapter(Context context, String currentStatus, OnOrderActionListener listener) {
        this.context = context;
        this.orders = new ArrayList<>();
        this.currentStatus = currentStatus;
        this.listener = listener;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        
        // Table number
        holder.tvTableNumber.setText("Bàn " + order.getTableNumber());
        
        // Server name
        holder.tvServerName.setText("Phục vụ: " + order.getServerName());
        
        // Time
        holder.tvTime.setText(Constants.getTimeAgo(order.getCreatedAt()));
        
        // Total amount
        holder.tvTotalAmount.setText(Constants.formatPrice(order.getTotalAmount()));
        
        // Status color
        int statusColor = getStatusColor(order.getStatus());
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, statusColor));
        
        // Order items
        OrderDetailAdapter detailAdapter = new OrderDetailAdapter(context, order.getItems());
        holder.rvOrderDetails.setLayoutManager(new LinearLayoutManager(context));
        holder.rvOrderDetails.setAdapter(detailAdapter);
        
        // Action buttons based on status
        holder.btnAction.setVisibility(View.VISIBLE);
        
        switch (currentStatus) {
            case Constants.STATUS_NEW:
                holder.btnAction.setText("Bắt đầu nấu");
                holder.btnAction.setOnClickListener(v -> {
                    if (listener != null) listener.onStartCooking(order);
                });
                break;
                
            case Constants.STATUS_COOKING:
                holder.btnAction.setText("Hoàn thành");
                holder.btnAction.setOnClickListener(v -> {
                    if (listener != null) listener.onMarkDone(order);
                });
                break;
                
            case Constants.STATUS_DONE:
                holder.btnAction.setText("Thanh toán");
                holder.btnAction.setOnClickListener(v -> {
                    if (listener != null) listener.onPayOrder(order);
                });
                break;
                
            case Constants.STATUS_PAID:
                holder.btnAction.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private int getStatusColor(String status) {
        switch (status) {
            case Constants.STATUS_NEW:
                return R.color.status_new;
            case Constants.STATUS_COOKING:
                return R.color.status_cooking;
            case Constants.STATUS_DONE:
                return R.color.status_done;
            case Constants.STATUS_PAID:
                return R.color.status_paid;
            default:
                return R.color.card_background;
        }
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTableNumber, tvServerName, tvTime, tvTotalAmount;
        RecyclerView rvOrderDetails;
        Button btnAction;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardOrder);
            tvTableNumber = itemView.findViewById(R.id.tvTableNumber);
            tvServerName = itemView.findViewById(R.id.tvServerName);
            tvTime = itemView.findViewById(R.id.tvOrderTime);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            rvOrderDetails = itemView.findViewById(R.id.rvOrderDetails);
            btnAction = itemView.findViewById(R.id.btnOrderAction);
        }
    }
    
    // Inner adapter for order details
    static class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.DetailViewHolder> {
        private Context context;
        private List<OrderDetail> details;

        public OrderDetailAdapter(Context context, List<OrderDetail> details) {
            this.context = context;
            this.details = details;
        }

        @NonNull
        @Override
        public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
            return new DetailViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
            OrderDetail detail = details.get(position);
            holder.tvName.setText(detail.getName());
            holder.tvQuantity.setText("x" + detail.getQuantity());
            
            if (detail.getNote() != null && !detail.getNote().isEmpty()) {
                holder.tvNote.setVisibility(View.VISIBLE);
                holder.tvNote.setText("(" + detail.getNote() + ")");
            } else {
                holder.tvNote.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return details.size();
        }

        static class DetailViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvQuantity, tvNote;

            public DetailViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvDetailName);
                tvQuantity = itemView.findViewById(R.id.tvDetailQuantity);
                tvNote = itemView.findViewById(R.id.tvDetailNote);
            }
        }
    }
}