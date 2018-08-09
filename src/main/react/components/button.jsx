import React, { Component } from 'react';
import { connect } from 'react-redux';

import { LOUNGE_STATUS_REQUEST } from '../globals';

class Button extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div>
        <button onClick={event => this.props.getLoungeStatus()}>
          {this.props.buttonText}
        </button>
      </div>
    );
  }
}

const mapDispatchToProps = dispatch => {
  let payload = {
    hello: 'rob'
  };
  return {
    getLoungeStatus: () => dispatch({ type: LOUNGE_STATUS_REQUEST, payload })
  };
};


export default connect(null, mapDispatchToProps)(Button);
