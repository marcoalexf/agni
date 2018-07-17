package com.example.marisco.myapplication.constructors;

import java.util.List;

public class ListIds {

    private List<Long> uploadMediaIDs;

    public ListIds(List<Long> uploadMediaIds) {
        this.uploadMediaIDs = uploadMediaIds;
    }


    public List<Long> getUploadMediaIDs() {
        return uploadMediaIDs;
    }

    public void setUploadMediaIDs(List<Long> uploadMediaIDs) {
        this.uploadMediaIDs = uploadMediaIDs;
    }
}
