package com.mattquiros.omnitor;

import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mattquiros.omnitor.bean.UserLog;
import com.mattquiros.omnitor.util.Logger;
import com.mattquiros.omnitor.util.This;

public class SignUpTask extends AsyncTask<Void, Void, Void> {
    
    private Context context;
    private String email;
    private String successMessage;
    private String noNetMessage;
    private ProgressDialog pd;
    private Toast signupToast;
    private String toastMessage;
    private SharedPreferences prefs;
    
    public SignUpTask(Context context, String email, Toast signupToast) {
        this.context = context;
        this.email = email;
        this.signupToast = signupToast;
        this.successMessage = context.getString(R.string.signup_success);
        this.noNetMessage = context.getString(R.string.signup_error_no_net);
        prefs = context.getSharedPreferences(This.PREFS, Context.MODE_MULTI_PROCESS);
        
        this.pd = new ProgressDialog(context);
        pd.setMessage(context.getString(R.string.signup_wait));
        pd.setCancelable(false);
    }
    
    @Override
    protected void onPreExecute() {
        pd.show();
    }
    
    @Override
    protected Void doInBackground(Void... params) {
        Logger.d("STARTED: SignUpTask");
        toastMessage = null;
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(This.URL);
        post.setHeader("content-type", "application/json");
        try {
            String uuid = prefs.getString(This.KEY_UUID, This.NULL);
            String userLog = new Gson().toJson(new UserLog(uuid, email));
            Logger.d("user log: " + userLog);
            post.setEntity(new StringEntity(userLog));
            HttpResponse response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                toastMessage = successMessage;
                Editor editor = prefs.edit();
                editor.putBoolean(This.KEY_SIGNED_UP, true);
                editor.commit();
            } else {
                throw new Exception(statusLine.getReasonPhrase());
            }
        } catch (UnknownHostException e) {
            toastMessage = noNetMessage;
        } catch (Exception e) {
            toastMessage = "Error: " + e.getMessage();
            e.printStackTrace();
        }
        Logger.d("FINISHED: SignUpTask");
        return null;
    }
    
    @Override
    protected void onPostExecute(Void result) {
        pd.dismiss();
        if (signupToast == null) {
            signupToast = Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT);
        } else {
            signupToast.setText(toastMessage);
        }
        signupToast.show();
        
        if (toastMessage.equals(successMessage)) {
            context.startActivity(new Intent(context, HelloActivity.class));
            ((MainActivity) context).finish();
        }
    }

}
