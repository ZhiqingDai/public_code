/**
 * 文件名: CommonViewHolder.java
 * 包名 com.xiao4r.adapter.common
 * 描述: TODO
 * Copyright: Copyright (c) 2015
 * Company:上海福城网络
 * @author Comsys-戴智青
 * @date 2015-6-17 下午2:39:51
 * @version V1.0
 */
package com.xiao4r.adapter.common;

import com.lidroid.xutils.BitmapUtils;
import com.xiao4r.R.color;
import com.xiao4r.constant.Constants;

import android.content.Context;
import android.os.Environment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 类描述: 一个公用的ViewHolder适配与所有的Adapter
 * @author Comsys-戴智青
 * @date 2015-6-17 下午2:39:51
 */
public class CommonViewHolder {
	
	private SparseArray<View> mViews;
	private int mPosition;
	private View mConvertView;
	
	private BitmapUtils bitmapUtils;
	/**
	 * 描述: 获取到item对象
	 * @return View
	 * @Exception
	 */
	public View getConvertView() {
		return mConvertView;
	}
	/**
	  * 创建一个新的实例 CommonViewHolder. 
	  * @param context
	  * @param parent
	  * @param layoutId
	  * @param position
	 */
	private CommonViewHolder(Context context, ViewGroup parent,int layoutId, int position){
		
		bitmapUtils = new BitmapUtils(context,Environment.getExternalStorageDirectory()+Constants.CACHE);
		
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
//		LayoutInflater.from(context).inflate(layoutId, parent);
//		View.inflate(context, layoutId, parent);
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent);
		mConvertView.setTag(this);
	}
	/**
	 * 描述: 获取ViewHolder的实例
	 * @return CommonViewHolder
	 * @Exception
	 */
	public static CommonViewHolder get(Context context,View convertView,ViewGroup parent,int layoutId,int position){
		if(convertView == null){
			return new CommonViewHolder(context,parent,layoutId,position);
		}else{
			CommonViewHolder holder = (CommonViewHolder) convertView.getTag();
			holder.mPosition = position;
			return holder;
		}
	}
	/**
	 * 描述: 通过view的id来获取控件
	 * @return T
	 * @Exception
	 */
	public <T extends View> T getView(int viewId){
		View view = mViews.get(viewId);
		if(view == null){
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T)view;
	}
	/**
	 * 设置TextView的值
	 * @return CommonViewHolder
	 * @Exception
	 */
	public CommonViewHolder setText(int viewId ,String text){
		TextView tv = getView(viewId);
		tv.setText(text);
		return this;
	}
	/**
	 * 设置ImageView控件的网络图片
	 * @return CommonViewHolder
	 * @Exception
	 */
	public CommonViewHolder setImageUrl(int viewId,String imageUrl){
		ImageView iv = getView(viewId);
		bitmapUtils.display(iv, imageUrl);
		return this;
	}
	/**
	 * 设置ImageView控件的本地资源文件
	 * @return CommonViewHolder
	 * @Exception
	 */
	public CommonViewHolder setImageResource(int viewId,int resourceId){
		ImageView iv = getView(viewId);
		iv.setImageResource(resourceId);
		return this;
	}
}
