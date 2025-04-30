import React, { useEffect, useState } from "react";
import Sidebar from "../sidebar/Sidebar.jsx";
import MoodSelector from "./MoodSelector";
import GenreSelector from "./GenreSelector";
import SongList from "./SongList";
import MiniPlayer  from "../minicomponent/MiniPlayer.jsx";
import { FaMusic, FaFolderOpen } from "react-icons/fa";
import "./Home.css";

const HomePage = () => {
    const [genre, setGenre] = useState("");
    const [mood, setMood] = useState("");
    const [songs, setSongs] = useState([]);
    const [loading, setLoading] = useState(false);
    const [searchType, setSearchType] = useState("");

    useEffect(() => {
        setSongs([]);
    }, [searchType]);

    useEffect(() => {
        if (genre && mood && searchType) {
            fetchSongs();
        }
    }, [genre, mood, searchType]);

    const fetchSongs = async () => {
        const searchQuery = `música ${genre} ${mood}`;

        setLoading(true);
        try {
            const response = await fetch(
                `http://localhost:8080/songs/search?q=${encodeURIComponent(searchQuery)}&type=${searchType}`,
                {
                    method: "GET",
                    credentials: "include"
                }
            );

            if (!response.ok) {
                throw new Error("Error al buscar canciones");
            }

            const data = await response.json();
            setSongs(data);
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
            <main className="home__main">

                <h2 className="home__title">¿What do you want to listen today?</h2>

                <div className="search-tabs">
                    <button
                        onClick={() => {
                            setSearchType("song");
                            setMood("");
                            setGenre("");
                            setSongs([]);
                        }}
                        className={searchType === "song" ? "active" : ""}
                    >
                        <FaMusic className="icon" /> Songs
                    </button>
                    <button
                        onClick={() => {
                            setSearchType("playlist");
                            setMood("");
                            setGenre("");
                            setSongs([]);
                        }}
                        className={searchType === "playlist" ? "active" : ""}
                    >
                        <FaFolderOpen className="icon" /> Playlists
                    </button>
                </div>

                {searchType && (
                    <MoodSelector
                        onSelect={(selectedMood) => {
                            setMood(selectedMood);
                            setGenre("");
                            setSongs([]);
                        }}
                        selectedEmotion={mood}
                    />
                )}

                {searchType && mood && (
                    <GenreSelector
                        onSelect={setGenre}
                        selectedGenre={genre}
                    />
                )}

                <div className="home__content">
                    {loading ? (
                        <div className="loader"></div>
                    ) : (
                        <>
                            {songs.length === 0 && genre && (
                                <p className="no-results">No se encontraron resultados para esta combinación.</p>
                            )}
                            <SongList songs={songs} />
                        </>
                    )}
                </div>
            </main>
        </div>
    );
};

export default HomePage;
