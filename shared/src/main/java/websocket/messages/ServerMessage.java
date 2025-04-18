package websocket.messages;

import java.util.Objects;
import java.util.Collection;
import chess.ChessMove;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    private String message;
    private String errorMessage;
    private Object game;
    private Collection<ChessMove> moves;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION,
        MOVES,
    }


    public void setMoves(Collection<ChessMove> moves) {
        this.moves = moves;
    }

    public Collection<ChessMove> getMoves() {
        return moves;
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessage(ServerMessageType type, String text) {
        this.serverMessageType = type;
        if (type == ServerMessageType.NOTIFICATION) {
            this.message = text;
        }
        else if (type == ServerMessageType.ERROR) {
            this.errorMessage = text;
        }
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }


    public String getMessage() {
        return message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Object getGame() {
        return game;
    }

    public void setGame(Object game) {
        this.game = game;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return serverMessageType == that.serverMessageType &&
                Objects.equals(message, that.message) &&
                Objects.equals(errorMessage, that.errorMessage) &&
                Objects.equals(game, that.game) &&
                Objects.equals(moves, that.moves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType(), message, errorMessage, game, moves);
    }
}
