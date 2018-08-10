import React, { Component } from 'react';
import { connect } from 'react-redux';

import AnimationControl from './animation-control';

class NotificationBar extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    const notificationBar = this.props.notificationBar;

    console.log(notificationBar.text);
    if (!notificationBar.text) {
      return (<div />);
    }

    return (
      <label htmlFor='notify'>
        <input id='notify' type='checkbox' checked={!notificationBar.text} />
        <div id='notification-bar'>
          <div className='container'>
            <i className='fa fa-times-circle'></i>
            <i className='fa fa-exclamation'></i>
            <p>{notificationBar.text}</p>
          </div>
        </div>
      </label>
    );
  }
}

function mapStateToProps(state) {
  return {
    notificationBar: state.notificationBar
  };
}

export default connect(mapStateToProps)(NotificationBar);
