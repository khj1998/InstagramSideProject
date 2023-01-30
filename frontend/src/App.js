import React from 'react';
import {BrowserRouter as Router,Route,Routes} from 'react-router-dom';
import ChatRoom from './components/Chat/ChatRoom.js'
import Login from './components/Instagramlogin/login.js';
import Home from './components/Home/home.js';
import Signup from './components/Signup/signup.js';
import './App.scss';

function App() {
  return (
        <Router>
            <Routes>
                <Route exact path="/" element={<Login />} />
                <Route exact path="/signup" element = {<Signup />} />
                <Route exact path="/home" className="App" element={<Home />} />
                <Route exact path="/instagram/dm" element={<ChatRoom />} />
            </Routes>
        </Router>
  );
}

export default App;
