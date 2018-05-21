package com.nissan.alldriverguide.model;

import com.mobioapp.infinitipacket.model.EpubInfo;

/**
 * Created by shubha on 11/14/17.
 */

public class EPubInfoWithType extends EpubInfo {

    int ePubType = 0;

    public int getePubType() {
        return ePubType;
    }

    public void setePubType(int ePubType) {
        this.ePubType = ePubType;
    }
}
