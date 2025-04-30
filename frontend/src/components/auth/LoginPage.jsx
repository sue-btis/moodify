import React from "react";
import "./loginPage.css";

const LoginPage = () => {
    const HandleLogin = () => {
        window.location.href = "http://localhost:8080/oauth2/authorization/spotify";
    }

    return (
        <div className="login">
            <h1 className="login__title">Moodify</h1>
            <p className="login__subtitle">
                Listen music based on your mood
            </p>
            <button className="login__button liquid" onClick={HandleLogin}>
                Login with Spotify
            </button>
        </div>
    );
}

export default LoginPage;