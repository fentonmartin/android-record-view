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
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    RecordButton recordButton;
    RecordView recordView;

    Random random;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    public static final int RequestPermissionCode = 1;

    String audioBase64 = "";
    String audioPathRecorded = "";
    String audioPathDecrypted = "";
    String RandomAudioFileName = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

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
            }

            @Override
            public void onFinish(long recordTime) {
                // On Stop Recording
                setLog("setOnRecordListener", "onFinish");

                recordStop();
                convertAudioAndPlay();
            }

            @Override
            public void onLessThanSecond() {
                // When the record time is less than One Second
                setLog("setOnRecordListener", "onLessThanSecond");

                recordStop();
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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
        mediaRecorder.setOutputFile(audioPathRecorded);
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
            audioPathRecorded =
                    Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/Android/data/audio/recorded" +
                            CreateRandomAudioFileName(5) + "recorded.3gp";
            audioPathDecrypted =
                    Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/Android/data/audio/decrypted" +
                            CreateRandomAudioFileName(5) + "decrypted.3gp";

            MediaRecorderReady();

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            setLog("MediaRecorder", "Started");
        } else {
            requestPermission();
        }
    }

    public void recordStop() {
        setLog("MediaRecorder", "recordStop");

        if (mediaRecorder != null)
            mediaRecorder.stop();
    }

    /* HERE: PLAY AUDIO FUNCTIONS */

    public void recordAudioPlay(String path) {
        setLog("AudioPlay", "recordAudioPlay");

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
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

    public void convertAudioEncode(String path) {
        setLog("convertAudioEncode", "File path: " + path);
        File file = new File(path);
        byte[] b = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
            for (byte aB : b) {
                System.out.print((char) aB);
            }
        } catch (FileNotFoundException e) {
            setLog("convertAudioEncode", "File Not Found");
            e.printStackTrace();
        } catch (IOException e1) {
            setLog("convertAudioEncode", "Error Reading The File");
            e1.printStackTrace();
        }

        byte[] byteFileArray = new byte[0];
        try {
            byteFileArray = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (byteFileArray.length > 0) {
            audioBase64 = android.util.Base64.encodeToString(byteFileArray, android.util.Base64.NO_WRAP);
            setLog("convertAudioEncode", "File Base64: " + audioBase64);
        }
        setLog("convertAudioEncode", "FINISHED");

        convertAudioDecode(audioPathDecrypted);
    }

    public void convertAudioDecode(String output) {
        setLog("convertAudioDecode", "File output: " + output);
        try {
            FileOutputStream out = new FileOutputStream(output);
            byte[] decoded = Base64.decode(audioBase64, 0);
            out.write(decoded);
            out.close();

            recordAudioPlay(audioPathDecrypted);
        } catch (FileNotFoundException e) {
            setLog("convertAudioDecode", "File Not Found");
            e.printStackTrace();
        } catch (IOException e) {
            setLog("convertAudioDecode", "Error Reading The File");
            e.printStackTrace();
        }
        setLog("convertAudioDecode", "FINISHED");
    }

    public void convertAudioAndPlay() {
        setLog("convertAudioAndPlay", "convertAudioAndPlay");
        convertAudioEncode(audioPathRecorded);
    }
}
