package umb.actividades;


import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class ConfiguracionActivity extends Activity{

	final String[] datos =
	        new String[]{"BOGOTA","CALI","MEDELLIN","ARMENIA","PASTO"};
	 
	
	Spinner sp;
	private int widgetId = 0;
	String seleccionado;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuracion);
		
		ArrayAdapter<String> adaptador =
		        new ArrayAdapter<String>(this,
		            android.R.layout.simple_spinner_item, datos);
		
		sp = (Spinner) findViewById(R.id.spinner1);
		sp.setAdapter(adaptador);
		
		//widget
		
		//Obtenemos el Intent que ha lanzado esta ventana
        //y recuperamos sus parámetros
        Intent intentOrigen = getIntent();
        Bundle params = intentOrigen.getExtras();
 
        //Obtenemos el ID del widget que se está configurando
        widgetId = params.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
 
        //Establecemos el resultado por defecto (si se pulsa el botón 'Atrás'
        //del teléfono será éste el resultado devuelto).
        setResult(RESULT_CANCELED);
	}

	
	public void guardar(View view){
		seleccionado = sp.getSelectedItem().toString();
		Toast.makeText(getApplicationContext(), sp.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
	
		//guardar resultado
		//Guardamos el mensaje personalizado en las preferencias
        SharedPreferences prefs =
            getSharedPreferences("WidgetPrefsUMB", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("widget"+widgetId, seleccionado);
        editor.commit();

        //Actualizamos el widget tras la configuración
        AppWidgetManager appWidgetManager =
            AppWidgetManager.getInstance(ConfiguracionActivity.this);
        MainActivity.actualizarWidget(ConfiguracionActivity.this, appWidgetManager, widgetId);

        //Devolvemos como resultado: ACEPTAR (RESULT_OK)
        Intent resultado = new Intent();
        resultado.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, resultado);
        finish();
	}
	
	
}
