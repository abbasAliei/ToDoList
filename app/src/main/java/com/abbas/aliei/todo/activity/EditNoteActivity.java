package com.abbas.aliei.todo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.abbas.aliei.todo.datamodel.Note;
import com.abbas.aliei.todo.abbas.todo.R;

public class EditNoteActivity extends AppCompatActivity {
    public static final String EDIT_NOTE_TITLE_KEY = "edit_note_title_key";
    public static final String EDIT_NOTE_DESCRIPTION_KEY = "edit_note_description_key";
    public static final String NOTE_ID_KEY = "note_id_key";

    private EditText titleEditText;
    private EditText descriptionEditText;
    private Button saveChangesButton;

    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        init();
        setupViews();
    }

    private void init() {
        note = getIntent().getExtras().getParcelable(MainActivity.NOTE_KEY);
    }

    private void setupViews(){
        titleEditText = findViewById(R.id.et_editNote_title);
        titleEditText.setText(note.getTitle());

        descriptionEditText = findViewById(R.id.et_editNote_description);
        descriptionEditText.setText(note.getDescription());
        descriptionEditText.requestFocus();

        saveChangesButton = findViewById(R.id.button_editNote_saveChanges);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!descriptionEditText.getText().toString().equals("")){
                    Intent intent = new Intent();
                    intent.putExtra(EDIT_NOTE_TITLE_KEY, titleEditText.getText().toString());
                    intent.putExtra(EDIT_NOTE_DESCRIPTION_KEY, descriptionEditText.getText().toString());
                    intent.putExtra(NOTE_ID_KEY, note.getId());
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
