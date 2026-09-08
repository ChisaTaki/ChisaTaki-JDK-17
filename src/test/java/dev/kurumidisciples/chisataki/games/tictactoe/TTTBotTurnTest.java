package dev.kurumidisciples.chisataki.games.tictactoe;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfMember;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

class TTTBotTurnTest {
    private static final String MESSAGE_ID = "444444444444444444";
    private final List<List<List<Button>>> boards = new ArrayList<>();
    private final List<MessageEmbed> results = new ArrayList<>();
    private final List<MessageEditData> edits = new ArrayList<>();
    private final List<Runnable> successfulEdits = new ArrayList<>();
    private final List<Consumer<Throwable>> failedEdits = new ArrayList<>();
    private final List<MessageEditData> botEdits = new ArrayList<>();
    private final List<Runnable> successfulBotEdits = new ArrayList<>();
    private final List<Consumer<Throwable>> failedBotEdits = new ArrayList<>();
    private final List<ScheduledFuture<?>> expirations = new ArrayList<>();
    private JDA jda;
    private Guild guild;
    private Member human;
    private SelfMember self;
    private MessageChannelUnion channel;
    private MessageEditCallbackAction defer;
    private InteractionHook hook;
    private RestAction<Void> typing;
    private TTTEventHandler handler;
    private boolean completeEdits = true;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        handler = new TTTEventHandler();
        human = member("111111111111111111", false);
        self = mock(SelfMember.class);
        SelfUser selfUser = mock(SelfUser.class);
        when(self.getId()).thenReturn("222222222222222222");
        when(self.getUser()).thenReturn(selfUser);
        when(self.getEffectiveName()).thenReturn("ChisaTaki");
        when(selfUser.getId()).thenReturn("222222222222222222");
        when(selfUser.isBot()).thenReturn(true);
        jda = mock(JDA.class);
        when(jda.getSelfUser()).thenReturn(selfUser);
        guild = mock(Guild.class);
        when(guild.getSelfMember()).thenReturn(self);
        channel = mock(MessageChannelUnion.class);
        when(channel.getId()).thenReturn("555555555555555555");
        typing = mock(RestAction.class);
        when(channel.sendTyping()).thenReturn(typing);
        MessageCreateAction send = mock(MessageCreateAction.class, RETURNS_SELF);
        when(channel.sendMessage(any(CharSequence.class))).thenReturn(send);
        doAnswer(call -> {
            results.add(call.getArgument(0));
            return send;
        }).when(channel).sendMessageEmbeds(any(MessageEmbed.class));
        doAnswer(call -> {
            MessageTopLevelComponent[] components = (MessageTopLevelComponent[]) call.getRawArguments()[0];
            boards.add(Arrays.stream(components).map(component -> ((ActionRow) component).getButtons()).toList());
            return send;
        }).when(send).setComponents(any(MessageTopLevelComponent[].class));
        Message initialMessage = message(List.of());
        doAnswer(call -> {
            call.<Consumer<Message>>getArgument(0).accept(initialMessage);
            return null;
        }).when(send).queue(any());
        defer = mock(MessageEditCallbackAction.class);
        hook = mock(InteractionHook.class);
        RestAction<Void> delete = mock(RestAction.class);
        when(hook.deleteOriginal()).thenReturn(delete);
        doAnswer(call -> {
            call.<Consumer<InteractionHook>>getArgument(0).accept(hook);
            return null;
        }).when(defer).queue(any());
        doAnswer(call -> {
            call.<Consumer<Void>>getArgument(0).accept(null);
            return null;
        }).when(delete).queue(any(), any());
        doAnswer(call -> {
            MessageEditData data = call.getArgument(0);
            WebhookMessageEditAction<Message> edit = mock(WebhookMessageEditAction.class);
            doAnswer(queued -> {
                assertEquals(5L, queued.<Long>getArgument(0));
                assertEquals(TimeUnit.SECONDS, queued.getArgument(1));
                botEdits.add(data);
                successfulBotEdits.add(() -> {
                    applyEdit(data);
                    queued.<Consumer<Message>>getArgument(2).accept(message(List.of()));
                });
                failedBotEdits.add(queued.getArgument(3));
                return mock(ScheduledFuture.class);
            }).when(edit).queueAfter(anyLong(), any(TimeUnit.class), any(), any(Consumer.class));
            doAnswer(queued -> {
                applyEdit(data);
                queued.<Consumer<Message>>getArgument(0).accept(message(List.of()));
                return null;
            }).when(edit).queue(any(), any());
            return edit;
        }).when(hook).editOriginal(any(MessageEditData.class));
    }

    @AfterEach
    void cancelExpiry() {
        TTTUtils.cancelBoardExpiry(MESSAGE_ID);
    }

    @ParameterizedTest
    @EnumSource(TTTChoice.class)
    void botRespondsAfterEachHumanTurnWithEitherPiece(TTTChoice humanChoice) {
        choosePiece(self, humanChoice);
        assertEquals(3, latestBoard().size());
        assertEquals(0, occupied(latestBoard()));
        clearInvocations(channel, hook);
        ButtonInteractionEvent firstClick = click(latestBoard(), 0, 0);
        assertEquals(1, occupied(latestBoard()), "Show the human move before the bot responds");
        assertTrue(latestBoard().stream().flatMap(List::stream).allMatch(Button::isDisabled));
        assertEquals("ChisaTaki is thinking...", edits.get(0).getContent());
        verify(typing).queue();
        finishBotMove();
        assertEquals(2, occupied(latestBoard()), "A human move must be followed by a bot move");
        char[][] firstTurn = TTTUtils.discordButtonsToCharBoardFromButton(latestBoard());
        assertEquals(humanChoice.getString().charAt(0), firstTurn[0][0]);
        assertEquals(TTTChoice.getAlternate(humanChoice).getString().charAt(0), firstTurn[1][1]);

        ButtonInteractionEvent secondClick = click(latestBoard(), 2, 2);
        assertEquals(3, occupied(latestBoard()));
        finishBotMove();
        assertEquals(4, occupied(latestBoard()), "The bot must also respond on subsequent turns");
        for (List<Button> row : latestBoard()) {
            for (Button button : row) {
                assertTrue(button.getCustomId().endsWith("-" + human.getId()));
                assertEquals(button.getEmoji() != null, button.isDisabled());
            }
        }
        assertTrue(results.isEmpty());
        assertEquals(2, edits.size());
        assertEquals(2, botEdits.size());
        assertEquals(human.getAsMention() + " it's your turn!", botEdits.get(1).getContent());
        verify(typing, times(2)).queue();
        verify(firstClick).editMessage(any(MessageEditData.class));
        verify(secondClick).editMessage(any(MessageEditData.class));
        verify(firstClick, never()).deferEdit();
        verify(secondClick, never()).deferEdit();
        verify(hook, never()).deleteOriginal();
        verify(channel, never()).sendMessage(any(CharSequence.class));
        for (int i = 0; i < expirations.size() - 1; i++) {
            verify(expirations.get(i)).cancel(false);
        }
    }

    @Test
    void rapidClicksCannotOverwriteThePendingTurn() {
        completeEdits = false;
        List<List<Button>> original = soloBoard(board("   ", "   ", "   "));
        click(original, 0, 0);
        ButtonInteractionEvent duplicate = click(original, 2, 2);
        assertEquals(1, edits.size());
        assertTrue(boards.isEmpty(), "The visible board changes only after the edit succeeds");
        verify(duplicate, never()).editMessage(any(MessageEditData.class));
        verify(duplicate).reply(anyString());

        successfulEdits.get(0).run();
        assertEquals(1, occupied(latestBoard()));
        ButtonInteractionEvent duringDelay = click(latestBoard(), 2, 2);
        verify(duringDelay, never()).editMessage(any(MessageEditData.class));
        verify(duringDelay).reply(anyString());
        assertEquals(1, botEdits.size());
        finishBotMove();
        assertEquals(2, occupied(latestBoard()));
        assertEquals('x', TTTUtils.discordButtonsToCharBoardFromButton(latestBoard())[0][0]);
    }

    @Test
    void delayedClickFromTheOldBoardCannotReplayACompletedTurn() {
        List<List<Button>> original = soloBoard(board("   ", "   ", "   "));
        click(original, 0, 0);
        finishBotMove();
        click(original, 2, 2);
        assertEquals(1, edits.size());
        assertEquals(2, occupied(latestBoard()));
        click(latestBoard(), 2, 2);
        finishBotMove();
        assertEquals(2, edits.size());
        assertEquals(4, occupied(latestBoard()));
    }

    @Test
    void failedDiscordEditLeavesTheBoardPlayableForRetry() {
        completeEdits = false;
        List<List<Button>> original = soloBoard(board("   ", "   ", "   "));
        click(original, 0, 0);
        failedEdits.get(0).accept(new IllegalStateException("Simulated Discord edit failure"));
        assertTrue(boards.isEmpty());
        assertTrue(expirations.isEmpty());
        assertTrue(botEdits.isEmpty());
        verify(channel, never()).sendTyping();

        click(original, 2, 2);
        assertEquals(2, edits.size(), "A failed edit must release the turn for another click");
        successfulEdits.get(1).run();
        finishBotMove();
        assertEquals(2, occupied(latestBoard()));
        char[][] visible = TTTUtils.discordButtonsToCharBoardFromButton(latestBoard());
        assertEquals(' ', visible[0][0]);
        assertEquals('x', visible[2][2]);
    }

    @Test
    void failedBotEditRestoresTheOriginalBoardForRetry() {
        List<List<Button>> original = soloBoard(board("   ", "   ", "   "));
        click(original, 0, 0);
        assertEquals(1, occupied(latestBoard()));
        failedBotEdits.get(0).accept(new IllegalStateException("Simulated delayed edit failure"));
        assertEquals(0, occupied(latestBoard()));
        assertTrue(latestBoard().stream().flatMap(List::stream).noneMatch(Button::isDisabled));

        click(latestBoard(), 2, 2);
        assertEquals(2, botEdits.size());
        finishBotMove();
        assertEquals(2, occupied(latestBoard()));
        char[][] visible = TTTUtils.discordButtonsToCharBoardFromButton(latestBoard());
        assertEquals(' ', visible[0][0]);
        assertEquals('x', visible[2][2]);
    }

    @Test
    void multiplayerStillAlternatesOneMoveAtATime() {
        Member opponent = member("333333333333333333", false);
        when(guild.getMemberById(opponent.getId())).thenReturn(opponent);
        when(guild.getMemberById(human.getId())).thenReturn(human);
        TTTGameSetup setup = new TTTGameSetup(human, opponent, false);
        setup.setPlayer1Choice(TTTChoice.X);
        click(TTTUtils.createBoard(setup, board("   ", "   ", "   "), human), 0, 0);
        assertEquals(1, occupied(latestBoard()));
        assertEquals(opponent.getAsMention() + " it's your turn!", edits.get(0).getContent());

        ButtonInteractionEvent second = buttonEvent(latestBoard(), 1, 1);
        when(second.getMember()).thenReturn(opponent);
        doReturn(opponent.getUser()).when(second).getUser();
        handler.onButtonInteraction(second);
        assertEquals(2, occupied(latestBoard()));
        assertEquals(human.getAsMention() + " it's your turn!", edits.get(1).getContent());
        assertTrue(botEdits.isEmpty());
        verify(channel, never()).sendTyping();
    }

    @Test
    void anotherBotIsNotTreatedAsChisaTaki() {
        Member otherBot = member("333333333333333333", true);
        choosePiece(otherBot, TTTChoice.X);
        assertEquals(1, latestBoard().size(), "Only ChisaTaki starts a solo game immediately");
        assertTrue(latestBoard().get(0).get(0).getCustomId().startsWith("TTTReqAp-"));

        TTTGameSetup setup = new TTTGameSetup(human, otherBot, false);
        setup.setPlayer1Choice(TTTChoice.X);
        click(TTTUtils.createBoard(setup, board("   ", "   ", "   "), human), 0, 0);
        assertEquals(1, occupied(latestBoard()));
        assertTrue(latestBoard().get(0).get(1).getCustomId().endsWith("-" + otherBot.getId()));
        assertTrue(botEdits.isEmpty());
        verify(channel, never()).sendTyping();
    }

    @Test
    void botWinEndsTheGame() {
        TTTUtils.scheduleBoardExpiry(message(List.of()));
        click(soloBoard(board("oo ", "x  ", "x  ")), 1, 1);
        assertTrue(results.isEmpty());
        finishBotMove();
        assertEquals(1, results.size());
        assertEquals("ChisaTaki has won the game!", results.get(0).getTitle());
        assertEquals(1, boards.size(), "Only the temporary thinking board precedes the result");
        assertResultReplacesBoard(botEdits.get(0));
        verify(expirations.get(0)).cancel(false);
        verify(expirations.get(1)).cancel(false);
        assertEquals(2, expirations.size(), "A result must not get another deletion timer");
    }

    @Test
    void botDoesNotMoveAfterHumanWins() {
        click(soloBoard(board("xx ", "oo ", "   ")), 0, 2);
        assertEquals(1, results.size());
        assertEquals("Player has won the game!", results.get(0).getTitle());
        assertTrue(boards.isEmpty());
        assertResultReplacesBoard(edits.get(0));
        assertTrue(botEdits.isEmpty());
        verify(channel, never()).sendTyping();
    }

    @Test
    void botDoesNotMoveAfterADraw() {
        click(soloBoard(board("xox", "xoo", "ox ")), 2, 2);
        assertEquals(1, results.size());
        assertEquals("The game has ended in a draw!", results.get(0).getFields().get(0).getValue());
        assertTrue(boards.isEmpty());
        assertResultReplacesBoard(edits.get(0));
        assertTrue(botEdits.isEmpty());
        verify(channel, never()).sendTyping();
    }

    private void assertResultReplacesBoard(MessageEditData result) {
        assertEquals(1, edits.size());
        assertEquals("", result.toData().getString("content"), "Clear the old turn text");
        assertEquals(0, result.toData().getArray("components").length(), "Remove playable buttons");
        verify(channel, never()).sendMessageEmbeds(any(MessageEmbed.class));
    }

    private void finishBotMove() {
        successfulBotEdits.get(successfulBotEdits.size() - 1).run();
    }

    private void applyEdit(MessageEditData data) {
        if (!data.getComponents().isEmpty()) {
            boards.add(data.getComponents().stream()
                .map(component -> component.asActionRow().getButtons()).toList());
        }
        results.addAll(data.getEmbeds());
    }

    private Member member(String id, boolean bot) {
        Member member = mock(Member.class);
        User user = mock(User.class);
        when(member.getId()).thenReturn(id);
        when(member.getUser()).thenReturn(user);
        when(member.getAsMention()).thenReturn("<@" + id + ">");
        when(member.getEffectiveName()).thenReturn("Player");
        when(user.getId()).thenReturn(id);
        when(user.isBot()).thenReturn(bot);
        return member;
    }

    private void configure(GenericComponentInteractionCreateEvent event, String id) {
        when(event.getJDA()).thenReturn(jda);
        when(event.getGuild()).thenReturn(guild);
        when(event.getMember()).thenReturn(human);
        doReturn(human.getUser()).when(event).getUser();
        when(event.getChannel()).thenReturn(channel);
        when(event.getComponentId()).thenReturn(id);
        when(event.deferEdit()).thenReturn(defer);
    }

    private void choosePiece(Member opponent, TTTChoice choice) {
        when(guild.getMemberById(opponent.getId())).thenReturn(opponent);
        StringSelectInteractionEvent event = mock(StringSelectInteractionEvent.class);
        configure(event, "menu:TTT-" + human.getId() + "-" + opponent.getId());
        when(event.getValues()).thenReturn(List.of(choice.getString()));
        new TTTInteractionHandler().onStringSelectInteraction(event);
    }

    private ButtonInteractionEvent click(List<List<Button>> buttons, int row, int column) {
        ButtonInteractionEvent event = buttonEvent(buttons, row, column);
        handler.onButtonInteraction(event);
        return event;
    }

    private ButtonInteractionEvent buttonEvent(List<List<Button>> buttons, int row, int column) {
        ButtonInteractionEvent event = mock(ButtonInteractionEvent.class);
        configure(event, buttons.get(row).get(column).getCustomId());
        doReturn(message(buttons)).when(event).getMessage();
        when(event.getMessageId()).thenReturn(MESSAGE_ID);
        when(event.reply(anyString())).thenReturn(mock(ReplyCallbackAction.class, RETURNS_SELF));
        doAnswer(call -> {
            MessageEditData data = call.getArgument(0);
            edits.add(data);
            MessageEditCallbackAction edit = mock(MessageEditCallbackAction.class);
            doAnswer(queued -> {
                Runnable success = () -> {
                    applyEdit(data);
                    queued.<Consumer<InteractionHook>>getArgument(0).accept(hook);
                };
                successfulEdits.add(success);
                failedEdits.add(queued.getArgument(1));
                if (completeEdits) {
                    success.run();
                }
                return null;
            }).when(edit).queue(any(), any());
            return edit;
        }).when(event).editMessage(any(MessageEditData.class));
        return event;
    }

    @SuppressWarnings("unchecked")
    private Message message(List<List<Button>> buttons) {
        Message message = mock(Message.class);
        when(message.getId()).thenReturn(MESSAGE_ID);
        List<MessageTopLevelComponentUnion> components = buttons.stream().map(buttonRow -> {
            MessageTopLevelComponentUnion component = mock(MessageTopLevelComponentUnion.class);
            when(component.asActionRow()).thenReturn(ActionRow.of(buttonRow));
            return component;
        }).toList();
        when(message.getComponents()).thenReturn(components);
        AuditableRestAction<Void> delete = mock(AuditableRestAction.class);
        when(message.delete()).thenReturn(delete);
        doAnswer(call -> {
            ScheduledFuture<?> expiry = mock(ScheduledFuture.class);
            expirations.add(expiry);
            return expiry;
        }).when(delete).queueAfter(eq(10L), eq(TimeUnit.MINUTES), any(), any(Consumer.class));
        return message;
    }

    private List<List<Button>> soloBoard(char[][] board) {
        TTTGameSetup setup = new TTTGameSetup(human, self, true);
        setup.setPlayer1Choice(TTTChoice.X);
        return TTTUtils.createBoard(setup, board, human);
    }

    private List<List<Button>> latestBoard() {
        return boards.get(boards.size() - 1);
    }

    private long occupied(List<List<Button>> buttons) {
        return buttons.stream().flatMap(List::stream).filter(button -> button.getEmoji() != null).count();
    }

    private char[][] board(String... rows) {
        return new char[][] {rows[0].toCharArray(), rows[1].toCharArray(), rows[2].toCharArray()};
    }
}
