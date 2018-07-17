package com.example.aditi.todo_list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ItemAdaptor extends ArrayAdapter {
    ArrayList<Items> items;
    LayoutInflater layoutInflater;
    View view;
    ButtonClickListener clickListener;
    CheckBoxClickListener checkBoxClickListener;


    public ItemAdaptor(@NonNull Context context, ArrayList<Items> items,ButtonClickListener listener,CheckBoxClickListener checkBoxClickListener) {
        super(context,0, items);
        layoutInflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        this.items = items;
        this.clickListener=listener;
        this.checkBoxClickListener=checkBoxClickListener;
    }

    @Override
    public int getCount() {
        return items.size();
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        view =convertView;
        if(view ==null) {

            view = layoutInflater.inflate(R.layout.row_layout, parent, false);
            TextView t1 = view.findViewById(R.id.title);
            TextView t2 = view.findViewById(R.id.description);
            TextView t3 = view.findViewById(R.id.date);
            TextView t4 = view.findViewById(R.id.time);
            TextView t5 = view.findViewById(R.id.category);
            Button button1 = view.findViewById(R.id.deleteButton);
            Button button2=view.findViewById(R.id.impButtonShow);
            CheckBox c= view.findViewById(R.id.completedCheckbox);
            ViewHolder holder = new ViewHolder();
            holder.title=t1;
            holder.description=t2;
            holder.date=t3;
            holder.time=t4;
            holder.category=t5;
            holder.deleteButton= button1;
            holder.importantButton=button2;
            holder.completedCheckBox=c;
            view.setTag(holder);
        }
     final ViewHolder viewHolder = (ViewHolder) view.getTag();

     final Items i = items.get(position);
     viewHolder.title.setText(i.getTitle());
     viewHolder.description.setText(i.getDescription());
     viewHolder.date.setText(i.getDate());
     viewHolder.time.setText(i.getTime());
     viewHolder.category.setText(i.getCategory());
     if(i.isImportant()==1){
         viewHolder.importantButton.setVisibility(View.VISIBLE);
         viewHolder.importantButton.setEnabled(false);}
     else
         viewHolder.importantButton.setVisibility(View.GONE);

     if(i.isCompleted()==1){
         viewHolder.completedCheckBox.setOnCheckedChangeListener(null);
         viewHolder.completedCheckBox.setChecked(true);
         viewHolder.completedCheckBox.setEnabled(true);
     }
     else{
         viewHolder.completedCheckBox.setOnCheckedChangeListener(null);
         viewHolder.completedCheckBox.setEnabled(true);
         viewHolder.completedCheckBox.setChecked(false);
     }


     viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
         @Override

         public void onClick(View view) {

             clickListener.rowButtonClicked(position,i);

         }
     });


     viewHolder.completedCheckBox.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             if(i.isCompleted()==0) {
                 viewHolder.completedCheckBox.setChecked(true);
                 Toast.makeText(getContext(),"Task Completed",Toast.LENGTH_SHORT).show();
                 checkBoxClickListener.checkBoxClicked(i,1);
             }
             else if(i.isCompleted()==1){
                 viewHolder.completedCheckBox.setChecked(false);
                 Toast.makeText(getContext(),"Task Not Completed",Toast.LENGTH_SHORT).show();
                 checkBoxClickListener.checkBoxClicked(i,0);

             }
         }
     });


        return view;
    }
}
