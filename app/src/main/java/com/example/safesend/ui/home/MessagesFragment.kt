package com.example.safesend.ui.home

import android.os.Bundle
import android.provider.Telephony
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safesend.R
import com.example.safesend.adapters.MessagesAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MessagesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    lateinit var recycleAdapter: MessagesAdapter
    private lateinit var fab: FloatingActionButton
    private val smsViewModel: MessagesViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        recycleAdapter = MessagesAdapter(activity?.applicationContext)
        val defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(requireContext())
        if (requireContext().packageName == defaultSmsApp) {
            smsViewModel.allInbox.observe(viewLifecycleOwner, Observer {
                recycleAdapter.setData(it)
            })
        }
        setHasOptionsMenu(true)
        return  inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rc_view)
        recyclerView.adapter = recycleAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)
        fab = view.findViewById(R.id.create_message_fab)
        fab.setOnClickListener { _ ->
            findNavController().navigate(R.id.action_nav_home_to_nav_new_message)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.about_menu, menu)
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                recycleAdapter.filter.filter(newText)
                return false
            }

        })
    }
}