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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.clientdetails.ClientDetailsFragment;
import com.mifos.objects.accounts.loan.LoanAccount;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ishankhanna on 01/03/14.
 */
public class LoanAccountsListAdapter extends RecyclerView.Adapter<LoanAccountsListAdapter.ViewHolder> {

    Context context;
    private List<LoanAccount> loanAccountList;
    public ClientDetailsFragment.OnFragmentInteractionListener mlistener;

    public LoanAccountsListAdapter(Context context, List<LoanAccount> loanAccountList, ClientDetailsFragment.OnFragmentInteractionListener listener) {

        this.loanAccountList = loanAccountList;
        this.context = context;
        this.mlistener = listener;
    }

    @NonNull
    @Override
    public LoanAccountsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.row_account_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LoanAccountsListAdapter.ViewHolder holder, int position) {
        final LoanAccount item = loanAccountList.get(position);

        holder.tv_amount.setText(item.getProductName());
        holder.tv_amount.setEllipsize(TextUtils.TruncateAt.END);
        holder.tv_accountNumber.setText(item.getAccountNo());

        if (loanAccountList.get(position).getStatus().getActive()) {

            holder.view_status_indicator.setColorFilter(
                    ContextCompat.getColor(context, R.color.loan_status_disbursed));

        } else if (loanAccountList.get(position).getStatus().getWaitingForDisbursal()) {

            holder.view_status_indicator.setColorFilter(
                    ContextCompat.getColor(context, R.color.status_approved));

        } else if (loanAccountList.get(position).getStatus().getPendingApproval()) {

            holder.view_status_indicator.setColorFilter(
                    ContextCompat.getColor(context,
                            R.color.status_submitted_and_pending_approval));

        } else if (loanAccountList.get(position).getStatus().getActive() && loanAccountList.get(position)
                .getInArrears()) {

            holder.view_status_indicator.setColorFilter(
                    ContextCompat.getColor(context, R.color.red));

        } else {

            holder.view_status_indicator.setColorFilter(
                    ContextCompat.getColor(context, R.color.status_closed));
        }
        holder.bind(item, mlistener);
    }


    @Override
    public long getItemId(int i) {
        return loanAccountList.size();

    }

    @Override
    public int getItemCount() {
        return loanAccountList.size();
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

        public void bind(LoanAccount loanAccount, ClientDetailsFragment.OnFragmentInteractionListener onItemClickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.loadLoanAccountSummary(loanAccount.getId());
                }
            });
        }
    }
}
