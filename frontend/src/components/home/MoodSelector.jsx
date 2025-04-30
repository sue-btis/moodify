import React from "react";
import "./MoodSelector.css";

const EMOTIONS = [
    { label: "Happy", value: "feliz", emoji: "😄" },
    { label: "Sad", value: "triste", emoji: "😢" },
    { label: "Energetic", value: "energetico", emoji: "⚡" },
    { label: "Calm", value: "calmado", emoji: "🌙" },
];

const MoodSelector = ({ onSelect, selectedEmotion }) => {
    return (
        <div className="emotion-selector">
            {EMOTIONS.map((emotion) => (
                <div
                    key={emotion.value}
                    className={`emotion-selector__card ${
                        selectedEmotion === emotion.value ? "emotion-selector__card--active" : ""
                    }`}
                    onClick={() => onSelect(emotion.value)}
                >
                    <span className="emotion-selector__emoji">{emotion.emoji}</span>
                    <span className="emotion-selector__label">{emotion.label}</span>
                </div>
            ))}
        </div>
    );
};

export default MoodSelector;
