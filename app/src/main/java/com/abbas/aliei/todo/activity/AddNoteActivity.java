package com.abbas.aliei.todo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.abbas.aliei.todo.abbas.todo.R;

public class AddNoteActivity extends AppCompatActivity {
    public static final String RESULT_TITLE_KEY = "result_title";
    public static final String RESULT_DESCRIPTION_KEY = "result_description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        setupViews();
    }

    private void setupViews() {
        final EditText titleEditText = (EditText) findViewById(R.id.et_addNote_title);
        final EditText descriptionEditText = (EditText) findViewById(R.id.et_addNote_description);
        descriptionEditText.requestFocus();

        Button saveButton = (Button) findViewById(R.id.button_addNote_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!descriptionEditText.getText().toString().equals("")) {
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_TITLE_KEY, titleEditText.getText().toString());
                    intent.putExtra(RESULT_DESCRIPTION_KEY, descriptionEditText.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    setResult(MainActivity.RESULT_CANCEL);
                    finish();
                }
            }
        });
    }
}
