package ck.edu.basketballtracer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class newMatch extends AppCompatActivity {
    private Spinner spinnerModeSuivi;
    private Spinner spinnerEquipe1;
    private Spinner spinnerEquipe2;
    private Button butRetour;
    private Button butValider;
    private Button butPhoto;
    private ImageView imagePhoto;
    private ArrayList equipesArray;

    private static final int PIC_ID = 123;
    private static final int REQUEST_CAMERA_ID = 1;
    private static final int REQUEST_STORAGE_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_match_constraint);
        getSupportActionBar().hide();
        equipesArray = new ArrayList();
        String[] equipes = new String[3];
        equipes[0] = "Equipe 1";
        equipes[1] = "Equipe 2";
        equipes[2] = "Les deux";
        this.spinnerModeSuivi = (Spinner) findViewById(R.id.modeSuiviBox);
        this.spinnerEquipe1 = (Spinner) findViewById(R.id.equipe1Box);
        this.spinnerEquipe2 = (Spinner) findViewById(R.id.equipe2Box);
        connexionToDB();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                equipes);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                equipesArray);

        this.spinnerModeSuivi.setAdapter(adapter);
        this.spinnerEquipe1.setAdapter(adapter2);
        this.spinnerEquipe2.setAdapter(adapter2);
        butRetour = (Button) findViewById(R.id.buttonRetour);
        butValider = (Button) findViewById(R.id.buttonValider);
        butPhoto = (Button) findViewById(R.id.photoButton);
        imagePhoto = (ImageView) findViewById(R.id.photoView);

        butPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(newMatch.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
                    requestCameraPermission();
                if (ContextCompat.checkSelfPermission(newMatch.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(newMatch.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    requestStoragePermission();

                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, PIC_ID);
            }
        });
    }

    public void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_ID);
    }

    public void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_ID);
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_ID || requestCode == REQUEST_STORAGE_ID){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted !", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Permission denied !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PIC_ID){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imagePhoto.setImageBitmap(photo);
        }
    }

    protected void connexionToDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("AVANT COOOOOOOO");
                    Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.90.41:3306/basketballtracer", "root", "");
                    Statement statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery("SELECT * from equipes");
                    while (rs.next()) {
                        System.out.println("équipe : "+ rs.getString(2));
                        equipesArray.add(rs.getString(2));
                    }
                    System.out.println("APRES COOOOOOOO");
                } catch (Exception e) {
                    System.out.println("ERREUR");
                }
            }
        }).start();


    }

    public void redirectMain(View view){
        Intent monIntent = new Intent(newMatch.this, MainActivity.class);
        startActivity(monIntent);
    }

    public void redirectMatch(View view){
        Intent monIntent = null;
        if (spinnerModeSuivi.getSelectedItem().toString().equals("Les deux"))
            monIntent = new Intent(newMatch.this, twoteams.class);

        else
            monIntent = new Intent(newMatch.this, oneTeam.class);
        startActivity(monIntent);
    }
}
