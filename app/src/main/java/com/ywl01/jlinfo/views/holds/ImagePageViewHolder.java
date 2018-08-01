package com.ywl01.jlinfo.views.holds;

import android.Manifest;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.activities.BaseActivity;
import com.ywl01.jlinfo.beans.ImageBean;
import com.ywl01.jlinfo.beans.User;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.net.SqlFactory;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.UserObserver;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.utils.MPermissionUtils;
import com.ywl01.jlinfo.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;

/**
 * Created by ywl01 on 2018/2/27.
 */

public class ImagePageViewHolder extends BaseHolder<ImageBean> implements OnPhotoTapListener {
    @BindView(R.id.photo_view)
    PhotoView photoView;

    @BindView(R.id.loading)
    ProgressBar progressBar;

    @BindView(R.id.tv_update_info)
    TextView tvUpdateInfo;

    @BindView(R.id.btn_download)
    Button btnDownload;

    private ImageBean markImageBean;

    private Bitmap bitmap;

    private static String[] externalStroagePermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected View initView() {
        View view = View.inflate(AppUtils.getContext(), R.layout.page_image, null);
        ButterKnife.bind(this, view);
        photoView.setOnPhotoTapListener(this);
        return view;
    }

    @Override
    protected void refreshUI(ImageBean markImageBean) {
        this.markImageBean = markImageBean;
        ImageLoader.getInstance().displayImage(CommVar.serverImageRootUrl + markImageBean.imageUrl, photoView, new ImageLoadingListener(progressBar));
        getUpdateInfo();
    }

    private void getUpdateInfo() {
        UserObserver userObserver = new UserObserver();
        String sql = SqlFactory.selectUser(markImageBean.insertUser);
        HttpMethods.getInstance().getSqlResult(userObserver, SqlAction.SELECT, sql);
        userObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                if (data != null) {
                    User user = (User) data;
                    setText(tvUpdateInfo, user.realName + markImageBean.insertTime.substring(0, 10) + "更新");
                }
            }
        });
    }

    private void setText(TextView tv, String text) {
        if (!StringUtils.isEmpty(text)) {
            tv.setVisibility(View.VISIBLE);
            tv.setText(text);
        } else {
            tv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPhotoTap(ImageView view, float x, float y) {
        BaseActivity.currentActivity.finish();
    }

    @OnClick(R.id.btn_download)
    public void saveImage() {
        if (bitmap != null) {
            if (isHasExternalStoragePermission()) {
                AppUtils.saveImage(bitmap, "jlinfo");
            } else {
                MPermissionUtils.requestPermissionsResult(BaseActivity.currentActivity, 10, externalStroagePermissions, new MPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        saveImage();
                    }

                    @Override
                    public void onPermissionDenied() {
                        MPermissionUtils.showTipsDialog(BaseActivity.currentActivity);
                    }
                });
            }
        }
    }

    private boolean isHasExternalStoragePermission() {
        return MPermissionUtils.checkPermissions(AppUtils.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    class ImageLoadingListener extends SimpleImageLoadingListener {
        private ProgressBar progressBar;

        public ImageLoadingListener(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            String message = null;
            switch (failReason.getType()) {
                case IO_ERROR:
                    message = "下载错误";
                    break;
                case DECODING_ERROR:
                    message = "图片无法显示";
                    break;
                case NETWORK_DENIED:
                    message = "网络有问题，无法下载";
                    break;
                case OUT_OF_MEMORY:
                    message = "图片太大无法显示";
                    break;
                case UNKNOWN:
                    message = "未知的错误";
                    break;
            }
            AppUtils.showToast(message);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            progressBar.setVisibility(View.GONE);
            btnDownload.setVisibility(View.VISIBLE);
            bitmap = loadedImage;
        }
    }
}
