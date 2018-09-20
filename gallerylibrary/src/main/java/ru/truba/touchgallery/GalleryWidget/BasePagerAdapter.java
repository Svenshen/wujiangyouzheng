package ru.truba.touchgallery.GalleryWidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;

import java.util.List;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;

import ru.truba.touchgallery.R;
import ru.truba.touchgallery.TouchView.TouchImageView;

/**
 * Class wraps URLs to adapter, then it instantiates <b>UrlTouchImageView</b>
 * objects to paging up through them.
 */
public class BasePagerAdapter extends PagerAdapter {

	protected final List<String> mResources;
	protected final Context mContext;
	protected int mCurrentPosition = -1;
	protected OnItemChangeListener mOnItemChangeListener;

	public BasePagerAdapter() {
		mResources = null;
		mContext = null;
	}

	public BasePagerAdapter(Context context, List<String> resources) {
		this.mResources = resources;
		this.mContext = context;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, final int position,
			Object object) {
		super.setPrimaryItem(container, position, object);
		if (mCurrentPosition == position)
			return;
		GalleryViewPager galleryContainer = ((GalleryViewPager) container);
		if (galleryContainer.mCurrentView != null) {
			galleryContainer.mCurrentView.resetScale();
		}
		((GalleryViewPager) container).mCurrentView = (TouchImageView) object;
		mCurrentPosition = position;
		if (mOnItemChangeListener != null)
			mOnItemChangeListener.onItemChange(mCurrentPosition);
	}

	@Override
	public void destroyItem(ViewGroup collection, int position, Object view) {
		collection.removeView((View) view);
	}

	@Override
	public Object instantiateItem(final ViewGroup container, int position) {
		final TouchImageView iv = new TouchImageView(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		iv.setScaleType(ScaleType.MATRIX);
		iv.setLayoutParams(params);
		Glide.with(container.getContext()).load(mResources.get(position))
				.asBitmap().into(new SimpleTarget<Bitmap>() {
					public void onResourceReady(Bitmap arg0,
							GlideAnimation<? super Bitmap> arg1) {
						iv.setImageBitmap(arg0);
					}

					@Override
					public void onLoadFailed(Exception e, Drawable errorDrawable) {
						iv.setImageBitmap(BitmapFactory.decodeResource(
								container.getResources(),
								R.drawable.log_no_pic));
					}
				});
		container.addView(iv);
		return iv;
	}

	@Override
	public int getCount() {
		return mResources.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void finishUpdate(ViewGroup arg0) {
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(ViewGroup arg0) {
	}

	public int getCurrentPosition() {
		return mCurrentPosition;
	}

	public void setOnItemChangeListener(OnItemChangeListener listener) {
		mOnItemChangeListener = listener;
	}

	public static interface OnItemChangeListener {
		public void onItemChange(int currentPosition);
	}
};