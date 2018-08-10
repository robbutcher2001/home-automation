import React from 'react';

import NotificationBar from '../components/notification-bar';
import Nav from '../components/nav';
import RoomDetails from '../components/room-details';
import Footer from '../components/footer';

const LandingPage = () => {
  return (
    <div>
      <NotificationBar />
      <div id='wrapper'>
        <Nav />
        <div id='main'>
          <RoomDetails title='Lounge'/>
        </div>
        <Footer />
      </div>
    </div>
  );
}

export default LandingPage;
