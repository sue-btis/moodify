import React from "react";
import "./SwitchToggle.css";

const SwitchToggle = ({ isChecked, onToggle, label }) => {
    return (
        <label className="switch">
            <input type="checkbox" checked={isChecked} onChange={onToggle} />
            <span className="slider"></span>
            <span className="switch-label">{label}</span>
        </label>
    );
};

export default SwitchToggle;