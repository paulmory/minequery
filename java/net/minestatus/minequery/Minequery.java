package net.minestatus.minequery;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.BindException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A port of Minequery that works with the Bukkit plugin platform.
 * 
 * @author Blake Beaupain
 * @author Kramer Campbell
 * @since 1.0
 */
public final class Minequery extends JavaPlugin {

	/**
	 * The main configuration file.
	 */
	public static final String CONFIG_FILE = "server.properties";

	/**
	 * The logging utility (used for error logging).
	 */
	private final Logger log = Logger.getLogger("Minecraft");

	/**
	 * The host that the server listens on (any by default).
	 */
	private String serverIP;

	/**
	 * The port of the Minecraft server.
	 */
	private int serverPort;

	/**
	 * The port of the Minequery server.
	 */
	private int port;

	/**
	 * The maximum amount of players allowed on the Minecraft server.
	 */
	private int maxPlayers;

	/**
	 * The main Minequery server.
	 */
	private QueryServer server;

	/**
	 * Creates a new <code>Minequery</code> object.
	 */
	public Minequery() {
		// Initialize the Minequery plugin.
		try {
			Properties props = new Properties();
			props.load(new FileReader(CONFIG_FILE));
			serverIP = props.getProperty("server-ip", "ANY");
			serverPort = Integer.parseInt(props.getProperty("server-port", "25565"));
			port = Integer.parseInt(props.getProperty("minequery-port", "25566"));
			maxPlayers = Integer.parseInt(props.getProperty("max-players", "32"));

			// By default, "server-ip=" is set in server.properties which causes the default in getProperty() to not
			// apply. This checks if it's blank and sets it to "ANY" if so.
			if (serverIP.equals("")) {
				serverIP = "ANY";
			}
		} catch (FileNotFoundException ex) {
			// Highly unlikely to ever get this exception as the server.properties file is created before hand.
			log.log(Level.SEVERE, "Could not find server.properties.", ex);
		} catch (IOException ex) {
			log.log(Level.SEVERE, "Error initializing Minequery", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.plugin.Plugin#onDisable()
	 */
	@Override
	public void onDisable() {
		log.info("Stopping Minequery server");

		try {
			if (server != null && server.getListener() != null)
				server.getListener().close();
		} catch (IOException ex) {
			log.log(Level.WARNING, "Unable to close the Minequery listener", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.plugin.Plugin#onEnable()
	 */
	@Override
	public void onEnable() {
		try {
			// Initialize a new server thread.
			server = new QueryServer(this, serverIP, port);

			// Start the server listener.
			server.startListener();

			// Start listening for requests.
			server.start();
		} catch (BindException ex) {
			log.log(Level.SEVERE, "Minequery cannot bind to the port " + port + ". Perhaps it's already in use?");
		} catch (IOException ex) {
			log.log(Level.SEVERE, "Error starting server listener", ex);
		}
	}

	/**
	 * Gets the port that the Minecraft server is running on.
	 *
	 * @return The Minecraft server port
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * Gets the port that the Minequery server is running on.
	 *
	 * @return The Minecraft server port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Gets the maximum amount of players the Minecraft server can hold.
	 * 
	 * @return The maximum amount of players
	 */
	public int getMaxPlayers() {
		return maxPlayers;
	}

}
