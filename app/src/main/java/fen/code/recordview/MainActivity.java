package fen.code.recordview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    RecordButton recordButton;
    RecordView recordView;

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
            
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
