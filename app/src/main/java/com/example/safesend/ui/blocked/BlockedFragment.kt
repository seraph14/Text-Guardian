package com.example.safesend.ui.blocked

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safesend.R
import com.example.safesend.Utility.SMS
import com.example.safesend.adapters.BlockedMessagesAdapter
import com.example.safesend.adapters.MessagesAdapter

class BlockedFragment : Fragment() {
    private lateinit var blockedRecyclerView: RecyclerView
    private lateinit var blockedRecycleAdapter: BlockedMessagesAdapter
    private lateinit var blockedViewModel: BlockedViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        blockedViewModel =
                ViewModelProvider(this).get(BlockedViewModel::class.java)
        blockedRecycleAdapter = BlockedMessagesAdapter()
        blockedViewModel.listenForMessage().observe(viewLifecycleOwner, Observer {
            blockedRecycleAdapter.setData(it)
        })



        setHasOptionsMenu(true)
        return  inflater.inflate(R.layout.fragment_blocked, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        blockedRecyclerView = view.findViewById(R.id.blocked_rc_view)
        blockedRecyclerView.adapter = blockedRecycleAdapter
        blockedRecyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)
    }

}