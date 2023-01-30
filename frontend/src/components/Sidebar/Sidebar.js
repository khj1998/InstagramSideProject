import React from 'react';
import './Sidebar.scss'
import data from '../../data.js';

const Sidebar = () => {
    return (
        <div className="sidebar">
            <h2>Suggestions For You!</h2>
            {data.map((item) => (
                <div className="sidabarProfile">
                    <div className="img">
                        <img src={item.profile} alt={item.id} />
                        <div className="name">
                            <h3>{item.name}</h3>
                            <span>follows You</span>
                        </div>
                    </div>
                    <button>Follow</button>
                </div>
            ))}
        </div>
    )
}

export default Sidebar;