import React, { Component } from 'react';

import Title from '../components/title';
import Button from '../components/button';
import ShowHellos from '../components/showHellos';

const FETCH_URL = "http://localhost:3000/say-hello";

export default class Homepage extends Component {
  constructor(props) {
    super(props);

    this.state = {
      serverHellos: []
    };

    this.makeServerRequest = this.makeServerRequest.bind(this);
  }

  makeServerRequest() {
    fetch(FETCH_URL)
      .then(response => response.json())
      .then(json => {
        console.log(`Server said: ${json.status}`);
        this.setState({
          serverHellos: [...this.state.serverHellos, json.newHello ]
        });
      })
      .catch(err => console.error(err));
  }

  render() {
    return (
      <div id="main">
        <Title text="Hello World" />
        <Button
          buttonText="Say hello to the server"
          askServerToSayHello={this.makeServerRequest}
        />
        <ShowHellos hellos={this.state.serverHellos} />
      </div>
    )
  }
}
