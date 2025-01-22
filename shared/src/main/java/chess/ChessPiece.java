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
            case KING:
                return calculateKingMoves(board, myPosition);
            case KNIGHT:
                return calculateKnightMoves(board, myPosition);
            case PAWN:
                return calculatePawnMoves(board, myPosition);
            case QUEEN:
                return calculateQueenMoves(board, myPosition);
            case ROOK:
                return calculateRookMoves(board, myPosition);
            // add cases for each piece type
            default:
                return new ArrayList<>();
        }
    }


    private Collection<ChessMove> calculateBishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();


        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();


        int[][] directions = {{-1, -1}, {1, 1}, {1, -1}, {-1, 1}};


        //movement
        for (int[] dir : directions) {
            int row = startRow + dir[0];
            int col = startCol + dir[1];

            // inbounds
            while (row >= 0 && row <= 7 && col >= 0 && col <= 7) {

                ChessPosition onePosition = new ChessPosition(row + 1, col + 1);

                ChessPiece piece = board.getPiece(onePosition);

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


    private Collection<ChessMove> calculateKingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();


        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();


        int[][] directions = {{-1, -1}, {1, 1}, {1, -1}, {-1, 1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};


        //movement
        for (int[] dir : directions) {
            int row = startRow + dir[0];
            int col = startCol + dir[1];
            if (row >= 0 && row <= 7 && col >= 0 && col <= 7) {
                ChessPosition onePosition = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = board.getPiece(onePosition);
                if (piece != null) {
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, onePosition, null));
                    }
                    continue;
                }

                moves.add(new ChessMove(myPosition, onePosition, null));
            }

        }

        return moves;
    }

    private Collection<ChessMove> calculateKnightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();


        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();


        int[][] directions = {{2, -1}, {2, 1}, {-2, -1}, {-2, 1}, {1, 2}, {1, -2}, {-1, -2}, {-1, 2}};


        //movement
        for (int[] dir : directions) {
            int row = startRow + dir[0];
            int col = startCol + dir[1];
            if (row >= 0 && row <= 7 && col >= 0 && col <= 7) {
                ChessPosition onePosition = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = board.getPiece(onePosition);
                if (piece != null) {
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, onePosition, null));
                    }
                    continue;
                }

                moves.add(new ChessMove(myPosition, onePosition, null));
            }

        }

        return moves;
    }

    private Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();


        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();


        ChessPiece myPiece = board.getPiece(myPosition);


        if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            int[][] directions = {{1, 0}};
            // movement
            for (int[] dir : directions) {
                int row = startRow + dir[0];
                int col = startCol + dir[1];

                if (row >= 0 && row <= 7 && col >= 0 && col <= 7) {
                    ChessPosition startPosition = new ChessPosition(row - dir[0], col - dir[1]);
                    ChessPosition onePosition = new ChessPosition(row + 1, col + 1);
                    ChessPosition zeroPosition = new ChessPosition(row, col);
                    ChessPiece piece = board.getPiece(onePosition);


                    if (startRow == 1) {
                        ChessPosition rightLookPosition = new ChessPosition(row + 2, col + 2);
                        ChessPiece upRightLookPiece = board.getPiece(rightLookPosition);
                        if (upRightLookPiece != null)
                        {
                            if (upRightLookPiece.getTeamColor() != this.getTeamColor())
                            {
                                moves.add(new ChessMove(myPosition, rightLookPosition, null));
                            }
                        }
                        ChessPosition leftLookPosition = new ChessPosition(row + 1, col);
                        ChessPiece upLeftLookPiece = board.getPiece(leftLookPosition);
                        if (upLeftLookPiece != null)
                        {
                            if (upLeftLookPiece.getTeamColor() != this.getTeamColor())
                            {
                                moves.add(new ChessMove(myPosition, leftLookPosition, null));
                            }
                        }
                        if (piece == null)
                        {
                            ChessPosition doubleMove = new ChessPosition(row + 1 + dir[0], col + 1 + dir[1]);
                            ChessPiece doublePiece = board.getPiece(doubleMove);
                            moves.add(new ChessMove(myPosition, onePosition, null));
                            if (doublePiece == null)
                            {
                                moves.add(new ChessMove(myPosition, doubleMove, null));
                            }
                        }

                        break;

                    }
                    else if (startCol == 7)
                    {
                        if (startRow == 6)
                        {
                            ChessPosition leftLookPosition = new ChessPosition(row + 1, col);
                            ChessPiece upLeftLookPiece = board.getPiece(leftLookPosition);
                            if (upLeftLookPiece != null)
                            {
                                if (upLeftLookPiece.getTeamColor() != this.getTeamColor())
                                {
                                    moves.add(new ChessMove(myPosition, leftLookPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, leftLookPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, leftLookPosition, PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, leftLookPosition, PieceType.KNIGHT));

                                }
                            }
                        }
                        ChessPosition leftLookPosition = new ChessPosition(row + 1, col);
                        ChessPiece upLeftLookPiece = board.getPiece(leftLookPosition);
                        if (upLeftLookPiece != null)
                        {
                            if (upLeftLookPiece.getTeamColor() != this.getTeamColor())
                            {
                                moves.add(new ChessMove(myPosition, leftLookPosition, null));
                            }
                        }
                        if (piece == null)
                        {
                            ChessPosition doubleMove = new ChessPosition(row + 1 + dir[0], col + 1 + dir[1]);
                            moves.add(new ChessMove(myPosition, onePosition, null));
                        }

                        break;

                    }
                    else if (startRow == 6)
                    {
                        ChessPosition rightLookPosition = new ChessPosition(startRow + 1, startCol + 1);
                        ChessPiece upRightLookPiece = board.getPiece(rightLookPosition);
                        if (upRightLookPiece != null)
                        {
                            if (upRightLookPiece.getTeamColor() != this.getTeamColor())
                            {
                                moves.add(new ChessMove(myPosition, rightLookPosition, PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, rightLookPosition, PieceType.ROOK));
                                moves.add(new ChessMove(myPosition, rightLookPosition, PieceType.BISHOP));
                                moves.add(new ChessMove(myPosition, rightLookPosition, PieceType.KNIGHT));

                            }
                        }
                        ChessPosition leftLookPosition = new ChessPosition(startRow + 2, startCol);
                        ChessPiece upLeftLookPiece = board.getPiece(leftLookPosition);
                        if (upLeftLookPiece != null)
                        {
                            if (upLeftLookPiece.getTeamColor() != this.getTeamColor())
                            {
                                moves.add(new ChessMove(myPosition, leftLookPosition, PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, leftLookPosition, PieceType.ROOK));
                                moves.add(new ChessMove(myPosition, leftLookPosition, PieceType.BISHOP));
                                moves.add(new ChessMove(myPosition, leftLookPosition, PieceType.KNIGHT));

                            }
                        }
                        if (piece == null)
                        {
                            moves.add(new ChessMove(myPosition, onePosition, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, onePosition, PieceType.KNIGHT));
                            moves.add(new ChessMove(myPosition, onePosition, PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, onePosition, PieceType.BISHOP));
                        }

                    }
                    else
                    {
                        ChessPosition rightLookPosition = new ChessPosition(row + 2, col + 2);
                        ChessPiece upRightLookPiece = board.getPiece(rightLookPosition);
                        if (upRightLookPiece != null)
                        {
                            if (upRightLookPiece.getTeamColor() != this.getTeamColor())
                            {
                                moves.add(new ChessMove(myPosition, rightLookPosition, null));
                            }
                        }
                        ChessPosition leftLookPosition = new ChessPosition(row + 1, col);
                        ChessPiece upLeftLookPiece = board.getPiece(leftLookPosition);
                        if (upLeftLookPiece != null)
                        {
                            if (upLeftLookPiece.getTeamColor() != this.getTeamColor())
                            {
                                moves.add(new ChessMove(myPosition, leftLookPosition, null));
                            }
                        }
                        if (piece == null)
                        {
                            moves.add(new ChessMove(myPosition, onePosition, null));
                        }

                        break;
                    }



                }

            }

        }
        else
        {
            int[][] directions = {{-1, 0}};
            // movement
            for (int[] dir : directions) {
                int row = startRow + dir[0];
                int col = startCol + dir[1];

                if (row >= 0 && row <= 7 && col >= 0 && col <= 7) {
                    ChessPosition startPosition = new ChessPosition(row - dir[0], col - dir[1]);
                    ChessPosition onePosition = new ChessPosition(row + 1, col + 1);
                    ChessPiece piece = board.getPiece(onePosition);

                    if (startRow== 6) {
                        ChessPosition rightLookPosition = new ChessPosition(startRow, startCol + 2);
                        ChessPiece upRightLookPiece = board.getPiece(rightLookPosition);
                        if (upRightLookPiece != null)
                        {
                            if (upRightLookPiece.getTeamColor() != this.getTeamColor())
                            {
                                moves.add(new ChessMove(myPosition, rightLookPosition, null));
                            }
                        }
                        ChessPosition leftLookPosition = new ChessPosition(startRow, startCol);
                        ChessPiece upLeftLookPiece = board.getPiece(leftLookPosition);
                        if (upLeftLookPiece != null)
                        {
                            if (upLeftLookPiece.getTeamColor() != this.getTeamColor())
                            {
                                moves.add(new ChessMove(myPosition, leftLookPosition, null));
                            }
                        }
                        if (piece == null)
                        {
                            ChessPosition doubleMove = new ChessPosition(row + 1 + dir[0], col + 1 + dir[1]);
                            ChessPiece doublePiece = board.getPiece(doubleMove);
                            moves.add(new ChessMove(myPosition, onePosition, null));
                            if (doublePiece == null)
                            {
                                moves.add(new ChessMove(myPosition, doubleMove, null));
                            }
                        }

                        break;

                    }
                    else if (startRow == 1)
                    {
                        ChessPosition rightLookPosition = new ChessPosition(startRow, startCol + 2);
                        ChessPiece upRightLookPiece = board.getPiece(rightLookPosition);
                        if (upRightLookPiece != null)
                        {
                            if (upRightLookPiece.getTeamColor() != this.getTeamColor())
                            {
                                moves.add(new ChessMove(myPosition, rightLookPosition, PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, rightLookPosition, PieceType.ROOK));
                                moves.add(new ChessMove(myPosition, rightLookPosition, PieceType.KNIGHT));
                                moves.add(new ChessMove(myPosition, rightLookPosition, PieceType.BISHOP));
                            }
                        }
                        ChessPosition leftLookPosition = new ChessPosition(startRow, startCol);
                        ChessPiece upLeftLookPiece = board.getPiece(leftLookPosition);
                        if (upLeftLookPiece != null)
                        {

                            if (upLeftLookPiece.getTeamColor() != this.getTeamColor())
                            {
                                moves.add(new ChessMove(myPosition, leftLookPosition, PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, leftLookPosition, PieceType.KNIGHT));
                                moves.add(new ChessMove(myPosition, leftLookPosition, PieceType.BISHOP));
                                moves.add(new ChessMove(myPosition, leftLookPosition, PieceType.ROOK));
                            }
                        }
                        if (piece == null)
                        {
                            moves.add(new ChessMove(myPosition, onePosition, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, onePosition, PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, onePosition, PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, onePosition, PieceType.KNIGHT));
                        }

                        break;
                    }
                    else if (startCol == 7)
                    {
                        ChessPosition leftLookPosition = new ChessPosition(startRow, startCol);
                        ChessPiece upLeftLookPiece = board.getPiece(leftLookPosition);
                        if (upLeftLookPiece != null)
                        {
                            if (upLeftLookPiece.getTeamColor() != this.getTeamColor())
                            {
                                moves.add(new ChessMove(myPosition, leftLookPosition, null));
                            }
                        }
                        if (piece == null)
                        {
                            moves.add(new ChessMove(myPosition, onePosition, null));
                        }

                        break;
                    }
                    else
                    {
                        ChessPosition rightLookPosition = new ChessPosition(startRow, startCol + 2);
                        ChessPiece upRightLookPiece = board.getPiece(rightLookPosition);
                        if (upRightLookPiece != null)
                        {
                            if (upRightLookPiece.getTeamColor() != this.getTeamColor())
                            {
                                moves.add(new ChessMove(myPosition, rightLookPosition, null));
                            }
                        }
                        ChessPosition leftLookPosition = new ChessPosition(startRow, startCol);
                        ChessPiece upLeftLookPiece = board.getPiece(leftLookPosition);
                        if (upLeftLookPiece != null)
                        {
                            if (upLeftLookPiece.getTeamColor() != this.getTeamColor())
                            {
                                moves.add(new ChessMove(myPosition, leftLookPosition, null));
                            }
                        }
                        if (piece == null)
                        {
                            moves.add(new ChessMove(myPosition, onePosition, null));
                        }

                        break;
                    }



                }

            }
        }





        return moves;
    }

    private Collection<ChessMove> calculateQueenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();


        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();


        int[][] directions = {{-1, -1}, {1, 1}, {1, -1}, {-1, 1},{1, 0}, {-1, 0}, {0, 1}, {0, -1}};


        //movement
        for (int[] dir : directions) {
            int row = startRow + dir[0];
            int col = startCol + dir[1];

            // inbounds
            while (row >= 0 && row <= 7 && col >= 0 && col <= 7) {

                ChessPosition startPosition = new ChessPosition(startRow, startCol);

                ChessPosition zeroPosition = new ChessPosition(row, col);

                ChessPosition onePosition = new ChessPosition(row + 1, col + 1);

                ChessPosition currentPosition = new ChessPosition(row + dir[0], col + dir[1]);

                if (row < 7) {
                    if (col < 7) {
                        ChessPiece piece = board.getPiece(onePosition);
                        if (piece != null) {
                            if (piece.getTeamColor() != this.getTeamColor()) {
                                moves.add(new ChessMove(myPosition, onePosition, null));
                            }
                            break;
                        }
                    }
                }



                moves.add(new ChessMove(myPosition, onePosition, null));

                row += dir[0];
                col += dir[1];

            }
        }

        return moves;
    }

    private Collection<ChessMove> calculateRookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();


        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();


        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};


        //movement
        for (int[] dir : directions) {
            int row = startRow + dir[0];
            int col = startCol + dir[1];

            // inbounds
            while (row >= 0 && row <= 7 && col >= 0 && col <= 7) {

                ChessPosition zeroPosition = new ChessPosition(row, col);

                ChessPosition onePosition = new ChessPosition(row + 1, col + 1);

                if (row <= 7) {
                    if (col <= 7) {
                        ChessPiece piece = board.getPiece(onePosition);
                        if (piece != null) {
                            if (piece.getTeamColor() != this.getTeamColor()) {
                                moves.add(new ChessMove(myPosition, onePosition, null));
                            }
                            break;
                        }
                    }
                }



                moves.add(new ChessMove(myPosition, onePosition, null));

                row += dir[0];
                col += dir[1];

            }
        }

        return moves;
    }


}
