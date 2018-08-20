package com.ywl01.jlinfo.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.ProgressBar;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.consts.PeopleFlag;
import com.ywl01.jlinfo.PhpFunction;
import com.ywl01.jlinfo.views.adapters.DividerItemDecoration;
import com.ywl01.jlinfo.views.adapters.PeopleListAdapter;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.CommVar;
import com.ywl01.jlinfo.consts.TableName;
import com.ywl01.jlinfo.events.ListEvent;
import com.ywl01.jlinfo.events.TypeEvent;
import com.ywl01.jlinfo.events.UpdatePeopleEvent;
import com.ywl01.jlinfo.events.UploadImageEvent;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.net.ProgressRequestBody;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.IntObserver;
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

public class PeoplesActivity extends BaseActivity implements Filter.FilterListener {

    private ArrayList<PeopleBean> peoples;

    @BindView(R.id.recycler_view)
    RecyclerView peopleListView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private PeopleListAdapter adapter;
    private UploadImageEvent  uploadPhotoEvent;
    private int               peopleFlag;
    private String            hostName;

    @Override
    protected void initView() {
        System.out.println("peopleActivity initView");
        setContentView(R.layout.activity_peoples);
        ButterKnife.bind(this);
        peoples = (ArrayList<PeopleBean>) CommVar.getInstance().get("peoples");
        peopleFlag = peoples.get(0).peopleFlag;
        hostName = (String) CommVar.getInstance().get("hostName");

        LinearLayoutManager manager = new LinearLayoutManager(AppUtils.getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new PeopleListAdapter(peoples);
        if (peopleFlag == PeopleFlag.FROM_FAMILY) {
            adapter.getFilter().filter(null, this);
        } else {
            adapter.getFilter().filter("0", this);
        }

        peopleListView.setLayoutManager(manager);
        peopleListView.setAdapter(adapter);
        peopleListView.addItemDecoration(new DividerItemDecoration(AppUtils.getContext(), LinearLayoutManager.VERTICAL));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.people, menu);
        if (peopleFlag == PeopleFlag.FROM_HOME || peopleFlag == PeopleFlag.FROM_FAMILY) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.now_people:
                adapter.getFilter().filter("0", this);
                break;
            case R.id.leave_people:
                adapter.getFilter().filter("1", this);
                break;
            case R.id.all_people:
                adapter.getFilter().filter(null, this);
                break;
        }
        return true;
    }

    @Override
    public void onStart() {
        System.out.println("people activity on start");
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TypeEvent.dispatch(TypeEvent.RESET_SWIPEITEM_STATE);
    }

    @Override
    public void onStop() {
        System.out.println("people activity on stop");
        super.onStop();
    }

    @Subscribe
    public void showOrHideProgressBar(TypeEvent event) {
        if (event.type == TypeEvent.SHOW_PROGRESS_BAR) {
            progressBar.setVisibility(View.VISIBLE);
        } else if (event.type == TypeEvent.HIDE_PROGRESS_BAR) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onUpdatePeople(UpdatePeopleEvent event) {
        PeopleBean p = event.people;
        int countPeople = peoples.size();
        for (int i = 0; i < countPeople; i++) {
            if (peoples.get(i).id == p.id) {
                peoples.set(i, p);
                adapter.notifyItemChanged(i);
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
                File tempFile = ImageUtils.saveBitmapToFile(this, bitmap, "temp", "uploadImage");
                uploadFile(tempFile);
            } else if (requestCode == PhotoUtils.TAKE_PHOTO) {
                Bitmap bm = ImageUtils.getScaleBitmap(PhotoUtils.tempFile.getPath());
                File tempFile = ImageUtils.saveBitmapToFile(this, bm, "temp", "uploadImage");
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

                Map<String, String> tableData = new HashMap<String, String>();
                int id = uploadPhotoEvent.id;

                tableData.put("peopleID", id + "");
                tableData.put("photoUrl", imgUrl);
                tableData.put("thumbUrl", thumbUrl);
                tableData.put("insertUser", CommVar.loginUser.id + "");
                tableData.put("insertTime", "now()");

                IntObserver insertObserver = new IntObserver();
                PhpFunction.insert(insertObserver,TableName.PEOPLE_PHOTO,tableData);
                insertObserver.setOnNextListener(new BaseObserver.OnNextListener() {
                    @Override
                    public void onNext(Observer observer, Object data) {
                        int returnData = (int) data;
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

    @Override
    public void onFilterComplete(int i) {
        System.out.println("filter complete:-----" + i);
        if (peopleFlag == PeopleFlag.FROM_MARK) {
            setTitle(hostName + "有" + i + "个工作人员");
        } else if (peopleFlag == PeopleFlag.FROM_BUILDING) {
            setTitle(hostName + "有" + i + "个人");
        } else if (peopleFlag == PeopleFlag.FROM_HOUSE || peopleFlag == PeopleFlag.FROM_HOME) {
            setTitle(hostName + "家有" + i + "个人");
        } else if (peopleFlag == PeopleFlag.FROM_SEARCH) {
            setTitle("查询到共有" + i + "个人");
        } else if (peopleFlag == PeopleFlag.FROM_FAMILY) {
            PeopleBean p = peoples.get(0);
            setTitle(p.name + "家所有人员信息");
        }
    }
}
