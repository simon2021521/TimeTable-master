package com.ulan.timetable.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ulan.timetable.model.Teacher;
import com.ulan.timetable.R;
import com.ulan.timetable.utils.AlertDialogsHelper;
import com.ulan.timetable.utils.DbHelper;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Ulan on 08.10.2018.
 */
public class TeachersAdapter extends ArrayAdapter<Teacher> {
    // create data type and relating attribute.
    // private attribute could be only used in the class, not outside class
    private Activity mActivity;
    private int mResource;
    private ArrayList<Teacher> teacherlist;
    private Teacher teacher;
    private ListView mListView;
    //create view type and relating attribute
    //for static key word, could call the method directly without new the instance
    // A ViewHolder keeps references to children views to avoid unneccessary calls
    // to findViewById() on each row.
    private static class ViewHolder {
        TextView name;
        TextView post;
        TextView phonenumber;
        TextView email;
        CardView cardView;
        ImageView popup;
    }
    // create the constructor for the class, assign the parameters to corresponding attributes.
    public TeachersAdapter(Activity activity, ListView listView, int resource, ArrayList<Teacher> objects) {
        super(activity, resource, objects);
        mActivity = activity;
        mListView = listView;
        mResource = resource;
        teacherlist = objects;
    }
    @NonNull
    @Override
    //every item in the list would call this method
    //https://blog.csdn.net/bear_wr/article/details/48935099
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        String name = Objects.requireNonNull(getItem(position)).getName();
        String post = Objects.requireNonNull(getItem(position)).getPost();
        String phonenumber = Objects.requireNonNull(getItem(position)).getPhonenumber();
        String email = Objects.requireNonNull(getItem(position)).getEmail();
        int color = Objects.requireNonNull(getItem(position)).getColor();

        teacher = new Teacher(name, post, phonenumber, email, color);
        final ViewHolder holder;
        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if(convertView == null){
            //LayoutInflater class is used to find out the xml layout, and instance it
            //is very similar with findViewById, which is used to find a view
            //https://blog.csdn.net/robertcpp/article/details/51523218
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            // call inflate method
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            //fetch everything in the item with different view.
            holder.name = convertView.findViewById(R.id.nameteacher);
            holder.post = convertView.findViewById(R.id.postteacher);
            holder.phonenumber = convertView.findViewById(R.id.numberteacher);
            holder.email = convertView.findViewById(R.id.emailteacher);
            holder.cardView = convertView.findViewById(R.id.teacher_cardview);
            holder.popup = convertView.findViewById(R.id.popupbtn);
            //set a tag to all the views in the holder, this could be a attributes to call by the instance
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(teacher.getName());
        holder.post.setText(teacher.getPost());
        holder.phonenumber.setText(teacher.getPhonenumber());
        holder.email.setText(teacher.getEmail());
        holder.cardView.setCardBackgroundColor(teacher.getColor());
        holder.popup.setOnClickListener(new View.OnClickListener() {
            @Override
            //delete or edit item view
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(mActivity, holder.popup);
                final DbHelper db = new DbHelper(mActivity);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete_popup:
                                db.deleteTeacherById(getItem(position));
                                db.updateTeacher(getItem(position));
                                teacherlist.remove(position);
                                notifyDataSetChanged();
                                return true;

                            case R.id.edit_popup:
                                final View alertLayout = mActivity.getLayoutInflater().inflate(R.layout.dialog_add_teacher, null);
                                AlertDialogsHelper.getEditTeacherDialog(mActivity, alertLayout, teacherlist, mListView, position);
                                notifyDataSetChanged();
                                return true;
                            default:
                                return onMenuItemClick(item);
                        }
                    }
                });
                popup.show();
            }
        });
//call method
        hidePopUpMenu(holder);

        return convertView;
    }
//fetch teacherlist
    public ArrayList<Teacher> getTeacherList() {
        return teacherlist;
    }
//获取teacher数据
    public Teacher getTeacher() {
        return teacher;
    }
//隐藏pop-up menu
     private void hidePopUpMenu(ViewHolder holder) {
        SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();
        //Returns the set of checked items in the list.
         // Returns the number of key-value mappings that this SparseBooleanArray currently stores.
        if (checkedItems.size() > 0) {
            for (int i = 0; i < checkedItems.size(); i++) {
                int key = checkedItems.keyAt(i);
                if (checkedItems.get(key)) {
                    holder.popup.setVisibility(View.INVISIBLE);
                    }
            }
        } else {
            holder.popup.setVisibility(View.VISIBLE);
        }
    }
}