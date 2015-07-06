/**
 * 文件名: CommonAdapter.java
 * 包名 com.xiao4r.adapter.common
 * 描述: TODO
 * Copyright: Copyright (c) 2015
 * Company:上海福城网络
 * @author Comsys-戴智青
 * @date 2015-6-17 下午3:11:35
 * @version V1.0
 */
package com.xiao4r.adapter.common;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 类描述: 公用的适配器
 * @author Comsys-戴智青
 * @date 2015-6-17 下午3:11:35
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

	private Context context;
	
	private List<T> mDatas;
	
	private int layoutId;
	
	public CommonAdapter(Context context,List<T> datas,int layoutId){
		this.context = context;
		this.mDatas = datas;
		
		this.layoutId = layoutId;
	}
	/*
	 * <p>Title: getCount</p>
	 * <p>Description: </p>
	 * @return
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return mDatas.size();
	}

	/*
	 * <p>Title: getItem</p>
	 * <p>Description: </p>
	 * @param position
	 * @return
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	/*
	 * <p>Title: getItemId</p>
	 * <p>Description: </p>
	 * @param position
	 * @return
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * <p>Title: getView</p>
	 * <p>Description: </p>
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		CommonViewHolder holder = CommonViewHolder.get(context, convertView, null, layoutId, position);
		
		convert(holder, getItem(position));
				
		return holder.getConvertView();
	}

	/**
	 * 
	 * 描述: 用于给具体子类实现的方法，将设置布局个view的数据
	 * @return void
	 * @Exception
	 */
	public abstract void convert(CommonViewHolder holder,T t);
}
