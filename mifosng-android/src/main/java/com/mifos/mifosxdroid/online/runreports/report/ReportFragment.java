package com.mifos.mifosxdroid.online.runreports.report;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.RunReportListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.objects.runreports.ColumnHeader;
import com.mifos.objects.runreports.DataRow;
import com.mifos.objects.runreports.FullParameterListResponse;
import com.mifos.utils.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tarun on 05-08-17.
 */

public class ReportFragment extends MifosBaseFragment implements ReportMvpView {

  /*  @BindView(R.id.table_report)
    TableLayout tableReport;*/

    @BindView(R.id.ll_headers)
    LinearLayout ll_headers;

    @BindView(R.id.rc_runreport)
    RecyclerView rc_runreport;

    @Inject
    ReportPresenter presenter;

    private View rootView;
    private FullParameterListResponse report;

    public ReportFragment() {
    }

    public static ReportFragment newInstance(Bundle args) {
        ReportFragment fragment = new ReportFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client_report, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, rootView);
        presenter.attachView(this);

        setHasOptionsMenu(true);
        report = getArguments().getParcelable(Constants.REPORT_NAME);
        setUpUi();

        return rootView;
    }

    private void setUpUi() {
        try {
            showProgressbar(true);
            //setUpHeading();
            if (report.getData().size() > 0) {
                setUpHeadings();
                //setUpValues();
                setRunReportList();
            } else {
                Toast.makeText(getActivity(), getString(R.string.msg_report_empty), Toast.LENGTH_SHORT)
                        .show();
            }
            showProgressbar(false);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    private void setUpHeading() {
        TableRow row = new TableRow(getContext());
        TableRow.LayoutParams headingRowParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headingRowParams.gravity = Gravity.CENTER;
        headingRowParams.setMargins(0, 0, 0, 10);
        row.setLayoutParams(headingRowParams);

        for (ColumnHeader column : report.getColumnHeaders()) {
            switch (column.getColumnDisplayType()) {
                case "STRING":
                    TextView tv = new TextView(getContext());
                    tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                    tv.setGravity(Gravity.CENTER);
                    tv.setText(column.getColumnName());
                    row.addView(tv);
                    break;
            }
        }
        // tableReport.addView(row);
    }

    private void setUpHeadings() {
        try {
            LinearLayout.LayoutParams headingRowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //headingRowParams.gravity = Gravity.CENTER;
            headingRowParams.setMargins(0, 0, 0, 0);
            ll_headers.setLayoutParams(headingRowParams);

            for (ColumnHeader column : report.getColumnHeaders()) {
                switch (column.getColumnDisplayType()) {
                    case "STRING":
                        TextView tv = new TextView(getContext());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                        tv.setLayoutParams(params);
                        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                        tv.setGravity(Gravity.CENTER);
                        tv.setText(column.getColumnName());
                        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        ll_headers.addView(tv);
                        break;
                }
            }
            ll_headers.setWeightSum(ll_headers.getChildCount());
            int headers=ll_headers.getChildCount();
            Log.e("count", String.valueOf(headers));
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        // tableReport.addView(row);
    }

    private void setUpValues() {

        // For each dataRow, the item in the index i refers to the column
        // i of the columnHeader list.

        for (DataRow dataRow : report.getData()) {
            TableRow row = new TableRow(getContext());
            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowParams.gravity = Gravity.CENTER;
            rowParams.setMargins(0, 0, 0, 10);
            row.setLayoutParams(rowParams);

            for (int i = 0; i < report.getColumnHeaders().size(); i++) {

                // Add more cases, if they are, for the other types of data here.
                switch (report.getColumnHeaders().get(i).getColumnDisplayType()) {
                    case "STRING":
                        TextView tv = new TextView(getContext());
                        tv.setGravity(Gravity.CENTER);
                        if (dataRow.getRow().get(i) != null) {
                            tv.setText(dataRow.getRow().get(i));
                        } else {
                            tv.setText("-");
                        }
                        row.addView(tv);
                        break;
                }
            }
            // tableReport.addView(row);
        }
    }

    private void setRunReportList() {
        try {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            // mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rc_runreport.setLayoutManager(mLayoutManager);
            rc_runreport.setHasFixedSize(true);
            RunReportListAdapter adapter = new RunReportListAdapter(report.getData(),report.getColumnHeaders());
            rc_runreport.setAdapter(adapter);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_report, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_download_report:
                //Verify if menu should be there or not.
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showProgressbar(boolean b) {
        if (b) {
            showMifosProgressDialog();
        } else {
            hideMifosProgressDialog();
        }
    }
}
