package procedures;

//import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.voltdb.*;
import org.voltdb.types.TimestampType;
import org.voltdb.client.ClientResponse;

public class UpsertCustomerScores extends VoltProcedure {

    public final SQLStmt upsertScores = new SQLStmt(
        "UPSERT INTO customer_contest_score (contest_id, customer_id, score) "+
        "SELECT c.contest_id, r.customer_id, SUM(p.score) "+
        "FROM "+
        "  customer_contest_roster r "+
        "INNER JOIN nfl_contest_large c ON r.contest_id = c.contest_id "+
        "INNER JOIN nfl_player_game_score p ON r.player_id = p.player_id AND c.game_id = p.game_id "+
        "WHERE "+
        " r.contest_id = ? "+
        "GROUP BY c.contest_id, r.customer_id "+
        "ORDER BY 1,2"+
        ";");


    public final SQLStmt selectScores = new SQLStmt(
        "SELECT customer_id, score "+
        "FROM customer_contest_score "+
        "WHERE contest_id = ? "+
        "ORDER BY score;");
        
    public VoltTable[] run(int partitionVal, int contest) throws VoltAbortException {

	voltQueueSQL(upsertScores, contest);
        voltQueueSQL(selectScores, contest);
        return voltExecuteSQL(true);

    }
}
