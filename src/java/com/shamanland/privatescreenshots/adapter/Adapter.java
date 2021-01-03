/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.res.Resources
 *  android.graphics.Bitmap
 *  android.graphics.Bitmap$Config
 *  android.graphics.drawable.BitmapDrawable
 *  android.graphics.drawable.ColorDrawable
 *  android.graphics.drawable.Drawable
 *  android.support.v7.widget.CardView
 *  android.support.v7.widget.GridLayoutManager
 *  android.support.v7.widget.GridLayoutManager$SpanSizeLookup
 *  android.support.v7.widget.RecyclerView
 *  android.support.v7.widget.RecyclerView$Adapter
 *  android.support.v7.widget.RecyclerView$ViewHolder
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.View$OnClickListener
 *  android.view.View$OnLongClickListener
 *  android.view.ViewGroup
 *  android.view.ViewGroup$LayoutParams
 *  android.view.ViewGroup$MarginLayoutParams
 *  android.widget.ImageView
 *  android.widget.ImageView$ScaleType
 *  android.widget.TextView
 *  java.io.File
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.HashSet
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Set
 */
package com.shamanland.privatescreenshots.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.shamanland.analytics.Analytics;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.privatescreenshots.ad.AdManager;
import com.shamanland.privatescreenshots.utils.AppUtils;
import com.shamanland.privatescreenshots.utils.AsyncBitmapDecoder;
import com.shamanland.privatescreenshots.view.BannerView;
import com.shamanland.remoteconfig.RemoteConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Adapter
extends RecyclerView.Adapter<ItemViewHolder> {
    protected static final String LOG_TAG = "Adapter";
    protected final LazyRef<AdManager> adManager;
    protected final LazyRef<Analytics> analytics;
    protected final LazyRef<AsyncBitmapDecoder> asyncBitmapDecoder;
    protected final Client client;
    protected final Context context;
    protected final int gridSpanCount;
    protected final List<Item> items;
    protected RecyclerView recyclerView;
    protected final LazyRef<RemoteConfig> remoteConfig;
    protected final Set<File> selected;

    public Adapter(Context context, Client client, LazyRef<Analytics> lazyRef, LazyRef<AsyncBitmapDecoder> lazyRef2, LazyRef<AdManager> lazyRef3, LazyRef<RemoteConfig> lazyRef4, int n) {
        this.context = context;
        this.analytics = lazyRef;
        this.adManager = lazyRef3;
        this.remoteConfig = lazyRef4;
        this.client = client;
        this.asyncBitmapDecoder = lazyRef2;
        this.selected = new HashSet();
        this.items = new ArrayList(0);
        this.gridSpanCount = n;
    }

    private void setFilesNoAd(List<File> list) {
        this.items.clear();
        for (File file : list) {
            this.items.add((Object)new Item(file));
        }
        this.notifyDataSetChanged();
    }

    private void setFilesWithAd(List<File> list) {
        this.items.clear();
        int n = list.size();
        int n2 = (int)this.remoteConfig.get().getLong("second_ad_align", 0L);
        int n3 = 0;
        int n4 = n2 != 1 ? 0 : 1;
        boolean bl = n > 3 * this.gridSpanCount;
        int n5 = this.gridSpanCount;
        int n6 = n > n5 * 5 ? n5 * (n4 + n / n5) - 2 * (n4 + 1) : -1;
        Iterator iterator = list.iterator();
        int n7 = 0;
        while (iterator.hasNext()) {
            File file = (File)iterator.next();
            this.items.add((Object)new Item(file));
            if (!bl || ++n3 != this.gridSpanCount && n3 != n6) continue;
            List<Item> list2 = this.items;
            list2.add((Object)new Item(++n7));
        }
        this.notifyDataSetChanged();
    }

    public File getFile(int n) {
        return this.getItem((int)n).file;
    }

    public GridLayoutManager.SpanSizeLookup getGridSpanSizeLookup() {
        return new GridLayoutManager.SpanSizeLookup(){

            public int getSpanSize(int n) {
                if (Adapter.this.getItemViewType(n) == 0) {
                    return 1;
                }
                return 2;
            }
        };
    }

    public Item getItem(int n) {
        return (Item)this.items.get(n);
    }

    public int getItemCount() {
        return this.items.size();
    }

    public int getItemViewType(int n) {
        return this.getItem((int)n).adIndex;
    }

    public List<File> getSelected() {
        return new ArrayList(this.selected);
    }

    public int getSelectedCount() {
        return this.selected.size();
    }

    public boolean hasSelected() {
        return this.selected.size() > 0;
    }

    public void hideAd(int n) {
        Iterator iterator = this.items.iterator();
        int n2 = 0;
        while (iterator.hasNext()) {
            if (((Item)iterator.next()).adIndex == n) {
                iterator.remove();
                this.notifyItemRemoved(n2);
                return;
            }
            ++n2;
        }
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    public void onBindViewHolder(ItemViewHolder itemViewHolder, int n) {
        itemViewHolder.onBindViewHolder(n);
    }

    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int n) {
        View view = LayoutInflater.from((Context)this.context).inflate(2131492951, viewGroup, false);
        if (n == 0) {
            return new FileViewHolder(view);
        }
        return new AdViewHolder(view, n);
    }

    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    public void onFileAdded(File file) {
        this.items.add(0, (Object)new Item(file));
        this.notifyItemInserted(0);
        RecyclerView recyclerView = this.recyclerView;
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    public void onFileRemoved(File file) {
        int n = Item.indexOf(this.items, file);
        if (n >= 0) {
            this.items.remove(n);
            this.selected.remove((Object)file);
            this.notifyItemRemoved(n);
        }
    }

    public void onFilesRemoved(Collection<File> collection) {
        Item.removeAll(this.items, collection);
        this.selected.removeAll(collection);
        this.notifyDataSetChanged();
    }

    void onItemClicked(FileViewHolder fileViewHolder) {
        int n = fileViewHolder.getAdapterPosition();
        if (n < 0) {
            return;
        }
        this.client.onItemClicked(n);
    }

    void onItemLongClicked(FileViewHolder fileViewHolder) {
        int n = fileViewHolder.getAdapterPosition();
        if (n < 0) {
            return;
        }
        this.client.onItemLongClicked(n);
    }

    void onItemMenuClicked(ItemViewHolder itemViewHolder) {
        int n = itemViewHolder.getAdapterPosition();
        if (n < 0) {
            return;
        }
        Item item = this.getItem(n);
        if (item.file != null) {
            this.client.onFileMenuClicked(item.file, itemViewHolder.menu);
            return;
        }
        this.client.onAdMenuClicked(item.adIndex, itemViewHolder.menu);
    }

    public void selectAll() {
        int n = this.selected.size();
        for (Item item : this.items) {
            if (item.file == null) continue;
            this.selected.add((Object)item.file);
        }
        if (this.selected.size() != n) {
            this.notifyDataSetChanged();
        }
    }

    public void selectNone() {
        if (this.hasSelected()) {
            this.selected.clear();
            this.notifyDataSetChanged();
        }
    }

    public void setFiles(List<File> list) {
        if (this.adManager.get().isBannerEnabled()) {
            this.setFilesWithAd(list);
            return;
        }
        this.setFilesNoAd(list);
    }

    public void toggleSelection(int n) {
        int n2 = this.selected.size();
        Item item = this.getItem(n);
        if (this.selected.contains((Object)item.file)) {
            this.selected.remove((Object)item.file);
        } else {
            this.selected.add((Object)item.file);
        }
        int n3 = this.selected.size();
        if (n2 != 0 && n3 != 0) {
            this.notifyItemChanged(n);
            return;
        }
        this.notifyDataSetChanged();
    }

    public class AdViewHolder
    extends ItemViewHolder {
        BannerView bannerView;

        public AdViewHolder(View view, int n) {
            super(view);
            this.text.setText(2131689693);
            this.bannerView = new BannerView(this.itemView.getContext());
            this.bannerView.setAdIndex(n);
            this.itemContent.addView((View)this.bannerView, new ViewGroup.LayoutParams(-1, -1));
        }
    }

    public static interface Client {
        public File getThumb(File var1);

        public void onAdMenuClicked(int var1, View var2);

        public void onFileMenuClicked(File var1, View var2);

        public void onItemClicked(int var1);

        public void onItemLongClicked(int var1);
    }

    public class FileViewHolder
    extends ItemViewHolder
    implements AsyncBitmapDecoder.Listener {
        File expectedFile;
        final ImageView image;

        public FileViewHolder(View view) {
            super(view);
            this.image = new ImageView(view.getContext());
            this.itemContent.addView((View)this.image, new ViewGroup.LayoutParams(-1, -1));
            View.OnClickListener onClickListener = new View.OnClickListener(){

                public void onClick(View view) {
                    Adapter.this.onItemClicked(FileViewHolder.this);
                }
            };
            View.OnLongClickListener onLongClickListener = new View.OnLongClickListener(){

                public boolean onLongClick(View view) {
                    Adapter.this.onItemLongClicked(FileViewHolder.this);
                    return true;
                }
            };
            this.image.setOnLongClickListener(onLongClickListener);
            this.text.setOnLongClickListener(onLongClickListener);
            this.image.setOnClickListener(onClickListener);
            this.text.setOnClickListener(onClickListener);
        }

        @Override
        public void onBindViewHolder(int n) {
            ViewGroup.LayoutParams layoutParams;
            super.onBindViewHolder(n);
            File file = Adapter.this.getItem((int)n).file;
            if (file == null) {
                Adapter.this.analytics.get().logError("adapter_file_null");
                return;
            }
            boolean bl = Adapter.this.selected.contains((Object)file);
            this.cardView.setSelected(bl);
            this.text.setText((CharSequence)file.getName());
            File file2 = Adapter.this.client.getThumb(file);
            if (!file2.equals((Object)this.expectedFile)) {
                Adapter.this.asyncBitmapDecoder.get().cancel(this.expectedFile);
                this.expectedFile = file2;
                if (!this.setImage(AppUtils.getFromCache(this.expectedFile), false)) {
                    Adapter.this.asyncBitmapDecoder.get().decode(this.expectedFile, Bitmap.Config.RGB_565, this);
                }
            }
            int n2 = 0;
            if (bl) {
                n2 = Adapter.this.context.getResources().getDimensionPixelSize(2131165416);
            }
            if ((layoutParams = this.image.getLayoutParams()) instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams)layoutParams).setMargins(n2, n2, n2, n2);
                this.image.setLayoutParams(layoutParams);
            }
        }

        @Override
        public void onDecodeFinished(File file, Bitmap bitmap) {
            if (file.equals((Object)this.expectedFile)) {
                this.setImage(bitmap, true);
            }
            if (bitmap != null) {
                AppUtils.putToCache(file, bitmap);
            }
        }

        public boolean setImage(Bitmap bitmap, boolean bl) {
            if (bitmap != null) {
                this.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                this.image.setImageDrawable((Drawable)new BitmapDrawable(Adapter.this.context.getResources(), bitmap));
                return true;
            }
            if (bl) {
                this.image.setScaleType(ImageView.ScaleType.CENTER);
                this.image.setImageResource(2131230840);
                return false;
            }
            this.image.setScaleType(ImageView.ScaleType.CENTER);
            this.image.setImageDrawable((Drawable)new ColorDrawable(-1118482));
            return false;
        }

    }

    public static class Item {
        final int adIndex;
        final File file;

        Item(int n) {
            this.file = null;
            this.adIndex = n;
        }

        Item(File file) {
            this.file = file;
            this.adIndex = 0;
        }

        static int indexOf(List<Item> list, File file) {
            int n = list.size();
            for (int i = 0; i < n; ++i) {
                if (!file.equals((Object)((Item)list.get((int)i)).file)) continue;
                return i;
            }
            return -1;
        }

        static void removeAll(List<Item> list, Collection<File> collection) {
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                File file = ((Item)iterator.next()).file;
                if (file == null || !collection.contains((Object)file)) continue;
                iterator.remove();
            }
        }
    }

    public class ItemViewHolder
    extends RecyclerView.ViewHolder {
        final CardView cardView;
        final ViewGroup itemContent;
        final View menu;
        final TextView text;

        public ItemViewHolder(View view) {
            super(view);
            this.cardView = (CardView)view.findViewById(2131296303);
            this.itemContent = (ViewGroup)view.findViewById(2131296373);
            this.text = (TextView)view.findViewById(2131296471);
            this.menu = view.findViewById(2131296385);
            this.menu.setOnClickListener(new View.OnClickListener(){

                public void onClick(View view) {
                    Adapter.this.onItemMenuClicked(ItemViewHolder.this);
                }
            });
        }

        public void onBindViewHolder(int n) {
            View view = this.menu;
            int n2 = Adapter.this.hasSelected() ? 4 : 0;
            view.setVisibility(n2);
        }

    }

}

