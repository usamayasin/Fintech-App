package com.mifos.mifosxdroid.passcode

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.online.DashboardActivity
import com.mifos.mobile.passcode.utils.PassCodeConstants.PASSCODE_INITIAL_LOGIN
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper
import com.mifos.utils.CheckSelfPermissionAndRequest
import java.util.concurrent.Executor


class NewPassCodeActivity : AppCompatActivity() {

    private var ll_add_biometric: LinearLayout? = null
    private var count = 0
    private var passCodeString = ""
    private var passCodeFirstDigit: TextView? = null
    private var passCodeSecondDigit: TextView? = null
    private var passCodeThirdDigit: TextView? = null
    private var passCodeFourthDigit: TextView? = null
    private var biometricLabel: TextView? = null

    private var passcodePreferencesHelper: PasscodePreferencesHelper? = null
    private var isInitialScreen = false
    private var showPassCodeFlag = false
    private var submitPassocdeButton: AppCompatButton? = null
    private var executor: Executor? = null
    private var biometricPrompt: BiometricPrompt? = null
    private var promptInfo: BiometricPrompt.PromptInfo? = null

    val INTIAL_LOGIN = "initial_login"
    val PERMISSIONS_REQUEST_READ_PHONE_STATE = 2
    val PERMISSIONS_READ_PHONE_STATE_STATUS = "read_phone_status"
    val CHANGE_PASSCODE = "change_passcode"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_pass_code)

        passcodePreferencesHelper = PasscodePreferencesHelper(this)
        passCodeFirstDigit = findViewById(R.id.tv_passcode_first_digit)
        passCodeSecondDigit = findViewById(R.id.tv_passcode_second_digit)
        passCodeThirdDigit = findViewById(R.id.tv_passcode_third_digit)
        passCodeFourthDigit = findViewById(R.id.tv_passcode_fourth_digit)
        submitPassocdeButton = findViewById(R.id.btn_set_passcode)
        biometricLabel = findViewById(R.id.tv_biometric_label)
        ll_add_biometric = findViewById(R.id.ll_add_biometric)

        isInitialScreen = intent.getBooleanExtra(PASSCODE_INITIAL_LOGIN, false)

        setupPassCodeButton()
        if (!CheckSelfPermissionAndRequest.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            )
        ) {
            requestPermission()
        }
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this@NewPassCodeActivity,
            executor!!, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    MifosBaseActivity.showAlertDialogForError(
                        this@NewPassCodeActivity,
                        "Authentication Error"
                    );
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    startHomeActivity()
                    /*Toast.makeText(
                        applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT
                    ).show()*/
                    Toaster.show(window.decorView.rootView,"Authentication succeeded!",1000)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    /*Toast.makeText(
                        applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT
                    )
                        .show()*/
                    Toaster.show(window.decorView.rootView,"Authentication failed!",1000)
                    // showAlertDialogForError(this@NewPassCodeActivity, "Authentication failed")
                }
            })

        promptInfo = PromptInfo.Builder()
            .setTitle("Biometric login")
            .setSubtitle("Log in using your biometric")
            .setNegativeButtonText(" ")
            .build()
    }

    private fun setupPassCodeButton() {
        if (isInitialScreen) {
            submitPassocdeButton!!.text = resources.getString(R.string.passcode_setup)
        } else if (intent.getBooleanExtra(CHANGE_PASSCODE, false)) {
            submitPassocdeButton!!.text = "Change Passcode"
            ll_add_biometric!!.visibility = View.GONE
        } else {
            submitPassocdeButton!!.text = resources.getString(R.string.submit)
            biometricLabel!!.text = resources.getString(R.string.biometric)
        }
    }

    private fun requestPermission() {
        CheckSelfPermissionAndRequest.requestPermission(
            this,
            Manifest.permission.READ_PHONE_STATE,
            PERMISSIONS_REQUEST_READ_PHONE_STATE,
            resources.getString(
                R.string.dialog_message_phone_state_permission_denied_prompt
            ),
            resources.getString(R.string.dialog_message_phone_state_permission_never_ask_again),
            PERMISSIONS_READ_PHONE_STATE_STATUS
        )
    }

    private fun isPassCodeLengthCorrect(): Boolean {
        if (passCodeString.length == 4) {
            return true
        }
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Alert")
            .setContentText("Invalid PassCode!")
            .setConfirmText("Ok")
            .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
            .show()
        return false
    }

    @Throws(Exception::class)
    fun savePassCode(view: View?) {
        if (isPassCodeLengthCorrect()) {
            if (isInitialScreen) {
                try {
                    passcodePreferencesHelper!!.savePassCode(AESEncryption.encrypt(passCodeString))
                    startHomeActivity()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (intent.getBooleanExtra(CHANGE_PASSCODE, false)) {
                try {
                    showChangePasscodeDialog()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val savedPasscode = passcodePreferencesHelper!!.passCode
                if (AESEncryption.decrypt(savedPasscode).equals(passCodeString)) {
                    startHomeActivity()
                } else {
                   /* Toast.makeText(
                        this,
                        resources.getString(R.string.incorrect_passcode),
                        Toast.LENGTH_SHORT
                    ).show()*/
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Alert")
                        .setContentText(resources.getString(R.string.incorrect_passcode))
                        .setConfirmText("Ok")
                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                        .show()

                    //showAlertDialogForError(this, resources.getString(R.string.incorrect_passcode))
                }
            }
        }
    }

    fun changePassCodeVisibility(view: View?) {
        if (!showPassCodeFlag) {
            passCodeFirstDigit!!.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            passCodeSecondDigit!!.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            passCodeThirdDigit!!.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            passCodeFourthDigit!!.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            showPassCodeFlag = true
        } else {
            passCodeFirstDigit!!.inputType = InputType.TYPE_CLASS_TEXT
            passCodeSecondDigit!!.inputType = InputType.TYPE_CLASS_TEXT
            passCodeThirdDigit!!.inputType = InputType.TYPE_CLASS_TEXT
            passCodeFourthDigit!!.inputType = InputType.TYPE_CLASS_TEXT
            showPassCodeFlag = false
        }
    }

    fun clearPassCode(view: View?) {
        count = 0
        passCodeFirstDigit!!.text = ""
        passCodeSecondDigit!!.text = ""
        passCodeThirdDigit!!.text = ""
        passCodeFourthDigit!!.text = ""
        passCodeString = ""
    }

    override fun onBackPressed() {
        if (isInitialScreen || intent.getBooleanExtra(CHANGE_PASSCODE, false)) {
            super.onBackPressed()
        }
    }

    fun onKeypadBtnClick(view: View) {
        val textView: TextView = findViewById(view.id)
        val inputPassCodeDigit = textView.text.toString()
        count++
        setPassCode(inputPassCodeDigit)
    }

    fun setPassCode(passCode: String) {
        if (count < 5) {
            passCodeString = passCodeString + passCode
            when (count) {
                1 -> {
                    passCodeFirstDigit!!.text = passCode
                }
                2 -> {
                    passCodeSecondDigit!!.text = passCode
                }
                3 -> {
                    passCodeThirdDigit!!.text = passCode
                }
                4 -> {
                    passCodeFourthDigit!!.text = passCode
                }
                else -> {
                    clearPassCode(View(this))
                }
            }
        }
    }


    fun startHomeActivity() {
        startActivity(Intent(this@NewPassCodeActivity, DashboardActivity::class.java))
        finish()
    }

    fun startBiometricActivity(view: View?) {
        biometricPrompt!!.authenticate(promptInfo!!)
    }

    private fun showChangePasscodeDialog() {
        MaterialDialog.Builder().init(this@NewPassCodeActivity)
            .setCancelable(false)
            .setMessage("Are you sure to change the passcode?")
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, which ->
                    try {
                        passcodePreferencesHelper!!.savePassCode(
                            AESEncryption.encrypt(
                                passCodeString
                            )
                        )
                        /*Toast.makeText(
                            this@NewPassCodeActivity,
                            "passcode change succesfully",
                            Toast.LENGTH_SHORT
                        ).show()*/
                        Toaster.show(window.decorView.rootView,"passcode change succesfully",1000)
                        clearPassCode(View(this@NewPassCodeActivity))
                        onBackPressed()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
            .createMaterialDialog()
            .show()
    }
}