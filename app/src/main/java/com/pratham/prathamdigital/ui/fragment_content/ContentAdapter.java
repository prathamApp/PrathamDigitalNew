package com.pratham.prathamdigital.ui.fragment_content;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContentAdapter extends RecyclerView.Adapter {

    public static final int FOLDER_TYPE = 1;
    public static final int FILE_TYPE = 2;
    public static final int HEADER_TYPE = 3;

    ArrayList<Modal_ContentDetail> datalist = new ArrayList<>();
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
                    Picasso.get().load(contentDetail.getNodeserverimage()).into(folderViewHolder.folder_content_image);
                    folderViewHolder.folder_content_desc.setText(contentDetail.getNodetitle());
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
                    Picasso.get().load(contentDetail.getNodeserverimage()).into(fileViewHolder.file_content_image);
                    fileViewHolder.file_content_desc.setText(contentDetail.getNodetitle());
                    if (contentDetail.getResourcetype().equalsIgnoreCase("Game"))
                        fileViewHolder.file_item_lottieview.setAnimation("gaming_pad.json");
                    else if (contentDetail.getResourcetype().equalsIgnoreCase("Video"))
                        fileViewHolder.file_item_lottieview.setAnimation("play_button.json");
                    else if (contentDetail.getResourcetype().equalsIgnoreCase("Pdf"))
                        fileViewHolder.file_item_lottieview.setAnimation("book.json");
                    if (contentDetail.isDownloaded()) {
                        fileViewHolder.txt_download.setVisibility(View.GONE);
                        fileViewHolder.txt_download.setOnClickListener(null);
                        fileViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                contentClick.openContent(holder.getAdapterPosition(), contentDetail);
                            }
                        });
                    } else {
                        fileViewHolder.txt_download.setVisibility(View.VISIBLE);
                        fileViewHolder.txt_download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                contentClick.onDownloadClicked(holder.getAdapterPosition(), contentDetail);
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
            Modal_ContentDetail content = (Modal_ContentDetail) payloads.get(0);
            switch (holder.getItemViewType()) {
                case FOLDER_TYPE:
                    //folder type
                    FolderViewHolder folderViewHolder = (FolderViewHolder) holder;
                    Picasso.get().load(content.getNodeserverimage()).into(folderViewHolder.folder_content_image);
                    folderViewHolder.folder_content_desc.setText(content.getNodetitle());
                    folderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            contentClick.onfolderClicked(holder.getAdapterPosition(), content);
                        }
                    });
                    break;
                case FILE_TYPE:
                    //file type
                    FileViewHolder fileViewHolder = (FileViewHolder) holder;
                    Picasso.get().load(content.getNodeserverimage()).into(fileViewHolder.file_content_image);
                    fileViewHolder.file_content_desc.setText(content.getNodetitle());
                    if (content.getResourcetype().equalsIgnoreCase("Game"))
                        fileViewHolder.file_item_lottieview.setAnimation("gaming_pad.json");
                    else if (content.getResourcetype().equalsIgnoreCase("Video"))
                        fileViewHolder.file_item_lottieview.setAnimation("play_button.json");
                    else if (content.getResourcetype().equalsIgnoreCase("Pdf"))
                        fileViewHolder.file_item_lottieview.setAnimation("book.json");
                    if (content.isDownloaded()) {
                        fileViewHolder.txt_download.setVisibility(View.GONE);
                        fileViewHolder.txt_download.setOnClickListener(null);
                        fileViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                contentClick.openContent(holder.getAdapterPosition(), content);
                            }
                        });
                    } else {
                        fileViewHolder.txt_download.setVisibility(View.VISIBLE);
                        fileViewHolder.txt_download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                contentClick.onDownloadClicked(holder.getAdapterPosition(), content);
                            }
                        });
                        fileViewHolder.itemView.setOnClickListener(null);
                    }
                    break;
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
        @BindView(R.id.folder_content_desc)
        TextView folder_content_desc;

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
        @BindView(R.id.file_content_desc)
        TextView file_content_desc;
        @Nullable
        @BindView(R.id.file_item_lottieview)
        LottieAnimationView file_item_lottieview;
        @Nullable
        @BindView(R.id.iv_delete)
        ImageView iv_delete;
        @Nullable
        @BindView(R.id.txt_download)
        TextView txt_download;

        public FileViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
