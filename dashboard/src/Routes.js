import React from "react";
import {BrowserRouter as Router, Route} from "react-router-dom";
import { Home } from "./views/Home.js";
import { Airline } from "./views/Airline.js"

export default function(props) {
    return(
        <Router>
            <Route exact path="/">
                <Home />
            </Route>

            <Route path="/airline">
                <Airline />
            </Route>

        </Router>
    );
}