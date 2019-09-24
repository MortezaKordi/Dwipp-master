package com.mystartup.bezanberimapp;

public class HomeKashi {

    private int image;
    private String title;
    private String subTitle;
    private String bgColor;

    public HomeKashi(int image, String title, String subTitle, String bgColor) {
        this.image = image;
        this.title = title;
        this.subTitle = subTitle;
        this.bgColor = bgColor;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public static int [] images = {

            R.drawable.cycling,
            R.drawable.walking,
            R.drawable.running,
            R.drawable.exercise,
            R.drawable.step,
            R.drawable.water,
            R.drawable.sleep,
            R.drawable.sm,
            R.drawable.cardiogram,
            R.drawable.scale,
            R.drawable.compress,
            R.drawable.nm
    };

    public static String [] titles = {

            "00:00",
            "00:00",
            "00:00",
            "00:00",
            "0",
            "0",
            "00:00",
            "00:00",
            "0",
            "0",
            "0",
            "0"
    };

    public static String [] subTitles = {

            "Hours",
            "Hours",
            "Hours",
            "Hours",
            "Step",
            "Glass",
            "Hours",
            "Hours",
            "Heart Rate",
            "Kilogram",
            "Day",
            "Smoking"
    };

}
