import react from 'react';
import './content.css';
import Header from '../components/Header/Header.js';
import Posts from '../components/Posts/Posts.js';
import Sidebar from '../components/Sidebar/Sidebar.js';

const Content = () => {
    return (
        <div className="content">
            <div className="left">
                <Header />
                <Posts />
            </div>
            <div className="right">
                <Sidebar />
            </div>
        </div>
    );
};

export default Content;