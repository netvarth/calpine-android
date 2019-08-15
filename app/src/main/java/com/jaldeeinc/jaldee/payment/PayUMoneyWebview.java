package com.jaldeeinc.jaldee.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.response.CheckSumModel;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.nio.charset.Charset;

public class PayUMoneyWebview extends Activity {

	//private Button button;

	private static final String TAG = "PayUMoneyWebview";
	WebView webviewPayment;
	WebView mwebview;
	TextView  txtview;
	/*
	protected  void writeStatus(String str){
		txtview.setText(str);
	}*/

	Activity mActivity;
	Context mContext;
	String amount;
	CheckSumModel response_data;
	ProgressBar mProgress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payumoneyweb);

		mActivity=this;
		mContext=this;
		//button=(Button)findViewById(R.id.button1);

		webviewPayment = (WebView) findViewById(R.id.webView1);
		webviewPayment.getSettings().setJavaScriptEnabled(true);
		webviewPayment.getSettings().setDomStorageEnabled(true);
		webviewPayment.getSettings().setLoadWithOverviewMode(true);
		webviewPayment.getSettings().setUseWideViewPort(true);
		webviewPayment.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webviewPayment.getSettings().setSupportMultipleWindows(true);
		webviewPayment.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		mProgress=(ProgressBar)findViewById(R.id.progressbar);
		mProgress.setVisibility(View.VISIBLE);
		webviewPayment.setVisibility(View.GONE);
		webviewPayment.addJavascriptInterface(new PayUJavaScriptInterface(), "PayUMoney");
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			/*ynwUUID = extras.getString("ynwUUID");
			accountID = extras.getString("accountID");*/
			// amount = extras.getString("amount");
			amount =  Config.getAmountinSingleDecimalPoints(Double.parseDouble(extras.getString("amount")));
			 response_data =(CheckSumModel)getIntent().getSerializableExtra("responsedata");


		}
		if(response_data!=null)
		loadWebView(response_data);



	}

	@Override
	public void onBackPressed() {
	//	super.onBackPressed();
	}

	public void loadWebView(CheckSumModel responseDATA){



		StringBuilder url_s = new StringBuilder();

		if(responseDATA.getPaymentEnv().equalsIgnoreCase("production")) {
			//url_s.append("https://test.payu.in/_payment");
			url_s.append("https://secure.payu.in/_payment");
		}else{
			url_s.append("https://test.payu.in/_payment");
		}

		Log.e(TAG, "call url " + url_s);


		//	webviewPayment.postUrl(url_s.toString(),EncodingUtils.getBytes(getPostString(), "utf-8"));

		webviewPayment.postUrl(url_s.toString(),getPostString(responseDATA,amount).getBytes(Charset.forName("UTF-8")));

		webviewPayment.setWebChromeClient(new WebChromeClient() {

			// this will be called on page loading progress

			@Override

			public void onProgressChanged(WebView view, int newProgress) {

				//super.onProgressChanged(view, newProgress);


				mProgress.setProgress(newProgress);

				// hide the progress bar if the loading is complete

				if (newProgress == 100) {

  				/* call after laoding splash.html  */

					webviewPayment.setVisibility(View.VISIBLE);
					mProgress.setVisibility(View.GONE);

				}

			}

		});

		webviewPayment.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

				Config.logV("URL FINISH------------"+url);
			}



			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				Config.logV("Load URL--------------"+url);
				view.loadUrl(url);
				return true;
			}

		});
	}

	private final class PayUJavaScriptInterface {
        PayUJavaScriptInterface() {
        }

		@JavascriptInterface
        public void success( long id, final String paymentId) {
            runOnUiThread(new Runnable() {
                public void run() {

//                	Toast.makeText(PayUMoneyWebview.this, "Status is txn is success "+" payment id is "+paymentId, Toast.LENGTH_SHORT).show();
					Toast.makeText(PayUMoneyWebview.this, "Payment Successful", Toast.LENGTH_SHORT).show();


					Intent intent = new Intent(mContext, Home.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
					intent.putExtra("message", "payu");
					startActivity(intent);
					finish();
                	//String str="Status is txn is success "+" payment id is "+paymentId;
                  // new MainActivity().writeStatus(str);

                	/*TextView  txtview;
                	txtview = (TextView) findViewById(R.id.textView1);
                	txtview.setText("Status is txn is success "+" payment id is "+paymentId);*/

                }
            });
        }
		@JavascriptInterface
		public void failure( long id, final String paymentId) {
			runOnUiThread(new Runnable() {
				public void run() {

//					Toast.makeText(PayUMoneyWebview.this, "Status is txn is failed "+" payment id is "+paymentId, Toast.LENGTH_SHORT).show();
					Toast.makeText(PayUMoneyWebview.this, "Payment Failed", Toast.LENGTH_SHORT).show();
					//String str="Status is txn is failed "+" payment id is "+paymentId;
					// new MainActivity().writeStatus(str);

					/*TextView  txtview;
					txtview = (TextView) findViewById(R.id.textView1);
					txtview.setText("Status is txn is failed "+" payment id is "+paymentId);*/
					Intent intent = new Intent(mContext, Home.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
					intent.putExtra("message", "payu");
					startActivity(intent);
					finish();

				}
			});
		}

    }




	private String getPostString(CheckSumModel checkSumModel,String amountPass)
	{

		String firstnamePass = SharedPreference.getInstance(mContext).getStringValue("firstname", "");


		String mobile = SharedPreference.getInstance(mContext).getStringValue("mobile", "");

		String key  = checkSumModel.getMerchantKey();
		String salt  = "Bwxo1cPe";
		String txnid = checkSumModel.getTxnid();
		String amount = amountPass;
		String firstname = firstnamePass;
		String email = checkSumModel.getEmail();
		String productInfo = new Gson().toJson(checkSumModel.getProductinfo());
		String mobilePass=mobile;

		String surlPass= checkSumModel.getSuccessUrl();
		String furlpass=checkSumModel.getFailureUrl();

		StringBuilder post = new StringBuilder();
		post.append("key=");
		post.append(key);
		post.append("&");
		post.append("txnid=");
		post.append(txnid);
		post.append("&");
		post.append("amount=");
		post.append(amount);
		post.append("&");
		post.append("productinfo=");
		Config.logV("Product Info @@@@@@@@@@@@@@"+productInfo);
		post.append(productInfo);
		post.append("&");
		post.append("firstname=");
		post.append(firstname);
		post.append("&");
		post.append("email=");
		post.append(email);
		post.append("&");
		post.append("phone=");
		post.append(mobilePass);
		post.append("&");
		post.append("surl=");
		post.append(surlPass);
		//https://www.payumoney.com/mobileapp/payumoney/success.php
		//https://www.payumoney.com/mobileapp/payumoney/failure.php
		post.append("&");
		post.append("furl=");
		post.append(furlpass);
		post.append("&");

		post.append("hash=");
		post.append(checkSumModel.getChecksum());
		post.append("&");

		/*StringBuilder checkSumStr = new StringBuilder();
		*//* =sha512(key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5||||||salt) *//*
	    MessageDigest digest=null;
	    String hash;
	    try {
	        digest = MessageDigest.getInstance("SHA-512");// MessageDigest.getInstance("SHA-256");

	        checkSumStr.append(key);
	        checkSumStr.append("|");
	        checkSumStr.append(txnid);
	        checkSumStr.append("|");
	        checkSumStr.append(amount);
	        checkSumStr.append("|");
	        checkSumStr.append(productInfo);
	        checkSumStr.append("|");
	        checkSumStr.append(firstname);
	        checkSumStr.append("|");
	        checkSumStr.append(email);
	        checkSumStr.append("|||||||||||");
	        checkSumStr.append(salt);

	        digest.update(checkSumStr.toString().getBytes());

	        hash = bytesToHexString(digest.digest());
	    	post.append("hash=");
	        post.append(hash);
	        post.append("&");
	        Log.i(TAG, "SHA result is " + hash);
	    } catch (NoSuchAlgorithmException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
	    }*/

		post.append("service_provider=");
		post.append("payu_paisa");
		return post.toString();
	}



	private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');

            }
            sb.append(hex);
        }
        return sb.toString();
    }



}
