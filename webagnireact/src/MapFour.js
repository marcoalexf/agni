import React, { Component } from 'react'
import { Map, TileLayer, Marker, Popup } from 'react-leaflet'
// import {Map, GoogleApiWrapper} from 'google-maps-react';
import L from 'leaflet';
import {withStyles} from "material-ui/styles/index";
import Tooltip from 'material-ui/Tooltip';
import Typography from 'material-ui/Typography';
import IconButton from 'material-ui/IconButton';
import UpdateListIcon from '@material-ui/icons/Cached';
import Toolbar from 'material-ui/Toolbar';
import PropTypes from 'prop-types';

const styles = theme => ({
    spacer: {
        flex: '1 1 100%',
    },
    toolbarroot: {
        paddingRight: theme.spacing.unit,
    },
    toolbartitle: {
        flex: '0 0 auto',
    },
});

let EnhancedTableToolbar = props => {
    const { classes } = props;

    return (
        <Toolbar
            className={classes.toolbarroot}
        >
            <div className={classes.toolbartitle}>
                {(
                    <Typography variant="title" id="tableTitle">
                        Mapa de Ocorrências
                    </Typography>
                )}
            </div>
            <div className={classes.spacer} />
            <div className={classes.actions}>
                {(
                    <Tooltip title="Atualizar mapa">
                        <IconButton aria-label="Atualizar mapa">
                            <UpdateListIcon />
                        </IconButton>
                    </Tooltip>
                )}
            </div>
        </Toolbar>
    );
};

EnhancedTableToolbar.propTypes = {
    classes: PropTypes.object.isRequired,
};

EnhancedTableToolbar = withStyles(styles)(EnhancedTableToolbar);

var greenIcon = L.icon({
    iconUrl: require('./img/marker-green.png'),
    // shadowUrl: require('./img/leaf-shadow.png'),
    //
    // iconSize:     [38, 95], // size of the icon
    // shadowSize:   [50, 64], // size of the shadow
    iconAnchor:   [22, 94], // point of the icon which will correspond to marker's location
    // shadowAnchor: [4, 62],  // the same for the shadow
    popupAnchor:  [-6, -90] // point from which the popup should open relative to the iconAnchor
});

var redIcon = L.icon({
    iconUrl: require('./img/marker-red.png'),
    iconAnchor:   [22, 94], // point of the icon which will correspond to marker's location
    // shadowAnchor: [4, 62],  // the same for the shadow
    popupAnchor:  [-6, -90] // point from which the popup should open relative to the iconAnchor
});

var yellowIcon = L.icon({
    iconUrl: require('./img/marker-yellow.png'),
    iconAnchor:   [22, 94], // point of the icon which will correspond to marker's location
    // shadowAnchor: [4, 62],  // the same for the shadow
    popupAnchor:  [-6, -90] // point from which the popup should open relative to the iconAnchor
});

var orangeIcon = L.icon({
    iconUrl: require('./img/marker-orange.png'),
    iconAnchor:   [22, 94], // point of the icon which will correspond to marker's location
    // shadowAnchor: [4, 62],  // the same for the shadow
    popupAnchor:  [-6, -90] // point from which the popup should open relative to the iconAnchor
});

var ligthGreenIcon = L.icon({
    iconUrl: require('./img/marker-ligthgreen.png'),
    iconAnchor:   [22, 94], // point of the icon which will correspond to marker's location
    // shadowAnchor: [4, 62],  // the same for the shadow
    popupAnchor:  [-6, -90] // point from which the popup should open relative to the iconAnchor
});

let xmlRequest = new Promise(function(resolve, reject) {
    console.log("xmlRequest");
    var map;
    console.log("pedido");
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", "https://custom-tine-204615.appspot.com/rest/occurrence/list", true);
    xmlHttp.send();

    console.log("esperar pelo estado");
    xmlHttp.onreadystatechange = function () {
        console.log("1");
        if (xmlHttp.readyState === 4) {
            console.log("2");
            if (xmlHttp.status === 200) {
                console.log("3");
                var response = xmlHttp.response;
                var obj = JSON.parse(response);
                console.log("obj:");
                console.log(obj);
                map = obj[0];
                console.log("map:");
                console.log(obj.mapList);
                resolve(obj.mapList);
            }
            else {
                console.log("tempo expirado");
            }
        }
    }

});

export default class SimpleExample extends Component {
    state = {
        lat: 38.66,
        lng: -9.20,
        zoom: 10,
        object: [
            {user_occurrence_title: '',
                user_occurrence_lat: 38.66,
                user_occurrence_lon: -9.20}],
    };

    componentDidMount() {
        xmlRequest.then((value) =>{
                this.setState({object: value});
                console.log("state object");
                console.log(this.state.object);
            }
        );
    }

    iconWithLevel = (level) => {
        if(level == 1)
            return greenIcon;
        else if(level == 2)
            return ligthGreenIcon;
        else if(level == 3)
            return yellowIcon;
        else if(level == 4)
            return orangeIcon;
        else
            return redIcon;
    };

    render() {
        const position = [this.state.lat, this.state.lng];
        const {object} = this.state;

        return (
            <div>
            <EnhancedTableToolbar></EnhancedTableToolbar>
            <Map center={position} zoom={this.state.zoom}>
                <TileLayer
                    attribution="&amp;copy <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                {/*<Marker position={position}>*/}
                    {/*<Popup>*/}
                        {/*A pretty CSS3 popup. <br /> Easily customizable.*/}
                    {/*</Popup>*/}
                {/*</Marker>*/}

                {object.map(n => {
                    return (
                        <Marker position={[n.user_occurrence_lat, n.user_occurrence_lon]}
                                icon={this.iconWithLevel(n.user_occurrence_level)}
                        >
                            <Popup>
                                {n.user_occurrence_title} <br /> {n.user_occurrence_data} <br />
                                Grau de urgencia: {n.user_occurrence_level} <br/>
                                Tipo de problema: {n.user_occurrence_type}
                            </Popup>
                        </Marker>

                    );})
                }
            </Map>
            </div>
        )
    }
}
