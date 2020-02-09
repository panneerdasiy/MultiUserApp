package com.iydas.multiuserloginapp.utils

import android.text.SpannableStringBuilder
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.iydas.multiuserloginapp.bottomsheet.BottomSheetViewModel
import com.iydas.multiuserloginapp.bottomsheet.Mode
import com.iydas.multiuserloginapp.home.ContactsAdapter
import com.iydas.multiuserloginapp.pojo.Contact

@BindingAdapter("contacts")
fun RecyclerView.setContactsList(contacts: LiveData<List<Contact>>) {
    (adapter as ContactsAdapter).submitList(contacts.value)
}


@BindingAdapter("name")
fun EditText.setMode(viewModel: BottomSheetViewModel) {
    this.isEnabled = (viewModel.mode.value == Mode.EDIT)
    if (viewModel.contact.value != null)
        text = SpannableStringBuilder(viewModel.contact.value?.name)
}

@BindingAdapter("mobile")
fun EditText.setMobile(viewModel: BottomSheetViewModel) {
    if (viewModel.contact.value != null)
        text = SpannableStringBuilder(viewModel.contact.value?.mobileNumber)
}

@BindingAdapter("edit")
fun Button.setEditMode(viewModel: BottomSheetViewModel) {
    visibility = if (viewModel.mode.value == Mode.VIEW) View.VISIBLE else View.GONE
}

@BindingAdapter("save")
fun Button.setSaveMode(viewModel: BottomSheetViewModel) {
    visibility = if (viewModel.mode.value == Mode.EDIT) View.VISIBLE else View.GONE
}

@BindingAdapter("delete")
fun Button.setDeleteMode(viewModel: BottomSheetViewModel) {
    visibility = if (viewModel.mode.value == Mode.VIEW) View.VISIBLE else View.GONE
}

