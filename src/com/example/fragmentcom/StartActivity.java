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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class StartActivity extends Activity {

	private Button btn_login;
	private TextWatcher textWatcher;
	private EditText edt_user;
	private EditText edt_pwd;
	private TextView tv_del_user;
	private TextView tv_del_pwd;
	
    ProgressDialog waitingDialog;
	
	MySqliteHelper data_helper;
	
	class MyHandler  extends Handler {
	 	public MyHandler() { 
	 		
        } 
 
        public MyHandler(Looper L) { 
            super(L); 
        } 
        
        @Override
        public void handleMessage(Message msg) {
        	super.handleMessage(msg);
        	Bundle b = msg.getData(); 
            String error_info = b.getString("error"); 
            if (error_info.equals("0")) {
            	showNormalDialog("用户名或者密码错误!");
            } else {
            	showNormalDialog("连接服务器失败!");
            }
        }

	}
	
	private MyHandler myHandler;
	
	public StartActivity() {	
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		data_helper = DbManager.getInstanceDb(this);
		
		setContentView(R.layout.start_layout);
		Init();
	}
	 
    private static final int TCP_SERVER_PORT = SerConInfo.TCP_SERVER_PORT;//should be same to the server port
    private static final String TCP_SERVER_ADDR = SerConInfo.TCP_SERVER_ADDR;
    private String weight_inMsg = "";
    private String user_inMsg = "";
    private int login_flag;

    Runnable networkTask = new Runnable() {
       
		@Override
		public void run() {
			 runTcpClient();
			 finish();
			 runTcpClientAsService();
		}
    };
    
    Runnable checkUserTask = new Runnable() {
		
		@Override
		public void run() {
			login_flag = checkTheUser();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			waitingDialog.dismiss();
			if (login_flag == 1) {
				new Thread(networkTask).start();
			} else {
				Message msg = new Message();
				Bundle b = new Bundle();// 存放数据 
				if (login_flag == 0) {
					//账号或者密码错误
					b.putString("error","0"); 
					msg.setData(b); 
				} else {
					//连接服务器失败
					b.putString("error","-1"); 
					msg.setData(b); 
				}
				StartActivity.this.myHandler.sendMessage(msg);
			}
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
            String outMsg = "2";
            out.write(outMsg);
            out.flush();
            
            String inMsg;
            
            while (!(inMsg = in.readLine()).equals("==end by wth==")) {
            	
            }
            
            outMsg = edt_user.getText().toString().trim();
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
            //close connection  
            s.close();  
        } catch (SocketTimeoutException e) {
        	weight_inMsg = "[ ]";
        	user_inMsg = "[ ]";
        } catch (UnknownHostException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
    }
    
    private void runTcpClientAsService() {  
        Intent lIntent = new Intent(StartActivity.this, MainActivity.class);
        lIntent.putExtra("weight", weight_inMsg);
        lIntent.putExtra("user", user_inMsg);
        startActivity(lIntent);
        this.finish();
    }  
    
    private void setBtnDisabled() {
    	btn_login.setEnabled(false);
    	btn_login.setBackgroundColor(Color.parseColor("#E4E4E4"));
    	btn_login.setTextColor(Color.parseColor("#BAC5C0"));//#C9C6CF
    }
    
    private void setBtnEnabled() {
    	btn_login.setEnabled(true);
    	btn_login.setBackgroundResource(R.drawable.btn_selector);
    	btn_login.setTextColor(Color.parseColor("#FFFFFF"));
    }
    
    
    /**
     * 登录按钮
     * @param view
     */
    public void Login(View view) {
    	if (!isNetworkAvailable(this)) {
    		showNormalDialog("网络未连接!");
    		return;
    	}
    	showWaitingDialog();
    	Thread check = new Thread(checkUserTask);
    	check.start();
    }
    
    private void Init() {
    	btn_login = (Button) findViewById(R.id.btn_login);
		edt_user = (EditText) findViewById(R.id.edt_user);
		edt_pwd = (EditText) findViewById(R.id.edt_pwd);
		
		tv_del_user = (TextView) findViewById(R.id.tv_del_user);
		tv_del_pwd = (TextView)findViewById(R.id.tv_del_pwd);
		
		tv_del_user.setBackgroundColor(Color.WHITE);
		tv_del_user.setEnabled(false);
		tv_del_pwd.setBackgroundColor(Color.WHITE);
		tv_del_pwd.setEnabled(false);

		
		setBtnDisabled();
		textWatcher = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
					set_tv_user_pwd();
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (edt_user.length() > 0 && edt_pwd.length() > 0) {
					setBtnEnabled();
				} else {
					setBtnDisabled();
				}
				set_tv_user_pwd();
			}
		};
		
		edt_user.addTextChangedListener(textWatcher);
		edt_pwd.addTextChangedListener(textWatcher);
		
		waitingDialog = new ProgressDialog(this);
		myHandler = new MyHandler(); 
		
    }
    
    private void set_tv_user_pwd() {
    	String content_user =edt_user.getText().toString();     
		if (content_user!=null && !content_user.equals("")) {            
		  		tv_del_user.setBackgroundResource(R.drawable.delete);
		  		tv_del_user.setEnabled(true);           
		}else{          
		  		tv_del_user.setBackgroundColor(Color.WHITE);
		  		tv_del_user.setEnabled(false);       
		}   
		String content_pwd =edt_pwd.getText().toString();
		if (content_pwd!=null && !content_pwd.equals("")) {            
	  		tv_del_pwd.setBackgroundResource(R.drawable.delete);
	  		tv_del_pwd.setEnabled(true);           
		}else{          
			tv_del_pwd.setBackgroundColor(Color.WHITE);
			tv_del_pwd.setEnabled(false);       
		}   
    }
    
    public void edt_del_user(View view) {
    	edt_user.setText("");
    }
    
    public void edt_del_pwd(View view) {
    	edt_pwd.setText("");
    }
    
    /*
     * 等待提示框
     */
    private void showWaitingDialog() {
    	
        /* 等待Dialog具有屏蔽其他控件的交互能力
         * @setCancelable 为使屏幕不可点击，设置为不可取消(false)
         * 下载等事件完成后，主动调用函数关闭该Dialog
         */
        waitingDialog.setTitle("登录");
        waitingDialog.setMessage("等待中...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }
    
    /*
     * 
     */
    private void showNormalDialog(String text){
		/* @setIcon 设置对话框图标
		 * @setTitle 设置对话框标题
		 * @setMessage 设置对话框消息提示
		 * setXXX方法返回Dialog对象，因此可以链式设置属性
		 */
		final AlertDialog.Builder normalDialog = 
				new AlertDialog.Builder(this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage(text);
        normalDialog.setPositiveButton("确定", 
        		new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	//do nothing
            }
        });
        
        // 显示
        normalDialog.show();
    }
    
    /*
     * 判断用户是否登录成功
     */
    public int checkTheUser() {
    	/*
    	 *  -1 与服务器连接失败
    	 *  0 验证失败
    	 *  1 验证成功
    	 *  
    	 */
    	int flag = -1;
    	Socket s = new Socket();
    	try {  
        	//注意host改成你服务器的hostname或IP地址
        	SocketAddress address = new InetSocketAddress(TCP_SERVER_ADDR, TCP_SERVER_PORT);
        	s.connect(address, 50);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            //send output msg
            String outMsg = "1";
            out.write(outMsg);
            out.flush();
            
            String inMsg;
            while (!(inMsg = in.readLine()).equals("==end by wth=="));
            
            out.write(edt_user.getText().toString());
            out.flush();
            while (!(inMsg = in.readLine()).equals("==end by wth=="));
            
            out.write(edt_pwd.getText().toString());
            out.flush();
            
            String lastMsg = "";
            while (!(inMsg = in.readLine()).equals("==end by wth==")) {
            	lastMsg = inMsg;
            }
            if (lastMsg.equals("true")) {
            	flag = 1;
            } else {
            	flag = 0;
            }
            
    	} catch (SocketTimeoutException e) {
    		flag = -1; //与服务器连接失败
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		try {
				s.close();
				return flag;
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	return flag;
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
