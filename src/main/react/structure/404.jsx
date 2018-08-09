import React from 'react';

import Nav from '../components/nav';
import Footer from '../components/footer';

const NoMatch = () => {
  return (
    <div id='wrapper'>
      <Nav />
      <div id='main'>
        <h1>Page doesn't exist yet!</h1>
      </div>
      <Footer />
    </div>
  );
}

export default NoMatch;
