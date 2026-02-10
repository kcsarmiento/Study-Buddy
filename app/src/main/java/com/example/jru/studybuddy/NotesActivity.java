package com.example.jru.studybuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class NotesActivity extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {

    private RecyclerView notesRecyclerView;
    private TextView emptyNotesText;
    private NoteAdapter noteAdapter;
    private FirebaseRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        repository = new FirebaseRepository();

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        emptyNotesText = findViewById(R.id.emptyNotesText);
        FloatingActionButton addNoteFab = findViewById(R.id.addNoteFab);

        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(this);
        notesRecyclerView.setAdapter(noteAdapter);

        loadNotes();

        addNoteFab.setOnClickListener(v -> showAddNoteDialog(null));
    }

    private void loadNotes() {
        repository.getNotes(new FirebaseRepository.DataCallback<Note>() {
            @Override
            public void onSuccess(List<Note> data) {
                noteAdapter.setNotes(data);
                if (data.isEmpty()) {
                    emptyNotesText.setVisibility(View.VISIBLE);
                    notesRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyNotesText.setVisibility(View.GONE);
                    notesRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(NotesActivity.this, "Error loading notes: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddNoteDialog(Note existingNote) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_note, null);
        EditText noteTitleInput = dialogView.findViewById(R.id.noteTitleInput);
        EditText noteContentInput = dialogView.findViewById(R.id.noteContentInput);

        if (existingNote != null) {
            noteTitleInput.setText(existingNote.getTitle());
            noteContentInput.setText(existingNote.getContent());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(existingNote == null ? R.string.add_note : R.string.edit_task);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.save_note, (dialog, which) -> {
            String title = noteTitleInput.getText().toString().trim();
            String content = noteContentInput.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, R.string.error_title_required, Toast.LENGTH_SHORT).show();
                return;
            }

            long currentTime = System.currentTimeMillis();

            if (existingNote != null) {
                existingNote.setTitle(title);
                existingNote.setContent(content);
                existingNote.setUpdatedAt(currentTime);

                repository.updateNote(existingNote, new FirebaseRepository.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(NotesActivity.this, R.string.note_added, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(NotesActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Note note = new Note(null, title, content, currentTime, currentTime);

                repository.addNote(note, new FirebaseRepository.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(NotesActivity.this, R.string.note_added, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(NotesActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    @Override
    public void onNoteClick(Note note) {
        showAddNoteDialog(note);
    }

    @Override
    public void onNoteDelete(Note note) {
        repository.deleteNote(note.getId(), new FirebaseRepository.OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(NotesActivity.this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(NotesActivity.this, "Error deleting note: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
