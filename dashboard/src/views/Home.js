import React from "react";


import { Jumbotron,
         Container } from "reactstrap";

class Home extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return(
            <Container>
                <Jumbotron>
                    <h1>HOME</h1>
                </Jumbotron>
            </Container>
        );
    }
}

export { Home };