package ck.edu.basketballtracer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class DetailsActivity extends AppCompatActivity {

    TextView textEq1;
    TextView textEq2;
    TextView textScore;
    TextView textDate;
    ArrayList<String> infosMatch;
    String adr = "";
    MapsActivity mapAct = new MapsActivity();

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        this.infosMatch = new ArrayList<>();

        textEq1 = findViewById(R.id.textEq1);
        textEq2 = findViewById(R.id.textEq2);
        textScore = findViewById(R.id.textScore);
        textDate = findViewById(R.id.textDate);

        connexionToDB();

    }

    /**
     * Connexion à la base de données et recuperation des infos sur le match sélectionné
     */
    protected void connexionToDB(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/basketballtracer", "root", "");
                    System.out.println("Connexion Details reussi");
                    Statement statement = connection.createStatement();
                    //RECUPERER L'ID DU MATCH POUR METTRE DANS LE WHERE
                    Intent intent = getIntent();
                    adr = intent.getStringExtra("title");
                    System.out.println("RECUP ADRESSE : "+adr);
                    ResultSet rs = statement.executeQuery("SELECT e1.nomEquipe, e2.nomEquipe, m.dateMatch, m.score1, m.score2 from matchs m, equipes e1, equipes e2 WHERE e1.idE = m.idEquipe1 AND e2.idE = m.idEquipe2 AND m.adresse = '"+adr+"'");
                    System.out.println("APRES RESULTSET");
                    while (rs.next()){
                        //RECUPERER POUR CHAQUE TEXTE

                        textEq1.setText(rs.getString(1));
                        System.out.println("Nom de score : "+rs.getString(3));
                        textEq2.setText(rs.getString(2));
                        textDate.setText(rs.getString(3));
                        textScore.setText(rs.getString(4));
                    }
                } catch (Exception e) {
                    System.out.println("ERREUR");
                }



            }}).start();

    }

    /**
     * Redirection vers la page de la map
     * @param view
     */
    public void redirectMaps(View view) {
        Intent monIntent = new Intent(DetailsActivity.this, MapsActivity.class);
        startActivity(monIntent);
    }
}
