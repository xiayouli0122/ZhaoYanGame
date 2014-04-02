package com.zhaoyan.game;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SpyRuleActivity extends BaseActivity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rule_item);
		
		ImageView backBtn = (ImageView) findViewById(R.id.iv_spyrule_back);
		backBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_spyrule_back:
			this.finish();
			break;

		default:
			break;
		}
	}
}
