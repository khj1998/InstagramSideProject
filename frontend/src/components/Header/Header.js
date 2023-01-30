import react from 'react';
import './Header.scss';
import data from '../../data.js'

const Header = () => {
    return(
        <div className="header">
            {data.map((item) => {
                <div className="stories" key={item.id}>
                    <img src={data.profile} alt={item.id} />
                    <span>{item.name}</span>
                </div>
            })}
        </div>
    )
}

export default Header;