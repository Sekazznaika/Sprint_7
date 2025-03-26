package pojo.requests;

public class ReqLogin {
    private String login;
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ReqLogin(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public ReqLogin() {
    }


}
