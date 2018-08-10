import React, { Component } from 'react';
import { connect } from 'react-redux';

class AnimationControl extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    if (this.props.show) {
      return (
        <input id='notify' type='checkbox' />
      );
    }

    return (
      <input id='notify' type='checkbox' checked />
    );
  }
}

function mapStateToProps(state) {
  return {
    show: state.notificationBar.show
  };
}

export default connect(mapStateToProps)(AnimationControl);
