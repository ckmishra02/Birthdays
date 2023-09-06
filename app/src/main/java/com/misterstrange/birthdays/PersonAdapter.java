package com.misterstrange.birthdays;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {

    private List<Person> personList;
    Context context;
    private MyDatabaseHelper dbHelper;

  //  public PersonAdapter(List<Person> personList) {
  //      this.personList = personList;
  //  }

    public PersonAdapter(List<Person> personList, Context context, MyDatabaseHelper dbHelper) {
        this.personList = personList;
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder,  int position) {
        Person person = personList.get(position);
        holder.nameTextView.setText(person.getName());

        // Format the date with an extra zero if it's a single-digit value
        String formattedDate = (person.getDate().length() == 1) ? "0" + person.getDate() : person.getDate();

        // Format the month with an extra zero if it's a single-digit value
        String formattedMonth = (person.getMonth().length() == 1) ? "0" + person.getMonth() : person.getMonth();

        holder.dobTextView.setText("Birthday: "+ formattedDate + "-" + formattedMonth + "-" + person.getYear());


        // Calculate and set the remaining days and months until the next birthday
        String nextBirthday = person.calculateTimeUntilNextBirthday();
        holder.nextBdayRow.setText("Next Birthday: " + nextBirthday);



        String ageWithMonthsAndDays = person.calculateAgeWithMonthsAndDays();
        holder.ageTextView.setText("Age: " + ageWithMonthsAndDays);

        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPerson(person);
            }
        });


        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Confirm Deletion");
                alertDialogBuilder.setMessage("Are you sure you want to delete this contact?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        int contactIdToDelete = person.getId();
                        dbHelper.deleteContactById(contactIdToDelete);
                        personList.remove(position);
                        notifyDataSetChanged();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", null);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });



    }



    @Override
    public int getItemCount() {
        return personList.size();
    }

    static class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView dobTextView;
        TextView ageTextView, nextBdayRow;

        ImageView imgDelete, imgEdit;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameRow);
            dobTextView = itemView.findViewById(R.id.dobRow);
            ageTextView = itemView.findViewById(R.id.ageRow);
            nextBdayRow = itemView.findViewById(R.id.nextBdayRow);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            imgEdit = itemView.findViewById(R.id.imgEdit);
        }
    }

    private void editPerson(Person person){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit, null);
        builder.setView(view);

        EditText editName = view.findViewById(R.id.editName);
        EditText editDate = view.findViewById(R.id.editDate);
        EditText editMonth = view.findViewById(R.id.editMonth);
        EditText editYear = view.findViewById(R.id.editYear);
        Button editThisPerson = view.findViewById(R.id.editThisPerson);

        // Populate the edit text fields with existing data

        editName.setText(person.getName());
        editDate.setText(person.getDate());
        editMonth.setText(person.getMonth());
        editYear.setText(person.getYear());

        AlertDialog alertDialog = builder.create();

        editThisPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = editName.getText().toString();
                String newDate = editDate.getText().toString();
                String newMonth = editMonth.getText().toString();
                String newYear = editYear.getText().toString();


                if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newDate) || TextUtils.isEmpty(newMonth) || TextUtils.isEmpty(newYear)){
                    Toast.makeText(context, "Please fill all the details", Toast.LENGTH_LONG).show();
                }


                else {

                    int newIntDate = Integer.parseInt(newDate);
                    int newIntMonth = Integer.parseInt(newMonth);
                    int newIntYear = Integer.parseInt(newYear);

                    // Format the date with an extra zero if it's less than 10
                    String formattedDate = (newIntDate < 10) ? "0" + newIntDate : String.valueOf(newIntDate);

                    // Format the month with an extra zero if it's less than 10
                    String formattedMonth = (newIntMonth < 10) ? "0" + newIntMonth : String.valueOf(newIntMonth);


                    if (newIntDate <= 31 && newIntDate > 0 && newIntMonth <= 12 && newIntMonth > 0 && newIntYear > 0){


                        person.setName(newName);
                        person.setDate(newDate);
                        person.setMonth(newMonth);
                        person.setYear(newYear);

                        dbHelper.updateContact(person);


                        notifyDataSetChanged();

                        sortAndRefreshList();

                        alertDialog.dismiss();

                    }

                    else {
                        Toast.makeText(context, "Invalid Details", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        alertDialog.show();



    }


    public void sortAndRefreshList() {
        if (context instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.sortPersonListByBirthdate();
        }
    }





}
