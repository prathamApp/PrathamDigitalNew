package com.pratham.prathamdigital.ui.PullData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PEF on 19/11/2018.
 */

public interface PullDataContract {
    public interface PullDataView {
        public void showStatesSpinner(String[] states);

        public void showProgressDialog(String msg);

        public void shoConfermationDialog(int crlListCnt, int studentListcnt, int groupListCnt, int villageIDListCnt);

        public void closeProgressDialog();

        public void clearBlockSpinner();
        public void clearStateSpinner();

        public void showBlocksSpinner(List blocks);

        public void showVillageDialog(List villageList);

    }

    public interface PullDataPresenter {
        public void loadSpinner();

        public void proccessVillageData(String respnce);

        public void loadBloackSpinner(int pos, String selectedprogram);

        public void downloadStudentAndGroup(ArrayList<String> villageIDList);

        public void saveData();
        public void clearLists();
    }
}
