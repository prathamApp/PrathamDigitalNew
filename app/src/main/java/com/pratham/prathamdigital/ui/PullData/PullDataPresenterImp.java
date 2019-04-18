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
import org.androidannotations.annotations.EBean;
import org.json.JSONArray;

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
    Context context;
    PullDataContract.PullDataView pullDataView;
    String selectedBlock;
    String selectedProgram;
    int count = 0;
    int groupCount = 0;
    ArrayList<Modal_Village> vilageList = new ArrayList<>();
    List<Modal_Crl> crlList = new ArrayList<>();
    List<Modal_Student> studentList = new ArrayList();
    List<Modal_Groups> groupList = new ArrayList();
    List<String> villageIDList = new ArrayList();
    List<ModalProgram> prgrmList = new ArrayList<>();

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
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
                case RI:
                    url = APIs.RIpullVillagesKolibriURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
                case SC:
                    url = APIs.SCpullVillagesKolibriURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
                case PI:
                    url = APIs.PIpullVillagesKolibriURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
                case UP:
                    url = APIs.UPpullVillagesKolibriURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
                case HG:
                    url = APIs.HGpullVillagesKolibriURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
                case KGBV:
                    url = APIs.KGBVpullVillagesKolibriURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
                case ECCE:
                    url = APIs.ECCEpullVillagesKolibriURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
                case GP:
                    url = APIs.GPpullVillagesKolibriURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromKolibri(PD_Constant.KOLIBRI_BLOCK, url);
                    break;
            }
        else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork() || PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork())
            switch (this.selectedProgram) {
                case APIs.HL:
                    url = APIs.HLpullVillagesServerURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
                case RI:
                    url = APIs.RIpullVillagesServerURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
                case SC:
                    url = APIs.SCpullVillagesServerURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
                case PI:
                    url = APIs.PIpullVillagesServerURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
                case UP:
                    url = APIs.UPpullVillagesServerURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
                case HG:
                    url = APIs.HGpullVillagesServerURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
                case KGBV:
                    url = APIs.KGBVpullVillagesServerURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
                case ECCE:
                    url = APIs.ECCEpullVillagesServerURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
                case GP:
                    url = APIs.GPpullVillagesServerURL + selectedBlock;
                    new PD_ApiRequest(context, PullDataPresenterImp.this)
                            .pullFromInternet(PD_Constant.SERVER_BLOCK, url);
                    break;
            }
    }

    public String getAuthHeader(String ID, String pass) {
        String encoded = Base64.encodeToString((ID + ":" + pass).getBytes(), Base64.NO_WRAP);
        String returnThis = "Basic " + encoded;
        return returnThis;
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
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
                        break;
                    case RI:
                        url = APIs.RIpullStudentsKolibriURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
                        break;
                    case SC:
                        url = APIs.SCpullStudentsKolibriURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
                        break;
                    case PI:
                        url = APIs.PIpullStudentsKolibriURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
                        break;
                    case UP:
                        url = APIs.UPpullStudentsKolibriURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
                        break;
                    case HG:
                        url = APIs.HGpullStudentsKolibriURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
                        break;
                    case KGBV:
                        url = APIs.KGBVpullStudentsKolibriURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
                        break;
                    case ECCE:
                        url = APIs.ECCEpullStudentsKolibriURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_STU, url);
                        break;
                    case GP:
                        url = APIs.GPpullStudentsKolibriURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
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
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                    case RI:
                        url = APIs.RIpullStudentsServerURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                    case SC:
                        url = APIs.SCpullStudentsServerURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                    case PI:
                        url = APIs.PIpullStudentsServerURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                    case UP:
                        url = APIs.UPpullStudentsServerURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                    case HG:
                        url = APIs.HGpullStudentsServerURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                    case KGBV:
                        url = APIs.KGBVpullStudentsServerURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                    case ECCE:
                        url = APIs.ECCEpullStudentsServerURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                    case GP:
                        url = APIs.GPpullStudentsServerURL + id;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_STU, url);
                        break;
                }
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
                            break;
                        case RI:
                            urlgroup = APIs.RIpullGroupsKolibriURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
                            break;
                        case SC:
                            urlgroup = APIs.SCpullGroupsKolibriURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
                            break;
                        case PI:
                            urlgroup = APIs.PIpullGroupsKolibriURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
                            break;
                        case UP:
                            urlgroup = APIs.UPpullGroupsKolibriURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
                            break;
                        case HG:
                            urlgroup = APIs.HGpullGroupsKolibriURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
                            break;
                        case KGBV:
                            urlgroup = APIs.KGBVpullGroupsKolibriURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
                            break;
                        case ECCE:
                            urlgroup = APIs.ECCEpullGroupsKolibriURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
                            break;
                        case GP:
                            urlgroup = APIs.GPpullGroupsKolibriURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromKolibri(PD_Constant.KOLIBRI_GRP, urlgroup);
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
                            break;
                        case RI:
                            urlgroup = APIs.RIpullGroupsServerURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
                            break;
                        case SC:
                            urlgroup = APIs.SCpullGroupsServerURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
                            break;
                        case PI:
                            urlgroup = APIs.PIpullGroupsServerURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
                            break;
                        case UP:
                            urlgroup = APIs.UPpullGroupsServerURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
                            break;
                        case HG:
                            urlgroup = APIs.HGpullGroupsServerURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
                            break;
                        case KGBV:
                            urlgroup = APIs.KGBVpullGroupsServerURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
                            break;
                        case ECCE:
                            urlgroup = APIs.ECCEpullGroupsServerURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
                            break;
                        case GP:
                            urlgroup = APIs.GPpullGroupsServerURL + id;
                            new PD_ApiRequest(context, PullDataPresenterImp.this)
                                    .pullFromInternet(PD_Constant.SERVER_GRP, urlgroup);
                            break;
                    }
                }
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
                        break;
                    case RI:
                        crlURL = APIs.RIpullCrlsKolibriURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
                        break;
                    case SC:
                        crlURL = APIs.SCpullCrlsKolibriURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
                        break;
                    case PI:
                        crlURL = APIs.PIpullCrlsKolibriURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
                        break;
                    case UP:
                        crlURL = APIs.UPpullCrlsKolibriURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
                        break;
                    case HG:
                        crlURL = APIs.HGpullCrlsKolibriURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
                        break;
                    case KGBV:
                        crlURL = APIs.KGBVpullCrlsKolibriURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
                        break;
                    case ECCE:
                        crlURL = APIs.ECCEpullCrlsKolibriURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
                        break;
                    case GP:
                        crlURL = APIs.GPpullCrlsKolibriURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromKolibri(PD_Constant.KOLIBRI_CRL, crlURL);
                        break;
                }
            else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork() || PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork())
                switch (selectedProgram) {
                    case APIs.HL:
                        crlURL = APIs.HLpullCrlsServerURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
                        break;
                    case RI:
                        crlURL = APIs.RIpullCrlsServerURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
                        break;
                    case SC:
                        crlURL = APIs.SCpullCrlsServerURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
                        break;
                    case PI:
                        crlURL = APIs.PIpullCrlsServerURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
                        break;
                    case UP:
                        crlURL = APIs.UPpullCrlsServerURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
                        break;
                    case HG:
                        crlURL = APIs.HGpullCrlsServerURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
                        break;
                    case KGBV:
                        crlURL = APIs.KGBVpullCrlsServerURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
                        break;
                    case ECCE:
                        crlURL = APIs.ECCEpullCrlsServerURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
                        break;
                    case GP:
                        crlURL = APIs.GPpullCrlsServerURL + selectedBlock;
                        new PD_ApiRequest(context, PullDataPresenterImp.this)
                                .pullFromInternet(PD_Constant.SERVER_CRL, crlURL);
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
        } else if (header.equalsIgnoreCase(PD_Constant.KOLIBRI_PROGRAM)) {
            prgrmList.clear();
            Type listType = new TypeToken<List<RaspProgram>>() {
            }.getType();
            List<RaspProgram> prgm = gson.fromJson(response.toString(), listType);
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
            prgrmList = gson.fromJson(response.toString(), listType);
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
            new PD_ApiRequest(context, PullDataPresenterImp.this)
                    .pullFromKolibri(PD_Constant.KOLIBRI_PROGRAM, PD_Constant.URL.DATASTORE_RASPBERY_PROGRAM_STATE_URL.toString());
        else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork() || PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork())
            new PD_ApiRequest(context, PullDataPresenterImp.this)
                    .pullFromInternet(PD_Constant.SERVER_PROGRAM, PD_Constant.URL.PULL_PROGRAMS.toString());
    }
}
