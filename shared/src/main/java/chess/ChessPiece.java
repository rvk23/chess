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
        switch (pieceType){
            case BISHOP:
                return calculateBishopMoves(board, myPosition);
            case ROOK:
                return calculateRookMoves(board, myPosition);
            case QUEEN:
                return calculateQueenMoves(board, myPosition);
            case KING:
                return calculateKingMoves(board, myPosition);
            case KNIGHT:
                return calculateKnightMoves(board, myPosition);
            case PAWN:
                return calculatePawnMoves(board, myPosition);
            default:
                return new ArrayList<>();

        }
    }
    public Collection<ChessMove> calculateBishopMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();

        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1,-1}};

        for (int[] dir : directions){
            int row = startRow + dir[0];
            int col = startCol + dir[1];

            while(row >= 0 && row <= 7 && col >= 0 && col <= 7){
                ChessPosition onePosition = new ChessPosition(row + 1, col + 1);

                ChessPiece piece = board.getPiece(onePosition);

                if (piece != null){
                    if (piece.getTeamColor() != this.getTeamColor()){
                        moves.add(new ChessMove(myPosition, onePosition, null));
                    }
                    break;
                }
                moves.add(new ChessMove(myPosition, onePosition, null));

                row+= dir[0];
                col+= dir[1];
            }
        }
        return moves;
    }
    public Collection<ChessMove> calculateRookMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();

        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0,-1}};

        for (int[] dir : directions){
            int row = startRow + dir[0];
            int col = startCol + dir[1];

            while(row >= 0 && row <= 7 && col >= 0 && col <= 7){
                ChessPosition onePosition = new ChessPosition(row + 1, col + 1);

                ChessPiece piece = board.getPiece(onePosition);

                if (piece != null){
                    if (piece.getTeamColor() != this.getTeamColor()){
                        moves.add(new ChessMove(myPosition, onePosition, null));
                    }
                    break;
                }
                moves.add(new ChessMove(myPosition, onePosition, null));

                row+= dir[0];
                col+= dir[1];
            }
        }
        return moves;
    }
    public Collection<ChessMove> calculateQueenMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();

        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0,-1}, {1,1}, {1,-1}, {-1,1}, {-1,-1}};

        for (int[] dir : directions){
            int row = startRow + dir[0];
            int col = startCol + dir[1];

            while(row >= 0 && row <= 7 && col >= 0 && col <= 7){
                ChessPosition onePosition = new ChessPosition(row + 1, col + 1);

                ChessPiece piece = board.getPiece(onePosition);

                if (piece != null){
                    if (piece.getTeamColor() != this.getTeamColor()){
                        moves.add(new ChessMove(myPosition, onePosition, null));
                    }
                    break;
                }
                moves.add(new ChessMove(myPosition, onePosition, null));

                row+= dir[0];
                col+= dir[1];
            }
        }
        return moves;
    }
    public Collection<ChessMove> calculateKingMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();

        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0,-1}, {1,1}, {1,-1}, {-1,1}, {-1,-1}};

        for (int[] dir : directions){
            int row = startRow + dir[0];
            int col = startCol + dir[1];

            if (row >= 0 && row <= 7 && col >= 0 && col <= 7){
                ChessPosition onePosition = new ChessPosition(row + 1, col + 1);

                ChessPiece piece = board.getPiece(onePosition);

                if (piece != null){
                    if (piece.getTeamColor() != this.getTeamColor()){
                        moves.add(new ChessMove(myPosition, onePosition, null));
                    }
                    continue;
                }
                moves.add(new ChessMove(myPosition, onePosition, null));

            }
        }
        return moves;
    }
    public Collection<ChessMove> calculateKnightMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();

        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        int[][] directions = {{1, 2}, {1, -2}, {-1, 2}, {-1,-2}, {2,1}, {2,-1}, {-2,1}, {-2,-1}};

        for (int[] dir : directions){
            int row = startRow + dir[0];
            int col = startCol + dir[1];

            if (row >= 0 && row <= 7 && col >= 0 && col <= 7){
                ChessPosition onePosition = new ChessPosition(row + 1, col + 1);

                ChessPiece piece = board.getPiece(onePosition);

                if (piece != null){
                    if (piece.getTeamColor() != this.getTeamColor()){
                        moves.add(new ChessMove(myPosition, onePosition, null));
                    }
                    continue;
                }
                moves.add(new ChessMove(myPosition, onePosition, null));

            }
        }
        return moves;
    }
    public Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();

        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        ChessPiece myPiece = board.getPiece(myPosition);

        if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
            int[][] directions = {{1, 0}};

            for (int[] dir : directions){
                int row = startRow + dir[0];
                int col = startCol + dir[1];

                if (row >= 0 && row <= 7 && col >= 0 && col <= 7){
                    ChessPosition onePosition = new ChessPosition(row + 1, col + 1);

                    ChessPiece piece = board.getPiece(onePosition);

                    ChessPosition doublePosition = new ChessPosition(row + 1 + dir[0], col + 1);


                    if (startCol == 7){
                        // check left
                        // get left piece
                        ChessPosition leftPosition = new ChessPosition(startRow + 2, startCol);
                        if (startRow == 1){
                            ChessPiece leftPiece = board.getPiece(leftPosition);
                            if (leftPiece != null){
                                if (leftPiece.getTeamColor() != this.getTeamColor()){
                                    moves.add(new ChessMove(myPosition, leftPosition, null));
                                }
                                continue;
                            }
                            if (piece == null){
                                moves.add(new ChessMove(myPosition, onePosition, null));
                                ChessPiece doublePiece = board.getPiece(doublePosition);
                                if (doublePiece == null){
                                    moves.add(new ChessMove(myPosition, doublePosition, null));
                                }
                            }
                        }
                        else if (startRow == 6) {
                            // normal move add if null
                            // add left
                            // promotion
                            ChessPiece leftPiece = board.getPiece(leftPosition);
                            if (leftPiece != null) {
                                if (leftPiece.getTeamColor() != this.getTeamColor()) {
                                    moves.add(new ChessMove(myPosition, leftPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, leftPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, leftPosition, PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, leftPosition, PieceType.KNIGHT));
                                }
                            }
                            if (piece == null){
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.ROOK));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.BISHOP));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.KNIGHT));
                            }
                        }
                        else
                        {
                            ChessPiece leftPiece = board.getPiece(leftPosition);
                            if (leftPiece != null) {
                                if (leftPiece.getTeamColor() != this.getTeamColor()) {
                                    moves.add(new ChessMove(myPosition, leftPosition, null));
                                }
                            }
                            if (piece == null){
                                moves.add(new ChessMove(myPosition, onePosition, null));

                            }
                        }

                    }
                    else if (startCol == 0){
                        ChessPosition rightPosition = new ChessPosition(startRow + 2, startCol + 2);
                        if (startRow == 1){
                            ChessPiece rightPiece = board.getPiece(rightPosition);
                            if (rightPiece != null) {
                                if (rightPiece.getTeamColor() != this.getTeamColor()) {
                                    moves.add(new ChessMove(myPosition, rightPosition, null));
                                }
                            }
                            if (piece == null){
                                moves.add(new ChessMove(myPosition, onePosition, null));
                                ChessPiece doublePiece = board.getPiece(doublePosition);
                                if (doublePiece == null){
                                    moves.add(new ChessMove(myPosition, doublePosition, null));
                                }
                            }

                        }
                        else if (startRow == 6) {
                            ChessPiece rightPiece = board.getPiece(rightPosition);
                            if (rightPiece != null) {
                                if (rightPiece.getTeamColor() != this.getTeamColor()) {
                                    moves.add(new ChessMove(myPosition, rightPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, rightPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, rightPosition, PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, rightPosition, PieceType.KNIGHT));
                                }
                            }
                            if (piece == null){
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.ROOK));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.BISHOP));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.KNIGHT));
                            }

                        }
                        else
                        {
                            if (startRow < 7) {
                                ChessPiece rightPiece = board.getPiece(rightPosition);
                                if (rightPiece != null) {
                                    if (rightPiece.getTeamColor() != this.getTeamColor()) {
                                        moves.add(new ChessMove(myPosition, rightPosition, null));
                                    }
                                }
                            }
                            if (piece == null){
                                moves.add(new ChessMove(myPosition, onePosition, null));

                            }

                        }
                    }
                    else {
                        ChessPosition rightPosition = new ChessPosition(startRow + 2, startCol + 2);
                        ChessPosition leftPosition = new ChessPosition(startRow + 2, startCol);
                        if (startRow == 1) {
                            ChessPiece rightPiece = board.getPiece(rightPosition);
                            if (rightPiece != null) {
                                if (rightPiece.getTeamColor() != this.getTeamColor()) {
                                    moves.add(new ChessMove(myPosition, rightPosition, null));
                                }
                            }
                            ChessPiece leftPiece = board.getPiece(leftPosition);
                            if (leftPiece != null) {
                                if (leftPiece.getTeamColor() != this.getTeamColor()) {
                                    moves.add(new ChessMove(myPosition, leftPosition, null));
                                }
                            }
                            if (piece == null) {
                                moves.add(new ChessMove(myPosition, onePosition, null));
                                ChessPiece doublePiece = board.getPiece(doublePosition);
                                if (doublePiece == null) {
                                    moves.add(new ChessMove(myPosition, doublePosition, null));
                                }
                            }

                        } else if (startRow == 6) {
                            ChessPiece rightPiece = board.getPiece(rightPosition);
                            if (rightPiece != null) {
                                if (rightPiece.getTeamColor() != this.getTeamColor()) {
                                    moves.add(new ChessMove(myPosition, rightPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, rightPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, rightPosition, PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, rightPosition, PieceType.KNIGHT));
                                }
                            }
                            ChessPiece leftPiece = board.getPiece(leftPosition);
                            if (leftPiece != null) {
                                if (leftPiece.getTeamColor() != this.getTeamColor()) {
                                    moves.add(new ChessMove(myPosition, leftPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, leftPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, leftPosition, PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, leftPosition, PieceType.KNIGHT));
                                }
                            }
                            if (piece == null) {
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.ROOK));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.BISHOP));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.KNIGHT));
                            }

                        } else {
                            if (piece == null) {
                                moves.add(new ChessMove(myPosition, onePosition, null));
                            }
                            ChessPiece rightPiece = board.getPiece(rightPosition);
                            if (rightPiece != null) {
                                if (rightPiece.getTeamColor() != this.getTeamColor()) {
                                    moves.add(new ChessMove(myPosition, rightPosition, null));
                                }
                            }

                            ChessPiece leftPiece = board.getPiece(leftPosition);
                            if (leftPiece != null) {
                                if (leftPiece.getTeamColor() != this.getTeamColor()) {
                                    moves.add(new ChessMove(myPosition, leftPosition, null));
                                }
                            }

                        }
                    }



                }

            }

        }
        else
        {
            int[][] directions = {{-1, 0}};

            for (int[] dir : directions){
                int row = startRow + dir[0];
                int col = startCol + dir[1];

                if (row >= 0 && row <= 7 && col >= 0 && col <= 7){
                    ChessPosition onePosition = new ChessPosition(row + 1, col + 1);

                    ChessPiece piece = board.getPiece(onePosition);

                    ChessPosition doublePosition = new ChessPosition(row + dir[0] + 1, col + 1);


                    if (startCol == 7){
                        // check left
                        // get left piece
                        ChessPosition leftPosition = new ChessPosition(startRow , startCol);
                        if (startRow == 6){
                            ChessPiece leftPiece = board.getPiece(leftPosition);
                            if (leftPiece != null){
                                if (leftPiece.getTeamColor() != this.getTeamColor()){
                                    moves.add(new ChessMove(myPosition, leftPosition, null));
                                }
                                continue;
                            }
                            if (piece == null){
                                moves.add(new ChessMove(myPosition, onePosition, null));
                                ChessPiece doublePiece = board.getPiece(doublePosition);
                                if (doublePiece == null){
                                    moves.add(new ChessMove(myPosition, doublePosition, null));
                                }
                            }
                        }
                        else if (startRow == 1) {
                            // normal move add if null
                            // add left
                            // promotion
                            ChessPiece leftPiece = board.getPiece(leftPosition);
                            if (leftPiece != null) {
                                if (leftPiece.getTeamColor() != this.getTeamColor()) {
                                    moves.add(new ChessMove(myPosition, leftPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, leftPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, leftPosition, PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, leftPosition, PieceType.KNIGHT));
                                }
                            }
                            if (piece == null){
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.ROOK));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.BISHOP));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.KNIGHT));
                            }
                        }
                        else
                        {
                            if (startRow > 0) {
                                ChessPiece leftPiece = board.getPiece(leftPosition);
                                if (leftPiece != null) {
                                    if (leftPiece.getTeamColor() != this.getTeamColor()) {
                                        moves.add(new ChessMove(myPosition, leftPosition, null));
                                    }
                                }
                            }
                            if (piece == null){
                                moves.add(new ChessMove(myPosition, onePosition, null));

                            }
                        }

                    }
                    else if (startCol == 0){
                        ChessPosition rightPosition = new ChessPosition(startRow, startCol + 2);
                        if (startRow == 6){
                            ChessPiece rightPiece = board.getPiece(rightPosition);
                            if (rightPiece != null) {
                                if (rightPiece.getTeamColor() != this.getTeamColor()) {
                                    moves.add(new ChessMove(myPosition, rightPosition, null));
                                }
                            }
                            if (piece == null){
                                moves.add(new ChessMove(myPosition, onePosition, null));
                                ChessPiece doublePiece = board.getPiece(doublePosition);
                                if (doublePiece == null){
                                    moves.add(new ChessMove(myPosition, doublePosition, null));
                                }
                            }

                        }
                        else if (startRow == 1) {
                            ChessPiece rightPiece = board.getPiece(rightPosition);
                            if (rightPiece != null) {
                                if (rightPiece.getTeamColor() != this.getTeamColor()) {
                                    moves.add(new ChessMove(myPosition, rightPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, rightPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, rightPosition, PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, rightPosition, PieceType.KNIGHT));
                                }
                            }
                            if (piece == null){
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.ROOK));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.BISHOP));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.KNIGHT));
                            }

                        }
                        else
                        {
                            if (startRow > 0) {
                                ChessPiece rightPiece = board.getPiece(rightPosition);
                                if (rightPiece != null) {
                                    if (rightPiece.getTeamColor() != this.getTeamColor()) {
                                        moves.add(new ChessMove(myPosition, rightPosition, null));
                                    }
                                }
                            }
                            if (piece == null){
                                moves.add(new ChessMove(myPosition, onePosition, null));

                            }

                        }
                    }
                    else {
                        ChessPosition rightPosition = new ChessPosition(startRow, startCol);
                        ChessPosition leftPosition = new ChessPosition(startRow, startCol + 2);
                        if (startRow == 6) {
                            ChessPiece rightPiece = board.getPiece(rightPosition);
                            if (rightPiece != null) {
                                if (rightPiece.getTeamColor() != this.getTeamColor()) {
                                    moves.add(new ChessMove(myPosition, rightPosition, null));
                                }
                            }
                            ChessPiece leftPiece = board.getPiece(leftPosition);
                            if (leftPiece != null) {
                                if (leftPiece.getTeamColor() != this.getTeamColor()) {
                                    moves.add(new ChessMove(myPosition, leftPosition, null));
                                }
                            }
                            if (piece == null) {
                                moves.add(new ChessMove(myPosition, onePosition, null));
                                ChessPiece doublePiece = board.getPiece(doublePosition);
                                if (doublePiece == null) {
                                    moves.add(new ChessMove(myPosition, doublePosition, null));
                                }
                            }

                        } else if (startRow == 1) {
                            ChessPiece rightPiece = board.getPiece(rightPosition);
                            if (rightPiece != null) {
                                if (rightPiece.getTeamColor() != this.getTeamColor()) {
                                    moves.add(new ChessMove(myPosition, rightPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, rightPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, rightPosition, PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, rightPosition, PieceType.KNIGHT));
                                }
                            }
                            ChessPiece leftPiece = board.getPiece(leftPosition);
                            if (leftPiece != null) {
                                if (leftPiece.getTeamColor() != this.getTeamColor()) {
                                    moves.add(new ChessMove(myPosition, leftPosition, PieceType.QUEEN));
                                    moves.add(new ChessMove(myPosition, leftPosition, PieceType.ROOK));
                                    moves.add(new ChessMove(myPosition, leftPosition, PieceType.BISHOP));
                                    moves.add(new ChessMove(myPosition, leftPosition, PieceType.KNIGHT));
                                }
                            }
                            if (piece == null) {
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.ROOK));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.BISHOP));
                                moves.add(new ChessMove(myPosition, onePosition, PieceType.KNIGHT));
                            }

                        } else {
                            if (startRow > 0) {
                                ChessPiece rightPiece = board.getPiece(rightPosition);
                                if (rightPiece != null) {
                                    if (rightPiece.getTeamColor() != this.getTeamColor()) {
                                        moves.add(new ChessMove(myPosition, rightPosition, null));
                                    }
                                }
                            }

                            if (piece == null) {
                                moves.add(new ChessMove(myPosition, onePosition, null));

                            }
                            if (startRow > 0) {
                                ChessPiece leftPiece = board.getPiece(leftPosition);
                                if (leftPiece != null) {
                                    if (leftPiece.getTeamColor() != this.getTeamColor()) {
                                        moves.add(new ChessMove(myPosition, leftPosition, null));
                                    }
                                }
                            }

                        }
                    }



                }

            }

        }

        return moves;
    }

}
