package com.pointters.adapter;

/**
 * Created by vikas on 9/25/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointters.R;

import java.util.List;


public class TransactionCarouselAdapter extends RecyclerView.Adapter<TransactionCarouselAdapter.ViewHolder> {

    private Context _context;
    private List<String> transAmounts;
    private String[] titles;
    private String currencySymbol = "";

    public TransactionCarouselAdapter(Context _context, String[] titles, List<String> transAmounts) {
        this._context = _context;
        this.titles = titles;
        this.transAmounts = transAmounts;
    }
    public void setCurrencySymbol(String str){
        this.currencySymbol = str;
        notifyDataSetChanged();
    }

    @Override
    public TransactionCarouselAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(_context).inflate(R.layout.adapter_transaction_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.txtTitle.setText(titles[position]);
        holder.txtAmount.setText(currencySymbol + "" + transAmounts.get(position));

    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle, txtAmount;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.purchase_type);
            txtAmount = (TextView) itemView.findViewById(R.id.purchase_amounts);
        }
    }
}