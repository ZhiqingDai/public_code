package com.xiao4r.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xiao4r.R;
import com.xiao4r.constant.APPDevKey;
/**
 * 
  * 类描述: 第三方分享工具类
  * @author Comsys-戴智青
  * @date 2015-6-15 下午5:03:31
 */
public class ThirdSharedUtil {
	/**
	 *  整个平台的Controller,负责管理整个SDK的配置、操作等处理
	 */
	private UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share");
	public static ThirdSharedUtil instance=new ThirdSharedUtil();
	private Context context;
	private ThirdSharedUtil(){
	}
	
	public static ThirdSharedUtil getInstance(){
		return instance;
	}
	
	public void init(Activity activity){
		configPlatforms(activity);
	}
	/**
	 * 添加所分享平台</br>
	 */
	public void addCustomPlatforms(Activity activity) {
//		mController.getConfig().setPlatforms(
//				SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
//				 SHARE_MEDIA.TENCENT,SHARE_MEDIA.RENREN);
		mController.getConfig().setPlatforms(
				SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA,
				SHARE_MEDIA.QQ,SHARE_MEDIA.TENCENT,SHARE_MEDIA.RENREN,SHARE_MEDIA.QZONE);
		mController.setAppWebSite(SHARE_MEDIA.RENREN, "http://www.umeng.com/social");
		mController.openShare(activity, false);
	}

	/**
	 * 配置分享平台参数
	 */
	private void configPlatforms(Activity activity) {
		context = activity;
		// 添加新浪sso授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// 添加腾讯微博SSO授权
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

		// 添加QQ、QZone平台
		addQQQZonePlatform(activity);

		// 添加微信、微信朋友圈平台
		addWXPlatform(activity);

	}

	/**
	 *  添加微信平台分享
	 * @return
	 */
	private void addWXPlatform(Activity activity) {
		// 注意：在微信授权的时候，必须传递appSecret
		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appId = APPDevKey.WEIXIN_APPID;
		String appSecret = APPDevKey.WEI_XIN_APPSECRET;
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(activity,
				appId, appSecret);
		wxHandler.addToSocialSDK();

		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(activity,
				appId, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	/**
	 *  添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
	 *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
	 *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
	 *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
	 * @return
	 */
	private void addQQQZonePlatform(Activity activity) {
		String appId = APPDevKey.QQ_APPID;
		String appKey = APPDevKey.QQ_APPKEY;
		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(
				activity, appId, appKey);
		qqSsoHandler.setTargetUrl(context.getString(R.string.app_dowload_url));
		qqSsoHandler.addToSocialSDK();

		// 添加QZone平台
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
				activity, appId, appKey);
		qZoneSsoHandler.addToSocialSDK();
	}
	/**
	 * 设置分享的内容（title,content,shareUrl,imageUrl）
	 * @return void
	 * @Exception
	 */
	public void setShareContent(String title,String content,String url,String imageUrl){
		
		// 配置SSO
		/*
		 * mController.getConfig().setSsoHandler(new SinaSsoHandler());
		 * mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		 * 
		 * QZoneSsoHandler qZoneSsoHandler = new
		 * QZoneSsoHandler(PersonalSetActivity.this, "1103555751",
		 * "CZU5R01HBibbyOgG"); qZoneSsoHandler.addToSocialSDK();
		 */
		L.i("title:"+title+",content:"+content+",url:"+url+",imageUrl:"+imageUrl);
		mController.setShareContent(content);
		if(TextUtils.isEmpty(imageUrl)){
			mController.setShareImage(new UMImage(context, R.drawable.icon_share));
		}else{
			mController.setShareImage(new UMImage(context, imageUrl));
		}


		// 微信分享
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent.setShareContent(content);
		weixinContent.setTitle(title);
		weixinContent.setTargetUrl(url);
		if(TextUtils.isEmpty(imageUrl)){
			weixinContent.setShareImage(new UMImage(context, R.drawable.icon_share));
		}else{
			weixinContent.setShareImage(new UMImage(context, imageUrl));
		}
		mController.setShareMedia(weixinContent);

		// 设置朋友圈分享的内容
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia
				.setShareContent(content);
		circleMedia.setTitle(title);
		circleMedia.setTargetUrl(url);
		if(TextUtils.isEmpty(imageUrl)){
			circleMedia.setShareImage(new UMImage(context, R.drawable.icon_share));
		}else{
			circleMedia.setShareImage(new UMImage(context, imageUrl));
		}
		mController.setShareMedia(circleMedia);

		// 新浪分享
		SinaShareContent sinaContent = new SinaShareContent();
		sinaContent.setShareContent(content);
		sinaContent.setTitle(title);
		sinaContent.setTargetUrl(url);
		if(TextUtils.isEmpty(imageUrl)){
			sinaContent.setShareImage(new UMImage(context, R.drawable.icon_share));
		}else{
			sinaContent.setShareImage(new UMImage(context, imageUrl));
		}
		mController.setShareMedia(sinaContent);		
		
		// 设置QQ空间分享内容
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent(content);
		qzone.setTargetUrl(url);
		qzone.setTitle(title);
		if(TextUtils.isEmpty(imageUrl)){
			qzone.setShareImage(new UMImage(context, R.drawable.icon_share));
		}else{
			qzone.setShareImage(new UMImage(context, imageUrl));
		}
		mController.setShareMedia(qzone);

		// QQ分享
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent(content);
		qqShareContent.setTitle(title);
		qqShareContent.setTargetUrl(url);
		if(TextUtils.isEmpty(imageUrl)){
			qqShareContent.setShareImage(new UMImage(context, R.drawable.icon_share));
		}else{
			qqShareContent.setShareImage(new UMImage(context, imageUrl));
		}
		mController.setShareMedia(qqShareContent);

		// 腾讯微博分享
		TencentWbShareContent tencent = new TencentWbShareContent();
		tencent.setShareContent(content);
		tencent.setTitle(title);
		tencent.setTargetUrl(url);
		if(TextUtils.isEmpty(imageUrl)){
			tencent.setShareImage(new UMImage(context, R.drawable.icon_share));
		}else{
			tencent.setShareImage(new UMImage(context, imageUrl));
		}
		mController.setShareMedia(tencent);

	}
	
	/**
	 * 根据不同的平台设置不同的分享内容</br>
	 */
	public void setShareContent(String title,String content,String url) {

		setShareContent(title, content, url, null);
//		// 配置SSO
//		/*
//		 * mController.getConfig().setSsoHandler(new SinaSsoHandler());
//		 * mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
//		 * 
//		 * QZoneSsoHandler qZoneSsoHandler = new
//		 * QZoneSsoHandler(PersonalSetActivity.this, "1103555751",
//		 * "CZU5R01HBibbyOgG"); qZoneSsoHandler.addToSocialSDK();
//		 */
//		L.i("title:"+title+",content:"+content+",url:"+url);
//		mController.setShareContent(content);
//		mController.setShareImage(new UMImage(context, R.drawable.icon_share));
//
//		// 微信分享
//		WeiXinShareContent weixinContent = new WeiXinShareContent();
//		weixinContent.setShareContent(content);
//		weixinContent.setTitle(title);
//		weixinContent.setTargetUrl(url);
//		weixinContent.setShareImage(new UMImage(context, R.drawable.icon_share));
//		mController.setShareMedia(weixinContent);
//
//		// 设置朋友圈分享的内容
//		CircleShareContent circleMedia = new CircleShareContent();
//		circleMedia
//				.setShareContent(content);
//		circleMedia.setTitle(title);
//		circleMedia.setTargetUrl(url);
//		circleMedia.setShareImage(new UMImage(context, R.drawable.icon_share));
//		mController.setShareMedia(circleMedia);
//
//		// 设置QQ空间分享内容
//		QZoneShareContent qzone = new QZoneShareContent();
//		qzone.setShareContent(content);
//		qzone.setTargetUrl(url);
//		qzone.setTitle(title);
//		qzone.setShareImage(new UMImage(context, R.drawable.icon_share));
//		mController.setShareMedia(qzone);
//
//		// QQ分享
//		QQShareContent qqShareContent = new QQShareContent();
//		qqShareContent.setShareContent(content);
//		qqShareContent.setTitle(title);
//		qqShareContent.setTargetUrl(url);
//		mController.setShareMedia(qqShareContent);
//
//		// 腾讯微博分享
//		TencentWbShareContent tencent = new TencentWbShareContent();
//		tencent.setShareContent(content);
//		tencent.setTitle(title);
//		tencent.setTargetUrl(url);
//		tencent.setShareImage(new UMImage(context, R.drawable.icon_share));
//		mController.setShareMedia(tencent);
//
//		// 新浪分享
//		SinaShareContent sinaContent = new SinaShareContent();
//		sinaContent.setShareContent(content);
//		tencent.setTitle(title);
//		tencent.setTargetUrl(url);
//		tencent.setShareImage(new UMImage(context, R.drawable.icon_share));
//		mController.setShareMedia(sinaContent);

	}
	
	/**
	 * 根据不同的平台设置不同的分享内容</br>
	 */
	public void setShareContent() {

		// 配置SSO
		/*
		 * mController.getConfig().setSsoHandler(new SinaSsoHandler());
		 * mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		 * 
		 * QZoneSsoHandler qZoneSsoHandler = new
		 * QZoneSsoHandler(PersonalSetActivity.this, "1103555751",
		 * "CZU5R01HBibbyOgG"); qZoneSsoHandler.addToSocialSDK();
		 */

		mController
				.setShareContent("快来下载小事儿App吧,办证缴费,查询订购,吃喝玩乐,一应俱全,我在小事儿等你哦。");
		// 微信分享
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent
				.setShareContent("快来下载小事儿App吧,办证缴费,查询订购,吃喝玩乐,一应俱全,我在小事儿等你哦。");
		weixinContent.setTitle("小事儿");
		weixinContent.setTargetUrl(context.getString(R.string.app_dowload_url));
		weixinContent.setShareImage(new UMImage(context, R.drawable.icon_share));
		mController.setShareMedia(weixinContent);

		// 设置朋友圈分享的内容
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia
				.setShareContent("快来下载小事儿App吧,办证缴费,查询订购,吃喝玩乐,一应俱全,我在小事儿等你哦。");
		circleMedia.setTitle("小事儿");
		circleMedia.setTargetUrl(context.getString(R.string.app_dowload_url));
		circleMedia.setShareImage(new UMImage(context, R.drawable.icon_share));
		mController.setShareMedia(circleMedia);

		// 设置QQ空间分享内容
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent("快来下载小事儿App吧,办证缴费,查询订购,吃喝玩乐,一应俱全,我在小事儿等你哦。");
		qzone.setTargetUrl(context.getString(R.string.app_dowload_url));
		qzone.setShareImage(new UMImage(context, R.drawable.icon_share));
		qzone.setTitle("小事儿");
		mController.setShareMedia(qzone);

		// QQ分享
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent
				.setShareContent("快来下载小事儿App吧,办证缴费,查询订购,吃喝玩乐,一应俱全,我在小事儿等你哦。");
		qqShareContent.setTitle("小事儿");
		qqShareContent.setTargetUrl(context.getString(R.string.app_dowload_url));
		qqShareContent.setShareImage(new UMImage(context, R.drawable.icon_share));
		mController.setShareMedia(qqShareContent);

		// 腾讯微博分享
		TencentWbShareContent tencent = new TencentWbShareContent();
		tencent.setShareContent("快来下载小事儿App吧,办证缴费,查询订购,吃喝玩乐,一应俱全,我在小事儿等你哦。");
		tencent.setTitle("小事儿");
		tencent.setTargetUrl(context.getString(R.string.app_dowload_url));
		tencent.setShareImage(new UMImage(context, R.drawable.icon_share));
		mController.setShareMedia(tencent);

		// 新浪分享
		SinaShareContent sinaContent = new SinaShareContent();
		sinaContent
				.setShareContent("快来下载小事儿App吧,办证缴费,查询订购,吃喝玩乐,一应俱全,我在小事儿等你哦。");
		tencent.setTitle("小事儿");
		sinaContent.setShareImage(new UMImage(context, R.drawable.icon_share));
		tencent.setTargetUrl(context.getString(R.string.app_dowload_url));
		mController.setShareMedia(sinaContent);
	}
}
