package com.rashid.app.languagetranslation;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

public class translate_api extends AsyncTask<String, String, String> {
    private OnTranslationCompleteListener listener;
    @Override
    protected String doInBackground(String... strings) {
        String[] strArr = (String[]) strings;
        String str = "";
        try {
            String encode = URLEncoder.encode(strArr[0], "utf-8");
            StringBuilder sb = new StringBuilder();
            sb.append("https://translate.googleapis.com/translate_a/single?client=gtx&sl=");
            sb.append(strArr[1]);
            sb.append("&tl=");
            sb.append(strArr[2]);
            sb.append("&dt=t&q=");
            sb.append(encode);
            HttpResponse execute = new DefaultHttpClient().execute(new HttpGet(sb.toString()));
            StatusLine statusLine = execute.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                execute.getEntity().writeTo(byteArrayOutputStream);
                String byteArrayOutputStream2 = byteArrayOutputStream.toString();
                byteArrayOutputStream.close();
                JSONArray jSONArray = new JSONArray(byteArrayOutputStream2).getJSONArray(0);
                String str2 = str;
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONArray jSONArray2 = jSONArray.getJSONArray(i);
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str2);
                    sb2.append(jSONArray2.get(0).toString());
                    str2 = sb2.toString();
                }
                return str2;
            }
            execute.getEntity().getContent().close();
            throw new IOException(statusLine.getReasonPhrase());
        } catch (Exception e) {
            Log.e("translate_api",e.getMessage());
            listener.onError(e);
            return str;
        }
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onStartTranslation();
    }
    @Override
    protected void onPostExecute(String text) {
        listener.onCompleted(text);
    }
    public interface OnTranslationCompleteListener{
        void onStartTranslation();
        void onCompleted(String text);
        void onError(Exception e);
    }
    public void setOnTranslationCompleteListener(OnTranslationCompleteListener listener){
        this.listener=listener;
    }
}
