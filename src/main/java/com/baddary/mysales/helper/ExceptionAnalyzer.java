package com.baddary.mysales.helper;

public class ExceptionAnalyzer {

    private ExceptionAnalyzer(){}

    public static String translate(Throwable ex) {
        String message = ex.toString();
        if(message.contains("23505")){
            return analyzeUniqueConstraint(message);
        }else if(message.contains("23503")){
            return "Cannot delete or update due because related data exists";
        } else if (message.contains("23502")) {
            return "Required field is missing";
        }
        return "Unexpected database error\n" + message;
    }

    private static String analyzeUniqueConstraint(String message) {

        if (message.contains("PHONES")) {
            return "This phone number already exists";
        }

        if (message.contains("CUSTOMERS")) {
            return "Customer already exists";
        }

        return "Duplicate value exists";
    }
}
