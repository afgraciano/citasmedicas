//Creamos la clase principal para iniciar el programa e importamos las diferentes librerias necesarias
package com.example.citas_medicas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.Result;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import java.util.regex.*;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

//se realiza mi método main para inicializar el programa
public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    // Declaración de variables miembro
    private String dato;
    private EditText txtCedula, txtNombre, txtApellido, txtFecha, txtHora;
    private Spinner spinnerTipoCita; // Agregamos el Spinner

    private String nombreBD = "administracion6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de las variables de interfaz de usuario
        txtCedula = findViewById(R.id.txt_cedula);
        txtNombre = findViewById(R.id.txt_nombre);
        txtApellido = findViewById(R.id.txt_apellido);
        txtFecha = findViewById(R.id.txt_fecha);
        txtHora = findViewById(R.id.txt_hora);

        // Obtenemos el valor de la variable del segundo activity
        dato = getIntent().getStringExtra("dato");
        txtCedula.setText(dato);

        // Inicializamos el Spinner
        spinnerTipoCita = findViewById(R.id.spinner_tipo_cita); // Asociamos el Spinner desde el layou cargarDatosSpinner();
        cargarDatosSpinner();// Cargamos los datos del Spinner desde la base de datos

        // debajo usamos la estructura alerta para mostrar un mensaje
        // Verificamos si hay datos en el campo de cédula
        if (txtCedula.length() != 0) {
            // Mostramos un mensaje con la cédula escaneada
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Resultado del scanner");
            builder.setMessage("Se lee la cedula:  " + dato);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }


    }


    // Método para cargar las opciones en el Spinner desde la base de datos
    private void cargarDatosSpinner() {
        CitasMedicasBD admin = new CitasMedicasBD(this, nombreBD, null, 1);
        SQLiteDatabase baseDeDatos = admin.getReadableDatabase();

        List<String> opciones = new ArrayList<>();
        Cursor cursor = baseDeDatos.rawQuery("SELECT nombre FROM TipoCita", null);

        while (cursor.moveToNext()) {
            String nombreTipoCita = cursor.getString(cursor.getColumnIndex("nombre"));
            opciones.add(nombreTipoCita);
        }

        cursor.close();
        baseDeDatos.close();

        // Configurar el adaptador y asociarlo al Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoCita.setAdapter(adapter);
    }


    // Método para manejar el resultado del escáner (no implementado)
    @Override
    public void handleResult(Result result) {
    // Aquí puedes implementar la lógica para manejar el resultado del escáner
    }

    // Método para el botón que nos lleva al segundo activity
    public void btn(View v) {  // metodo que se despliega desde onClick del boton y nos lleva al intent al que apunta
        Intent siguiente = new Intent(this, SegundoActivity.class);
        startActivity(siguiente);
    }

    // Método para buscar la cédula en la base de datos
    //validacion del ingreso de la cedula
    public void buscarCedula(View v) {
        String ced = txtCedula.getText().toString();
        if (ced.length() == 0) {
            Toast.makeText(this, "Ingresar cedula para procesar", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Se esta procesando la solicitud", Toast.LENGTH_LONG).show();
            Busqueda();
        }
    }

    //metodo para consultar la cedula en la base de datos
    private void Busqueda() {
        CitasMedicasBD admin = new CitasMedicasBD(this, nombreBD, null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        String cedu = txtCedula.getText().toString();
        if (cedu.equals("BORRAR")) {  //borra toda la base de datos poniendo en el campo de cedula la palabra BORRAR y presionando boton "Buscar Documento"
            this.LimpiarCitas();
            return;
        }
//validacion de que la cedula sea un numero
        if (!this.esNumerico(cedu)) { //si la cedula no es numerica, salimos
            this.MensajeCuidado("Existen caracteres invalidos en la cedula");
            return;
        }
//damos formato a la fecha
        SimpleDateFormat dataFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        String fecha = dataFormat.format(date);
        StringBuilder constructor = new StringBuilder();
//validamos que el campo cedula tenga algun numero
        if (!cedu.isEmpty()) {
            Cursor fila = BaseDeDatos.rawQuery("SELECT Nombre,Apellidos,Fecha,Hora, TipoCita FROM Citas WHERE Cedula = ? order by FECHA, HORA", new String[]{cedu});
            //miramos si el numero de cedula ingresado esta registrado en base de datos y muestro con un mensaje de alerta con la informacion de la cita medica
            if (fila.moveToFirst()) {
                //declaro variables  indicando que son strings
                String nombre = fila.getString(0);
                String apellidos = fila.getString(1);
                String fecha1 = fila.getString(2);
                String hora = fila.getString(3);


                txtNombre.setText(nombre);
                txtApellido.setText(apellidos);
                txtFecha.setText(fecha1);
                txtHora.setText(hora);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Información");
                constructor.append("El usuario: " + nombre + " " + apellidos + " identificado con cedula: " + cedu + " tiene las siguientes citas:\r\n");
                do {
                    int tipoCita = fila.getInt(4); // Obtener el tipo de cita
                    String tipoCitaNombre = obtenerNombreTipoCita(tipoCita); // Obtener el nombre del tipo de cita
                    fecha1 = fila.getString(2);
                    hora = fila.getString(3);
                    if (fecha1.equals(fecha)) {
                        constructor.append("El dia de hoy " + fecha1 + " a las " + hora + " de " + tipoCitaNombre + "\r\n");
                    } else {
                        constructor.append("El dia " + fecha1 + " a las " + hora + " de " + tipoCitaNombre + " \r\n");
                    }
                } while (fila.moveToNext());
                builder.setMessage(constructor.toString());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            //si no esta registrada muestro con un mensaje de alerta que el usuario no existe en base de datos
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Resultado de la consulta");
                builder.setMessage("Se ha presentado un error, la cedula: " + cedu + " no existe en la base de datos actual ó no tiene agendada cita medica " +
                        ", debe registrarse primero");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            BaseDeDatos.close();
        }
        //mensaje que indica que no ha ingresado ningun valor al campo de texto de cedula
        else {
            Toast.makeText(this, "No ha ingresado la cedula para generar la busqueda", Toast.LENGTH_LONG).show();
        }

    }


    // Método para obtener el nombre del tipo de cita a partir de su ID
    private String obtenerNombreTipoCita(int tipoCitaId) {
        CitasMedicasBD admin = new CitasMedicasBD(this, nombreBD, null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        Cursor cursor = BaseDeDatos.rawQuery("SELECT nombre FROM TipoCita WHERE rowid = ?", new String[]{String.valueOf(tipoCitaId)});

        // Cursor cursor = BaseDeDatos.rawQuery("SELECT nombre, rowid FROM TipoCita ", new String[]{});


        if (cursor.moveToFirst()) {
            String nombre = cursor.getString(0);
            // Integer elId = cursor.getInt(1);

            return nombre;
        } else {
            return "Desconocido";
        }
    }


    // Método para obtener el tipo de cita seleccionado en un clic de botón
    private int obtenerTipoCitaSeleccionado(Spinner spinner) {
        String tipoCitaSeleccionado = spinner.getSelectedItem().toString();

        // Aquí, realiza la lógica para convertir el tipo de cita a un valor numérico
        // por ejemplo, podrías buscar en la base de datos el ID asociado al nombre del tipo de cita

        // Supongamos que tienes un método en tu base de datos que obtiene el ID del tipo de cita por nombre
        int idTipoCita = obtenerIdTipoCitaPorNombre(tipoCitaSeleccionado);

        return idTipoCita;
    }


    // Método para obtener el ID del tipo de cita por nombre
    private int obtenerIdTipoCitaPorNombre(String nombreTipoCita) {
        CitasMedicasBD admin = new CitasMedicasBD(this, nombreBD, null, 1);
        SQLiteDatabase BaseDeDatos = admin.getReadableDatabase();

        Cursor cursor = BaseDeDatos.rawQuery("SELECT rowid FROM TipoCita WHERE nombre = ?", new String[]{nombreTipoCita});

        int idTipoCita = -1; // Valor predeterminado si no se encuentra el tipo de cita

        if (cursor.moveToFirst()) {
            idTipoCita = cursor.getInt(0);
        }

        cursor.close();
        BaseDeDatos.close();

        return idTipoCita;
    }


    // Método modificado para obtener el tipo de cita seleccionado
    // metodo para insertar en la base de datos empleando un boton y sensando los campos
    public void insertar(View view) {
        CitasMedicasBD admin = new CitasMedicasBD(this, nombreBD, null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        //declaro variables y asigno strings
        String cedula = txtCedula.getText().toString();
        String nombre = txtNombre.getText().toString();
        String apellido = txtApellido.getText().toString();
        String fecha = txtFecha.getText().toString();
        String hora = txtHora.getText().toString();

        // Obtener el tipo de cita seleccionado
        int tipoCita = obtenerTipoCitaSeleccionado(spinnerTipoCita);
        String tipoCitaNombre = obtenerNombreTipoCita(tipoCita); // Obtener el nombre del tipo de cita


        Integer tipo = 0;
        if (!this.esNumerico(cedula)) { //comprobamos si la cedula no es numerica,
            this.MensajeCuidado("Existen caracteres invalidos en la cedula");
            return;
        }
        if (!this.esFecha(fecha)) {//comprobamos que la fecha sea valida
            this.MensajeCuidado("La fecha es invalida, escriba la fecha en formato DD-MM-AAAA separado de guiones");
            return;
        }
        if (hora.indexOf("24:00") != -1) {//comprobamos que la hora no sea 24:00 ya que debe ser 00:00
            this.MensajeCuidado("La hora no es valida");
            return;
        }
        if (!this.Time24HoursValidator(hora)) {//comprobamos que la hora sea valida
            this.MensajeCuidado("La hora no es valida");
            return;
        }

        //ingreso datos a la base de datos
        if (!cedula.isEmpty() && !nombre.isEmpty() && !apellido.isEmpty() && !fecha.isEmpty() && !hora.isEmpty()) {

            Cursor cursor = BaseDeDatos.rawQuery("Select * from Citas where Cedula = ? and Fecha = ? and Hora = ?", new String[]{cedula, fecha, hora});
            if (cursor.moveToFirst()) {
                //sacar mensaje que ya tiene cita asignada
                this.MensajeCuidado("El usuario " + cedula + " " + nombre + " " + apellido + " ya tiene una cita asignada en la misma fecha y hora");
            } else {
                BaseDeDatos.execSQL("INSERT into Citas (Cedula, Nombre, Apellidos, Fecha, Hora, TipoCita) VALUES(?, ?, ?, ?, ?, ?)", new Object[]{cedula, nombre, apellido, fecha, hora, tipoCita});
                // saque un mensaje que diga que la cita se creo.
                this.MensajeInformacion("Se creo la cita el día " + fecha + " a la hora " + hora + " para el usuario " + nombre + " " + apellido + " con CC " + cedula + "la cita " + tipoCitaNombre);
            }

            //BaseDeDatos.execSQL("INSERT INTO Citas (Cedula,Nombre,Apellidos,Fecha,Hora,TipoCita ) VALUES ('71375739','Andres Felipe', 'Graciano Monsalve', '13-11-2020', '11:30:00', 2)");
        } else {
            this.MensajeCuidado("Todos los campos deben estas llenos"); //llamada al mensaje de cuidado para avisar que debe llenar todos los campos
            BaseDeDatos.close();
        }
    }

    //metodo para mostrar mensaje de cuidado
    private void MensajeCuidado(String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cuidado");
        builder.setMessage(mensaje);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //metodo para mostrar mensaje de informacion
    private void MensajeInformacion(String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Información");
        builder.setMessage(mensaje);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //metodo para borrar toda la base de datos
    private void LimpiarCitas() {
        CitasMedicasBD admin = new CitasMedicasBD(this, nombreBD, null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        BaseDeDatos.execSQL("DELETE FROM Citas;");
    }

    //metodo para validar ingreso de campo de cedula
    private boolean esNumerico(String valor) {
        // Código para validar si es numérico
        if (valor == null) {
            return false;
        }
        try {
            Long numero = Long.parseLong(valor);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    //metodo para validar ingreso de campo de fecha
    private boolean esFecha(String valor) {
        // Código para validar si es una fecha válida
        DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);
        try {
            sdf.parse(valor);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    //metodo para validar ingreso en campo de hora militar
    private boolean Time24HoursValidator(String valor) {
        //Código para validar si es una hora militar válida
        try {
            DateTimeFormatter.ofPattern("HH:mm").parse(valor);
        } catch (DateTimeParseException e) {
            System.out.println(valor + "  esta no es una hora valida militar.");

            return false;
        }
        return true;
    }

    // Clase interna para validar el formato de la hora en formato militar
    public class Time24HoursValidator {

        private Pattern pattern;
        private Matcher matcher;

        private static final String TIME24HOURS_PATTERN =
                "([01]?[0-9]|2[0-3]):[0-5][0-9]";

        public Time24HoursValidator() {
            pattern = Pattern.compile(TIME24HOURS_PATTERN);
        }

        /**
         * valida tiempo en formato 24 horas con expresion regular
         *
         * @param time direcion para validacion
         * @return true formato de tiempo valido, false si formato de tiempo invalido
         */
        public boolean validate(final String time) {

            matcher = pattern.matcher(time);
            return matcher.matches();

        }
    }
}