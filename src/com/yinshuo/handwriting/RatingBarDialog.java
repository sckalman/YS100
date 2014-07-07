package com.yinshuo.handwriting;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.yinshuo.R;
import com.yinshuo.handler.MiNaIOHandler;
import com.yinshuo.handwriting.SignNameDialog.PaintView;
import com.yinshuo.usbconnect.ThreadReadWriterIOSocket;
import com.yinshuo.utils.BulkEnum;
import com.yinshuo.utils.MyUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class RatingBarDialog extends Dialog implements OnRatingBarChangeListener{
	Context context;
	LayoutParams p ;
	RatDialogInterface dialogListener;
	private TextView textView;
	private float mRating;
	
	public RatingBarDialog(Context context,RatDialogInterface dialogListener) {
		super(context, R.style.Dialog_Fullscreen);
		this.context = context;
		this.dialogListener = dialogListener; 
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(context.getString(R.string.rat_dialog_title));
		//requestWindowFeature(Window.FEATURE_PROGRESS);
		
		setContentView(R.layout.rat_dialog);
/*		
		p = getWindow().getAttributes();  //获取对话框当前的参数值   
		p.height = 552;//(int) (d.getHeight() * 0.4);   //高度设置为屏幕的0.4 
		p.width = 1024;//(int) (d.getWidth() * 0.6);    //宽度设置为屏幕的0.6		
		
		getWindow().setAttributes(p);     //设置生效
*/		
		RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		textView = (TextView) findViewById(R.id.rating_textview);

		ratingBar.setOnRatingBarChangeListener(this);
		ratingBar.setRating(5);
		
		Button btnOk = (Button) findViewById(R.id.rating_ok);
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					//dialogListener.refreshActivity(mRating);
					JSONObject jsonObject = new JSONObject();
					
					jsonObject.put("cmd", BulkEnum.EVALUTE_STATUS.EVALUTE_RECV_CMD);
					jsonObject.put("content", mRating);
					MiNaIOHandler.getmSession().write(jsonObject.toString());

					
					RatingBarDialog.this.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		Button btnCancel = (Button)findViewById(R.id.rating_cancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				JSONObject jsonObject = new JSONObject();
				
				try {
					jsonObject.put("cmd", BulkEnum.EVALUTE_STATUS.EVALUTE_RECV_CMD);
					jsonObject.put("content",-1);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				MiNaIOHandler.getmSession().write(jsonObject.toString());
				cancel();
			}
		});
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser)
	{
		mRating = rating;
	//	if (ratingBar.getId() == R.id.ratingBar){
			textView.setText(String.valueOf(rating));
			Log.i("sc", rating+"--rating");
	//	}
	}


}
