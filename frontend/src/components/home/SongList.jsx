import React from "react";
import SongCard from "./SongCard";
import "./SongList.css";

const SongList = ({ songs }) => {

    return (
        <div className="song-list">
            {songs.map((s) => (
                <SongCard key={s.id} song={s}/>
            ))}
        </div>
    );
};

export default SongList;

