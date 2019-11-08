package com.pratham.prathamdigital.ui.pullData;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Village;

import java.util.ArrayList;
import java.util.List;

public class SelectVillageDialog extends Dialog {

    ImageButton btn_close;
    TextView clear_changes;
    TextView txt_message_village;
    TextView txt_ok;
    GridLayout flowLayout;

    private final Context context;
    private final List<Village> villageList;
    private final List<CheckBox> checkBoxes = new ArrayList<>();
    private VillageSelectListener villageSelectListener = null;


    SelectVillageDialog(@NonNull Context context, VillageSelectListener villageSelectListener, List tempList) {
        super(context, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        this.villageList = tempList;
        this.context = context;
        this.villageSelectListener = villageSelectListener;

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_village_dialog);
        clear_changes = findViewById(R.id.txt_clear_changes);
        btn_close = findViewById(R.id.btn_close);
        txt_message_village = findViewById(R.id.txt_message);
        txt_ok = findViewById(R.id.txt_ok);
        flowLayout = findViewById(R.id.flowLayout);
        // initialize clicks
        btn_close.setOnClickListener(closeClickListener);
        clear_changes.setOnClickListener(changeClickListener);
        txt_ok.setOnClickListener(okClickListener);

        setCanceledOnTouchOutside(false);
        setCancelable(false);
        txt_message_village.setText("Select Village");
        for (int i = 0; i < villageList.size(); i++) {
            CheckBox checkBox = new CheckBox(context);
            checkBox.setText(villageList.get(i).getVillageName());
            checkBox.setTag(villageList.get(i).getVillageId());
            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = GridLayout.LayoutParams.WRAP_CONTENT;
            param.setGravity(Gravity.FILL_HORIZONTAL);
            checkBox.setLayoutParams(param);
            flowLayout.addView(checkBox);
            checkBoxes.add(checkBox);
        }
    }

    private View.OnClickListener closeClickListener = v -> dismiss();
    private View.OnClickListener changeClickListener = v -> {
        for (int i = 0; i < checkBoxes.size(); i++) {
            checkBoxes.get(i).setChecked(false);
        }
    };
    private View.OnClickListener okClickListener = v -> {
        ArrayList<String> villageIDList = new ArrayList();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isChecked()) {
                villageIDList.add(checkBoxes.get(i).getTag().toString());
            }
        }
        villageSelectListener.getSelectedItems(villageIDList);
        dismiss();
    };
}

