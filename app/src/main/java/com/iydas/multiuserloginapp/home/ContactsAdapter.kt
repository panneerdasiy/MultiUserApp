package com.iydas.multiuserloginapp.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iydas.multiuserloginapp.R
import com.iydas.multiuserloginapp.databinding.ContactItemBinding
import com.iydas.multiuserloginapp.pojo.Contact

class ContactsAdapter(val clickListener: ContactClickListener) :
    ListAdapter<Contact, ContactsAdapter.ContactViewHolder>(ContactsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ContactViewHolder private constructor(private val binding: ContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            contact: Contact,
            clickListener: ContactClickListener
        ){
            binding.contact = contact
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ContactViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ContactItemBinding>(
                    inflater,
                    R.layout.contact_item,
                    parent,
                    false
                )
                return ContactViewHolder(binding)
            }
        }
    }
}

class ContactsDiffUtil : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }
}

class ContactClickListener(val clickListener: (contact: Contact) -> Unit){
    fun onClick(contact: Contact) = clickListener(contact)
}