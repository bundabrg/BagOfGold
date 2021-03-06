package one.lindegaard.BagOfGold.storage;

import java.sql.SQLException;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.PlayerBalances;

public interface IDataStore {
	/**
	 * Initialize - opening a connection to the Database and initialize the
	 * connection.
	 * 
	 * @throws DataStoreException
	 */
	void initialize() throws DataStoreException;

	/**
	 * Closing all connections to the Database
	 * 
	 * @throws DataStoreException
	 */
	void shutdown() throws DataStoreException;

	/**
	 * Get the players Settings from the Database
	 * 
	 * @param player
	 * @return
	 * @throws DataStoreException
	 * @throws SQLException
	 */
	PlayerSettings loadPlayerSettings(OfflinePlayer player) throws DataStoreException;

	/**
	 * Update the players Settings in the Database
	 * 
	 * @param playerDataSet
	 * @throws DataStoreException
	 */
	void savePlayerSettings(Set<PlayerSettings> ps) throws DataStoreException;

	/**
	 * Insert all PlayerData for one player into the Database
	 * 
	 * @param ps
	 * @throws DataStoreException
	 */
	void insertPlayerSettingsNOW(PlayerSettings ps) throws DataStoreException;

	/**
	 * Get the players Balances from the Database
	 * 
	 * @param player
	 * @return
	 * @throws DataStoreException
	 * @throws SQLException
	 */
	PlayerBalances loadPlayerBalances(OfflinePlayer player) throws DataStoreException;

	/**
	 * Save the players Balances in the Database
	 * 
	 * @param playerDataSet
	 * @throws DataStoreException
	 */
	void savePlayerBalances(Set<PlayerBalance> ps) throws DataStoreException;

	/**
	 * Insert PlayerBalance one player into the Database
	 * 
	 * @param ps
	 * @throws DataStoreException
	 */
	void insertPlayerBalanceNOW(PlayerBalance ps) throws DataStoreException;

	/**
	 * Convert all tables to use UTF-8 character set.
	 * @param database_name
	 * @throws DataStoreException
	 */
	void databaseConvertToUtf8(String database_name) throws DataStoreException;

}
