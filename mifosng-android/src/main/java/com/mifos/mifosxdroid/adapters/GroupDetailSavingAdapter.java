package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.clientdetails.ClientDetailsFragment;
import com.mifos.mifosxdroid.online.groupdetails.GroupDetailsFragment;
import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.accounts.savings.SavingsAccount;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupDetailSavingAdapter extends RecyclerView.Adapter<GroupDetailSavingAdapter.ViewHolder> {

    Context context;
    private List<SavingsAccount> savingsAccountList;
    public GroupDetailsFragment.OnFragmentInteractionListener mlistener;

    public GroupDetailSavingAdapter(Context context, List<SavingsAccount> savingsAccountList,
                                    GroupDetailsFragment.OnFragmentInteractionListener mlistener) {
        this.context = context;
        this.savingsAccountList = savingsAccountList;
        this.mlistener = mlistener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.row_account_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SavingsAccount item = savingsAccountList.get(position);

        holder.tv_amount.setText(item.getProductName());
        holder.tv_amount.setEllipsize(TextUtils.TruncateAt.END);
        holder.tv_accountNumber.setText(item.getAccountNo());

        if (savingsAccountList.get(position).getStatus().getActive()) {

            holder.view_status_indicator.setColorFilter(
                    ContextCompat.getColor(context, R.color.loan_status_disbursed));

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
        holder.bind(item, mlistener);
    }

    @Override
    public int getItemCount() {
        return savingsAccountList.size();
    }

    @Override
    public long getItemId(int position) {
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

        public void bind(SavingsAccount savingsAccount, GroupDetailsFragment.OnFragmentInteractionListener onItemClickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.loadSavingsAccountSummary(savingsAccount.getId(),savingsAccount.getDepositType());
                }
            });
        }
    }
}
