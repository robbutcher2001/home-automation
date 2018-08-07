import React from 'react';

import Title from '../components/title';
import Button from '../components/button';
import RoomStatus from '../components/roomStatus';

const Homepage = () => {
  return (
    <div id="wrapper">
      <Title text="Rob's Apartment" />
      <Button buttonText="Get lounge data" />
      <RoomStatus title="Lounge Info"/>
    </div>
  );
}

export default Homepage;
