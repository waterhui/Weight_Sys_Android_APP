package com.example.fragmentcom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.utils.DbManager;
import com.utils.MySqliteHelper;
import com.utils.SerConInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;


public class FirstActivity extends Activity {

	MySqliteHelper data_helper;
	
	 private static final int TCP_SERVER_PORT = SerConInfo.TCP_SERVER_PORT;	//should be same to the server port
	 private static final String TCP_SERVER_ADDR = SerConInfo.TCP_SERVER_ADDR;
	 private String weight_inMsg = "";
	 private String user_inMsg = "";
	
	public FirstActivity() {
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data_helper = DbManager.getInstanceDb(this);
		if (!isNetworkAvailable(this)) {
			if (DbManager.isExistUser()) {
				Intent lIntent = new Intent(FirstActivity.this, MainActivity.class);
				lIntent.putExtra("weight", "wth");
		        lIntent.putExtra("user", "wth");
		        startActivity(lIntent);
		        this.finish();
		        return;
			}
		}
		//如果存在用户,则直接进入登录界面
		if (DbManager.isExistUser()) {
			new Thread(networkTask).start();
		} else {
			Intent lIntent = new Intent(FirstActivity.this, StartActivity.class);
			startActivity(lIntent);
			this.finish();
		}
		
	}
	
	Runnable networkTask = new Runnable() {
	       
		@Override
		public void run() {
			 runTcpClient();
			 finish();
			 runTcpClientAsService();
		}
    };
	
	private void runTcpClient() {  
		Socket s = new Socket();
        try {
        	//注意host改成你服务器的hostname或IP地址
        	SocketAddress address = new InetSocketAddress(TCP_SERVER_ADDR, TCP_SERVER_PORT);
        	s.connect(address, 50);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            //send output msg
            //String outMsg = edt_user.getText().toString().trim();
            String outMsg = "2";
            out.write(outMsg);
            out.flush();
            
            String inMsg;
            
            while (!(inMsg = in.readLine()).equals("==end by wth==")) {
            	
            }
            Log.i("wth", inMsg);
            
            outMsg = DbManager.cur_user_name;
            out.write(outMsg);		//发送数据  
            out.flush();
            Log.i("TcpClient", "sent: " + outMsg);  
            //accept server response  

            
            while (!(inMsg = in.readLine()).equals("==end by wth==")) {
            	user_inMsg += inMsg;
            }
            
            //!!!去掉BOM头,否则无法正确解析
            if (user_inMsg != null && user_inMsg.startsWith("\ufeff")) {  
            	user_inMsg.substring(1);  
            }
            out.write(outMsg);		//发送数据  
            out.flush(); 
            
            while (!(inMsg = in.readLine()).equals("==end by wth==")) {
            	weight_inMsg += inMsg;
            }
            //!!!去掉BOM头,否则无法正确解析
            if (weight_inMsg != null && weight_inMsg.startsWith("\ufeff")) {  
            	weight_inMsg.substring(1);  
            }
            s.close();
            //close connection  
        } catch (UnknownHostException e) {  
            e.printStackTrace();  
        } catch (SocketTimeoutException e) {
        		weight_inMsg = "wth";
        		user_inMsg = "wth";
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {
        	try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
	
	private void runTcpClientAsService() {  
        Intent lIntent = new Intent(FirstActivity.this, MainActivity.class);
        lIntent.putExtra("weight", weight_inMsg);
        lIntent.putExtra("user", user_inMsg);
        startActivity(lIntent);
        this.finish();
    }  
	
	public static boolean isNetworkAvailable(Context context) {   
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        if (cm == null) {   
        	
        } else {
        	
			//如果仅仅是用来判断网络连接
			//则可以使用 cm.getActiveNetworkInfo().isAvailable();  
            NetworkInfo[] info = cm.getAllNetworkInfo();
              
			if (info != null) {   
                for (int i = 0; i < info.length; i++) {   
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {   
                        return true;   
                    }   
                }   
            }   
            
        }   
        return false;   
    } 
}
