import React from "react";
import "./GenreSelector.css";


const GenreSelector = ({onSelect, selectedGenre}) => {

    const genres = [
        "pop",
        "hip hop",
        "rap",
        "rock",
        "electronic",
        "reggaeton",
        "latin",
        "r&b",
        "indie",
        "k-pop",
        "trap",
        "dance",
        "metal",
        "alternative",
        "house",
        "techno",
        "country",
        "jazz",
        "classical",
        "soul"
    ];

    return (
        <div className="genre-selector">
            {genres.map((genre) => (
                <button
                    key={genre}
                    className={`genre-selector__button ${
                        selectedGenre === genre ? "genre-selector__button--active" : ""
                    }`}
                    onClick={() => onSelect(genre)}
                >
                    {genre}
                </button>
            ))}
        </div>
    );
};

export default GenreSelector;
