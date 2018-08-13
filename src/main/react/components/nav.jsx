import React from 'react';
import { Link } from 'react-router-dom';

import {
  LANDING_PAGE_PATH,
  SECOND_PAGE_PATH,
  THIRD_PAGE_PATH,
  FOURTH_PAGE_PATH
} from '../globals';

const activeTab = tab => window.location.pathname === tab ? 'active' : '';

const Nav = () => {
  return (
    <nav id='nav'>
      <Link to={LANDING_PAGE_PATH} className={`icon fa-home ${activeTab(LANDING_PAGE_PATH)}`}><span>Home</span></Link>
      <Link to={SECOND_PAGE_PATH} className={`icon fa-folder ${activeTab(SECOND_PAGE_PATH)}`}><span>Work</span></Link>
      <Link to={THIRD_PAGE_PATH} className={`icon fa-envelope ${activeTab(THIRD_PAGE_PATH)}`}><span>Contact</span></Link>
      <Link to={FOURTH_PAGE_PATH} className={`icon fa-pencil ${activeTab(FOURTH_PAGE_PATH)}`}><span>Twitter</span></Link>
    </nav>
  );
}

export default Nav;
