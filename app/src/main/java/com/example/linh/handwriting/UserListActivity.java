package com.example.linh.handwriting;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.linh.handwriting.utils.Person;
import com.example.linh.handwriting.utils.PersonAdapter;

import java.util.ArrayList;

/**
 * Created by Linh on 3/10/2017.
 */

public class UserListActivity extends AppCompatActivity implements View.OnClickListener {
    static final int REQUEST_ADD = 1;
    private ArrayList<Person> persons = new ArrayList<>();
    private PersonAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView searchImageView;
    private ImageView addImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        searchImageView = (ImageView) findViewById(R.id.searchPersonImage);
        addImageView = (ImageView) findViewById(R.id.addPersonImage);
        recyclerView = (RecyclerView) findViewById(R.id.listUserRecyclerView);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        searchImageView.setOnClickListener(this);
        addImageView.setOnClickListener(this);

        if (persons.size() == 0 || persons.isEmpty()) {
            initData();
            //adapter.addAll(persons);
        }
        adapter = new PersonAdapter(persons);
        if (savedInstanceState != null && savedInstanceState.containsKey("person")) {
            persons = savedInstanceState.getParcelableArrayList("person");
            adapter.addAll(persons);
        }
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initData() {
        Person person1 = new Person("Nguyen Van A", "01/01/1990");
        persons.add(person1);
        Person person2 = new Person("Nguyen Van B", "01/01/1990");
        persons.add(person2);
        Person person3 = new Person("Nguyen Thi C", "01/01/1990");
        persons.add(person3);
        Person person4 = new Person("Nguyen Van D", "01/01/1990");
        persons.add(person4);
        Person person5 = new Person("Nguyen Van E", "01/01/1990");
        persons.add(person5);
        Person person6 = new Person("Nguyen Van F", "01/01/1990");
        persons.add(person6);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (persons != null && !persons.isEmpty()) {
            outState.putParcelableArrayList("person", persons);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addPersonImage: {
                Intent intent = new Intent(this, AddActivity.class);
                startActivityForResult(intent, REQUEST_ADD);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD) {
            if (data == null) {
                return;
            }
            if (data.getExtras() != null && data.getExtras().containsKey("person")) {
                Person person = data.getExtras().getParcelable("person");
                adapter.add(person);
            }
        }
    }
}
