package dev.kurumidisciples.chisataki.games.tictactoe;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import dev.kurumidisciples.chisataki.commands.slash.TTTCommand;
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
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class TTTInteractionTest {

    private Member human;
    private Member opponent;
    private Guild guild;
    private MessageChannelUnion channel;
    private MessageCreateAction send;
    private ReplyCallbackAction reply;
    private MessageEditCallbackAction defer;
    private InteractionHook hook;
    private RestAction<Void> delete;
    private TTTGameSetup setup;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        human = member("111111111111111111", "Player", false);
        opponent = member("222222222222222222", "ChisaTaki", true);
        guild = mock(Guild.class);
        when(guild.getSelfMember()).thenReturn((SelfMember) opponent);
        when(guild.getMemberById(human.getId())).thenReturn(human);
        channel = mock(MessageChannelUnion.class);
        send = mock(MessageCreateAction.class, RETURNS_SELF);
        when(channel.sendMessage(any(CharSequence.class))).thenReturn(send);
        when(channel.sendMessageEmbeds(any(MessageEmbed.class))).thenReturn(send);
        reply = mock(ReplyCallbackAction.class, RETURNS_SELF);
        defer = mock(MessageEditCallbackAction.class);
        hook = mock(InteractionHook.class);
        delete = mock(RestAction.class);
        when(hook.deleteOriginal()).thenReturn(delete);
        doAnswer(call -> {
            call.<Consumer<InteractionHook>>getArgument(0).accept(hook);
            return null;
        }).when(defer).queue(any());
        doAnswer(call -> {
            call.<Consumer<Void>>getArgument(0).accept(null);
            return null;
        }).when(delete).queue(any(), any());
        setup = new TTTGameSetup(human, opponent);
        setup.setPlayer1Choice(TTTChoice.X);
    }

    @Test
    public void registersSoloAndMultiplayerCommands() {
        SlashCommandData command = (SlashCommandData) new TTTCommand().build();
        assertEquals(List.of("singleplayer", "multiplayer"),
            command.getSubcommands().stream().map(subcommand -> subcommand.getName()).toList());
        assertTrue(command.getSubcommands().get(0).getOptions().isEmpty());
        assertTrue(command.getSubcommands().get(1).getOptions().get(0).isRequired());
    }

    @Test
    public void soloCommandUsesTheBotWithoutAnOpponentOption() {
        SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
        when(event.getGuild()).thenReturn(guild);
        when(event.getMember()).thenReturn(human);
        when(event.getSubcommandName()).thenReturn("singleplayer");
        when(event.reply(anyString())).thenReturn(reply);
        new TTTCommand().execute(event);
        ArgumentCaptor<MessageTopLevelComponent[]> components = ArgumentCaptor.forClass(MessageTopLevelComponent[].class);
        verify(reply).setComponents(components.capture());
        String menuId = ((ActionRow) components.getValue()[0]).getActionComponents().get(0).getCustomId();
        assertEquals("menu:TTT-" + human.getId() + "-" + opponent.getId(), menuId);
        verify(reply).setEphemeral(true);
        verify(event, never()).getOption(anyString());
    }

    @Test
    public void soloPieceSelectionStartsImmediatelyWithHumanFirst() {
        StringSelectInteractionEvent event = menu("o");
        new TTTInteractionHandler().onStringSelectInteraction(event);
        List<List<Button>> board = sentBoard();
        assertEquals(0, occupied(board));
        String[] id = board.get(0).get(0).getCustomId().split("-");
        assertEquals("o", id[4]);
        assertEquals("x", id[6]);
        assertEquals(human.getId(), id[7]);
        verify(send, never()).setEmbeds(any(MessageEmbed[].class));
    }

    @Test
    public void onlyTheInitiatingPlayerCanChooseAPiece() {
        StringSelectInteractionEvent event = menu("x");
        doReturn(opponent.getUser()).when(event).getUser();
        new TTTInteractionHandler().onStringSelectInteraction(event);
        verify(reply).setEphemeral(true);
        verifyNoInteractions(channel);
        verify(event, never()).deferEdit();
    }

    @Test
    public void multiplayerStillSendsAnAcceptRejectRequest() {
        useMultiplayer();
        new TTTInteractionHandler().onStringSelectInteraction(menu("x"));
        ArgumentCaptor<MessageTopLevelComponent[]> components = ArgumentCaptor.forClass(MessageTopLevelComponent[].class);
        verify(send).setComponents(components.capture());
        assertEquals(1, components.getValue().length);
        List<Button> buttons = ((ActionRow) components.getValue()[0]).getButtons();
        assertEquals(2, buttons.size());
        assertTrue(buttons.get(0).getCustomId().startsWith("TTTReqAp-"));
        assertTrue(buttons.get(1).getCustomId().startsWith("TTTReqRe-"));
    }

    @Test
    public void acceptingMultiplayerStartsTheChallengersTurn() {
        useMultiplayer();
        ButtonInteractionEvent event = mock(ButtonInteractionEvent.class);
        configure(event, opponent);
        doReturn("TTTReqAp-" + human.getId() + "-x-" + opponent.getId() + "-o").when(event).getComponentId();
        new TTTEventHandler().onButtonInteraction(event);
        assertEquals(0, occupied(sentBoard()));
        verify(channel).sendMessage(human.getAsMention() + " it's your turn!");
    }

    @Test
    public void botRepliesOnceAndReturnsControlToHuman() {
        new TTTEventHandler().onButtonInteraction(click(board("   ", "   ", "   "), 0, 0, human));
        List<List<Button>> buttons = sentBoard();
        char[][] result = TTTUtils.discordButtonsToCharBoardFromButton(buttons);
        assertEquals('x', result[0][0]);
        assertEquals('o', result[1][1]);
        assertEquals(2, occupied(buttons));
        for (List<Button> row : buttons) {
            for (Button button : row) {
                assertTrue(button.getCustomId().endsWith("-" + human.getId()));
                assertEquals(button.getEmoji() != null, button.isDisabled());
            }
        }
        verify(channel).sendMessage(human.getAsMention() + " it's your turn!");
    }

    @Test
    public void botAlsoRepliesWhenHumanChoosesO() {
        setup.setPlayer1Choice(TTTChoice.O);
        new TTTEventHandler().onButtonInteraction(click(board("   ", "   ", "   "), 0, 0, human));
        char[][] result = TTTUtils.discordButtonsToCharBoardFromButton(sentBoard());
        assertEquals('o', result[0][0]);
        assertEquals('x', result[1][1]);
    }

    @Test
    public void multiplayerOnlyPlacesTheHumansMove() {
        useMultiplayer();
        new TTTEventHandler().onButtonInteraction(click(board("   ", "   ", "   "), 0, 0, human));
        List<List<Button>> result = sentBoard();
        assertEquals(1, occupied(result));
        assertTrue(result.get(2).get(2).getCustomId().endsWith("-" + opponent.getId()));
        verify(channel).sendMessage(opponent.getAsMention() + " it's your turn!");
    }

    @Test
    public void rejectsOutOfTurnAndOccupiedSquareClicks() {
        new TTTEventHandler().onButtonInteraction(click(board("   ", "   ", "   "), 0, 0, opponent));
        new TTTEventHandler().onButtonInteraction(click(board("x  ", " o ", "   "), 0, 0, human));
        verify(reply, times(2)).setEphemeral(true);
        verifyNoInteractions(channel, delete);
    }

    @Test
    public void announcesHumanWinWithoutABotMove() {
        new TTTEventHandler().onButtonInteraction(click(board("xx ", "oo ", "   "), 0, 2, human));
        MessageEmbed result = resultEmbed();
        assertEquals("Player has won the game!", result.getTitle());
        assertEquals(2, occurrences(result.getFields().get(1).getValue(), TTTChoice.O.getEmojString()));
        verify(channel, never()).sendMessage(any(CharSequence.class));
    }

    @Test
    public void announcesBotWinWithoutAnotherPlayableBoard() {
        new TTTEventHandler().onButtonInteraction(click(board("oo ", "x  ", "x  "), 1, 1, human));
        assertEquals("ChisaTaki has won the game!", resultEmbed().getTitle());
        verify(channel, never()).sendMessage(any(CharSequence.class));
    }

    @Test
    public void announcesADrawOnTheFinalHumanMove() {
        new TTTEventHandler().onButtonInteraction(click(board("xox", "xoo", "ox "), 2, 2, human));
        assertEquals("The game has ended in a draw!", resultEmbed().getFields().get(0).getValue());
        verify(channel, never()).sendMessage(any(CharSequence.class));
    }

    @Test
    public void duplicateClicksOnlyPublishOneBoard() {
        AtomicBoolean consumed = new AtomicBoolean();
        ErrorResponseException expired = mock(ErrorResponseException.class);
        when(expired.getErrorResponse()).thenReturn(ErrorResponse.UNKNOWN_MESSAGE);
        doAnswer(call -> {
            if (consumed.compareAndSet(false, true)) {
                call.<Consumer<Void>>getArgument(0).accept(null);
            } else {
                call.<Consumer<Throwable>>getArgument(1).accept(expired);
            }
            return null;
        }).when(delete).queue(any(), any());
        ButtonInteractionEvent event = click(board("   ", "   ", "   "), 0, 0, human);
        TTTEventHandler handler = new TTTEventHandler();
        handler.onButtonInteraction(event);
        handler.onButtonInteraction(event);
        verify(channel, times(1)).sendMessage(any(CharSequence.class));
    }

    private Member member(String id, String name, boolean bot) {
        Member member = bot ? mock(SelfMember.class) : mock(Member.class);
        User user = bot ? mock(SelfUser.class) : mock(User.class);
        when(member.getId()).thenReturn(id);
        when(member.getEffectiveName()).thenReturn(name);
        when(member.getAsMention()).thenReturn("<@" + id + ">");
        when(member.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(id);
        when(user.isBot()).thenReturn(bot);
        return member;
    }

    private void useMultiplayer() {
        opponent = member("333333333333333333", "Opponent", false);
        when(guild.getMemberById(opponent.getId())).thenReturn(opponent);
        setup = new TTTGameSetup(human, opponent);
        setup.setPlayer1Choice(TTTChoice.X);
    }

    private void configure(GenericComponentInteractionCreateEvent event, Member actor) {
        doReturn(actor.getUser()).when(event).getUser();
        when(event.getMember()).thenReturn(actor);
        when(event.getGuild()).thenReturn(guild);
        when(event.getChannel()).thenReturn(channel);
        when(event.deferEdit()).thenReturn(defer);
        when(event.reply(anyString())).thenReturn(reply);
    }

    private StringSelectInteractionEvent menu(String choice) {
        StringSelectInteractionEvent event = mock(StringSelectInteractionEvent.class);
        configure(event, human);
        doReturn("menu:TTT-" + human.getId() + "-" + opponent.getId()).when(event).getComponentId();
        when(event.getValues()).thenReturn(List.of(choice));
        return event;
    }

    private ButtonInteractionEvent click(char[][] board, int row, int column, Member actor) {
        List<List<Button>> buttons = TTTUtils.createBoard(setup, board, human);
        Message message = mock(Message.class);
        List<MessageTopLevelComponentUnion> components = buttons.stream().map(buttonRow -> {
            MessageTopLevelComponentUnion component = mock(MessageTopLevelComponentUnion.class);
            when(component.asActionRow()).thenReturn(ActionRow.of(buttonRow));
            return component;
        }).toList();
        when(message.getComponents()).thenReturn(components);
        ButtonInteractionEvent event = mock(ButtonInteractionEvent.class);
        configure(event, actor);
        when(event.getComponentId()).thenReturn(buttons.get(row).get(column).getCustomId());
        when(event.getMessage()).thenReturn(message);
        return event;
    }

    private List<List<Button>> sentBoard() {
        ArgumentCaptor<MessageTopLevelComponent[]> components = ArgumentCaptor.forClass(MessageTopLevelComponent[].class);
        verify(send).setComponents(components.capture());
        assertEquals(3, components.getValue().length);
        return Arrays.stream(components.getValue()).map(component -> ((ActionRow) component).getButtons()).toList();
    }

    private MessageEmbed resultEmbed() {
        ArgumentCaptor<MessageEmbed> embed = ArgumentCaptor.forClass(MessageEmbed.class);
        verify(channel).sendMessageEmbeds(embed.capture());
        return embed.getValue();
    }

    private long occupied(List<List<Button>> board) {
        return board.stream().flatMap(List::stream).filter(button -> button.getEmoji() != null).count();
    }

    private int occurrences(String text, String value) {
        return (text.length() - text.replace(value, "").length()) / value.length();
    }

    private char[][] board(String... rows) {
        return new char[][] {rows[0].toCharArray(), rows[1].toCharArray(), rows[2].toCharArray()};
    }
}
