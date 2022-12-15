package com.example.todo;

import android.provider.BaseColumns;
import android.util.Patterns;
import android.widget.EditText;

import java.util.regex.Pattern;

public abstract class CredentialsChecker {

    // Implements credential validation with Regex. (Regular expressions)

    public static class ERROR_CODES implements BaseColumns
    {
        public static String EmptyField = "Field can't be empty.";
        public static class EMAIL implements BaseColumns
        {
            public static String EmailIsUnavailable = "This email has been assigned to a different user, select a new one.";
            public static String EmailIsUsed = "The email matching this user has already been signed into this device.";
            public static String NoSuchEmail = "No user by this email.";
            public static String IncorrectFormat = "Please enter a valid email address.";

        }
        public static class PASSWORD implements BaseColumns
        {
            public static String WrongPass = "Incorrect password.";
            public static String WrongMatch = "Passwords do not match.";
            public static String IncorrectFormat = "Password is too weak.";
            public static int minChar = 6;
            public static final String specialChars= "@#$%^&+=";

            public static class FORMAT implements BaseColumns
            {
                public static String TooShort = "Password must contain at least " + minChar + " characters.";
                public static String NoSmallLetters = "Password must contain at least 1 lower case letter (a-z).";
                public static String NoBigLetters = "Password must contain at least 1 upper case letter (A-Z).";
                public static String NoDigits = "Password must contain at least 1 digit (0–9).";
                public static String NoSpecialSymbol = "Password must contain at least 1 special character ("+ specialChars +").";
            }

        }
        public static class USERNAME implements BaseColumns
        {
            public static String NoEnglishLetters = "Name and surname must be in English characters only";
        }
    }

    private static final Pattern NAME_AND_SURNAME_PATTERN =
            Pattern.compile("^" +
                    "(.*[a-zA-Z])" +      //Must contain English letters only.
                    "$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    //"(?=.*[a-zA-Z])" +      //at least 1 of any letter
                    "(?=.*["+ ERROR_CODES.PASSWORD.specialChars + "])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{"+ ERROR_CODES.PASSWORD.minChar+",}" +               //at least 6 characters, no maximum
                    "$");

    public static boolean validateEmail(EditText email)
    {
        String emailInput = email.getText().toString().trim();

        if(emailInput.isEmpty())
        {
            // Check if field is empty.
            email.setError(ERROR_CODES.EmptyField);
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches())
        {
            // Check if email address does not match valid format.
            email.setError(ERROR_CODES.EMAIL.IncorrectFormat);
            return false;
        }
        return true;
    }

    public static boolean validatePassword(EditText pass)
    {
        String passwordInput = pass.getText().toString().trim();

        if(passwordInput.isEmpty())
        {
            // Check if field is empty.
            pass.setError(ERROR_CODES.EmptyField);
            return false;
        }
        else if(!PASSWORD_PATTERN.matcher(passwordInput).matches())
        {
            // Check if password does not match valid format.
            String error = ERROR_CODES.PASSWORD.IncorrectFormat;
            error += "\n";
            error += ERROR_CODES.PASSWORD.FORMAT.TooShort;
            error += "\n";
            error += ERROR_CODES.PASSWORD.FORMAT.NoBigLetters;
            error += "\n";
            error += ERROR_CODES.PASSWORD.FORMAT.NoSmallLetters;
            error += "\n";
            error += ERROR_CODES.PASSWORD.FORMAT.NoDigits;
            error += "\n";
            error += ERROR_CODES.PASSWORD.FORMAT.NoSpecialSymbol;

            pass.setError(error);
            return false;
        }
        return true;
    }

    public static boolean matchPasswords(EditText pass1, EditText pass2)
    {
        if(pass1.getText().toString().equals(pass2.getText().toString()))
            return true;
        pass1.setError(ERROR_CODES.PASSWORD.WrongMatch);
        pass2.setError(ERROR_CODES.PASSWORD.WrongMatch);
        return false;
    }

    public static boolean validateName(EditText name, EditText surname)
    {
        boolean isValid = true;
        String nameInput = name.getText().toString().trim();
        String surnameInput = surname.getText().toString().trim();
        if(name.getText().toString().isEmpty())
        {
            name.setError(ERROR_CODES.EmptyField);
            isValid = false;
        }
        if(surname.getText().toString().isEmpty())
        {
            surname.setError(ERROR_CODES.EmptyField);
            isValid = false;
        }

        if(!NAME_AND_SURNAME_PATTERN.matcher(nameInput).matches())
        {
            name.setError(ERROR_CODES.USERNAME.NoEnglishLetters);
            isValid = false;
        }

        if(!NAME_AND_SURNAME_PATTERN.matcher(surnameInput).matches())
        {
            surname.setError(ERROR_CODES.USERNAME.NoEnglishLetters);
            isValid = false;
        }
        return isValid;
    }

}