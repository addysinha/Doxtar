package addysden.doctor.doxtar;

/**
 * Created by Abhyudaya sinha on 5/21/2016.
 */
public class UserDetails {
    private int id;
    private String userName;
    private String userCode;

    public UserDetails(String userName) {
        super();
        this.userName = userName;
    }

    public UserDetails(String userName, String userCode) {
        super();
        this.userName = userName;
        this.userCode = userCode;
    }

    public UserDetails() {
        super();
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", userName=" + userName + ", userCode=" + userCode + "]";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
