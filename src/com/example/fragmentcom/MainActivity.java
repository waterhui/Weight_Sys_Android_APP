package com.example.fragmentcom;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.utils.ConstWeight;
import com.utils.DbManager;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;

import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends FragmentActivity implements OnClickListener{

	private ChartFragment chartFragment;
	private ListFragment listFragment;
	private MemberFragment memberFragment;
	
	private View chartLayout;
	private View listLayout;
	private View memberLayout;
	
	private TextView chartText;
	private TextView listText;
	private TextView memberText;
	
	private ViewPager mViewPager;
	
    private List<Fragment> mFragmentList = new ArrayList<Fragment>(); 
    private List<ConstWeight> mWeightList = new ArrayList<ConstWeight>();
    private FragmentAdapter mFragmentAdapter;
    
	/**
	 * 用于对Fragment进行管理
	 */
    private FragmentManager fragmentManager;
    
    private JSONArray jsonArray_weg;
    private JSONArray jsonArray_user;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        InitViews();
        InitFragment();
        setTabSelection(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

   /** 
     * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。 
     */  

   private void InitViews() {
   		chartLayout = findViewById(R.id.chart_info);
   		listLayout = findViewById(R.id.list_info);
   		memberLayout = findViewById(R.id.member_info);
   		
   		chartText = (TextView) findViewById(R.id.chart_text);
   		listText = (TextView) findViewById(R.id.list_text);
   		memberText = (TextView) findViewById(R.id.member_text);
   		
   		mViewPager = (ViewPager) findViewById(R.id.view_pager);
   		
   		chartLayout.setOnClickListener(this);
   		listLayout.setOnClickListener(this);
   		memberLayout.setOnClickListener(this);
   		
   		Intent intent = getIntent();
   		String weight = intent.getExtras().getString("weight");
   		String user = intent.getExtras().getString("user"); 
   		if (weight.equals("wth") && user.equals("wth")) {
   			weight = "[ ]";
   			user = "[ ]";
   		}
   		try {
			jsonArray_weg = new JSONArray(weight);
			jsonArray_user = new JSONArray(user);
		} catch (JSONException e) {
			e.printStackTrace();
		}
   }

   private void InitFragment() {
	   chartFragment = new ChartFragment();
	   listFragment = new ListFragment();
	   memberFragment = new MemberFragment();
	   mFragmentList.add(chartFragment);
	   mFragmentList.add(listFragment);
	   mFragmentList.add(memberFragment);

	   if (jsonArray_weg.length() != 0) {
		   UpdateDbWeightInfo();
	   } else {
		   //读取数据库中的体重
		   readDbWeightInfo();
	   }
	   chartFragment.setJsonArray(jsonArray_weg);
	   chartFragment.setList(mWeightList);
	   listFragment.setJsonArray(jsonArray_weg);
	   listFragment.setList(mWeightList);
	   memberFragment.setJsonArray(jsonArray_user);
	   
	   fragmentManager = this.getSupportFragmentManager();
       mFragmentAdapter = new FragmentAdapter(fragmentManager, mFragmentList);
       mViewPager.setAdapter(mFragmentAdapter);
       mViewPager.setOffscreenPageLimit(3);
       mViewPager.setCurrentItem(0);
       
       mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int pos) {
			setTabSelection(pos);
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}
	});
   }
   
   private void UpdateDbWeightInfo() {
	   DbManager.delWeight();
	   String str[] = new String[5];
	   for (int row = 0; row < jsonArray_weg.length(); row++) {
		   ConstWeight weight = new ConstWeight();
		   try {
			str[0] = jsonArray_weg.getJSONObject(row).getString("_id");
			str[1] = jsonArray_weg.getJSONObject(row).getString("weight");
			str[2] = jsonArray_weg.getJSONObject(row).getString("bmi");
			str[3] = jsonArray_weg.getJSONObject(row).getString("status");
			str[4] = jsonArray_weg.getJSONObject(row).getString("time");
			
			weight.set_id(str[0]);
			weight.setWeight(str[1]);
			weight.setBmi(str[2]);
			weight.setStatus(str[3]);
			weight.setTime(str[4]);
			
			String sql_add_weight = String.format("insert into %s values('%s', '%s', '%s', '%s','%s')", 
					ConstWeight.TABLE_NAME, str[0], str[1], str[2],
					str[3], str[4]);
			DbManager.add(sql_add_weight);
			mWeightList.add(weight);
			
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		   
	   }
   }
   
   private void readDbWeightInfo() {
	   DbManager.RawQueryWeight(mWeightList);
   }
   
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onClick(View v){
		
		switch (v.getId()) {
		case R.id.chart_info:
			setTabSelection(0);
			break;
		case R.id.list_info:
			setTabSelection(1);
			break;
		case R.id.member_info:
			setTabSelection(2);
			break;
		default:
			break;
		}
		
	}
	
	/** 
     * 根据传入的index参数来设置选中的tab页。 
     *  
     * @param index 
     *            每个tab页对应的下标。0表示消息，1表示联系人，2表示动态，3表示设置。 
     */
	private void setTabSelection(int index) {
		// 每次选中之前先清楚掉上次的选中状态  
        clearSelection();  
        // 开启一个Fragment事务  
        
        switch (index) {
        case 0:
        	//myLayout.setBackgroundColor(Color.WHITE);
        	chartText.setTextColor(Color.WHITE);
        	chartLayout.setBackgroundColor(Color.parseColor("#32CCC2"));
        	mViewPager.setCurrentItem(0,false);
        	break;
        case 1:
        	//myLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        	listText.setTextColor(Color.WHITE);
        	listLayout.setBackgroundColor(Color.parseColor("#32CCC2"));
        	mViewPager.setCurrentItem(1,false);
        	break;
        case 2:
        default:
        	//myLayout.setBackgroundColor(Color.parseColor("#EBEBEB"));
        	memberText.setTextColor(Color.WHITE);
        	memberLayout.setBackgroundColor(Color.parseColor("#32CCC2"));
        	mViewPager.setCurrentItem(2,false);
        	break;
        }
	}
	
	 /** 
     * 清除掉所有的选中状态。 
     */  
	private void clearSelection() {
		chartText.setTextColor(Color.parseColor("#999999"));
		listText.setTextColor(Color.parseColor("#999999"));
		memberText.setTextColor(Color.parseColor("#999999"));
		chartLayout.setBackgroundColor(Color.parseColor("#FCFCFC"));
		listLayout.setBackgroundColor(Color.parseColor("#FCFCFC"));
		memberLayout.setBackgroundColor(Color.parseColor("#FCFCFC"));
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){ 
			//竖屏
			//设置chart_layout中的线性布局为垂直居中
			LinearLayout layout = 
					(LinearLayout) chartFragment.getView().findViewById(R.id.chart_linear_layout);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(layout.getLayoutParams()); 
			params.gravity = Gravity.CENTER_VERTICAL;
			layout.setLayoutParams(params);
	    }  
	    if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){  
	    	//横屏
	    	//设置chart_layout中的线性布局为水平居中
	    	LinearLayout layout = 
					(LinearLayout) chartFragment.getView().findViewById(R.id.chart_linear_layout);
	    	FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(layout.getLayoutParams()); 
			 params.gravity = Gravity.CENTER_HORIZONTAL;
			 layout.setLayoutParams(params);
	    }  
	}

	
}
