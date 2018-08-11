import React, { Component } from 'react';
import { connect } from 'react-redux';

const getHide = notificationBar => notificationBar.text === null ? 'hide' : '';

class NotificationBar extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    const notificationBar = this.props.notificationBar;

    return (
      <label htmlFor='notify'>
        <input id='notify' type='checkbox' readOnly checked={!notificationBar.show} />
        <div id='notification-bar'>
          <div className={`container ${getHide(notificationBar)}`}>
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
