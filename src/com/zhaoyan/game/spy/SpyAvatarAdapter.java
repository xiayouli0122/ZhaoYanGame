package com.zhaoyan.game.spy;

import java.util.List;

import com.zhaoyan.game.R;
import com.zhaoyan.game.spy.SpyConstant.Spys;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class SpyAvatarAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private List<Splayer> mList;
	
	private boolean mIsForget = false;
	
	public SpyAvatarAdapter(Context context, List<Splayer> dataList){
		mInflater = LayoutInflater.from(context);
		mList = dataList;
	}
	
	public void setForget(boolean isForget){
		mIsForget = isForget;
	}
	
	public boolean isForget(){
		return mIsForget;
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View  view = mInflater.inflate(R.layout.spy_layout_avatar, null);
		ImageView bgView = (ImageView) view.findViewById(R.id.iv_avatar_bg);
		ImageView numberView = (ImageView) view.findViewById(R.id.iv_avatar_no);
		ImageView maskView = (ImageView) view.findViewById(R.id.iv_avatar_mask);
		
		Splayer player = mList.get(position);
		int number = player.getNumber();
		if (player.isDead()) {
			number = 0;
			bgView.setImageResource(R.drawable.photo_voted);
		}
		switch (number) {
		case 1:
			numberView.setImageResource(R.drawable.spy_superscript_1);
			break;
		case 2:
			numberView.setImageResource(R.drawable.spy_superscript_2);
			break;
		case 3:
			numberView.setImageResource(R.drawable.spy_superscript_3);
			break;
		case 4:
			numberView.setImageResource(R.drawable.spy_superscript_4);
			break;
		case 5:
			numberView.setImageResource(R.drawable.spy_superscript_5);
			break;
		case 6:
			numberView.setImageResource(R.drawable.spy_superscript_6);
			break;
		case 7:
			numberView.setImageResource(R.drawable.spy_superscript_7);
			break;
		case 8:
			numberView.setImageResource(R.drawable.spy_superscript_8);
			break;
		case 9:
			numberView.setImageResource(R.drawable.spy_superscript_9);
			break;
		case 10:
			numberView.setImageResource(R.drawable.spy_superscript_10);
			break;
		case 11:
			numberView.setImageResource(R.drawable.spy_superscript_11);
			break;
		case 12:
			numberView.setImageResource(R.drawable.spy_superscript_12);
			break;
		case 13:
			numberView.setImageResource(R.drawable.spy_superscript_13);
			break;
		case 14:
			numberView.setImageResource(R.drawable.spy_superscript_14);
			break;
		case 15:
			numberView.setImageResource(R.drawable.spy_superscript_15);
			break;
		case 16:
			numberView.setImageResource(R.drawable.spy_superscript_16);
			break;
		default:
			Spys role = player.getIdentity();
			if (Spys.Blank == role) {
				numberView.setImageResource(R.drawable.blank_label);
			} else if (Spys.Spy == role) {
				numberView.setImageResource(R.drawable.spy_label);
			} else {
				numberView.setImageResource(R.drawable.civilian_label);
			}
			break;
		}
		
		maskView.setVisibility(mIsForget ? View.VISIBLE : View.INVISIBLE);
		return view;
	}

}
