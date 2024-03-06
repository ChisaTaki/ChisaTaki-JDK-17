package dev.kurumidisciples.chisataki.booster;

public class Booster {
    

    private long userId;
    private String roleId = null;

    public Booster(long userId, String roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public long getUserId() {
        return userId;
    }

    public String getRoleId() {
        return roleId;
    }
}
