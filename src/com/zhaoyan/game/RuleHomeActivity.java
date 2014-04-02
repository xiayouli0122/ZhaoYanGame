package com.zhaoyan.game;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class RuleHomeActivity extends BaseActivity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rule_home);
		
		ImageView spyHelp = (ImageView) findViewById(R.id.iv_spy_rule);
		ImageView foolHelp = (ImageView) findViewById(R.id.iv_fool_rule);
		ImageView backBtn = (ImageView) findViewById(R.id.iv_rule_btn_back);
		backBtn.setOnClickListener(this);
		spyHelp.setOnClickListener(this);
		foolHelp.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_spy_rule:
			openActivity(SpyRuleActivity.class);
			break;
		case R.id.iv_fool_rule:
			showToast("fool rule");
			break;
		case R.id.iv_rule_btn_back:
			this.finish();
			break;

		default:
			break;
		}
	}
}
