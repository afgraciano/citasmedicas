//clase de base de datos usando SQLite
package com.example.citas_medicas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CitasMedicasBD extends SQLiteOpenHelper {
    //private static final String NOMBRE_BD = "administracion";
    //private static final String TABLA_CITAS = "CREATE TABLE Citas ( id INTEGER NOT NULL, Cedula TEXT NOT NULL, Nombre TEXT NOT NULL, Apellidos TEXT NOT NULL, Fecha TEXT NOT NULL, Hora TEXT NOT NULL, Estado boolean DEFAULT 1, PRIMARY KEY(id))";


    // Constructor de la clase que recibe el contexto, nombre de la base de datos, factor de cursor y versión de la base de datos
    public CitasMedicasBD(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Método llamado cuando se crea la base de datos por primera vez
    @Override
    //creacion base de datos
    public void onCreate(SQLiteDatabase BaseDeDatos) {
        // Creación de la tabla Citas con las columnas: Cedula, Nombre, Apellidos, Fecha, Hora, TipoCita
        BaseDeDatos.execSQL("CREATE TABLE Citas ( Cedula text, Nombre text, Apellidos text, Fecha text, Hora text, TipoCita int)");

        // Inserción de datos de ejemplo en la tabla Citas
        BaseDeDatos.execSQL("INSERT INTO Citas (Cedula,Nombre,Apellidos,Fecha,Hora,TipoCita ) VALUES ('71375739','Andres Felipe', 'Graciano Monsalve', '28-12-2022', '12:30:00', 1)");
        BaseDeDatos.execSQL("INSERT INTO Citas (Cedula,Nombre,Apellidos,Fecha,Hora,TipoCita ) VALUES ('1020410367','Neilans Catalina', 'Fernandez Monsalve', '04-12-2023', '01:30:00', 2)");
        BaseDeDatos.execSQL("INSERT INTO Citas (Cedula,Nombre,Apellidos,Fecha,Hora,TipoCita ) VALUES ('1214714441','Katerine Juliet', 'Diaz Lasso', '11-12-2022', '03:00:00', 3)");
        BaseDeDatos.execSQL("INSERT INTO Citas (Cedula,Nombre,Apellidos,Fecha,Hora,TipoCita ) VALUES ('71375739','Andres Felipe', 'Graciano Monsalve', '25-12-2022', '12:30:00', 2)");

        // Creación de la tabla TipoCita con la columna nombre
        BaseDeDatos.execSQL("CREATE TABLE TipoCita (nombre text)");

        // Inserción de datos de ejemplo en la tabla TipoCita
        BaseDeDatos.execSQL("INSERT INTO TipoCita (nombre) VALUES('Odontologica')"); // id 1
        BaseDeDatos.execSQL("INSERT INTO TipoCita (nombre) VALUES('Medico General')"); // id 2
        BaseDeDatos.execSQL("INSERT INTO TipoCita (nombre) VALUES('Ginecologia')"); //id 3
        BaseDeDatos.execSQL("INSERT INTO TipoCita (nombre) VALUES('Sicologia')"); // id 4
    }


    // Método llamado cuando se realiza una actualización de la base de datos
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Puedes agregar código aquí para manejar actualizaciones de la base de datos
    }


}
