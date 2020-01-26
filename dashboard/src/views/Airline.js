import React from "react";
import { Container, ListGroup, ListGroupItem } from 'reactstrap';

class Airline extends React.Component {
    constructor(props) {
        super(props);
    }

    // TODO: change the url and fetch the data
    getLuggageData = (query) => {
        fetch("http://tamuflights.tech:5000/getsingleflight"+query)
          .then(res => res.json())
          .then(
            (results) => {
                this.setState({luggages: results})
            },
            (err) => {
                console.log(err)
            }
        )
    }

    componentDidMount() {
        this.getLuggageData(window.location.search)
    }

    state = {
    }

    createluggages = () => {
        let luggages = {
            "will46_box": "onboard",
            "jason's luggage": "onboard",
            "allen's bag": "onboard",
            "Allen's 2nd bag": "awaiting boarding"
        }
        for (let luggage in luggages) {
          luggages.push(<ListGroupItem>Device's Name: {luggage.name}; Status:{luggage.status}</ListGroupItem>)
        }
        return luggages
    }

    render() {
        console.log("URL",window.location.search)
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