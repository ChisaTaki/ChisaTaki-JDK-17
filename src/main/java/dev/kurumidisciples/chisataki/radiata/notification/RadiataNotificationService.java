package dev.kurumidisciples.chisataki.radiata.notification;

import com.openai.models.moderations.Moderation;

import dev.kurumidisciples.chisataki.radiata.service.RadiataModerationTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RadiataNotificationService {
    
    static final Logger logger = LoggerFactory.getLogger(RadiataNotificationService.class);

    public static void notifyModerators(Moderation result, RadiataModerationTask task){
        
    }
}
