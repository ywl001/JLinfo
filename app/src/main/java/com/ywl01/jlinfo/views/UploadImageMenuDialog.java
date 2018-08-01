package com.ywl01.jlinfo.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.consts.ImageType;
import com.ywl01.jlinfo.events.UploadImageEvent;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ywl01 on 2017/2/14.
 */

public class UploadImageMenuDialog extends Dialog {
    private Context context;
    public Map<String,Object> data;

    private String imageDir;
    private int id;
    private int imageType;

    public UploadImageMenuDialog(Context context) {
        super(context);
        this.context = context;
    }

    public UploadImageMenuDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_upload_image, null);
        ButterKnife.bind(this,view);
        setContentView(view);

        imageType = (int) data.get("imageType");
        id = (int) data.get("id");
        imageDir = (String) data.get("imageDir");
    }

    @OnClick(R.id.btn_photos)
    public void onSelect() {
        UploadImageEvent event = null;
        if (imageType == ImageType.images) {
            event = new UploadImageEvent(UploadImageEvent.SELECT_IMAGE_FOR_MARK);
        }else if(imageType == ImageType.phpto){
            event = new UploadImageEvent(UploadImageEvent.SELECT_IMAGE_FOR_PEOPLE);
        }
       sendEvent(event);
    }

    @OnClick(R.id.btn_camera)
    public void onCamera() {
        UploadImageEvent event = null;
        if (imageType == ImageType.images) {
            event = new UploadImageEvent(UploadImageEvent.TAKE_IMAGE_FOR_MARK);
        }else if(imageType == ImageType.phpto){
            event = new UploadImageEvent(UploadImageEvent.TAKE_IMAGE_FOR_PEOPLE);
        }
        sendEvent(event);
    }

    private void sendEvent(UploadImageEvent event) {
        event.IMAGE_DIR = imageDir;
        event.id = id;
        event.dispatch();
        dismiss();
    }
}
