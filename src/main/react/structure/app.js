import React, { Component } from 'react';
import { BrowserRouter, Switch, Route } from 'react-router-dom';

import LandingPage from './landing-page';
import NoMatch from './404';

export default class App extends Component {
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
