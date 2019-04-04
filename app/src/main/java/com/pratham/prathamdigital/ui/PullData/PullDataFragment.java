package com.pratham.prathamdigital.ui.PullData;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.pratham.prathamdigital.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PEF on 19/11/2018.
 */

@EFragment(R.layout.pull_data_fragment)
public class PullDataFragment extends Fragment implements PullDataContract.PullDataView, VillageSelectListener {
    private static final String TAG = PullDataFragment.class.getSimpleName();
    @ViewById(R.id.rg_programs)
    RadioGroup radioGroupPrograms;

    @ViewById(R.id.stateSpinner)
    Spinner stateSpinner;

    @ViewById(R.id.blockSpinner)
    Spinner blockSpinner;

    @ViewById(R.id.save_button)
    Button save_button;

    @Bean(PullDataPresenterImp.class)
    PullDataContract.PullDataPresenter pullDataPresenter;

    ProgressDialog progressDialog;

    @AfterViews
    public void initialize() {
        pullDataPresenter.setView(PullDataFragment.this);
        pullDataPresenter.loadSpinner();
        radioGroupPrograms.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                pullDataPresenter.clearLists();
            }
        });
    }

    @UiThread
    @Override
    public void showStatesSpinner(String[] states) {
        ArrayAdapter arrayStateAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, states);
        arrayStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(arrayStateAdapter);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                disableSaveButton();
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
                Log.d(TAG, "onNothingSelected:");
            }
        });
    }

    @UiThread
    @Override
    public void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
        }
        progressDialog.setCancelable(false);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    @UiThread
    @Override
    public void showConfirmationDialog(int crlListCnt, int studentListcnt, int groupListCnt, int villageListCnt) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog);
        dialogBuilder.setCancelable(false);
        if (studentListcnt > 0) {
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
        } else {
            dialogBuilder.setTitle("   No students to save..         ");
            dialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    pullDataPresenter.clearLists();
                }
            });
            dialogBuilder.show();
        }
    }

    @UiThread
    @Override
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @UiThread
    @Override
    public void clearBlockSpinner() {
        blockSpinner.setSelection(0);
        blockSpinner.setEnabled(false);
    }

    @UiThread
    @Override
    public void clearStateSpinner() {
        stateSpinner.setSelection(0);
    }

    @UiThread
    @Override
    public void showBlocksSpinner(List blocks) {
        blockSpinner.setEnabled(true);
        ArrayAdapter arrayBlockAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, blocks);
        arrayBlockAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blockSpinner.setAdapter(arrayBlockAdapter);
        blockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                disableSaveButton();
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

    //
    @UiThread
    @Override
    public void showVillageDialog(List villageList) {
        Dialog villageDialog = new SelectVillageDialog(getActivity(), this, villageList);
        villageDialog.show();
    }

    @UiThread
    @Override
    public void disableSaveButton() {
        save_button.setEnabled(false);
    }

    @UiThread
    @Override
    public void enableSaveButton() {
        save_button.setEnabled(true);
    }

    @UiThread
    @Override
    public void showErrorToast() {
        Toast.makeText(getActivity(), "Please check connection", Toast.LENGTH_SHORT).show();
    }

    @UiThread
    @Override
    public void openLoginActivity() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @UiThread
    @Override
    public void getSelectedItems(ArrayList<String> villageIDList) {
        pullDataPresenter.downloadStudentAndGroup(villageIDList);
    }

    @Click(R.id.save_button)
    public void saveData() {
        pullDataPresenter.onSaveClick();
    }

    @Click(R.id.btn_clearData)
    public void clearData() {
        AlertDialog clearDataDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Clear Data")
                .setMessage("Are you sure you want to clear everything ?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        pullDataPresenter.clearData();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        clearDataDialog.show();
        clearDataDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }

    @UiThread
    @Override
    public void onDataClearToast() {
        Toast.makeText(getActivity(), "Data cleared Successfully", Toast.LENGTH_SHORT).show();
    }
}
