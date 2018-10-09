package fen.code.recordview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;

import java.io.IOException;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    RecordButton recordButton;
    RecordView recordView;

    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* views declaration */
        textView = findViewById(R.id.text_view);
        recordButton = findViewById(R.id.record_button);
        recordView = findViewById(R.id.record_view);

        /* set record button on view */
        recordButton.setRecordView(recordView);

        /* set record listener on record view */
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                // On Start Recording
                Log.d("RecordView", "onStart");
                textView.setText("RecordView: onStart\n\n");
            }

            @Override
            public void onCancel() {
                // On Swipe To Cancel
                Log.d("RecordView", "onCancel");
                textView.setText(textView.getText().toString() + "RecordView: onCancel\n\n");
            }

            @Override
            public void onFinish(long recordTime) {
                // On Stop Recording
                Log.d("RecordView", "onFinish");
                textView.setText(textView.getText().toString() + "RecordView: onFinish\n\n");
            }

            @Override
            public void onLessThanSecond() {
                // When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");
                textView.setText(textView.getText().toString() + "RecordView: onLessThanSecond\n\n");
            }
        });


        /* PERMISSIONS */
        requestPermission();

        /* RECORDER */
        random = new Random();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_record) {
            setActivity(RecordActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setActivity(Class activity) {
        Intent intent = new Intent(getApplicationContext(), activity);
        startActivity(intent);
    }

    private void setToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /* HERE: REQUEST PERMISSIONS */

    private void requestPermission() {
        Log.d("requestPermission", "requestPermission");
        textView.setText("requestPermission: requestPermission\n\n");
        
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("requestPermission", "onRequestPermissionsResult");
        textView.setText("requestPermission: onRequestPermissionsResult\n\n");

        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        Log.d("requestPermission", "checkPermission");
        textView.setText("requestPermission: checkPermission\n\n");

        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    /* HERE: RECORDING FUNCTIONS */

    public void MediaRecorderReady() {
        Log.d("MediaRecorder", "MediaRecorderReady");
        textView.setText("MediaRecorder: MediaRecorderReady\n\n");

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string) {
        Log.d("MediaRecorder", "CreateRandomAudioFileName");
        textView.setText("MediaRecorder: CreateRandomAudioFileName\n\n");

        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));
            i++;
        }
        return stringBuilder.toString();
    }

    public void recordStart() {
        Log.d("MediaRecorder", "recordStart");
        textView.setText("MediaRecorder: recordStart\n\n");

        if (checkPermission()) {
            AudioSavePathInDevice =
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                            CreateRandomAudioFileName(5) + "AudioRecording.3gp";

            MediaRecorderReady();

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Toast.makeText(MainActivity.this, "Recording started",
                    Toast.LENGTH_LONG).show();
        } else {
            requestPermission();
        }
    }

    public void recordStop() {
        Log.d("MediaRecorder", "recordStop");
        textView.setText("MediaRecorder: recordStop\n\n");

        mediaRecorder.stop();
    }

    /* HERE: PLAY AUDIO FUNCTIONS */

    public void recordAudioPlay() {
        Log.d("AudioPlay", "recordAudioPlay");
        textView.setText("AudioPlay: recordAudioPlay\n\n");

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(AudioSavePathInDevice);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    public void recordAudioStop() {
        Log.d("AudioPlay", "recordAudioStop");
        textView.setText("AudioPlay: recordAudioStop\n\n");

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            MediaRecorderReady();
        }
    }
}
