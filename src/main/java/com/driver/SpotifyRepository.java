package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User newUser=new User(name,mobile);
        users.add(newUser);

        //Initialized userPlaylist map as well
        userPlaylistMap.put(newUser,new ArrayList<>());
        return newUser;
    }

    public Artist createArtist(String name) {
        Artist newArtist=new Artist(name);
        artists.add(newArtist);
        //Initialized artistAlbum map as well
        artistAlbumMap.put(newArtist,new ArrayList<>());
        return newArtist;
    }

    public Album createAlbum(String title, String artistName) {
        Album newAlbum=new Album(title);
        for(Artist artist:artistAlbumMap.keySet()){
            if(artist.getName().equals(artistName)){
                albums.add(newAlbum);
                //added to artist album map as well
                artistAlbumMap.get(artist).add(newAlbum);
                //Initialized album song as well
                albumSongMap.put(newAlbum,new ArrayList<>());
            }
        }
        return newAlbum;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        if(albums!=null) {
            for (Album album : albumSongMap.keySet()) {
                if (album.getTitle().equals(albumName)) {
                    Song newSong = new Song(title, length);
                    songs.add(newSong);
                    songLikeMap.put(newSong,new ArrayList<>());
                    albumSongMap.get(album).add(newSong);
                    return newSong;
                }
            }
        }
        throw new Exception("Album does not exist");

    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
       if(users!=null){
           User oldUser=null;
           for(User user:users){
               if(user.getMobile().equals(mobile)){
                   oldUser=user;
                   break;
               }
           }
           if(oldUser!=null){
               Playlist newPlaylist=new Playlist(title);
               //added to the playlist database;
               playlists.add(newPlaylist);
               //adding playlist to user playlist map
               userPlaylistMap.get(oldUser).add(newPlaylist);
               //adding playlist to creator playlist map--doubt------------
               creatorPlaylistMap.put(oldUser,newPlaylist);
               //initialized to the playlist song map
               playlistSongMap.put(newPlaylist,new ArrayList<>());
               //added to the playlist listner map

               List<User>users=new ArrayList<>();
               users.add(oldUser);
               playlistListenerMap.put(newPlaylist,users);

               if(songs!=null){
                   for(Song song: songs){
                       if(song.getLength()==length){
                           playlistSongMap.get(newPlaylist).add(song);
                       }
                   }
               }
               return newPlaylist;
           }else{
               throw new Exception("User does not exist");
           }
       }else{
           throw new Exception("User does not exist");
       }
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        if(users!=null){
            User oldUser=null;
            for(User user:users){
                if(user.getMobile().equals(mobile)){
                    oldUser=user;
                    break;
                }
            }
            if(oldUser!=null){
                Playlist newPlaylist=new Playlist(title);
                playlists.add(newPlaylist);
                userPlaylistMap.get(oldUser).add(newPlaylist);
                creatorPlaylistMap.put(oldUser,newPlaylist); //--doubt
                //initialized to the playlist song map
                playlistSongMap.put(newPlaylist,new ArrayList<>());

                //added to the playlist listner map
                List<User>users=new ArrayList<>();
                users.add(oldUser);
                playlistListenerMap.put(newPlaylist,users);

                if(songs!=null){
                    for(Song song:songs){
                        if(songTitles.contains(song.getTitle())){
                            if(playlistSongMap.get(newPlaylist).contains(song)==false){
                                playlistSongMap.get(newPlaylist).add(song);
                            }
                        }
                    }
                }
                return newPlaylist;

            }else{
                throw new Exception("User does not exist");
            }
        }else{
            throw new Exception("User does not exist");
        }
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User oldUser=null;
        Playlist oldPlaylist=null;
        if(users!=null && users.size()!=0){
            for(User user:users){
                if(user.getMobile().equals(mobile)){
                    oldUser=user;
                    break;
                }
            }
            if(oldUser==null){
                throw new Exception("User does not exist");
            }
        }else{
            throw new Exception("User does not exist");
        }
        if(playlists!=null && playlists.size()!=0){
            for(Playlist playlist:playlists){
                if(playlist.getTitle().equals(playlistTitle)){
                    oldPlaylist=playlist;
                }
            }
            if(oldPlaylist==null){
                throw new Exception("Playlist does not exist");
            }
        }else{
            throw new Exception("Playlist does not exist");
        }
        if(creatorPlaylistMap.containsKey(oldUser)==false || (creatorPlaylistMap.containsKey(oldUser)==true && !(creatorPlaylistMap.get(oldUser).equals(oldPlaylist)))){
            if(playlistListenerMap.get(oldPlaylist).contains(oldUser)==false){
                playlistListenerMap.get(oldPlaylist).add(oldUser);
            }
        }
        return oldPlaylist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User oldUser=null;
        Song oldSong=null;
        if(users!=null && users.size()!=0){
            for(User user:users){
                if(user.getMobile().equals(mobile)){
                    oldUser=user;
                    break;
                }
            }
            if(oldUser==null){
                throw new Exception("User does not exist");
            }
        }else{
            throw new Exception("User does not exist");
        }

        if(songs!=null && songs.size()!=0){
            for(Song song:songs){
                if(song.getTitle().equals(songTitle)){
                    oldSong=song;
                    break;
                }
            }
            if(oldSong==null){
                throw new Exception("Song does not exist");
            }
        }else{
            throw new Exception("Song does not exist");
        }
        if(songLikeMap.get(oldSong).contains(oldUser)==false){
            songLikeMap.get(oldSong).add(oldUser);
            oldSong.setLikes(oldSong.getLikes()+1);
            Album oldAlbum=null;
            for(Album album: albumSongMap.keySet()){
                if(albumSongMap.get(album).contains(oldSong)==true){
                    oldAlbum=album;
                }
            }
            for(Artist artist:artistAlbumMap.keySet()){
                if(artistAlbumMap.get(artist).contains(oldAlbum)){
                    artist.setLikes(artist.getLikes()+1);
                    break;
                }
            }
        }
        return oldSong;
    }

    public String mostPopularArtist() {
        int cnt=0;
        Artist popularArtist=null;
        for(Artist artist:artists){
            if(artist.getLikes()>cnt){
                cnt=artist.getLikes();
                popularArtist=artist;
            }
        }
        return popularArtist.getName();
    }

    public String mostPopularSong() {
        int cnt=0;
        Song popularSong=null;
        for(Song song:songs){
            if(song.getLikes()>cnt){
                cnt=song.getLikes();
                popularSong=song;
            }
        }
        return popularSong.getTitle();
    }
}
