import React, { Component } from 'react';
import { connect } from 'react-redux';

import Button from '../components/button';

class RoomStatus extends Component {
  render() {
    const statuses = this.props.lounge;

    if (!Object.keys(statuses).length) {
      return (
        <article id="work" className="panel">
          <p>No data yet</p>
          <Button buttonText="Get lounge data" />
        </article>
      )
    }

    return (
      <article id="work" className="panel">
        <header>
          <h2>{this.props.title}</h2>
          <p>A sub title</p>
        </header>
        <p>
          Phasellus enim sapien, blandit ullamcorper elementum eu, condimentum eu elit.
          Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia
          luctus elit eget interdum.
        </p>
        <Button buttonText="Refresh lounge data" />
        <p>Last occupied: {statuses.multisensor.last_occupied}</p>
        <p>Temperature: {statuses.multisensor.temperature}</p>
        <p>Brightness: {statuses.multisensor.luminiscence}</p>
        <p>Humidity: {statuses.multisensor.humidity}</p>
        <p>Patio door blind {statuses.blind1.percent_open}% open</p>
        <p>Main blind {statuses.blind2.percent_open}% open</p>
        <p>Bedroom mode: {statuses.bedroom_mode}</p>
      </article>
    );
  }
}

function mapStateToProps(state) {
  return {
    lounge: state.lounge
  };
}

export default connect(mapStateToProps)(RoomStatus);
