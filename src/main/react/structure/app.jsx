import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Switch, Route } from 'react-router-dom';
import { ConnectedRouter } from 'react-router-redux';

import { DETERMINE_USER_LOCATION } from '../globals';

import { getShowSuccessNotificationAction } from '../globals/utils';

import NotificationBar from '../components/notification-bar';
import LandingPage from './landing-page';
import LoungePage from './lounge-page';
import LoginPage from './login-page';
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
        <ConnectedRouter history={this.props.history}>
          <Switch>
            <Route exact path='/' component={LandingPage} />
            <Route exact path='/lounge' component={LoungePage} />
            <Route exact path='/login' component={LoginPage} />
            <Route component={NoMatch} />
          </Switch>
        </ConnectedRouter>
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
