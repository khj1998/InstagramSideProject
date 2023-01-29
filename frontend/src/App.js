import React from 'react';
import {BrowserRouter as Router,Route,Routes} from 'react-router-dom';
import ChatRoom from './components/Chat/ChatRoom.js'
import Login from './components/Instagramlogin/login.js';
import Home from './components/Home/home.js';
import './App.css';

function App() {
  return (
        <Router>
            <Routes>
                <Route exact path="/home" className="App" element={<Home />} />
                <Route exact path="/" element={<Login />} />
                <Route exact path="/instagram/dm" element={<ChatRoom />} />
            </Routes>
        </Router>
  );
}

export default App;
