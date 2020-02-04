package com.example.universalinterpreter;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class Fundamental_Converter {

    String input, output, output_format;
    Context context;
    Vibrator v;
    int count = 0;
    TextToSpeech textToSpeech;


    public Fundamental_Converter(String input, String output_format, final Context context) {

        this.input = input;
        this.output_format = output_format;
        this.context = context;

        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (output_format.equals("Text")) {

        } else if (output_format.equals("Morse")) {
            count = 0;
            output = convtomorse(input, "");
            Morse_Converter(output.charAt(count));
        } else if (output_format.equals("Speech")) {
            output = input;
            Speech_Converter(input);
        }

    }


    public void Morse_Converter(char input) {

        try {

            if (input == '_') {
                // Vibrate for 2000 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(1000);
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        Morse_Converter(output.charAt(++count));
                    }
                }, 1000);
            } else if (input == '.') {
                // Vibrate for 1000 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        Morse_Converter(output.charAt(++count));
                    }
                }, 500);
            }
            if (input == ' ') {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        Morse_Converter(output.charAt(++count));
                    }
                }, 500);
            }

        } catch (StringIndexOutOfBoundsException e) {
            return;
        }


    }

    public void Speech_Converter(final String input) {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!= TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                    textToSpeech.speak(input, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

    String convtomorse(String word, String morse) {

        int strpos = 0;
        while (strpos <= word.length() - 1) {
            morse = morse + signtomorse(word.charAt(strpos));
            strpos += 1;
          }
        return morse.trim();
    }

    String signtomorse(char sign) {
        String code = tomorse(Character.toUpperCase(sign));
        return (code + "   ");
    }

    String tomorse(char character) {

        switch (character) {
            case 'A' : return ". _";
            case 'B' : return "_ . . .";
            case 'C' : return "_ . _ .";
            case 'D' : return "_ . .";

case                     'E' : return ".";

case                     'F' : return ". . _ .";

case                     'G' : return "_ _ .";

case                     'H' : return ". . . .";

case                     'I' : return ". .";

case                     'J' : return ". _ _ _";

case                     'K' : return "_ . _";

case                     'L' : return ". _ . .";

case                    'M' : return "_ _";

case                     'N' : return "_ .";

case                     'O' : return "_ _ _";

case                     'P' : return ". _ _ .";

case                     'Q' : return "_ _ . _";

case                     'R' : return ". _ .";

case                     'S' : return ". . .";

case                     'T' : return "_";

case                     'U' : return ". . _";

case                     'V' : return ". . . _";

case                     'W' : return ". _ _";

case                     'X' : return "_ . . _";

case                     'Y' : return "_ . _ _";

case                     'Z' : return "_ _ . .";

case                     'Å' : return ". _ _ . _";

case                     'Ä' : return ". _ . _";

case                     'Ö' : return "_ _ _ .";

case                     'É' : return ". . _ . .";

case                     'Ñ' : return "_ _ . _ _";


case                     'Ü' : return ". . _ _";

case                     'Û' : return ". . _ _";

case                     'Š' : return "_ _ _ _";

case                     'ß' : return ". . . _ _ . .";

case                     'Ç' : return "_ . _ . .";

case                     'Ŝ' : return "_ . _ . .";

case                     '1' : return ". _ _ _ _";

case                     '2' : return ". . _ _ _";

case                     '3' : return ". . . _ _";

case                     '4' : return ". . . . _";

case                     '5' : return ". . . . .";

case                     '6' : return "_ . . . .";

case                     '7' : return "_ _ . . .";

case                     '8' : return "_ _ _ . .";

case                     '9' : return "_ _ _ _ .";

case                     '0' : return "_ _ _ _ _";

case                     '?' : return ". . _ _ . .";

case               '!' : return ". . _ _ .";

case               ',' : return "_ _ . . _ _";

case               '.' : return ". _ . _ . _";

case               '=' : return "_ . . . _";

case               '-' : return "_ . . . . _";

case               '(' : return "_ . _ _ .";

case               ')' : return "_ . _ _ . _";

case               '¤' : return ". . . . . . . .";

case               '+' : return ". _ . _ .";

case               'Æ' : return ". . . _ . _";

case              '@' : return ". _ _ . _ .";

case              '/' : return "_ . . _ .";

case               '%' : return ". _ _ . .";

case              '"' : return ". _ . . _ .";

case               ';' : return "_ . _ . _ .";

case               ':' : return "_ _ _ . . .";

case               '§' : return ". . . _ .";

case               '¿' : return ". . _ . _";

case               '~' : return "_ . _ . _";

case              '\'' : return ". _ _ _ _ .";

case               '#' : return ". _ . . _";

case                     '&' : return ". _ . . .";

case               '$' : return ". . . _ . . _";

case               '√' : return ". _ . . .";

case               '*' : return ". .   . .";

case               '¡' : return "_ _ . . . _";

case             ' ' : return "       ";
        }

        return "";
    }



}
