package nablarch.common.web.token;

import mockit.Expectations;
import mockit.Mocked;
import nablarch.core.date.SystemTimeUtil;
import nablarch.fw.web.servlet.NablarchHttpServletRequestWrapper;
import nablarch.fw.web.servlet.ServletExecutionContext;
import nablarch.test.support.SystemRepositoryResource;
import nablarch.test.support.db.helper.DatabaseTestRunner;
import nablarch.test.support.db.helper.VariousDbTestHelper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * {@link DbTokenManager}のテストクラス
 */
@RunWith(DatabaseTestRunner.class)
public class DbTokenManagerTest {
    @Rule
    public SystemRepositoryResource repositoryResource = new SystemRepositoryResource(
            "nablarch/common/web/token/DbTokenManagerTest.xml");
    @Mocked
    SystemTimeUtil systemTimeUtil;
    @Mocked
    NablarchHttpServletRequestWrapper unused;
    @Mocked
    ServletExecutionContext unusedContext;
    private static final String TOKEN = "token";

    /**
     * saveTokenでDBトークンテーブルのスキーマが指定されていない場合、
     * デフォルトのスキーマにトークンが保存されること
     */
    @Test
    public void testSaveTokenByDefaultSchema() {
        VariousDbTestHelper.createTable(Token.class);
        new Expectations() {
            {
                SystemTimeUtil.getTimestamp();
                result = new Timestamp(0);
            }
        };
        DbTokenManager tokenManager = repositoryResource.getComponent("tokenManager");
        tokenManager.initialize();
        tokenManager.saveToken(TOKEN, unused);

        // 空のテーブルにinsertできていること
        List<Token> savedTokenList = VariousDbTestHelper.findAll(Token.class);
        assertThat(savedTokenList.size(), is(1));

        Token savedToken = savedTokenList.get(0);
        assertThat(savedToken.value, is(TOKEN));
        assertThat(savedToken.createdAt, is(new Timestamp(0)));
    }

    /**
     * saveTokenでDBトークンテーブルのスキーマが指定された場合、
     * 指定されたスキーマにトークンが保存されること
     */
    @Test
    public void testSaveTokenByAnotherSchema() {
        VariousDbTestHelper.createTable(AnotherToken.class);
        VariousDbTestHelper.setUpTable(
                new AnotherToken("token1", new Timestamp(0)),
                new AnotherToken("token2", new Timestamp(0)));
        new Expectations() {
            {
                SystemTimeUtil.getTimestamp();
                result = new Timestamp(0);
            }
        };
        DbTokenManager tokenManager = repositoryResource.getComponent("tokenManager2");
        tokenManager.initialize();
        NablarchHttpServletRequestWrapper unused = null;

        tokenManager.saveToken(TOKEN, unused);

        // すでにレコードのあるテーブルに追加できていること
        List<AnotherToken> savedTokenList = VariousDbTestHelper.findAll(AnotherToken.class);
        assertThat(savedTokenList.size(), is(3));

        AnotherToken savedToken = VariousDbTestHelper.findById(AnotherToken.class, TOKEN);
        assertThat(savedToken.value, is(TOKEN));
        assertThat(savedToken.createdAt, is(new Timestamp(0)));
    }

    /**
     * isValidTokenでDBトークンテーブルのスキーマが指定されていない場合、
     * デフォルトのスキーマからトークンが読み出されること
     */
    @Test
    public void testIsValidTokenByDefaultSchema() {
        VariousDbTestHelper.createTable(Token.class);
        VariousDbTestHelper.setUpTable(new Token(TOKEN, new Timestamp(0)));
        DbTokenManager tokenManager = repositoryResource.getComponent("tokenManager");
        tokenManager.initialize();
        assertTrue(tokenManager.isValidToken(TOKEN, unusedContext));
        // 判定に使用されたトークンは削除されていること
        List<Token> deletedTokenList = VariousDbTestHelper.findAll(Token.class);
        assertTrue(deletedTokenList.isEmpty());
    }

    /**
     * isValidTokenでDBトークンテーブルのスキーマが指定された場合、
     * 指定されたスキーマからトークンが読み出されること
     */
    @Test
    public void tstIsValidTokenByAnotherSchema() {
        VariousDbTestHelper.createTable(AnotherToken.class);
        VariousDbTestHelper.setUpTable(new AnotherToken(TOKEN, new Timestamp(0)));
        DbTokenManager tokenManager = repositoryResource.getComponent("tokenManager2");
        tokenManager.initialize();
        assertTrue(tokenManager.isValidToken(TOKEN, unusedContext));
        // 判定に使用されたトークンは削除されていること
        List<AnotherToken> deletedTokenList = VariousDbTestHelper.findAll(AnotherToken.class);
        assertTrue(deletedTokenList.isEmpty());
    }

    /**
     * isValidTokenでTokenテーブルが空の場合、無効なトークンと判定されること
     */
    @Test
    public void testIsValidTokenForEmptyTokenTable() {
        VariousDbTestHelper.createTable(Token.class);
        DbTokenManager tokenManager = repositoryResource.getComponent("tokenManager");
        tokenManager.initialize();
        assertFalse(tokenManager.isValidToken(TOKEN, unusedContext));
    }

    /**
     * isValidTokenでTokenが見つからない場合、無効なトークンと判定されること
     */
    @Test
    public void testIsValidTokenForInvalidToken() {
        VariousDbTestHelper.createTable(Token.class);
        VariousDbTestHelper.setUpTable(
                new Token("token1", new Timestamp(0)),
                new Token("token2", new Timestamp(0))
        );
        DbTokenManager tokenManager = repositoryResource.getComponent("tokenManager");
        tokenManager.initialize();
        assertFalse(tokenManager.isValidToken(TOKEN, unusedContext));
    }
}
