package com.pratham.prathamdigital.ui.fragment_admin_options;

import android.net.Uri;
import android.view.View;

import com.pratham.prathamdigital.models.Modal_NavigationMenu;

public interface ContractOptions {
    interface optionView {
        void onDataCleared();
    }

    interface optionPresenter {
        void clearData();

        void setView(Fragment_AdminOptions fragment_adminOptions);

        void updateDatabase(Uri treeUri);

        void databaseSuccessfullyUpdated();
    }

    interface optionAdapterClick {
        void menuClicked(int position, Modal_NavigationMenu modal_navigationMenu, View view);
    }
}
