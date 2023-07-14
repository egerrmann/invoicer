package com.example.demo.services;

import com.example.demo.models.exceptions.IncorrectDataException;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateService {
    // TODO Add different date formats
    public static Long dateToTimestampInSecs(String stringDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        // TODO Decide what timezone should be used
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        Date date;
        try {
            date = dateFormat.parse(stringDate);
        } catch (ParseException e) {
            throw new IncorrectDataException("Wrong date format, " +
                    "use dd-MM-yyyy instead", HttpStatus.BAD_REQUEST);
        }

        Timestamp timestamp = new Timestamp(date.getTime());
        return timestamp.getTime() / 1000;
    }
}
