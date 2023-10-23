package dev.kurumiDisciples.chisataki.internal.database.enums;

/**
 * This enum is used to represent the different types of queries. 
 * It is used to determine the type of a query.
 * @author Hacking Pancakez
 */
public enum QueryTypes {
    SELECT("SELECT"),
    INSERT("INSERT"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    CREATE("CREATE"),
    ALTER("ALTER"),
    DROP("DROP");


    private final String queryType;

    QueryTypes(String queryType) {
        this.queryType = queryType;
    }

    public String getQueryType() {
        return queryType;
    }

    public static QueryTypes getQueryType(String queryType) {
        for (QueryTypes type : QueryTypes.values()) {
            if (type.getQueryType().equalsIgnoreCase(queryType)) {
                return type;
            }
        }
        return null;
    }
}
