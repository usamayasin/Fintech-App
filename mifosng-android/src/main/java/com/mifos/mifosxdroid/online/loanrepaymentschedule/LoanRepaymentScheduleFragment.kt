/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loanrepaymentschedule

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.LoanRepaymentScheduleAdapter
import com.mifos.mifosxdroid.adapters.LoanTransactionAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.objects.accounts.loan.LoanWithAssociations
import com.mifos.objects.accounts.loan.Period
import com.mifos.objects.accounts.loan.RepaymentSchedule
import com.mifos.utils.Constants
import com.mifos.utils.DateHelper
import javax.inject.Inject

class LoanRepaymentScheduleFragment : ProgressableFragment(), LoanRepaymentScheduleMvpView {
    private val LOG_TAG = javaClass.simpleName

    @kotlin.jvm.JvmField
    @BindView(R.id.lv_repayment_schedule)
    var lv_repaymentSchedule: ListView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_total_paid)
    var tv_totalPaid: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_total_upcoming)
    var tv_totalUpcoming: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_total_overdue)
    var tv_totalOverdue: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.et_loan_repayment__schedule_search)
    var et_loan_repayment__schedule_search: EditText? = null

    @kotlin.jvm.JvmField
    @Inject
    var mLoanRepaymentSchedulePresenter: LoanRepaymentSchedulePresenter? = null
    private var loanAccountNumber = 0
    private lateinit var rootView: View
    private lateinit var periodList: List<Period>
    private var loanRepaymentScheduleAdapter: LoanRepaymentScheduleAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) loanAccountNumber = arguments!!.getInt(Constants.LOAN_ACCOUNT_NUMBER)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_loan_repayment_schedule, container, false)
        setToolbarTitle(resources.getString(R.string.loan_repayment_schedule))
        ButterKnife.bind(this, rootView)
        mLoanRepaymentSchedulePresenter!!.attachView(this)
        inflateRepaymentSchedule()

        et_loan_repayment__schedule_search?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (s.isEmpty().not() && s.isBlank().not()) {
                    searchList(s.toString())
                } else {
                    loanRepaymentScheduleAdapter?.setLoanRepaymentSchedulList(periodList)
                }
            }
        })
        return rootView
    }

    fun searchList(value: String) {

        val filteredList = periodList?.filter { it ->
            DateHelper.getDateAsString(it.dueDate).toLowerCase().contains(value.toLowerCase())
        } as List<Period>
        if (filteredList.size > 0) {
            loanRepaymentScheduleAdapter?.setLoanRepaymentSchedulList(filteredList)

        } else {
            loanRepaymentScheduleAdapter?.setLoanRepaymentSchedulList(periodList)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        super.onPrepareOptionsMenu(menu)
    }

    fun inflateRepaymentSchedule() {
        mLoanRepaymentSchedulePresenter!!.loadLoanRepaySchedule(loanAccountNumber)
    }

    override fun showProgressbar(b: Boolean) {
        showProgress(b)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLoanRepaymentSchedulePresenter!!.detachView()
    }

    override fun showLoanRepaySchedule(loanWithAssociations: LoanWithAssociations) {

        try {
            /* Activity is null - Fragment has been detached; no need to do anything. */
            if (activity == null) return

            val listOfActualPeriods = loanWithAssociations
                    .repaymentSchedule
                    .getlistOfActualPeriods()

            loanRepaymentScheduleAdapter = LoanRepaymentScheduleAdapter(activity, listOfActualPeriods)
            lv_repaymentSchedule!!.adapter = loanRepaymentScheduleAdapter
            val totalRepaymentsCompleted = resources.getString(R.string.complete) + "" +
                    " : "
            val totalRepaymentsOverdue = resources.getString(R.string.overdue) + " : "
            val totalRepaymentsPending = resources.getString(R.string.pending) + " : "
            //Implementing the Footer here
            tv_totalPaid!!.text = totalRepaymentsCompleted + RepaymentSchedule
                    .getNumberOfRepaymentsComplete(listOfActualPeriods)
            tv_totalOverdue!!.text = totalRepaymentsOverdue + RepaymentSchedule
                    .getNumberOfRepaymentsOverDue(listOfActualPeriods)
            tv_totalUpcoming!!.text = totalRepaymentsPending + RepaymentSchedule
                    .getNumberOfRepaymentsPending(listOfActualPeriods)

            periodList = loanWithAssociations
                    .repaymentSchedule
                    .getlistOfActualPeriods()
        } catch (e: Exception) {
            Log.e("Error ", e.message.toString())
        }
    }

    override fun showFetchingError(s: String?) {
        Toaster.show(rootView, s)
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun newInstance(loanAccountNumber: Int): LoanRepaymentScheduleFragment {
            val fragment = LoanRepaymentScheduleFragment()
            val args = Bundle()
            args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber)
            fragment.arguments = args
            return fragment
        }
    }
}