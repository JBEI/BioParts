package org.abf.bps.lib.dto.user;

import org.abf.bps.lib.dto.IDataTransferModel;
import org.abf.bps.lib.dto.bulkupload.PreferenceInfo;

import java.util.ArrayList;

/**
 * Wrapper for user preferences
 *
 * @author Hector Plahar
 */
public class UserPreferences implements IDataTransferModel {

    private String userId;
    private ArrayList<PreferenceInfo> preferences;

    public UserPreferences() {
        preferences = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<PreferenceInfo> getPreferences() {
        return preferences;
    }
}
