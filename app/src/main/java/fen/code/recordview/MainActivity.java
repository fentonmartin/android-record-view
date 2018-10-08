package fen.code.recordview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    RecordButton recordButton;
    RecordView recordView;

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
    }
}
