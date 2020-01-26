import React from "react";
import {BrowserRouter as Router, Route} from "react-router-dom";
import { Home } from "./views/Home.js";

export default function(props) {
    return(
        <Router>
            <Route path="/">
                <Home />
            </Route>
        </Router>
    );
}