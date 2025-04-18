package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessGame.TeamColor turn;

    private ChessBoard board;

    private boolean over;

    public ChessGame() {
// adding turn
        turn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
        over = false;

    }



    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }


    public boolean getGameOver() {
        return over;
    }

    public void setGameOver(boolean over) {
        this.over = over;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return new ArrayList<>();
        }

        Collection<ChessMove> realMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();


        for (ChessMove move : realMoves){
            if (isLegalMove(startPosition, move)){
                validMoves.add(move);
            }

        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (over) {
            TeamColor opponent = getOtherColor(turn);
            if (isInStalemate(opponent)) {
                throw new InvalidMoveException("The game is already over due to Stalemate.");
            }
            else if (isInCheckmate(opponent)) {
                throw new InvalidMoveException("The game is already over due to Checkmate.");
            }
            else {
                throw new InvalidMoveException("The game is already over.");
            }
        }

        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            throw new InvalidMoveException("There is no piece here.");
        }

        if (piece.getTeamColor() != turn) {
            throw new InvalidMoveException("This isn't your turn.");
        }

        var validMoves = piece.pieceMoves(board, startPosition);

        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("That move is not allowed.");
        }

        ChessPiece captured = board.getPiece(endPosition);
        board.addPiece(endPosition, piece);
        board.addPiece(startPosition, null);

        boolean check = isInCheck(turn);

        board.addPiece(startPosition, piece);
        board.addPiece(endPosition, captured);

        if (check){
            throw new InvalidMoveException("Move keeps King in check.");
        }

        board.addPiece(endPosition, piece);
        board.addPiece(startPosition, null);

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && (endPosition.getRow() == 0 || endPosition.getRow() == 7)) {
            if (move.getPromotionPiece() == null) {
                throw new InvalidMoveException("Pawn needs to be promoted.");
            }
            board.addPiece(endPosition, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }

        if (isInCheckmate(getOtherColor(turn)) || isInStalemate(getOtherColor(turn))) {
            over = true;
        } else {
            if (turn == TeamColor.WHITE) {
                turn = TeamColor.BLACK;
            }
            else {
                turn = TeamColor.WHITE;
            }
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    private ChessPosition findKing(TeamColor teamColor){
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition position = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor){
                    return position;
                }
            }
        }
        return  null;
    }

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition king = findKing(teamColor);
        if (king == null) {
            throw new RuntimeException("King is not found.");
        }


        TeamColor otherColor;

        if (teamColor == TeamColor.WHITE) {
            otherColor = TeamColor.BLACK;
        }
        else
        {
            otherColor = TeamColor.WHITE;
        }

        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                ChessPosition position = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == otherColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(king)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isLegalMove(ChessPosition startPosition, ChessMove move){
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return false;
        }



        ChessPosition endPosition = move.getEndPosition();
        ChessPiece captured = board.getPiece(endPosition);

        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);
        if (!potentialMoves.contains(move)) {
            return false;
        }

        board.addPiece(endPosition, piece);
        board.addPiece(startPosition, null);




        boolean ifInCheck = isInCheck(piece.getTeamColor());

        board.addPiece(startPosition, piece);
        board.addPiece(endPosition, captured);

        if (ifInCheck) {
            return false;
        }


        return true;

    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */

    // get the pawns for checkmate
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)){
            return false;
        }


        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++){
                ChessPosition position = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor){
                    Collection<ChessMove> moves = validMoves(position);

                    for (ChessMove move: moves) {
                        if (isLegalMove(position, move)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)){
            return false;
        }

        for (int row = 0; row < 8; row++)  {
            for (int col = 0; col < 8; col++) {
                ChessPosition position = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor){
                    Collection<ChessMove> moves = validMoves(position);

                    if (!moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        if (board == null) {
            throw new IllegalArgumentException("Board is empty.");
        }
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private TeamColor getOtherColor(TeamColor color) {
        return (color == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public ChessGame deepCopy() {
        ChessGame copy = new ChessGame();
        copy.setBoard(this.board.deepCopy());
        copy.setTeamTurn(this.turn);
        copy.setGameOver(this.over);
        return copy;
    }

}
