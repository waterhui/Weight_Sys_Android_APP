package com.example.fragmentcom;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.utils.ConstWeight;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ListFragment extends Fragment {

	
	private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
	private final int FP = ViewGroup.LayoutParams.MATCH_PARENT;
	private TableLayout tableLayout;
	private JSONArray jsonArray;
	private	List<ConstWeight> weightList;
	
	public ListFragment() {
		 
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View listView = inflater.inflate(R.layout.list_layout, container, false);
		Init(listView);
		return listView;
	}
	
	private void Init(View view) {
		tableLayout = (TableLayout) view.findViewById(R.id.table_layout_1);
		tableLayout.setStretchAllColumns(true);
		setData();
	}
	
	public void setJsonArray(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}
	
	public void setList(List<ConstWeight> weightList) {
		this.weightList = weightList;
	}
	
	private String[] getDataArrayByJson(JSONArray jsonArray, int row) throws JSONException {
		String str[] = new String[4];
		str[0] = jsonArray.getJSONObject(row).getString("weight");
		str[1] = jsonArray.getJSONObject(row).getString("bmi");
		str[2] = jsonArray.getJSONObject(row).getString("status");
		str[3] = jsonArray.getJSONObject(row).getString("time");
		return str;
	}
	
	
	private void setDataByJson() throws JSONException {
		String str[] = new String[4];
		int count = 1;
		for (int row = 0; row < jsonArray.length(); row++) {
			str = getDataArrayByJson(jsonArray, row);
			setTableRow(str, count);
			count++;
		}
	}
	
	private String[]  getDataArrayByList(int num) {
		String str[] = new String[4];
		str[0] = weightList.get(num).getWeight();
		str[1] = weightList.get(num).getBmi();
		str[2] = weightList.get(num).getStatus();
		str[3] = weightList.get(num).getTime();
		return str;
	}
	
	private void setDataByList() {
		String str[] = new String[4];
		int count = 1;
		for (int i = 0; i < weightList.size(); i++) {
			str = getDataArrayByList(i);
			setTableRow(str, count);
			count++;
		}
	}
	
	private void setTableRow(String[] s, int count) {
		String str[] = s;
		TableRow tableRow = new TableRow(getActivity());
		tableRow.setBackgroundColor(Color.rgb(222, 220, 210));
		
		for (int col = 0; col < 5; col++) {
			TextView tv = new TextView(getActivity());
			tv.setGravity(Gravity.CENTER);
			if (col == 0) {
				tv.setText(Integer.toString(count));
			} else {
				tv.setText(str[col - 1]);
			}
			tv.setBackgroundResource(R.drawable.shape);
			tableRow.addView(tv);
		}
		tableLayout.addView(tableRow, new TableLayout.LayoutParams(FP, WC));
	
	}
	
	private void setData() {
		if (jsonArray.length() > 0) {
			try {
				setDataByJson();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			setDataByList();
		}
	}
}
