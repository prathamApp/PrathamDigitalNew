package com.pratham.prathamdigital.ui.fragment_admin_options;

import android.view.View;

import com.pratham.prathamdigital.models.Modal_NavigationMenu;

public interface ContractOptions {
    void menuClicked(int position, Modal_NavigationMenu modal_navigationMenu, View view);

    void toggleMenuIcon();
}
