import java.util.HashMap;

import net.dv8tion.jda.api.entities.Message;

public class MessageCache {

    private static final HashMap<String, Message> messageMap;
    private static int maxSize;

    static {
        messageMap = new HashMap<>();
    }

    public static void storeMessage(Message message) {
        if (messageMap.size() >= maxSize) {
            // remove oldest message if cache is full
            messageMap.remove(messageMap.keySet().iterator().next());
        }
        messageMap.put(message.getId(), message);
    }

    public static Message getMessageById(String messageId) {
        return messageMap.get(messageId);
    }

    public static int getMaxSize() {
        return maxSize;
    }

    public static void setMaxSize(int maxSize) {
        MessageCache.maxSize = maxSize;
    }

  public static void updateMessage(Message message){
    storeMessage(message);
  }
  
  public static int getSize(){
    return messageMap.size();
  }
}