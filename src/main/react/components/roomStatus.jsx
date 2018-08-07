import React, { Component } from 'react';
import { connect } from 'react-redux';

class RoomStatus extends Component {
  render() {
    const statuses = this.props.lounge;

    if (!Object.keys(statuses).length) {
      return (
        <p>No data yet</p>
      )
    }

    return (
      <div id="main">
        <h3>{this.props.title}</h3>
        <p>Last occupied: {statuses.multisensor.last_occupied}</p>
        <p>Temperature: {statuses.multisensor.temperature}</p>
        <p>Brightness: {statuses.multisensor.luminiscence}</p>
        <p>Humidity: {statuses.multisensor.humidity}</p>
        <p>Patio door blind {statuses.blind1.percent_open}% open</p>
        <p>Main blind {statuses.blind2.percent_open}% open</p>
        <p>Bedroom mode: {statuses.bedroom_mode}</p>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    lounge: state.lounge
  };
}

export default connect(mapStateToProps)(RoomStatus);
