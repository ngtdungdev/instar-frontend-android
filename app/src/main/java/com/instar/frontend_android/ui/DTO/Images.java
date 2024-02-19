package com.instar.frontend_android.ui.DTO;
//Test avatar
public class Images {

    public static final int TYPE_PERSONAL_AVATAR = 0;
    public static final int TYPE_FRIEND_AVATAR = 1;

    private int type;
    private String name;

    private int imgPath;

    public Images(int type, String name, int imgPath) {
        this.type = type;
        this.name = name;
        this.imgPath = imgPath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImgPath() {
        return imgPath;
    }

    public void setImgPath(int imgPath) {
        this.imgPath = imgPath;
    }
}
