package fyp.yuki.vitrox2.Recipe;

import java.io.Serializable;

public class Recipe implements Serializable {

    private String title;
    private String description;
    private String ingredients;
    private String score;
    private int carboPortion;
    private int proteinPortion;
    private int fatsPortion;
    private String PicPath;
    private String CaloriesTxt;

    public Recipe(String title, String description, String ingredients, String score, int carboPortion, int proteinPortion, int fatsPortion,String PicPath,String CaloriesTxt) {
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.score = score;
        this.carboPortion = carboPortion;
        this.proteinPortion = proteinPortion;
        this.fatsPortion = fatsPortion;
        this.PicPath = PicPath;
        this.CaloriesTxt = CaloriesTxt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getCarboPortion() {
        return carboPortion;
    }

    public void setCarboPortion(int carboPortion) {
        this.carboPortion = carboPortion;
    }

    public int getProteinPortion() {
        return proteinPortion;
    }

    public void setProteinPortion(int proteinPortion) {
        this.proteinPortion = proteinPortion;
    }

    public int getFatsPortion() {
        return fatsPortion;
    }

    public void setFatsPortion(int fatsPortion) {
        this.fatsPortion = fatsPortion;
    }

    public String getPicPath() {
        return PicPath;
    }

    public void setPicPath(String picPath) {
        PicPath = picPath;
    }

    public String getCaloriesTxt() {
        return CaloriesTxt;
    }

    public void setCaloriesTxt(String caloriesTxt) {
        CaloriesTxt = caloriesTxt;
    }
}
