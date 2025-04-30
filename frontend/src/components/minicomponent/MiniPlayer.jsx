import React, { useState, useEffect, useRef } from "react";
import {
    FaPlay,
    FaPause,
    FaStepForward,
    FaStepBackward
} from "react-icons/fa";
import "./MiniPlayer.css";

const MiniPlayer = () => {
    const [track, setTrack] = useState(null);
    const [isPlaying, setIsPlaying] = useState(false);
    const [progressMs, setProgressMs] = useState(0);
    const [durationMs, setDurationMs] = useState(0);
    const intervalRef = useRef(null);

    const fetchCurrent = async () => {
        try {
            const res = await fetch("http://localhost:8080/spotify/current", {
                credentials: "include",
            });
            if (!res.ok) return;
            const data = await res.json();
            setTrack(data.item);
            setIsPlaying(data.is_playing);
            setProgressMs(data.progress_ms);
            setDurationMs(data.item?.duration_ms);
        } catch (e) {
            console.error("Error al obtener reproducción actual", e);
        }
    };

    const togglePlayback = async () => {
        try {
            await fetch(`http://localhost:8080/spotify/${isPlaying ? "pause" : "resume"}`, {
                method: "POST",
                credentials: "include",
            });
            setIsPlaying(!isPlaying);
        } catch (e) {
            console.error("Error al cambiar estado de reproducción", e);
        }
    };

    const skipNext = async () => {
        await fetch("http://localhost:8080/spotify/next", {
            method: "POST",
            credentials: "include",
        });
        fetchCurrent();
    };

    const skipPrevious = async () => {
        await fetch("http://localhost:8080/spotify/previous", {
            method: "POST",
            credentials: "include",
        });
        fetchCurrent();
    };

    const formatTime = (ms) => {
        const minutes = Math.floor(ms / 60000);
        const seconds = Math.floor((ms % 60000) / 1000);
        return `${minutes}:${seconds < 10 ? "0" : ""}${seconds}`;
    };

    useEffect(() => {
        fetchCurrent();
        intervalRef.current = setInterval(() => {
            setProgressMs(prev => (isPlaying ? prev + 1000 : prev));
        }, 1000);
        return () => clearInterval(intervalRef.current);
    }, [isPlaying]);

    if (!track) return null;

    const progressPercent = (progressMs / durationMs) * 100;

    return (
        <div className="mini-player">
            <img src={track.album.images[0].url} alt="cover" className="mini-player__cover" />
            <div className="mini-player__info">
                <div className="mini-player__meta">
                    <strong className="mini-player__title">{track.name}</strong>
                    <small className="mini-player__artist">
                        {track.artists.map((a) => a.name).join(", ")}
                    </small>
                </div>
                <div className="mini-player__progress-bar">
                    <span>{formatTime(progressMs)}</span>
                    <div className="bar-track">
                        <div className="bar-fill" style={{ width: `${progressPercent}%` }} />
                    </div>
                    <span>{formatTime(durationMs)}</span>
                </div>
            </div>
            <div className="mini-player__controls">
                <button className="mini-player__btn" onClick={skipPrevious}><FaStepBackward /></button>
                <button className="mini-player__btn" onClick={togglePlayback}>
                    {isPlaying ? <FaPause /> : <FaPlay />}
                </button>
                <button className="mini-player__btn" onClick={skipNext}><FaStepForward /></button>
            </div>
        </div>
    );
};

export default MiniPlayer;
