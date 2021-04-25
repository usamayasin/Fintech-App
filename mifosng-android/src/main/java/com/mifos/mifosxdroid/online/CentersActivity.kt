/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online

import android.os.Bundle
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.online.centerdetails.CenterDetailsFragment
import com.mifos.mifosxdroid.online.centerdetails.CenterDetailsFragment.Companion.newInstance
import com.mifos.mifosxdroid.online.clientlist.ClientListFragment
import com.mifos.mifosxdroid.online.grouplist.GroupListFragment
import com.mifos.mifosxdroid.online.savingsaccount.SavingsAccountFragment
import com.mifos.objects.client.Client
import com.mifos.utils.Constants

class CentersActivity : MifosBaseActivity(), GroupListFragment.OnFragmentInteractionListener, CenterDetailsFragment.OnFragmentInteractionListener {
    private val LOG_TAG = this.javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        showBackButton()
        val bundle = intent.extras
        if (bundle != null) {
            val centerId = bundle.getInt(Constants.CENTER_ID)
            replaceFragment(newInstance(centerId), false, R.id.container)
        }
    }

    override fun loadGroupsOfCenter(centerId: Int) {
        replaceFragment(GroupListFragment.newInstance(centerId), true, R.id.container)
    }

    override fun loadClientsOfGroup(clientList: List<Client?>?) {
        replaceFragment(ClientListFragment.newInstance(clientList, true), true, R.id.container)
    }

    override fun addCenterSavingAccount(centerId: Int) {
        replaceFragment(SavingsAccountFragment.newInstance(centerId, true), true, R.id.container)
    }
}