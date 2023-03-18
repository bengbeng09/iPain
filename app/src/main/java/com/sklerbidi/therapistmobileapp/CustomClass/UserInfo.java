
//This is a model class used to represent user information

package com.sklerbidi.therapistmobileapp.CustomClass;

public class UserInfo {
    private String username;
    private String first_name;
    private String last_name;
    private String user_type;
    private String user_code;

    public UserInfo(String username, String first_name, String last_name, String user_type, String user_code) {
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.user_type = user_type;
        this.user_code = user_code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }
}
