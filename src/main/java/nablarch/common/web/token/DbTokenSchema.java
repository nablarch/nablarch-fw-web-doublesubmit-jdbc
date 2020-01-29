package nablarch.common.web.token;

import nablarch.common.schema.TableSchema;

/**
 * トークンテーブルのスキーマ情報を保持するクラス。
 *
 * @author Goro Kumano
 */
public class DbTokenSchema extends TableSchema {
    /** トークンカラム名 */
    private String tokenName;

    /** 作成日時カラム名 */
    private String createdAtName;

    /**
     * トークンカラム名 を取得する。
     *
     * @return トークンカラム名
     */
    public String getTokenName() {
        return tokenName;
    }

    /**
     * 作成日時カラム名 を取得する。
     *
     * @return 作成日時カラム名
     */
    public String getCreatedAtName() {
        return createdAtName;
    }

    /**
     * トークンカラム名 を設定する
     *
     * @param tokenName トークンカラム名
     */
    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    /**
     * 作成日時カラム名 を設定する
     *
     * @param createdAtName 作成日時カラム名
     */
    public void setCreatedAtName(String createdAtName) {
        this.createdAtName = createdAtName;
    }
}
