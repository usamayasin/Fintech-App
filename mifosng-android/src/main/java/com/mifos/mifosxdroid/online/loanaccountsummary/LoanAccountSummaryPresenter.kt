package com.mifos.mifosxdroid.online.loanaccountsummary

import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.accounts.loan.LoanWithAssociations
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 07/06/16.
 */
class LoanAccountSummaryPresenter @Inject constructor(private val mDataManagerLoan: DataManagerLoan) : BasePresenter<LoanAccountSummaryMvpView?>() {
    private val mSubscriptions: CompositeSubscription
    override fun attachView(mvpView: LoanAccountSummaryMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
    }

    fun loadLoanById(loanAccountNumber: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerLoan.getLoanById(loanAccountNumber)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<LoanWithAssociations?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError("Loan Account not found.")
                    }

                    override fun onNext(loanWithAssociations: LoanWithAssociations?) {
                        mvpView!!.showProgressbar(false)
                        loanWithAssociations?.let { mvpView!!.showLoanById(it) }
                    }
                }))
    }

    init {
        mSubscriptions = CompositeSubscription()
    }
}