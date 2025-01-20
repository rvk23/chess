package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor teamColor;
    private final PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece piece = (ChessPiece) o;
        return teamColor == piece.teamColor && pieceType == piece.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "teamColor=" + teamColor +
                ", pieceType=" + pieceType +
                '}';
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */


    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (pieceType) {
            case BISHOP:
                return calculateBishopMoves(board, myPosition);
            // add cases for each piece type
            default:
                return new ArrayList<>();
        }
    }

    private Collection<ChessMove> calculateBishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();


        int startRow = myPosition.getRow() - 1;
        int startCol = myPosition.getColumn() - 1;


        int[][] directions = {{1, 1}, {1, -1}, {-1, -1}, {-1, 1}};


        //movement
        for (int[] dir : directions) {
            int row = startRow + dir[0];
            int col = startCol + dir[1];

            // inbounds
            while (row >= 0 && row <= 7 && col >= 0 && col <= 7) {

                ChessPosition newPosition = new ChessPosition(row, col);

                ChessPosition onePosition = new ChessPosition(row + 1, col + 1);

                ChessPiece piece = board.getPiece(newPosition);

                if (piece != null) {

                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, onePosition, null));
                    }
                    break;
                }




                moves.add(new ChessMove(myPosition, onePosition, null));


                row += dir[0];
                col += dir[1];
            }
        }

        return moves;
    }


}
