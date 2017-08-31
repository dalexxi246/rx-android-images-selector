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
import com.wh2.foss.imageselector.model.Image;
import com.wh2.foss.imageselector.model.Config;
import com.wh2.foss.imageselector.ui.viewmodels.ImageViewModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {

    private List<Image> items;
    private Config config;
    private Context context;

    private PublishSubject<ImageViewModel> imageSelected = PublishSubject.create();
    private PublishSubject<ImageViewModel> imageIgnored = PublishSubject.create();

    private CompositeDisposable subscriptions = new CompositeDisposable();

    public ImagesAdapter(List<Image> items, Config config, Context context) {
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

    public Observable<ImageViewModel> imageSelected() {
        return imageSelected.hide();
    }

    public Observable<ImageViewModel> imageIgnored() {
        return imageIgnored.hide();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Image image) {
        this.items.add(image);
        notifyDataSetChanged();
    }

    class ImagesViewHolder extends RecyclerView.ViewHolder {

        private ItemImageBinding binding;
        Disposable imageSelectedSubscription;
        Disposable imageIgnoredSubscription;

        ImagesViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bind(ImageViewModel viewModel) {
            binding.setImage(viewModel);
            binding.executePendingBindings();
            manageSubscriptions(viewModel);
        }

        private void manageSubscriptions(ImageViewModel viewModel) {
            if (imageSelectedSubscription != null && !imageSelectedSubscription.isDisposed()) {
                imageSelectedSubscription.dispose();
            }
            if (imageIgnoredSubscription != null && !imageIgnoredSubscription.isDisposed()) {
                imageIgnoredSubscription.dispose();
            }
            imageSelectedSubscription = RxView.clicks(binding.buttonSelect).subscribe(o -> imageSelected.onNext(viewModel));
            imageIgnoredSubscription = RxView.clicks(binding.buttonIgnore).subscribe(o -> imageIgnored.onNext(viewModel));
            subscriptions.add(imageSelectedSubscription);
            subscriptions.add(imageIgnoredSubscription);
        }
    }

}
