package websocket.commands;

import java.util.Objects;
import chess.ChessMove;
import chess.ChessPosition;

/**
 * Represents a command a user can send the server over a websocket
 *
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    private final CommandType commandType;

    private final String authToken;

    private final Integer gameID;
    private final ChessMove move;
    private final ChessPosition position;

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move, ChessPosition position) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.move = move;
        this.position = position;
    }

    // constructor for leave, connect, resign
    public UserGameCommand(CommandType commandType, String authToken, Integer gameID) {
        this(commandType, authToken, gameID, null, null);
    }

    // for move
    public UserGameCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        this(commandType, authToken, gameID, move, null);
    }

    // display moves
    public UserGameCommand(CommandType commandType, String authToken, Integer gameID, ChessPosition position) {
        this(commandType, authToken, gameID, null, position);
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        DISPLAY_MOVES,
        REDRAW,
        RESIGN,
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }

    public ChessMove getMove() {
        return move;
    }

    public ChessPosition getPosition() { return position;}


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand)) {
            return false;
        }
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() &&
                Objects.equals(authToken, that.authToken) &&
                Objects.equals(gameID, that.gameID) &&
                Objects.equals(move, that.move) &&
                Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {

        return Objects.hash(commandType, authToken, gameID, move, position);
    }
}
