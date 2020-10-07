package com.alaan.roamu.pojo;

import java.io.Serializable;

public class promopojo implements Serializable {
    String promo_code ;
    String promo_title;
    String promo_desc;

    public String getPromo_code() {
        return promo_code;
    }

    public void setPromo_code(String promo_code) {
        this.promo_code = promo_code;
    }

    public String getPromo_title() {
        return promo_title;
    }

    public void setPromo_title(String promo_title) {
        this.promo_title = promo_title;
    }

    public String getPromo_desc() {
        return promo_desc;
    }

    public void setPromo_desc(String promo_desc) {
        this.promo_desc = promo_desc;
    }

    public String getPromo_policy() {
        return promo_policy;
    }

    public void setPromo_policy(String promo_policy) {
        this.promo_policy = promo_policy;
    }

    public Integer getPromo_percentage() {
        return promo_percentage;
    }

    public void setPromo_percentage(Integer promo_percentage) {
        this.promo_percentage = promo_percentage;
    }

    String promo_policy;
    Integer promo_percentage;

}
