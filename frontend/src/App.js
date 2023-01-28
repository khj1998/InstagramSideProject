import React from 'react';
import {BrowserRouter as Router,Route,Routes} from 'react-router-dom';
import './components/Chat/ChatRoom.css';
import './components/Instagramlogin/login.css'
import ChatRoom from './components/Chat/ChatRoom.js'
import Login from './components/Instagramlogin/login.js';

function App() {
  return (
        <Router>
            <Routes>
                <Route exact path="/" element={<Login />} />
                <Route exact path="/instagram/dm" element={<ChatRoom />} />
            </Routes>
        </Router>
  );
}

export default App;
