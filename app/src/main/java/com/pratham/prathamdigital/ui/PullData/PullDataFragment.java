package com.pratham.prathamdigital.ui.PullData;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.pratham.prathamdigital.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by PEF on 19/11/2018.
 */


public class PullDataFragment extends Fragment implements PullDataContract.PullDataView, VillageSelectListener {
    @BindView(R.id.rg_programs)
    RadioGroup radioGroupPrograms;

    @BindView(R.id.stateSpinner)
    Spinner stateSpinner;

    @BindView(R.id.blockSpinner)
    Spinner blockSpinner;

    PullDataContract.PullDataPresenter pullDataPresenter;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pull_data_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        pullDataPresenter = new PullDataPresenterImp(getActivity(), this);
        pullDataPresenter.loadSpinner();
        radioGroupPrograms.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
               pullDataPresenter.clearLists();
            }
        });
    }

    @Override
    public void showStatesSpinner(String[] states) {
        ArrayAdapter arrayStateAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, states);
        arrayStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(arrayStateAdapter);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos <= 0) {
                    clearBlockSpinner();
                } else {
                    int selectedRadioButtonId = radioGroupPrograms.getCheckedRadioButtonId();
                    if (selectedRadioButtonId == -1) {
                        Toast.makeText(getContext(), "Please Select Program", Toast.LENGTH_SHORT).show();
                    } else {
                        RadioButton radioButton = radioGroupPrograms.findViewById(selectedRadioButtonId);
                        String selectedProgram = radioButton.getText().toString();
                        pullDataPresenter.loadBloackSpinner(pos, selectedProgram);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
        }
        progressDialog.setCancelable(false);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    @Override
    public void shoConfermationDialog(int crlListCnt, int studentListcnt, int groupListCnt, int villageListCnt) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setTitle("Data Preview");
        dialogBuilder.setMessage("CRLList : " + crlListCnt + "\nstudentList : " + studentListcnt + "\ngroupsList : " + groupListCnt + "\nvillageList : " + villageListCnt);
        dialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                pullDataPresenter.saveData();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                pullDataPresenter.clearLists();
            }
        });
        dialogBuilder.show();
    }

    @Override
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void clearBlockSpinner() {
        blockSpinner.setSelection(0);
        blockSpinner.setEnabled(false);
    }

    @Override
    public void clearStateSpinner() {
        stateSpinner.setSelection(0);
    }

    @Override
    public void showBlocksSpinner(List blocks) {
        blockSpinner.setEnabled(true);
        ArrayAdapter arrayBlockAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, blocks);
        arrayBlockAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blockSpinner.setAdapter(arrayBlockAdapter);
        blockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos > 0) {
                    //open Village Dialog
                    String block = adapterView.getSelectedItem().toString();
                    pullDataPresenter.proccessVillageData(block);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void showVillageDialog(List villageList) {
        Dialog villageDialog = new SelectVillageDialog(getActivity(), this, villageList);
        villageDialog.show();
    }

    @Override
    public void getSelectedItems(ArrayList<String> villageIDList) {
        pullDataPresenter.downloadStudentAndGroup(villageIDList);
    }
}
