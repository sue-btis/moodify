import { createContext, useContext, useEffect, useState } from "react";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [authenticated, setAuthenticated] = useState(false);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch("http://localhost:8080/user/token", {
            method: "GET",
            credentials: "include", // 👈 Importante: enviar cookies de sesión
        })
            .then(res => {
                setAuthenticated(res.ok);
                setLoading(false);
            })
            .catch(() => {
                setAuthenticated(false);
                setLoading(false);
            });
    }, []);

    return (
        <AuthContext.Provider value={{ authenticated, loading }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
