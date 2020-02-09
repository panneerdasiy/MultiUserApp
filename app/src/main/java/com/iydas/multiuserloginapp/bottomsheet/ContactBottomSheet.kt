package com.iydas.multiuserloginapp.bottomsheet

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.iydas.multiuserloginapp.R
import com.iydas.multiuserloginapp.databinding.BottomSheetBinding
import com.iydas.multiuserloginapp.pojo.Contact

class ContactBottomSheet : BottomSheetDialogFragment() {
    companion object {
        const val TAG: String = "ContactBottomSheet"
    }

    private lateinit var binding: BottomSheetBinding
    private lateinit var viewModel: BottomSheetViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet, container, false)
        binding.btnSave.setOnClickListener {
            viewModel.saveClicked(
                binding.etName.text.toString().trim(),
                binding.etMobile.text.toString().trim()
            )
        }
        binding.btnEdit.setOnClickListener {
            viewModel.editClicked()
        }
        binding.btnDelete.setOnClickListener {
            viewModel.deleteClicked()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val contact = arguments!!.getParcelable<Contact>("contact")
        val mode = arguments!!.getParcelable<Mode>("mode")

        Log.d(TAG, "contact $contact \nmode $mode")

        viewModel = ViewModelProvider(
            this,
            BottomSheetModelFactory(contact, mode)
        ).get()
        viewModel.dismiss.observe(viewLifecycleOwner, Observer {
            if (it) {
                dismiss()
                viewModel.stopDismissing()
            }
        })
        viewModel.contact.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.etMobile.text = SpannableStringBuilder(it.mobileNumber)
                binding.etName.text = SpannableStringBuilder(it.name)
            }
        })
        viewModel.mode.observe(viewLifecycleOwner, Observer {
            when (it) {
                Mode.EDIT -> {
                    binding.btnSave.visibility = View.VISIBLE
                    binding.btnEdit.visibility = View.GONE
                    binding.btnDelete.visibility = View.VISIBLE
                    binding.etMobile.isEnabled = true
                    binding.etName.isEnabled = true
                }
                Mode.VIEW -> {
                    binding.btnSave.visibility = View.GONE
                    binding.btnEdit.visibility = View.VISIBLE
                    binding.btnDelete.visibility = View.VISIBLE
                    binding.etMobile.isEnabled = false
                    binding.etName.isEnabled = false
                }
                else -> {
                    binding.btnSave.visibility = View.VISIBLE
                    binding.btnEdit.visibility = View.GONE
                    binding.btnDelete.visibility = View.GONE
                    binding.etMobile.isEnabled = true
                    binding.etName.isEnabled = true
                }
            }
        })
    }
}