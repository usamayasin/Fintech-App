package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.online.clientdetails.ClientDetailsFragment;
import com.mifos.objects.accounts.savings.SavingsAccount;
import com.mifos.objects.runreports.ColumnHeader;
import com.mifos.objects.runreports.DataRow;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mifos.App.getContext;

public class RunReportListAdapter extends RecyclerView.Adapter<RunReportListAdapter.ViewHolder> {

    private List<DataRow> rowDataList;
    private List<ColumnHeader> columnHeadersList;

    public RunReportListAdapter(List<DataRow> data, List<ColumnHeader> columnHeadersList) {
        this.rowDataList = data;
        this.columnHeadersList = columnHeadersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.row_runreport_list, parent, false);
        RunReportListAdapter.ViewHolder viewHolder = new RunReportListAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            DataRow item = rowDataList.get(position);
            holder.setIsRecyclable(false);

            LinearLayout.LayoutParams headingRowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            headingRowParams.gravity = Gravity.CENTER;
            headingRowParams.setMargins(0, 0, 0, 10);
            holder.ll_runreport_list.setLayoutParams(headingRowParams);
            holder.ll_runreport_list.setWeightSum(item.getRow().size() - 1);
            holder.ll_runreport_list.setOrientation(LinearLayout.HORIZONTAL);

            for (int i = 0; i < columnHeadersList.size(); i++) {
                if ("STRING".equals(columnHeadersList.get(i).getColumnDisplayType())) {
                    TextView tv = new TextView(getContext());
                    LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                    tv.setGravity(Gravity.CENTER);
                    tv.setLayoutParams(rowParams);
                    tv.setPadding(5, 5, 5, 5);
                    tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                    if (item.getRow().get(i) != null) {
                        tv.setText(item.getRow().get(i));
                    } else {
                        tv.setText("-");
                    }
                    holder.ll_runreport_list.addView(tv);
                }
            }

        } catch (Exception e) {
            Log.e("Error ", e.getMessage());
        }


    }

    @Override
    public int getItemCount() {
        return rowDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return  position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ll_runreport_list)
        LinearLayout ll_runreport_list;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
