package com.pratham.prathamdigital.ui.fragment_content;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.elastic_view.ElasticView;
import com.pratham.prathamdigital.custom.swipe_reveal_layout.SwipeRevealLayout;
import com.pratham.prathamdigital.custom.swipe_reveal_layout.ViewBinderHelper;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContentAdapter extends RecyclerView.Adapter {

    public static final int FOLDER_TYPE = 1;
    public static final int FILE_TYPE = 2;
    public static final int HEADER_TYPE = 3;
    Context context;
    ContentContract.contentClick contentInterface;
    private AsyncListDiffer<Modal_ContentDetail> mDiffer;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public ContentAdapter(Context context, ContentContract.contentClick contentClick) {
        mDiffer = new AsyncListDiffer<Modal_ContentDetail>(this, diffcallback);
        this.context = context;
        this.contentInterface = contentClick;
    }

    private DiffUtil.ItemCallback<Modal_ContentDetail> diffcallback = new DiffUtil.ItemCallback<Modal_ContentDetail>() {
        @Override
        public boolean areItemsTheSame(@NonNull Modal_ContentDetail detail, @NonNull Modal_ContentDetail t1) {
            return Objects.equals(detail.getNodeid(), t1.getNodeid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Modal_ContentDetail detail, @NonNull Modal_ContentDetail t1) {
            int result = detail.compareTo(t1);
            if (result == 0) return true;
            return false;
        }
    };

    public void submitList(List<Modal_ContentDetail> data) {
        mDiffer.submitList(data);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HEADER_TYPE;
        else if (mDiffer.getCurrentList().get(position).getContentType().equalsIgnoreCase("folder"))
            return FOLDER_TYPE;
        else
            return FILE_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case HEADER_TYPE:
                LayoutInflater header = LayoutInflater.from(parent.getContext());
                v = header.inflate(R.layout.item_content_header, parent, false);
                return new EmptyHolder(v);
            case FOLDER_TYPE:
                LayoutInflater folder = LayoutInflater.from(parent.getContext());
                v = folder.inflate(R.layout.item_content_folder, parent, false);
                return new FolderViewHolder(v, contentInterface);
            case FILE_TYPE:
                LayoutInflater file = LayoutInflater.from(parent.getContext());
                v = file.inflate(R.layout.item_content_file, parent, false);
                return new FileViewHolder(v, contentInterface);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        Modal_ContentDetail contentDetail = mDiffer.getCurrentList().get(holder.getAdapterPosition());
        if (contentDetail != null) {
            switch (holder.getItemViewType()) {
                case FOLDER_TYPE:
                    FolderViewHolder folderViewHolder = (FolderViewHolder) holder;
                    folderViewHolder.setFolderItem(contentDetail, holder.getAdapterPosition());
                    break;
                case FILE_TYPE:
                    FileViewHolder fileViewHolder = (FileViewHolder) holder;
                    // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
                    // put an unique string id as value, can be any string which uniquely define the data
                    binderHelper.bind(fileViewHolder.file_swipe_layout, contentDetail.getNodeid());
                    fileViewHolder.setContentItem(contentDetail, holder.getAdapterPosition());
                    break;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Modal_ContentDetail contentDetail = (Modal_ContentDetail) payloads.get(0);
            switch (holder.getItemViewType()) {
                case FOLDER_TYPE:
                    //folder type
                    FolderViewHolder folderViewHolder = (FolderViewHolder) holder;
                    folderViewHolder.setFolderItem(contentDetail, holder.getAdapterPosition());
                    break;
                case FILE_TYPE:
                    FileViewHolder fileViewHolder = (FileViewHolder) holder;
                    binderHelper.bind(fileViewHolder.file_swipe_layout, contentDetail.getNodeid());
                    unreveal(fileViewHolder.rl_reveal);
                    fileViewHolder.setContentItem(contentDetail, holder.getAdapterPosition());
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public List<Modal_ContentDetail> getData() {
        return mDiffer.getCurrentList();
    }

    public void reveal(View view) {
        // previously invisible view
        try {
            int centerX = view.getWidth();
            int centerY = view.getHeight();
            int startRadius = 0;
            int endRadius = (int) Math.hypot(view.getWidth(), view.getHeight());
            Animator anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(300);
            view.setVisibility(View.VISIBLE);
            anim.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unreveal(View view) {
        // previously visible view
        try {
            int centerX = view.getWidth();
            int centerY = view.getHeight();
            int startRadius = 0;
            int endRadius = (int) Math.max(view.getWidth(), view.getHeight());
            Animator anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, endRadius, startRadius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(300);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            anim.start();
        } catch (Exception e) {
            e.printStackTrace();
            view.setVisibility(View.GONE);
        }
    }

    class EmptyHolder extends RecyclerView.ViewHolder {
        public EmptyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class FolderViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.folder_content_image)
        ImageView folder_content_image;
        @Nullable
        @BindView(R.id.folder_title)
        TextView folder_title;
        @Nullable
        @BindView(R.id.folder_content_desc)
        TextView folder_content_desc;
        @Nullable
        @BindView(R.id.content_card)
        RelativeLayout content_card;

        Modal_ContentDetail contentItem;
        ContentContract.contentClick contentClick;

        public FolderViewHolder(View itemView, final ContentContract.contentClick contentClick) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.contentClick = contentClick;
        }

        public void setFolderItem(Modal_ContentDetail contentItem, int pos) {
            this.contentItem = contentItem;
            if (contentItem.isDownloaded())
                if (contentItem.isOnSDCard())
                    Picasso.get().load(new File(PrathamApplication.contentSDPath + "/PrathamImages/" + contentItem.getNodeimage()))
                            .resize(130, 130)
                            .placeholder(R.drawable.ic_app_logo_)
                            .into(folder_content_image);
                else
                    Picasso.get().load(new File(PrathamApplication.pradigiPath + "/PrathamImages/" + contentItem.getNodeimage()))
                            .resize(130, 130)
                            .placeholder(R.drawable.ic_app_logo_)
                            .into(folder_content_image);
            else
                Picasso.get().load(contentItem.getNodeserverimage()).placeholder(R.drawable.ic_app_logo_).into(folder_content_image);
            content_card.setBackgroundColor(PD_Utility.getRandomColorGradient());
            folder_title.setText(contentItem.getNodetitle());
            if (contentItem.getNodedesc() == null || contentItem.getNodedesc().isEmpty())
                folder_content_desc.setText("No description");
            else
                folder_content_desc.setText(contentItem.getNodedesc());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contentClick.onfolderClicked(pos, contentItem);
                }
            });
        }
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.file_content_image)
        ImageView file_content_image;
        @Nullable
        @BindView(R.id.rl_reveal)
        RelativeLayout rl_reveal;
        @Nullable
        @BindView(R.id.file_item_lottieview)
        LottieAnimationView file_item_lottieview;
        @Nullable
        @BindView(R.id.rl_download)
        RelativeLayout rl_download;
        @Nullable
        @BindView(R.id.rl_play_content)
        RelativeLayout rl_play_content;
        @Nullable
        @BindView(R.id.txt_resource_title)
        TextView txt_resource_title;
        @Nullable
        @BindView(R.id.file_content_desc)
        TextView file_content_desc;
        @Nullable
        @BindView(R.id.delete_layout)
        View delete_layout;
        @Nullable
        @BindView(R.id.file_swipe_layout)
        SwipeRevealLayout file_swipe_layout;
        @Nullable
        @BindView(R.id.content_card_file)
        ElasticView content_card_file;
        Modal_ContentDetail contentItem;
        ContentContract.contentClick contentClick;

        public FileViewHolder(View view, final ContentContract.contentClick contentClick) {
            super(view);
            ButterKnife.bind(this, view);
            this.contentClick = contentClick;
        }

        public void setContentItem(Modal_ContentDetail contentItem, int pos) {
            this.contentItem = contentItem;
            if (contentItem.isDownloaded())
                if (contentItem.isOnSDCard()) {
                    file_swipe_layout.setLockDrag(true);
                    Picasso.get().load(new File(PrathamApplication.contentSDPath + "/PrathamImages/" + contentItem.getNodeimage()))
                            .resize(130, 130)
                            .placeholder(R.drawable.ic_app_logo_)
                            .into(file_content_image);
                } else {
                    file_swipe_layout.setLockDrag(true);
                    Picasso.get().load(new File(PrathamApplication.pradigiPath + "/PrathamImages/" + contentItem.getNodeimage()))
                            .resize(130, 130)
                            .placeholder(R.drawable.ic_app_logo_)
                            .into(file_content_image);
                }
            else {
                file_swipe_layout.setLockDrag(true);
                Picasso.get().load(contentItem.getNodeserverimage()).placeholder(R.drawable.ic_app_logo_).into(file_content_image);
            }
            if (rl_reveal.getVisibility() == View.VISIBLE)
                unreveal(rl_reveal);
            if (contentItem.isDownloaded()) {
                rl_download.setVisibility(View.GONE);
                rl_download.setOnClickListener(null);
                rl_play_content.setVisibility(View.VISIBLE);
                file_content_desc.setText(contentItem.getNodetitle());
                if (contentItem.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.GAME))
                    file_item_lottieview.setAnimation("gaming_pad.json");
                else if (contentItem.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.VIDEO))
                    file_item_lottieview.setAnimation("play_button.json");
                else if (contentItem.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.PDF))
                    file_item_lottieview.setAnimation("book.json");
                content_card_file.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contentClick.openContent(pos, contentItem);
                    }
                });
                delete_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        file_swipe_layout.close(true);
                        contentClick.deleteContent(pos, contentItem);
                    }
                });
            } else {
                rl_play_content.setVisibility(View.GONE);
                rl_reveal.setVisibility(View.INVISIBLE);
                rl_download.setVisibility(View.VISIBLE);
                txt_resource_title.setText(contentItem.getNodetitle());
                Drawable background = rl_download.getBackground();
                if (background instanceof GradientDrawable) {
                    int color = PD_Utility.getRandomColorGradient();
                    ((GradientDrawable) background).setColor(color);
                    rl_reveal.setBackgroundColor(color);
                }
                rl_download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contentClick.onDownloadClicked(pos, contentItem, rl_reveal);
                    }
                });
                content_card_file.setOnClickListener(null);
            }
        }
    }
}
