import React from 'react';
import { Link } from 'react-router-dom';

const Nav = () => {
  return (
    <nav id="nav">
      <Link to="/" className="icon fa-home active"><span>Home</span></Link>
      <Link to="/lounge" className="icon fa-folder"><span>Work</span></Link>
      <Link to="/bedrooms" className="icon fa-envelope"><span>Contact</span></Link>
      <Link to="/other" className="icon fa-pencil"><span>Twitter</span></Link>
    </nav>
  );
}

export default Nav;
