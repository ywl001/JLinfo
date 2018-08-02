package com.ywl01.jlinfo.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.adapters.DividerItemDecoration;
import com.ywl01.jlinfo.adapters.PeopleListAdapter;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.consts.TableName;
import com.ywl01.jlinfo.events.ListEvent;
import com.ywl01.jlinfo.events.TypeEvent;
import com.ywl01.jlinfo.events.UpdatePeopleEvent;
import com.ywl01.jlinfo.events.UploadImageEvent;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.net.ProgressRequestBody;
import com.ywl01.jlinfo.net.SqlFactory;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.InsertObserver;
import com.ywl01.jlinfo.observers.UploadObserver;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.utils.ImageUtils;
import com.ywl01.jlinfo.utils.PhotoUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PeoplesActivity extends BaseActivity {

    private ArrayList<PeopleBean> peoples;

    @BindView(R.id.recycler_view)
    RecyclerView peopleListView;
    private PeopleListAdapter adapter;

    private ArrayList<ArrayList<PeopleBean>> peopless;
    private UploadImageEvent uploadPhotoEvent;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_peoples);
        ButterKnife.bind(this);
        peopless = new ArrayList<>();
        peoples = (ArrayList<PeopleBean>) CommVar.getInstance().get("peoples");
        peopless.add(peoples);

        LinearLayoutManager manager = new LinearLayoutManager(AppUtils.getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new PeopleListAdapter(peoples);
        peopleListView.setLayoutManager(manager);
        peopleListView.setAdapter(adapter);
        peopleListView.addItemDecoration(new DividerItemDecoration(AppUtils.getContext(), LinearLayoutManager.VERTICAL));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        peoples = (ArrayList<PeopleBean>) CommVar.getInstance().get("peoples");
        peopless.add(peoples);
        adapter = new PeopleListAdapter(peoples);
        peopleListView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        System.out.println("on back pressed................");
        peopless.remove(peopless.size() - 1);
        if (peopless.size() > 0) {
            peoples = peopless.get(peopless.size() - 1);
            adapter = new PeopleListAdapter(peoples);
            peopleListView.setAdapter(adapter);
        }else{
            finish();
        }
    }

    @Override
    public void onStart() {
        System.out.println("people activity on start");
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this))
             EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStop() {
        System.out.println("people activity on stop");
        super.onStop();
    }

    @Subscribe
    public void onUpdatePeople(UpdatePeopleEvent event) {
        PeopleBean p = event.people;
        int countPeople = peoples.size();
        for (int i = 0; i < countPeople; i++) {
            if (peoples.get(i).id == p.id) {
                peoples.set(i, p);
                ListEvent e = new ListEvent(ListEvent.update,i);
                e.dispatch();
                break;
            }
        }
    }

    @Subscribe
    public void uploadPhoto(UploadImageEvent event) {
        uploadPhotoEvent = event;
        if (event.type == UploadImageEvent.SELECT_IMAGE_FOR_PEOPLE)
            PhotoUtils.selectImage(this);
        else if (event.type == UploadImageEvent.TAKE_IMAGE_FOR_PEOPLE)
            PhotoUtils.takeImage(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PhotoUtils.SELECT_PHOTO) {
                // 被选中文件的Uri
                Uri uri = data.getData();
                System.out.println(uri.getPath());
                File file = new File(AppUtils.getPathByUri(uri));

                Bitmap bitmap = ImageUtils.getScaleBitmap(file.getPath());
                File tempFile = ImageUtils.saveBitmap(bitmap, "temp");
                uploadFile(tempFile);
            } else if (requestCode == PhotoUtils.TAKE_PHOTO) {
                Bitmap bm = ImageUtils.getScaleBitmap(PhotoUtils.tempFile.getPath());
                File tempFile = ImageUtils.saveBitmap(bm, "temp");
                uploadFile(tempFile);
            }
        }
    }

    private void uploadFile(File file) {
        System.out.println(file.length());
        //传递服务器端存储图片文件的目录
        String server_image_dir = uploadPhotoEvent.IMAGE_DIR;
        RequestBody fileDir = RequestBody.create(MultipartBody.FORM, server_image_dir);
        //上传文件的包装Filedata为php服务器端_FILE[fileData],这个名字和服务器的要一致，区分大小写！
        ProgressRequestBody requestFile = new ProgressRequestBody(file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("fileData", file.getName(), requestFile);

        UploadObserver uploadObserver = new UploadObserver();

        HttpMethods.getInstance().uploadImage(uploadObserver, fileDir, body);
        uploadObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                String returnData = (String) data;
                String imgUrl = returnData.substring(12);
                String[] temp = imgUrl.split("\\.");
                String thumbUrl = temp[0] + "_thumb.jpg";

                InsertObserver insertObserver = new InsertObserver();
                Map<String, String> tableData = new HashMap<String, String>();
                int id = uploadPhotoEvent.id;

                tableData.put("peopleID", id+"");
                tableData.put("photoUrl", imgUrl);
                tableData.put("thumbUrl", thumbUrl);
                tableData.put("insertUser", CommVar.UserID + "");
                tableData.put("insertTime", "now()");
                String sql = SqlFactory.insert(TableName.PEOPLE_PHOTO, tableData);
                HttpMethods.getInstance().getSqlResult(insertObserver, SqlAction.INSERT, sql);

                insertObserver.setOnNextListener(new BaseObserver.OnNextListener() {
                    @Override
                    public void onNext(Observer observer, Object data) {
                        Long returnData = (Long) data;
                        if (returnData > 0) {
                            AppUtils.showToast("上传图片成功");
                            //派发事件，让cameraInfoView刷新图片
                            TypeEvent.dispatch(TypeEvent.REFRESH_IMAGE);
                        }
                    }
                });
            }
        });
    }

}
