package com.example.timetable.Exam;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timetable.Class.ClassDateFragment;
import com.example.timetable.ColorPicker;
import com.example.timetable.Database.DBHandler;
import com.example.timetable.Database.SubjectMaster;
import com.example.timetable.Homework.displayHomework;
import com.example.timetable.OptionsMenu;
import com.example.timetable.R;
import com.example.timetable.ReminderBroadcast;
import com.example.timetable.SelectDateFragment;
import com.example.timetable.SelectTimeFragement;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link exam#newInstance} factory method to
 * create an instance of this fragment.
 */
public class exam extends Fragment {

    EditText title,note,location;
    Button save;
    TextView stime,etime;
    Spinner subject,reminder;
    ColorStateList col;
    DBHandler db;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DBHandler(getActivity().getApplicationContext());
//        db.create();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_exam, container, false);

        final ImageView addIcon = view.findViewById(R.id.addIcon);
        final  ImageView calendaricon = view.findViewById(R.id.calendarIcon);
        LayoutInflater layoutInflater= (LayoutInflater)getActivity().getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.list_popup, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,450, ViewGroup.LayoutParams.WRAP_CONTENT);
        OptionsMenu.displayMenu(calendaricon,addIcon,popupWindow,popupView);


        Button btn = view.findViewById(R.id.save);
        title = (EditText) view.findViewById(R.id.title);
        location = (EditText) view.findViewById(R.id.location);
        subject = (Spinner) view.findViewById(R.id.subjectSelect);
        reminder = (Spinner) view.findViewById(R.id.reminder);
        note = (EditText) view.findViewById(R.id.note);

        String[] reminders= new String[]{"5 minutes","10 minutes","15 minutes","30 minutes","1 hour","2 hours", "6 hours", "12 hours","1 day"};
        ArrayAdapter reminderAdapter= new ArrayAdapter(getActivity().getApplicationContext(), R.layout.spinner_item, reminders);
        final Spinner reminderSpinner= (Spinner) view.findViewById(R.id.reminder);
        reminderSpinner.setAdapter(reminderAdapter);

        // Subject Selector
        Cursor cs = db.getAllSubjects();
        int count = db.getSubjectCount();
        String[] subjects = new String[count];
        int i = 0;
        while (cs.moveToNext()){
            subjects[i] = cs.getString(1);
            i++;
        }

        ArrayAdapter subjectAdapter= new ArrayAdapter(getActivity().getApplicationContext(), R.layout.spinner_item, subjects);
        final Spinner subjectSpinner= (Spinner) view.findViewById(R.id.subjectSelect);
        subjectSpinner.setAdapter(subjectAdapter);

        //Colour Picker
        final Button colorbtn = (Button) view.findViewById(R.id.colorbtn);
        final Button bgBtn = (Button) view.findViewById(R.id.testbtn);
        colorbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ColorPicker cp = new ColorPicker() ;
                cp.openColorPicker(getChildFragmentManager(),colorbtn, bgBtn);
            }

        });

        //End Date Picker
        final TextView end = (TextView) view.findViewById(R.id.endDate);
        LinearLayout pickDatebtn1 = (LinearLayout) view.findViewById(R.id.dateSelectorEnd);
        pickDatebtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pass the textView in order to set the date for the text

                DialogFragment newFragment = new SelectDateFragment(end);
                newFragment.show(getFragmentManager(), "DatePicker");

            }
        });
        final String today = new SimpleDateFormat("d/M/yyyy", Locale.US)
                .format(Calendar.getInstance().getTime());
        end.setText(today);
        //Start Time Picker
        stime = (TextView) view.findViewById(R.id.sTime);

        LinearLayout pickTimebtn1 = (LinearLayout) view.findViewById(R.id.timeSelectorStart);
        pickTimebtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pass the textView in order to set the date for the text
                DialogFragment newFragment = new SelectTimeFragement(stime);
                newFragment.show(getFragmentManager(), "TimePicker");

            }
        });

        //End Time Picker
        etime = (TextView) view.findViewById(R.id.eTime);

        LinearLayout pickDatebtn2 = (LinearLayout) view.findViewById(R.id.timeSelectorEnd);
        pickDatebtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pass the textView in order to set the date for the text
                DialogFragment newFragment = new SelectTimeFragement(etime);
                newFragment.show(getFragmentManager(), "TimePicker");

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DateFormat timeFormat = new SimpleDateFormat("HH:mm");
                Date d1 = null;
                Date d2 = null;
                long rem = 0;
                String sstime, estime;

                sstime = stime.getText().toString();
                estime = etime.getText().toString();

                try {
                    d1 = timeFormat.parse(sstime);
                    d2 = timeFormat.parse(estime);
                    rem = d2.getTime() - d1.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                if (title.getText().toString().length() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter a title", Toast.LENGTH_LONG).show();
                } else if (location.getText().toString().length() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter a location", Toast.LENGTH_LONG).show();
                } else if (rem <= 0) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please select a valid end time", Toast.LENGTH_LONG).show();
                } else {

                    boolean isInserted = db.addExam(title.getText().toString(), subject.getSelectedItem().toString(), location.getText().toString(), end.getText().toString(), stime.getText().toString(), etime.getText().toString(), reminder.getSelectedItem().toString(), ((ColorDrawable) bgBtn.getBackground()).getColor(), note.getText().toString());

                    if (isInserted == true) {

                        Intent intent = new Intent(getActivity().getApplicationContext(), ReminderBroadcast.class);
                        intent.putExtra("reminderType", "Exam Reminder");
                        intent.putExtra("text", "You have your " + title.getText().toString() + " Exam at " + stime.getText().toString() + " on " + end.getText().toString());

                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String date = null;
                        Date startTime = null;
                        Date sdate = null;
                        String sttime;
                        String rtype;
                        Calendar cald = Calendar.getInstance();
                        Calendar calt = Calendar.getInstance();
                        sttime = stime.getText().toString();
                        date = end.getText().toString();
                        rtype = reminder.getSelectedItem().toString();

                        int min = 0;

                        if (rtype.equals("5 minutes")) {
                            min = -5;
                        } else if (rtype.equals("10 minutes")) {
                            min = -10;
                        } else if (rtype.equals("15 minutes")) {
                            min = -15;
                        } else if (rtype.equals("30 minutes")) {
                            min = -30;
                        } else if (rtype.equals("1 hour")) {
                            min = -60;
                        } else if (rtype.equals("2 hours")) {
                            min = -120;
                        } else if (rtype.equals("6 hours")) {
                            min = -360;
                        } else if (rtype.equals("12 hours")) {
                            min = -720;
                        } else if (rtype.equals("1 day")) {
                            min = 1440;
                        }

                        try {
                            startTime = timeFormat.parse(sttime);
                            sdate = dateFormat.parse(date);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        cald.setTime(sdate);
                        calt.setTime(startTime);

                        cald.add(Calendar.HOUR_OF_DAY, calt.get(Calendar.HOUR_OF_DAY));
                        cald.add(Calendar.MINUTE, calt.get(Calendar.MINUTE));
                        cald.add(Calendar.MINUTE, min);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), db.getLastExamIndex(), intent, 0);
                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        long timeAtButtonClick = cald.getTimeInMillis();

                        alarmManager.set(AlarmManager.RTC_WAKEUP,
                                timeAtButtonClick,
                                pendingIntent);

                        Toast.makeText(getActivity().getApplicationContext(), "Exam Added Successfully", Toast.LENGTH_LONG).show();
                        displayExams fragment = new displayExams();
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

                    } else
                        Toast.makeText(getActivity().getApplicationContext(), "Insert Failed, Try again", Toast.LENGTH_LONG).show();
                }
            }
        });


        return view;
    }
}