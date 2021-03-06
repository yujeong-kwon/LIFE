package org.techtown.life;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.content.ContentValues.TAG;

public class Diary_calendar extends Fragment {
    public int mCenterPosition;
    private long mCurrentTime;
    public ArrayList<Object> mCalendarList = new ArrayList<>();

    public TextView textView;
    public RecyclerView recyclerView;
    private CalendarAdapter mAdapter;
    private StaggeredGridLayoutManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.diary_cal, container, false);

        initView(rootView);

        initSet();

        setRecycler();

        return rootView;
    }

    public void initView(View v){

        textView = (TextView)v.findViewById(R.id.title);
        recyclerView = (RecyclerView)v.findViewById(R.id.calendar);

    }

    public void initSet(){

        initCalendarList();

    }

    public void initCalendarList() {
        GregorianCalendar cal = new GregorianCalendar();
        setCalendarList(cal);
    }

    private void setRecycler() {

        if (mCalendarList == null) {
            Log.w(TAG, "No Query, not initializing RecyclerView");
        }

        manager = new StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL);

        mAdapter = new CalendarAdapter(mCalendarList);

        mAdapter.setCalendarList(mCalendarList,this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);

        if (mCenterPosition >= 0) {
            recyclerView.scrollToPosition(mCenterPosition);
        }
    }

    public void setCalendarList(GregorianCalendar cal) {

        //setTitle(cal.getTimeInMillis());

        ArrayList<Object> calendarList = new ArrayList<>();

        for (int i = -300; i < 300; i++) {
            try {
                GregorianCalendar calendar = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + i, 1, 0, 0, 0);
                if (i == 0) {
                    mCenterPosition = calendarList.size();
                }

                // ???????????????
                calendarList.add(calendar.getTimeInMillis());

                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; //?????? ?????? ???????????? ?????? -1 ??? ?????? ????????? ?????? ??? ????????? ?
                int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // ?????? ?????? ????????? ??????

                // EMPTY ??????
                for (int j = 0; j < dayOfWeek; j++) {
                    calendarList.add(Keys.EMPTY);
                }
                for (int j = 1; j <= max; j++) {
                    calendarList.add(new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), j));
                }

                // TODO : ????????? ????????? ?????????????????????

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mCalendarList = calendarList;
    }



}