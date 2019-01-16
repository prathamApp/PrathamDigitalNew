package com.pratham.prathamdigital.ui.PullData;

import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.interfaces.ApiResult;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Crl;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.models.Modal_Village;
import com.pratham.prathamdigital.models.RaspCrl;
import com.pratham.prathamdigital.models.RaspGroup;
import com.pratham.prathamdigital.models.RaspStudent;
import com.pratham.prathamdigital.models.RaspVillage;
import com.pratham.prathamdigital.models.Village;
import com.pratham.prathamdigital.util.APIs;
import com.pratham.prathamdigital.util.PD_Constant;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static com.pratham.prathamdigital.util.APIs.PI;
import static com.pratham.prathamdigital.util.APIs.RI;
import static com.pratham.prathamdigital.util.APIs.SC;
import static com.pratham.prathamdigital.util.APIs.UP;

/**
 * Created by PEF on 20/11/2018.
 */
@EBean
public class PullDataPresenterImp implements PullDataContract.PullDataPresenter, ApiResult {
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

    public PullDataPresenterImp(Context context) {
        this.context = context;
    }

    @Override
    public void setView(PullDataFragment pullDataFragment) {
        this.pullDataView = (PullDataContract.PullDataView) pullDataFragment;
    }

    @Override
    public void loadSpinner() {
        String[] states = context.getResources().getStringArray(R.array.india_states);
        pullDataView.showStatesSpinner(states);
    }

    //    @Background
    @Override
    public void proccessVillageData(String block) {
        ArrayList<Village> villageName = new ArrayList();
        for (Modal_Village vill : vilageList) {
//            for (Modal_Village village : raspVillage.getData()) {
            if (block.equalsIgnoreCase(vill.getBlock().trim()))
                villageName.add(new Village(vill.getVillageId(), vill.getVillageName()));
//            }
        }
        if (!villageName.isEmpty()) {
            pullDataView.showVillageDialog(villageName);
        }
    }

    @Override
    public void loadBloackSpinner(int pos, String selectedProgram) {
        pullDataView.showProgressDialog("loading Blocks");
        getUrlAndPull(pos, selectedProgram);
    }

    @Background
    public void getUrlAndPull(int pos, String selectedProgram) {
        String[] statesCodes = context.getResources().getStringArray(R.array.india_states_shortcode);
        selectedBlock = statesCodes[pos];
        this.selectedProgram = selectedProgram;
        String url;
        if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT))
            switch (this.selectedProgram) {
                case APIs.HL:
                    url = APIs.HLpullVillagesKolibriURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
//                    downloadblock(false, url);
                    break;
                case RI:
                    url = APIs.RIpullVillagesKolibriURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
//                    downloadblock(false, url);
                    break;
                case SC:
                    url = APIs.SCpullVillagesKolibriURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
//                    downloadblock(false, url);
                    break;
                case PI:
                    url = APIs.PIpullVillagesKolibriURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
//                    downloadblock(false, url);
                    break;
                case UP:
                    url = APIs.UPpullVillagesKolibriURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
//                    downloadblock(false, url);
                    break;
            }
        else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork() || PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork())
            switch (this.selectedProgram) {
                case APIs.HL:
                    url = APIs.HLpullVillagesServerURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
//                    downloadblock(true, url);
                    break;
                case RI:
                    url = APIs.RIpullVillagesServerURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
//                    downloadblock(true, url);
                    break;
                case SC:
                    url = APIs.SCpullVillagesServerURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
//                    downloadblock(true, url);
                    break;
                case PI:
                    url = APIs.PIpullVillagesServerURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
//                    downloadblock(true, url);
                    break;
                case UP:
                    url = APIs.UPpullVillagesServerURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
//                    downloadblock(true, url);
                    break;
            }
    }

    public void downloadblock(boolean isInternet, String url) {
        if (!isInternet) {
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("Authorization", getAuthHeader("pratham", "pratham"))
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            List<String> blockList = new ArrayList<>();
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<RaspVillage>>() {
                            }.getType();
                            List<RaspVillage> raspvilageList = gson.fromJson(response.toString(), listType);
                            if (raspvilageList != null) {
                                if (raspvilageList.isEmpty()) {
                                    blockList.add("NO BLOCKS");
                                } else {
                                    blockList.add("Select block");
                                    for (RaspVillage raspVillage : raspvilageList) {
                                        //                                    for (Modal_Village village : raspVillage.getData()) {
                                        vilageList.add(raspVillage.getData());
                                        blockList.add(raspVillage.getData().getBlock());
                                        //                                    }
                                    }
                                }
                                LinkedHashSet hs = new LinkedHashSet(blockList);
                                blockList.clear();
                                blockList.addAll(hs);
                                LinkedHashSet hs1 = new LinkedHashSet(vilageList);
                                vilageList.clear();
                                vilageList.addAll(hs1);
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
        } else {
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
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
                                    for (Modal_Village vill : vilageList) {
                                        //                                    for (Modal_Village village : raspVillage.getData()) {
                                        blockList.add(vill.getBlock());
                                        //                                    }
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
    }

    public String getAuthHeader(String ID, String pass) {
        String encoded = Base64.encodeToString((ID + ":" + pass).getBytes(), Base64.NO_WRAP);
        String returnThis = "Basic " + encoded;
        return returnThis;
    }

    //    @Background
    @Override
    public void downloadStudentAndGroup(ArrayList<String> villageIDList1) {
        //download Student groups and KOLIBRI_CRL
        // 1 download crl
        pullDataView.showProgressDialog("loading..");
        getStudentUrlAndFetch(villageIDList1);
    }

    @Background
    public void getStudentUrlAndFetch(ArrayList<String> villageIDList1) {
        villageIDList.clear();
        villageIDList.addAll(villageIDList1);
        studentList.clear();
        count = 0;
        if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT))
            for (String id : villageIDList) {
                String url;
                switch (selectedProgram) {
                    case APIs.HL:
                        url = APIs.HLpullStudentsKolibriURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
//                        loadStudent(false, url);
                        break;
                    case RI:
                        url = APIs.RIpullStudentsKolibriURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
//                        loadStudent(false, url);
                        break;
                    case SC:
                        url = APIs.SCpullStudentsKolibriURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
//                        loadStudent(false, url);
                        break;
                    case PI:
                        url = APIs.PIpullStudentsKolibriURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
//                        loadStudent(false, url);
                        break;
                    case UP:
                        url = APIs.UPpullStudentsKolibriURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
//                        loadStudent(false, url);
                        break;
                }
            }
        else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork() || PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork())
            for (String id : villageIDList) {
                String url;
                switch (selectedProgram) {
                    case APIs.HL:
                        url = APIs.HLpullStudentsServerURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
//                        loadStudent(true, url);
                        break;
                    case RI:
                        url = APIs.RIpullStudentsServerURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
//                        loadStudent(true, url);
                        break;
                    case SC:
                        url = APIs.SCpullStudentsServerURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
//                        loadStudent(true, url);
                        break;
                    case PI:
                        url = APIs.PIpullStudentsServerURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
//                        loadStudent(true, url);
                        break;
                    case UP:
                        url = APIs.UPpullStudentsServerURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
//                        loadStudent(true, url);
                        break;
                }
            }
    }

    public void loadStudent(boolean isInternet, String url) {
        if (!isInternet) {
            AndroidNetworking.get(url).addHeaders("Content-Type", "application/json")
                    .addHeaders("Authorization", getAuthHeader("pratham", "pratham"))
                    .build().getAsJSONArray(new JSONArrayRequestListener() {
                @Override
                public void onResponse(JSONArray response) {
                    count++;
                    String json = response.toString();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<RaspStudent>>() {
                    }.getType();
                    List<RaspStudent> studentListTemp = gson.fromJson(json, listType);
                    for (RaspStudent raspStudent : studentListTemp) {
                        for (Modal_Student student : raspStudent.getData()) {
                            studentList.add(student);
                        }
                    }
                    loadGroups();
                }

                @Override
                public void onError(ANError error) {
                    studentList.clear();
                    pullDataView.closeProgressDialog();
                    pullDataView.showErrorToast();
                }
            });
        } else {
            AndroidNetworking.get(url).addHeaders("Content-Type", "application/json")
                    .build().getAsJSONArray(new JSONArrayRequestListener() {
                @Override
                public void onResponse(JSONArray response) {
                    count++;
                    String json = response.toString();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Modal_Student>>() {
                    }.getType();
                    studentList = gson.fromJson(json, listType);
                    loadGroups();
                }

                @Override
                public void onError(ANError error) {
                    studentList.clear();
                    pullDataView.closeProgressDialog();
                    pullDataView.showErrorToast();
                }
            });
        }
    }

    public void loadGroups() {
        if (count >= villageIDList.size()) {
            groupCount = 0;
            groupList.clear();
            String urlgroup;
            if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT))
                for (String id : villageIDList) {
                    switch (selectedProgram) {
                        case APIs.HL:
                            urlgroup = APIs.HLpullGroupsKolibriURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
//                            downloadGroups(false, urlgroup);
                            break;
                        case RI:
                            urlgroup = APIs.RIpullGroupsKolibriURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
//                            downloadGroups(false, urlgroup);
                            break;
                        case SC:
                            urlgroup = APIs.SCpullGroupsKolibriURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
//                            downloadGroups(false, urlgroup);
                            break;
                        case PI:
                            urlgroup = APIs.PIpullGroupsKolibriURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
//                            downloadGroups(false, urlgroup);
                            break;
                        case UP:
                            urlgroup = APIs.UPpullGroupsKolibriURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
//                            downloadGroups(false, urlgroup);
                            break;
                    }
                }
            else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork() || PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork())
                for (String id : villageIDList) {
                    switch (selectedProgram) {
                        case APIs.HL:
                            urlgroup = APIs.HLpullGroupsServerURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
//                            downloadGroups(true, urlgroup);
                            break;
                        case RI:
                            urlgroup = APIs.RIpullGroupsServerURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
//                            downloadGroups(true, urlgroup);
                            break;
                        case SC:
                            urlgroup = APIs.SCpullGroupsServerURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
//                            downloadGroups(true, urlgroup);
                            break;
                        case PI:
                            urlgroup = APIs.PIpullGroupsServerURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
//                            downloadGroups(true, urlgroup);
                            break;
                        case UP:
                            urlgroup = APIs.UPpullGroupsServerURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
//                            downloadGroups(true, urlgroup);
                            break;
                    }
                }
        }
    }

    public void downloadGroups(boolean isInternet, String url) {
        if (!isInternet) {
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("Authorization", getAuthHeader("pratham", "pratham"))
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            groupCount++;
                            String json = response.toString();
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<RaspGroup>>() {
                            }.getType();
                            List<RaspGroup> groupListTemp = gson.fromJson(json, listType);
                            for (RaspGroup raspGroup : groupListTemp) {
                                for (Modal_Groups modal_groups : raspGroup.getData()) {
                                    groupList.add(modal_groups);
                                }

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
        } else {
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            groupCount++;
                            String json = response.toString();
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Modal_Groups>>() {
                            }.getType();
                            groupList = gson.fromJson(json, listType);
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
    }

    public void loadCRL() {
        if (groupCount >= villageIDList.size()) {
            String crlURL;
            if (crlList != null) {
                crlList.clear();
            }
            if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT))
                switch (selectedProgram) {
                    case APIs.HL:
                        crlURL = APIs.HLpullCrlsKolibriURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
//                        downloadCRL(false, crlURL);
                        break;
                    case RI:
                        crlURL = APIs.RIpullCrlsKolibriURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
//                        downloadCRL(false, crlURL);
                        break;
                    case SC:
                        crlURL = APIs.SCpullCrlsKolibriURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
//                        downloadCRL(false, crlURL);
                        break;
                    case PI:
                        crlURL = APIs.PIpullCrlsKolibriURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
//                        downloadCRL(false, crlURL);
                        break;
                    case UP:
                        crlURL = APIs.UPpullCrlsKolibriURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
//                        downloadCRL(false, crlURL);
                        break;
                }
            else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork() || PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork())
                switch (selectedProgram) {
                    case APIs.HL:
                        crlURL = APIs.HLpullCrlsServerURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
//                        downloadCRL(true, crlURL);
                        break;
                    case RI:
                        crlURL = APIs.RIpullCrlsServerURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
//                        downloadCRL(true, crlURL);
                        break;
                    case SC:
                        crlURL = APIs.SCpullCrlsServerURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
//                        downloadCRL(true, crlURL);
                        break;
                    case PI:
                        crlURL = APIs.PIpullCrlsServerURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
//                        downloadCRL(true, crlURL);
                        break;
                    case UP:
                        crlURL = APIs.UPpullCrlsServerURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
//                        downloadCRL(true, crlURL);
                        break;
                }
        }
    }

    public void downloadCRL(boolean isInternet, String url) {
        if (!isInternet) {
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("Authorization", getAuthHeader("pratham", "pratham"))
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            // do anything with response
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<RaspCrl>>() {
                            }.getType();
                            ArrayList<RaspCrl> crlListTemp = gson.fromJson(response.toString(), listType);
                            crlList.clear();
                            for (RaspCrl raspCrl : crlListTemp) {
                                for (Modal_Crl modal_crl : raspCrl.getData()) {
                                    crlList.add(modal_crl);
                                }
                            }
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
        } else {
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            // do anything with response
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Modal_Crl>>() {
                            }.getType();
                            crlList = gson.fromJson(response.toString(), listType);
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
    }

    //    @Background
    @Override
    public void saveData() {
        BaseActivity.crLdao.insertAllCRL(crlList);
        BaseActivity.studentDao.insertAllStudents(studentList);
        BaseActivity.groupDao.insertAllGroups(groupList);
        saveDownloadedVillages();

        switch (selectedProgram) {
            case APIs.HL:
                BaseActivity.statusDao.updateValue("programId", "1");
                break;
            case RI:
                BaseActivity.statusDao.updateValue("programId", "2");
                break;
            case SC:
                BaseActivity.statusDao.updateValue("programId", "3");
                break;
            case PI:
                BaseActivity.statusDao.updateValue("programId", "4");
                break;
            case UP:
                BaseActivity.statusDao.updateValue("programId", "6");
                break;
            default:
                BaseActivity.statusDao.updateValue("programId", "1");
                break;
        }
        Toast.makeText(context, "Data Pulled Successful !", Toast.LENGTH_SHORT).show();
        pullDataView.openLoginActivity();
    }

    public void saveDownloadedVillages() {
        for (Modal_Village vill : vilageList) {
//            for (Modal_Village v : vill.getData()) {
            if (villageIDList.contains(String.valueOf(vill.getVillageId())))
                BaseActivity.villageDao.insertVillage(vill);
//            }
        }
    }

    //    @Background
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
        pullDataView.showConfirmationDialog(crlList.size(), studentList.size(), groupList.size(), vilageList.size());
    }

    @Override
    public void recievedContent(String header, String response, ArrayList<Modal_ContentDetail> contentList) {
        Gson gson = new Gson();
        if (header.equalsIgnoreCase(PD_Constant.KOLIBRI_CRL)) {
            Type listType = new TypeToken<List<RaspCrl>>() {
            }.getType();
            ArrayList<RaspCrl> crlListTemp = gson.fromJson(response.toString(), listType);
            crlList.clear();
            for (RaspCrl raspCrl : crlListTemp) {
                for (Modal_Crl modal_crl : raspCrl.getData()) {
                    crlList.add(modal_crl);
                }
            }
            pullDataView.closeProgressDialog();
            pullDataView.enableSaveButton();
        } else if (header.equalsIgnoreCase(PD_Constant.SERVER_CRL)) {
            Type listType = new TypeToken<List<Modal_Crl>>() {
            }.getType();
            List<Modal_Crl> temp = gson.fromJson(response.toString(), listType);
            crlList.addAll(temp);
            LinkedHashSet hs = new LinkedHashSet(crlList);
            crlList.clear();
            crlList.addAll(hs);
            pullDataView.closeProgressDialog();
            pullDataView.enableSaveButton();
        } else if (header.equalsIgnoreCase(PD_Constant.KOLIBRI_GRP)) {
            groupCount++;
            String json = response.toString();
            Type listType = new TypeToken<List<RaspGroup>>() {
            }.getType();
            List<RaspGroup> groupListTemp = gson.fromJson(json, listType);
            for (RaspGroup raspGroup : groupListTemp) {
                for (Modal_Groups modal_groups : raspGroup.getData()) {
                    groupList.add(modal_groups);
                }
            }
            loadCRL();
        } else if (header.equalsIgnoreCase(PD_Constant.SERVER_GRP)) {
            groupCount++;
            String json = response.toString();
            Type listType = new TypeToken<List<Modal_Groups>>() {
            }.getType();
            List<Modal_Groups> temp = gson.fromJson(json, listType);
            groupList.addAll(temp);
            LinkedHashSet hs = new LinkedHashSet(groupList);
            groupList.clear();
            groupList.addAll(hs);
            loadCRL();
        } else if (header.equalsIgnoreCase(PD_Constant.KOLIBRI_STU)) {
            count++;
            String json = response.toString();
            Type listType = new TypeToken<List<RaspStudent>>() {
            }.getType();
            List<RaspStudent> studentListTemp = gson.fromJson(json, listType);
            for (RaspStudent raspStudent : studentListTemp) {
                for (Modal_Student student : raspStudent.getData()) {
                    studentList.add(student);
                }
            }
            loadGroups();
        } else if (header.equalsIgnoreCase(PD_Constant.SERVER_STU)) {
            count++;
            String json = response.toString();
            Type listType = new TypeToken<List<Modal_Student>>() {
            }.getType();
            List<Modal_Student> temp = gson.fromJson(json, listType);
            studentList.addAll(temp);
            LinkedHashSet hs = new LinkedHashSet(studentList);
            studentList.clear();
            studentList.addAll(hs);
            loadGroups();
        } else if (header.equalsIgnoreCase(PD_Constant.KOLIBRI_BLOCK)) {
            List<String> blockList = new ArrayList<>();
            Type listType = new TypeToken<List<RaspVillage>>() {
            }.getType();
            List<RaspVillage> raspvilageList = gson.fromJson(response.toString(), listType);
            if (raspvilageList != null) {
                if (raspvilageList.isEmpty()) {
                    blockList.add("NO BLOCKS");
                } else {
                    blockList.add("Select block");
                    for (RaspVillage raspVillage : raspvilageList) {
                        //                                    for (Modal_Village village : raspVillage.getData()) {
                        vilageList.add(raspVillage.getData());
                        blockList.add(raspVillage.getData().getBlock());
                        //                                    }
                    }
                }
                LinkedHashSet hs = new LinkedHashSet(blockList);
                blockList.clear();
                blockList.addAll(hs);
                LinkedHashSet hs1 = new LinkedHashSet(vilageList);
                vilageList.clear();
                vilageList.addAll(hs1);
                pullDataView.showBlocksSpinner(blockList);
            }
            pullDataView.closeProgressDialog();
        } else if (header.equalsIgnoreCase(PD_Constant.SERVER_BLOCK)) {
            List<String> blockList = new ArrayList<>();
            Type listType = new TypeToken<List<Modal_Village>>() {
            }.getType();
            vilageList = gson.fromJson(response.toString(), listType);
            if (vilageList != null) {
                if (vilageList.isEmpty()) {
                    blockList.add("NO BLOCKS");
                } else {
                    blockList.add("Select block");
                    for (Modal_Village vill : vilageList) {
                        //                                    for (Modal_Village village : raspVillage.getData()) {
                        blockList.add(vill.getBlock());
                        //                                    }
                    }
                }
                LinkedHashSet hs = new LinkedHashSet(blockList);
                blockList.clear();
                blockList.addAll(hs);
                pullDataView.showBlocksSpinner(blockList);
            }
            pullDataView.closeProgressDialog();
        }
    }

    @Override
    public void recievedError(String header, ArrayList<Modal_ContentDetail> contentList) {
        if (header.equalsIgnoreCase(PD_Constant.SERVER_BLOCK) || header.equalsIgnoreCase(PD_Constant.KOLIBRI_BLOCK)) {
            pullDataView.closeProgressDialog();
            pullDataView.clearBlockSpinner();
            pullDataView.showErrorToast();
        } else if (header.equalsIgnoreCase(PD_Constant.SERVER_STU) || header.equalsIgnoreCase(PD_Constant.KOLIBRI_STU)
                || header.equalsIgnoreCase(PD_Constant.SERVER_GRP) || header.equalsIgnoreCase(PD_Constant.KOLIBRI_GRP)) {
            studentList.clear();
            pullDataView.closeProgressDialog();
            pullDataView.showErrorToast();
        } else if (header.equalsIgnoreCase(PD_Constant.SERVER_CRL) || header.equalsIgnoreCase(PD_Constant.KOLIBRI_CRL)) {
            pullDataView.closeProgressDialog();
            pullDataView.showErrorToast();
        }
    }
}
