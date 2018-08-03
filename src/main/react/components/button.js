import React, { Component } from 'react';

class Button extends Component {
  constructor(props) {
    super(props);
  }

  handleClick() {
    console.log('Asking server to say hello');
    this.props.askServerToSayHello();
  }

  render() {
    return (
      <div>
        <button onClick={event => this.handleClick()}>
          {this.props.buttonText}
        </button>
      </div>
    );
  }
}

export default Button;
