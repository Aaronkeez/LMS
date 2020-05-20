package com.thetravella.librarymanagementsystem.FirebaseFunctions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CoreFunctions {

    public String getDateToday() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(date);
    }

    public String getReturnDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar currentCal = Calendar.getInstance();
        String currentdate = dateFormat.format(currentCal.getTime());
        currentCal.add(Calendar.DATE, 7);
        String returnDate = dateFormat.format(currentCal.getTime());
        return returnDate;
    }

}
