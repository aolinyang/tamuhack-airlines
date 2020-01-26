import React from "react";
import { Container, ListGroup, ListGroupItem } from 'reactstrap';
import "./home.scss";
import "./airline.scss";
import fullnames from "./../airlinenames.js";
const queryString = require('query-string');

class Airline extends React.Component {
    constructor(props) {
        super(props);
        const flight = queryString.parse(window.location.search).flight.split('-')[0];
        this.state = {
           flight: flight
        }
    }

    getLuggageData = (query) => {
        fetch("http://tamuflights.tech:5000/getsingleflight" + query)
          .then(res => res.json())
          .then(
            (luggages) => {
                console.log(luggages);
                this.setState({luggages});
                return luggages;
            },
            (err) => {
                console.log(err);
            }
        )
    }

    componentDidMount() {
        this.getLuggageData(window.location.search);
        this.timer = setInterval(() => this.getLuggageData(window.location.search), 2000);
    }

    componentWillUnmount() {
        clearInterval(this.timer);
    }

    createluggages = (luggages) => {
        let luggageList = [];
        for (let luggage in luggages) {
            luggageList.push(<ListGroupItem>Device's Name: {luggage}; Status:{luggages[luggage]}</ListGroupItem>);
        }
        return luggageList;
    }

    render() {
        return(
            <Container>
                <div className="largeheading">
                    <h1>{fullnames[this.state.flight]}</h1>
                </div>

                <ListGroup className="listx">
                    {this.createluggages(this.state.luggages)}
                </ListGroup>
            </Container>
        );
    }
}

export { Airline };