package com.example.panglimi;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetJSONObject extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urlstr) {
        String result = null;
        try{
            URL url = new URL(urlstr[0]);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setDoInput(true);

            InputStreamReader inputStrteam = new InputStreamReader(con.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(inputStrteam);
            StringBuilder builder = new StringBuilder();
            String resultStr;

            while((resultStr = reader.readLine()) != null){
                builder.append(resultStr + "\n");
            }
            result = builder.toString();
            reader.close();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static String[] jsonParser_location(String jsonString){
        String longtitude = null;
        String latitude = null;
        String resdate = null;
        String[] cautionArray = null;

        try{
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("caution_list");
            cautionArray = new String[3*jsonArray.length()];
            Caution_Page.cautionCnt = jsonArray.length();
            Home_Page.cautionCnt = jsonArray.length();
            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject location = jsonArray.getJSONObject(i);
                latitude = location.optString("lc_latitude");
                longtitude = location.optString("lc_longtitude");
                resdate = location.optString("lc_resdate");

                cautionArray[3*i + 0] = latitude;
                cautionArray[3*i + 1] = longtitude;
                cautionArray[3*i + 2] = resdate;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cautionArray;
    }

    public static String[] jsonParser_user(String jsonString){
        String[] userArray = null;

        try{
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("user_list");
            userArray = new String[7];
            Caution_Page.cautionCnt = jsonArray.length();
            Home_Page.cautionCnt = jsonArray.length();
            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject location = jsonArray.getJSONObject(i);
                userArray[0] = location.optString("user_name");
                userArray[1] = location.optString("user_id");
                userArray[2] = location.optString("user_passwd");
                userArray[3]= location.optString("user_email");
                userArray[4]= location.optString("user_phone");
                userArray[5]= location.optString("user_stickNum");
                userArray[6]= location.optString("user_address");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userArray;
    }

    public static String[] jsonParser_users(String jsonString){
        String[] userArray = null;

        try{
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("user_select");
            userArray = new String[12];
            Caution_Page.cautionCnt = jsonArray.length();
            Home_Page.cautionCnt = jsonArray.length();
            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject location = jsonArray.getJSONObject(i);
                userArray[0] = location.optString("user_name");
                userArray[1] = location.optString("user_id");
                userArray[2] = location.optString("user_passwd");
                userArray[3]= location.optString("user_email");
                userArray[4]= location.optString("user_phone");
                userArray[5]= location.optString("user_stickNum");
                userArray[6]= location.optString("user_address");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userArray;
    }

    public static String[] jsonParser_address(String jsonString){
        String[] userArray = null;

        try{
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("user_list");
            userArray = new String[2];
            Caution_Page.cautionCnt = jsonArray.length();
            Home_Page.cautionCnt = jsonArray.length();
            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject location = jsonArray.getJSONObject(i);
                userArray[0] = location.optString("user_latitude");
                userArray[1] = location.optString("user_longtitude");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userArray;
    }

    public static String[] jsonParser_address2(String jsonString){
        String[] userArray = null;

        try{
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("user_select");
            userArray = new String[2];
            Caution_Page.cautionCnt = jsonArray.length();
            Home_Page.cautionCnt = jsonArray.length();
            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject location = jsonArray.getJSONObject(i);
                userArray[0] = location.optString("user_latitude");
                userArray[1] = location.optString("user_longtitude");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userArray;
    }
}