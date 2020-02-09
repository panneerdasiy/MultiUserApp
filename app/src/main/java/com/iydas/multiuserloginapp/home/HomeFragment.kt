package com.iydas.multiuserloginapp.home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.iydas.multiuserloginapp.R
import com.iydas.multiuserloginapp.bottomsheet.ContactBottomSheet
import com.iydas.multiuserloginapp.bottomsheet.Mode
import com.iydas.multiuserloginapp.databinding.HomeFragmentBinding
import com.iydas.multiuserloginapp.pojo.Contact

class HomeFragment : Fragment() {

    private val TAG: String = this.javaClass.name
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: HomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        binding.rvContacts.adapter = ContactsAdapter(ContactClickListener {
            Log.d(TAG, "contact clicked $it")
            viewModel.showContactBottomSheet(it)
        })
        binding.fabAdd.setOnClickListener {
            viewModel.fabAddClicked()
        }
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.moveToLogin.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "logout $it")
            if (findNavController().currentDestination?.id == R.id.homeFragment) {
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                viewModel.finishMovingToLogin()
            }
        })
        viewModel.showBottomSheet.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                showContactBottomSheet(it)
                viewModel.dismissBottomSheet()
            }
        })
        viewModel.contactsLoaded.observe(viewLifecycleOwner, Observer {
            if (it)
                observeContacts()
        })
    }

    private fun observeContacts() {
        viewModel.contacts.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "contacts = ${it.size}")
            (binding.rvContacts.adapter as ContactsAdapter).submitList(it)
        })
    }

    private fun showContactBottomSheet(contact: Contact) {
        Log.d(TAG, "contact = $contact")
        val sheet = ContactBottomSheet()
        sheet.arguments = Bundle().apply {
            putParcelable("contact", contact)
            putParcelable("mode", if (contact.name.isNotBlank()) Mode.VIEW else Mode.SAVE)
        }
        sheet.show(childFragmentManager, ContactBottomSheet.TAG)
//        Navigation.findNavController(binding.fabAdd)
//            .navigate(R.id.action_homeFragment_to_contactBottomSheet)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.log_out -> {
                viewModel.logOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
