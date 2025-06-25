package com.example.stockmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stockmanager.R;
import com.example.stockmanager.network.CouponResponse;
import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {

    private List<CouponResponse> couponList;
    private Context context;
    private OnCouponActionListener listener;

    public interface OnCouponActionListener {
        void onDelete(int couponId);
        void onEdit(CouponResponse coupon);
    }

    public CouponAdapter(Context context, List<CouponResponse> couponList, OnCouponActionListener listener) {
        this.context = context;
        this.couponList = couponList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_coupon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CouponResponse coupon = couponList.get(position);

        holder.couponCode.setText("Code: " + coupon.getCode());
        holder.discountValue.setText("Discount: " + coupon.getAmount());

        // ✅ Fix Expiry Date Display Issue (Remove "T00:00:00")
        String expiryDateRaw = coupon.getDateExpires();
        String formattedDate = formatExpiryDate(expiryDateRaw);
        holder.expiryDate.setText(formattedDate);

        holder.btnDelete.setOnClickListener(v -> listener.onDelete(coupon.getId()));
        holder.editBtn.setOnClickListener(v -> listener.onEdit(coupon));
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView couponCode, discountValue, expiryDate;
        Button btnDelete, editBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            couponCode = itemView.findViewById(R.id.txtCouponCode);
            discountValue = itemView.findViewById(R.id.txtDiscountValue);
            expiryDate = itemView.findViewById(R.id.txtExpiryDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            editBtn = itemView.findViewById(R.id.editBtn);
        }
    }

    // ✅ Utility Function to Format Expiry Date
    private String formatExpiryDate(String expiryDate) {
        if (expiryDate != null && !expiryDate.trim().isEmpty()) {
            return "Valid until: " + expiryDate.split("T")[0]; // 🔥 Remove Time Part
        } else {
            return "No Expiry Date";
        }
    }
}
