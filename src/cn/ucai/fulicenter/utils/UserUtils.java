package cn.ucai.fulicenter.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ucai.fulicenter.Constant;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.applib.controller.HXSDKHelper;
import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.Contact;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.data.RequestManager;
import cn.ucai.fulicenter.domain.EMUser;

import com.android.volley.toolbox.NetworkImageView;
import com.easemob.util.HanziToPinyin;
import com.squareup.picasso.Picasso;

public class UserUtils {
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     * @param username
     * @return
     */
    public static EMUser getUserInfo(String username){
        EMUser user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getContactList().get(username);
        if(user == null){
            user = new EMUser(username);
        }
            
        if(user != null){
            //demo没有这些数据，临时填充
        	if(TextUtils.isEmpty(user.getNick()))
        		user.setNick(username);
        }
        return user;
    }



	public static Contact getUserBeanInfo(String username) {
		Contact contact= FuliCenterApplication.getInstance().getUserList().get(username);
		return  contact;
	}

	public static String getAvatarPath(String username) {
		if (username==null||username.isEmpty())return null;
		return I.REQUEST_DOWNLOAD_AVATAR_USER + username;
	}

	/**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	EMUser user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        }else{
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }

	public static void setUserBeanAvatar(String username, NetworkImageView imageView) {
		Contact contact = getUserBeanInfo(username);
		if (contact != null && contact.getMContactCname() != null) {
			setUserAvatar(getAvatarPath(username),imageView);
		}
	}





	public static void setUserBeanAvatar(User user, NetworkImageView imageView) {
		if (user != null && user.getMUserName() != null) {
			setUserAvatar(getAvatarPath(user.getMUserName()),imageView);
		}
	}

	public static void setUserAvatar(String url, NetworkImageView imageView) {
		Log.e("error", "url=" + url);
		if (url == null && url.isEmpty())return;
		imageView.setDefaultImageResId(R.drawable.default_avatar);
		imageView.setImageUrl(url, RequestManager.getImageLoader());
		imageView.setErrorImageResId(R.drawable.default_avatar);
	}

	/**
     * 设置当前用户头像
     */
	public static void setCurrentUserAvatar(Context context, ImageView imageView) {
		EMUser user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
		if (user != null && user.getAvatar() != null) {
			Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
		} else {
			Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
		}
	}
	public static void setCurrentUserBeanAvatar( NetworkImageView imageView) {
		User user = FuliCenterApplication.getInstance().getUser();
		if (user != null) {
			setUserAvatar(getAvatarPath(user.getMUserName()),imageView);
		}
	}
    
    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
    	EMUser user = getUserInfo(username);
    	if(user != null){
    		textView.setText(user.getNick());
    	}else{
    		textView.setText(username);
    	}
    }

	public static void setUserBeanNick(String username, TextView textView) {
		Contact contact = getUserBeanInfo(username);
		if (contact != null) {
			if (contact.getMUserNick() != null) {
				textView.setText(contact.getMUserNick());
			} else if (contact.getMUserName() != null) {
				textView.setText(contact.getMUserName());
			}
		} else {
			textView.setText(username);
		}
	}
	public static void setUserBeanNick(User user, TextView textView) {
		if (user != null) {
			if (user.getMUserNick() != null) {
				textView.setText(user.getMUserNick());
			} else if (user.getMUserName() != null) {
				textView.setText(user.getMUserName());
			}
		}
	}

	/**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(TextView textView){
    	EMUser user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
    	if(textView != null){
    		textView.setText(user.getNick());
    	}
    }
	/**
	 * 设置当前用户昵称
	 */
	public static void setCurrentUserBeanNick(TextView textView){
		User user = FuliCenterApplication.getInstance().getUser();
		Log.e("error", "user" + user);
		if(textView != null&&user!=null&&user.getMUserNick()!=null){
			textView.setText(user.getMUserNick());
		}
	}
    
    /**
     * 保存或更新某个用户
     */
	public static void saveUserInfo(EMUser newUser) {
		if (newUser == null || newUser.getUsername() == null) {
			return;
		}
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveContact(newUser);
	}

	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 *
	 * @param username
	 * @param user
	 */
	public static void setUserHearder(String username, Contact user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getMUserNick())) {
			headerName = user.getMUserNick();
		} else {
			headerName = user.getMContactCname();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)||username.equals(Constant.GROUP_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.trim().substring(0, 1)).get(0).target.substring(0, 1)
					.toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}

	public static String getPinYinFromHanZi(String hanzi) {
		String pinyin = "";
		for (int i = 0; i <hanzi.length() ; i++) {
			String s = hanzi.substring(i, i + 1);
			pinyin = pinyin + HanziToPinyin.getInstance().get(s).get(0).target.toLowerCase();
		}
		return pinyin;
	}

}