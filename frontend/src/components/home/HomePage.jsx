import React, { useEffect, useState } from "react";
import Sidebar from "../sidebar/Sidebar.jsx";
import MoodSelector from "./MoodSelector";
import GenreSelector from "./GenreSelector";
import SongList from "./SongList";
import MiniPlayer  from "../minicomponent/MiniPlayer.jsx";
import { FaMusic, FaFolderOpen,FaSignOutAlt  } from "react-icons/fa";
import "./Home.css";

const HomePage = () => {
    const [genre, setGenre] = useState("");
    const [mood, setMood] = useState("");
    const [songs, setSongs] = useState([]);
    const [loading, setLoading] = useState(false);
    const [searchType, setSearchType] = useState("");
    const [user, setUser] = useState(null);
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const [offset, setOffset] = useState(0);
    const [hasMore, setHasMore] = useState(true);

    useEffect(() => {
        setSongs([]);
        setOffset(0);
        setHasMore(true);
    }, [searchType]);

    useEffect(() => {
        if (genre && mood && searchType) fetchSongs(true);
    }, [genre, mood, searchType]);

    useEffect(() => {
        fetch("http://localhost:8080/user/profile", { credentials: "include" })
            .then(res => res.json())
            .then(setUser)
            .catch(err => console.error("❌ Error al obtener perfil", err));
    }, []);

    const logout = async () => {
        await fetch("http://localhost:8080/logout", { method: "POST", credentials: "include" });
        window.location.href = "/";
    };

    const fetchSongs = async (reset = false) => {
        const searchQuery = `música ${genre} ${mood}`;
        const usedOffset = reset ? 0 : offset;
        setLoading(true);
        try {
            const response = await fetch(
                `http://localhost:8080/songs/search?q=${encodeURIComponent(searchQuery)}&type=${searchType}&offset=${usedOffset}`,
                { method: "GET", credentials: "include" }
            );
            if (!response.ok) throw new Error("Error en la búsqueda");
            const data = await response.json();

            setHasMore(data.length > 0);;
            if (reset) {
                setSongs(data);
                setOffset(50);
                setHasMore(data.length > 0);
            } else {
                setSongs(prev => [...prev, ...data]);
                setOffset(prev => prev + 50);
                setHasMore(data.length > 0);
            }
        } catch (error) {
            console.error("Error fetching songs:", error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="home">
            <Sidebar />
            <MiniPlayer />
            {user && (
                <div className="user-profile" onClick={() => setDropdownOpen(!dropdownOpen)}>
                    <img src={user.images?.[0]?.url || "/default-avatar.png"} alt="Avatar" />
                    {dropdownOpen && (
                        <div className="dropdown-menu">
                            <button onClick={logout}><FaSignOutAlt /> Logout</button>
                        </div>
                    )}
                </div>
            )}
            <main className="home__main">
                <h2 className="home__title">¿What do you want to listen today?</h2>

                <div className="search-tabs">
                    <button onClick={() => { setSearchType("song"); setMood(""); setGenre(""); setSongs([]); }} className={searchType === "song" ? "active" : ""}>
                        <FaMusic className="icon" /> Songs
                    </button>
                    <button onClick={() => { setSearchType("playlist"); setMood(""); setGenre(""); setSongs([]); }} className={searchType === "playlist" ? "active" : ""}>
                        <FaFolderOpen className="icon" /> Playlists
                    </button>
                </div>

                {searchType && (
                    <MoodSelector onSelect={(selectedMood) => { setMood(selectedMood); setGenre(""); setSongs([]); }} selectedEmotion={mood} />
                )}

                {searchType && mood && (
                    <GenreSelector onSelect={setGenre} selectedGenre={genre} />
                )}

                <div className="home__content">
                    {loading ? (
                        <div className="loader"></div>
                    ) : (
                        <>
                            {songs.length === 0 && genre && (
                                <p className="no-results">Results not found for this combination</p>
                            )}
                            <SongList songs={songs} />
                            {songs.length > 0 && hasMore && (
                                <div className="load-more-wrapper">
                                    <button onClick={() => fetchSongs(false)} className="load-more-btn">
                                        Load More
                                    </button>
                                </div>
                            )}
                        </>
                    )}
                </div>
            </main>
        </div>
    );
};

export default HomePage;

