import React, { Component } from 'react';
import { connect } from 'react-redux';
import { BrowserRouter, Switch, Route } from 'react-router-dom';

import { DETERMINE_USER_LOCATION } from '../globals';

import { getShowSuccessNotificationAction } from '../globals/utils';

import NotificationBar from '../components/notification-bar';
import LandingPage from './landing-page';
import NoMatch from './404';

class App extends Component {
  constructor(props) {
    super(props);
  }

  componentDidMount() {
    this.props.notifyUserGeolocation({
      text: 'Determining your location..',
      persist: true
    });
    this.props.determineUserGeolocation();
  }

  render() {
    return (
      <div>
        <NotificationBar />
        <BrowserRouter>
          <Switch>
            <Route exact path='/' component={LandingPage} />
            <Route component={NoMatch} />
          </Switch>
        </BrowserRouter>
      </div>
    );
  }
}

const mapDispatchToProps = dispatch => {
  return {
    notifyUserGeolocation: payload => dispatch(getShowSuccessNotificationAction(payload)),
    determineUserGeolocation: () => dispatch({ type: DETERMINE_USER_LOCATION })
  };
};

export default connect(null, mapDispatchToProps)(App);
