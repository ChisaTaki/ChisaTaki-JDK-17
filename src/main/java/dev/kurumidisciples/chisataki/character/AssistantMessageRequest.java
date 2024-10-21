package dev.kurumidisciples.chisataki.character;

import java.util.List;

import com.theokanning.openai.OpenAiResponse;
import com.theokanning.openai.assistants.assistant.Assistant;

import com.theokanning.openai.assistants.message.Message;
import com.theokanning.openai.assistants.message.MessageListSearchParameters;
import com.theokanning.openai.assistants.message.MessageRequest;
import com.theokanning.openai.assistants.message.MessageRequest.MessageRequestBuilder;

import com.theokanning.openai.assistants.run.Run;
import com.theokanning.openai.assistants.run.RunCreateRequest;
import com.theokanning.openai.assistants.run.ToolChoice;
import com.theokanning.openai.assistants.thread.Thread;
import com.theokanning.openai.assistants.thread.ThreadRequest;
import com.theokanning.openai.completion.chat.ImageContent;
import com.theokanning.openai.completion.chat.ImageUrl;

import com.theokanning.openai.service.OpenAiService;

import dev.kurumidisciples.chisataki.Main;
import dev.kurumidisciples.chisataki.character.expressions.ExpressionUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.AttachmentProxy;

import java.util.Optional;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class AssistantMessageRequest {
    

    private final String userMessage;
    private final Member member;
    private final long guildId;

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(AssistantMessageRequest.class);

    public class Response {
        public String aiMessage;

        public Response(String aiMessage) {
            this.aiMessage = aiMessage;
        }

        public String getMessage(Member member) {
            aiMessage = aiMessage.replace("{user}", member.getEffectiveName()) + "\n-# Chisataki is an Ai and not a real person. Please do not share personal information with it.";
            return ExpressionUtil.formatResponse(aiMessage);
        }
    }
    

    public AssistantMessageRequest(String userMessage, Member member, long guildId) {
        this.userMessage = userMessage;
        this.member = member;
        this.guildId = guildId;
    }

    @SuppressWarnings("null")
    public Response submitRequest(Optional<List<AttachmentProxy>> attachments){
        

        // Retrieve the assistant and the OpenAiService
        Assistant assistant = Main.getAssistant();
        OpenAiService aiService = Main.getAiService();

        List<ImageContent> imageContents = new ArrayList<>();
        if (attachments.isPresent()) {
            List<AttachmentProxy> attachmentProxies = attachments.get();
            for (AttachmentProxy temp : attachmentProxies) {
                imageContents.add(new ImageContent(new ImageUrl(temp.getUrl())));
            }
        }

        Thread thread = null;
        // Check if the user has a thread else create a new one
        String userThreadId = UsageTableUtils.selectUserThreadId(member.getIdLong());
        if (userThreadId == null) {
            ThreadRequest threadRequest = ThreadRequest.builder().build();
            thread = aiService.createThread(threadRequest);
        } else {
            thread = aiService.retrieveThread(userThreadId);
        }
        
        updateUsage(thread.getId()); //will update usage for the given user, or create a new usage if it doesn't exist and push it to the database

        MessageRequestBuilder requestBuilder = MessageRequest.builder()
        .content(userMessage);
        if (!imageContents.isEmpty()) {
            requestBuilder.content(imageContents);
        }
        MessageRequest request = requestBuilder.build();
        aiService.createMessage(thread.getId(), request);

        RunCreateRequest runCreateRequest = RunCreateRequest.builder().assistantId(assistant.getId()).toolChoice(ToolChoice.AUTO).build();


        Run run = aiService.createRun(thread.getId(), runCreateRequest);

        Run retrievedRun = aiService.retrieveRun(thread.getId(), run.getId());

        while (!(retrievedRun.getStatus().equals("completed"))
                && !(retrievedRun.getStatus().equals("failed"))
                && !(retrievedRun.getStatus().equals("expired"))
                && !(retrievedRun.getStatus().equals("incomplete"))
                && !(retrievedRun.getStatus().equals("requires_action"))) {
            retrievedRun = aiService.retrieveRun(thread.getId(), run.getId());
        }


        OpenAiResponse<Message> response = aiService.listMessages(thread.getId(), MessageListSearchParameters.builder()
            .runId(retrievedRun.getId()).build());
        List<Message> messages = response.getData();
        return new Response(messages.get(messages.size() - 1).getContent().get(0).getText().getValue());
    }

    public Response submitResponse(){
        return submitRequest(Optional.empty());
    }


    private void updateUsage(String threadId) {
        UserUsage usage = UsageTableUtils.selectUserUsage(member.getIdLong());
        long currentTime = System.currentTimeMillis();
    
        if (usage == null) {
            usage = new UserUsage(member.getIdLong(), currentTime, 1, false, threadId);
            UsageTableUtils.insertUserUsage(usage);
        } else {
            boolean shouldUpdate = false;
    
            if (usage.getLastMessage() + 3600000 < currentTime) {
                usage.setLastMessage(currentTime);
                usage.setMessageCount(1);
                usage.setReachedLimitOfDay(false);
                shouldUpdate = true;
            } else if (usage.getMessageCount() < AiStatusTableUtils.selectLimit(guildId)) {
                usage.incrementMessageCount();
                shouldUpdate = true;
            } else {
                usage.setReachedLimitOfDay(true);
                shouldUpdate = true;
            }
    
            if (shouldUpdate) {
                UsageTableUtils.updateUserUsage(usage, guildId);
            }
        }
    }
}


