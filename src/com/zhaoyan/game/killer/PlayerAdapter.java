package com.zhaoyan.game.killer;

import java.util.ArrayList;
import java.util.List;

import com.zhaoyan.game.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayerAdapter extends BaseAdapter {
	private static final String TAG = "PlayerAdapter";

	private LayoutInflater mInflater;
	private List<KPlayer> mDataList = new ArrayList<KPlayer>();

	private boolean mShowId = false;

	public PlayerAdapter(Context context, List<KPlayer> data) {
		mDataList = data;
		mInflater = LayoutInflater.from(context);
	}

	public void setShowId(boolean show) {
		mShowId = show;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.killer_player_item, null);
		ImageView photoView = (ImageView) view
				.findViewById(R.id.iv_player_photo);
		ImageView numBgView = (ImageView) view
				.findViewById(R.id.iv_killer_num_bg);
		TextView numberView = (TextView) view
				.findViewById(R.id.tv_killer_number);
		numberView.setText(mDataList.get(position).getNumber() + "");
		TextView idView = (TextView) view.findViewById(R.id.tv_killer_identity);
		KPlayer player = mDataList.get(position);
		if (player.getHeadIcon() == null)
			photoView.setImageResource(R.drawable.default_killer_03);
		else
			photoView.setImageBitmap(player.getHeadIcon());
		boolean isDead = player.isDead();
		boolean isChecked = player.isChecked();
		switch (player.getIdentity()) {
		case Police:
			idView.setText(R.string.police);
			if (isDead) {
				numBgView.setImageResource(R.drawable.killer_number_grey);
				photoView.setImageResource(R.drawable.policeman_profile_grey);
			} else if (isChecked) {
				photoView.setImageResource(R.drawable.policeman_profile_color);
			}
			idView.setVisibility(View.VISIBLE);
			break;
		case Killer:
			idView.setText(R.string.killer);
			if (isDead) {
				numBgView.setImageResource(R.drawable.killer_number_grey);
				photoView.setImageResource(R.drawable.killer_grey);
			} else if (isChecked) {
				photoView.setImageResource(R.drawable.killer_profile_color);
			}
			idView.setVisibility(View.VISIBLE);
			break;
		case Cilivian:
			idView.setText(R.string.civilian);
			if (isDead) {
				numBgView.setImageResource(R.drawable.killer_number_grey);
				photoView.setImageResource(R.drawable.civilian_profile_grey);
			} else if (isChecked) {
				photoView.setImageResource(R.drawable.civilian_profile_color);
			}
			idView.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}

		idView.setVisibility(mShowId || isDead || isChecked ? View.VISIBLE
				: View.INVISIBLE);
		return view;
	}

}
