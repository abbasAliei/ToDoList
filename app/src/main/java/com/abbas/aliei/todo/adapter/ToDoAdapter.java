package com.abbas.aliei.todo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abbas.aliei.todo.abbas.todo.R;
import com.abbas.aliei.todo.datamodel.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ProLap on 24/03/2018.
 */

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder> {
    private Context context;
    private List<Note> notes = new ArrayList<>();
    private OnNoteEvent onNoteEvent;

    public ToDoAdapter(Context context, OnNoteEvent onNoteEvent) {
        this.context = context;
        this.onNoteEvent = onNoteEvent;
    }

    public void setNotes(List<Note> notes){
        this.notes.clear();
        this.notes.addAll(notes);
        notifyDataSetChanged();
    }

    public int getSelectedItemsCount(){
        int selected = 0;
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).isSelected()){
                selected += 1;
            }
        }
        return selected;
    }

    public List<Integer> getSelectedItemsId(){
        List<Integer> selectedItemsId = new ArrayList<>();
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).isSelected()){
                selectedItemsId.add(notes.get(i).getId());
            }
        }
        return selectedItemsId;
    }

    public void deselectAllNotes(){
        for (int i = 0; i < notes.size(); i++) {
            notes.get(i).setSelected(false);
        }
    }

    public void selectAllNotes(){
        for (int i = 0; i < notes.size(); i++) {
            notes.get(i).setSelected(true);
        }
    }

    public void addNote(Note note) {
        notes.add(note);
        notifyItemInserted(notes.size() - 1);
    }

    @Override
    public ToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.todo_item, parent, false);
        return new ToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ToDoViewHolder holder, int position) {
        holder.bindNote(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ToDoViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;

        public ToDoViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_todoItem_title);
            description = itemView.findViewById(R.id.tv_todoItem_description);
         }

        public void bindNote(final Note note) {
            if (note.isSelected()){
                itemView.setBackgroundColor(context.getResources().getColor(R.color.todo_selected_item));
            }else {
                itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
            }

            if (note.getTitle().equals("")){
                title.setVisibility(View.GONE);
            } else {
                title.setVisibility(View.VISIBLE);
                title.setText(note.getTitle());
            }

            description.setText(note.getDescription());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onNoteEvent.onClick(note, itemView);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onNoteEvent.onLongClick(note, itemView);
                    return true;
                }
            });
        }
    }

    public interface OnNoteEvent {
        void onClick(Note note, View v);

        void onLongClick(Note note, View v);
    }
}
