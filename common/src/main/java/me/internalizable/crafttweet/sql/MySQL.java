package me.internalizable.crafttweet.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import me.internalizable.crafttweet.config.IConfig;
import me.internalizable.crafttweet.sql.prep.PredefinedStmts;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class MySQL {

    private IConfig pluginConfig;

    @Getter private HikariConfig config = new HikariConfig();

    @Getter public static HikariDataSource ds;

    public MySQL(IConfig config) {
        this.pluginConfig = config;
    }

    public void init() {
        getConfig().setJdbcUrl("jdbc:mysql://" + pluginConfig.getSQLHost() + ":" + pluginConfig.getSQLPort() + "/" + pluginConfig.getSQLDatabase());
        getConfig().setUsername(pluginConfig.getSQLUsername());
        getConfig().setPassword(pluginConfig.getSQLPassword());
        getConfig().addDataSourceProperty("cachePrepStmts", "true");
        getConfig().addDataSourceProperty("prepStmtCacheSize", "250");
        getConfig().addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        getConfig().addDataSourceProperty("useSSL", "false");

        ds = new HikariDataSource(getConfig());
        CompletableFuture.runAsync(this::createTable);
    }

    public void createTable() {
        try {
            PredefinedStmts stmt = PredefinedStmts.CREATION;
            PreparedStatement pst = getDs().getConnection().prepareStatement(stmt.getStatement());
            pst.executeUpdate();
        } catch (SQLException exception) {
            //todo log
        }
    }

}
