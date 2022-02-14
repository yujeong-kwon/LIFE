package org.techtown.life;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

import lib.kingja.switchbutton.SwitchMultiButton;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class Diary_list extends Fragment {

    RecyclerView recyclerView;
    NoteAdapter adapter;

    Context context;
    OnTabItemSelectedListener listener;

    Intent intent;

    EditText content;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        this.context = context;

        if(context instanceof OnTabItemSelectedListener){
            listener = (OnTabItemSelectedListener) context;
        }
    }
    public
    @Override void onDetach() {

        super.onDetach();

        if(context != null){
            context = null;
            listener = null;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.diary_list, container, false);

        initUI(rootView);

        loadNoteListData();

        return rootView;
    }
    private void initUI(ViewGroup rootView){

        content = rootView.findViewById(R.id.contentsInput);
        Button todayWriteButton = rootView.findViewById(R.id.todayWriteButton);
        todayWriteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), Diary_write.class);
                startActivity(intent);

                if(listener != null){
                    listener.onTabSelected(1);
                }


            }


        });

        SwitchMultiButton switchButton = rootView.findViewById(R.id.switchButton);
        switchButton.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {
                Toast.makeText(getContext(), tabText, Toast.LENGTH_SHORT).show();

                adapter.switchLayout(position);
                adapter.notifyDataSetChanged();
            }
        });

        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NoteAdapter();

        adapter.addItem(new Note(0, "0", "서북구 두정동","","","안드로이드 앱 개발",
                "0","picture1.jpg","5월10일"));
        adapter.addItem(new Note(1, "1", "서북구 두정동","","","일기와 감정을 기록한다",
                "0","picture1.jpg","5월11일"));
        adapter.addItem(new Note(2, "0", "서북구 두정동","","","다이어리리스트 테스트",
                "0",null,"5월13일"));

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnNoteItemClickListener() {
            @Override
            public void onItemClick(NoteAdapter.ViewHolder holder, View view, int position) {
                Note item = adapter.getItem(position);
                Log.d(TAG, "아이템 선택됨 : " + item.getSearch_id());

                if (listener != null) {
                    listener.showFragment2(item);
                }
            }
        });
    }
    public int loadNoteListData() {
        AppConstants.println("loadNoteListData called.");

        String sql = "select _id, WEATHER, ADDRESS, LOCATION_X, LOCATION_Y, CONTENTS, MOOD, PICTURE, CREATE_DATE, MODIFY_DATE from "
                + NoteDatabase.TABLE_NOTE + " order by CREATE_DATE desc";

        int recordCount = -1;
        NoteDatabase database = NoteDatabase.getInstance(context);
        if (database != null) {
            Cursor outCursor = database.rawQuery(sql);

            recordCount = outCursor.getCount();
            AppConstants.println("record count : " + recordCount + "\n");

            ArrayList<Note> items = new ArrayList<Note>();

            for (int i = 0; i < recordCount; i++) {
                outCursor.moveToNext();

                int _id = outCursor.getInt(0);
                String weather = outCursor.getString(1);
                String address = outCursor.getString(2);
                String locationX = outCursor.getString(3);
                String locationY = outCursor.getString(4);
                String contents = outCursor.getString(5);
                String mood = outCursor.getString(6);
                String picture = outCursor.getString(7);
                String dateStr = outCursor.getString(8);
                String createDateStr = null;
                if (dateStr != null && dateStr.length() > 10) {
                    try {
                        Date inDate = AppConstants.dateFormat4.parse(dateStr);
                        createDateStr = AppConstants.dateFormat3.format(inDate);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    createDateStr = "";
                }

                AppConstants.println("#" + i + " -> " + _id + ", " + weather + ", " +
                        address + ", " + locationX + ", " + locationY + ", " + contents + ", " +
                        mood + ", " + picture + ", " + createDateStr);

                items.add(new Note(_id, weather, address, locationX, locationY, contents, mood, picture, createDateStr));
            }

            outCursor.close();

            adapter.setItems(items);
            adapter.notifyDataSetChanged();

        }

        return recordCount;
    }

}




