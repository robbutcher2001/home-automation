import React, { Component } from 'react';
import { connect } from 'react-redux';

import { getShowSuccessNotificationAction } from '../globals/utils';

class Button extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div>
        <button onClick={event => this.props.doNotification({ text: 'This is a test notification' })}>
          {this.props.buttonText}
        </button>
      </div>
    );
  }
}

const mapDispatchToProps = dispatch => {
  return {
    doNotification: payload => dispatch(getShowSuccessNotificationAction(payload))
  };
};


export default connect(null, mapDispatchToProps)(Button);
