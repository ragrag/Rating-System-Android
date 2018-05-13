package com.deacons_droid.api.model;

/**
 * Created by Raggi on 10/28/2017.
 */

import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class UserStaff {

    @SerializedName("is_staff")
    @Expose
    private Boolean isStaff;

    public Boolean getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(Boolean isStaff) {
        this.isStaff = isStaff;
    }

}