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
                setLog("setOnRecordListener", "onStart");

                recordStart();
            }

            @Override
            public void onCancel() {
                // On Swipe To Cancel
                setLog("setOnRecordListener", "onCancel");

                recordStop();
                recordAudioPlay();
            }

            @Override
            public void onFinish(long recordTime) {
                // On Stop Recording
                setLog("setOnRecordListener", "onFinish");

                recordStop();
                recordAudioPlay();
            }

            @Override
            public void onLessThanSecond() {
                // When the record time is less than One Second
                setLog("setOnRecordListener", "onLessThanSecond");

                recordStop();
                recordAudioPlay();
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

    /* HERE: LOG FUNCTIONS */

    @SuppressLint("SetTextI18n")
    private void setLog(String function, String text) {
        Log.d("RecordView " + function, text);
        textView.setText(textView.getText().toString() + "\n\n" + function + ": " + text);
    }

    /* HERE: REQUEST PERMISSIONS */

    private void requestPermission() {
        setLog("requestPermission", "requestPermission");

        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        setLog("requestPermission", "onRequestPermissionsResult");

        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        setLog("requestPermission", "Granted");
                    } else {
                        setLog("requestPermission", "Denied");
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        setLog("requestPermission", "checkPermission");

        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    /* HERE: RECORDING FUNCTIONS */

    public void MediaRecorderReady() {
        setLog("MediaRecorder", "MediaRecorderReady");

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string) {
        setLog("MediaRecorder", "CreateRandomAudioFileName");

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
        setLog("MediaRecorder", "recordStart");

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

            setLog("MediaRecorder", "Started");
        } else {
            requestPermission();
        }
    }

    public void recordStop() {
        setLog("MediaRecorder", "recordStop");

        mediaRecorder.stop();
    }

    /* HERE: PLAY AUDIO FUNCTIONS */

    public void recordAudioPlay() {
        setLog("AudioPlay", "recordAudioPlay");

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
        setLog("AudioPlay", "recordAudioStop");

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            MediaRecorderReady();
        }
    }

    /* HERE: CONVERT AUDIO FUNCTIONS */

    public void convertAudioStart() {

        setLog("convertAudioStart", "encode");
    }

    public void convertAudioStop() {

        setLog("convertAudioStart", "decode");
    }
}
