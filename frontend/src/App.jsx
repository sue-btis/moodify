import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext";
import './App.css'
import LoginPage from "./components/auth/LoginPage.jsx";
import HomePage from "./components/home/HomePage.jsx";
import { Toaster } from 'react-hot-toast';

const ProtectedRoute = ({ element }) => {
    const{ authenticated, loading } = useAuth();

    if (loading) {
        return <div style={{ color: "white", padding: "2rem" }}>Loading...</div>;
    }

    return authenticated ? element : <Navigate to="/" />
}
function App() {
    return (
        <AuthProvider>
            <Router>
                <>
                    <Routes>
                        <Route path="/" element={<LoginPage />} />
                        <Route path="/home" element={<ProtectedRoute element={<HomePage />} />} />
                    </Routes>
                    <Toaster position="bottom-right" reverseOrder={false} />
                </>
            </Router>
        </AuthProvider>
    );
}

export default App
