import React from "react";
import { ListGroup, ListGroupItem } from 'reactstrap';


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
                } else {
                    this.setState({
                        flights: [
                            {airline: 'aa', number: 948},
                            {airline: 'ua', number: 548},
                            {airline: 'da', number: 302}
                        ]
                    })
                }

            },
            (err) => {
                console.log(err)
            }
        )
    }

    componentWillMount() {
        this.getFlightData()
    }

    state = {
        flights: [
            // dummy data
            {airline: 'aa', number: 948},
            {airline: 'ua', number: 548},
            {airline: 'da', number: 302}
        ]
    }

    createFlights = () => {
        let flights = []
        for (let flight of this.state.flights) {
          flights.push(<ListGroupItem>Airline: {flight.airline}; Number:{flight.number}</ListGroupItem>)
        }
        return flights
    }

    render() {
        return(
            <Container>
                <Jumbotron>
                    <h1>HOME</h1>
                </Jumbotron>

                <ListGroup>
                    {this.createFlights()}
                </ListGroup>
            </Container>
        );
    }
}

export { Home };