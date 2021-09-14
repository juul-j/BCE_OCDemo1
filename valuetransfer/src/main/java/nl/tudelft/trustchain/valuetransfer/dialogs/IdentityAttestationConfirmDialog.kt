package nl.tudelft.trustchain.valuetransfer.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html.fromHtml
import android.text.InputType
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jaredrummler.blockingdialog.BlockingDialogFragment
import nl.tudelft.ipv8.android.IPv8Android
import nl.tudelft.ipv8.attestation.schema.ID_METADATA_RANGE_18PLUS
import nl.tudelft.ipv8.attestation.schema.ID_METADATA_RANGE_UNDERAGE
import nl.tudelft.trustchain.valuetransfer.R
import nl.tudelft.trustchain.valuetransfer.ValueTransferMainActivity
import nl.tudelft.trustchain.valuetransfer.community.IdentityCommunity
import nl.tudelft.trustchain.valuetransfer.util.betweenDates
import nl.tudelft.trustchain.valuetransfer.util.getColorIDFromThemeAttribute
import nl.tudelft.trustchain.valuetransfer.util.toggleButton
import java.lang.IllegalStateException
import java.util.*

@SuppressLint("ValidFragment")
class IdentityAttestationConfirmDialog(
    private val attributeName: String,
    private val idFormat: String,
    private val parentActivity: ValueTransferMainActivity
) : BlockingDialogFragment<String>() {

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {

        @Suppress("DEPRECATION")
        return activity?.let {
            val bottomSheetDialog = BottomSheetDialog(it, R.style.BaseBottomSheetDialog)
            val view = it.layoutInflater.inflate(R.layout.dialog_identity_attestation_confirm, null)

            bottomSheetDialog.window!!.navigationBarColor = ContextCompat.getColor(parentActivity.applicationContext, getColorIDFromThemeAttribute(parentActivity, R.attr.colorPrimary))

            // Fix keyboard exposing over content of dialog
            bottomSheetDialog.behavior.skipCollapsed = true
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

            val subtitleView = view.findViewById<TextView>(R.id.tvSubTitle)
            val attributeValueView = view.findViewById<EditText>(R.id.etAttributeValue)

            attributeValueView.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

            if (idFormat == ID_METADATA_RANGE_18PLUS || idFormat == ID_METADATA_RANGE_UNDERAGE) {
                val attributeValueAutoGeneratedView = view.findViewById<TextView>(R.id.tvAttributeValueAutoGenerated)
                val identityCommunity = IPv8Android.getInstance().getOverlay<IdentityCommunity>()!!

                val now = Date()
                val dateOfBirth = identityCommunity.getIdentity()!!.content.dateOfBirth
                val years = betweenDates(dateOfBirth, now).toString()

                attributeValueView.setText(years)
                attributeValueView.isVisible = false
                attributeValueAutoGeneratedView.isVisible = true
                attributeValueAutoGeneratedView.text = "$years years (automatically fetched from identity)"
            }

            var subtitle = subtitleView.text.toString()
            subtitle = subtitle.replace("XXX", "<b>$attributeName</b>")
            subtitle = subtitle.replace("YYY", "<b>$idFormat</b>")
            subtitleView.text = fromHtml(subtitle)

            val confirmButton = view.findViewById<Button>(R.id.btnConfirmRequestedAttestation)
            toggleButton(confirmButton, attributeValueView.text.toString().isNotEmpty())

            attributeValueView.doAfterTextChanged { state ->
                toggleButton(confirmButton, state != null && state.isNotEmpty())
            }

            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()

            confirmButton.setOnClickListener {
                attributeValueView.text.toString().let { attributeValue ->
                    setResult(attributeValue, false)
                    bottomSheetDialog.dismiss()
                }
            }

            bottomSheetDialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
