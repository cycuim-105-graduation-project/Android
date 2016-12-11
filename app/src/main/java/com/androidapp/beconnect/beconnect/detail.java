package com.androidapp.beconnect.beconnect;

/**
 * Created by mitour on 2016/12/11.
 */

public class detail {
    String id;
    String name;
    String description;
    String feature_img_url;
    String start_at;
    String end_at;
    String registration_start_at;
    String registration_end_at;
    String quantity;
    String vacancy;
    String place;

    public detail(String id,String name, String description, String feature_img_url, String start_at, String end_at, String registration_start_at, String registration_end_at, String quantity, String vacancy, String place) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.feature_img_url = feature_img_url;
        this.start_at = start_at;
        this.end_at = end_at;
        this.registration_start_at = registration_start_at;
        this.registration_end_at = registration_end_at;
        this.quantity = quantity;
        this.vacancy = vacancy;
        this.place = place;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getFeature_img_url() {
        return feature_img_url;
    }
    public String getStart_at() {
        return start_at;
    }
    public String getEnd_at() {
        return end_at;
    }
    public String getRegistration_start_at() {
        return registration_start_at;
    }
    public String getRegistration_end_at() {
        return registration_end_at;
    }
    public String getQuantity() {
        return quantity;
    }
    public String getVacancy() {
        return vacancy;
    }
    public String getPlace() {
        return place;
    }

    public void setId(String name) {
        id = id;
    }
    public void setName(String name) {
        name = name;
    }
    public void setDescription(String description) {
        description = description;
    }
    public void setFeature_img_url(String feature_img_url) {
        feature_img_url = feature_img_url;
    }
    public void setStart_at(String start_at) {
        start_at = start_at;
    }
    public void setEnd_at(String end_at) {
        end_at = end_at;
    }
    public void setRegistration_start_at(String registration_start_at) {
        registration_start_at = registration_start_at;
    }
    public void setRegistration_end_at(String registration_end_at) {
        registration_end_at = registration_end_at;
    }
    public void setQuantity(String quantity) {
        quantity = quantity;
    }
    public void setVacancy(String vacancy) {
        vacancy = vacancy;
    }
    public void setPlace(String place) {
        place = place;
    }

}
