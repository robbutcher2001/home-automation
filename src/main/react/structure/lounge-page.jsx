import React from 'react';

import Nav from '../components/nav';
import RoomDetails from '../components/room-details';
import Footer from '../components/footer';

const LoungePage = () => {
  return (
    <div id='wrapper'>
      <Nav />
      <div id='main'>
        <RoomDetails title='Lounge'/>
      </div>
      <Footer />
    </div>
  );
}

export default LoungePage;
