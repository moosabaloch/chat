package pana.com.chat;

/**
 * Created by Moosa on 9/10/2015.
 * Dear Maintainer
 * When i wrote this code Only i and God knew What it was.
 * Now only God Knows..!
 * So if you are done trying to optimize this routine and Failed
 * Please increment the following counter as the warning to the next Guy.
 * TOTAL_HOURS_WASTED_HERE=1
 */
public class DataModelMeSingleton {
    private static DataModelMeSingleton ourInstance;
    private String id;
    private String imageUrl;
    private String name;
    private String phone;

    @Override
    public String toString() {
        return "DataModelMeSingleton{" +
                "id='" + id + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private DataModelMeSingleton() {
    }

    public static DataModelMeSingleton getInstance() {
        if (ourInstance == null) {
            ourInstance = new DataModelMeSingleton();
        }
        return ourInstance;
    }
}
