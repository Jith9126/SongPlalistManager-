package Plist;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PlayListCreator {
	public static void main(String[] args) {
		ConnectionClass conn = ConnectionClass.getConnectionToDB();
		ArrayList<Integer> Ids = new ArrayList<Integer>();
		Scanner input = new Scanner(System.in);
		User currUser = null;

		int opr = 0;
		logLoop: while (true) {
			System.out.println("1. Login");
			System.out.println("2. Create Account");
			System.out.println("3. Exit");
			System.out.print("Choose an option: ");

			opr = input.nextInt();
			switch (opr) {
			case 1:
				System.out.print("Enter username: ");
				String userName = input.next();
				if (conn.userCheck(userName)) {
					System.out.print("Enter the password: ");
					input.nextLine(); // Consume the newline character
					String password = input.nextLine();
					if(conn.login(userName, password)) {
						currUser = new User(userName, password);
						currUser.login();
					}
					else {
						System.out.println("Wrong Password");
						continue logLoop;

					}
				} else {
					System.out.println("User Name doesn't Exist");
					continue logLoop;
					
				}
				break logLoop;
			case 2:
				System.out.print("Enter username: ");
				String usrName = input.next();
				if (conn.userCheck(usrName)) {
					System.out.println("Username already taken");
					continue logLoop;
				}
				input.nextLine(); // Consume the newline character
				String pass = input.nextLine();
				conn.CreateAccount(usrName, pass, currUser);
				break logLoop;
			case 3:
				System.exit(0);
			}
		}

		menuLoop: while (true) {
			System.out.println("1. Add Public Song");
            System.out.println("2. Show All Songs");
            System.out.println("3. Create Playlist");
            System.out.println("4. Add Song to Playlist");
            System.out.println("5. Show Playlist Songs");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
			opr = input.nextInt();
			switch (opr) {
			case 1:
				System.out.println("this song will show to every one");
				new song();
				break;
			case 2:
				System.out.println("Showing all songs");
				conn.showAllSongs(Ids);
				System.out.println("Choose a song");
				opr = input.nextInt();
				if (opr < Ids.size()) {
					System.out.println(conn.getSong(Ids.get(opr - 1)));
				}
//				System.out.println(conn.getSong(opr));
				break;
			case 3:
				input.nextLine();
				System.out.print("Enter name of the Playlist:");
				String playName = input.nextLine();
				currUser.createPlayList(playName, conn);
				break;
			case 4:
				currUser.showPlayList();
				System.out.println("Select PlayList");
				int playListId = input.nextInt();
				playlist currPlayList = currUser.getPlayList(playListId);
				currPlayList.addSong();
//		conn.showAllSongs(Ids);	
//		System.out.println("Select Song");
//		int songId = Ids.get(input.nextInt() - 1);
//		conn.addSongsToPlayList(songId, playListId);
				break;
			case 5:
				currUser.showPlayList();
				System.out.println("Select PlayList");
				opr = input.nextInt();
				playlist cplayList = currUser.getPlayList(opr);
//				conn.showSongsFromPlayList(playList, Ids);
				cplayList.showAllSong();
				System.out.println("Select Song");
				opr = input.nextInt();

				System.out.println(cplayList.getSong(opr).getPath());
//				
//				if (opr <= Ids.size()) {
//					conn.getSong(Ids.get(opr - 1));
//
//				}
//				System.out.println(conn.getSong(opr));
				break;
			case 6:
				break menuLoop;
			}
		}
	}
}

class ClearConsole {
	static void clearCon() {
		System.out.println(
				"\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
	}
}

class song {
	String title;
	String artist;
	String album;
	String path;
	int Id;

	song() {
		try {
			JFileChooser fileChooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Audio Files", "mp3", "wav", "flac", "ogg");
			fileChooser.setFileFilter(filter);
			int result = fileChooser.showOpenDialog(null);
			System.out.println(result);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				String filePath = selectedFile.getAbsolutePath();
				AudioFile audioFile = AudioFileIO.read(new File(filePath));
				Tag tag = audioFile.getTag();
//				ClearConsole.clearCon();
				this.title = tag.getFirst(FieldKey.TITLE);
				this.artist = tag.getFirst(FieldKey.ARTIST);
				this.album = tag.getFirst(FieldKey.ALBUM);
				this.path = filePath;
				ConnectionClass.getConnectionToDB().InsertSongs(this);
				System.out.print("\033[H\033[2J");
				System.out.flush();
				System.out.println("Song Added");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}

	song(String title, String artist, String album, String path,int Id) {
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.path = path;
		this.Id = Id;
	}

	String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return title + " - " + artist;
	}

}


