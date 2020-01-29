package nablarch.common.web.token;

import nablarch.core.date.SystemTimeUtil;
import nablarch.core.db.connection.AppDbConnection;
import nablarch.core.db.statement.SqlPStatement;
import nablarch.core.db.transaction.SimpleDbTransactionExecutor;
import nablarch.core.db.transaction.SimpleDbTransactionManager;
import nablarch.core.repository.initialization.Initializable;
import nablarch.fw.web.servlet.NablarchHttpServletRequestWrapper;
import nablarch.fw.web.servlet.ServletExecutionContext;

/**
 * DBを使用した{@link TokenManager}実装クラス
 */
public class DbTokenManager implements TokenManager, Initializable {
    /** SimpleDbTransactionManagerのインスタンス。 */
    private SimpleDbTransactionManager dbManager;

    /** トークンテーブルのスキーマ */
    private DbTokenSchema dbTokenSchema;

    /** 登録用SQL */
    private String insertSql;

    /** 削除用SQL */
    private String deleteSql;

    /**
     * DbManagerのインスタンスをセットする。
     *
     * @param dbManager SimpleDbTransactionManagerのインスタンス
     */
    public void setDbManager(SimpleDbTransactionManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * トークンテーブルのスキーマをセットする。
     *
     * @param dbTokenSchema トークンテーブルのスキーマ
     */
    public void setDbTokenSchema(DbTokenSchema dbTokenSchema) {
        this.dbTokenSchema = dbTokenSchema;
    }

    /**
     * 初期化処理を行う。
     * トークンテーブル登録用、削除用のSQL文を組み立てる。
     */
    public void initialize() {
        if (dbTokenSchema == null) {
            // デフォルトのトークンテーブルスキーマをセットする
            dbTokenSchema = new DbTokenSchema();
            dbTokenSchema.setTableName("TOKEN");
            dbTokenSchema.setTokenName("VALUE");
            dbTokenSchema.setCreatedAtName("CREATED_AT");
        }
        String tmpInsertSql = "  INSERT INTO $TABLE_NAME$ "
                + "  ($TOKEN$,$CREATED_AT$)"
                + "  VALUES (?,?)";

        insertSql = tmpInsertSql.replace("$TABLE_NAME$", dbTokenSchema.getTableName())
                .replace("$TOKEN$", dbTokenSchema.getTokenName())
                .replace("$CREATED_AT$", dbTokenSchema.getCreatedAtName());

        String tmpDeleteSql = "  DELETE FROM $TABLE_NAME$ "
                + "  WHERE $TOKEN$ = ?";

        deleteSql = tmpDeleteSql.replace("$TABLE_NAME$", dbTokenSchema.getTableName())
                .replace("$TOKEN$", dbTokenSchema.getTokenName());
    }

    @Override
    public void saveToken(final String serverToken, NablarchHttpServletRequestWrapper request) {
        new SimpleDbTransactionExecutor<Void>(dbManager) {
            @Override
            public Void execute(AppDbConnection connection) {
                SqlPStatement insert = connection.prepareStatement(insertSql);
                insert.setString(1, serverToken);
                insert.setTimestamp(2, SystemTimeUtil.getTimestamp());
                insert.executeUpdate();
                return null;
            }
        }.doTransaction();
    }

    @Override
    public boolean isValidToken(final String clientToken, ServletExecutionContext context) {
        return new SimpleDbTransactionExecutor<Boolean>(dbManager) {
            @Override
            public Boolean execute(AppDbConnection connection) {
                SqlPStatement delete = connection.prepareStatement(deleteSql);
                delete.setString(1, clientToken);
                return delete.executeUpdate() == 1;
            }
        }.doTransaction();
    }
}
