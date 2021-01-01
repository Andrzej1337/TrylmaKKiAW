package player;

import movement.AdditionalVerifyCondition;
import movement.GameMaster;
import movement.JumpStatusVerifyCondition;
import movement.PreviousPawnVerifyCondition;
import shared.Coord;
import shared.PlayerColor;
import shared.Response;
import shared.ResponseInterpreter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClassicBot extends Player
{
    List<Move> moves;
    private GameMaster gameMaster;
    private Coord target;
    private JumpStatusVerifyCondition jumpStatusVerifyCondition;
    private PreviousPawnVerifyCondition previousPawnVerifyCondition;
    private AdditionalVerifyCondition[] verifyConditions;
    boolean strayMode = false;
    private int skipCount;

    public ClassicBot(PlayerColor playerColor, GameMaster gameMaster)
    {
        this.gameMaster = gameMaster;
        moves = new ArrayList<>();
        this.color = playerColor;
        setTarget();
        jumpStatusVerifyCondition = new JumpStatusVerifyCondition(0);
        previousPawnVerifyCondition = new PreviousPawnVerifyCondition();
        verifyConditions = new AdditionalVerifyCondition[]{jumpStatusVerifyCondition, previousPawnVerifyCondition};
    }

    @Override
    public void sendCommand(String command)
    {
        Response[] responses = ResponseInterpreter.getResponses(command);
        executeResponses(responses);
    }

    @Override
    public String readResponse()
    {
        try
        {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException ex)
        {
            throw new RuntimeException();
        }
        if (moves.size() == 0 || bestMove().getValue() <= 0)
        {
            skipCount++;
            if (skipCount > 1)
            {
                activateStrayMode();
            }
            return "SKIP";
        } else
        {
            skipCount = 0;
            return makeMoveCommand(bestMove());
        }

    }

    /**
     * Tworzy listę wszystkich dostępnych ruchów
     */
    void listMoves()
    {
        List<Coord> possibleMoves;
        moves.clear();
        for (int i = 1; i <= gameMaster.getBoard().getColumns(); i++)
        {
            for (int j = 1; j <= gameMaster.getBoard().getRows(); j++)
            {
                if (gameMaster.getBoard().getField(i, j).getCurrentColor() == color)
                {
                    if (!strayMode || gameMaster.getBoard().getField(i, j).getTargetColor() != color)
                    {
                        previousPawnVerifyCondition.setCurrentXY(i, j);
                        possibleMoves = gameMaster.getPossibleMovesForPos(i, j, verifyConditions);
                        for (Coord temp : possibleMoves)
                        {
                            moves.add(new Move(new Coord(i, j), temp));
                        }
                    }
                }
            }
        }
    }

    /**
     * Przypisujemy każdemu ruchowi wartość (tym większą im bardziej zbliża nas do celu)
     */
    void evaluateMoves()
    {
        if (moves.size() == 0)
        {
            return;
        }
        Double prevDistance, currDistance;
        for (Move temp : moves)
        {
            prevDistance = calcDistance(temp.from, target);
            currDistance = calcDistance(temp.to, target);
            temp.setValue(prevDistance - currDistance);
        }
        moves.sort(Collections.reverseOrder());
        updateVerifyConditions(bestMove());
    }

    Move bestMove()
    {
        return moves.size() != 0 ? moves.get(0) : null;
    }

    /**
     * Zwraca odległość dwóch koordynatów w uładzie kartezjańskim
     */
    Double calcDistance(Coord c1, Coord c2)
    {
        return Math.sqrt(Math.pow(c1.getX() - c2.getX(), 2) + Math.pow(c1.getY() - c2.getY(), 2));
    }

    String makeMoveCommand(Move move)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MOVE ");
        stringBuilder.append(move.from.getX());
        stringBuilder.append(" ");
        stringBuilder.append(move.from.getY());
        stringBuilder.append(" ");
        stringBuilder.append(move.to.getX());
        stringBuilder.append(" ");
        stringBuilder.append(move.to.getY());

        return stringBuilder.toString();
    }

    void updateVerifyConditions(Move move)
    {
        resetVerifyConditions();
        previousPawnVerifyCondition.setCurrentXY(move.to.getX(), move.to.getY());
        jumpStatusVerifyCondition.setStatus(gameMaster.verifyMove(move.from.getX(), move.from.getY(), move.to.getX(), move.to.getY(), verifyConditions));
        previousPawnVerifyCondition.setPreviousXY(move.to.getX(), move.to.getY());

    }

    void resetVerifyConditions()
    {
        jumpStatusVerifyCondition.setStatus(0);
        previousPawnVerifyCondition.setCurrentXY(0, 0);
        previousPawnVerifyCondition.setPreviousXY(0, 0);
    }

    void executeResponses(Response responses[])
    {
        for (Response response : responses)
        {
            executeResponse(response);
        }
    }

    void executeResponse(Response response)
    {
        switch (response.getCode())
        {
            case "YOU":
                resetVerifyConditions();
                listMoves();
                evaluateMoves();
                break;
            case "OK":
                listMoves();
                evaluateMoves();
                break;

        }
    }

    void activateStrayMode()
    {
        strayMode = true;
        setStrayTarget();
    }

    void setStrayTarget()
    {
        for (int i = 1; i <= gameMaster.getBoard().getColumns(); i++)
        {
            for (int j = 1; j <= gameMaster.getBoard().getRows(); j++)
            {
                if (gameMaster.getBoard().getField(i, j).getTargetColor() == color && gameMaster.getBoard().getField(i, j).getCurrentColor() == PlayerColor.NONE)
                {
                    target = new Coord(i, j);
                }
            }
        }
    }


    /**
     * Ustawia cel na wierzchołek mety
     */
    void setTarget()
    {
        switch (color)
        {
            case RED:
                target = new Coord(7, 1);
                break;
            case GREEN:
                target = new Coord(7, 17);
                break;
            case BLUE:
                target = new Coord(13, 13);
                break;
            case ORANGE:
                target = new Coord(1, 5);
                break;
            case VIOLET:
                target = new Coord(13, 5);
                break;
            case YELLOW:
                target = new Coord(1, 13);
                break;
        }
    }


    static class Move implements Comparable<Move>
    {
        Coord from, to;
        Double value;

        Move(Coord from, Coord to)
        {
            this.from = from;
            this.to = to;
        }

        @Override
        public int compareTo(Move o)
        {
            return this.value.compareTo(o.value);
        }

        Double getValue()
        {
            return value;
        }

        void setValue(Double value)
        {
            this.value = value;
        }
    }
}
