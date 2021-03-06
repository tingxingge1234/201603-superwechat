package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.activity.BaseActivity;
import cn.ucai.superwechat.bean.Group;
import cn.ucai.superwechat.bean.Member;
import cn.ucai.superwechat.data.ApiParams;
import cn.ucai.superwechat.data.GsonRequest;
import cn.ucai.superwechat.utils.Utils;

/**
 * Created by Administrator on 2016/5/31 0031.
 */
public class DownloadGroupMemberTask extends BaseActivity{
    private static final String TAG = DownloadGroupMemberTask.class.getName();
    Context mContext;
    String hxid;
    String path;

    public DownloadGroupMemberTask(Context mContext,String hxid) {
        this.hxid = hxid;
        this.mContext = mContext;
        initPath();
    }
    private void initPath() {
        try {
            path = new ApiParams()
                    .with(I.Member.GROUP_HX_ID, hxid)
                    .getRequestUrl(I.REQUEST_ADD_GROUP_MEMBER_BY_USERNAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void execute() {
        executeRequest(new GsonRequest<Member[]>(path, Member[].class, responseDownloadAllGroupListener(), errorListener()));
    }

    private Response.Listener<Member[]> responseDownloadAllGroupListener() {
        return new Response.Listener<Member[]>() {
            @Override
            public void onResponse(Member[] members) {
                if (members != null) {
                    HashMap<String, ArrayList<Member>> groupMembers = SuperWeChatApplication.getInstance().getGroupMembers();
                    ArrayList<Member> list = Utils.array2List(members);
                    ArrayList<Member> memberArrayList = groupMembers.get(hxid);
                    if (memberArrayList != null) {
                        memberArrayList.clear();
                        memberArrayList.addAll(list);
                    } else {
                        groupMembers.put(hxid, list);
                    }
                    mContext.sendStickyBroadcast(new Intent("update_member_group"));

                }
            }
        };
    }

}
