package com.pratham.prathamdigital.ui.PullData;

import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.dbclasses.PrathamDatabase;
import com.pratham.prathamdigital.models.Modal_Crl;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.models.Modal_Village;
import com.pratham.prathamdigital.models.Village;
import com.pratham.prathamdigital.util.APIs;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static com.pratham.prathamdigital.util.APIs.PI;
import static com.pratham.prathamdigital.util.APIs.RI;
import static com.pratham.prathamdigital.util.APIs.SC;

/**
 * Created by PEF on 20/11/2018.
 */

public class PullDataPresenterImp implements PullDataContract.PullDataPresenter {
    Context context;
    PullDataContract.PullDataView pullDataView;
    String selectedBlock;
    String selectedProgram;
    int count = 0;
    int groupCount = 0;
    ArrayList<Modal_Village> vilageList;
    List<Modal_Crl> crlList = new ArrayList<>();
    List<Modal_Student> studentList = new ArrayList();
    List<Modal_Groups> groupList = new ArrayList();
    List<String> villageIDList = new ArrayList();

    public PullDataPresenterImp(Context context, PullDataContract.PullDataView pullDataView) {
        this.context = context;
        this.pullDataView = pullDataView;
    }

    @Override
    public void loadSpinner() {
        String[] states = context.getResources().getStringArray(R.array.india_states);
        pullDataView.showStatesSpinner(states);
    }

    @Override
    public void proccessVillageData(String block) {
        ArrayList<Village> villageName = new ArrayList();
        for (Modal_Village village : vilageList) {
            if (block.equalsIgnoreCase(village.getBlock().trim()))
                villageName.add(new Village(village.getVillageId(), village.getVillageName()));
        }
        if (!villageName.isEmpty()) {
            pullDataView.showVillageDialog(villageName);
        }
    }

    @Override
    public void loadBloackSpinner(int pos, String selectedProgram) {
        String[] statesCodes = context.getResources().getStringArray(R.array.india_states_shortcode);
        selectedBlock = statesCodes[pos];
        this.selectedProgram = selectedProgram;
        pullDataView.showProgressDialog("loading Blocks");
        String url;
        switch (selectedProgram) {
            case APIs.HL:
                url = APIs.HLpullVillagesURL + selectedBlock;
                downloadblock(url);
                break;
           /* case APIs.UP:
                //todo urban
                url = APIs.UPpullVillagesURL + selectedBlock;
                downloadblock(url);
                break;
            case APIs.ECE:
                url = APIs.ECEpullVillagesURL + selectedBlock;
                downloadblock(url);
                break;*/
            case RI:
                url = APIs.RIpullVillagesURL + selectedBlock;
                downloadblock(url);
                break;
            case SC:
                url = APIs.SCpullVillagesURL + selectedBlock;
                downloadblock(url);
                break;
            case PI:
                url = APIs.PIpullVillagesURL + selectedBlock;
                downloadblock(url);
                break;
        }
    }

    private void downloadblock(String url) {
        AndroidNetworking.get(url).build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        List<String> blockList = new ArrayList<>();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Modal_Village>>() {
                        }.getType();
                        vilageList = gson.fromJson(response.toString(), listType);
                        if (vilageList != null) {
                            if (vilageList.isEmpty()) {
                                blockList.add("NO BLOCKS");
                            } else {
                                blockList.add("Select block");
                                for (Modal_Village village : vilageList) {
                                    blockList.add(village.getBlock());
                                }
                            }
                            LinkedHashSet hs = new LinkedHashSet(blockList);
                            blockList.clear();
                            blockList.addAll(hs);
                            pullDataView.showBlocksSpinner(blockList);
                        }
                        pullDataView.closeProgressDialog();
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        pullDataView.closeProgressDialog();
                        pullDataView.clearBlockSpinner();
                        pullDataView.showErrorToast();
                    }
                });
    }

    @Override
    public void downloadStudentAndGroup(ArrayList<String> villageIDList1) {
        //download Student groups and CRL
        // 1 download crl
        pullDataView.showProgressDialog("loading..");
        villageIDList.clear();
        villageIDList.addAll(villageIDList1);
        studentList.clear();
        count = 0;
        for (String id : villageIDList) {
            String url;
            switch (selectedProgram) {
                case APIs.HL:
                    url = APIs.HLpullStudentsURL + id;
                    loadStudent(url);
                    break;
              /*  case APIs.UP:
                    //todo urban
                    url = APIs.UPpullStudentsURL + id;
                    loadStudent(url);
                    break;
                case APIs.ECE:
                    url = APIs.ECEpullStudentsURL + id;
                    loadStudent(url);
                    break;*/
                case RI:
                    url = APIs.RIpullStudentsURL + id;
                    loadStudent(url);
                    break;
                case SC:
                    url = APIs.SCpullStudentsURL + id;
                    loadStudent(url);
                    break;
                case PI:
                    url = APIs.PIpullStudentsURL + id;
                    loadStudent(url);
                    break;
            }

        }
    }

    private void loadStudent(String url) {
        AndroidNetworking.get(url).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                count++;
                String json = response.toString();
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Modal_Student>>() {
                }.getType();
                List<Modal_Student> studentListTemp = gson.fromJson(json, listType);
                if (studentListTemp != null) {
                    studentList.addAll(studentListTemp);
                }

                loadGroups();
                //dismissDialog();
            }

            @Override
            public void onError(ANError error) {
                studentList.clear();
                pullDataView.closeProgressDialog();
                pullDataView.showErrorToast();
                // dismissDialog();
            }
        });
    }

    private void loadGroups() {
        if (count >= villageIDList.size()) {
            groupCount = 0;
            groupList.clear();
            String urlgroup;
            for (String id : villageIDList) {
                switch (selectedProgram) {
                    case APIs.HL:
                        urlgroup = APIs.HLpullGroupsURL + id;
                        downloadGroups(urlgroup);
                        break;
                   /* case APIs.UP:
                        //todo urban
                        urlgroup = APIs.UPpullGroupsURL + id;
                        downloadGroups(urlgroup);
                        break;
                    case APIs.ECE:
                        urlgroup = APIs.ECEpullGroupsURL + id;
                        downloadGroups(urlgroup);
                        break;*/
                    case RI:
                        urlgroup = APIs.RIpullGroupsURL + id;
                        downloadGroups(urlgroup);
                        break;
                    case SC:
                        urlgroup = APIs.SCpullGroupsURL + id;
                        downloadGroups(urlgroup);
                        break;
                    case PI:
                        urlgroup = APIs.PIpullGroupsURL + id;
                        downloadGroups(urlgroup);
                        break;
                }
            }
        }
    }

    private void downloadGroups(String url) {

        AndroidNetworking.get(url).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                groupCount++;
                String json = response.toString();
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Modal_Groups>>() {
                }.getType();
                List<Modal_Groups> groupListTemp = gson.fromJson(json, listType);
                if (groupListTemp != null) {
                    groupList.addAll(groupListTemp);
                }
                loadCRL();
            }

            @Override
            public void onError(ANError error) {
                studentList.clear();
                pullDataView.closeProgressDialog();
                pullDataView.showErrorToast();
                // dismissDialog();
            }
        });
    }

    private void loadCRL() {
        if (groupCount >= villageIDList.size()) {
            String crlURL;
            if (crlList != null) {
                crlList.clear();
            }
            switch (selectedProgram) {
                case APIs.HL:
                    crlURL = APIs.HLpullCrlsURL + selectedBlock;
                    downloadCRL(crlURL);
                    break;
               /* case APIs.UP:
                    //todo urban
                    crlURL = APIs.UPpullCrlsURL + selectedBlock;
                    downloadCRL(crlURL);
                    break;
                case APIs.ECE:
                    crlURL = APIs.ECEpullCrlsURL + selectedBlock;
                    downloadCRL(crlURL);
                    break;*/
                case RI:
                    crlURL = APIs.RIpullCrlsURL + selectedBlock;
                    downloadCRL(crlURL);
                    break;
                case SC:
                    crlURL = APIs.SCpullCrlsURL + selectedBlock;
                    downloadCRL(crlURL);
                    break;
                case PI:
                    crlURL = APIs.PIpullCrlsURL + selectedBlock;
                    downloadCRL(crlURL);
                    break;
            }
        }
    }

    private void downloadCRL(String url) {
        AndroidNetworking.get(url).build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Modal_Crl>>() {
                        }.getType();
                        ArrayList<Modal_Crl> crlListTemp = gson.fromJson(response.toString(), listType);
                        crlList.clear();
                        crlList.addAll(crlListTemp);
                        pullDataView.closeProgressDialog();
                        pullDataView.enableSaveButton();
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        pullDataView.closeProgressDialog();
                        pullDataView.showErrorToast();
                    }
                });
    }

    @Override
    public void saveData() {
        PrathamDatabase.getDatabaseInstance(context).getCrLdao().insertAllCRL(crlList);
        PrathamDatabase.getDatabaseInstance(context).getStudentDao().insertAllStudents(studentList);
        PrathamDatabase.getDatabaseInstance(context).getGroupDao().insertAllGroups(groupList);
        PrathamDatabase.getDatabaseInstance(context).getVillageDao().insertAllVillages(vilageList);
        pullDataView.openLoginActivity();
    }

    @Override
    public void clearLists() {
        if (crlList != null) {
            crlList.clear();
        }
        if (studentList != null) {
            studentList.clear();
        }
        if (groupList != null) {
            groupList.clear();
        }
        if (vilageList != null) {
            vilageList.clear();
        }
        if (villageIDList != null) {
            villageIDList.clear();
        }
        pullDataView.clearStateSpinner();
        pullDataView.clearBlockSpinner();
        pullDataView.disableSaveButton();
    }

    @Override
    public void onSaveClick() {
        pullDataView.shoConfermationDialog(crlList.size(), studentList.size(), groupList.size(), vilageList.size());
    }


}
