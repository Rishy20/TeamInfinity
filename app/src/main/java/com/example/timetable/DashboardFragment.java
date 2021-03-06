package com.example.timetable;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.timetable.Class.addClass;
import com.example.timetable.Course.AddCourseFragment;
import com.example.timetable.Course.editCourse;
import com.example.timetable.Database.DBHandler;

import com.example.timetable.Exam.exam;
import com.example.timetable.Goals.createevents;
import com.example.timetable.Homework.AddHomeworkFragment;
import com.example.timetable.Study.AddStudyFragment;
import com.example.timetable.Study.StudyTimerFragment;
import com.example.timetable.Subject.AddSubjectFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class DashboardFragment extends Fragment {
    @Nullable
    DBHandler db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DBHandler(getActivity().getApplicationContext());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard,null);

        final ImageView addIcon = view.findViewById(R.id.addIcon);
        final  ImageView calendaricon = view.findViewById(R.id.calendarIcon);
        LayoutInflater layoutInflater= (LayoutInflater)getActivity().getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.list_popup, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,450, ViewGroup.LayoutParams.WRAP_CONTENT);
        OptionsMenu.displayMenu(calendaricon,addIcon,popupWindow,popupView);




        TextView todayDate = view.findViewById(R.id.todayDate);
        TextView classCount = view.findViewById(R.id.classCount);
        TextView homeworkCount = view.findViewById(R.id.homeWorkCount);
        TextView examCount = view.findViewById(R.id.examCount);
        Calendar cal = Calendar.getInstance();
        todayDate.setText(cal.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.ENGLISH)+", " + cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH ) + " "+ cal.get(Calendar.DAY_OF_MONTH) + " "+cal.get(Calendar.YEAR) );
        String todayDay = cal.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.ENGLISH);

        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDate = dd+"/"+(mm+1)+"/"+yy;

        Cursor c = db.getAllClass();
        int count = 0;

        classCount.setText(String.valueOf( getTodayClassCount(currentDate,todayDay)));
        homeworkCount.setText(String.valueOf(getTodayHomeworkCount(currentDate)));
        examCount.setText(String.valueOf(getTodayExamCount(currentDate)));


        final TabLayout tabLayout1 = (TabLayout) view.findViewById(R.id.tabDashboard);
        tabLayout1.addTab(tabLayout1.newTab().setText("All"));
        tabLayout1.addTab(tabLayout1.newTab().setText("Class"));
        tabLayout1.addTab(tabLayout1.newTab().setText("Homework"));
        tabLayout1.addTab(tabLayout1.newTab().setText("Exam"));
        tabLayout1.addTab(tabLayout1.newTab().setText("Study"));
        tabLayout1.addTab(tabLayout1.newTab().setText("Goals"));
        tabLayout1.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout1.setTabMode(TabLayout.MODE_SCROLLABLE);

        final ViewPager viewPager1 = (ViewPager) view.findViewById(R.id.dashboard_pager);
        MyAdapter adapter = new MyAdapter(getChildFragmentManager(),tabLayout1.getTabCount());
        viewPager1.setAdapter(adapter);
        viewPager1.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout1));

        for (int i = 0; i < tabLayout1.getTabCount(); i++) {

            TabLayout.Tab tab = tabLayout1.getTabAt(i);
            if (tab != null) {

                TextView tabTextView = new TextView(getActivity().getApplicationContext());
                tab.setCustomView(tabTextView);

                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.setTypeface(null, Typeface.NORMAL);
                tabTextView.setTextColor(Color.parseColor("#FFFFFF"));
                tabTextView.setText(tab.getText());
                tabTextView.setTextSize(18);
                tabTextView.setTypeface(null, Typeface.BOLD);
                if (i == 0) {
                    tabTextView.setTextSize(22);


                }

            }

        }
        tabLayout1.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager1.setCurrentItem(tab.getPosition());
                TextView text = (TextView) tab.getCustomView();

                text.setTextSize(22);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView text = (TextView) tab.getCustomView();
                text.setTextSize(18);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }
    public int getTodayClassCount(String currentDate,String todayDay){
        Cursor c = db.getAllClass();
        int count = 0;
        while (c.moveToNext()){
            String date = c.getString(13);
            if(date.equals(currentDate) || todayDay.equals(c.getString(10))){
                count++;
            }
        }
        return count;
    }

    public int getTodayHomeworkCount(String currentDate){
        Cursor c = db.getAllHomework();
        int count = 0;
        while (c.moveToNext()){
            String date = c.getString(3);
            if(date.equals(currentDate)){
                count++;
            }
        }
        return count;
    }


    public int getTodayExamCount(String currentDate){

        Cursor c = db.getAllExam();
        int count = 0;
        while (c.moveToNext()){
            String date = c.getString(4);
            if(date.equals(currentDate)){
                count++;
            }
        }
        return count;
    }


}
class MyAdapter extends FragmentPagerAdapter {

    int noOfTabs;

    public MyAdapter(FragmentManager fm, int NumberOfTabs) {
        super(fm);
        this.noOfTabs = NumberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
//
        if(position == 0){
            fragment = new AllFragment().newInstance(0);
        }
        if(position == 1){
            fragment = new AllFragment().newInstance(1);
        }
        if(position == 2){
            fragment = new AllFragment().newInstance(2);
        }
        if(position == 3){
            fragment = new AllFragment().newInstance(3);
        }
        if(position == 4){
            fragment  = new AllFragment().newInstance(4);
        }
        if(position == 5){
            fragment = new AllFragment().newInstance(5);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 6;
    }
}
