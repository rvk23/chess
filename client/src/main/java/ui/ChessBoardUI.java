package ui;

import chess.*;

import client.ServerFacade;

import java.util.Map;

public class ChessBoardUI {

    public static void drawBoard(String authToken, ServerFacade facade, int gameID, String perspective) throws Exception {
        ChessGame game = facade.getGameState(authToken, gameID);
        ChessBoard board = game.getBoard();

        boolean blackPerspective = perspective.equalsIgnoreCase("BLACK");

        System.out.println(EscapeSequences.ERASE_SCREEN);
        printColumnHeaders(blackPerspective);

        for (int row = 1; row <= 8; row++) {
            int displayRow = blackPerspective ? row : 9 - row;
            int labelRow = blackPerspective ? row : 9 - row; // change label
            System.out.print(" " + labelRow + " "); // left row label

            for (int col = 1; col <= 8; col++) {
                int displayCol = blackPerspective ? 9 - col : col;
                ChessPosition pos = new ChessPosition(displayRow, displayCol);
                ChessPiece piece = board.getPiece(pos);

                boolean isLightSquare = (displayRow + displayCol) % 2 == 0;
                String bgColor = isLightSquare ? EscapeSequences.SET_BG_COLOR_DARK_GREY
                        : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;

                System.out.print(bgColor);

                if (piece == null) {
                    System.out.print(EscapeSequences.EMPTY);
                } else {
                    System.out.print(getColoredPiece(piece));
                }

                System.out.print(EscapeSequences.RESET_BG_COLOR);
            }

            System.out.println(" " + labelRow); // right row label
        }

        printColumnHeaders(blackPerspective);
    }

    private static void printColumnHeaders(boolean blackPerspective) {
        String letters = "hgfedcba";
        if (!blackPerspective) {
            letters = new StringBuilder(letters).reverse().toString();
        }

        System.out.print("  ");
        for (char c : letters.toCharArray()) {
            System.out.print(" " + c + "  ");
        }
        System.out.println();
    }

    private static String getColoredPiece(ChessPiece piece) {
        Map<ChessPiece.PieceType, String> whitePieces = Map.of(
                ChessPiece.PieceType.KING, EscapeSequences.WHITE_KING,
                ChessPiece.PieceType.QUEEN, EscapeSequences.WHITE_QUEEN,
                ChessPiece.PieceType.BISHOP, EscapeSequences.WHITE_BISHOP,
                ChessPiece.PieceType.KNIGHT, EscapeSequences.WHITE_KNIGHT,
                ChessPiece.PieceType.ROOK, EscapeSequences.WHITE_ROOK,
                ChessPiece.PieceType.PAWN, EscapeSequences.WHITE_PAWN
        );

        Map<ChessPiece.PieceType, String> blackPieces = Map.of(
                ChessPiece.PieceType.KING, EscapeSequences.BLACK_KING,
                ChessPiece.PieceType.QUEEN, EscapeSequences.BLACK_QUEEN,
                ChessPiece.PieceType.BISHOP, EscapeSequences.BLACK_BISHOP,
                ChessPiece.PieceType.KNIGHT, EscapeSequences.BLACK_KNIGHT,
                ChessPiece.PieceType.ROOK, EscapeSequences.BLACK_ROOK,
                ChessPiece.PieceType.PAWN, EscapeSequences.BLACK_PAWN
        );

        return piece.getTeamColor() == ChessGame.TeamColor.WHITE
                ? blackPieces.get(piece.getPieceType())
                : whitePieces.get(piece.getPieceType());
    }
}
