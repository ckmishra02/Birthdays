package com.misterstrange.birthdays;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fabAdd;
    TextView tvName, tvDob;
    TextView btnSort, btnSortName, btnSortDate, btnSortMonth, btnSortYoungest, btnSortOldest, btnSortUpcoming;

    MyDatabaseHelper dbHelper;
    private RecyclerView recyclerView;
   // private List<Person> personList;
    private ArrayList<Person> personList;

    private PersonAdapter adapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        fabAdd = findViewById(R.id.fabAdd);
        recyclerView = findViewById(R.id.recyclerView);
        btnSortOldest = findViewById(R.id.btnSortOldest);
        btnSortYoungest = findViewById(R.id.btnSortYoungest);
        btnSortName = findViewById(R.id.btnSortName);
        btnSortDate = findViewById(R.id.btnSortDate);
        btnSortMonth = findViewById(R.id.btnSortMonth);
        btnSortUpcoming = findViewById(R.id.btnSortUpcoming);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        personList = new ArrayList<>();


        dbHelper = new MyDatabaseHelper(this);


        List<Person> dbPersons = dbHelper.getAllPersons();


        personList.addAll(dbPersons);


        sortPersonListByBirthdate();

        adapter = new PersonAdapter(personList, MainActivity.this, dbHelper);
        recyclerView.setAdapter(adapter);



        btnSortYoungest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                sortPersonListByBirthdate();

                adapter.notifyDataSetChanged();

            }
        });


        btnSortUpcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sort the personList by upcoming birthday
                Collections.sort(personList, Person.BIRTHDAY_COMPARATOR);

                adapter.notifyDataSetChanged();
            }
        });



        btnSortOldest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sort the contacts by birthdate in ascending order
                Collections.sort(personList, new Comparator<Person>() {
                    @Override
                    public int compare(Person person1, Person person2) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                        Date birthdate1, birthdate2;
                        String b1 = person1.getDate() + "-" + person1.getMonth() + "-" + person1.getYear();
                        String b2 = person2.getDate() + "-" + person2.getMonth() + "-" + person2.getYear();
                        try {
                            birthdate1 = dateFormat.parse(b1);
                            birthdate2 = dateFormat.parse(b2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0; // Handle the error as needed
                        }
                        return birthdate1.compareTo(birthdate2); // Sort from younger to older
                    }
                });



                adapter.notifyDataSetChanged();





            }
        });








        btnSortName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Collections.sort(personList, new Comparator<Person>() {
                    @Override
                    public int compare(Person contact1, Person contact2) {
                        return contact1.getName().compareToIgnoreCase(contact2.getName());

                    }
                });

                adapter.notifyDataSetChanged();


            }
        });


        btnSortDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(personList, new Comparator<Person>() {
                    @Override
                    public int compare(Person contact1, Person contact2) {
                        return contact1.getDate().compareToIgnoreCase(contact2.getDate());

                    }
                });

                adapter.notifyDataSetChanged();

            }
        });


        btnSortMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(personList, new Comparator<Person>() {
                    @Override
                    public int compare(Person contact1, Person contact2) {
                        return contact1.getMonth().compareToIgnoreCase(contact2.getMonth());

                    }
                });

                adapter.notifyDataSetChanged();

            }
        });



        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPerson();
            }
        });



    }

    @SuppressLint("MissingInflatedId")
    private void addPerson(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_add, null);
        builder.setView(view);

        EditText dialogName = view.findViewById(R.id.dialogName);
        EditText dialogDate = view.findViewById(R.id.dialogDate);
        EditText dialogMonth = view.findViewById(R.id.dialogMonth);
        EditText dialogYear = view.findViewById(R.id.dialogYear);
        Button dialogAddThisPerson = view.findViewById(R.id.dialogAddThisPerson);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        dialogAddThisPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String etName = dialogName.getText().toString();
                String etDate = dialogDate.getText().toString();
                String etMonth = dialogMonth.getText().toString();
                String etYear = dialogYear.getText().toString();



                if (TextUtils.isEmpty(etName) || TextUtils.isEmpty(etDate) || TextUtils.isEmpty(etMonth) || TextUtils.isEmpty(etYear)){
                    Toast.makeText(MainActivity.this, "Please fill all the details", Toast.LENGTH_LONG).show();
                }


                else {

                    int intDate = Integer.parseInt(etDate);
                    int intMonth = Integer.parseInt(etMonth);
                    int intYear = Integer.parseInt(etYear);

                    // Format the date with an extra zero if it's less than 10
                    String formattedDate = (intDate < 10) ? "0" + intDate : String.valueOf(intDate);

                    // Format the month with an extra zero if it's less than 10
                    String formattedMonth = (intMonth < 10) ? "0" + intMonth : String.valueOf(intMonth);


                    if (intDate <= 31 && intDate > 0 && intMonth <= 12 && intMonth > 0 && intYear > 0){


                        Person newPerson = new Person();
                         newPerson.setName(etName);
                         newPerson.setDate(formattedDate);
                         newPerson.setMonth(formattedMonth);
                         newPerson.setYear(etYear);

                         dbHelper.addContact(newPerson);

                         personList.add(newPerson);

                        // Sort the list by birthdate
                        sortPersonListByBirthdate();

                         Toast.makeText(MainActivity.this, "Data Added", Toast.LENGTH_SHORT).show();
                         adapter.notifyDataSetChanged();



                        alertDialog.dismiss();

                    }

                    else {
                        Toast.makeText(MainActivity.this, "Invalid Details", Toast.LENGTH_SHORT).show();
                    }

                }




            }
        });


    }

    public void sortPersonListByBirthdate() {
        Collections.sort(personList, new Comparator<Person>() {
            @Override
            public int compare(Person person1, Person person2) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                Date birthdate1, birthdate2;
                String b1 = person1.getDate() + "-" + person1.getMonth() + "-" + person1.getYear();
                String b2 = person2.getDate() + "-" + person2.getMonth() + "-" + person2.getYear();
                try {
                    birthdate1 = dateFormat.parse(b1);
                    birthdate2 = dateFormat.parse(b2);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0; // Handle the error as needed
                }
                return birthdate2.compareTo(birthdate1); // Sort from younger to older
            }
        });

    }







}