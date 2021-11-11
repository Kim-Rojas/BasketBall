package ck.edu.basketballtracer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class historical extends AppCompatActivity {
    private Button butRetour;
    private MainActivity act;
    private ArrayList<String> listeTout;

    private ArrayAdapter<String> adapter;
    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historical);
        getSupportActionBar().hide();

        this.butRetour = (Button) findViewById(R.id.buttonRetour);
        this.act = new MainActivity();
        this.listeTout = new ArrayList<String>();

        butRetour.setBackgroundColor(Color.rgb(204, 102, 51));

        if(act.getLanguageChosen()=="fr"){
            act.changeLanguage("fr");
        } else if(act.getLanguageChosen()=="en"){
            act.changeLanguage("en");
        }

        connexionToDB();

    }

    /**
     * Redirection vers la page d'accueil
     * @param view
     */
    public void redirectMain(View view) {
        Intent monIntent = new Intent(historical.this, MainActivity.class);
        startActivity(monIntent);
    }

    /**
     * Connexion à la base de données
     */
    protected void connexionToDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/basketballtracer", "root", "");
                    System.out.println("Connexion historique reussi");
                    Statement statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery("SELECT e1.nomEquipe, e2.nomEquipe, m.dateMatch, m.score1, m.score2 from matchs m, equipes e1, equipes e2 WHERE e1.idE = m.idEquipe1 AND e2.idE = m.idEquipe2");

                    //Recuperation des attributs issus de la requete
                    while (rs.next()) {
                        String s = rs.getString(1) + " vs " + rs.getString(2) + "     |     " + rs.getString(3) + "   |   " + rs.getString(4)+ " : " + rs.getString(5);
                        listeTout.add(s);
                        //insertion des informations dans la listView
                        ListView liste = (ListView) findViewById(R.id.liste);
                        adapter = new ArrayAdapter<>(historical.this, R.layout.list_items, R.id.textItem, listeTout);
                        liste.setAdapter(adapter);
                    }

                } catch (Exception e) {
                    System.out.println("ERREUR");
                }
            }
        }).start();

    }

}
