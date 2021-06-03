/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.dialogfragments.datatablerowdialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.api.GenericResponse;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.formwidgets.FormEditText;
import com.mifos.mifosxdroid.formwidgets.FormNumericEditText;
import com.mifos.mifosxdroid.formwidgets.FormSpinner;
import com.mifos.mifosxdroid.formwidgets.FormToggleButton;
import com.mifos.mifosxdroid.formwidgets.FormWidget;
import com.mifos.objects.noncore.ColumnHeader;
import com.mifos.objects.noncore.ColumnValue;
import com.mifos.objects.noncore.DataTable;
import com.mifos.utils.Constants;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ishankhanna on 01/08/14.
 */
public class DataTableRowDialogFragment extends DialogFragment
        implements DataTableRowDialogMvpView {

    private final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.ll_data_table_entry_form)
    LinearLayout linearLayout;

    @BindView(R.id.tv_title)
    TextView tv_title;


    @Inject
    DataTableRowDialogPresenter dataTableRowDialogPresenter;

    private View rootView;

    private DataTable dataTable;
    private int entityId;
    private SafeUIBlockingUtility safeUIBlockingUtility;
    private List<FormWidget> listFormWidgets = new ArrayList<>();


    //TODO Check for Static vs Bundle Approach
    public static DataTableRowDialogFragment newInstance(DataTable dataTable, int entityId) {
        DataTableRowDialogFragment dataTableRowDialogFragment = new DataTableRowDialogFragment();
        Bundle args = new Bundle();
        dataTableRowDialogFragment.dataTable = dataTable;
        dataTableRowDialogFragment.entityId = entityId;
        dataTableRowDialogFragment.setArguments(args);
        return dataTableRowDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        /**
         * This is very Important
         * It is used to auto resize the dialog when a Keyboard appears.
         * And User can still easily scroll through the form. Sweet, isn't it?
         */
     /*   getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_ADJUST_RESIZE);*/
        getDialog().getWindow().addFlags(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        rootView = inflater.inflate(R.layout.dialog_fragment_add_entry_to_datatable, container,
                false);

        ButterKnife.bind(this, rootView);
        dataTableRowDialogPresenter.attachView(this);


        //getDialog().getWindow().setTitle("");
        tv_title.setText(dataTable.getRegisteredTableName());

        safeUIBlockingUtility = new SafeUIBlockingUtility(DataTableRowDialogFragment.this
                .getActivity(), getString(R.string.data_table_row_dialog_loading_message));

        createForm(dataTable);
        addSaveButton();

        return rootView;
    }

    public void createForm(DataTable table) {
        List<FormWidget> formWidgets = new ArrayList<>();

        for (ColumnHeader columnHeader : table.getColumnHeaderData()) {

            if (!columnHeader.getColumnPrimaryKey()) {

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10, 20, 10, 20);

                if (columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_STRING) ||
                        columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_TEXT)) {

                    FormEditText formEditText = new FormEditText(getActivity(), "");
                    formEditText.getView().setLayoutParams(layoutParams);
                    formWidgets.add(formEditText);
                    linearLayout.addView(formEditText.getView());

                } else if (columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_INT)) {

                    FormNumericEditText formNumericEditText = new FormNumericEditText(getActivity
                            (), "");
                    formNumericEditText.setReturnType(FormWidget.SCHEMA_KEY_INT);
                    formNumericEditText.getView().setLayoutParams(layoutParams);
                    formWidgets.add(formNumericEditText);
                    linearLayout.addView(formNumericEditText.getView());


                } else if (columnHeader.getColumnDisplayType().equals(FormWidget
                        .SCHEMA_KEY_DECIMAL)) {

                    FormNumericEditText formNumericEditText = new FormNumericEditText(getActivity
                            (), "");
                    formNumericEditText.setReturnType(FormWidget.SCHEMA_KEY_DECIMAL);
                    formNumericEditText.getView().setLayoutParams(layoutParams);
                    formWidgets.add(formNumericEditText);
                    linearLayout.addView(formNumericEditText.getView());


                } else if (columnHeader.getColumnDisplayType().equals(FormWidget
                        .SCHEMA_KEY_CODELOOKUP) || columnHeader.getColumnDisplayType().equals
                        (FormWidget.SCHEMA_KEY_CODEVALUE)) {

                    if (columnHeader.getColumnValues().size() > 0) {
                        List<String> columnValueStrings = new ArrayList<>();
                        List<Integer> columnValueIds = new ArrayList<>();

                        for (ColumnValue columnValue : columnHeader.getColumnValues()) {
                            columnValueStrings.add(columnValue.getValue());
                            columnValueIds.add(columnValue.getId());
                        }

                        FormSpinner formSpinner = new FormSpinner(getActivity(),"", columnValueStrings, columnValueIds);
                        formSpinner.getView().setLayoutParams(layoutParams);
                        formSpinner.setReturnType(FormWidget.SCHEMA_KEY_CODEVALUE);
                        formWidgets.add(formSpinner);
                        linearLayout.addView(formSpinner.getView());
                    }

                } else if (columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_DATE)) {

                    FormEditText formEditText = new FormEditText(getActivity(),"");
                    formEditText.setIsDateField(true, getActivity().getSupportFragmentManager());
                    formEditText.getView().setLayoutParams(layoutParams);
                    formWidgets.add(formEditText);
                    linearLayout.addView(formEditText.getView());
                } else if (columnHeader.getColumnDisplayType().equals(FormWidget.SCHEMA_KEY_BOOL)) {

                    FormToggleButton formToggleButton = new FormToggleButton(getActivity(),
                            "");
                    formToggleButton.getView().setLayoutParams(layoutParams);
                    formWidgets.add(formToggleButton);
                    linearLayout.addView(formToggleButton.getView());
                }
            }
        }
        listFormWidgets.addAll(formWidgets);
    }

    private void addSaveButton() {
        Button bt_processForm = new Button(getActivity());
        LinearLayout.LayoutParams butLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bt_processForm.setLayoutParams(butLayoutParams);
        bt_processForm.setBackgroundResource(R.drawable.login_button_rounded);
        bt_processForm.setText(getString(R.string.save));
        // bt_processForm.setBackgroundColor(getActivity().getResources().getColor(R.color.blue_dark));

        linearLayout.addView(bt_processForm);
        bt_processForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onSaveActionRequested();
                } catch (RequiredFieldException e) {
                    Log.d(LOG_TAG, e.getMessage());
                }
            }
        });
    }

    public void onSaveActionRequested() throws RequiredFieldException {
        dataTableRowDialogPresenter.addDataTableEntry(dataTable.getRegisteredTableName(),
                entityId, addDataTableInput());
    }

    private HashMap<String, Object> addDataTableInput() {
        List<FormWidget> formWidgets = listFormWidgets;
        HashMap<String, Object> payload = new HashMap<>();
        payload.put(Constants.DATE_FORMAT, "dd-mm-YYYY");
        payload.put(Constants.LOCALE, "en");
        for (FormWidget formWidget : formWidgets) {
            if (formWidget.getReturnType().equals(FormWidget.SCHEMA_KEY_INT)) {
                payload.put(formWidget.getPropertyName(), Integer.parseInt(formWidget.getValue()
                        .equals("") ? "0" : formWidget.getValue()));
            } else if (formWidget.getReturnType().equals(FormWidget.SCHEMA_KEY_DECIMAL)) {
                payload.put(formWidget.getPropertyName(), Double.parseDouble(formWidget.getValue
                        ().equals("") ? "0.0" : formWidget.getValue()));
            } else if (formWidget.getReturnType().equals(FormWidget.SCHEMA_KEY_CODEVALUE)) {
                FormSpinner formSpinner = (FormSpinner) formWidget;
                payload.put(formWidget.getPropertyName(), formSpinner.getIdOfSelectedItem
                        (formWidget.getValue()));
            } else {
                payload.put(formWidget.getPropertyName(), formWidget.getValue());
            }
        }
        return payload;
    }

    @Override
    public void showDataTableEntrySuccessfully(GenericResponse genericResponse) {
        Toast.makeText(getActivity(), R.string.data_table_entry_added, Toast.LENGTH_LONG).show();
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
                getActivity().getIntent());
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showProgressbar(boolean b) {
        if (b) {
            safeUIBlockingUtility.safelyBlockUI();
        } else {
            safeUIBlockingUtility.safelyUnBlockUI();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dataTableRowDialogPresenter.detachView();
    }
}
