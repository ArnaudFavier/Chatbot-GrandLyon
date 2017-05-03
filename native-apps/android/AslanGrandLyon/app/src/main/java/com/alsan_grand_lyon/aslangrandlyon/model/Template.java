package com.alsan_grand_lyon.aslangrandlyon.model;

/**
 * Created by Nico on 03/05/2017.
 */

public class Template {
    String title;
    String imageUrl;
    String subtitle;
    String url;
    String buttonUrl;
    String buttonTitle;

    public Template(String title, String imageUrl, String subtitle, String url, String buttonUrl, String buttonTitle) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.subtitle = subtitle;
        this.url = url;
        this.buttonUrl = buttonUrl;
        this.buttonTitle = buttonTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String titre) {
        this.title = titre;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getButtonUrl() {
        return buttonUrl;
    }

    public void setButtonUrl(String buttonUrl) {
        this.buttonUrl = buttonUrl;
    }

    public String getButtonTitle() {
        return buttonTitle;
    }

    public void setButtonTitle(String buttonTitle) {
        this.buttonTitle = buttonTitle;
    }

    @Override
    public String toString() {
        return "Template{" +
                "title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", url='" + url + '\'' +
                ", buttonUrl='" + buttonUrl + '\'' +
                ", buttonTitle='" + buttonTitle + '\'' +
                '}';
    }
}
