package com.pratham.prathamdigital.ui.fragment_content;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContentAdapter extends RecyclerView.Adapter {

    public static final int FOLDER_TYPE = 1;
    public static final int FILE_TYPE = 2;
    public static final int HEADER_TYPE = 3;

    ArrayList<Modal_ContentDetail> datalist;
    Context context;
    ContentContract.contentClick contentClick;

    public ContentAdapter(Context context, ArrayList<Modal_ContentDetail> datalist, ContentContract.contentClick contentClick) {
        this.context = context;
        this.datalist = datalist;
        this.contentClick = contentClick;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HEADER_TYPE;
        else if (datalist.get(position).getContentType().equalsIgnoreCase("folder"))
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
                return new FolderViewHolder(v);
            case FILE_TYPE:
                LayoutInflater file = LayoutInflater.from(parent.getContext());
                v = file.inflate(R.layout.item_content_file, parent, false);
                return new FileViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        Modal_ContentDetail contentDetail = datalist.get(holder.getAdapterPosition());
        if (contentDetail != null) {
            switch (holder.getItemViewType()) {
                case FOLDER_TYPE:
                    //folder type
                    FolderViewHolder folderViewHolder = (FolderViewHolder) holder;
                    if (contentDetail.isDownloaded())
                        if (contentDetail.isOnSDCard())
                            Picasso.get().load(new File(PrathamApplication.contentSDPath + "/PrathamImages/" + contentDetail.getNodeimage()))
                                    .placeholder(R.drawable.ic_app_logo_).into(folderViewHolder.folder_content_image);
                        else
                            Picasso.get().load(new File(PrathamApplication.pradigiPath + "/PrathamImages/" + contentDetail.getNodeimage()))
                                    .placeholder(R.drawable.ic_app_logo_).into(folderViewHolder.folder_content_image);
                    else
                        Picasso.get().load(contentDetail.getNodeserverimage()).placeholder(R.drawable.ic_app_logo_).into(folderViewHolder.folder_content_image);
                    folderViewHolder.content_card.setBackgroundColor(PD_Utility.getRandomColorGradient());
                    folderViewHolder.folder_title.setText(contentDetail.getNodetitle());
                    if (contentDetail.getNodedesc() == null || contentDetail.getNodedesc().isEmpty())
                        folderViewHolder.folder_content_desc.setText("No description");
                    else
                        folderViewHolder.folder_content_desc.setText(contentDetail.getNodedesc());
                    folderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            contentClick.onfolderClicked(holder.getAdapterPosition(), contentDetail);
                        }
                    });
                    break;
                case FILE_TYPE:
                    //file type
                    FileViewHolder fileViewHolder = (FileViewHolder) holder;
                    if (contentDetail.isDownloaded())
                        if (contentDetail.isOnSDCard())
                            Picasso.get().load(new File(PrathamApplication.contentSDPath + "/PrathamImages/" + contentDetail.getNodeimage()))
                                    .placeholder(R.drawable.ic_app_logo_).into(fileViewHolder.file_content_image);
                        else
                            Picasso.get().load(new File(PrathamApplication.pradigiPath + "/PrathamImages/" + contentDetail.getNodeimage()))
                                    .placeholder(R.drawable.ic_app_logo_).into(fileViewHolder.file_content_image);
                    else
                        Picasso.get().load(contentDetail.getNodeserverimage()).placeholder(R.drawable.ic_app_logo_).into(fileViewHolder.file_content_image);
                    if (fileViewHolder.rl_reveal.getVisibility() == View.VISIBLE)
                        unreveal(fileViewHolder.rl_reveal);
                    if (contentDetail.isDownloaded()) {
                        fileViewHolder.rl_download.setVisibility(View.GONE);
                        fileViewHolder.rl_download.setOnClickListener(null);
                        fileViewHolder.rl_play_content.setVisibility(View.VISIBLE);
                        if (contentDetail.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.GAME))
                            fileViewHolder.file_item_lottieview.setAnimation("gaming_pad.json");
                        else if (contentDetail.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.VIDEO))
                            fileViewHolder.file_item_lottieview.setAnimation("play_button.json");
                        else if (contentDetail.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.PDF))
                            fileViewHolder.file_item_lottieview.setAnimation("book.json");
                        fileViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                contentClick.openContent(holder.getAdapterPosition(), contentDetail);
                            }
                        });
                    } else {
                        fileViewHolder.rl_play_content.setVisibility(View.GONE);
                        fileViewHolder.rl_reveal.setVisibility(View.INVISIBLE);
                        fileViewHolder.rl_download.setVisibility(View.VISIBLE);
                        Drawable background = fileViewHolder.rl_download.getBackground();
                        if (background instanceof GradientDrawable) {
                            int color = PD_Utility.getRandomColorGradient();
                            ((GradientDrawable) background).setColor(color);
                            fileViewHolder.rl_reveal.setBackgroundColor(color);
                        }
                        fileViewHolder.rl_download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                contentClick.onDownloadClicked(holder.getAdapterPosition(), contentDetail, fileViewHolder.rl_reveal);
                            }
                        });
                        fileViewHolder.itemView.setOnClickListener(null);
                    }
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
                    if (contentDetail.isDownloaded())
                        if (contentDetail.isOnSDCard())
                            Picasso.get().load(new File(PrathamApplication.contentSDPath + "/PrathamImages/" + contentDetail.getNodeimage()))
                                    .placeholder(R.drawable.ic_app_logo_).into(folderViewHolder.folder_content_image);
                        else
                            Picasso.get().load(new File(PrathamApplication.pradigiPath + "/PrathamImages/" + contentDetail.getNodeimage()))
                                    .placeholder(R.drawable.ic_app_logo_).into(folderViewHolder.folder_content_image);
                    else
                        Picasso.get().load(contentDetail.getNodeserverimage()).placeholder(R.drawable.ic_app_logo_).into(folderViewHolder.folder_content_image);
                    folderViewHolder.content_card.setBackgroundColor(PD_Utility.getRandomColorGradient());
                    folderViewHolder.folder_title.setText(contentDetail.getNodetitle());
                    if (contentDetail.getNodedesc() == null || contentDetail.getNodedesc().isEmpty())
                        folderViewHolder.folder_content_desc.setText("No description");
                    else
                        folderViewHolder.folder_content_desc.setText(contentDetail.getNodedesc());
                    folderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            contentClick.onfolderClicked(holder.getAdapterPosition(), contentDetail);
                        }
                    });
                    break;
                case FILE_TYPE:
                    FileViewHolder fileViewHolder = (FileViewHolder) holder;
                    if (contentDetail.isDownloaded()) {
                        unreveal(fileViewHolder.rl_reveal);
                        fileViewHolder.rl_download.setVisibility(View.GONE);
                        fileViewHolder.rl_download.setOnClickListener(null);
                        fileViewHolder.rl_play_content.setVisibility(View.VISIBLE);
                        if (contentDetail.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.GAME))
                            fileViewHolder.file_item_lottieview.setAnimation("gaming_pad.json");
                        else if (contentDetail.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.VIDEO))
                            fileViewHolder.file_item_lottieview.setAnimation("play_button.json");
                        else if (contentDetail.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.PDF))
                            fileViewHolder.file_item_lottieview.setAnimation("book.json");
                        fileViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                contentClick.openContent(holder.getAdapterPosition(), contentDetail);
                            }
                        });
                    }
            }
        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public void updateList(final ArrayList<Modal_ContentDetail> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ContentDiffUtilCallback(newList, datalist));
        datalist.clear();
        this.datalist.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
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

        public FolderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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

        public FileViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
