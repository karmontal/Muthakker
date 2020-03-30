package com.karmon.muthakker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * Created by karmo on 18/02/2016.
 */
public class AthkarSabah  extends Activity{

    TextView thekrText,thekrTypText;
    Button thekrButton, nextButton, prevButton;
    ImageButton amButton, pmButton;
    int i;
    Vibrator vib;
    SharedPreferences thekrType;
    Cursor allrows;
    SQLiteDatabase database;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.athkar_sabah);
        vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        thekrType = getSharedPreferences("TYP", 0);
        editor = thekrType.edit();

        amButton = (ImageButton)findViewById(R.id.amimageButton);
        pmButton = (ImageButton)findViewById(R.id.pmimageButton);
        thekrText = (TextView)findViewById(R.id.thekrText);
        thekrTypText = (TextView)findViewById(R.id.thekrTypText);
        thekrButton = (Button)findViewById(R.id.thekrButton);
        nextButton = (Button)findViewById(R.id.nextthekrButton);
        prevButton = (Button)findViewById(R.id.prevthekrButton);
        i = 1;


        database = openOrCreateDatabase("MuthakkerDB", Context.MODE_PRIVATE,null);

        if (thekrType.getInt("TYP",1) == 1){
            allrows = database.rawQuery("Select * from ATHKARSABAH",null);
                amButton.setBackgroundResource(R.drawable.amon);
                pmButton.setBackgroundResource(R.drawable.pmoff);
            thekrTypText.setText("أذكار الصباح");
        }else {
            allrows = database.rawQuery("Select * from ATHKARMASAA",null);
                amButton.setBackgroundResource(R.drawable.amoff);
                pmButton.setBackgroundResource(R.drawable.pmon);
            thekrTypText.setText("أذكار المساء");
        }
        allrows.moveToFirst();

        thekrText.setText(allrows.getString(1));
        thekrButton.setText(String.valueOf(i) + " / " + allrows.getString(2));

        thekrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == allrows.getInt(2)) {
                    if (!allrows.isLast()) {
                        allrows.moveToNext();
                        thekrText.startAnimation(AnimationUtils.loadAnimation(AthkarSabah.this, R.anim.slide_out));
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                thekrText.setText(allrows.getString(1));
                                i = 1;
                                thekrButton.setText(String.valueOf(i) + " / " + allrows.getString(2));
                                thekrText.startAnimation(AnimationUtils.loadAnimation(AthkarSabah.this, R.anim.slide_from_right));
                                vib.vibrate(400);
                            }
                        }, 400);

                    } else {
                        finish();
                    }
                } else {
                    i++;
                    thekrText.startAnimation(AnimationUtils.loadAnimation(AthkarSabah.this, R.anim.slide_out));
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            thekrButton.setText(String.valueOf(i) + " / " + allrows.getString(2));
                            thekrText.startAnimation(AnimationUtils.loadAnimation(AthkarSabah.this, R.anim.slide_from_right));
                            vib.vibrate(75);
                        }
                    }, 400);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!allrows.isLast()) {
                    allrows.moveToNext();
                    i = 1;
                    thekrText.startAnimation(AnimationUtils.loadAnimation(AthkarSabah.this, R.anim.slide_out));
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            thekrText.setText(allrows.getString(1));
                            thekrButton.setText(String.valueOf(i) + " / " + allrows.getString(2));
                            thekrText.startAnimation(AnimationUtils.loadAnimation(AthkarSabah.this, R.anim.slide_from_right));
                            vib.vibrate(400);
                        }
                    }, 400);
                } else {
                    finish();
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!allrows.isFirst()) {
                    allrows.moveToPrevious();
                    i = 1;
                    thekrText.startAnimation(AnimationUtils.loadAnimation(AthkarSabah.this, R.anim.slide_out));
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            thekrText.setText(allrows.getString(1));
                            thekrButton.setText(String.valueOf(i) + " / " + allrows.getString(2));
                            thekrText.startAnimation(AnimationUtils.loadAnimation(AthkarSabah.this, R.anim.slide_from_left));
                            vib.vibrate(400);
                        }
                    }, 400);
                } else {
                    finish();
                }
            }
        });

        amButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allrows = database.rawQuery("Select * from ATHKARSABAH", null);
                amButton.setBackgroundResource(R.drawable.amon);
                pmButton.setBackgroundResource(R.drawable.pmoff);
                thekrTypText.setText("أذكار الصباح");
                thekrTypText.startAnimation(AnimationUtils.loadAnimation(AthkarSabah.this, R.anim.slide_from_right));
                editor.putInt("TYP", 1);
                editor.commit();
                allrows.moveToFirst();
                i = 1;
                thekrText.setText(allrows.getString(1));
                thekrButton.setText(String.valueOf(i) + " / " + allrows.getString(2));
            }
        });

        pmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allrows = database.rawQuery("Select * from ATHKARMASAA",null);
                    amButton.setBackgroundResource(R.drawable.amoff);
                    pmButton.setBackgroundResource(R.drawable.pmon);
                thekrTypText.setText("أذكار المساء");
                thekrTypText.startAnimation(AnimationUtils.loadAnimation(AthkarSabah.this, R.anim.slide_from_left));
                editor.putInt("TYP", 2);
                editor.commit();
                allrows.moveToFirst();
                i = 1;
                thekrText.setText(allrows.getString(1));
                thekrButton.setText(String.valueOf(i) + " / " + allrows.getString(2));
            }
        });
    }
}
