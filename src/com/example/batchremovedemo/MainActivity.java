package com.example.batchremovedemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.batchremovedemo.MyAdapter.OnShowItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnShowItemClickListener {

	private ListView listView;
	private MyAdapter adapter;

	private List<ItemBean> dataList = new ArrayList<ItemBean>();//所有数据
	private List<ItemBean> selectList = new ArrayList<ItemBean>();//选中数据

	//顶部控件
	private ImageButton imageButton1 , imageButton2 , imageButton3;
	private TextView textView1 , textView2 , textView3;
	private Button btnSelect;

	private static boolean isShow; // 是否显示CheckBox标识

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listView = (ListView) findViewById(R.id.main_listview);

		imageButton1 = (ImageButton) findViewById(R.id.ib_user_top);
		imageButton2 = (ImageButton) findViewById(R.id.ib_back);
		imageButton3 = (ImageButton) findViewById(R.id.ib_delete);

		textView1 = (TextView) findViewById(R.id.tv_title);
		textView2 = (TextView) findViewById(R.id.tv_search);
		textView3 = (TextView) findViewById(R.id.tv_count);

		btnSelect = (Button) findViewById(R.id.btn_select);
		btnSelect.setText("全选");

		//模拟数据源
		for (int i=0; i<50; i++) {
			dataList.add(new ItemBean("第  " +i+ "项", false, false));
		}

		//显示listview，绑定适配器
		adapter = new MyAdapter(dataList, this);
		listView.setAdapter(adapter);
		adapter.setOnShowItemClickListener(this);

		//item长按监听
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (isShow) {
					return false;
				} else {
					isShow = true;
					for (ItemBean bean : dataList) {
						bean.setShow(true);
					}
					adapter.notifyDataSetChanged();
					showOpervate();
					listView.setLongClickable(false);
				}
				return true;
			}
		});

		//item点击监听
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (isShow) {
					ItemBean bean = dataList.get(position);
					boolean isChecked = bean.isChecked();
					if (isChecked) {
						bean.setChecked(false);
						selectList.remove(bean);
					} else {
						bean.setChecked(true);
						selectList.add(bean);
					}
					textView3.setText(String.valueOf(selectList.size()));
					adapter.notifyDataSetChanged();
				} else {
					Toast.makeText(MainActivity.this, dataList.get(position).getMsg(), Toast.LENGTH_SHORT).show();
				}
			}
		});


	}

	//item里面checkbox选中监听事件
	@Override
	public void onShowItemClick(ItemBean bean) {
		if (bean.isChecked() && !selectList.contains(bean)) {
			selectList.add(bean);
		} else if (!bean.isChecked() && selectList.contains(bean)) {
			selectList.remove(bean);
		}
		textView3.setText(String.valueOf(selectList.size()));
	}

	//顶部变化显示及顶部按钮监听事件
	private void showOpervate(){

		if(isShow){//长按状态显示
			imageButton1.setVisibility(View.GONE);
			textView1.setVisibility(View.GONE);

			imageButton2.setVisibility(View.VISIBLE);
			textView2.setVisibility(View.VISIBLE);
			imageButton3.setVisibility(View.VISIBLE);
			textView3.setVisibility(View.VISIBLE);
		}else{//长按状态取消
			imageButton1.setVisibility(View.VISIBLE);
			textView1.setVisibility(View.VISIBLE);

			imageButton2.setVisibility(View.GONE);
			textView2.setVisibility(View.GONE);
			imageButton3.setVisibility(View.GONE);
			textView3.setVisibility(View.GONE);
		}

		//全选按钮监听事件（全选，反选）
		btnSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("全选".equals(btnSelect.getText().toString())) {
					for (ItemBean bean : dataList) {
						if (!bean.isChecked()) {
							bean.setChecked(true);
							if (!selectList.contains(bean)) {
								selectList.add(bean);
							}
						}
					}
					adapter.notifyDataSetChanged();
					btnSelect.setText("反选");
					textView3.setText(String.valueOf(selectList.size()));
				} else if ("反选".equals(btnSelect.getText().toString())) {
					for (ItemBean bean : dataList) {
						bean.setChecked(false);
//						if (!selectList.contains(bean)) {
//							selectList.remove(bean);
//						}
						selectList.remove(bean);
					}
					adapter.notifyDataSetChanged();
					btnSelect.setText("全选");
					textView3.setText(String.valueOf(selectList.size()));
				}
			}
		});

		//退出按钮监听事件
		imageButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isShow) {
					selectList.clear();
					for (ItemBean bean : dataList) {
						bean.setChecked(false);
						bean.setShow(false);
					}
					adapter.notifyDataSetChanged();
					isShow = false;
					listView.setLongClickable(true);
				}
			}
		});

		//删除按钮监听事件
		imageButton3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (selectList!=null && selectList.size()>0) {
					dataList.removeAll(selectList);//清除掉已经选择的选项
					adapter.notifyDataSetChanged();
					selectList.clear();
				} else {
					Toast.makeText(MainActivity.this, "请选择条目", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	//单独获取手机Back键的按下事件，直接重写onBackPressed方法
	@Override
	public void onBackPressed() {
		if (isShow) {//在长按状态下

			selectList.clear();//初始化数据源
			for (ItemBean bean : dataList) {
				bean.setChecked(false);
				bean.setShow(false);
			}

			adapter.notifyDataSetChanged();
			isShow = false;//退出长按状态
			listView.setLongClickable(true);
		} else {
			super.onBackPressed();
		}
	}


}


