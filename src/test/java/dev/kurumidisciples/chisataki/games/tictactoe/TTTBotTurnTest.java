package dev.kurumidisciples.chisataki.games.tictactoe;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

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
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;

class TTTBotTurnTest {
    private final List<List<List<Button>>> boards = new ArrayList<>();
    private final List<MessageEmbed> results = new ArrayList<>();
    private JDA jda;
    private Guild guild;
    private Member human;
    private SelfMember self;
    private MessageChannelUnion channel;
    private MessageEditCallbackAction defer;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
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
        defer = mock(MessageEditCallbackAction.class);
        InteractionHook hook = mock(InteractionHook.class);
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
    }

    @ParameterizedTest
    @EnumSource(TTTChoice.class)
    void botRespondsAfterEachHumanTurnWithEitherPiece(TTTChoice humanChoice) {
        choosePiece(self, humanChoice);
        assertEquals(3, latestBoard().size());
        assertEquals(0, occupied(latestBoard()));
        click(latestBoard(), 0, 0);
        assertEquals(2, occupied(latestBoard()), "A human move must be followed by a bot move");
        char[][] firstTurn = TTTUtils.discordButtonsToCharBoardFromButton(latestBoard());
        assertEquals(humanChoice.getString().charAt(0), firstTurn[0][0]);
        assertEquals(TTTChoice.getAlternate(humanChoice).getString().charAt(0), firstTurn[1][1]);

        click(latestBoard(), 2, 2);
        assertEquals(4, occupied(latestBoard()), "The bot must also respond on subsequent turns");
        for (List<Button> row : latestBoard()) {
            for (Button button : row) {
                assertTrue(button.getCustomId().endsWith("-" + human.getId()));
                assertEquals(button.getEmoji() != null, button.isDisabled());
            }
        }
        assertTrue(results.isEmpty());
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
    }

    @Test
    void botWinEndsTheGame() {
        click(soloBoard(board("oo ", "x  ", "x  ")), 1, 1);
        assertEquals(1, results.size());
        assertEquals("ChisaTaki has won the game!", results.get(0).getTitle());
        assertTrue(boards.isEmpty());
    }

    @Test
    void botDoesNotMoveAfterHumanWins() {
        click(soloBoard(board("xx ", "oo ", "   ")), 0, 2);
        assertEquals(1, results.size());
        assertEquals("Player has won the game!", results.get(0).getTitle());
        assertTrue(boards.isEmpty());
    }

    @Test
    void botDoesNotMoveAfterADraw() {
        click(soloBoard(board("xox", "xoo", "ox ")), 2, 2);
        assertEquals(1, results.size());
        assertEquals("The game has ended in a draw!", results.get(0).getFields().get(0).getValue());
        assertTrue(boards.isEmpty());
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

    private void click(List<List<Button>> buttons, int row, int column) {
        Message message = mock(Message.class);
        List<MessageTopLevelComponentUnion> components = buttons.stream().map(buttonRow -> {
            MessageTopLevelComponentUnion component = mock(MessageTopLevelComponentUnion.class);
            when(component.asActionRow()).thenReturn(ActionRow.of(buttonRow));
            return component;
        }).toList();
        when(message.getComponents()).thenReturn(components);
        ButtonInteractionEvent event = mock(ButtonInteractionEvent.class);
        configure(event, buttons.get(row).get(column).getCustomId());
        when(event.getMessage()).thenReturn(message);
        new TTTEventHandler().onButtonInteraction(event);
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
