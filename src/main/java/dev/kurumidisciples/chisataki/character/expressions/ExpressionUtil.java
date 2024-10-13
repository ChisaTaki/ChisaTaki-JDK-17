package dev.kurumidisciples.chisataki.character.expressions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionUtil {
    

    public static String formatResponse(String response) {
        // For each expression, replace all occurrences of the key with the emoji
        for (ChisatoExpressions expression : ChisatoExpressions.values()) {
            // Build a regex pattern to match the key as a whole word
            String regex = "\\b" + Pattern.quote(expression.getKey()) + "\\b";
            // Replace with the emoji string
            response = response.replaceAll(regex, Matcher.quoteReplacement(expression.getEmoji().getFormatted()));
        }

        for (TakinaExpressions expressions : TakinaExpressions.values()) {
            String regex = "\\b" + Pattern.quote(expressions.getKey()) + "\\b";
            response = response.replaceAll(regex, Matcher.quoteReplacement(expressions.getEmoji().getFormatted()));
        }
        return response;
    }
    
}
