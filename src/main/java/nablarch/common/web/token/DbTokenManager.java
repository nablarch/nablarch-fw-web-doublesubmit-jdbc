package nablarch.common.web.token;

import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.connection.AppDbConnection;
import nablarch.core.db.connection.DbConnectionContext;
import nablarch.core.db.statement.SqlPStatement;
import nablarch.core.repository.initialization.Initializable;
import nablarch.core.transaction.TransactionContext;
import nablarch.fw.web.servlet.NablarchHttpServletRequestWrapper;
import nablarch.fw.web.servlet.ServletExecutionContext;

public class DbTokenManager implements TokenManager, Initializable {

    /** トークンテーブル物理名 */
    private String tableName;

    /** トークンテーブルのトークンカラム物理名 */
    private String tokenColumnName;

    /** トークンテーブルの作成日時カラム物理名 */
    private String createdAtColumnName;

    /** データベーストランザクション名 */
    private String dbTransactionName = TransactionContext.DEFAULT_TRANSACTION_CONTEXT_KEY;

    /** 登録用SQL */
    private String insertSql;

    /** 削除用SQL */
    private String deleteSql;

    /**
     * tableName を設定する
     *
     * @param tableName テーブル名
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * tokenColumnName を設定する
     *
     * @param tokenColumnName トークンカラム名
     */
    public void setTokenColumnName(String tokenColumnName) {
        this.tokenColumnName = tokenColumnName;
    }

    /**
     * createdAtColumnName を設定する
     *
     * @param createdAtColumnName 作成日時カラム名
     */
    public void setCreatedAtColumnName(String createdAtColumnName) {
        this.createdAtColumnName = createdAtColumnName;
    }

    /**
     * dbTransactionName を設定する
     *
     * @param dbTransactionName トランザクション名
     */
    public void setDbTransactionName(String dbTransactionName) {
        this.dbTransactionName = dbTransactionName;
    }

    /**
     * 初期化処理を行う。
     * トークンテーブル登録用、削除用のSQL文を組み立てる。
     */
    public void initialize() {
        String tmpInsertSql = "  INSERT INTO $TABLE_NAME$ "
                + "  ($TOKEN$,$CREATED_AT$)"
                + "  VALUES (?,?)";

        insertSql = tmpInsertSql.replace("$TABLE_NAME$", tableName)
                .replace("$TOKEN$", tokenColumnName)
                .replace("$CREATED_AT$", createdAtColumnName);

        String tmpdeleteSql = "  DELETE FROM $TABLE_NAME$ "
                + "  WHERE $TOKEN$ = ?";

        deleteSql = tmpdeleteSql.replace("$TABLE_NAME$", tableName)
                .replace("$TOKEN$", tokenColumnName);
    }

    @Override
    public void saveToken(String serverToken, NablarchHttpServletRequestWrapper request) {
        AppDbConnection connection = DbConnectionContext.getConnection(dbTransactionName);
        SqlPStatement insert = connection.prepareStatement(insertSql);
        insert.setString(1, serverToken);
        insert.setTimestamp(2, SystemTimeUtil.getTimestamp());
        insert.executeUpdate();
    }

    @Override
    public boolean isValidToken(String clientToken, ServletExecutionContext context) {
        AppDbConnection connection = DbConnectionContext.getConnection(dbTransactionName);
        SqlPStatement delete = connection.prepareStatement(deleteSql);
        delete.setString(1, clientToken);
        return delete.executeUpdate() == 1;
    }
}
