package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.SuperWeChatApplication;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.Group;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/5/23 0023.
 */
public class DownloadAllGroupTask extends BaseActivity {
    private static final String TAG = DownloadAllGroupTask.class.getName();
    Context mContext;
    String username;
    String path;

    public DownloadAllGroupTask(Context mContext,String username) {
        this.username = username;
        this.mContext = mContext;
        initPath();
    }
    private void initPath() {
        try {
            path = new ApiParams()
                    .with(I.User.USER_NAME, username)
                    .getRequestUrl(I.REQUEST_DOWNLOAD_GROUPS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void execute() {
        executeRequest(new GsonRequest<Group[]>(path, Group[].class, responseDownloadAllGroupListener(), errorListener()));
    }

    private Response.Listener<Group[]> responseDownloadAllGroupListener() {
        return new Response.Listener<Group[]>() {
            @Override
            public void onResponse(Group[] response) {
                if (response != null) {
                    ArrayList<Group> contactList = SuperWeChatApplication.getInstance().getGroupList();
                    ArrayList<Group> list = Utils.array2List(response);
                    contactList.clear();
                    contactList.addAll(list);
                    mContext.sendStickyBroadcast(new Intent("update_all_group"));

                }
            }
        };
    }
}
