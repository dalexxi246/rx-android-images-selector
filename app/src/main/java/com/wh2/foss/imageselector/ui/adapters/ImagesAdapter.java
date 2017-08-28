package com.wh2.foss.imageselector.ui.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;
import com.wh2.foss.imageselector.R;
import com.wh2.foss.imageselector.databinding.ItemImageBinding;
import com.wh2.foss.imageselector.model.Company;
import com.wh2.foss.imageselector.ui.viewmodels.ImageViewModel;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>{

    private OnItemClickListener listener;
    private List<Company> items;
    private Context context;

    public ImagesAdapter(List<Company> items, Context context) {
        this.items = items;
        this.context = context;
    }

    public ImagesAdapter(Context context) {
        this.context = context;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Company> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImagesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImagesViewHolder holder, int position) {
        ImageViewModel viewModel = new ImageViewModel(context, items.get(position));
        holder.bind(viewModel);
        ItemImageBinding binding = holder.getBinding();

        RxView.clicks(binding.getRoot()).subscribe(o -> {
            if (listener != null){
                listener.onClick(viewModel);
            }
        });

        RxView.clicks(binding.buttonSelect).subscribe(o -> {
            if (listener != null){
                listener.onSelect(viewModel);
            }
        });

        RxView.clicks(binding.buttonIgnore).subscribe(o -> {
            if (listener != null){
                listener.onIgnore(viewModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ImagesViewHolder extends RecyclerView.ViewHolder {

        private ItemImageBinding binding;

        ImagesViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        ItemImageBinding getBinding() {
            return binding;
        }

        void bind(ImageViewModel viewModel){
            binding.setImage(viewModel);
            binding.executePendingBindings();
        }
    }

    public interface OnItemClickListener {
        void onClick(ImageViewModel item);
        void onSelect(ImageViewModel item);
        void onIgnore(ImageViewModel item);
    }

}
