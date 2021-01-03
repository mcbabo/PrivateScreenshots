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
 *  android.support.v4.view.PagerAdapter
 *  android.view.View
 *  android.view.View$OnClickListener
 *  android.view.ViewGroup
 *  android.view.ViewGroup$LayoutParams
 *  java.io.File
 *  java.lang.Class
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Runtime
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Deque
 *  java.util.HashSet
 *  java.util.LinkedList
 *  java.util.List
 *  java.util.Objects
 *  java.util.Set
 */
package com.shamanland.privatescreenshots.viewer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.github.chrisbanes.photoview.PhotoView;
import com.shamanland.common.utils.LazyRef;
import com.shamanland.privatescreenshots.utils.AppUtils;
import com.shamanland.privatescreenshots.utils.AsyncBitmapDecoder;
import java.io.File;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PageAdapter
extends PagerAdapter {
    protected static final String LOG_TAG = "PageAdapter";
    protected final Set<ItemHolder> activeHolders;
    protected final LazyRef<AsyncBitmapDecoder> asyncBitmapDecoder;
    protected final Deque<ItemHolder> cachedHolders;
    protected final Bitmap.Config config;
    protected final List<Item> items;
    protected View.OnClickListener onClickListener;

    public PageAdapter(LazyRef<AsyncBitmapDecoder> lazyRef) {
        this.asyncBitmapDecoder = lazyRef;
        this.items = new ArrayList(2);
        this.cachedHolders = new LinkedList();
        this.activeHolders = new HashSet();
        Bitmap.Config config = Runtime.getRuntime().maxMemory() < 160000000L ? Bitmap.Config.RGB_565 : Bitmap.Config.ARGB_8888;
        this.config = config;
    }

    public void destroyItem(ViewGroup viewGroup, int n, Object object) {
        ItemHolder itemHolder = (ItemHolder)object;
        itemHolder.setImage(null, false);
        viewGroup.removeView((View)itemHolder.getView());
        this.cachedHolders.push((Object)itemHolder);
        this.activeHolders.remove((Object)itemHolder);
    }

    public int getCount() {
        return this.items.size();
    }

    public Item getItem(int n) {
        return (Item)this.items.get(n);
    }

    public int getItemPosition(Object object) {
        int n = this.items.indexOf((Object)((ItemHolder)object).getItem());
        if (n < 0) {
            return -2;
        }
        return n;
    }

    public Object instantiateItem(ViewGroup viewGroup, int n) {
        ItemHolder itemHolder;
        if (this.cachedHolders.isEmpty()) {
            itemHolder = new ItemHolder();
            itemHolder.init(viewGroup);
        } else {
            itemHolder = (ItemHolder)this.cachedHolders.pop();
        }
        itemHolder.bind((Item)this.items.get(n));
        viewGroup.addView((View)itemHolder.getView(), new ViewGroup.LayoutParams(-1, -1));
        this.activeHolders.add((Object)itemHolder);
        return itemHolder;
    }

    public boolean isViewFromObject(View view, Object object) {
        return ((ItemHolder)object).getView() == view;
    }

    public void setFiles(List<File> list, List<File> list2) {
        this.items.clear();
        int n = Math.min((int)list.size(), (int)list2.size());
        for (int i = 0; i < n; ++i) {
            this.items.add((Object)new Item((File)list.get(i), (File)list2.get(i)));
        }
        this.notifyDataSetChanged();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setPrimaryItem(ViewGroup viewGroup, int n, Object object) {
        for (ItemHolder itemHolder : this.activeHolders) {
            if (itemHolder == object) {
                itemHolder.onSelected();
                continue;
            }
            itemHolder.onDeselected();
        }
    }

    static class Item {
        final File file;
        final File thumb;

        Item(File file, File file2) {
            this.file = file;
            this.thumb = file2;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object != null && this.getClass() == object.getClass()) {
                Item item = (Item)object;
                return Objects.equals((Object)this.file, (Object)item.file);
            }
            return false;
        }

        public int hashCode() {
            Object[] arrobject = new Object[]{this.file};
            return Objects.hash((Object[])arrobject);
        }
    }

    class ItemHolder
    implements AsyncBitmapDecoder.Listener {
        File active;
        Item item;
        PhotoView view;

        ItemHolder() {
        }

        public void bind(Item item) {
            if (!item.equals(this.item)) {
                this.setImage(null, false);
                if (this.item != null) {
                    PageAdapter.this.asyncBitmapDecoder.get().cancel(this.item.file);
                    PageAdapter.this.asyncBitmapDecoder.get().cancel(this.item.thumb);
                }
                this.item = item;
                this.decode(this.item.thumb, Bitmap.Config.RGB_565);
            }
        }

        public void decode(File file, Bitmap.Config config) {
            if (!file.equals((Object)this.active)) {
                this.active = file;
                Bitmap bitmap = AppUtils.getFromCache(this.active);
                if (bitmap != null) {
                    this.setImage(bitmap, true);
                    return;
                }
                PageAdapter.this.asyncBitmapDecoder.get().decode(this.active, config, this);
            }
        }

        public Item getItem() {
            return this.item;
        }

        public PhotoView getView() {
            return this.view;
        }

        public void init(ViewGroup viewGroup) {
            this.view = new PhotoView(viewGroup.getContext());
            this.view.setAllowParentInterceptOnEdge(true);
            this.view.setUseMediumScale(false);
            this.view.setOnClickListener(new View.OnClickListener(){

                public void onClick(View view) {
                    if (PageAdapter.this.onClickListener != null) {
                        PageAdapter.this.onClickListener.onClick(view);
                    }
                }
            });
        }

        @Override
        public void onDecodeFinished(File file, Bitmap bitmap) {
            if (file.equals((Object)this.active)) {
                Item item;
                if (bitmap != null && (item = this.item) != null && file.equals((Object)item.thumb)) {
                    AppUtils.putToCache(file, bitmap);
                }
                this.setImage(bitmap, true);
            }
        }

        public void onDeselected() {
            Item item = this.item;
            if (item != null) {
                this.decode(item.thumb, Bitmap.Config.RGB_565);
            }
        }

        public void onSelected() {
            Item item = this.item;
            if (item != null) {
                this.decode(item.file, PageAdapter.this.config);
            }
        }

        public void setImage(Bitmap bitmap, boolean bl) {
            if (bitmap != null) {
                PhotoView photoView = this.view;
                photoView.setImageDrawable((Drawable)new BitmapDrawable(photoView.getResources(), bitmap));
                return;
            }
            if (bl) {
                this.view.setImageResource(2131230840);
                return;
            }
            this.view.setImageDrawable((Drawable)new ColorDrawable(-16777216));
        }

    }

}

