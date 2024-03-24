package Plist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ConnectionClass {
	private Connection connection;
	private static ConnectionClass instence;

	private ConnectionClass() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/DummySongs");
			Statement statement = connection.createStatement();
			System.out.println("succeed");

		} catch (ClassNotFoundException | SQLException e) {
			System.out.println(e.getMessage());
		}

	}

	void createDatabaseIfNotExist() {
		try {
			Statement statement = connection.createStatement();
			String createSongs = "CREATE TABLE IF NOT EXISTS songs (song_id INT AUTO_INCREMENT PRIMARY KEY,title VARCHAR(100),artist VARCHAR(100),album VARCHAR(100),path VARCHAR(255), UNIQUE KEY unique_song (title, artist));";
			String createUsers = "CREATE TABLE IF NOT EXISTS users (userName VARCHAR(30) PRIMARY KEY, password VARCHAR(200))";
			String createPlaylist = "CREATE TABLE IF NOT EXISTS playlists (playlist_id INT AUTO_INCREMENT PRIMARY KEY,userName VARCHAR(30),playlist_name VARCHAR(100) NOT NULL,FOREIGN KEY (userName) REFERENCES users(userName))";
			String createPlayListSongs = "CREATE TABLE IF NOT EXISTS playlist_songs (playlist_song_id INT AUTO_INCREMENT PRIMARY KEY,playlist_id INT,song_id INT,FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id),FOREIGN KEY (song_id) REFERENCES songs(song_id),UNIQUE KEY (playlist_id, song_id))";

			boolean c1 = statement.execute(createSongs);
			boolean c2 = statement.execute(createUsers);
			boolean c3 = statement.execute(createPlaylist);
			boolean c4 = statement.execute(createPlayListSongs);
			statement.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	void InsertSongs(song newSong) {

		try {
			PreparedStatement insertStatement = connection
					.prepareStatement("INSERT INTO songs (title, artist, album, path) VALUES (?, ?, ?, ?)");
			insertStatement.setString(1, newSong.title);
			insertStatement.setString(2, newSong.artist);
			insertStatement.setString(3, newSong.album);
			insertStatement.setString(4, newSong.path);
			
			System.out.println(newSong.title+" "+newSong.artist +"  "+newSong.album+"  "+newSong.path );
			int RowsAffected = insertStatement.executeUpdate();
			PreparedStatement SelectId = connection
					.prepareStatement("select song_id from songs where artist LIKE ? AND  title like ?");
			SelectId.setString(1, newSong.artist);
			SelectId.setString(2, newSong.title);
			ResultSet songId = SelectId.executeQuery();
			songId.next();
			System.out.println(songId.getInt("song_id")); 
			newSong.Id = songId.getInt("song_id");

			
//			System.out.println(RowsAffected);
			insertStatement.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	boolean userCheck(String UserName) {
		try {
			PreparedStatement insertStatement = connection.prepareStatement("SELECT * FROM users WHERE userName = ?");
			insertStatement.setString(1, UserName);
			ResultSet userSet = insertStatement.executeQuery();
			if (userSet.next()) {
				System.out.println(userSet.getString(1));
				insertStatement.close();
				return true;
			} else {
				System.out.println("false");
				insertStatement.close();
				return false;
			}

		}

		catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	static ConnectionClass getConnectionToDB() {
		if (instence == null) {
			instence = new ConnectionClass();
		}
		return instence;
	}

	boolean login(String UserName, String password) {
		try {
			PreparedStatement insertStatement = connection.prepareStatement("SELECT * FROM users WHERE userName = ?");
			insertStatement.setString(1, UserName);
			ResultSet userSet = insertStatement.executeQuery();
			userSet.next();
			System.out.println(userSet.getString(1));
			if (userSet.getString("password").equals(password)) {
//				currUser = new User(UserName, password);
				System.out.println("Login successful");
				return true;
			} else {
				System.out.println("Wrong Password");
				return false;
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	void showAllSongs(ArrayList<Integer> songId) {
		try {
			Statement insertStatement = connection.createStatement();
			int index = 1;
			songId.clear();
			ResultSet allSongs = insertStatement.executeQuery("Select * from songs");
			while (allSongs.next()) {
				System.out.println(index + ". " + allSongs.getString("title") + "-" + allSongs.getString("artist"));
				songId.add(allSongs.getInt("song_id"));
				index++;
			}

		} catch (SQLException e) {

		}
	}

	String getSong(int songId) {
		String currSongPath = "";
		try {
			PreparedStatement insertStatement = connection.prepareStatement("Select * from songs where song_id = ?");
			insertStatement.setInt(1, songId);
			ResultSet currSong = insertStatement.executeQuery();
			currSong.next();
			currSongPath = currSong.getString("path");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return currSongPath;
	}

	void CreatePlayList(String PlayListName, User currUser) {
		try {
			PreparedStatement insertStatement = connection
					.prepareStatement("INSERT INTO playlists (userName, playlist_name) VALUES (?, ?)");
			insertStatement.setString(1, currUser.getUserName());
			insertStatement.setString(2, PlayListName);

			int exeuc = insertStatement.executeUpdate();
			System.out.println("Playlist Created");

			PreparedStatement Id = connection
					.prepareStatement("Select playlist_name, playlist_id from playlists where userName = ? and playlist_name = ?");
			Id.setString(1, PlayListName);
			Id.setString(2, currUser.getUserName());
			ResultSet playID = Id.executeQuery();
			playID.next();
			currUser.addplayList(new playlist(playID.getString("playlist_name"), playID.getInt("playlist_id")));
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	void CreateAccount(String UserName, String password, User currUser) {
		try {
			PreparedStatement insertStatement = connection
					.prepareStatement("INSERT INTO users (userName, password) VALUES (?, ?)");
			insertStatement.setString(1, UserName);
			insertStatement.setString(2, password);
			insertStatement.executeUpdate();
			currUser = new User(UserName, password);
			System.out.println("Accout Created");

		} catch (SQLException e) {

		}
	}

	void GetAllPlaylists(User currUser) {
		try {
			PreparedStatement insertStatement = connection
					.prepareStatement("Select playlist_name,playlist_id from playlists where userName like ?");
			insertStatement.setString(1, currUser.getUserName());
			ResultSet currPlayList = insertStatement.executeQuery();
			while (currPlayList.next()) {
				
				currUser.addplayList(new playlist(currPlayList.getString("playlist_name"), currPlayList.getInt("playlist_id")));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	void addSongsToPlayList(int songId, playlist currPlayList) {
		try {
			PreparedStatement insertStatement = connection
					.prepareStatement("INSERT INTO playlist_songs (playlist_id, song_id) VALUES (?, ?)");
			insertStatement.setInt(1, currPlayList.PlayListId);
			insertStatement.setInt(2, songId);
			insertStatement.executeUpdate();
			PreparedStatement SelectId = connection
					.prepareStatement("Select title,artist,album,path from songs where song_id = ?");
			SelectId.setInt(1, songId);
			ResultSet Song = SelectId.executeQuery();
			Song.next();
			currPlayList.addSong(new song(Song.getString("title"), Song.getString("artist"), Song.getString("album"),
					Song.getString("path"),songId));

			System.out.println("Song Added");
		} catch (SQLException e) {

		}
	}

	void getSongsFromPlayList(playlist currPlayList) {
		try {
			PreparedStatement insertStatement = connection.prepareStatement(
					"SELECT songs.song_id as Id, title, artist, album,path FROM playlist_songs join songs on playlist_songs.song_id=songs.song_id where	playlist_id = ?");
			insertStatement.setInt(1, currPlayList.PlayListId);
			ResultSet playSongs = insertStatement.executeQuery();
			int index = 1;
			while (playSongs.next()) {
				currPlayList.addSong(new song(playSongs.getString("title"),playSongs.getString("artist"),playSongs.getString("album"),playSongs.getString("path"), playSongs.getInt("Id")));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}