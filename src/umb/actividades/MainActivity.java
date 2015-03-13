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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RemoteViews;
import android.widget.TextView;

public class MainActivity extends AppWidgetProvider {

	static String[] cuidados = new String[] {
			"Nivel BAJO. Circular libremente, no hay peligro.",
			"Nivel MODERADO. Usar casco o gorro, bloqueador solar, anteojos UV y transitar por zonas sombreadas.",
			"Nivel ALTO. Procure no exponerse al sol de 10 a 16 horas. Usar casco o gorro, bloqueador solar, anteojos UV y transitar por zonas sombreadas.",
			"Nivel MUY ALTO. Procure no exponerse al sol de 10 a 16 horas. Usar casco o gorro, bloqueador solar, anteojos UV y transitar por zonas sombreadas.",
			"Nivel EXTREMO. Procure no exponerse al sol de 10 a 16 horas. Usar casco o gorro, bloqueador solar, anteojos UV y transitar por zonas sombreadas." };

	@Override
	public void onEnabled(Context context) {
		final Intent intent = new Intent(context, MainActivity.class);
		final PendingIntent pending = PendingIntent.getService(context, 0,
				intent, 0);
		final AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pending);
		long interval = 1000 * 60;
		alarm.setRepeating(AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime(), interval, pending);

	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		String mensaje = "";
		for (int i = 0; i < appWidgetIds.length; i++) {
			// ID del widget actual
			int widgetId = appWidgetIds[i];
			actualizarWidget(context, appWidgetManager, widgetId);

		}
	}

	public static void actualizarWidget(Context context,
			AppWidgetManager appWidgetManager, int widgetId) {
		// tomamos del guardado
		// Recuperamos el mensaje personalizado para el widget actual
		SharedPreferences prefs = context.getSharedPreferences(
				"WidgetPrefsUMB", Context.MODE_PRIVATE);

		String mensaje = prefs.getString("widget" + widgetId, "CIUDAD...");

		//
		

		String res = consultarClima(mensaje);
		RemoteViews remoteViews;
		ComponentName thisWidget;
		remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.activity_main);
		thisWidget = new ComponentName(context, MainActivity.class);
		
		if(!res.equals("SIN CONEXION")){
			int valor = Integer.valueOf(res);
			if(valor>=0 && valor<=10){
				remoteViews.setImageViewResource(R.id.imageView1, R.drawable.n1);
				
			}
			if(valor>=11 && valor<=20){
				remoteViews.setImageViewResource(R.id.imageView1, R.drawable.n2);
				
			}
			if(valor>=21 && valor<=30){
				remoteViews.setImageViewResource(R.id.imageView1, R.drawable.n3);
				
			}
			if(valor>=31 && valor<=40){
				remoteViews.setImageViewResource(R.id.imageView1, R.drawable.n4);
				
			}
		}
		else{
			remoteViews.setImageViewResource(R.id.imageView1, R.drawable.n1);
		}
		
		
		remoteViews.setTextViewText(R.id.textView1, mensaje);
		remoteViews.setTextViewText(R.id.textView2, cuidados[1]);

		appWidgetManager.updateAppWidget(thisWidget, remoteViews);
	}

	@SuppressLint("NewApi")
	public static String consultarClima(String mensaje) {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		String resultado = "SIN CONEXION";
		String queryString = "http://162.243.173.93:8081/umb-ideam-clima/api/clima/obtenerClima/"
				+ mensaje;

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(queryString);

		try {
			Log.e("lo hace", queryString);
			HttpResponse httpEntity = httpClient.execute(httpGet);
			String respStr = EntityUtils.toString(httpEntity.getEntity());
			JSONObject resj = new JSONObject(respStr);
			resultado = (String) resj.get("temperatura");
			Log.e("temperatura", resultado);

		} catch (Exception e) {
			Log.e("excepcion", e.getMessage());
			e.printStackTrace();
		}
		return resultado;
	}

}
