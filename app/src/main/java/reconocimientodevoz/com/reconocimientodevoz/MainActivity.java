package reconocimientodevoz.com.reconocimientodevoz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.zxing.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.security.PrivateKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class MainActivity extends Activity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView scannerView; //Declaramos el tipo del sccaner
    private EditText txtResultado, txtFormato, precio /*txtCompara*/;


    private ViewFlipper vfImagenes;// Declaramos el tipo ViewFlipper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        visorImagenes();

        txtFormato=(EditText)findViewById(R.id.txtFormato);
        txtFormato.requestFocus();
    }

    public void visorImagenes(){
        vfImagenes=(ViewFlipper)findViewById(R.id.vfimagenes);
        vfImagenes.setFlipInterval(2000);
        vfImagenes.startFlipping();
    }

    public void scannerQR(View view){
        scannerView= new ZXingScannerView(this);
        setContentView(scannerView);
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        setContentView(R.layout.activity_main);
        txtFormato=(EditText)findViewById(R.id.txtFormato);
        txtResultado=(EditText)findViewById(R.id.txtResultado);
        txtResultado.setText(result.getText());//Formato si es QR o Barras
        txtFormato.setText(String.valueOf(result.getBarcodeFormat()));//codigo del producto
    }

    EditText grabarProducto;
    private static final int RECOGNIZE_SPEECH_ACTIVITY=1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case RECOGNIZE_SPEECH_ACTIVITY:

                if(resultCode == RESULT_OK && null != data){

                    ArrayList<String> objhabla= data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String HablaCadena= objhabla.get(0);
                    grabarProducto=(EditText)findViewById(R.id.txtNomProducto);
                    grabarProducto.setText(HablaCadena);
                   /* txtCompara=(EditText)findViewById(R.id.txtCompara);*/
                   // String producto= this.grabarProducto.getText().toString();

                   /* String compara = this.txtCompara.getText().toString();
                    if (producto.equals("")) {

                        grabarProducto.setText(HablaCadena);
                    }else {
                        txtCompara.setText(HablaCadena);
                    }*/


                }
                break;
            default:
                break;
        }
    }

    public void btnHablar(View view){

        Intent IntentoReconocimientoVoz= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        //Configuramos a español
        IntentoReconocimientoVoz.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,"es-MX");

        try {
            startActivityForResult(IntentoReconocimientoVoz,RECOGNIZE_SPEECH_ACTIVITY);

        }catch (ActivityNotFoundException a){

            Toast.makeText(getApplicationContext(),"Tu dispositivo no soporta el reconocimiento por voz",
                    Toast.LENGTH_SHORT).show();
        }
    }

 /*   String precio= this.precio.getText().toString();

if (precio.equals("")) {
        Toast.makeText(this, "Ha dejado campos vacios",
                Toast.LENGTH_LONG).show();
    }

    En lugar de .equals(“”), también podemos usar la librería de android “TextUtils”


    String precio= this.precio.getText().toString();

if (TextUtils.isEmpty(precio_coste) {
        Toast.makeText(this, "Ha dejado campos vacios",
                Toast.LENGTH_LONG).show();
*/
    RadioButton vidrio,plastico;

    public void Guardar(View view){

        precio=(EditText)findViewById(R.id.txtPrecio);

        vidrio=(RadioButton)findViewById(R.id.rbVidrio);
        plastico=(RadioButton)findViewById(R.id.rbPlastico);
        String Envase="";
        if(vidrio.isChecked()==true) {
            Envase = "Vidrio";
        }else if (plastico.isChecked()==true){
            Envase="Plástico";
        }

        Date date= new Date();
        DateFormat fecha= new SimpleDateFormat("dd-MM-yyyy");
        DateFormat hora= new SimpleDateFormat("HH:mm:ss");

        grabarProducto=(EditText)findViewById(R.id.txtNomProducto);
        /*txtCompara=(EditText)findViewById(R.id.txtCompara);*/
        String producto= this.grabarProducto.getText().toString();
       /* String compara = this.txtCompara.getText().toString();*/

        /*if(producto.length()==compara.length()){*/
        try
        {
            File ruta_memoria_interna = Environment.getExternalStoragePublicDirectory("archivo");


            File nombre_archivo = new File(ruta_memoria_interna.getAbsolutePath(), String.valueOf(txtResultado.getText())+"-"+
                                                         grabarProducto.getText()+"-"+
                                                        String.valueOf(fecha.format(date))+
                                                        ".txt");

            OutputStreamWriter Escribir_flujo_Salida = new OutputStreamWriter(new FileOutputStream(nombre_archivo));

            Escribir_flujo_Salida.write("Formato: " +txtFormato.getText()+ "\n"+
                       "Codigo : "+String.valueOf(txtResultado.getText())+"\n"+
                       "Descripción: "+grabarProducto.getText()+"\n"+
                       "Precio: S/. "+String.valueOf(precio.getText())+ "\n"+
                       "Envase: "+Envase+"\n"+
                       "Fecha de Consumo: "+String.valueOf(fecha.format(date))+"\n"+
                       "Hora de Consumo: "+String.valueOf(hora.format(date)));
            Escribir_flujo_Salida.close();

        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al escribir fichero en la memoria interna");
            Toast.makeText(getApplicationContext(), "No se guardaron los datos", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(getApplicationContext(),"Datos del Producto Guardado\n \n"+
                "Codigo : "+String.valueOf(txtResultado.getText())+"\n"+
                "Descripción: "+grabarProducto.getText()+"\n"+
                "Precio: S/. "+String.valueOf(precio.getText())+ "\n"+
                "Envase: "+Envase+"\n",Toast.LENGTH_SHORT).show();

        txtFormato.requestFocus();
        visorImagenes();
    }


    public  void limpiar(View view){
        vidrio=(RadioButton)findViewById(R.id.rbVidrio);
        plastico=(RadioButton)findViewById(R.id.rbPlastico);
        /*txtCompara=(EditText)findViewById(R.id.txtCompara);*/
        txtFormato.setText("");
        txtResultado.setText("");
        grabarProducto.setText("");
        /*txtCompara.setText("");*/
        precio.setText("");
        vidrio.setChecked(false);
        plastico.setChecked(false);
        txtFormato.requestFocus();
        visorImagenes();
    }
}
