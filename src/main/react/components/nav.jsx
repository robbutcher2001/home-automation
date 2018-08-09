import React from 'react';
import { Link } from 'react-router-dom';

const LINK_ONE = '/';
const LINK_TWO = '/lounge';
const LINK_THREE = '/bedrooms';
const LINK_FOUR = '/other';

const activeTab = tab => window.location.pathname === tab ? 'active' : '';

const Nav = () => {
  return (
    <nav id='nav'>
      <Link to={LINK_ONE} className={`icon fa-home ${activeTab(LINK_ONE)}`}><span>Home</span></Link>
      <Link to={LINK_TWO} className={`icon fa-folder ${activeTab(LINK_TWO)}`}><span>Work</span></Link>
      <Link to={LINK_THREE} className={`icon fa-envelope ${activeTab(LINK_THREE)}`}><span>Contact</span></Link>
      <Link to={LINK_FOUR} className={`icon fa-pencil ${activeTab(LINK_FOUR)}`}><span>Twitter</span></Link>
    </nav>
  );
}

export default Nav;
