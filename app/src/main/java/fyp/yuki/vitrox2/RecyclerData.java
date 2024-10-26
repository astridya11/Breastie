package fyp.yuki.vitrox2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RecyclerData {
    private String id;
    private String name;
    private String imgURL;
    private String grpDescription;
    private String date;
    private int member;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public Map<String, Object> getAllData() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", this.id);
        data.put("name", this.name);
        data.put("imgURL", this.imgURL);
        data.put("grpDescription", this.grpDescription);
        data.put("date", this.date);
        data.put("member", this.member);

        return data;
    }

    public RecyclerData(String id, String name, String imgURL, String grpDescription, String date, int member) {
        this.id = id;
        this.name = name;
        this.imgURL = imgURL;
        this.grpDescription = grpDescription;
        this.date = date;
        this.member = member;
    }
}
