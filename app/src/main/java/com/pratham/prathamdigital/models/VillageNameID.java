package com.pratham.prathamdigital.models;

public class VillageNameID {
    String villageName;
    String villageId;

    public VillageNameID( String villageName) {
        this.villageName = villageName;
    }

    public VillageNameID(String villaId, String villageName) {
        this.villageName = villageName;
        this.villageId = villaId;
    }

    @Override
    public String toString() {
        return villageName;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public String getVillageId() {
        return villageId;
    }

    public void setVillageId(String villageId) {
        this.villageId = villageId;
    }
}
