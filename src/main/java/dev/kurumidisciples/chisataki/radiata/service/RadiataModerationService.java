package dev.kurumidisciples.chisataki.radiata.service;

import com.openai.models.moderations.Moderation;
import com.openai.models.moderations.ModerationCreateParams;
import com.openai.models.moderations.ModerationModel;

import dev.kurumidisciples.chisataki.Main;
import dev.kurumidisciples.chisataki.radiata.database.ViolationRecordUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RadiataModerationService {

    static final Logger logger = LoggerFactory.getLogger(RadiataModerationService.class);

    private static final int MAX_QUEUE_SIZE = 500;
    private static final int WORKER_THREADS = 2;

    private static final BlockingQueue<RadiataModerationTask> queue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

    private static final ExecutorService workers = Executors.newFixedThreadPool(WORKER_THREADS);

    static {
        startWorkers();
    }

    private RadiataModerationService() {
        // Prevent instantiation
    }

    private static void startWorkers() {
        for (int i = 0; i < WORKER_THREADS; i++) {
            workers.submit(RadiataModerationService::processLoop);
        }
    }

    private static void processLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                RadiataModerationTask task = queue.take();
                moderate(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                logger.error("Thread error occurred during loop processing", e);
            }
        }
    }

    public static boolean enqueue(RadiataModerationTask task) {
        return queue.offer(task);
    }

    private static void moderate(RadiataModerationTask task) {
        try {

            ModerationCreateParams params =
                    ModerationCreateParams.builder()
                            .model(ModerationModel.OMNI_MODERATION_LATEST)
                            .input(task.getDataUrl()) // base64 data URL
                            .build();

            Moderation response = Main.getAiClient().moderations().create(params).results().get(0);

            boolean flagged = response.flagged();

            if (flagged) {
                ViolationRecordUtils.insertViolationRecord(task.event(), task.getDataUrl(), response.categories().toString()); // may cause errors as it is unchecked whether this works.
                // TODO: notification service call
            }

        } catch (Exception e) {
            logger.error("Something went wrong when attempting to evaluate message", e);
        }
    }
}