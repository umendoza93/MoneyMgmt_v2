package com.example.moneymgmt_v2;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button btnPlus,btnMinus,btnClr;
    EditText dateLine,amtLine,infoLine;
    TextView blnLine,tblDateLIne, tblAmtLine, tblInfoLine;
    TableRow mainRow;
    SQLiteDatabase db;
    IDataAccess dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateLine = findViewById(R.id.dateID);
        amtLine = findViewById(R.id.amtID);
        infoLine = findViewById(R.id.infoID);
        tblDateLIne = findViewById(R.id.tblDateID);
        tblInfoLine = findViewById(R.id.tblCatID);
        tblAmtLine = findViewById(R.id.tblAmtID);
        blnLine = findViewById(R.id.blnID);

        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);
        btnClr = findViewById(R.id.clrID);

        // Set current balance at the top and populate the content body
        db = openOrCreateDatabase("transaction.db", Context.MODE_PRIVATE,null);
        //db.execSQL("CREATE TABLE Transactions (Date TEXT, Price double, Category TEXT)");
        // Load the data from database
        LoadActivity();


    }
    @Override
    protected void onStart() {
        super.onStart();

        // Add money spent on minus click
        btnMinus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String date = dateLine.getText().toString();
                String amt = amtLine.getText().toString();
                String info = infoLine.getText().toString();
                String balanceTxt = blnLine.getText().toString();
                double bal = Double.parseDouble(balanceTxt.substring(balanceTxt.indexOf("$") + 1));

                Log.i("info", "The value of bal is " + bal);
                if((date.length() == 0) || (amt.length() == 0) || (info.length() == 0)) {
                    Log.e("error", "Missing content in either 'date', 'amt', or 'info'");
                    return;
                }
                if (bal == 0) {
                    Toast.makeText(v.getContext(), "Balance too low", Toast.LENGTH_SHORT).show();
                    return;
                }

                SetActivity(date, -Double.parseDouble(amt), info);

                dateLine.getText().clear();
                amtLine.getText().clear();
                infoLine.getText().clear();

                LoadActivity();
            }
        });

        // Add balance on Plus click
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = dateLine.getText().toString();
                String amt = amtLine.getText().toString();
                String info = infoLine.getText().toString();

                if((date.length() == 0) || (amt.length() == 0) || (info.length() == 0)) {
                    Log.e("error", "Missing content in either 'date', 'amt', or 'info'");
                    return;
                }

                // Use this method to add to the database
                SetActivity(date, Double.parseDouble(amt), info);

                dateLine.getText().clear();
                amtLine.getText().clear();
                infoLine.getText().clear();

                LoadActivity();
            }
        });

        btnClr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //blnLine.setText("Current Balance: $0.00");
                String sql = "DELETE FROM Transactions";
                db.execSQL(sql);
                Toast toast = Toast.makeText(v.getContext(), "Data Cleared", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                LoadActivity();
            }
        });

    }

    // This will save the activity to the database
    public void SetActivity(String date, Double amt, String cat) {
        ActivityModel activity = new ActivityModel();
        activity.atyDate = date;
        activity.atyPrice = amt;
        activity.atyCat = cat;

        String sql = "INSERT INTO Transactions VALUES " + activity.toSQL();
        db.execSQL(sql);


    }

    //This method loads the data from the database
    public void LoadActivity() {
        //Make sure the TextView is clear, otherwise it will cause data overflow
        tblDateLIne.setText("");
        tblAmtLine.setText("");
        tblInfoLine.setText("");
        double bal = 0;

        String sql = "SELECT * FROM Transactions";
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                String date = cursor.getString(0);
                double amt = cursor.getDouble(1);
                String cat = cursor.getString(2);

                String line1 = tblDateLIne.getText().toString();
                String line2 = tblAmtLine.getText().toString();
                String line3 = tblInfoLine.getText().toString();

                if (line1.length() == 0) {
                    line1 = date;
                    line2 = String.valueOf(amt);
                    line3 = cat;
                } else {
                    line1 = line1 + "\n" + date;
                    line2 = line2 + "\n" + amt;
                    line3 = line3 + "\n" + cat;
                }

                tblDateLIne.setText(line1);
                tblAmtLine.setText(line2);
                tblInfoLine.setText(line3);
                bal = bal + amt;
            }
        }
        String balance = "Current Balance: $" + String.format("%.2f",bal);
        blnLine.setText(balance);
    }
}
