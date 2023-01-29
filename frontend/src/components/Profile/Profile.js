import React from "react";
import './Profile.css';
import AccountCircleOutlinedIcon from '@mui/icons-material/AccountCircleOutlined';
import LogoutIcon from '@mui/icons-material/Logout';
import {useNavigate} from 'react-router-dom';
import axios from 'axios';

const Profile = () => {

    const navigate = useNavigate();

    const onLogOut = async () => {
        await axios.get('http://localhost:3000/users/signout');
        navigate('/');
    }

    return (
        <div className="profile">
            <div className="top">
                <AccountCircleOutlinedIcon />
                <b className="profile-click">Profile</b>
            </div>
            <div className="bottom">
                <LogoutIcon />
                <b className="profile-click" onClick={onLogOut}>Log Out</b>
            </div>
        </div>
    )
};

export default Profile;