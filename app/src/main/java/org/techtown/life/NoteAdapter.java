package org.techtown.life;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> implements OnNoteItemClickListener {
    ArrayList<Note> items = new ArrayList<Note>();

    OnNoteItemClickListener listener;

    int layoutType = 0;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.diary_note_item, viewGroup, false);

        return new ViewHolder(itemView, this, layoutType);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder viewHolder, int position) {
        Note item = items.get(position);
        viewHolder.setItem(item);
        viewHolder.setLayoutType(layoutType);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Note item) {
        items.add(item);
    }

    public void setItems(ArrayList<Note> items){
        this.items = items;
    }

    public Note getItem(int position){
        return items.get(position);
    }

    public void setOnItemClickListener(OnNoteItemClickListener listener){
        this.listener = listener;
    }
    @Override
    public void onItemClick(ViewHolder holder, View view, int position){
        if(listener != null){
            listener.onItemClick(holder, view, position);
        }
    }
    public void switchLayout(int position){
        layoutType = position;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout daily1;
        LinearLayout daily2;

        ImageView moodImageView;
        ImageView moodImageView2;

        ImageView pictureExistsImageView;
        ImageView pictureImageView;

        ImageView weatherImageView;
        ImageView weatherImageView2;

        TextView contentsTextView;
        TextView contentsTextView2;

        TextView locationTextView;
        TextView locationTextView2;

        TextView dateTextView;
        TextView dateTextView2;

        public ViewHolder(View itemView, final OnNoteItemClickListener listener, int layoutType){
            super(itemView);

            daily1 = itemView.findViewById(R.id.daily1);
            daily2 = itemView.findViewById(R.id.daily2);

            moodImageView = itemView.findViewById(R.id.moodImageView);
            moodImageView2 = itemView.findViewById(R.id.moodImageView2);

            pictureExistsImageView = itemView.findViewById(R.id.pictureExistsImageView);
            pictureImageView = itemView.findViewById(R.id.pictureImageView);

            weatherImageView = itemView.findViewById(R.id.weatherImageView);
            weatherImageView2 = itemView.findViewById(R.id.weatherImageView2);

            contentsTextView = itemView.findViewById(R.id.contentsTextView);
            contentsTextView2 = itemView.findViewById(R.id.contentsTextView2);

            locationTextView = itemView.findViewById(R.id.locationTextView);
            locationTextView2 = itemView.findViewById(R.id.locationTextView2);

            dateTextView =  itemView.findViewById(R.id.dateTextView);
            dateTextView2 = itemView.findViewById(R.id.dateTextView2);

            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if(listener != null){
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
            setLayoutType(layoutType);
        }

        public void setItem(Note item){
            String mood = item.getMood();
            int moodIndex = Integer.parseInt(mood);
            setMoodImage(moodIndex);

            String picturePath = item.getPicture();
            if(picturePath != null && !picturePath.equals("")){
                pictureExistsImageView.setVisibility(View.VISIBLE);
                pictureImageView.setVisibility(View.VISIBLE);
                pictureImageView.setImageURI(Uri.parse("file://" + picturePath));
            }else{
                pictureExistsImageView.setVisibility(View.GONE);
                pictureImageView.setVisibility(View.GONE);
                pictureImageView.setImageResource(R.drawable.add_image);
            }

            String weather = item.getWeather();

            int weatherIndex = Integer.parseInt(weather);
            setWeatherImage(weatherIndex);

            contentsTextView.setText(item.getContents());
            contentsTextView2.setText(item.getContents());

            locationTextView.setText(item.getAddress());
            locationTextView2.setText(item.getAddress());

            dateTextView.setText(item.getCreateDateStr());
            dateTextView2.setText(item.getCreateDateStr());
        }

        public void setMoodImage(int moodIndex){
            switch(moodIndex){
                case 0:
                    moodImageView.setImageResource(R.drawable.mood1);
                    moodImageView2.setImageResource(R.drawable.mood1);
                    break;
                case 1:
                    moodImageView.setImageResource(R.drawable.mood2);
                    moodImageView2.setImageResource(R.drawable.mood2);
                    break;
                case 2:
                    moodImageView.setImageResource(R.drawable.mood3);
                    moodImageView2.setImageResource(R.drawable.mood3);
                    break;
                case 3:
                    moodImageView.setImageResource(R.drawable.mood4);
                    moodImageView2.setImageResource(R.drawable.mood4);
                    break;
                case 4:
                    moodImageView.setImageResource(R.drawable.mood5);
                    moodImageView2.setImageResource(R.drawable.mood5);
                    break;
                default:
                    moodImageView.setImageResource(R.drawable.mood3);
                    moodImageView2.setImageResource(R.drawable.mood3);
                    break;
            }
        }
        public void setWeatherImage(int weatherIndex){
            switch(weatherIndex){
                case 0:
                    weatherImageView.setImageResource(R.drawable.weather1);
                    weatherImageView2.setImageResource(R.drawable.weather1);
                    break;
                case 1:
                    weatherImageView.setImageResource(R.drawable.weather2);
                    weatherImageView2.setImageResource(R.drawable.weather2);
                    break;
                case 2:
                    weatherImageView.setImageResource(R.drawable.weather3);
                    weatherImageView2.setImageResource(R.drawable.weather3);
                    break;
                case 3:
                    weatherImageView.setImageResource(R.drawable.weather4);
                    weatherImageView2.setImageResource(R.drawable.weather4);
                    break;
                case 4:
                    weatherImageView.setImageResource(R.drawable.weather5);
                    weatherImageView2.setImageResource(R.drawable.weather5);
                    break;
                default:
                    weatherImageView.setImageResource(R.drawable.weather1);
                    weatherImageView2.setImageResource(R.drawable.weather1);
                    break;
            }
        }
        public void setLayoutType(int layoutType){
            if(layoutType == 0){
                daily1.setVisibility(View.VISIBLE);
                daily2.setVisibility(View.GONE);
            }else if(layoutType == 1){
                daily1.setVisibility(View.GONE);
                daily2.setVisibility(View.VISIBLE);
            }
        }
    }

}
