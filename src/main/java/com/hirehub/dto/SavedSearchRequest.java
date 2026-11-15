
package com.hirehub.dto;

import jakarta.validation.constraints.Size;

public class SavedSearchRequest {

    @Size(max = 100, message = "Keyword cannot exceed 100 characters")
    private String keyword;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
