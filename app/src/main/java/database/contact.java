package database;

public class contact {
    private String name;
    private String phoneNumber;
    private int bool;

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getBool() {
        return bool;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setBool(int bool) {
        this.bool = bool;
    }
}
