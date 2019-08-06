import React from 'react';

import Nav from '../components/nav';
import Button from '../components/buttonStartPollers';
import Footer from '../components/footer';

const LoungePage = () => {
  return (
    <div id='wrapper'>
      <Nav />
      <div id='main'>
        <Button buttonText='Start pollers'/>
      </div>
      <Footer />
    </div>
  );
}

export default LoungePage;
