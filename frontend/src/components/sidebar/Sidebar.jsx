import React, { useEffect, useState } from "react";
import "./Sidebar.css";
import axios from "axios";
import { toast } from "react-hot-toast";
import { FaPlay, FaBars, FaTimes } from "react-icons/fa";

const Sidebar = () => {
    const [playlists, setPlaylists] = useState([]);
    const [isOpen, setIsOpen] = useState(false);

    useEffect(() => {
        axios
            .get("http://localhost:8080/playlists", { withCredentials: true })
            .then((res) => {
                setPlaylists(res.data.items || []);
            })
            .catch((err) => {
                console.error("❌ Error al cargar playlists", err);
                toast.error("No se pudieron cargar las playlists.");
            });
    }, []);

    const playPlaylist = (id) => {
        axios
            .post(`/api/playlists/${id}/play`, {}, { withCredentials: true })
            .then(() => {
                console.log(`▶️ Reproduciendo playlist ${id}`);
            })
            .catch((err) => {
                console.error("❌ Error al reproducir", err);
            });
    };

    return (
        <>
            <button className="sidebar__toggle" onClick={() => setIsOpen(!isOpen)}>
                {isOpen ? <FaTimes /> : <FaBars />}
            </button>
            <div className={`sidebar ${isOpen ? "sidebar--open" : ""}`}>
                <h2 className="sidebar__logo">Moodify</h2>
                <div className="sidebar__divider" />
                <div className="sidebar__playlists">
                    {playlists.map((pl) => (
                        <div key={pl.id} className="sidebar__playlist">
                            {pl.images && pl.images.length > 0 ? (
                                <img
                                    src={pl.images[0].url}
                                    alt={pl.name}
                                    className="sidebar__playlist-img"
                                />
                            ) : (
                                <div className="sidebar__playlist-placeholder">🎵</div>
                            )}
                            <span
                                className="sidebar__playlist-name"
                                onClick={() => playPlaylist(pl.id)}
                                style={{ cursor: "pointer" }}
                                title="Reproducir playlist"
                            >
                                {pl.name.length > 28 ? pl.name.slice(0, 25) + "..." : pl.name}
                            </span>
                        </div>
                    ))}
                </div>
            </div>
        </>
    );
};

export default Sidebar;
