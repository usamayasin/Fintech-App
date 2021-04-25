package com.mifos.mifosxdroid.online.grouploanaccount

import com.mifos.api.DataManager
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.accounts.loan.Loans
import com.mifos.objects.organisation.LoanProducts
import com.mifos.objects.templates.loans.*
import com.mifos.services.data.GroupLoanPayload
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 08/06/16.
 */
class GroupLoanAccountPresenter @Inject constructor(private val mDataManager: DataManager) : BasePresenter<GroupLoanAccountMvpView?>() {
    private val mSubscriptions: CompositeSubscription
    override fun attachView(mvpView: GroupLoanAccountMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.clear()
    }

    fun loadAllLoans() {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManager.allLoans
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<LoanProducts?>?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError("Failed to fetch Loans")
                    }

                    override fun onNext(productLoans: List<LoanProducts?>?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showAllLoans(productLoans)
                    }
                })
        )
    }

    fun loadGroupLoansAccountTemplate(groupId: Int, productId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManager.getGroupLoansAccountTemplate(groupId, productId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GroupLoanTemplate?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError("Failed to load GroupLoan")
                    }

                    override fun onNext(groupLoanTemplate: GroupLoanTemplate?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showGroupLoanTemplate(
                                groupLoanTemplate)
                    }
                })
        )
    }

    fun createGroupLoanAccount(loansPayload: GroupLoanPayload?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManager.createGroupLoansAccount(loansPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Loans?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError("Try Again")
                    }

                    override fun onNext(loans: Loans?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showGroupLoansAccountCreatedSuccessfully(loans)
                    }
                })
        )
    }

    fun filterAmortizations(amortizationTypeOptions: List<AmortizationTypeOptions>?): List<String> {
        val amortizationNameList = ArrayList<String>()
        Observable.from(amortizationTypeOptions)
                .subscribe { amortizationTypeOptions -> amortizationNameList.add(amortizationTypeOptions.value) }
        return amortizationNameList
    }

    fun filterInterestCalculationPeriods(interestCalculationPeriodType: List<InterestCalculationPeriodType>?): List<String> {
        val interestCalculationPeriodNameList = ArrayList<String>()
        Observable.from(interestCalculationPeriodType)
                .subscribe { interestCalculationPeriodType ->
                    interestCalculationPeriodNameList.add(
                            interestCalculationPeriodType.value)
                }
        return interestCalculationPeriodNameList
    }

    fun filterTransactionProcessingStrategies(transactionProcessingStrategyOptions: List<TransactionProcessingStrategyOptions>?): List<String> {
        val transactionProcessingStrategyNameList = ArrayList<String>()
        Observable.from(transactionProcessingStrategyOptions)
                .subscribe { transactionProcessingStrategyOptions ->
                    transactionProcessingStrategyNameList.add(
                            transactionProcessingStrategyOptions.name)
                }
        return transactionProcessingStrategyNameList
    }

    fun filterLoanProducts(loanProducts: List<LoanProducts>?): List<String> {
        val loanProductsNameList = ArrayList<String>()
        Observable.from(loanProducts)
                .subscribe { loanProducts -> loanProductsNameList.add(loanProducts.name) }
        return loanProductsNameList
    }

    fun filterTermFrequencyTypes(termFrequencyTypeOptions: List<TermFrequencyTypeOptions>?): List<String> {
        val termFrequencyNameList = ArrayList<String>()
        Observable.from(termFrequencyTypeOptions)
                .subscribe { termFrequencyTypeOptions -> termFrequencyNameList.add(termFrequencyTypeOptions.value) }
        return termFrequencyNameList
    }

    fun filterLoanPurposeTypes(loanPurposeOptions: List<LoanPurposeOptions>?): List<String> {
        val loanPurposeNameList = ArrayList<String>()
        Observable.from(loanPurposeOptions)
                .subscribe { loanPurposeOptions -> loanPurposeNameList.add(loanPurposeOptions.name) }
        return loanPurposeNameList
    }

    fun filterInterestTypeOptions(interestTypeOptions: List<InterestTypeOptions>?): List<String> {
        val interestTypeNameList = ArrayList<String>()
        Observable.from(interestTypeOptions)
                .subscribe { interestTypeOptions -> interestTypeNameList.add(interestTypeOptions.value) }
        return interestTypeNameList
    }

    fun filterLoanOfficers(loanOfficerOptions: List<LoanOfficerOptions>?): List<String> {
        val loanOfficerNameList = ArrayList<String>()
        Observable.from(loanOfficerOptions)
                .subscribe { loanOfficerOptions -> loanOfficerNameList.add(loanOfficerOptions.displayName) }
        return loanOfficerNameList
    }

    fun filterFunds(fundOptions: List<FundOptions>?): List<String> {
        val fundNameList = ArrayList<String>()
        Observable.from(fundOptions)
                .subscribe { fundOptions -> fundNameList.add(fundOptions.name) }
        return fundNameList
    }

    init {
        mSubscriptions = CompositeSubscription()
    }
}