package com.thekorovay.myportfolio.module_about

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.thekorovay.myportfolio.R
import com.thekorovay.myportfolio.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_about,
            container,
            false
        )

        setContacts()

        return binding.root
    }

    private fun setContacts() {
        binding.contactPhone.apply {
            tvContact.text = getString(R.string.contact_phone)
            ivIcon.setImageResource(R.drawable.ic_baseline_phone_128)
            root.setOnClickListener { makeTelephoneCall() }
        }
        binding.contactMail.apply {
            tvContact.text = getString(R.string.contact_email)
            ivIcon.setImageResource(R.drawable.ic_baseline_email_128)
            root.setOnClickListener { sendEmail() }
        }
        binding.contactTelegram.apply {
            tvContact.text = getString(R.string.contact_telegram)
            ivIcon.setImageResource(R.drawable.ic_telegram_black)
            root.setOnClickListener { openTelegram() }
        }
        binding.contactWhatsapp.apply {
            tvContact.text = getString(R.string.contact_whatsapp)
            ivIcon.setImageResource(R.drawable.ic_whatsapp_black)
            root.setOnClickListener { openWhatsapp() }
        }
        binding.contactGithub.apply {
            tvContact.text = getString(R.string.contact_github)
            ivIcon.setImageResource(R.drawable.ic_github_black)
            root.setOnClickListener { checkSourceCode() }
        }
    }

    private fun makeTelephoneCall() {
        val numberWithPrefix = "tel:${getTelephoneNumber()}"
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(numberWithPrefix)))
    }

    private fun getTelephoneNumber(): String {
        val rawNumber = getString(R.string.contact_phone)
        return rawNumber.filter { it != ' ' }
    }

    private fun sendEmail() {
        val email = getString(R.string.contact_email)

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
        }

        startActivityOrCopyToClipboard(emailIntent, "Email address", email)
    }

    private fun openTelegram() {
        val telegramId = getString(R.string.contact_telegram)
        val telegramUri = getString(R.string.contact_telegram_link, telegramId)

        val telegramIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(telegramUri)
        )

        startActivityOrCopyToClipboard(telegramIntent, "Telegram ID", telegramId)
    }

    private fun openWhatsapp() {
        val whatsappId = getString(R.string.contact_whatsapp)
        val whatsappUri = getString(R.string.contact_whatsapp_link, whatsappId)

        val whatsappIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(whatsappUri)
        )

        startActivityOrCopyToClipboard(whatsappIntent, "Whatsapp ID", whatsappId)
    }

    private fun checkSourceCode() {
        val nickname = getString(R.string.contact_github)
        val link = getString(R.string.contact_github_link, nickname)
        val intent = Intent(Intent.ACTION_VIEW, link.toUri())
        startActivityOrCopyToClipboard(intent, "Github Link", link)
    }

    private fun startActivityOrCopyToClipboard(someIntent: Intent, label: String, text: String) {
        try {
            startActivity(someIntent)
        } catch (exc: ActivityNotFoundException) {
            copyToClipboard(label, text)
        }
    }

    private fun copyToClipboard(label: String, text: String) {
        activity?.run {
            val clipManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipManager.setPrimaryClip(ClipData.newPlainText(label, text))

            Toast.makeText(
                requireContext(),
                getString(R.string.toast_copied_to_clipboard),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}