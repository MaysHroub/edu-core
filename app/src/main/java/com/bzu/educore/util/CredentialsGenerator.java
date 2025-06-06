package com.bzu.educore.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CredentialsGenerator {

    public static int generateStudentId(int currentStudentCount) {
        String year = new SimpleDateFormat("yyyy").format(new Date());
        int nextNum = currentStudentCount + 1;
        String paddedNumber = String.format("%04d", nextNum);
        return Integer.valueOf(year + paddedNumber);
    }

    public static String generateStudentEmail(int studentId) {
        return String.format("%d@student.educore.edu", studentId);
    }

    public static int generateTeacherId(int currentTeacherCount) {
        String year = "1" + new SimpleDateFormat("yyyy").format(new Date()).substring(2);  // 1 + (2025) -> 125
        int nextNum = currentTeacherCount + 1;
        String paddedNumber = String.format("%03d", nextNum);
        return Integer.valueOf(year + paddedNumber);
    }

    public static String generateTeacherEmail(int teacherId) {
        return String.format("%d@teacher.educore.edu", teacherId);
    }

}
