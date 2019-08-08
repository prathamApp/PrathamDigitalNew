package com.pratham.prathamdigital.custom.expansionpanel;

import java.util.Collection;
import java.util.HashSet;

public class ExpansionLayoutCollection {

    private final Collection<ExpansionLayout> expansions = new HashSet<>();
    private final ExpansionLayout.IndicatorListener indicatorListener = new ExpansionLayout.IndicatorListener() {
        @Override
        public void onStartedExpand(ExpansionLayout expansionLayout, boolean willExpand) {
            if (willExpand) {
                for (ExpansionLayout view : expansions) {
                    if (view != expansionLayout) {
                        view.collapse(true);
                    }
                }
            }
        }
    };
    private boolean openOnlyOne = false;

    public ExpansionLayoutCollection add(ExpansionLayout expansionLayout) {
        expansions.add(expansionLayout);
        expansionLayout.addIndicatorListener(indicatorListener);
        return this;
    }

    public ExpansionLayoutCollection addAll(ExpansionLayout... expansionLayouts) {
        for (ExpansionLayout expansionLayout : expansionLayouts) {
            add(expansionLayout);
        }
        return this;
    }

    public ExpansionLayoutCollection addAll(Collection<ExpansionLayout> expansionLayouts) {
        for (ExpansionLayout expansionLayout : expansionLayouts) {
            add(expansionLayout);
        }
        return this;
    }

    public ExpansionLayoutCollection remove(ExpansionLayout expansionLayout) {
        if (expansionLayout != null) {
            expansions.remove(expansionLayout);
            expansionLayout.removeIndicatorListener(indicatorListener);
        }
        return this;
    }

    public ExpansionLayoutCollection openOnlyOne(boolean openOnlyOne) {
        this.openOnlyOne = openOnlyOne;
        return this;
    }
}