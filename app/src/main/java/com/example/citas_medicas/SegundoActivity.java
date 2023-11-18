//clase segunda activity para escanear la cedula con la camara
package com.example.citas_medicas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SegundoActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;// Variable para manejar la vista del escáner
    private String mensaje, men, resultado; // Variables para almacenar información relacionada con el escaneo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segundo);// Establece el diseño de la actividad
    }

    // Método llamado cuando se presiona el botón para iniciar el escaneo
    public void btn(View v) {  // metodo que se dezpliega desde onClick del boton y dezpliegua el scanner abriendo la camara del celular
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView); // Configura la vista del escáner
        mScannerView.setResultHandler(this);// Manejador de resultados para procesar la información del escaneo
        mScannerView.startCamera(); // Inicia la cámara para escanear
    }

    // Método llamado cuando se obtiene un resultado del escaneo
    @Override
    public void handleResult(Result result) {
        Intent anterior = new Intent(this, MainActivity.class);
        Log.v("HandleResult", result.getText()); // Muestra en el registro el texto escaneado
        Log.v("result", result.getBarcodeFormat().toString());// Muestra en el registro el formato del código de barras escaneado

        mensaje = result.getText();// Almacena el texto escaneado en la variable mensaje
        men = mensaje.substring(48, 58); // Obtiene un fragmento específico del mensaje
        resultado = manejoCaracteres(men);   // Aplica un método para manejar los caracteres y obtener la cédula formateada en la variable resultado

        anterior.putExtra("dato", resultado);// Pasa la cédula formateada al intent anterior
        Toast.makeText(this, resultado, Toast.LENGTH_LONG).show();// Muestra un mensaje emergente con la cédula formateada
        //builder.setMessage("Se lee la cedula:  " + resultado);
        //AlertDialog alertDialog = builder.create();
        //alertDialog.show();
        startActivity(anterior);// Inicia la actividad anterior (MainActivity)

        //mScannerView.resumeCameraPreview(this); // con esta sentencia aseguramos que despues de leer un qr podamos seguir leyendo mas

    }

    // crear metodo para ser usado con la cedula = a la variable resultado
    //con lo cual se desea buscar en la base de datos para traer el registro

    //hace formato a los valores obtenidos del escaneo QR sacando solo el numero de cedula
    // Método para manejar los caracteres y formatear la cédula
    public String manejoCaracteres(String mensaje) {
        String cedula = mensaje;
        int num = Integer.parseInt(cedula);
        cedula = Integer.toString(num);


        return cedula;
    }


    // Método para regresar a la actividad anterior (MainActivity)
    public void regreso(View view) {
        Intent anterior = new Intent(this, MainActivity.class);
        startActivity(anterior);


    }
}