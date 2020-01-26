import React from "react";
import { Container, ListGroup, ListGroupItem } from 'reactstrap';

class Airline extends React.Component {
    constructor(props) {
        super(props);
        this.state = {}
    }

    getLuggageData = (query) => {
        fetch("http://tamuflights.tech:5000/getsingleflight" + query)
          .then(res => res.json())
          .then(
            (luggages) => {
                this.setState({luggages})
                return luggages
            },
            (err) => {
                console.log(err)
            }
        )
    }

    componentWillMount() {
        this.getLuggageData(window.location.search)
    }

    createluggages = (luggages) => {
        let luggageList = []
        for (let luggage in luggages) {
            luggageList.push(<ListGroupItem>Device's Name: {luggage}; Status:{luggages[luggage]}</ListGroupItem>)
        }
        return luggageList
    }

    render() {
        return(
            <Container>
                <h1>Airline</h1>

                <ListGroup>
                    {this.createluggages(this.state.luggages)}
                </ListGroup>
            </Container>
        );
    }
}

export { Airline };