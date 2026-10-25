package dev.kurumidisciples.chisataki.quotes;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.requests.restaction.pagination.ThreadChannelPaginationAction;

/** Looks up active and archived threads using the public JDA API. */
public final class QuoteThreadResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteThreadResolver.class);

    private QuoteThreadResolver() {}

    public static CompletableFuture<ThreadChannel> resolve(Guild guild, long threadId) {
        ThreadChannel cached = guild.getThreadChannelById(threadId);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }
        return guild.retrieveActiveThreads().submit().thenCompose(active -> {
            ThreadChannel match = active.stream().filter(thread -> thread.getIdLong() == threadId)
                .findFirst().orElse(null);
            return match != null ? CompletableFuture.completedFuture(match) : findArchived(guild, threadId);
        }).thenApply(thread -> {
            if (thread == null) {
                throw new IllegalStateException("The quotes thread could not be found. Check View Channel and Read Message History permissions.");
            }
            return thread;
        });
    }

    private static CompletableFuture<ThreadChannel> findArchived(Guild guild, long threadId) {
        CompletableFuture<ThreadChannel> result = CompletableFuture.completedFuture(null);
        for (var channel : guild.getChannels()) {
            if (!(channel instanceof IThreadContainer parent)
                    || !guild.getSelfMember().hasPermission(channel, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY)) {
                continue;
            }
            result = searchIfMissing(result, parent::retrieveArchivedPublicThreadChannels, threadId);
            if (channel.getType() == ChannelType.TEXT) {
                Supplier<ThreadChannelPaginationAction> privateThreads =
                    guild.getSelfMember().hasPermission(channel, Permission.MANAGE_THREADS)
                        ? parent::retrieveArchivedPrivateThreadChannels
                        : parent::retrieveArchivedPrivateJoinedThreadChannels;
                result = searchIfMissing(result, privateThreads, threadId);
            }
        }
        return result;
    }

    private static CompletableFuture<ThreadChannel> searchIfMissing(CompletableFuture<ThreadChannel> previous,
            Supplier<ThreadChannelPaginationAction> archive, long threadId) {
        return previous.thenCompose(found -> {
            if (found != null) {
                return CompletableFuture.completedFuture(found);
            }
            AtomicReference<ThreadChannel> match = new AtomicReference<>();
            try {
                // forEachAsync follows every page and stops as soon as the ID is found.
                return archive.get().cache(false).forEachAsync(thread -> {
                    if (thread.getIdLong() == threadId) {
                        match.set(thread);
                        return false;
                    }
                    return true;
                }).handle((ignored, failure) -> {
                    if (failure != null) {
                        LOGGER.warn("Could not search a thread archive for quotes thread {}", threadId, failure);
                    }
                    return match.get();
                });
            } catch (RuntimeException failure) {
                LOGGER.warn("Could not access a thread archive for quotes thread {}", threadId, failure);
                return CompletableFuture.completedFuture(null);
            }
        });
    }
}
