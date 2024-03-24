package Plist;

import java.util.ArrayList;
import java.util.Scanner;

public class playlist {
	int PlayListId;
	String name;
	ArrayList<song> playListSongs;

	public playlist(String name, int ID) {
		this.name = name;
		this.PlayListId = ID;
		playListSongs = new ArrayList<song>();
	}

	@Override
	public String toString() {
		return name;
	}

	void showAllSong() {
		for (int i = 0; i < playListSongs.size(); i++) {
			System.out.println((i + 1) + " - " + playListSongs.get(i));
		}
	}

	song getSong(int index) {
		return playListSongs.get(index - 1);
	}

	void addSong() {
		ArrayList<Integer> Ids = new ArrayList<Integer>();
		Scanner input = new Scanner(System.in);
		ConnectionClass conn = ConnectionClass.getConnectionToDB();
		conn.showAllSongs(Ids);
		System.out.println("Select Song");
		int songId = Ids.get(input.nextInt() - 1);
		conn.addSongsToPlayList(songId, this);
	}

	void addSong(song csong) {
		playListSongs.add(csong);
	}
}
