package com.example.potter.nct;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    Context context;
    ListView lvSong;
    TextView tvTitle, tvArtist,tvTimeProgress, tvTimeTotal;
    SeekBar sbProgress;
    ImageView imgPlay, imgNext, imgPrev, imgRepeat, imgShuffle;

    ArrayList<Song> list;
    SongAdapter adapter;
    MediaPlayer mediaPlayer;
    int kt = 0;
    int run = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anhXa();
        initPermission();
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        onClick();
    }

    private void onClick() {
        lvSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (kt == 0){
                    run = position;
                    initMedia(run);
                    mediaPlayer.start();
                    tvTitle.setText(list.get(position).getSongName());
                    tvArtist.setText(list.get(position).getSongArtist());
                    imgPlay.setImageResource(R.drawable.ic_pause_24dp);
                    setTimeTotal();
                    upDateTime();
                    kt = 1;
                }else {
                    mediaPlayer.release();
                    kt = 0;
                    run = position;
                    initMedia(run);
                    mediaPlayer.start();
                    tvTitle.setText(list.get(position).getSongName());
                    tvArtist.setText(list.get(position).getSongArtist());
                    imgPlay.setImageResource(R.drawable.ic_pause_24dp);
                    setTimeTotal();
                    upDateTime();
                    kt = 1;
                }

            }
        });

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    imgPlay.setImageResource(R.drawable.ic_play_arrow_24dp);
                    setTimeTotal();
                }else {
                    mediaPlayer.start();
                    tvTitle.setText(list.get(0).getSongName());
                    tvArtist.setText(list.get(0).getSongArtist());
                    imgPlay.setImageResource(R.drawable.ic_pause_24dp);
                    setTimeTotal();
                    upDateTime();
                }
            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run++;
                if (run > list.size() - 1){
                    run = 0;
                }
                mediaPlayer.release();
                initMedia(run);
                mediaPlayer.start();
                tvTitle.setText(list.get(run).getSongName());
                tvArtist.setText(list.get(run).getSongArtist());
                imgPlay.setImageResource(R.drawable.ic_pause_24dp);
                setTimeTotal();
                upDateTime();
            }
        });

        imgPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run--;
                if (run < 0){
                    run = list.size() - 1;
                }
                mediaPlayer.release();
                initMedia(run);
                mediaPlayer.start();
                tvTitle.setText(list.get(run).getSongName());
                tvArtist.setText(list.get(run).getSongArtist());
                imgPlay.setImageResource(R.drawable.ic_pause_24dp);
                setTimeTotal();
                upDateTime();
            }
        });



        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(sbProgress.getProgress());
            }
        });
    }

    private void upDateTime(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat format = new SimpleDateFormat("mm:ss");
                tvTimeProgress.setText(format.format(mediaPlayer.getCurrentPosition()));
                sbProgress.setProgress(mediaPlayer.getCurrentPosition());

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        run++;
                        if (run > list.size() - 1){
                            run = 0;
                        }
                        mediaPlayer.release();
                        initMedia(run);
                        mediaPlayer.start();
                        imgPlay.setImageResource(R.drawable.ic_pause_24dp);
                    }
                });

                handler.postDelayed(this,1000);
            }
        },500);
    }
    private void setTimeTotal(){
        SimpleDateFormat time = new SimpleDateFormat("mm:ss");
        tvTimeTotal.setText(time.format(mediaPlayer.getDuration()));
        sbProgress.setMax(mediaPlayer.getDuration());
    }

    private void initMedia(int position){
        Uri uri = Uri.parse(list.get(position).getSongPath());
        mediaPlayer = MediaPlayer.create(this,uri);

    }

    private void initView() {
        initList();
        adapter = new SongAdapter(MainActivity.this,list);
        lvSong.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initList() {
        list = new ArrayList<>();
       // String path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/Download";


        //lấy nhạc từ thẻ nhớ thông qua ContentResolver
        ContentResolver resolver = getApplicationContext().getContentResolver();
        //lấy tất cả các bài hát thông tin từ resolver
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null, null,MediaStore.Audio.Media.TITLE + " ASC");
        //lọc lấy các file cần thiết.
        if (cursor.moveToFirst()){
            while (cursor.moveToNext()){
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                String Name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                list.add(new Song(Name,cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),artist,album));
            }
        }
        /*Log.e("AAAAAAAAAAAAAAA",path);
        File file = new File(path);
        //lấy tất cả các file nhạc trong thư mục
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++){
            //đọc tất cả các file trong thư mục và thêm vào list
            String name = files[i].getName();
            if (name.endsWith(".mp3")){
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(files[i].getAbsolutePath());
                String Name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                list.add(new Song(Name,files[i].getAbsolutePath(),artist,album));
            }
        }*/
    }

    private void anhXa() {
        lvSong          =   findViewById(R.id.LvSong);
        tvTitle         =   findViewById(R.id.TvTenBh);
        tvArtist        =   findViewById(R.id.TvCaSi);
        tvTimeProgress  =   findViewById(R.id.TvTimeCurrent);
        tvTimeTotal     =   findViewById(R.id.TvTimeTotal);
        sbProgress      =   findViewById(R.id.SbProcess);
        imgPlay         =   findViewById(R.id.img_play);
        imgNext         =   findViewById(R.id.img_next);
        imgPrev         =   findViewById(R.id.img_prev);
        imgRepeat       =   findViewById(R.id.img_repeat);
        imgShuffle      =   findViewById(R.id.img_shuffle);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "Permision Write File is Granted", Toast.LENGTH_SHORT).show();
                //caaps quyeen
                initView();

            } else {
                Toast.makeText(getApplicationContext(), "Permision Write File is Denied", Toast.LENGTH_SHORT).show();
            }

        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void initPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (PermissionChecker.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){


                //permission don't granted
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Toast.makeText(getApplicationContext(), "Permission isn't granted ", Toast.LENGTH_SHORT).show();
                }
                // Permisson don't granted and dont show dialog again.
                else {
                    Toast.makeText(getApplicationContext(), "Permisson don't granted and dont show dialog again ", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }
    }
}