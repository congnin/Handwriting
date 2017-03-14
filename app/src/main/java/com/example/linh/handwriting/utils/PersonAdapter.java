package com.example.linh.handwriting.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.linh.handwriting.R;

import java.util.ArrayList;

/**
 * Created by Linh on 3/10/2017.
 */

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {
    private final ArrayList<Person> mPersons;

    public PersonAdapter(ArrayList<Person> persons) {
        this.mPersons = persons;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Person person = mPersons.get(position);
        holder.mNameView.setText(person.getName());
        holder.mBirthdayView.setText(person.getBirthday());
    }

    @Override
    public int getItemCount() {
        return mPersons.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mBirthdayView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name_textview);
            mBirthdayView = (TextView) view.findViewById(R.id.birthday_textview);
        }
    }

    public void addAll(ArrayList<Person> persons) {
        mPersons.clear();
        mPersons.addAll(persons);
        notifyDataSetChanged();
    }

    public void add(Person person) {
        mPersons.add(person);
        notifyDataSetChanged();
    }
}
