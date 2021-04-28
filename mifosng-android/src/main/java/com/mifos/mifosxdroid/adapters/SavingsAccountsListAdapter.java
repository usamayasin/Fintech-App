/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.clientdetails.ClientDetailsFragment;
import com.mifos.objects.accounts.savings.SavingsAccount;
import java.text.DecimalFormat;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ishankhanna on 23/05/14.
 */
public class SavingsAccountsListAdapter extends RecyclerView.Adapter<SavingsAccountsListAdapter.ViewHolder> {

    Context context;
    private List<SavingsAccount> savingsAccountList;
    public ClientDetailsFragment.OnFragmentInteractionListener mlistener;

    public SavingsAccountsListAdapter(Context context, List<SavingsAccount> savingsAccountList, ClientDetailsFragment.OnFragmentInteractionListener listener) {

        this.savingsAccountList = savingsAccountList;
        this.context = context;
        this.mlistener = listener;
    }

    @NonNull
    @Override
    public SavingsAccountsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.row_account_item, parent, false);
        SavingsAccountsListAdapter.ViewHolder viewHolder = new SavingsAccountsListAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SavingsAccountsListAdapter.ViewHolder holder, int position) {
        final SavingsAccount item = savingsAccountList.get(position);

        Double accountBalance = savingsAccountList.get(position).getAccountBalance();
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMaximumIntegerDigits(10);
        holder.tv_amount.setText(String.valueOf(accountBalance == null ? "0.00" :
                decimalFormat.format(accountBalance)));
        holder.tv_accountNumber.setText(savingsAccountList.get(position).getAccountNo());

        if (savingsAccountList.get(position).getStatus().getActive()) {

            holder.view_status_indicator.setColorFilter(
                    ContextCompat.getColor(context,
                            R.color.savings_account_status_active));

        } else if (savingsAccountList.get(position).getStatus().getApproved()) {

            holder.view_status_indicator.setColorFilter(
                    ContextCompat.getColor(context, R.color.status_approved));

        } else if (savingsAccountList.get(position).getStatus().getSubmittedAndPendingApproval()) {

            holder.view_status_indicator.setColorFilter(
                    ContextCompat.getColor(context,
                            R.color.status_submitted_and_pending_approval));

        } else {
            holder.view_status_indicator.setColorFilter(
                    ContextCompat.getColor(context, R.color.status_closed));
        }
        holder.bind(item,mlistener);
    }

    @Override
    public long getItemId(int i) {
        return savingsAccountList.size();
    }

    @Override
    public int getItemCount() {
        return savingsAccountList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_amount)
        TextView tv_amount;
        @BindView(R.id.tv_accountNumber)
        TextView tv_accountNumber;
        @BindView(R.id.view_status_indicator)
        AppCompatImageView view_status_indicator;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(SavingsAccount savingsAccount, ClientDetailsFragment.OnFragmentInteractionListener onItemClickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.loadSavingsAccountSummary(savingsAccount.getId(),savingsAccount.getDepositType());
                }
            });
        }
    }
}
