package com.abbas.aliei.todo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.abbas.aliei.todo.common.DBOpenHelper;
import com.abbas.aliei.todo.datamodel.Note;
import com.abbas.aliei.todo.adapter.ToDoAdapter;
import com.abbas.aliei.todo.abbas.todo.R;

public class MainActivity extends AppCompatActivity implements ToDoAdapter.OnNoteEvent {

    private static final int ADD_NOTE_REQUEST_CODE = 1001;
    private static final int EDIT_NOTE_REQUEST_CODE = 1002;
    public static final int RESULT_CANCEL = -2;
    public static final String NOTE_KEY = "note_key";
    private static final String TAG = "MainActivity";
    private ToDoAdapter adapter;
    private DBOpenHelper dbOpenHelper;

    private TextView deleteTextView;
    private FloatingActionButton newNoteButton;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CheckBox selectAllCheckBox;
    private TextView selectedItemsCountTextView;
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setupViews();
        getNotesFromDataBase();
    }

    private void init() {
        dbOpenHelper = new DBOpenHelper(this);
        adapter = new ToDoAdapter(this, this);
    }

    private void setupViews() {
        titleTextView = findViewById(R.id.tv_main_title);

        recyclerView = findViewById(R.id.rv_main_recyclerView);
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        deleteTextView = findViewById(R.id.tv_main_delete);
        deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDeleteAlertDialog();
            }
        });

        newNoteButton = findViewById(R.id.fab_main_newNote);
        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST_CODE);
            }
        });

        selectAllCheckBox = findViewById(R.id.chbox_main_selectAll);
        selectAllCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()){
                    adapter.selectAllNotes();
                    selectAllItems();
                    checkAllItemsState();
                } else {
                    adapter.deselectAllNotes();
                    deselectAllItems();
                    checkAllItemsState();
                }
            }
        });

        selectedItemsCountTextView = findViewById(R.id.tv_main_selectedItemsCount);
    }

    private void getNotesFromDataBase() {
        adapter.setNotes(dbOpenHelper.getNotes());
    }

    @Override
    public void onLongClick(Note note, View v) {
        note.setSelected(true);
        v.setBackgroundColor(getResources().getColor(R.color.todo_selected_item));
        checkAllItemsState();
    }

    @Override
    public void onClick(Note note, View v) {
        if (note.isSelected()) {
            note.setSelected(false);
            v.setBackgroundColor(getResources().getColor(R.color.white));
            checkAllItemsState();
        } else if (adapter.getSelectedItemsCount() > 0 &&
                !note.isSelected()) {
            note.setSelected(true);
            v.setBackgroundColor(getResources().getColor(R.color.todo_selected_item));
            checkAllItemsState();
        } else {
            Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
            intent.putExtra(NOTE_KEY, note);
            startActivityForResult(intent, EDIT_NOTE_REQUEST_CODE);
        }
    }

    private void makeDeleteAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(adapter.getSelectedItemsCount() + " " +
                getString(R.string.main_deleteDialog_message));
        builder.setPositiveButton(getString(R.string.main_deleteDialog_positive_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbOpenHelper.removeNotes(adapter.getSelectedItemsId());
                getNotesFromDataBase();
                checkAllItemsState();
            }
        });
        builder.setNegativeButton(getString(R.string.main_deleteDialog_negative_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // nothing
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCEL) {
            Toast.makeText(this, getString(R.string.main_nothing_to_save), Toast.LENGTH_SHORT).show();
        }
        if (requestCode == ADD_NOTE_REQUEST_CODE &&
                resultCode == RESULT_OK &&
                data != null) {
            String title = data.getStringExtra(AddNoteActivity.RESULT_TITLE_KEY);
            String description = data.getStringExtra(AddNoteActivity.RESULT_DESCRIPTION_KEY);

            Note note = new Note();
            note.setTitle(title);
            note.setDescription(description);
            long id = dbOpenHelper.addNote(note);
            note.setId((int) id);
            adapter.addNote(note);
        } else if (requestCode == EDIT_NOTE_REQUEST_CODE &&
                resultCode == RESULT_OK &&
                data != null) {
            int id = data.getIntExtra(EditNoteActivity.NOTE_ID_KEY, 0);
            String title = data.getStringExtra(EditNoteActivity.EDIT_NOTE_TITLE_KEY);
            String description = data.getStringExtra(EditNoteActivity.EDIT_NOTE_DESCRIPTION_KEY);

            Note note = new Note();
            note.setId(id);
            note.setTitle(title);
            note.setDescription(description);
            dbOpenHelper.updateNote(note);
            getNotesFromDataBase();
        }
    }

    @Override
    public void onBackPressed() {
        if (adapter.getSelectedItemsCount() > 0) {
            adapter.deselectAllNotes();
            deselectAllItems();
            checkAllItemsState();
        } else {
            finish();
        }
    }


    private void setDeleteButtonVisibility() {
        if (adapter.getSelectedItemsCount() > 0) {
            this.deleteTextView.setVisibility(View.VISIBLE);
        } else {
            this.deleteTextView.setVisibility(View.GONE);
        }
    }

    private void setNewNoteButtonVisibility() {
        if (adapter.getSelectedItemsCount() > 0) {
            newNoteButton.setVisibility(View.GONE);
        } else {
            newNoteButton.setVisibility(View.VISIBLE);
        }
    }

    private void setSelectAllCheckBoxCheck() {
        if (adapter.getSelectedItemsCount() == adapter.getItemCount()) {
            selectAllCheckBox.setChecked(true);
        } else {
            selectAllCheckBox.setChecked(false);
        }
    }

    private void setSelectAllCheckBoxVisibility() {
        if (adapter.getSelectedItemsCount() > 0) {
            selectAllCheckBox.setVisibility(View.VISIBLE);
        } else {
            selectAllCheckBox.setVisibility(View.GONE);
        }
    }

    private void setSelectedItemsCountTextViewNumber() {
        selectedItemsCountTextView.setText(String.valueOf(adapter.getSelectedItemsCount()));
    }

    private void setSelectedItemsCountTextViewVisibility() {
        if (adapter.getSelectedItemsCount() > 0) {
            selectedItemsCountTextView.setVisibility(View.VISIBLE);
        } else {
            selectedItemsCountTextView.setVisibility(View.GONE);
        }
    }

    private void setTitleTextViewVisibility() {
        if (adapter.getSelectedItemsCount() > 0) {
            titleTextView.setVisibility(View.GONE);
        } else {
            titleTextView.setVisibility(View.VISIBLE);
        }
    }

    private void deselectAllItems() {
        for (int i = 0; i < layoutManager.getChildCount(); i++) {
            layoutManager.findViewByPosition(i)
                    .setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    private void selectAllItems() {
        for (int i = 0; i < layoutManager.getChildCount(); i++) {
            layoutManager.findViewByPosition(i)
                    .setBackgroundColor(getResources().getColor(R.color.todo_selected_item));
        }
    }

    private void checkAllItemsState() {
        setDeleteButtonVisibility();
        setNewNoteButtonVisibility();
        setSelectAllCheckBoxCheck();
        setSelectAllCheckBoxVisibility();
        setSelectedItemsCountTextViewNumber();
        setSelectedItemsCountTextViewVisibility();
        setTitleTextViewVisibility();
    }
}
