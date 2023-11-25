package dev.kurumidisciples.chisataki.secretsanta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class SantaStruct {
    
    private final long userId;
    private final String preferredGift;
    private final String comments;
    private final String chisataki;

    public SantaStruct(long userId, String preferredGift, String comments, String chisataki) {
        this.userId = userId;
        this.preferredGift = preferredGift;
        this.comments = comments;
        this.chisataki = chisataki;   
    }

    public static List<SantaStruct> fromResultSet(@Nonnull ResultSet set) throws SQLException {
        List<SantaStruct> list = new ArrayList<>();
        while(set.next()){
            list.add(new SantaStruct(set.getLong("user_id"), set.getString("preferred_gift"), set.getString("comments"), set.getString("chisataki")));
        }
        return list;
    }

    public long getUserId() {
        return userId;
    }

    public String getPreferredGift() {
        return preferredGift;
    }

    public String getComments() {
        return comments;
    }

    public String getChisataki() {
        return chisataki;
    }

}
