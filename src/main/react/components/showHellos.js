import React from 'react';

const ShowHellos = props => {
  const ServerHellos = props.hellos.map(hello => {
    return (
      <li key={hello}>{hello}</li>
    );
  });

  return (
    <ul>{ServerHellos}</ul>
  );
}

export default ShowHellos;
