import React, {useState,useEffect} from 'react';
import './Navbar.css';
import Logo from '../../images/logo.png';
import ProfileImg from '../../images/profile.jpg';
import ProfileInfo from '../ProfileInfo/ProfileInfo.js';
import HomeOutlinedIcon from "@mui/icons-material/HomeOutlined";
import MapsUgcOutlinedIcon from "@mui/icons-material/MapsUgcOutlined";
import AddBoxIcon from "@mui/icons-material/AddBox";
import ExploreIcon from "@mui/icons-material/Explore";
import FavoriteBorderIcon from "@mui/icons-material/FavoriteBorder";
import Profile from '../Profile/Profile.js';

const Navbar = () => {

    const [open,setOpen] = useState(false);

    return (
        <div className="navbar">
        <div className="left">
            <img src={Logo} alt="Logo" />
        </div>

        <div className="center">
            <input type="text" placeholder="Search" />
        </div>

        <div className="right">

            <div className="icon">
                <HomeOutlinedIcon />
                <MapsUgcOutlinedIcon />
                <AddBoxIcon />
                <ExploreIcon />
                <FavoriteBorderIcon />
            </div>
            <img src={ProfileImg} alt="profile"
                            onClick={() => setOpen(!open)}
                        />
        </div>
        {open && <Profile />}
        </div>
    );
};

export default Navbar;