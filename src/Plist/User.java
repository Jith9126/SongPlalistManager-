package Plist;

import java.util.ArrayList;

class User {
	private String UserName;
	private String passWord;
//	private static User currUser;
	ArrayList<playlist> playlists;

	User(String UserName, String password) {
		this.UserName = UserName;
		this.passWord = password;
		playlists = new ArrayList<playlist>();
	}

	String getUserName() {
		return UserName;
	}
	
	void login() {
		ConnectionClass currConection = ConnectionClass.getConnectionToDB();
		currConection.GetAllPlaylists(this);
		for(playlist pl:playlists) {
			currConection.getSongsFromPlayList(pl);
		}
	}

	void addplayList(playlist playlst) {
		playlists.add(playlst);
	}

	void showPlayList() {
		for (int i = 0; i < playlists.size(); i++) {
			System.out.println((i + 1) + " - " + playlists.get(i));
		}
	}

	void createPlayList(String name, ConnectionClass conn) {
		conn.CreatePlayList(name, this);
//		addplayList(new playlist(name));

	}


	playlist getPlayList(int index) {
		return playlists.get(index - 1);
	}
}
