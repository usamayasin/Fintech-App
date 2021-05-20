package com.mifos.mifosxdroid.online.runreports.reportdetail;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.runreports.report.ReportFragment;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.runreports.DataRow;
import com.mifos.objects.runreports.FullParameterListResponse;
import com.mifos.objects.runreports.client.ClientReportTypeItem;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tarun on 04-08-17.
 */

public class ReportDetailFragment extends MifosBaseFragment
        implements ReportDetailMvpView, MFDatePicker.OnDatePickListener {

    @BindView(R.id.tv_report_name)
    TextView tvReportName;

    @BindView(R.id.tv_report_type)
    TextView tvReportType;

    @BindView(R.id.tv_report_category)
    TextView tvReportCategory;

    @BindView(R.id.table_details)
    TableLayout tableDetails;

    @BindView(R.id.ll_runreport_details)
    LinearLayout ll_runreport_details;

    @Inject
    ReportDetailPresenter presenter;

    private View rootView;
    private ClientReportTypeItem reportItem;

    private boolean fetchLoanOfficer = false;
    private boolean fetchLoanProduct = false;

    private HashMap<String, Integer> fundMap;
    private HashMap<String, Integer> loanOfficerMap;
    private HashMap<String, Integer> loanProductMap;
    private HashMap<String, Integer> loanPurposeMap;
    private HashMap<String, Integer> officeMap;
    private HashMap<String, Integer> parMap;
    private HashMap<String, Integer> subStatusMap;
    private HashMap<String, Integer> glAccountNoMap;
    private HashMap<String, Integer> obligDateTypeMap;
    private HashMap<String, String> currencyMap;

    private String dateField;
    public DialogFragment datePicker;

    private EditText tvField;

    public ReportDetailFragment() {
    }


    public static ReportDetailFragment newInstance(Bundle args) {
        ReportDetailFragment fragment = new ReportDetailFragment();
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
        rootView = inflater.inflate(R.layout.fragment_client_report_details, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, rootView);
        presenter.attachView(this);

        reportItem = getArguments().getParcelable(Constants.CLIENT_REPORT_ITEM);
        setUpUi();

        return rootView;
    }

    private void setUpUi() {
        tvReportName.setText(reportItem.getReportName());
        tvReportCategory.setText(reportItem.getReportCategory());
        tvReportType.setText(reportItem.getReportType());

        String reportName = "'" + reportItem.getReportName() + "'";
        presenter.fetchFullParameterList(reportName, true);
        datePicker = MFDatePicker.newInsance(this);
    }

    private void addTableRow(FullParameterListResponse data, String identifier) {

        TableRow row = new TableRow(getContext());
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.gravity = Gravity.CENTER;
        rowParams.setMargins(0, 0, 0, 10);
        row.setLayoutParams(rowParams);
        TextView tvLabel = new TextView(getContext());
        row.addView(tvLabel);

        final Spinner spinner = new Spinner(getContext());
        row.addView(spinner);

        ArrayList<String> spinnerValues = new ArrayList<>();

        // Add the parameter keys as the text so that we can identify the spinner
        // and can later add the corresponding values in the parameter-list while
        // requesting the report.
        switch (identifier) {
            case Constants.LOAN_OFFICER_ID_SELECT:
                spinner.setTag(Constants.R_LOAN_OFFICER_ID);
                loanOfficerMap = presenter.filterIntHashMapForSpinner(data.getData(),
                        spinnerValues);
                tvLabel.setText(getString(R.string.loan_officer));
                break;
            case Constants.LOAN_PRODUCT_ID_SELECT:
                spinner.setTag(Constants.R_LOAN_PRODUCT_ID);
                loanProductMap = presenter.filterIntHashMapForSpinner(data.getData(),
                        spinnerValues);
                tvLabel.setText(getString(R.string.loanproduct));
                break;
            case Constants.LOAN_PURPOSE_ID_SELECT:
                spinner.setTag(Constants.R_LOAN_PURPOSE_ID);
                loanPurposeMap = presenter.filterIntHashMapForSpinner(data.getData(),
                        spinnerValues);
                tvLabel.setText(getString(R.string.report_loan_purpose));
                break;
            case Constants.FUND_ID_SELECT:
                spinner.setTag(Constants.R_FUND_ID);
                fundMap = presenter.filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.loan_fund));
                break;
            case Constants.CURRENCY_ID_SELECT:
                spinner.setTag(Constants.R_CURRENCY_ID);
                currencyMap = presenter.filterStringHashMapForSpinner(data.getData(),
                        spinnerValues);
                tvLabel.setText(getString(R.string.currency));
                break;
            case Constants.OFFICE_ID_SELECT:
                spinner.setTag(Constants.R_OFFICE_ID);
                officeMap = presenter.filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.office));
                break;
            case Constants.PAR_TYPE_SELECT:
                spinner.setTag(Constants.R_PAR_TYPE);
                parMap = presenter.filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.par_calculation));
                break;
            case Constants.SAVINGS_ACCOUNT_SUB_STATUS:
                spinner.setTag(Constants.R_SUB_STATUS);
                subStatusMap = presenter.filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.savings_acc_deposit));
                break;
            case Constants.SELECT_GL_ACCOUNT_NO:
                spinner.setTag(Constants.R_ACCOUNT);
                glAccountNoMap = presenter.
                        filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.glaccount));
                break;
            case Constants.OBLIG_DATE_TYPE_SELECT:
                spinner.setTag(Constants.R_OBLIG_DATE_TYPE);
                obligDateTypeMap = presenter.
                        filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.obligation_date_type));

        }
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner.getTag().toString().equals(Constants.R_OFFICE_ID) && fetchLoanOfficer) {
                    int officeId = officeMap.get(spinner.getSelectedItem().toString());
                    presenter.fetchOffices(Constants.LOAN_OFFICER_ID_SELECT, officeId, true);
                } else if (spinner.getTag().toString().
                        equals(Constants.R_CURRENCY_ID) && fetchLoanProduct) {
                    String currencyId = currencyMap.get(spinner.getSelectedItem().toString());
                    presenter.fetchProduct(Constants.LOAN_PRODUCT_ID_SELECT, currencyId, true);
                } else {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tableDetails.addView(row);
    }

    private void setReportInfo(FullParameterListResponse data, String identifier) {

        TextView tvLabel = new TextView(getContext());
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.setMargins(20, 20, 0, 5);
        tvLabel.setLayoutParams(textViewParams);
        tvLabel.setTypeface(null, Typeface.BOLD);
        //tvLabel.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.total_color));
        //ll_runreport_details.addView(tvLabel);


        final Spinner spinner = new Spinner(getContext());
        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        spinnerParams.setMargins(20, 5, 0, 0);
        spinner.setLayoutParams(spinnerParams);
        // ll_runreport_details.addView(spinner);

        View divider = new View(getContext());
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
        dividerParams.setMargins(20, 0, 20, 20);
        divider.setLayoutParams(dividerParams);
        // divider.setLayoutParams(dividerParams);
        divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black));
        //ll_runreport_details.addView(divider);


        ArrayList<String> spinnerValues = new ArrayList<>();

        // Add the parameter keys as the text so that we can identify the spinner
        // and can later add the corresponding values in the parameter-list while
        // requesting the report.
        switch (identifier) {
            case Constants.LOAN_OFFICER_ID_SELECT:
                spinner.setTag(Constants.R_LOAN_OFFICER_ID);
                loanOfficerMap = presenter.filterIntHashMapForSpinner(data.getData(),
                        spinnerValues);
                tvLabel.setText(getString(R.string.loan_officer));
                //fetchLoanOfficer = false;
                break;
            case Constants.LOAN_PRODUCT_ID_SELECT:
                    spinner.setTag(Constants.R_LOAN_PRODUCT_ID);
                    loanProductMap = presenter.filterIntHashMapForSpinner(data.getData(),
                            spinnerValues);
                    tvLabel.setText(getString(R.string.loanproduct));
                    //  fetchLoanProduct = false;
                break;
            case Constants.LOAN_PURPOSE_ID_SELECT:
                spinner.setTag(Constants.R_LOAN_PURPOSE_ID);
                loanPurposeMap = presenter.filterIntHashMapForSpinner(data.getData(),
                        spinnerValues);
                tvLabel.setText(getString(R.string.report_loan_purpose));
                break;
            case Constants.FUND_ID_SELECT:
                spinner.setTag(Constants.R_FUND_ID);
                fundMap = presenter.filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.loan_fund));
                break;
            case Constants.CURRENCY_ID_SELECT:
                spinner.setTag(Constants.R_CURRENCY_ID);
                currencyMap = presenter.filterStringHashMapForSpinner(data.getData(),
                        spinnerValues);
                tvLabel.setText(getString(R.string.currency));
                break;
            case Constants.OFFICE_ID_SELECT:
                spinner.setTag(Constants.R_OFFICE_ID);
                officeMap = presenter.filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.office));
                break;
            case Constants.PAR_TYPE_SELECT:
                spinner.setTag(Constants.R_PAR_TYPE);
                parMap = presenter.filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.par_calculation));
                break;
            case Constants.SAVINGS_ACCOUNT_SUB_STATUS:
                spinner.setTag(Constants.R_SUB_STATUS);
                subStatusMap = presenter.filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.savings_acc_deposit));
                break;
            case Constants.SELECT_GL_ACCOUNT_NO:
                spinner.setTag(Constants.R_ACCOUNT);
                glAccountNoMap = presenter.
                        filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.glaccount));
                break;
            case Constants.OBLIG_DATE_TYPE_SELECT:
                spinner.setTag(Constants.R_OBLIG_DATE_TYPE);
                obligDateTypeMap = presenter.
                        filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.obligation_date_type));

        }
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner.getTag().toString().equals(Constants.R_OFFICE_ID) && fetchLoanOfficer) {
                    int officeId = officeMap.get(spinner.getSelectedItem().toString());
                    presenter.fetchOffices(Constants.LOAN_OFFICER_ID_SELECT, officeId, true);
                } else if (spinner.getTag().toString().
                        equals(Constants.R_CURRENCY_ID) && fetchLoanProduct) {
                    String currencyId = currencyMap.get(spinner.getSelectedItem().toString());
                    presenter.fetchProduct(Constants.LOAN_PRODUCT_ID_SELECT, currencyId, true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (tvLabel.getText().equals("")) {
            return;
        }
        ll_runreport_details.addView(tvLabel);
        ll_runreport_details.addView(spinner);
        ll_runreport_details.addView(divider);
        //tableDetails.addView(row);
    }

    private void runReport() {
        if (ll_runreport_details.getChildCount() < 1) {
            Toaster.show(rootView, getString(R.string.msg_report_empty));
        } else {
            Integer fundId;
            Integer loanOfficeId;
            Integer loanProductId;
            Integer loanPurposeId;
            Integer officeId;
            Integer parId;
            Integer subId;
            Integer obligId;
            Integer glAccountId;
            String currencyId;

            Map<String, String> map = new HashMap<>();

             /* There are variable number of parameters in the request query.
              Hence, create a Map instead of hardcoding the number of
              query parameters in the Retrofit Service.*/
            boolean showRunReport = true;
            for (int i = 0; i < ll_runreport_details.getChildCount(); i++) {
                //TableRow tableRow = (TableRow) tableDetails.getChildAt(i);
                if (ll_runreport_details.getChildAt(i) instanceof Spinner) {
                    Spinner sp = (Spinner) ll_runreport_details.getChildAt(i);
                    switch (sp.getTag().toString()) {
                        case Constants.R_LOAN_OFFICER_ID:
                            if (sp.getCount() > 0) {
                                loanOfficeId = loanOfficerMap.get(sp.getSelectedItem().toString());
                                if (loanOfficeId != -1) {
                                    map.put(sp.getTag().toString(), String.valueOf(loanOfficeId));
                                }
                            }
                            break;
                        case Constants.R_LOAN_PRODUCT_ID:
                            if (sp.getCount() > 0) {
                                loanProductId = loanProductMap.get(sp.getSelectedItem().toString());
                                if (loanProductId != -1) {
                                    map.put(sp.getTag().toString(), String.valueOf(loanProductId));
                                }
                            } else {
                                //Toast.makeText(getContext(), "Product is required", Toast.LENGTH_SHORT).show();
                                showRunReport = false;
                            }
                            break;
                        case Constants.R_LOAN_PURPOSE_ID:
                            if (sp.getCount() > 0) {
                                loanPurposeId = loanPurposeMap.get(sp.getSelectedItem().toString());
                                if (loanPurposeId != -1) {
                                    map.put(sp.getTag().toString(), String.valueOf(loanPurposeId));
                                }
                            } else {
                                showRunReport = false;
                            }
                            break;
                        case Constants.R_FUND_ID:
                            if (sp.getCount() > 0) {
                                fundId = fundMap.get(sp.getSelectedItem().toString());
                                if (fundId != -1) {
                                    map.put(sp.getTag().toString(), String.valueOf(fundId));
                                }
                            } else {
                                showRunReport = false;
                            }
                            break;
                        case Constants.R_CURRENCY_ID:
                            if (sp.getCount() > 0) {
                                currencyId = currencyMap.get(sp.getSelectedItem().toString());
                                if (!currencyId.equals("")) {
                                    map.put(sp.getTag().toString(), currencyId);
                                }
                            } else {
                                showRunReport = false;
                            }
                            break;
                        case Constants.R_OFFICE_ID:
                            if (sp.getCount() > 0) {
                                officeId = officeMap.get(sp.getSelectedItem().toString());
                                if (officeId != -1) {
                                    map.put(sp.getTag().toString(), String.valueOf(officeId));
                                }
                            } else {
                                showRunReport = false;
                            }
                            break;
                        case Constants.R_PAR_TYPE:
                            if (sp.getCount() > 0) {
                                parId = parMap.get(sp.getSelectedItem().toString());
                                if (parId != -1) {
                                    map.put(sp.getTag().toString(), String.valueOf(parId));
                                }
                            } else {
                                showRunReport = false;
                            }
                            break;
                        case Constants.R_ACCOUNT:
                            if (sp.getCount() > 0) {
                                glAccountId = glAccountNoMap.get(sp.getSelectedItem().toString());
                                if (glAccountId != -1) {
                                    map.put(sp.getTag().toString(), String.valueOf(glAccountId));
                                }
                            } else {
                                showRunReport = false;
                            }
                            break;
                        case Constants.R_SUB_STATUS:
                            if (sp.getCount() > 0) {
                                subId = subStatusMap.get(sp.getSelectedItem().toString());
                                if (subId != -1) {
                                    map.put(sp.getTag().toString(), String.valueOf(subId));
                                }
                            } else {
                                showRunReport = false;
                            }
                            break;
                        case Constants.R_OBLIG_DATE_TYPE:
                            if (sp.getCount() > 0) {
                                obligId = obligDateTypeMap.get(sp.getSelectedItem().toString());
                                if (obligId != -1) {
                                    map.put(sp.getTag().toString(), String.valueOf(obligId));
                                }
                            } else {
                                showRunReport = false;
                            }
                            break;
                    }
                } else if (ll_runreport_details.getChildAt(1) instanceof EditText) {
                    EditText et = (EditText) ll_runreport_details.getChildAt(1);
                    map.put(et.getTag().toString(), et.getText().toString());
                }
            }
            if (showRunReport) {
                presenter.fetchRunReportWithQuery(reportItem.getReportName(), map);
            } else {
                showError("Insufficient data");
            }
        }
    }

    @Override
    public void showRunReport(FullParameterListResponse response) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.REPORT_NAME, response);
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack("ClientDetails");
        fragmentTransaction.replace(R.id.container, ReportFragment.newInstance(bundle))
                .commit();
    }

    @Override
    public void showOffices(FullParameterListResponse response, String identifier) {
        for (int i = 0; i < ll_runreport_details.getChildCount(); i++) {
            //TableRow tableRow = (TableRow) tableDetails.getChildAt(i);
            if (ll_runreport_details.getChildAt(i) instanceof EditText) {
                continue;
            }
            if (ll_runreport_details.getChildAt(i) instanceof Spinner) {
                Spinner sp = (Spinner) ll_runreport_details.getChildAt(i);
                if (sp.getTag().toString().equals(Constants.R_LOAN_OFFICER_ID)) {
                    ArrayList<String> spinnerValues = new ArrayList<>();
                    loanOfficerMap = presenter.filterIntHashMapForSpinner(response.getData(),
                            spinnerValues);
                    ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_item, spinnerValues);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp.setAdapter(adapter);
                    return;
                }
            }
        }

        //addTableRow(response, identifier);
        setReportInfo(response, identifier);

    }

    @Override
    public void showProduct(FullParameterListResponse response, String identifier) {
        for (int i = 0; i < ll_runreport_details.getChildCount(); i++) {
            //TableRow tableRow = (TableRow) tableDetails.getChildAt(i);
            if (ll_runreport_details.getChildAt(i) instanceof EditText) {
                continue;
            }
            if (ll_runreport_details.getChildAt(i) instanceof Spinner) {
                Spinner sp = (Spinner) ll_runreport_details.getChildAt(i);
                if (sp.getTag().toString().equals(Constants.R_LOAN_PRODUCT_ID)) {
                    ArrayList<String> spinnerValues = new ArrayList<>();
                    loanProductMap = presenter.filterIntHashMapForSpinner(response.getData(),
                            spinnerValues);
                    ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_item, spinnerValues);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp.setAdapter(adapter);
                    return;
                }
            }
        }
        //addTableRow(response, identifier);
        setReportInfo(response, identifier);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_runreport, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_run_report:
                runReport();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showError(String error) {
        Toaster.show(rootView, error);
    }

    @Override
    public void showFullParameterResponse(FullParameterListResponse response) {
        for (DataRow row : response.getData()) {
            switch (row.getRow().get(0)) {
                case Constants.LOAN_OFFICER_ID_SELECT:
                    fetchLoanOfficer = true;
                    break;
                case Constants.LOAN_PRODUCT_ID_SELECT:
                    fetchLoanProduct = true;
                    break;
                case Constants.START_DATE_SELECT:
                    addTextView(Constants.START_DATE_SELECT);
                    break;
                case Constants.END_DATE_SELECT:
                    addTextView(Constants.END_DATE_SELECT);
                    break;
                case Constants.SELECT_ACCOUNT:
                    addTextView(Constants.SELECT_ACCOUNT);
                    break;
                case Constants.FROM_X_SELECT:
                    addTextView(Constants.FROM_X_SELECT);
                    break;
                case Constants.TO_Y_SELECT:
                    addTextView(Constants.TO_Y_SELECT);
                    break;
                case Constants.OVERDUE_X_SELECT:
                    addTextView(Constants.OVERDUE_X_SELECT);
                    break;
                case Constants.OVERDUE_Y_SELECT:
                    addTextView(Constants.OVERDUE_Y_SELECT);
                    break;
            }
            presenter.fetchParameterDetails(row.getRow().get(0), true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void addTextView(String identifier) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20, 25, 0, 0);
        final TextView tvLabel = new TextView(getContext());
        tvLabel.setLayoutParams(layoutParams);
        tvLabel.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams editTextLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editTextLayoutParams.setMargins(20, -10, 0, 10);
        tvField = new EditText(getContext());
        tvField.setInputType(InputType.TYPE_NULL);
        //tvField.setClickable(true);
        tvField.setLayoutParams(editTextLayoutParams);
        tvField.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.black));


        switch (identifier) {
            case Constants.START_DATE_SELECT:
                tvField.setTag(Constants.R_START_DATE);
                tvLabel.setText(getString(R.string.start_date));
                break;
            case Constants.END_DATE_SELECT:
                tvField.setTag(Constants.R_END_DATE);
                tvLabel.setText(getString(R.string.end_date));
                break;
            case Constants.SELECT_ACCOUNT:
                tvField.setTag(Constants.R_ACCOUNT_NO);
                tvLabel.setText(getString(R.string.enter_account_no));
                break;
            case Constants.FROM_X_SELECT:
                tvField.setTag(Constants.R_FROM_X);
                tvLabel.setText(getString(R.string.from_x_number));
                break;
            case Constants.TO_Y_SELECT:
                tvField.setTag(Constants.R_TO_Y);
                tvLabel.setText(getString(R.string.to_y_number));
                break;
            case Constants.OVERDUE_X_SELECT:
                tvField.setTag(Constants.R_OVERDUE_X);
                tvLabel.setText(getString(R.string.overdue_x_number));
                break;
            case Constants.OVERDUE_Y_SELECT:
                tvField.setTag(Constants.R_OVERDUE_Y);
                tvLabel.setText(getString(R.string.overdue_y_number));
                break;
        }
        tvField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dateField = v.getTag().toString();
                    if (dateField.equals(Constants.R_START_DATE) ||
                            dateField.equals(Constants.R_END_DATE)) {
                        datePicker.show(getActivity().getSupportFragmentManager(),
                                FragmentConstants.DFRAG_DATE_PICKER);
                    }
                } catch (Exception e) {
                    Log.e("Error ", e.getMessage());
                }
            }
        });
        // tableDetails.addView(row);
        ll_runreport_details.addView(tvLabel);
        ll_runreport_details.addView(tvField);

    }

    @Override
    public void showParameterDetails(FullParameterListResponse response, String identifier) {
        //addTableRow(response, identifier);
        setReportInfo(response, identifier);
    }

    @Override
    public void showProgressbar(boolean b) {
        if (b) {
            showMifosProgressDialog();
        } else {
            hideMifosProgressDialog();
        }
    }

    @Override
    public void onDatePicked(String date) {
        try {
            for (int i = 0; i < ll_runreport_details.getChildCount(); i++) {
                //TableRow tableRow = (TableRow) tableDetails.getChildAt(i);
                if (ll_runreport_details.getChildAt(i) instanceof Spinner) {
                    continue;
                }
                if (ll_runreport_details.getChildAt(i) instanceof EditText) {
                    EditText et = (EditText) ll_runreport_details.getChildAt(i);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date dateModified = null;
                    try {
                        dateModified = simpleDateFormat.parse(date);
                    } catch (ParseException e) {

                    }
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                    if (et.getTag().toString().equals(dateField)) {
                        et.setInputType(InputType.TYPE_CLASS_TEXT);
                        et.setText(simpleDateFormat1.format(dateModified));
                        et.setInputType(InputType.TYPE_NULL);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Error ", e.getMessage());
        }
    }
}
