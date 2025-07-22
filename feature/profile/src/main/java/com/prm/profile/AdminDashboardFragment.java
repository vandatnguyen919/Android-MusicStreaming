package com.prm.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.domain.model.Song;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdminDashboardFragment extends Fragment {

    private static final String TAG = "AdminDashboardFragment";

    private AdminDashboardViewModel viewModel;
    private PendingSongsAdapter adapter;

    // UI Components
    private ImageView ivBack;
    private TextView tvPendingCount;
    private TextView tvApprovedCount;
    private RecyclerView rvPendingSongs;
    private ProgressBar progressBar;
    private LinearLayout llEmptyState;

    public static AdminDashboardFragment newInstance() {
        return new AdminDashboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        initViews(view);
        setupRecyclerView();
        setupClickListeners();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AdminDashboardViewModel.class);

        observeViewModel();
        viewModel.loadPendingSongs();
        viewModel.loadStats();
    }

    private void initViews(View view) {
        ivBack = view.findViewById(R.id.iv_back);
        tvPendingCount = view.findViewById(R.id.tv_pending_count);
        tvApprovedCount = view.findViewById(R.id.tv_approved_count);
        rvPendingSongs = view.findViewById(R.id.rv_pending_songs);
        progressBar = view.findViewById(R.id.progress_bar);
        llEmptyState = view.findViewById(R.id.ll_empty_state);
    }

    private void setupRecyclerView() {
        adapter = new PendingSongsAdapter(new ArrayList<>(), new PendingSongsAdapter.OnSongActionListener() {
            @Override
            public void onApprove(Song song) {
                viewModel.approveSong(song.getId());
            }

            @Override
            public void onDeny(Song song) {
                viewModel.denySong(song.getId());
            }
        });

        rvPendingSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPendingSongs.setAdapter(adapter);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getPendingSongs().observe(getViewLifecycleOwner(), songs -> {
            adapter.updateSongs(songs);
            llEmptyState.setVisibility(songs.isEmpty() ? View.VISIBLE : View.GONE);
            rvPendingSongs.setVisibility(songs.isEmpty() ? View.GONE : View.VISIBLE);
        });

        viewModel.getPendingCount().observe(getViewLifecycleOwner(), count -> {
            tvPendingCount.setText(String.valueOf(count));
        });

        viewModel.getApprovedCount().observe(getViewLifecycleOwner(), count -> {
            tvApprovedCount.setText(String.valueOf(count));
        });

        viewModel.getActionResult().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                // Refresh the list after successful action
                viewModel.loadPendingSongs();
                viewModel.loadStats();
            } else {
                Toast.makeText(getContext(), result.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
