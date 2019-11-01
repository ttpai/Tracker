package com.ttpai.sample.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ttpai.sample.R;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     ItemListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link ItemListDialogFragment.Listener}.</p>
 */
public class ItemListDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";
    private Listener mListener;

    // TODO: Customize parameters
    public static ItemListDialogFragment newInstance(int itemCount) {
        final ItemListDialogFragment fragment = new ItemListDialogFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ItemAdapter(getArguments().getInt(ARG_ITEM_COUNT)));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (Listener) parent;
        } else {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface Listener {
        void onItemClicked(int position);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            // TODO: Customize the item layout
            super(inflater.inflate(R.layout.fragment_item_list_dialog_item, parent, false));
            text = (TextView) itemView.findViewById(R.id.text);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClicked(getAdapterPosition());
                        dismiss();
                    }
                }
            });
        }

    }

    private class ItemAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final int mItemCount;

        ItemAdapter(int itemCount) {
            mItemCount = itemCount;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return mItemCount;
        }

    }

}
