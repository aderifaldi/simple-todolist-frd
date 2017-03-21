package com.radyalabs.todolistusefrd.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.radyalabs.todolistusefrd.R;
import com.radyalabs.todolistusefrd.adapter.TodoListAdapter;
import com.radyalabs.todolistusefrd.helper.SimpleDividerItemDecoration;
import com.radyalabs.todolistusefrd.model.Todo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.listTodo) RecyclerView listTodo;
    @BindView(R.id.edtTodo) EditText edtTodo;
    @BindView(R.id.loading) ProgressBar loading;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private TodoListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private String todoItem, todoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        loading.setVisibility(View.VISIBLE);

        adapter = new TodoListAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this);

        listTodo.setAdapter(adapter);
        listTodo.setLayoutManager(linearLayoutManager);
        listTodo.addItemDecoration(new SimpleDividerItemDecoration(this));

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("todos");

        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");
                String appTitle = dataSnapshot.getValue(String.class);
                getSupportActionBar().setTitle(appTitle);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

        loadTodoList();

    }

    public void deleteTodo(String id){
        mFirebaseDatabase.child(id).setValue(null);
    }

    private void loadTodoList(){
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loading.setVisibility(View.VISIBLE);

                adapter.getData().clear();
                for (DataSnapshot data : dataSnapshot.getChildren()){

                    Todo todo = data.getValue(Todo.class);
                    adapter.getData().add(todo);
                    adapter.notifyItemInserted(adapter.getData().size() - 1);

                }

                adapter.notifyDataSetChanged();
                loading.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.addTodo)
    public void addTodo() {
        todoId = mFirebaseDatabase.push().getKey();
        todoItem = edtTodo.getText().toString();

        if (todoItem == "" || todoItem.equals("")){
            edtTodo.setError("Couldn't add empty todo!");
        }else {
            insertTodo(todoId, todoItem);
        }

    }

    private void insertTodo(String todoId, String todoItem){
        Todo todo = new Todo(todoId, todoItem);
        mFirebaseDatabase.child(todoId).setValue(todo);
        edtTodo.setText("");
    }

}
