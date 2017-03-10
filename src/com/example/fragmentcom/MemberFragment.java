package com.example.fragmentcom;

import org.json.JSONArray;
import org.json.JSONException;

import com.utils.ConstUserInfo;
import com.utils.DbManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class MemberFragment extends Fragment {
	
	private JSONArray jsonArray;
	private TextView tv_name;
	private TextView tv_height;
	private TextView tv_sex;
	private TextView tv_ideal_weight;
	private TextView tv_exit;
	
	private String user_info[] = new String[5];
	private ConstUserInfo userInfo = new ConstUserInfo();
	
	
	public MemberFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View memberView =  inflater.inflate(R.layout.member_layout, container, false);
		findById(memberView);
		
		setData();
		if (!DbManager.isExistUser()) {
			String sql_add_user = String.format("insert into %s values('%s', '%s', '%s', '%s','%s')", 
					ConstUserInfo.TABLE_NAME, user_info[0], user_info[1], user_info[2],
					user_info[3], user_info[4]);
			DbManager.add(sql_add_user);
		} else if (jsonArray.length() > 0) {
			DbManager.delUser();
			String sql_add_user = String.format("insert into %s values('%s', '%s', '%s', '%s','%s')", 
					ConstUserInfo.TABLE_NAME, user_info[0], user_info[1], user_info[2],
					user_info[3], user_info[4]);
			DbManager.add(sql_add_user);
		}
		return memberView;
	}
	
	private void findById(View view) {
		tv_name = (TextView) view.findViewById(R.id.user_name);
		tv_height = (TextView) view.findViewById(R.id.user_height);
		tv_sex = (TextView) view.findViewById(R.id.user_sex);
		tv_ideal_weight = (TextView) view.findViewById(R.id.user_ideal_weight);
		tv_exit = (TextView) view.findViewById(R.id.tv_exit);
		
		tv_exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {		
				showNormalDialog();
			}
		});
	}
	
	public void setJsonArray(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}
	
	private String[] getDataArray(JSONArray jsonArray, int row) throws JSONException {
		String str[] = new String[4];
		user_info[0] = jsonArray.getJSONObject(row).getString("_id");
		user_info[1] = "123";
		user_info[2] = str[0] = jsonArray.getJSONObject(row).getString("name");
		user_info[4] = str[1] = jsonArray.getJSONObject(row).getString("height");
		user_info[3] = str[2] = jsonArray.getJSONObject(row).getString("sex");
		return str;
	}
	
	private String getIdealWeight(String height, String sex) {
		float float_height = Float.parseFloat(height);
		float ideal_weight;
		if(sex.equals("男")) {
			ideal_weight = (float) ((float_height - 80 ) * 0.7);
		} else {
			ideal_weight = (float) ((float_height - 70 ) * 0.6);
		}
		return String.valueOf((int)ideal_weight);
	}
	
	private void setData() {
		String str[] = new String[3];
		String str_weight = null;
		if (jsonArray.length() > 0) {
			for (int row = 0; row < jsonArray.length(); row++) {
				try {
					str = getDataArray(jsonArray, row);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				str_weight = getIdealWeight(str[1], str[2]);
				
				tv_name.setText("姓名: " + str[0]);
				tv_height.setText("身高: " + str[1] + " (cm)");
				tv_sex.setText("性别: " + str[2]);
				tv_ideal_weight.setText("理想体重: " + str_weight + " (kg)");
			}
		} else {
			DbManager.RawQueryUser(userInfo);
			str_weight = getIdealWeight(userInfo.getHeight(), userInfo.getSex());
			
			tv_name.setText("姓名: " + userInfo.getName());
			tv_height.setText("身高: " + userInfo.getHeight() + " (cm)");
			tv_sex.setText("性别: " + userInfo.getSex());
			tv_ideal_weight.setText("理想体重: " + str_weight + " (kg)");
		}
	}
	
	private void showNormalDialog(){
		/* @setIcon 设置对话框图标
		 * @setTitle 设置对话框标题
		 * @setMessage 设置对话框消息提示
		 * setXXX方法返回Dialog对象，因此可以链式设置属性
		 */
		final AlertDialog.Builder normalDialog = 
				new AlertDialog.Builder(getActivity());
        normalDialog.setTitle("退出");
        normalDialog.setMessage("确定退出用户?");
        normalDialog.setPositiveButton("确定", 
        		new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	DbManager.delUser();
            	DbManager.delWeight();
            	Intent lIntent = new Intent(getActivity(), StartActivity.class);
			    startActivity(lIntent);
			    getActivity().finish();
            }
        });
        normalDialog.setNegativeButton("取消", 
            new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //...To-do
            }
        });
        // 显示
        normalDialog.show();
    }
	
}
