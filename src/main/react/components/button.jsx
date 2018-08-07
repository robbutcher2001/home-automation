import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import { doGet } from '../actions/loungeAction';

class Button extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div>
        <button onClick={event => this.props.fetchLoungeStatus()}>
          {this.props.buttonText}
        </button>
      </div>
    );
  }
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({ fetchLoungeStatus: doGet }, dispatch);
}

export default connect(null, mapDispatchToProps)(Button);
