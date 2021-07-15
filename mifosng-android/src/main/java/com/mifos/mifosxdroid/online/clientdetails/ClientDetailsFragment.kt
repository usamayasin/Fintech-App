/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.clientdetails

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.activity.pinpointclient.PinpointClientActivity
import com.mifos.mifosxdroid.adapters.LoanAccountsListAdapter
import com.mifos.mifosxdroid.adapters.SavingsAccountsListAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.online.activate.ActivateFragment
import com.mifos.mifosxdroid.online.clientcharge.ClientChargeFragment
import com.mifos.mifosxdroid.online.clientidentifiers.ClientIdentifiersFragment
import com.mifos.mifosxdroid.online.datatable.DataTableFragment
import com.mifos.mifosxdroid.online.documentlist.DocumentListFragment
import com.mifos.mifosxdroid.online.loanaccount.LoanAccountFragment
import com.mifos.mifosxdroid.online.note.NoteFragment
import com.mifos.mifosxdroid.online.savingsaccount.SavingsAccountFragment
import com.mifos.mifosxdroid.online.sign.SignatureFragment
import com.mifos.mifosxdroid.online.surveylist.SurveyListFragment
import com.mifos.mifosxdroid.views.CircularImageView
import com.mifos.objects.accounts.ClientAccounts
import com.mifos.objects.accounts.savings.DepositType
import com.mifos.objects.client.Charges
import com.mifos.objects.client.Client
import com.mifos.utils.Constants
import com.mifos.utils.FragmentConstants
import com.mifos.utils.ImageLoaderUtils
import com.mifos.utils.Utils
import kotlinx.android.synthetic.main.fragment_client_details.*
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject

class ClientDetailsFragment : MifosBaseFragment(), ClientDetailsMvpView {
    var imgDecodableString: String? = null
    private val TAG = ClientDetailsFragment::class.java.simpleName
    var clientId = 0
    var chargesList: MutableList<Charges> = ArrayList()
    var rvAdapter: RecyclerView.Adapter<*>? = null
    var rvlayoutManager: RecyclerView.LayoutManager? = null


    @JvmField
    @BindView(R.id.tv_fullName)
    var tv_fullName: TextView? = null

    @JvmField
    @BindView(R.id.tv_accountNumber)
    var tv_accountNumber: TextView? = null

    @JvmField
    @BindView(R.id.tv_externalId)
    var tv_externalId: TextView? = null

    @JvmField
    @BindView(R.id.tv_activationDate)
    var tv_activationDate: TextView? = null

    @JvmField
    @BindView(R.id.tv_office)
    var tv_office: TextView? = null

   /* @JvmField
    @BindView(R.id.tv_mobile_no)
    var tvMobileNo: TextView? = null*/

    @JvmField
    @BindView(R.id.tv_group)
    var tvGroup: TextView? = null

    @JvmField
    @BindView(R.id.iv_expandable)
    var ivExpandView: ImageView? = null

    @JvmField
    @BindView(R.id.iv_clientImage)
    var iv_clientImage: CircularImageView? = null

    @JvmField
    @BindView(R.id.pb_imageProgressBar)
    var pb_imageProgressBar: ProgressBar? = null

   /* @JvmField
    @BindView(R.id.row_account)
    var rowAccount: TableRow? = null

    @JvmField
    @BindView(R.id.row_external)
    var rowExternal: TableRow? = null

    @JvmField
    @BindView(R.id.row_activation)
    var rowActivation: TableRow? = null

    @JvmField
    @BindView(R.id.row_office)
    var rowOffice: TableRow? = null

    @JvmField
    @BindView(R.id.row_group)
    var rowGroup: TableRow? = null

    @JvmField
    @BindView(R.id.row_staff)
    var rowStaff: TableRow? = null

    @JvmField
    @BindView(R.id.row_loan)
    var rowLoan: TableRow? = null

    @JvmField
    @BindView(R.id.tableRow_mobile_no)
    var rowMobileNo: TableRow? = null*/

    @JvmField
    @BindView(R.id.ll_bottom_panel)
    var llBottomPanel: LinearLayout? = null

    @JvmField
    @BindView(R.id.rl_client)
    var rlClient: RelativeLayout? = null

    @JvmField
    @Inject
    var mClientDetailsPresenter: ClientDetailsPresenter? = null
    private lateinit var rootView: View
    private var mListener: OnFragmentInteractionListener? = null
    private val clientImageFile = File(Environment.getExternalStorageDirectory().toString() +
            "/client_image.png")
    private var isClientActive = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) {
            clientId = arguments!!.getInt(Constants.CLIENT_ID)
        }
        setHasOptionsMenu(true)
        checkPermissions()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_client_details, container, false)
        ButterKnife.bind(this, rootView)
        mClientDetailsPresenter!!.attachView(this)
        inflateClientInformation()
        return rootView
    }

    @OnClick(R.id.btn_activate_client)
    fun onClickActivateClient() {
        activateClient()
    }

    fun inflateClientInformation() {
        mClientDetailsPresenter!!.loadClientDetailsAndClientAccounts(clientId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iv_expandable.setOnClickListener {
            rv_loan_accounts.setVisibility(
                    if (rv_loan_accounts.getVisibility() == View.VISIBLE) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
            )

            if (rv_loan_accounts.visibility == View.GONE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    iv_expandable.background = resources.getDrawable(R.drawable.circular_bg_green)
                }
                iv_expandable.setImageResource(R.drawable.ic_add_green)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    iv_expandable.background = resources.getDrawable(R.drawable.circular_bg_pink)
                }
                iv_expandable.setImageResource(R.drawable.ic_minus_pink)
            }

        }

        iv_savings_expandable.setOnClickListener {
            rv_saving_accounts.setVisibility(
                    if (rv_saving_accounts.getVisibility() == View.VISIBLE) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
            )
            if (rv_saving_accounts.visibility == View.GONE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    iv_savings_expandable.background = resources.getDrawable(R.drawable.circular_bg_green)
                }
                iv_savings_expandable.setImageResource(R.drawable.ic_add_green)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    iv_savings_expandable.background = resources.getDrawable(R.drawable.circular_bg_pink)
                }
                iv_savings_expandable.setImageResource(R.drawable.ic_minus_pink)
            }
        }

        iv_recurring_expandable.setOnClickListener {
            rv_recurring_accounts.setVisibility(
                    if (rv_recurring_accounts.getVisibility() == View.VISIBLE) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
            )
            if (rv_recurring_accounts.visibility == View.GONE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    iv_recurring_expandable.background = resources.getDrawable(R.drawable.circular_bg_green)
                }
                iv_recurring_expandable.setImageResource(R.drawable.ic_add_green)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    iv_recurring_expandable.background = resources.getDrawable(R.drawable.circular_bg_pink)
                }
                iv_recurring_expandable.setImageResource(R.drawable.ic_minus_pink)
            }
        }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mListener = try {
            activity as OnFragmentInteractionListener
        } catch (e: ClassCastException) {
            throw ClassCastException(requireActivity().javaClass.simpleName + " must " +
                    "implement OnFragmentInteractionListener")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            // When an Image is picked
            if (requestCode == UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && null != data) {
                // Get the Image from data
                val selectedImage = data.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)


                // Get the cursor
                val cursor = activity!!.applicationContext.contentResolver.query(
                        selectedImage,
                        filePathColumn, null, null, null)
                // Move to first row
                cursor.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                imgDecodableString = cursor.getString(columnIndex)
                cursor.close()
                val pickedImage = BitmapFactory.decodeFile(imgDecodableString)
                saveBitmap(clientImageFile, pickedImage)
                uploadImage(clientImageFile)
            } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
                    && resultCode == Activity.RESULT_OK) {
                uploadImage(clientImageFile)
            } else {
                Toaster.show(rootView, R.string.havent_picked_image,
                        Toast.LENGTH_LONG)
            }
        } catch (e: Exception) {
            Toaster.show(rootView, e.toString(), Toast.LENGTH_LONG)
        }
    }

    fun saveBitmap(file: File, mBitmap: Bitmap) {
        try {
            file.createNewFile()
            var fOut: FileOutputStream? = null
            fOut = FileOutputStream(file)
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
        } catch (exception: Exception) {
            //Empty catch block to prevent crashing
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        if (isClientActive) {
            menu.add(Menu.NONE, MENU_ITEM_DATA_TABLES, Menu.NONE, getString(R.string.more_info))
            menu.add(Menu.NONE, MENU_ITEM_PIN_POINT, Menu.NONE, getString(R.string.pinpoint))
            menu.add(Menu.NONE, MENU_ITEM_CLIENT_CHARGES, Menu.NONE, getString(R.string.charges))
            menu.add(Menu.NONE, MENU_ITEM_ADD_SAVINGS_ACCOUNT, Menu.NONE, getString(R.string.savings_account))
            menu.add(Menu.NONE, MENU_ITEM_ADD_LOAN_ACCOUNT, Menu.NONE,
                    getString(R.string.add_loan))
            menu.add(Menu.NONE, MENU_ITEM_DOCUMENTS, Menu.NONE, getString(R.string.documents))
            menu.add(Menu.NONE, MENU_ITEM_UPLOAD_SIGN, Menu.NONE, R.string.upload_sign)
            menu.add(Menu.NONE, MENU_ITEM_IDENTIFIERS, Menu.NONE, getString(R.string.identifiers))
            menu.add(Menu.NONE, MENU_ITEM_SURVEYS, Menu.NONE, getString(R.string.survey))
            menu.add(Menu.NONE, MENU_ITEM_NOTE, Menu.NONE, getString(R.string.note))
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            MENU_ITEM_DATA_TABLES -> loadClientDataTables()
            MENU_ITEM_DOCUMENTS -> loadDocuments()
            MENU_ITEM_UPLOAD_SIGN -> loadSignUpload()
            MENU_ITEM_CLIENT_CHARGES -> loadClientCharges()
            MENU_ITEM_ADD_SAVINGS_ACCOUNT -> addsavingsaccount()
            MENU_ITEM_ADD_LOAN_ACCOUNT -> addloanaccount()
            MENU_ITEM_IDENTIFIERS -> loadIdentifiers()
            MENU_ITEM_PIN_POINT -> {
                val i = Intent(activity, PinpointClientActivity::class.java)
                i.putExtra(Constants.CLIENT_ID, clientId)
                startActivity(i)
            }
            MENU_ITEM_SURVEYS -> loadSurveys()
            MENU_ITEM_NOTE -> loadNotes()
        }
        return super.onOptionsItemSelected(item)
    }

    fun captureClientImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(clientImageFile))
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(activity!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    CHECK_PERMISSIONS)
        }
    }

    fun uploadClientImage() {
        // Create intent to Open Image applications like Gallery, Google Photos
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // Start the Intent
        galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(clientImageFile))
        startActivityForResult(galleryIntent, UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE)
    }

    /**
     * A service to upload the image of the client.
     *
     * @param pngFile - PNG images supported at the moment
     */
    private fun uploadImage(pngFile: File) {
        mClientDetailsPresenter!!.uploadImage(clientId, pngFile)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mClientDetailsPresenter!!.detachView()
    }

    fun loadDocuments() {
        val documentListFragment = DocumentListFragment.newInstance(Constants.ENTITY_TYPE_CLIENTS, clientId)
        val fragmentTransaction = activity!!.supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS)
        fragmentTransaction.replace(R.id.container, documentListFragment)
        fragmentTransaction.commit()
    }

    fun loadNotes() {
        val noteFragment = NoteFragment.newInstance(Constants.ENTITY_TYPE_CLIENTS, clientId)
        val fragmentTransaction = activity!!.supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS)
        fragmentTransaction.replace(R.id.container, noteFragment)
        fragmentTransaction.commit()
    }

    fun loadClientCharges() {
        val clientChargeFragment: ClientChargeFragment = ClientChargeFragment.Companion.newInstance(clientId,
                chargesList)
        val fragmentTransaction = activity!!.supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS)
        fragmentTransaction.replace(R.id.container, clientChargeFragment)
        fragmentTransaction.commit()
    }

    fun loadIdentifiers() {
        val clientIdentifiersFragment: ClientIdentifiersFragment = ClientIdentifiersFragment.Companion.newInstance(clientId)
        val fragmentTransaction = activity!!.supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS)
        fragmentTransaction.replace(R.id.container, clientIdentifiersFragment)
        fragmentTransaction.commit()
    }

    fun loadSurveys() {
        val surveyListFragment = SurveyListFragment.newInstance(clientId)
        val fragmentTransaction = activity!!.supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS)
        fragmentTransaction.replace(R.id.container, surveyListFragment)
        fragmentTransaction.commit()
    }

    fun addsavingsaccount() {
        val savingsAccountFragment = SavingsAccountFragment.newInstance(clientId, false)
        val fragmentTransaction = activity!!.supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS)
        fragmentTransaction.replace(R.id.container, savingsAccountFragment)
        fragmentTransaction.commit()
    }

    fun addloanaccount() {
        val loanAccountFragment = LoanAccountFragment.newInstance(clientId)
        val fragmentTransaction = activity!!.supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS)
        fragmentTransaction.replace(R.id.container, loanAccountFragment)
        fragmentTransaction.commit()
    }

    fun activateClient() {
        val activateFragment = ActivateFragment.newInstance(clientId, Constants.ACTIVATE_CLIENT)
        val fragmentTransaction = activity!!.supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS)
        fragmentTransaction.replace(R.id.container, activateFragment)
        fragmentTransaction.commit()
    }

    fun loadClientDataTables() {
        val loanAccountFragment = DataTableFragment.newInstance(Constants.DATA_TABLE_NAME_CLIENT, clientId)
        val fragmentTransaction = activity!!.supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS)
        fragmentTransaction.replace(R.id.container, loanAccountFragment)
        fragmentTransaction.commit()
    }

    fun loadSignUpload() {
        val fragment = SignatureFragment()
        val bundle = Bundle()
        bundle.putInt(Constants.CLIENT_ID, clientId)
        fragment.arguments = bundle
        val fragmentTransaction = activity!!.supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS)
        fragmentTransaction.replace(R.id.container, fragment).commit()
    }

    override fun showProgressbar(show: Boolean) {
        if (show) {
            rlClient!!.visibility = View.GONE
            showMifosProgressBar()
        } else {
            rlClient!!.visibility = View.VISIBLE
            hideMifosProgressBar()
        }
    }

    override fun showClientInformation(client: Client?) {
        if (client != null) {
            setToolbarTitle(getString(R.string.client) + " - " + client.displayName)
            isClientActive = client.isActive
            activity!!.invalidateOptionsMenu()
            if (!client.isActive) {
                llBottomPanel!!.visibility = View.VISIBLE
            }
            tv_fullName!!.text = client.displayName
            tv_accountNumber!!.text = client.accountNo
            tvGroup!!.text = client.groupNames
            tv_externalId!!.text = client.externalId
            /*tvMobileNo!!.text = client.mobileNo
            if (TextUtils.isEmpty(client.accountNo)) rowAccount!!.visibility = View.GONE
            if (TextUtils.isEmpty(client.externalId)) rowExternal!!.visibility = View.GONE
            if (TextUtils.isEmpty(client.mobileNo)) rowMobileNo!!.visibility = View.GONE
            if (TextUtils.isEmpty(client.groupNames)) rowGroup!!.visibility = View.GONE*/
            try {
                val dateString = Utils.getStringOfDate(
                        client.activationDate)
                tv_activationDate!!.text = dateString
                //if (TextUtils.isEmpty(dateString)) rowActivation!!.visibility = View.GONE
            } catch (e: IndexOutOfBoundsException) {
                /*Toast.makeText(activity, getString(R.string.error_client_inactive),
                        Toast.LENGTH_SHORT).show()*/
                Toaster.show(rootView,getString(R.string.error_client_inactive),1000)
                tv_activationDate!!.text = ""
            }
            tv_office!!.text = client.officeName
           // if (TextUtils.isEmpty(client.officeName)) rowOffice!!.visibility = View.GONE
            if (client.isImagePresent) {
                loadClientProfileImage()
            } else {
                iv_clientImage!!.setImageDrawable(
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_launcher, null))
                pb_imageProgressBar!!.visibility = View.GONE
            }
            iv_clientImage!!.setOnClickListener { view ->
                val menu = PopupMenu(activity!!, view)
                menu.menuInflater.inflate(R.menu.client_image_popup, menu
                        .menu)
                if (!client.isImagePresent) {
                    menu.menu.findItem(R.id.client_image_remove).isVisible = false
                }
                menu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.client_image_upload -> uploadClientImage()
                        R.id.client_image_capture -> captureClientImage()
                        R.id.client_image_remove -> mClientDetailsPresenter!!.deleteClientImage(clientId)
                        else -> Log.e("ClientDetailsFragment", "Unrecognized " +
                                "client " +
                                "image menu item")
                    }
                    true
                }
                menu.show()
            }
            //inflateClientsAccounts();
        }
    }

    override fun showUploadImageSuccessfully(response: ResponseBody?, imagePath: String?) {
        Toaster.show(rootView, R.string.client_image_updated)
        iv_clientImage!!.setImageBitmap(BitmapFactory.decodeFile(imagePath))
    }

    override fun showUploadImageFailed(s: String?) {
        Toaster.show(rootView, s)
        loadClientProfileImage()
    }

    override fun showUploadImageProgressbar(b: Boolean) {
        if (b) {
            pb_imageProgressBar!!.visibility = View.VISIBLE
        } else {
            pb_imageProgressBar!!.visibility = View.GONE
        }
    }

    fun loadClientProfileImage() {
        pb_imageProgressBar!!.visibility = View.VISIBLE
        ImageLoaderUtils.loadImage(activity, clientId, iv_clientImage)
        pb_imageProgressBar!!.visibility = View.GONE
    }

    override fun showClientImageDeletedSuccessfully() {
        Toaster.show(rootView, "Image deleted")
        iv_clientImage!!.setImageDrawable(ContextCompat.getDrawable(activity!!, R.drawable.ic_launcher))
    }

    override fun showClientAccount(clientAccounts: ClientAccounts) {
        // Proceed only when the fragment is added to the activity.
        if (!isAdded) {
            return
        }
        tv_loan_account_count.text = clientAccounts.loanAccounts.size.toString()
        tv_saving_account_count.text = clientAccounts.nonRecurringSavingsAccounts.size.toString()
        tv_recurring_count.text  = clientAccounts.recurringSavingsAccounts.size.toString()
        if (clientAccounts.loanAccounts.size > 0) {
            iv_expandable.isClickable = true
            rvlayoutManager = LinearLayoutManager(activity)
            (rvlayoutManager as LinearLayoutManager).orientation = LinearLayoutManager.VERTICAL
            rv_loan_accounts.layoutManager = rvlayoutManager
            rv_loan_accounts.setHasFixedSize(true)
            rvAdapter = LoanAccountsListAdapter(activity!!.applicationContext, clientAccounts.loanAccounts,mListener)
            rv_loan_accounts.adapter = rvAdapter
        } else {
            iv_expandable.isClickable = false
        }
        if (clientAccounts.nonRecurringSavingsAccounts.size > 0) {
            iv_savings_expandable.isClickable = true
            rvlayoutManager = LinearLayoutManager(activity)
            (rvlayoutManager as LinearLayoutManager).orientation = LinearLayoutManager.VERTICAL
            rv_saving_accounts.layoutManager = rvlayoutManager
            rv_saving_accounts.setHasFixedSize(true)

            rvAdapter = SavingsAccountsListAdapter(activity!!.applicationContext, clientAccounts.nonRecurringSavingsAccounts,mListener)

            rv_saving_accounts.adapter = rvAdapter
        } else {
            iv_savings_expandable.isClickable = false
        }
        if (clientAccounts.recurringSavingsAccounts.size > 0) {
            iv_recurring_expandable.isClickable = true
            rvlayoutManager = LinearLayoutManager(activity)
            (rvlayoutManager as LinearLayoutManager).orientation = LinearLayoutManager.VERTICAL
            rv_recurring_accounts.layoutManager = rvlayoutManager
            rv_recurring_accounts.setHasFixedSize(true)

            rvAdapter = SavingsAccountsListAdapter(activity!!.applicationContext, clientAccounts.recurringSavingsAccounts,mListener)

            rv_recurring_accounts.adapter = rvAdapter

        } else {
            iv_recurring_expandable.isClickable = false
        }
    }

    override fun showFetchingError(s: String?) {
        //Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
        Toaster.show(view,s,Toast.LENGTH_SHORT)
    }

    interface OnFragmentInteractionListener {
        fun loadLoanAccountSummary(loanAccountNumber: Int)
        fun loadSavingsAccountSummary(savingsAccountNumber: Int, accountType: DepositType?)
    }

    companion object {
        // Intent response codes. Each response code must be a unique integer.
        private const val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2
        private const val UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE = 1
        private const val CHECK_PERMISSIONS = 1010
        const val MENU_ITEM_DATA_TABLES = 1000
        const val MENU_ITEM_PIN_POINT = 1001
        const val MENU_ITEM_CLIENT_CHARGES = 1003
        const val MENU_ITEM_ADD_SAVINGS_ACCOUNT = 1004
        const val MENU_ITEM_ADD_LOAN_ACCOUNT = 1005
        const val MENU_ITEM_DOCUMENTS = 1006
        const val MENU_ITEM_UPLOAD_SIGN = 1010
        const val MENU_ITEM_IDENTIFIERS = 1007
        const val MENU_ITEM_SURVEYS = 1008
        const val MENU_ITEM_NOTE = 1009

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param clientId Client's Id
         */
        fun newInstance(clientId: Int): ClientDetailsFragment {
            val fragment = ClientDetailsFragment()
            val args = Bundle()
            args.putInt(Constants.CLIENT_ID, clientId)
            fragment.arguments = args
            return fragment
        }
    }
}