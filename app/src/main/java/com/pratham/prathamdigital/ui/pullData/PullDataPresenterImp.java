package com.pratham.prathamdigital.ui.pullData;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.interfaces.ApiResult;
import com.pratham.prathamdigital.models.ModalProgram;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Crl;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.models.Modal_Village;
import com.pratham.prathamdigital.models.RaspCrl;
import com.pratham.prathamdigital.models.RaspGroup;
import com.pratham.prathamdigital.models.RaspProgram;
import com.pratham.prathamdigital.models.RaspStudent;
import com.pratham.prathamdigital.models.RaspVillage;
import com.pratham.prathamdigital.models.Village;
import com.pratham.prathamdigital.util.APIs;
import com.pratham.prathamdigital.util.PD_Constant;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import static com.pratham.prathamdigital.util.APIs.ECCE;
import static com.pratham.prathamdigital.util.APIs.GP;
import static com.pratham.prathamdigital.util.APIs.HG;
import static com.pratham.prathamdigital.util.APIs.KGBV;
import static com.pratham.prathamdigital.util.APIs.PI;
import static com.pratham.prathamdigital.util.APIs.RI;
import static com.pratham.prathamdigital.util.APIs.SC;
import static com.pratham.prathamdigital.util.APIs.UP;

/**
 * Created by PEF on 20/11/2018.
 */
@EBean
public class PullDataPresenterImp implements PullDataContract.PullDataPresenter, ApiResult {
    private final Context context;
    private final List<Modal_Student> studentList = new ArrayList();
    private final List<Modal_Groups> groupList = new ArrayList();
    private final List<String> villageIDList = new ArrayList();
    @Bean(PD_ApiRequest.class)
    PD_ApiRequest pd_apiRequest;
    private PullDataContract.PullDataView pullDataView;
    private String selectedBlock;
    private String selectedProgram;
    private int count = 0;
    private int groupCount = 0;
    private ArrayList<Modal_Village> vilageList = new ArrayList<>();
    private List<Modal_Crl> crlList = new ArrayList<>();
    private List<ModalProgram> prgrmList = new ArrayList<>();

    public PullDataPresenterImp(Context context) {
        this.context = context;
    }

    @Override
    public void setView(PullDataFragment pullDataFragment) {
        this.pullDataView = pullDataFragment;
        pd_apiRequest.setApiResult(PullDataPresenterImp.this);
    }

    @Override
    public void loadSpinner() {
        String[] states = context.getResources().getStringArray(R.array.india_states);
        pullDataView.showStatesSpinner(states);
    }

    @Override
    public void proccessVillageData(String block) {
        ArrayList<Village> villageName = new ArrayList();
        for (Modal_Village vill : vilageList) {
            if (block.equalsIgnoreCase(vill.getBlock().trim()))
                villageName.add(new Village(vill.getVillageId(), vill.getVillageName()));
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
                    pd_apiRequest.pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
                case RI:
                    url = APIs.RIpullVillagesKolibriURL + selectedBlock;
                    pd_apiRequest.pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
                case SC:
                    url = APIs.SCpullVillagesKolibriURL + selectedBlock;
                    pd_apiRequest.pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
                case PI:
                    url = APIs.PIpullVillagesKolibriURL + selectedBlock;
                    pd_apiRequest
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
                case UP:
                    url = APIs.UPpullVillagesKolibriURL + selectedBlock;
                    pd_apiRequest
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
                case HG:
                    url = APIs.HGpullVillagesKolibriURL + selectedBlock;
                    pd_apiRequest
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
                case KGBV:
                    url = APIs.KGBVpullVillagesKolibriURL + selectedBlock;
                    pd_apiRequest
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
                case ECCE:
                    url = APIs.ECCEpullVillagesKolibriURL + selectedBlock;
                    pd_apiRequest
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
                case GP:
                    url = APIs.GPpullVillagesKolibriURL + selectedBlock;
                    pd_apiRequest
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
            }
        else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork() || PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork())
            switch (this.selectedProgram) {
                case APIs.HL:
                    url = APIs.HLpullVillagesServerURL + selectedBlock;
                    pd_apiRequest
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
                case RI:
                    url = APIs.RIpullVillagesServerURL + selectedBlock;
                    pd_apiRequest
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
                case SC:
                    url = APIs.SCpullVillagesServerURL + selectedBlock;
                    pd_apiRequest
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
                case PI:
                    url = APIs.PIpullVillagesServerURL + selectedBlock;
                    pd_apiRequest
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
                case UP:
                    url = APIs.UPpullVillagesServerURL + selectedBlock;
                    pd_apiRequest
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
                case HG:
                    url = APIs.HGpullVillagesServerURL + selectedBlock;
                    pd_apiRequest
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
                case KGBV:
                    url = APIs.KGBVpullVillagesServerURL + selectedBlock;
                    pd_apiRequest
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
                case ECCE:
                    url = APIs.ECCEpullVillagesServerURL + selectedBlock;
                    pd_apiRequest
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
                case GP:
                    url = APIs.GPpullVillagesServerURL + selectedBlock;
                    pd_apiRequest
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
            }
    }

    @Override
    public void downloadStudentAndGroup(ArrayList<String> villageIDList1) {
        //download Student groups and KOLIBRI_CRL
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
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
                        break;
                    case RI:
                        url = APIs.RIpullStudentsKolibriURL + id;
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
                        break;
                    case SC:
                        url = APIs.SCpullStudentsKolibriURL + id;
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
                        break;
                    case PI:
                        url = APIs.PIpullStudentsKolibriURL + id;
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
                        break;
                    case UP:
                        url = APIs.UPpullStudentsKolibriURL + id;
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
                        break;
                    case HG:
                        url = APIs.HGpullStudentsKolibriURL + id;
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
                        break;
                    case KGBV:
                        url = APIs.KGBVpullStudentsKolibriURL + id;
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
                        break;
                    case ECCE:
                        url = APIs.ECCEpullStudentsKolibriURL + id;
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
                        break;
                    case GP:
                        url = APIs.GPpullStudentsKolibriURL + id;
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
                        break;
                }
            }
        else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork() || PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork())
            for (String id : villageIDList) {
                String url;
                switch (selectedProgram) {
                    case APIs.HL:
                        url = APIs.HLpullStudentsServerURL + id;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                    case RI:
                        url = APIs.RIpullStudentsServerURL + id;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                    case SC:
                        url = APIs.SCpullStudentsServerURL + id;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                    case PI:
                        url = APIs.PIpullStudentsServerURL + id;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                    case UP:
                        url = APIs.UPpullStudentsServerURL + id;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                    case HG:
                        url = APIs.HGpullStudentsServerURL + id;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                    case KGBV:
                        url = APIs.KGBVpullStudentsServerURL + id;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                    case ECCE:
                        url = APIs.ECCEpullStudentsServerURL + id;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                    case GP:
                        url = APIs.GPpullStudentsServerURL + id;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                }
            }
    }

    private void loadGroups() {
        if (count >= villageIDList.size()) {
            groupCount = 0;
            groupList.clear();
            String urlgroup;
            if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT))
                for (String id : villageIDList) {
                    switch (selectedProgram) {
                        case APIs.HL:
                            urlgroup = APIs.HLpullGroupsKolibriURL + id;
                            pd_apiRequest
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
                            break;
                        case RI:
                            urlgroup = APIs.RIpullGroupsKolibriURL + id;
                            pd_apiRequest
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
                            break;
                        case SC:
                            urlgroup = APIs.SCpullGroupsKolibriURL + id;
                            pd_apiRequest
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
                            break;
                        case PI:
                            urlgroup = APIs.PIpullGroupsKolibriURL + id;
                            pd_apiRequest
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
                            break;
                        case UP:
                            urlgroup = APIs.UPpullGroupsKolibriURL + id;
                            pd_apiRequest
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
                            break;
                        case HG:
                            urlgroup = APIs.HGpullGroupsKolibriURL + id;
                            pd_apiRequest
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
                            break;
                        case KGBV:
                            urlgroup = APIs.KGBVpullGroupsKolibriURL + id;
                            pd_apiRequest
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
                            break;
                        case ECCE:
                            urlgroup = APIs.ECCEpullGroupsKolibriURL + id;
                            pd_apiRequest
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
                            break;
                        case GP:
                            urlgroup = APIs.GPpullGroupsKolibriURL + id;
                            pd_apiRequest
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
                            break;
                    }
                }
            else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork() || PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork())
                for (String id : villageIDList) {
                    switch (selectedProgram) {
                        case APIs.HL:
                            urlgroup = APIs.HLpullGroupsServerURL + id;
                            pd_apiRequest
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
                            break;
                        case RI:
                            urlgroup = APIs.RIpullGroupsServerURL + id;
                            pd_apiRequest
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
                            break;
                        case SC:
                            urlgroup = APIs.SCpullGroupsServerURL + id;
                            pd_apiRequest
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
                            break;
                        case PI:
                            urlgroup = APIs.PIpullGroupsServerURL + id;
                            pd_apiRequest
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
                            break;
                        case UP:
                            urlgroup = APIs.UPpullGroupsServerURL + id;
                            pd_apiRequest
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
                            break;
                        case HG:
                            urlgroup = APIs.HGpullGroupsServerURL + id;
                            pd_apiRequest
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
                            break;
                        case KGBV:
                            urlgroup = APIs.KGBVpullGroupsServerURL + id;
                            pd_apiRequest
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
                            break;
                        case ECCE:
                            urlgroup = APIs.ECCEpullGroupsServerURL + id;
                            pd_apiRequest
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
                            break;
                        case GP:
                            urlgroup = APIs.GPpullGroupsServerURL + id;
                            pd_apiRequest
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
                            break;
                    }
                }
        }
    }

    private void loadCRL() {
        if (groupCount >= villageIDList.size()) {
            String crlURL;
            if (crlList != null) {
                crlList.clear();
            }
            if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT))
                switch (selectedProgram) {
                    case APIs.HL:
                        crlURL = APIs.HLpullCrlsKolibriURL + selectedBlock;
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
                        break;
                    case RI:
                        crlURL = APIs.RIpullCrlsKolibriURL + selectedBlock;
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
                        break;
                    case SC:
                        crlURL = APIs.SCpullCrlsKolibriURL + selectedBlock;
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
                        break;
                    case PI:
                        crlURL = APIs.PIpullCrlsKolibriURL + selectedBlock;
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
                        break;
                    case UP:
                        crlURL = APIs.UPpullCrlsKolibriURL + selectedBlock;
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
                        break;
                    case HG:
                        crlURL = APIs.HGpullCrlsKolibriURL + selectedBlock;
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
                        break;
                    case KGBV:
                        crlURL = APIs.KGBVpullCrlsKolibriURL + selectedBlock;
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
                        break;
                    case ECCE:
                        crlURL = APIs.ECCEpullCrlsKolibriURL + selectedBlock;
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
                        break;
                    case GP:
                        crlURL = APIs.GPpullCrlsKolibriURL + selectedBlock;
                        pd_apiRequest
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
                        break;
                }
            else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork() || PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork())
                switch (selectedProgram) {
                    case APIs.HL:
                        crlURL = APIs.HLpullCrlsServerURL + selectedBlock;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
                        break;
                    case RI:
                        crlURL = APIs.RIpullCrlsServerURL + selectedBlock;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
                        break;
                    case SC:
                        crlURL = APIs.SCpullCrlsServerURL + selectedBlock;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
                        break;
                    case PI:
                        crlURL = APIs.PIpullCrlsServerURL + selectedBlock;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
                        break;
                    case UP:
                        crlURL = APIs.UPpullCrlsServerURL + selectedBlock;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
                        break;
                    case HG:
                        crlURL = APIs.HGpullCrlsServerURL + selectedBlock;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
                        break;
                    case KGBV:
                        crlURL = APIs.KGBVpullCrlsServerURL + selectedBlock;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
                        break;
                    case ECCE:
                        crlURL = APIs.ECCEpullCrlsServerURL + selectedBlock;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
                        break;
                    case GP:
                        crlURL = APIs.GPpullCrlsServerURL + selectedBlock;
                        pd_apiRequest
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
                        break;
                }
        }
    }

    //    @Background
    @Override
    public void saveData() {
        BaseActivity.crLdao.insertAllCRL(crlList);
        //To safely remove from a collection while iterating over it, Iterator should be used.
        Iterator<Modal_Student> i = studentList.iterator();
        while (i.hasNext()) {
            Modal_Student stu = i.next(); // must be called before you can call i.remove()
            if (stu.getGender().equalsIgnoreCase("deleted"))
                i.remove();
        }
        BaseActivity.studentDao.insertAllStudents(studentList);
        Iterator<Modal_Groups> gi = groupList.iterator();
        while (gi.hasNext()) {
            Modal_Groups stu = gi.next(); // must be called before you can call i.remove()
            if (stu.getDeviceId().equalsIgnoreCase("deleted"))
                gi.remove();
        }
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
                BaseActivity.statusDao.updateValue("programId", "10");
                break;
            case UP:
                BaseActivity.statusDao.updateValue("programId", "6");
                break;
            case HG:
                BaseActivity.statusDao.updateValue("programId", "13");
                break;
            case KGBV:
                BaseActivity.statusDao.updateValue("programId", "5");
                break;
            case ECCE:
                BaseActivity.statusDao.updateValue("programId", "8");
                break;
            case GP:
                BaseActivity.statusDao.updateValue("programId", "14");
                break;
            default:
                BaseActivity.statusDao.updateValue("programId", "1");
                break;
        }
        Toast.makeText(context, "Data Pulled Successful !", Toast.LENGTH_SHORT).show();
        pullDataView.openLoginActivity();
    }

    private void saveDownloadedVillages() {
        for (Modal_Village vill : vilageList) {
            if (villageIDList.contains(String.valueOf(vill.getVillageId())))
                BaseActivity.villageDao.insertVillage(vill);
        }
    }

    //    @Background
    @Override
    public void clearLists() {
        if (crlList != null) {
            crlList.clear();
        }
        studentList.clear();
        groupList.clear();
        if (vilageList != null) {
            vilageList.clear();
        }
        villageIDList.clear();
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
            ArrayList<RaspCrl> crlListTemp = gson.fromJson(response, listType);
            crlList.clear();
            for (RaspCrl raspCrl : crlListTemp) {
                crlList.addAll(raspCrl.getData());
            }
            pullDataView.closeProgressDialog();
            pullDataView.enableSaveButton();
        } else if (header.equalsIgnoreCase(PD_Constant.SERVER_CRL)) {
            Type listType = new TypeToken<List<Modal_Crl>>() {
            }.getType();
            List<Modal_Crl> temp = gson.fromJson(response, listType);
            crlList.addAll(temp);
            LinkedHashSet hs = new LinkedHashSet(crlList);
            crlList.clear();
            crlList.addAll(hs);
            pullDataView.closeProgressDialog();
            pullDataView.enableSaveButton();
        } else if (header.equalsIgnoreCase(PD_Constant.KOLIBRI_GRP)) {
            groupCount++;
            String json = response;
            Type listType = new TypeToken<List<RaspGroup>>() {
            }.getType();
            List<RaspGroup> groupListTemp = gson.fromJson(json, listType);
            for (RaspGroup raspGroup : groupListTemp) {
                groupList.addAll(raspGroup.getData());
            }
            loadCRL();
        } else if (header.equalsIgnoreCase(PD_Constant.SERVER_GRP)) {
            groupCount++;
            String json = response;
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
            String json = response;
            Type listType = new TypeToken<List<RaspStudent>>() {
            }.getType();
            List<RaspStudent> studentListTemp = gson.fromJson(json, listType);
            for (RaspStudent raspStudent : studentListTemp) {
                studentList.addAll(raspStudent.getData());
            }
            loadGroups();
        } else if (header.equalsIgnoreCase(PD_Constant.SERVER_STU)) {
            count++;
            String json = response;
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
            List<RaspVillage> raspvilageList = gson.fromJson(response, listType);
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
            vilageList = gson.fromJson(response, listType);
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
        } else if (header.equalsIgnoreCase(PD_Constant.KOLIBRI_PROGRAM)) {
            prgrmList.clear();
            Type listType = new TypeToken<List<RaspProgram>>() {
            }.getType();
            List<RaspProgram> prgm = gson.fromJson(response, listType);
            if (prgm != null) {
                List<String> progrm_names = new ArrayList<>();
                for (RaspProgram prg : prgm) {
                    progrm_names.add(prg.getData().getKolibriProgramName());
                }
                LinkedHashSet hs = new LinkedHashSet(progrm_names);//to remove redundant values
                progrm_names.clear();
                progrm_names.addAll(hs);
                progrm_names.add(0, "Select Program");
                pullDataView.showProgram(progrm_names);
            }
        } else if (header.equalsIgnoreCase(PD_Constant.SERVER_PROGRAM)) {
            prgrmList.clear();
            Type listType = new TypeToken<List<ModalProgram>>() {
            }.getType();
            prgrmList = gson.fromJson(response, listType);
            if (prgrmList != null) {
                ModalProgram modalProgram = new ModalProgram();
                modalProgram.setProgramId("-1");
                modalProgram.setProgramName("Select Program");
                prgrmList.add(0, modalProgram);
                List<String> programs = new ArrayList<>();
                for (ModalProgram prg : prgrmList) {
                    programs.add(prg.getProgramName());
                }
                pullDataView.showProgram(programs);
            }
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

    //    @Background
    @Override
    public void clearData() {
        clearData_();
        pullDataView.onDataClearToast();
    }

    @Background
    public void clearData_() {
        BaseActivity.villageDao.deleteAllVillages();
        BaseActivity.groupDao.deleteAllGroups();
        BaseActivity.studentDao.deleteAllStudents();
        BaseActivity.crLdao.deleteAllCRLs();
    }

    @Override
    public void loadProgrammes() {
        if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT))
            pd_apiRequest
                    .pullFromKolibri(PD_Constant.KOLIBRI_PROGRAM, PD_Constant.URL.DATASTORE_RASPBERY_PROGRAM_STATE_URL.toString());
        else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork() || PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork())
            pd_apiRequest
                    .pullFromInternet(PD_Constant.SERVER_PROGRAM, PD_Constant.URL.PULL_PROGRAMS.toString());
    }
}
