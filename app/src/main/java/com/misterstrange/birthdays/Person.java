package com.misterstrange.birthdays;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class Person {

    int id;
    private String name;
    private String date;
    private String month;
    private String year;

    public Person(int id, String name, String date, String month, String year) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.month = month;
        this.year = year;
    }

    public Person(String name, String date, String month, String year) {
        this.name = name;
        this.date = date;
        this.month = month;
        this.year = year;
    }

    public Person (){

    }

    public String calculateAgeWithMonthsAndDays() {
        Calendar today = Calendar.getInstance();
        Calendar birthdate = Calendar.getInstance();

        String dob = date + "-" + month + "-" + year;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        try {
            birthdate.setTime(dateFormat.parse(dob));
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Handle the error as needed
        }

        int years = today.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR);
        int months = today.get(Calendar.MONTH) - birthdate.get(Calendar.MONTH);
        int days = today.get(Calendar.DAY_OF_MONTH) - birthdate.get(Calendar.DAY_OF_MONTH);

        if (months < 0 || (months == 0 && days < 0)) {
            years--;
            months = 12 - birthdate.get(Calendar.MONTH) + today.get(Calendar.MONTH);
            if (days < 0) {
                months--;
                days = birthdate.getActualMaximum(Calendar.DAY_OF_MONTH) - birthdate.get(Calendar.DAY_OF_MONTH) + today.get(Calendar.DAY_OF_MONTH);
            }
        }

        if (days < 0) {
            days += today.getActualMaximum(Calendar.DAY_OF_MONTH);
            months--;
        }

        return years + " years, " + months + " months, " + days + " days";

    }

    public String calculateTimeUntilNextBirthday() {
        Calendar today = Calendar.getInstance();
        Calendar birthdate = Calendar.getInstance();

        String dob = date + "-" + month + "-" + year;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        try {
            birthdate.setTime(dateFormat.parse(dob));
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Handle the error as needed
        }

        // Set the birthdate's year to the current year to calculate the next birthday
        birthdate.set(Calendar.YEAR, today.get(Calendar.YEAR));

        // Check if the birthday has already occurred this year
        if (birthdate.before(today)) {
            birthdate.set(Calendar.YEAR, today.get(Calendar.YEAR) + 1); // Set it to next year
        }

        long timeInMillis = birthdate.getTimeInMillis() - today.getTimeInMillis();
        long daysUntilNextBirthday = timeInMillis / (24 * 60 * 60 * 1000); // Milliseconds to days

        // Calculate the number of months from days
        long monthsUntilNextBirthday = daysUntilNextBirthday / 30; // Assuming an average month length of 30 days

        return monthsUntilNextBirthday + " months, " + daysUntilNextBirthday % 30 + " days";
    }

    public static final Comparator<Person> BIRTHDAY_COMPARATOR = new Comparator<Person>() {
        @Override
        public int compare(Person person1, Person person2) {
            // Calculate the remaining time until the next birthday for each person
            String birthday1 = person1.calculateTimeUntilNextBirthday();
            String birthday2 = person2.calculateTimeUntilNextBirthday();

            // Split the strings into months and days
            String[] parts1 = birthday1.split(" ");
            String[] parts2 = birthday2.split(" ");

            int months1 = Integer.parseInt(parts1[0]);
            int months2 = Integer.parseInt(parts2[0]);

            // Compare months, and if they are equal, compare days
            if (months1 != months2) {
                return Integer.compare(months1, months2);
            } else {
                int days1 = Integer.parseInt(parts1[2]);
                int days2 = Integer.parseInt(parts2[2]);
                return Integer.compare(days1, days2);
            }
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
