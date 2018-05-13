
package com.deacons_droid.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment {

    @SerializedName("pk")
    @Expose
    private Integer pk;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("public")
    @Expose
    private Boolean _public;

    public String getContent() {
        return content;
    }
    public Integer getPk() {
        return pk;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getPublic() {
        return _public;
    }

    public void setPublic(Boolean _public) {
        this._public = _public;
    }

}