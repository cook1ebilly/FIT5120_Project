package database;

public class contact {
    private String name;
    private String phoneNumber;
    private Integer bool;

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Integer getBool() {
        return bool;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setBool(Integer bool) {
        this.bool = bool;
    }
}
