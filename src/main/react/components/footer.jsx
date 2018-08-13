import React, { Component } from 'react';
import { connect } from 'react-redux';

class Footer extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    const location = this.props.userGeolocation;

    if (!Object.keys(location).length) {
      return (
        <div id='footer'>
          <ul className='copyright'>
            <li>&copy; Rob Butcher 2018.</li>
          </ul>
        </div>
      );
    }

    return (
      <div id='footer'>
        <ul className='copyright'>
          <li>&copy; Rob Butcher 2018.</li>
          <li>Using <a href={`https://www.google.co.uk/maps/place/${location.latitude},${location.longitude}`} target='_blank'>this</a> as your location.</li>
        </ul>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    userGeolocation: state.userGeolocation
  };
}

export default connect(mapStateToProps)(Footer);
