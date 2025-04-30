import React,{useEffect,useState} from "react";
import "./Sidebar.css";
import axios from "axios";

const Sidebar = () => {
    const [playlists, setPlaylists] = useState([]);

    useEffect(() => {
        axios
            .get("/api/playlists", { withCredentials: true })
            .then((res) => {
                setPlaylists(res.data.items || []);
            })
            .catch((err) => console.error("❌ Error al cargar playlists", err));
    }, []);

    return (
        <div className="sidebar">
            <h2 className="sidebar__logo">Moodify</h2>

            <div className="sidebar__divider" />

                <div className="sidebar__playlists">
                    {playlists.map((pl) => (
                        <a key={pl.id} href={`/playlist/${pl.id}`} className="sidebar__playlist">
                            {pl.images && pl.images.length > 0 ? (
                                <img
                                    src={pl.images[0].url}
                                    alt={pl.name}
                                    className="sidebar__playlist-img"
                                />
                            ) : (
                                <div className="sidebar__playlist-placeholder">🎵</div>
                            )}
                            <span className="sidebar__playlist-name">
                                {pl.name.length > 28 ? pl.name.slice(0, 25) + "..." : pl.name}
                            </span>
                        </a>
                    ))}
                </div>
            </div>

    );
}

export default Sidebar
