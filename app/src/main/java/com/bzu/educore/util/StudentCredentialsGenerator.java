package com.bzu.educore.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StudentCredentialsGenerator {

    public int generateId(int currentStudentCount) {
        String year = new SimpleDateFormat("yyyy").format(new Date());
        int nextNum = currentStudentCount + 1;
        String paddedNumber = String.format("%04d", nextNum);
        return Integer.valueOf(year + paddedNumber);
    }

    public String generateEmail(int studentId) {
        return String.format("%d@student.educore.edu", studentId);
    }

}
