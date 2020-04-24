package com.userdev.winnerstars.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 30/03/2018.
 */

public class ExportarJson extends JSONObject {
    public ExportarJson() {
        try {
            Cursor c = bancoDados.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            if (c.moveToFirst())
            {
                while ( !c.isAfterLast() ){
                    String table = c.getString(c.getColumnIndex("name"));
                    put(table, (Object) getResults(table));
                    c.moveToNext();
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    SQLiteDatabase bancoDados = DbHelper.getInstance().getDatabase();

    private JSONArray getResults(String myTable) {

        String searchQuery = "SELECT  * FROM " + myTable;
        Cursor cursor = bancoDados.rawQuery(searchQuery, null );
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {
                    try
                    {
                        if( cursor.getString(i) != null )
                        {
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        Log.d("TAG_NAME", e.getMessage()  );
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        //Log.d("TAG_NAME", resultSet.toString() );
        return resultSet;
    }

    public void uploadParaWeb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String dados = ExportarJson.this.toString(4);
                    final ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
                    byte[] data = dados.getBytes("UTF-8");
                    String base64 = Base64.encodeToString(data, Base64.DEFAULT);
                    String nome = "omninotes.json";
                    nameValuePairs.add(new BasicNameValuePair("image", base64));
                    nameValuePairs.add(new BasicNameValuePair("nome", nome));

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://bruno-rodrigues2infoc.000webhostapp.com/uploadNotes.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    httpclient.execute(httppost);
                    System.out.println("http://bruno-rodrigues2infoc.000webhostapp.com/" + nome);
                } catch (Exception e) {
                    System.out.println("Error in http connection " + e.toString());
                }
            }
        }).start();
    }

}
