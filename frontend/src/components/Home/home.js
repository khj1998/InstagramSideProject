import React from 'react';
import Navbar from '../../components/Navbar/Navbar.js';
import Content from '../../content/content.js';
import './home.scss'

const Home = () => {
    return (
        <div className="home">
            <Navbar />
            <Content />
        </div>
    );
};

export default Home;