package com.karmon.muthakker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.MutableDouble;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends Activity {

    CheckBox checkBoxAM;
    CheckBox checkBoxPM;
    TimePicker timePickerPM;
    TimePicker timePickerAM;
    public AlarmManager alarmManager,alarmManagerAM,alarmManagerPM;
    Intent alarmIntent;
    PendingIntent pendingIntent;
    SharedPreferences times;
    Button saveButton;
    NumberPicker numberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //============== Shared Prefs =====================//
        times = getSharedPreferences("TIMER", 0);
        times = getSharedPreferences("AM_H_ALARM", 0);
        times = getSharedPreferences("AM_M_ALARM", 0);
        times = getSharedPreferences("PM_H_ALARM", 0);
        times = getSharedPreferences("PM_M_ALARM", 0);

        //============== Save Button =====================//
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = times.edit();
                editor.putInt("TIMER", numberPicker.getValue());
                if (checkBoxAM.isChecked()) {
                    editor.putInt("AM_H_ALARM", timePickerAM.getCurrentHour());
                    editor.putInt("AM_M_ALARM", timePickerAM.getCurrentMinute());
                } else {
                    editor.remove("AM_H_ALARM");
                    editor.remove("AM_M_ALARM");
                }
                if (checkBoxPM.isChecked()) {
                    editor.putInt("PM_H_ALARM", timePickerPM.getCurrentHour());
                    editor.putInt("PM_M_ALARM", timePickerPM.getCurrentMinute());
                } else {
                    editor.remove("PM_H_ALARM");
                    editor.remove("PM_M_ALARM");
                }
                editor.commit();

                setAlarm();
                Log.i("AM_H_ALARM",timePickerPM.getCurrentHour().toString());
                Toast.makeText(getApplicationContext(),"تم الحفظ بنجاح",Toast.LENGTH_LONG).show();
            }
        });

        //============== Athkar Button =====================//
        Button athkarButton = (Button)findViewById(R.id.duaButton);
        athkarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent athkarIntent = new Intent(MainActivity.this,AthkarSabah.class);
                startActivity(athkarIntent);
            }
        });

        //============== Share Button =====================//
        Button shareButton = (Button)findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "أدعوكم لتجربة تطبيق المذكر ليعلي صوت أجهزتكم بذكر الله https://play.google.com/store/apps/details?id=com.karmon.muthakker");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent,"شارك التطبيق عبر :"));
            }
        });

        //============== Number Picker For Minutes =====================//
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(60);
        numberPicker.setValue(times.getInt("TIMER", 5));

        //============== Time Pickers For Azkar Alarm =====================//
        timePickerAM = (TimePicker) findViewById(R.id.timePickerAM);
        timePickerPM = (TimePicker) findViewById(R.id.timePickerPM);
        if (times.getInt("AM_H_ALARM", 70) != 70) {
            timePickerAM.setVisibility(View.VISIBLE);
        }
        if (times.getInt("PM_H_ALARM", 70) != 70) {
            timePickerPM.setVisibility(View.VISIBLE);
        }
        timePickerAM.setCurrentHour(times.getInt("AM_H_ALARM", 5));
        timePickerAM.setCurrentMinute(times.getInt("AM_M_ALARM", 0));
        timePickerPM.setCurrentHour(times.getInt("PM_H_ALARM", 17));
        timePickerPM.setCurrentMinute(times.getInt("PM_M_ALARM", 0));


        //============== CheckBoxes & their actions =========//
        checkBoxAM = (CheckBox) findViewById(R.id.checkBoxAM);
        checkBoxAM.setOnCheckedChangeListener(new checkBoxListener());
        checkBoxPM = (CheckBox) findViewById(R.id.checkBoxPM);
        checkBoxPM.setOnCheckedChangeListener(new checkBoxListener());
        if (times.getInt("AM_H_ALARM", 70) != 70) {
            checkBoxAM.setChecked(true);
        }
        if (times.getInt("PM_H_ALARM", 70) != 70) {
            checkBoxPM.setChecked(true);
        }
        checkDB();
    }

    private void setAlarm() {
        long dailyMS = 24 * 60 * 60 * 1000;

        //============== Repeating Alarm tools ==============//
        Calendar alarmStartTime = Calendar.getInstance();
        int i = times.getInt("TIMER", 5);
        long repeatMS = i * 60000;
        alarmStartTime.add(Calendar.MINUTE, i);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);
        alarmManager.setRepeating(AlarmManager.RTC, alarmStartTime.getTimeInMillis(), repeatMS, pendingIntent);

        //============== AM Alarm tools ==============//
        alarmStartTime = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY,times.getInt("AM_H_ALARM", 7));
        alarmStartTime.set(Calendar.MINUTE,times.getInt("AM_M_ALARM", 0));
        alarmStartTime.set(Calendar.SECOND, 0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 1, alarmIntent, 0);
        if (checkBoxAM.isChecked()) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), dailyMS, pendingIntent);
        }else{
            alarmManager.cancel(pendingIntent);
        }

        //============== PM Alarm tools ==============//
        alarmStartTime = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY,times.getInt("PM_H_ALARM", 7));
        alarmStartTime.set(Calendar.MINUTE,times.getInt("PM_M_ALARM", 0));
        alarmStartTime.set(Calendar.SECOND,0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 2, alarmIntent, 0);
        if (checkBoxPM.isChecked()) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), dailyMS, pendingIntent);
        }else{
            alarmManager.cancel(pendingIntent);
        }

        Log.i("KARMON", "Alarm is set " + String.valueOf(i));
    }

    private  void checkDB(){
        SQLiteDatabase database;
        database = openOrCreateDatabase("MuthakkerDB", Context.MODE_PRIVATE,null);
        database.execSQL("Create Table If Not Exists Ver (ID INTEGER PRIMARY KEY, ver int)");
        database.execSQL("Create Table If Not Exists ATHKAR (ID INTEGER PRIMARY KEY, MSG TEXT, FILENAME TEXT)");
        database.execSQL("Create Table If Not Exists ATHKARMASAA (ID INTEGER PRIMARY KEY, Thekr TEXT, COUNTS INT)");
        database.execSQL("Create Table If Not Exists ATHKARSABAH (ID INTEGER PRIMARY KEY, Thekr TEXT, COUNTS INT)");
        int c;

        Cursor ver = database.rawQuery("Select * from Ver",null);
        if(ver.getCount() == 0) {
            c = 4;
        }
        else{
            ver.moveToLast();
            c = ver.getInt(1);
        }

        if(c < 5) {

            // ================== Duaa Check ===================== //

                database.execSQL("Drop Table ATHKAR");
                database.execSQL("Create Table If Not Exists ATHKAR (ID INTEGER PRIMARY KEY, MSG TEXT, FILENAME TEXT)");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('لاإله إلا الله','1')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('سبحان الله وبحمده','2')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('استغفر الله وأتوب إليه','3')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('صلي على محمد','4')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم أنت الحليم الذي لا يعجل ، وأنت الكريم الذي لا يبخل','5')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم أرزقنا برآبائنا وأمهاتنا وبر أبنائنا بنا','6')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم ارفع عنا الغلا والوبا والزنا والربا والزلازل والمحن','7')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم أرنا الحق حقا وارزقنا اتباعه وأرنا الباطل باطلا وارزقنا اجتنابه برحمتك يا أرحم الراحمين','8')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم اعتق رقابنا من النار','9')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم أعز الإسلام والمسلمين','10')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم اغفر لآبائنا وأمهاتنا كما ربونا صغارا','11')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم اغفر للمسلمين والمسلمات والمؤمنين والمؤمنات الأحياء منهم والأموات','12')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم اقسم لنا من خشيتك ما تحول به بيننا وبين معصيتك','13')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم إنا نشهد أنك أنت الله لا إله إلا أنت','14')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم انصر الإسلام وأهله في كل مكان','15')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم انصر دينك وكتابك وعبادك الموحدين','16')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم إنك عفو تحب العفو فاعف عنا','17')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم اهدنا فيمن هديت وعافنا فيمن عافيت وتولنا فيمن توليت','18')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم بك أسلمنا وبك آمنا وعليك توكلنا','19')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم صلي على محمد وعلى آل محمد كما صليت على إبراهيم وآل إبراهيم إنك حميد مجيد','20')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم صلي وسلم وأنعم على نبيك محمد وعلى آله وأصحابه والتابعين','21')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم لا تدع لنا ذنبا إلا غفرته','22')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم لك الحمد على كل نعمة أنعمت بها علينا في قديم أو حديث','23')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم لك الحمد على نعمة الإسلام','24')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('اللهم يا حي يا قيوم برحمتك نستغيث فأصلح لنا شأننا كله ولا تكلنا إلى نفسنا طرفة عين أو أقل من ذلك','25')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('سبحان من خلق الخلق وأحصاهم عددا','26')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('سبحان من يسبح الرعد بحمده والملائكة من خيفته','27')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('لا إله إلا الله','28')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('لا إله إلا أنت سبحانك إنا كنا من الظالمين','29')");
                database.execSQL("Insert Into ATHKAR(msg,filename) values('لاإله إلا الله','1')");
                Log.i("DATABASE", "Data insert is DONE");

            // ================== Athkar Sabah Check ===================== //
                database.execSQL("Drop Table ATHKARSABAH");
                database.execSQL("Create Table If Not Exists ATHKARSABAH (ID INTEGER PRIMARY KEY, Thekr TEXT, COUNTS INT)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('الحمد لله وحده ، والصلاة والسلام على من لا نبي بعده',1)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('بسم الله الرحمن الرحيم (الله لا إلهَ إلا هوَ الحيُّ القَيُّوم لا تأخُذُهّ سِنَةٌ ولا نَومٌ لهُ ما في السَّمَاواتِ وما في الأَرضِ من ذَا الَّذي يَشفَعُ عندَهُ إلا بِإِذنِهِ يَعلَمُ ما بينَ أيدِيهم وما خَلفَهُم ولا يُحِيطُونَ بِشَيءٍ من عِلمِهِ إِلا بِما شَاء وَسِعَ كُرسيُّهُ السَّماواتِ والأرضَ ولا يَؤُودُهُ حِفظُهُما وَهُوَ العلِيَّ العَظِيمَ)',1)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('بسم الله الرحمن الرحيم (قُل هُوَ اللهُ أَحَدُ * اللهُ الصَّمدُ * لَم يَلِد ولَم يُولَد * ولَم يَكُن لَهُ كُفُواً أَحَد)  بسم الله الرحمن الرحيم (قُل أعُوذُ بِرَبِّ الفَلَقِ * من شَرِّ ما خَلَقَ * ومِن شرِّ غاسِقٍ إذا وقَبَ * ومن شَرِّ النَّفَّاثَاتِ في العُقَدِ * ومن شَرِّ حاسِدٍ إذا حَسَدَ) بسم الله الرحمن الرحيم ( قُل أَعوذُ بِربِّ النَّاسِ * مَلِكِ النَّاسِ * إلَهِ النَّاسِ * من شَرِّ الوَسوَاسِ الخَنَّاسِ * الَّذي يُوَسوِسُ في صُدُورِ النَّاسِ * مِنَ الجِنَّةِ و النَّاسِ)',3)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('أصبحنا وأصبح الملك لله والحمد لله ، لا إله إلا الله وحده لا شريك له، له الملك وله الحمد وهو على كل شيء قدير ، ربِّ أسألك خير هذا اليوم وخير ما بعده ، وأعوذ بك من شر ما في هذا اليوم وشر ما بعده، رب أعوذ بك من الكسل وسوء الكِبر ، رب أعوذ بك من عذاب في النار وعذاب في القبر',1)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('اللهم بك أصبحنا ، وبك أمسينا ، وبك نحيا وبك نموت وإليك النشور',1)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('اللهم أنت ربي لا إله إلا أنت ، خلقتني وأنا عبدك ، وأنا على عهدك ووعدك ما استطعت ، أعوذ بك من شر ما صنعت ، أبوء لك بنعمتك علي وأبوء بذنبي ، فاغفر لي فإنه لا يغفر الذنوب إلا أنت',1)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('اللهم إني أصبحت أشهدك وأشهد حملة عرشك ، وملائكتك وجميع خلقك ، أنك أنت الله لا إله إلا أنت وحدك لا شريك لك ، وأن محمدا عبدك ورسولك',4)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('اللهم ما أصبح بي من نعمة أو بأحد من خلقك فمنك وحدك لا شريك لك ، فلك الحمد ولك الشكر',1)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('اللهم عافني في بدني ، اللهم عافني في سمعي ، اللهم عافني في بصري ، لا إله إلا أنت، اللهم إني أعوذ بك من الكفر والفقر ، وأعوذ بك من عذاب القبر ، لا إله إلا أنت',3)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('حسبي الله لا إله إلا هو عليه توكلت وهو رب العرش العظيم',7)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('اللهم إني أسألك العفو والعافية في الدنيا والآخرة ، اللهم إني أسألك العفو والعافية في ديني ودنياي ، وأهلي ومالي ، اللهم استر عوراتي ، وآمن روعاتي ، اللهم احفظني من بين يدي ومن خلفي ، وعن يميني وعن شمالي ، ومن فوقي ، وأعوذ بعظمتك أن أغتال من تحتي',1)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('اللهم عالم الغيب والشهادة ، فاطر السماوات والأرض ، رب كل شيءٍ ومليكه ، أشهد أن لا إله إلا أنت ، أعوذ بك من شر نفسي ومن شر الشيطان وشَرَكِه ، وأن أقترف على نفسي سوءاً أو أجره إلى مسلم',1)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('بسم الله الذي لا يضر مع اسمه شيء في الأرض ولا في السماء ، وهو السميع العليم',3)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('رضيت بالله رباً ، وبالإسلام ديناً ، وبمحمد صلى الله عليه وسلم نبياً',3)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('يا حيُّ يا قيُّوم برحمتك أستغيث ، فأصلح لي شأني كله ، ولا تكلني إلى نفسي طرفة عين',1)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('أصبحنا وأصبح الملك لله رب العالمين ، اللهم إني أسألك خير هذا اليوم ، فتحه ، ونصره ، ونوره ، وبركته ، وهداه ، وأعوذ بك من شر ما فيه ، وشر ما بعده',1)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('أصبحنا على فطرة الإسلام ، وعلى كلمة الإخلاص ، وعلى دين نبينا محمد صلى الله عليه وسلم ، وعلى ملة أبينا إبراهيم حنيفا مسلما وما كان من المشركين',1)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('سبحان الله وبحمده',100)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('لا إله إلا الله وحده لا شريك له ، له الملك وله الحمد ، وهو على كل شيءٍ قدير',100)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('سبحان الله وبحمده ، عدد خلقه ، ورضا نفسه ، وزنة عرشه ، ومداد كلماته',3)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('اللهم إني أسألك علما نافعا ، ورزقا طيبا ، وعملا متقبلا',1)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('أستغفر الله وأتوب إليه',100)");
                database.execSQL("Insert Into ATHKARSABAH(Thekr,COUNTS) values('اللهم صلِّ وسلم على نبينا محمد',10)");
                Log.i("DATABASE", "Data insert is DONE");


            // ================== Athkar Masaa Check ===================== //

                database.execSQL("Drop Table ATHKARMASAA");
                database.execSQL("Create Table If Not Exists ATHKARMASAA (ID INTEGER PRIMARY KEY, Thekr TEXT, COUNTS INT)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('الحمد لله وحده ، والصلاة والسلام على من لا نبي بعده',1)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('بسم الله الرحمن الرحيم (الله لا إلهَ إلا هوَ الحيُّ القَيُّوم لا تأخُذُهّ سِنَةٌ ولا نَومٌ لهُ ما في السَّمَاواتِ وما في الأَرضِ من ذَا الَّذي يَشفَعُ عندَهُ إلا بِإِذنِهِ يَعلَمُ ما بينَ أيدِيهم وما خَلفَهُم ولا يُحِيطُونَ بِشَيءٍ من عِلمِهِ إِلا بِما شَاء وَسِعَ كُرسيُّهُ السَّماواتِ والأرضَ ولا يَؤُودُهُ حِفظُهُما وَهُوَ العلِيَّ العَظِيمَ)',1)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('بسم الله الرحمن الرحيم (قُل هُوَ اللهُ أَحَدُ * اللهُ الصَّمدُ * لَم يَلِد ولَم يُولَد * ولَم يَكُن لَهُ كُفُواً أَحَد)  بسم الله الرحمن الرحيم (قُل أعُوذُ بِرَبِّ الفَلَقِ * من شَرِّ ما خَلَقَ * ومِن شرِّ غاسِقٍ إذا وقَبَ * ومن شَرِّ النَّفَّاثَاتِ في العُقَدِ * ومن شَرِّ حاسِدٍ إذا حَسَدَ) بسم الله الرحمن الرحيم ( قُل أَعوذُ بِربِّ النَّاسِ * مَلِكِ النَّاسِ * إلَهِ النَّاسِ * من شَرِّ الوَسوَاسِ الخَنَّاسِ * الَّذي يُوَسوِسُ في صُدُورِ النَّاسِ * مِنَ الجِنَّةِ و النَّاسِ)',3)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('أمسينا وأمسى الملك لله والحمد لله ، لا إله إلا الله وحده لا شريك له، له الملك وله الحمد وهو على كل شيء قدير ، ربِّ أسألك خير هذه الليلة وخير ما بعدها ، وأعوذ بك من شر ما في هذه الليلة وشر ما بعدها، رب أعوذ بك من الكسل وسوء الكِبر ، رب أعوذ بك من عذاب في النار وعذاب في القبر',1)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('اللهم بك أصبحنا ، وبك أمسينا ، وبك نحيا وبك نموت وإليك المصير',1)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('اللهم أنت ربي لا إله إلا أنت ، خلقتني وأنا عبدك ، وأنا على عهدك ووعدك ما استطعت ، أعوذ بك من شر ما صنعت ، أبوء لك بنعمتك علي وأبوء بذنبي ، فاغفر لي فإنه لا يغفر الذنوب إلا أنت',1)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('اللهم إني أمسيت أشهدك وأشهد حملة عرشك ، وملائكتك وجميع خلقك ، أنك أنت الله لا إله إلا أنت وحدك لا شريك لك ، وأن محمدا عبدك ورسولك',4)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('اللهم ما أمسى بي من نعمة أو بأحد من خلقك فمنك وحدك لا شريك لك ، فلك الحمد ولك الشكر',1)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('اللهم عافني في بدني ، اللهم عافني في سمعي ، اللهم عافني في بصري ، لا إله إلا أنت، اللهم إني أعوذ بك من الكفر والفقر ، وأعوذ بك من عذاب القبر ، لا إله إلا أنت',3)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('حسبي الله لا إله إلا هو عليه توكلت وهو رب العرش العظيم',7)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('اللهم إني أسألك العفو والعافية في الدنيا والآخرة ، اللهم إني أسألك العفو والعافية في ديني ودنياي ، وأهلي ومالي ، اللهم استر عوراتي ، وآمن روعاتي ، اللهم احفظني من بين يدي ومن خلفي ، وعن يميني وعن شمالي ، ومن فوقي ، وأعوذ بعظمتك أن أغتال من تحتي',1)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('اللهم عالم الغيب والشهادة ، فاطر السماوات والأرض ، رب كل شيءٍ ومليكه ، أشهد أن لا إله إلا أنت ، أعوذ بك من شر نفسي ومن شر الشيطان وشَرَكِه ، وأن أقترف على نفسي سوءاً أو أجره إلى مسلم',1)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('بسم الله الذي لا يضر مع اسمه شيء في الأرض ولا في السماء ، وهو السميع العليم',3)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('رضيت بالله رباً ، وبالإسلام ديناً ، وبمحمد صلى الله عليه وسلم نبياً',3)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('يا حيُّ يا قيُّوم برحمتك أستغيث ، فأصلح لي شأني كله ، ولا تكلني إلى نفسي طرفة عين',1)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('أمسينا وأمسى الملك لله رب العالمين ، اللهم إني أسألك خير هذه الليلة ، فتحها ، ونصرها ، ونورها ، وبركتها ، وهداها ، وأعوذ بك من شر ما فيها ، وشر ما بعدها',1)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('أمسينا على فطرة اإسلام ، وعلى كلمة الإخلاص ، وعلى دين نبينا محمد صلى الله عليه وسلم ، وعلى ملة أبينا إبراهيم حنيفا مسلم وما كان من المشركين',1)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('سبحان الله وبحمده',100)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('لا إله إلا الله وحده لا شريك له ، له الملك وله الحمد ، وهو على كل شيءٍ قدير',10)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('أستغفر الله وأتوب إليه',100)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('أعوذ بكلمات الله التامات من شر ما خلق',1)");
                database.execSQL("Insert Into ATHKARMASAA(Thekr,COUNTS) values('اللهم صلِّ وسلم على نبينا محمد',10)");
                Log.i("DATABASEMasaa", "Data insert is DONE");



            database.execSQL("Insert Into Ver(ver) values(5)");

        }
        Log.i("DATABASE","Data Check is DONE");
    }

    class checkBoxListener implements CheckBox.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (buttonView == checkBoxAM) {
                    timePickerAM.setVisibility(View.VISIBLE);
                }
                if (buttonView == checkBoxPM) {
                    timePickerPM.setVisibility(View.VISIBLE);
                }
            } else {
                if (buttonView == checkBoxAM) {
                    timePickerAM.setVisibility(View.GONE);
                }
                if (buttonView == checkBoxPM) {
                    timePickerPM.setVisibility(View.GONE);
                }
            }
        }
    }


}
