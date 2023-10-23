package dev.kurumiDisciples.chisataki.internal.database;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * This class holds all database values. The values are loaded from a .env file. 
 * The .env file is located in the crypt folder.
 * The .env file is not included in the repository.
 * It is important to load the secrets and not directly import them in the code to prevent the secrets from being leaked.
 * @author Hacking Pancakez
 */
public class DatabaseValues {
    
    private static final String DATABASE_URL;
    private static final String DATABASE_USERNAME;
    private static final String DATABASE_PASSWORD;
    

    static {
        Dotenv dotenv = Dotenv.configure()
            .directory("crypt/")
            .load();
        DATABASE_URL = dotenv.get("DATABASE_URL");
        DATABASE_USERNAME = dotenv.get("DATABASE_USERNAME");
        DATABASE_PASSWORD = dotenv.get("DATABASE_PASSWORD");
    }


    public static String getDatabaseUrl() {
        return DATABASE_URL;
    }

    public static String getDatabaseUsername() {
        return DATABASE_USERNAME;
    }

    public static String getDatabasePassword() {
        return DATABASE_PASSWORD;
    }

}
