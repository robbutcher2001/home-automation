import React, { Component } from 'react';
import { connect } from 'react-redux';

import { REQUEST_POLLERS } from '../globals';
import { getPagePollers } from '../globals/utils';

class Button extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    const latitude = this.props.userGeolocation.latitude;
    const longitude = this.props.userGeolocation.longitude;
    const requestedPollers = getPagePollers(window.location.pathname);

    return (
      <button onClick={event => this.props.startPollers({
        requestedPollers,
        latitude,
        longitude
      })}>
        {this.props.buttonText}
      </button>
    );
  }
}

function mapStateToProps(state) {
  return {
    userGeolocation: state.userGeolocation
  };
}

const mapDispatchToProps = dispatch => {
  return {
    startPollers: payload => dispatch({ type: REQUEST_POLLERS, payload })
  };
};


export default connect(mapStateToProps, mapDispatchToProps)(Button);
