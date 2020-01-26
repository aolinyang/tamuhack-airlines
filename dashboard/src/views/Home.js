import React from "react";
import { ListGroup, ListGroupItem } from 'reactstrap';
import fullnames from "./../airlinenames.js";
import "./home.scss"

import { Jumbotron,
         Container } from "reactstrap";

class Home extends React.Component {
    constructor(props) {
        super(props);
    }

    getFlightData = () => {
        fetch("http://tamuflights.tech:5000/getallflights")
          .then(res => res.json())
          .then(
            (results) => {
                if (results != null) {
                    let flights = []
                    for (let result of results) {
                        let [airline,number] = result.split('-')
                        flights.push({
                            airline,
                            number
                        })
                    }
                    this.setState({flights: flights})
                }/* else {
                    this.setState({
                        flights: [
                            {airline: 'aa', number: 948},
                            {airline: 'ua', number: 548},
                            {airline: 'da', number: 302}
                        ]
                    })
                }*/

            },
            (err) => {
                console.log(err)
            }
        )
    }

    componentDidMount() {
        this.getFlightData();
        this.timer = setInterval(() => this.getFlightData(), 2000);
    }

    componentWillUnmount() {
        clearInterval(this.timer);
    }

    state = {
        flights: []
    }

    createFlights = () => {
        let flights = []
        for (let flight of this.state.flights) {
            flights.push(
                <ListGroupItem onClick={e => window.location.href=`/airline?flight=${flight.airline}-${flight.number}`}>
                    <p>Airline: {fullnames[flight.airline]}</p>
                    <p>Flight Number: {flight.number}</p>
                </ListGroupItem>
            );
        }
        return flights
    }

    render() {
        return(
            <Container>
                <div id="controlpanelhead">
                    <h1>Control Panel</h1>
                </div>
                <ListGroup>
                    {this.createFlights()}
                </ListGroup>
            </Container>
        );
    }
}

export { Home };