import React, { Component } from 'react';
import { connect } from 'react-redux';
import { BrowserRouter, Switch, Route } from 'react-router-dom';

import { DETERMINE_USER_LOCATION } from '../globals';

import LandingPage from './landing-page';
import NoMatch from './404';

class App extends Component {
  constructor(props) {
    super(props);
  }

  componentDidMount() {
    this.props.determineUserGeolocation();
  }

  render() {
    return (
      <BrowserRouter>
        <Switch>
          <Route exact path='/' component={LandingPage} />
          <Route component={NoMatch} />
        </Switch>
      </BrowserRouter>
    );
  }
}

const mapDispatchToProps = dispatch => {
  return {
    determineUserGeolocation: () => dispatch({ type: DETERMINE_USER_LOCATION })
  };
};

export default connect(null, mapDispatchToProps)(App);
