package com.example.jru.studybuddy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes = new ArrayList<>();
    private OnNoteClickListener listener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
        void onNoteDelete(Note note);
    }

    public NoteAdapter(OnNoteClickListener listener) {
        this.listener = listener;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView contentText;
        private final TextView dateText;
        private final ImageButton deleteButton;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.noteTitle);
            contentText = itemView.findViewById(R.id.noteContent);
            dateText = itemView.findViewById(R.id.noteDate);
            deleteButton = itemView.findViewById(R.id.noteDeleteButton);
        }

        public void bind(Note note) {
            titleText.setText(note.getTitle());
            contentText.setText(note.getContent());
            
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            dateText.setText(sdf.format(new Date(note.getUpdatedAt())));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNoteClick(note);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNoteDelete(note);
                }
            });
        }
    }
}
