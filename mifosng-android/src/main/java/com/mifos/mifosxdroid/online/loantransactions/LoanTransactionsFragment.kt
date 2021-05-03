/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loantransactions

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ExpandableListView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.LoanTransactionAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.objects.accounts.loan.LoanWithAssociations
import com.mifos.objects.accounts.loan.Transaction
import com.mifos.utils.Constants
import com.mifos.utils.DateHelper
import javax.inject.Inject

class LoanTransactionsFragment : MifosBaseFragment(), LoanTransactionsMvpView {
    @kotlin.jvm.JvmField
    @BindView(R.id.elv_loan_transactions)
    var elv_loanTransactions: ExpandableListView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.et_loan_repayment_schedule_search)
    var et_loan_repayment_schedule_search: EditText? = null

    var transactionList: ArrayList<Transaction>? = null


    @kotlin.jvm.JvmField
    @Inject
    var mLoanTransactionsPresenter: LoanTransactionsPresenter? = null
    private var adapter: LoanTransactionAdapter? = null
    private var loanAccountNumber = 0
    private lateinit var rootView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) loanAccountNumber = arguments!!.getInt(Constants.LOAN_ACCOUNT_NUMBER)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_loan_transactions, container, false)
        ButterKnife.bind(this, rootView)
        mLoanTransactionsPresenter!!.attachView(this)

        et_loan_repayment_schedule_search?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (s.isEmpty().not() && s.isBlank().not()) {
                    searchList(s.toString())
                } else {
                    adapter?.setTransactionList(transactionList)
                }
            }
        })
        inflateLoanTransactions()
        return rootView
    }

    fun searchList(value: String) {
        val filtertedList = transactionList?.filter { it ->
            DateHelper.getDateAsString(it.date).toLowerCase().contains(value.toLowerCase())
        } as List<Transaction>
        Log.e("Filtered List size  ", filtertedList?.size.toString())
        if (filtertedList.size > 0) {
            adapter?.setTransactionList(filtertedList)

        } else {
            adapter?.setTransactionList(transactionList)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        super.onPrepareOptionsMenu(menu)
    }

    fun inflateLoanTransactions() {
        mLoanTransactionsPresenter!!.loadLoanTransaction(loanAccountNumber)
    }

    override fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }

    override fun showLoanTransaction(loanWithAssociations: LoanWithAssociations) {
        Log.i("Transaction List Size", "" + loanWithAssociations.transactions.size)
        transactionList = loanWithAssociations.transactions as ArrayList
        adapter = LoanTransactionAdapter(activity,
                loanWithAssociations.transactions)
        elv_loanTransactions!!.setAdapter(adapter)
        elv_loanTransactions!!.setGroupIndicator(null)
    }

    override fun showFetchingError(s: String?) {
        Toaster.show(rootView, s)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLoanTransactionsPresenter!!.detachView()
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun newInstance(loanAccountNumber: Int): LoanTransactionsFragment {
            val fragment = LoanTransactionsFragment()
            val args = Bundle()
            args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber)
            fragment.arguments = args
            return fragment
        }
    }
}