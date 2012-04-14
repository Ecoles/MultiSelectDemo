package com.locustgaming.multiselectdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import com.locustgaming.multiselectdemo.db.DemoDBM;

public class MultiSelectDemoActivity extends Activity
{
    // On Screen Objects
    protected Button showDialogButton;
    protected ListView selectedListView, multiListView;

    // Database Objects
    private DemoDBM demoDBM;
    private Cursor selectCur, multiCur;
    private SimpleCursorAdapter selectAdapter, multiAdapter;

    // Other Objects
    private int userId;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        userId = 1; // Would be equal to the ID of the user signed in
        demoDBM = new DemoDBM(this);

        setupListView();
        setupButton();
    }

    private void setupButton()
    {
        showDialogButton = (Button) findViewById(R.id.btnShowDialog);
        showDialogButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                showDialog(0);
            }
        });
    }

    private void setupListView()
    {
        selectedListView = (ListView) findViewById(R.id.lvSelectedList);

        demoDBM.open();
        selectCur = demoDBM.getSelectedList(userId);
        demoDBM.close();

        startManagingCursor(selectCur);
        selectAdapter = new SimpleCursorAdapter(this, R.layout.plain_list_item, selectCur, new String[] { DemoDBM.ID,
                DemoDBM.NAME }, new int[] { R.id.itemId, R.id.itemName });
        selectedListView.setAdapter(selectAdapter);
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id) {

        case 0:
            LayoutInflater factory = LayoutInflater.from(this);

            // Setup of the view for the dialog
            final View bindListDialog = factory.inflate(R.layout.multi_list_layout, null);
            multiListView = (ListView) bindListDialog.findViewById(R.id.multiList);

            return new AlertDialog.Builder(MultiSelectDemoActivity.this).setTitle(R.string.multiSelectTitle)
                    .setCancelable(false).setView(bindListDialog)
                    .setPositiveButton(R.string.btnClose, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            setupListView();
                        }
                    }).create();
        default:
            return null;
        }
    }

    @Override
    protected void onPrepareDialog(final int id, final Dialog dialog, Bundle args)
    {
        demoDBM.open();
        multiCur = demoDBM.getList(userId);
        startManagingCursor(multiCur);
        multiAdapter = new SimpleCursorAdapter(this, R.layout.check_list_item, multiCur, new String[] { DemoDBM.ID,
                DemoDBM.NAME, DemoDBM.SEL }, new int[] { R.id.itemId, R.id.itemName, R.id.itemCheck });
        demoDBM.close();

        multiAdapter.setViewBinder(new MyViewBinder());
        multiListView.setAdapter(multiAdapter);
    }

    public class MyViewBinder implements ViewBinder
    {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex)
        {
            int nCheckedIndex = cursor.getColumnIndex(DemoDBM.SEL);
            if (columnIndex == nCheckedIndex)
            {
                CheckBox cb = (CheckBox) view;
                boolean bChecked = (cursor.getInt(nCheckedIndex) != 0);
                cb.setChecked(bChecked);

                cb.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
                return true;
            }
            return false;
        }
    }

    public class MyOnCheckedChangeListener implements OnCheckedChangeListener
    {
        @Override
        public void onCheckedChanged(CompoundButton checkBox, boolean newVal)
        {
            View item = (View) checkBox.getParent(); // Gets the
                                                     // plain_list_item(Parent)
                                                     // of the Check Box
            int itemId = Integer.valueOf(((TextView) item.findViewById(R.id.itemId)).getText().toString());
            demoDBM.open();
            demoDBM.setChecked(itemId, userId, newVal);
            demoDBM.close();

        }
    }

}