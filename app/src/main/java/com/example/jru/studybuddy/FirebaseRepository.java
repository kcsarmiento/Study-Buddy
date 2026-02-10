package com.example.jru.studybuddy;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRepository {
    private final FirebaseAuth auth;
    private final DatabaseReference database;

    public FirebaseRepository() {
        this.auth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance().getReference();
    }

    public interface AuthCallback {
        void onSuccess(String userId);
        void onFailure(String error);
    }

    public interface DataCallback<T> {
        void onSuccess(List<T> data);
        void onFailure(String error);
    }

    public interface SingleDataCallback<T> {
        void onSuccess(T data);
        void onFailure(String error);
    }

    public interface OperationCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public void ensureAuthenticated(AuthCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            callback.onSuccess(currentUser.getUid());
        } else {
            auth.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful() && auth.getCurrentUser() != null) {
                    callback.onSuccess(auth.getCurrentUser().getUid());
                } else {
                    callback.onFailure("Authentication failed");
                }
            });
        }
    }

    public void addTask(Task task, OperationCallback callback) {
        ensureAuthenticated(new AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                String taskId = database.child("users").child(userId).child("tasks").push().getKey();
                if (taskId != null) {
                    task.setId(taskId);
                    database.child("users").child(userId).child("tasks").child(taskId)
                            .setValue(task.toMap())
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                } else {
                    callback.onFailure("Failed to generate task ID");
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void updateTask(Task task, OperationCallback callback) {
        ensureAuthenticated(new AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                database.child("users").child(userId).child("tasks").child(task.getId())
                        .updateChildren(task.toMap())
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void deleteTask(String taskId, OperationCallback callback) {
        ensureAuthenticated(new AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                database.child("users").child(userId).child("tasks").child(taskId)
                        .removeValue()
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void getTasks(DataCallback<Task> callback) {
        ensureAuthenticated(new AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                database.child("users").child(userId).child("tasks")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                List<Task> tasks = new ArrayList<>();
                                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                                    Task task = taskSnapshot.getValue(Task.class);
                                    if (task != null) {
                                        task.setId(taskSnapshot.getKey());
                                        tasks.add(task);
                                    }
                                }
                                callback.onSuccess(tasks);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                callback.onFailure(error.getMessage());
                            }
                        });
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void addNote(Note note, OperationCallback callback) {
        ensureAuthenticated(new AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                String noteId = database.child("users").child(userId).child("notes").push().getKey();
                if (noteId != null) {
                    note.setId(noteId);
                    database.child("users").child(userId).child("notes").child(noteId)
                            .setValue(note.toMap())
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                } else {
                    callback.onFailure("Failed to generate note ID");
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void updateNote(Note note, OperationCallback callback) {
        ensureAuthenticated(new AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                database.child("users").child(userId).child("notes").child(note.getId())
                        .updateChildren(note.toMap())
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void deleteNote(String noteId, OperationCallback callback) {
        ensureAuthenticated(new AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                database.child("users").child(userId).child("notes").child(noteId)
                        .removeValue()
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void getNotes(DataCallback<Note> callback) {
        ensureAuthenticated(new AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                database.child("users").child(userId).child("notes")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                List<Note> notes = new ArrayList<>();
                                for (DataSnapshot noteSnapshot : snapshot.getChildren()) {
                                    Note note = noteSnapshot.getValue(Note.class);
                                    if (note != null) {
                                        note.setId(noteSnapshot.getKey());
                                        notes.add(note);
                                    }
                                }
                                callback.onSuccess(notes);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                callback.onFailure(error.getMessage());
                            }
                        });
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void addEvent(Event event, OperationCallback callback) {
        ensureAuthenticated(new AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                String eventId = database.child("users").child(userId).child("events").push().getKey();
                if (eventId != null) {
                    event.setId(eventId);
                    database.child("users").child(userId).child("events").child(eventId)
                            .setValue(event.toMap())
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                } else {
                    callback.onFailure("Failed to generate event ID");
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void deleteEvent(String eventId, OperationCallback callback) {
        ensureAuthenticated(new AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                database.child("users").child(userId).child("events").child(eventId)
                        .removeValue()
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void getEvents(DataCallback<Event> callback) {
        ensureAuthenticated(new AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                database.child("users").child(userId).child("events")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                List<Event> events = new ArrayList<>();
                                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                                    Event event = eventSnapshot.getValue(Event.class);
                                    if (event != null) {
                                        event.setId(eventSnapshot.getKey());
                                        events.add(event);
                                    }
                                }
                                callback.onSuccess(events);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                callback.onFailure(error.getMessage());
                            }
                        });
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }
}
