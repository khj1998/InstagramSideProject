import React from "react";
import './Profile.css';
import AccountCircleOutlinedIcon from '@mui/icons-material/AccountCircleOutlined';

const Profile = () => {
    return (
        <div className="profile">
            <div className="top">
                <AccountCircleOutlinedIcon />
                <span>Profile</span>
            </div>
            <div className="bottom">
                <span>Log Out</span>
            </div>
        </div>
    )
};

export default Profile;