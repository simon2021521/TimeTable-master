package com.ulan.timetable.activities;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.ulan.timetable.adapters.TeachersAdapter;
import com.ulan.timetable.model.Teacher;
import com.ulan.timetable.R;
import com.ulan.timetable.utils.AlertDialogsHelper;
import com.ulan.timetable.utils.DbHelper;

import java.util.ArrayList;



public class TeachersActivity extends AppCompatActivity {

    private Context context = this;
    private ListView listView;
    private DbHelper db;
    private TeachersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers);
        initAll();
    }

    private void initAll() {
        setupAdapter();
        setupListViewMultiSelect();
        setupCustomDialog();
    }
//分别调用3个方法
    private void setupAdapter() {
        db = new DbHelper(context);
        listView = findViewById(R.id.teacherlist);
        //返回view， 并用listview承接， 每一个listview都需要一个数据适配器类型区显示里面的数据
        //这与其他的textview有些不同
        adapter = new TeachersAdapter(TeachersActivity.this, listView, R.layout.listview_teachers_adapter, db.getTeacher());
        //创建适配器实例
        listView.setAdapter(adapter);
    }
//多项选择，长按情况洗，选中多个item，进行删除
    private void setupListViewMultiSelect() {
        final CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorTeachers);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = listView.getCheckedItemCount();
                mode.setTitle(checkedCount + " " + getResources().getString(R.string.selected));
                if(checkedCount == 0) mode.finish();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.toolbar_action_mode, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        ArrayList<Teacher> removelist = new ArrayList<>();
                        SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
                        for(int i = 0; i < checkedItems.size(); i++) {
                            int key = checkedItems.keyAt(i);
                            if (checkedItems.get(key)) {
                                db.deleteTeacherById(adapter.getItem(key));
                                removelist.add(adapter.getTeacherList().get(key));
                            }
                        }
                        adapter.getTeacherList().removeAll(removelist);
                        db.updateTeacher(adapter.getTeacher());
                        adapter.notifyDataSetChanged();
                        mode.finish();
                        return true;

                    default:
                        return false;
                }
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
    }
//set up  an customised dialog
    private void setupCustomDialog() {
        final View alertLayout = getLayoutInflater().inflate(R.layout.dialog_add_teacher, null);
        AlertDialogsHelper.getAddTeacherDialog(TeachersActivity.this, alertLayout, adapter);
    }
}
