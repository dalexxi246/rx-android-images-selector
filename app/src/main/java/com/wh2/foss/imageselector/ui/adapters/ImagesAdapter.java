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
import com.wh2.foss.imageselector.model.Config;
import com.wh2.foss.imageselector.ui.viewmodels.ImageViewModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {

    private List<Company> items;
    private Config config;
    private Context context;

    private PublishSubject<ImageViewModel> companySelected = PublishSubject.create();
    private PublishSubject<ImageViewModel> companyIgnored = PublishSubject.create();

    private CompositeDisposable subscriptions = new CompositeDisposable();

    public ImagesAdapter(List<Company> items, Config config, Context context) {
        this.items = items;
        this.config = config;
        this.context = context;
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImagesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImagesViewHolder holder, int position) {
        ImageViewModel viewModel = new ImageViewModel(context, items.get(position), config);
        holder.bind(viewModel);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        subscriptions.dispose();
    }

    public Observable<ImageViewModel> companySelected() {
        return companySelected.hide();
    }

    public Observable<ImageViewModel> companyIgnored() {
        return companyIgnored.hide();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ImagesViewHolder extends RecyclerView.ViewHolder {

        private ItemImageBinding binding;
        Disposable companySelectedSubscription;
        Disposable companyIgnoredSubscription;

        ImagesViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bind(ImageViewModel viewModel) {
            binding.setImage(viewModel);
            binding.executePendingBindings();
            ImageViewModel.loadImage(binding.imageView, viewModel.getImageMediumUrl());
            manageSubscriptions(viewModel);
        }

        private void manageSubscriptions(ImageViewModel viewModel) {
            if (companySelectedSubscription != null && !companySelectedSubscription.isDisposed()) {
                companySelectedSubscription.dispose();
            }
            if (companyIgnoredSubscription != null && !companyIgnoredSubscription.isDisposed()) {
                companyIgnoredSubscription.dispose();
            }
            companySelectedSubscription = RxView.clicks(binding.buttonSelect).subscribe(o -> companySelected.onNext(viewModel));
            companyIgnoredSubscription = RxView.clicks(binding.buttonIgnore).subscribe(o -> companyIgnored.onNext(viewModel));
            subscriptions.add(companySelectedSubscription);
            subscriptions.add(companyIgnoredSubscription);
        }
    }

}
