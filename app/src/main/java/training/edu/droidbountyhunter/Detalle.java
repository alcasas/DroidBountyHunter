package training.edu.droidbountyhunter;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import training.edu.data.DBProvider;
import training.edu.interfaces.OnTaskListener;
import training.edu.models.Fugitivo;
import training.edu.network.NetServices;
import training.edu.utilities.PictureTools;

import static training.edu.utilities.PictureTools.MEDIA_TYPE_IMAGE;

/**
 * @author Giovani González
 * Created by darkgeat on 09/08/2017.
 */

public class Detalle extends AppCompatActivity{

    private String titulo;
    private int mode;
    private int id;
    private static final int REQUEST_CODE_PHOTO_IMAGE = 1787;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        // Se obtiene la información del intent
        titulo = getIntent().getStringExtra("title");
        mode = getIntent().getIntExtra("mode",0);
        id = getIntent().getIntExtra("id",0);
        // Se pone el nombre del fugitivo como titulo
        setTitle(titulo + " - [" + id + "]");
        TextView message = (TextView) findViewById(R.id.mensajeText);
        // Se identifica si es Fugitivo o Capturado para el mensaje...
        if (mode == 0){
            message.setText("El fugitivo sigue suelto...");
        }else {
            Button add = (Button)findViewById(R.id.buttonAgregar);
            add.setVisibility(View.GONE);
            message.setText("Atrapado!!!");
        }
    }

    public void OnCaptureClick(View view) {
        DBProvider database = new DBProvider(this);
        database.UpdateFugitivo(new Fugitivo(id,titulo,"1"));
        NetServices apiCall = new NetServices(new OnTaskListener() {
            @Override
            public void OnTaskCompleted(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String message = object.optString("mensaje","");
                    MessageClose(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void OnTaskError(int errorCode, String message, String error) {

            }
        });
        apiCall.execute("Atrapado",String.valueOf(id));
    }

    public void OnDeleteClick(View view) {
        DBProvider database = new DBProvider(this);
        database.DeleteFugitivo(id);
        setResult(mode);
        finish();
    }

    public void MessageClose(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.create();
        builder.setTitle("Alerta!!!");
        builder.setMessage(message);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                setResult(mode);
                finish();
            }
        });
        builder.show();
    }

    public void OnFotoClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri pathImage = PictureTools.with(Detalle.this).getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,pathImage);
        startActivityForResult(intent,REQUEST_CODE_PHOTO_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PHOTO_IMAGE){
            if (resultCode == RESULT_OK){

            }
        }
    }
}
