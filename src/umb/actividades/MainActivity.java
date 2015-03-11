package umb.actividades;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RemoteViews;

public class MainActivity extends AppWidgetProvider {

	
	
	@Override
	public void onEnabled(Context context) {
		final Intent intent = new Intent(context, MainActivity.class);
		final PendingIntent pending = PendingIntent.getService(context, 0, intent, 0);
		final AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pending);
		long interval = 1000*60;
		alarm.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),interval, pending);
		
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
		String res = consultarClima();
		RemoteViews remoteViews; 
        ComponentName thisWidget;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.activity_main);
		thisWidget = new ComponentName(context, MainActivity.class);
		remoteViews.setImageViewResource(R.id.imageView1, R.drawable.n2);
		remoteViews.setTextViewText(R.id.textView1, "BOGOTA");
		remoteViews.setTextViewText(R.id.textView2, res);
		
		appWidgetManager.updateAppWidget(thisWidget, remoteViews);
	}

	@SuppressLint("NewApi")
	public String consultarClima() {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		String resultado = "Sin información";
		String queryString = "http://162.243.173.93:8081/umb-ideam-clima/api/clima/obtenerClima/BOGOTA";

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(queryString);

		try {
			Log.e("lo hace", "lo hace");
			HttpResponse httpEntity = httpClient.execute(httpGet);
			String respStr = EntityUtils.toString(httpEntity.getEntity());
			JSONObject resj = new JSONObject(respStr);
			resultado = (String) resj.get("temperatura");
			Log.e("temperatura",resultado);

		} catch (Exception e) {
			Log.e("excepcion",e.getMessage());
			e.printStackTrace();
		}
		return resultado;
	}

}
