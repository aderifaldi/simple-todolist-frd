package com.radyalabs.todolistusefrd.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final String TABLE_TODOS = "todos";
    private static final String APP_TITLE = "app_title";
    private static final String IS_DONE = "is_done";

    @BindView(R.id.listTodo) RecyclerView listTodo;
    @BindView(R.id.edtTodo) EditText edtTodo;
    @BindView(R.id.loading) ProgressBar loading;
    @BindView(R.id.txtEmptyInfo) TextView txtEmptyInfo;

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

        adapter = new TodoListAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this);

        listTodo.setAdapter(adapter);
        listTodo.setLayoutManager(linearLayoutManager);
        listTodo.addItemDecoration(new SimpleDividerItemDecoration(this));

        mFirebaseInstance = FirebaseDatabase.getInstance();

        mFirebaseDatabase = mFirebaseInstance.getReference(TABLE_TODOS);
        mFirebaseDatabase.keepSynced(true);

        mFirebaseInstance.getReference(APP_TITLE).addValueEventListener(new ValueEventListener() {
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

        loading.setVisibility(View.VISIBLE);
        loadTodoList();

        adapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Todo todo = adapter.getData().get(position);
                mFirebaseDatabase.child(todo.id).setValue(null);

                return true;
            }
        });

    }

    public void setDone(String id, boolean isDone){
        mFirebaseDatabase.child(id).child(IS_DONE).setValue(isDone);
    }

    private void loadTodoList(){
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loading.setVisibility(View.VISIBLE);
                adapter.getData().clear();

                if (dataSnapshot.getChildrenCount() != 0){
                    txtEmptyInfo.setVisibility(View.GONE);

                    for (DataSnapshot data : dataSnapshot.getChildren()){
                        Todo todo = data.getValue(Todo.class);
                        adapter.getData().add(todo);
                        adapter.notifyItemInserted(adapter.getData().size() - 1);
                    }
                    adapter.notifyDataSetChanged();

                }else {
                    txtEmptyInfo.setVisibility(View.VISIBLE);
                }

                loading.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();

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
            insertTodo(todoId, todoItem, false);
        }

    }

    private void insertTodo(String todoId, String todoItem, boolean isDone){
        Todo todo = new Todo(todoId, todoItem, isDone);
        mFirebaseDatabase.child(todoId).setValue(todo);
        edtTodo.setText("");
    }

}
