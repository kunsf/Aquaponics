package cn.jit.aquaponics.mvp.ui.activity.photos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.zxl.baselib.commom.BaseAppConst;
import com.zxl.baselib.ui.base.BaseActivity;
import com.zxl.baselib.ui.base.BasePresenter;
import com.zxl.baselib.util.ui.UIUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * @author zxl on 2018/06/30.
 *         discription:
 */
public class MulPicWithNumActivity extends BaseActivity implements ViewPager.OnPageChangeListener, PhotoViewAttacher.OnPhotoTapListener {
    // 保存图片
    private TextView tv_save_big_image;
    // 接收传过来的uri地址
    List<String> imageuri;
    // 接收穿过来当前选择的图片的数量
    int code;
    // 用于判断是头像还是文章图片 1:头像 2：文章大图
    int selet;

    // 用于管理图片的滑动
    private ViewPager very_image_viewpager;
    // 当前页数
    private int page;

    /**
     * 显示当前图片的页数
     */
    private TextView very_image_viewpager_text;
    /**
     * 用于判断是否是加载本地图片
     */
    private boolean isLocal;

    ViewPagerAdapter adapter;

    /**
     * 本应用图片的id
     */
    private int imageId;
    /**
     * 是否是本应用中的图片
     */
    private boolean isApp;

    public static void startAction(BaseActivity baseActivity, int type, int position, ArrayList<String> mUris) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseAppConst.MulPicShow.TYPE,type);
        bundle.putInt(BaseAppConst.MulPicShow.NOW_POSITION,position);
        bundle.putStringArrayList(BaseAppConst.MulPicShow.IMAGE_URIS,mUris);
        baseActivity.jumpToActivity(MulPicWithNumActivity.class,bundle);
    }

    @Override
    protected void init() {
        Bundle bundle = getIntent().getExtras();
        code = bundle.getInt(BaseAppConst.MulPicShow.TYPE); // 2显示数字 1为头像
        selet = bundle.getInt(BaseAppConst.MulPicShow.NOW_POSITION);
        isLocal = bundle.getBoolean("isLocal", false);
        imageuri = bundle.getStringArrayList(BaseAppConst.MulPicShow.IMAGE_URIS);
        /**是否是本应用中的图片*/
        isApp = bundle.getBoolean("isApp", false);
        /**本应用图片的id*/
        imageId = bundle.getInt("id", 0);
    }

    @Override
    protected int provideContentViewId() {
        return com.zxl.baselib.R.layout.activity_mul_pic_with_num;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        getView();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }


    /**
     * 保存图片至相册
     */
    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), BaseAppConst.APP_NAME);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsoluteFile())));
    }


    /**
     * Glide 获得图片缓存路径
     */
    private String getImagePath(String imgUrl) {
        String path = null;
        FutureTarget<File> future = Glide.with(MulPicWithNumActivity.this)
                .load(imgUrl)
                .downloadOnly(500, 500);
        try {
            File cacheFile = future.get();
            path = cacheFile.getAbsolutePath();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return path;
    }


    private void getView() {
        /************************* 接收控件 ***********************/
        very_image_viewpager_text = (TextView) findViewById(com.zxl.baselib.R.id.very_image_viewpager_text);
        tv_save_big_image = (TextView) findViewById(com.zxl.baselib.R.id.tv_save_big_image);
        very_image_viewpager = (ViewPager) findViewById(com.zxl.baselib.R.id.very_image_viewpager);
        tv_save_big_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.showToast(getString(com.zxl.baselib.R.string.start_download_pic));
                if (isApp) {// 本地图片
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageId);
                    if (bitmap != null) {
                        saveImageToGallery(MulPicWithNumActivity.this, bitmap);
                        UIUtils.showToast(getString(com.zxl.baselib.R.string.save_success));
                    }

                } else {// 网络图片
                    final BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 子线程获得图片路径
                            final String imagePath = getImagePath(imageuri.get(page));
                            // 主线程更新
                            MulPicWithNumActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (imagePath != null) {
                                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
                                        if (bitmap != null) {
                                            saveImageToGallery(MulPicWithNumActivity.this, bitmap);
                                            UIUtils.showToast(String.format(getString(com.zxl.baselib.R.string.pic_save_path), Environment.getExternalStorageDirectory().getAbsolutePath(), BaseAppConst.APP_NAME));
                                        }
                                    }
                                }
                            });
                        }
                    }).start();
                }
            }
        });

        /**
         * 给viewpager设置适配器
         */
        if (isApp) {
            MyPageAdapter myPageAdapter = new MyPageAdapter();
            very_image_viewpager.setAdapter(myPageAdapter);
            very_image_viewpager.setEnabled(false);
        } else {
            adapter = new ViewPagerAdapter();
            very_image_viewpager.setAdapter(adapter);
            very_image_viewpager.setCurrentItem(code);
            page = code;
            very_image_viewpager.setOnPageChangeListener(this);
            very_image_viewpager.setEnabled(false);
            // 设定当前的页数和总页数
            if (selet == 2) {
                very_image_viewpager_text.setText(String.format(getString(com.zxl.baselib.R.string.mul_pic_num_show),String.valueOf(code+1),String.valueOf(imageuri.size())));
            }
        }
    }

    /**
     * 本应用图片适配器
     */

    class MyPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getLayoutInflater().inflate(com.zxl.baselib.R.layout.viewpager_very_image, container, false);
            PhotoView zoom_image_view = (PhotoView) view.findViewById(com.zxl.baselib.R.id.zoom_image_view);
            ProgressBar spinner = (ProgressBar) view.findViewById(com.zxl.baselib.R.id.loading);
            spinner.setVisibility(View.GONE);
            if (imageId != 0) {
                zoom_image_view.setImageResource(imageId);
            }
            zoom_image_view.setOnPhotoTapListener(MulPicWithNumActivity.this);
            container.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    /**
     * ViewPager的适配器
     *
     * @author guolin
     */
    class ViewPagerAdapter extends PagerAdapter {

        LayoutInflater inflater;

        ViewPagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = inflater.inflate(com.zxl.baselib.R.layout.viewpager_very_image, container, false);
            final PhotoView zoom_image_view = (PhotoView) view.findViewById(com.zxl.baselib.R.id.zoom_image_view);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(com.zxl.baselib.R.id.loading);
            // 保存网络图片的路径
            String adapter_image_Entity = (String) getItem(position);
            //TODO
            String imageUrl;
            if (isLocal) {
                imageUrl = "file://" + adapter_image_Entity;
                tv_save_big_image.setVisibility(View.GONE);
            } else {
                imageUrl = adapter_image_Entity;
            }
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setClickable(false);
            Glide.with(MulPicWithNumActivity.this).load(imageUrl)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(com.zxl.baselib.R.mipmap.ic_error_picture))
                    .thumbnail(0.1f)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            UIUtils.showToast(getString(com.zxl.baselib.R.string.str_resource_load_exception));
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            int height = zoom_image_view.getHeight();
                            int wHeight = getWindowManager().getDefaultDisplay().getHeight();
                            if (height > wHeight) {
                                zoom_image_view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            } else {
                                zoom_image_view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            }
                            return false;
                        }
                    })
                    .into(zoom_image_view);

            zoom_image_view.setOnPhotoTapListener(MulPicWithNumActivity.this);
            container.addView(view, 0);
            return view;
        }

        @Override
        public int getCount() {
            if (imageuri == null || imageuri.size() == 0) {
                return 0;
            }
            return imageuri.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        Object getItem(int position) {
            return imageuri.get(position);
        }
    }

    /**
     * 下面是对Viewpager的监听
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    /**
     * 本方法主要监听viewpager滑动的时候的操作
     */
    @Override
    public void onPageSelected(int arg0) {
        // 每当页数发生改变时重新设定一遍当前的页数和总页数
        very_image_viewpager_text.setText(String.format(getString(com.zxl.baselib.R.string.mul_pic_num_show),String.valueOf(arg0+1),String.valueOf(imageuri.size())));
        page = arg0;
    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
        finish();
    }
}
