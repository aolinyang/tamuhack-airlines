import React from "react";
import { Container, ListGroup, ListGroupItem } from 'reactstrap';

class Airline extends React.Component {
    constructor(props) {
        super(props);
    }

    // TODO: change the url and fetch the data
    getLuggageData = () => {
        fetch("http://tamuflights.tech:5000/getallflights")
          .then(res => res.json())
          .then(
            (results) => {
                console.log(results)
            },
            (err) => {
                console.log(err)
            }
        )
    }

    componentDidMount() {
        // this.getFlightData()
    }

    state = {
        luggages: [
            {name: "Bob's device", status: "onboard"},
            {name: "deviceNull", status: "arrived"}
        ]
    }

    createluggages = () => {
        let luggages = []
        for (let luggage of this.state.luggages) {
          luggages.push(<ListGroupItem>Device's Name: {luggage.name}; Status:{luggage.status}</ListGroupItem>)
        }
        return luggages
    }

    render() {
        return(
            <Container>
                <h1>Airline</h1>

                <ListGroup>
                    {this.createluggages()}
                </ListGroup>
            </Container>
        );
    }
}

export { Airline };