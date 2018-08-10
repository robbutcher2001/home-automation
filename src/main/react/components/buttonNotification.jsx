import React, { Component } from 'react';
import { connect } from 'react-redux';

import { NOTIFICATION_REQUEST } from '../globals';

class Button extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div>
        <button onClick={event => this.props.doNotification()}>
          {this.props.buttonText}
        </button>
      </div>
    );
  }
}

const mapDispatchToProps = dispatch => {
  return {
    doNotification: () => dispatch({ type: NOTIFICATION_REQUEST })
  };
};


export default connect(null, mapDispatchToProps)(Button);
