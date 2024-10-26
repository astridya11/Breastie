package fyp.yuki.vitrox2.Intro;

public class ScreenItem {


    String title,description;

    int mainImg;

    public ScreenItem(String title, String description, int mainImg) {
        this.title = title;
        this.description = description;
        this.mainImg = mainImg;
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

    public int getMainImg() {
        return mainImg;
    }

    public void setMainImg(int mainImg) {
        this.mainImg = mainImg;
    }
}
