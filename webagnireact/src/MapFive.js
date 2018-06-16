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
import {
    Circle,
    FeatureGroup,
    LayerGroup,
    LayersControl,
    Rectangle,
} from 'react-leaflet';
import PropTypes from 'prop-types';

const { BaseLayer, Overlay } = LayersControl;

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

    nameByLevel = (level) => {
        if(level == 1)
            return 'Grau 1';
        else if(level == 2)
            return 'Grau 2';
        else if(level == 3)
            return 'Grau 3';
        else if(level == 4)
            return 'Grau 4';
        else
            return 'Grau 5';
    };

    listObjects = (level) => {
        var result = [];
        for(var i =0; i < this.state.object.length; i++) {
            var obj = this.state.object;

            if(obj[i].user_occurrence_level == level){
                result.push(
                    <Marker position={[obj[i].user_occurrence_lat, obj[i].user_occurrence_lon]}
                            icon={this.iconWithLevel(obj[i].user_occurrence_level)}>
                        <Popup>
                            {obj[i].user_occurrence_title} <br /> {obj[i].user_occurrence_data} <br />
                            Grau de urgencia: {obj[i].user_occurrence_level} <br/>
                            Tipo de problema: {obj[i].user_occurrence_type}
                        </Popup>
                    </Marker>
                )}
            }

        return result;
    };

    render() {
        const position = [this.state.lat, this.state.lng];
        const {object} = this.state;
        const center = [51.505, -0.09]
        const rectangle = [[51.49, -0.08], [51.5, -0.06]]

        return (
            <div>
                <EnhancedTableToolbar></EnhancedTableToolbar>
                <Map center={position} zoom={this.state.zoom}>
                    <LayersControl position="topright">
                        <BaseLayer checked name="Cores">
                            <TileLayer
                                attribution="&amp;copy <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
                                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                            />
                        </BaseLayer>
                        <BaseLayer name="Preto e branco">
                            <TileLayer
                                attribution="&amp;copy <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
                                url="https://tiles.wmflabs.org/bw-mapnik/{z}/{x}/{y}.png"
                            />
                        </BaseLayer>
                        {/*<Overlay name="Marker with popup">*/}
                            {/*<Marker position={center}>*/}
                                {/*<Popup>*/}
                                    {/*<span>*/}
                                      {/*A pretty CSS3 popup. <br /> Easily customizable.*/}
                                    {/*</span>*/}
                                {/*</Popup>*/}
                            {/*</Marker>*/}
                        {/*</Overlay>*/}
                        {/*<Overlay checked name="Layer group with circles">*/}
                            {/*<LayerGroup>*/}
                                {/*<Circle center={center} fillColor="blue" radius={200} />*/}
                                {/*<Circle*/}
                                    {/*center={center}*/}
                                    {/*fillColor="red"*/}
                                    {/*radius={100}*/}
                                    {/*stroke={false}*/}
                                {/*/>*/}
                                {/*<LayerGroup>*/}
                                    {/*<Circle*/}
                                        {/*center={[51.51, -0.08]}*/}
                                        {/*color="green"*/}
                                        {/*fillColor="green"*/}
                                        {/*radius={100}*/}
                                    {/*/>*/}
                                {/*</LayerGroup>*/}
                            {/*</LayerGroup>*/}
                        {/*</Overlay>*/}
                        {/*<Overlay name="Feature group">*/}
                            {/*<FeatureGroup color="purple">*/}
                                {/*<Popup>*/}
                                    {/*<span>Popup in FeatureGroup</span>*/}
                                {/*</Popup>*/}
                                {/*<Circle center={[51.51, -0.06]} radius={200} />*/}
                                {/*<Rectangle bounds={rectangle} />*/}
                            {/*</FeatureGroup>*/}
                        {/*</Overlay>*/}

                    {/*{object.map(n => {*/}
                        {/*return (*/}
                            {/*<Marker position={[n.user_occurrence_lat, n.user_occurrence_lon]}*/}
                                    {/*icon={this.iconWithLevel(n.user_occurrence_level)}*/}
                            {/*>*/}
                                {/*<Popup>*/}
                                    {/*{n.user_occurrence_title} <br /> {n.user_occurrence_data} <br />*/}
                                    {/*Grau de urgencia: {n.user_occurrence_level} <br/>*/}
                                    {/*Tipo de problema: {n.user_occurrence_type}*/}
                                {/*</Popup>*/}
                            {/*</Marker>*/}
                        {/*);})*/}
                    {/*}*/}

                        <Overlay checked name="Grau 1">
                            <LayerGroup>
                                {this.listObjects(1)}
                            </LayerGroup>
                        </Overlay>

                        <Overlay checked name="Grau 2">
                            <LayerGroup>
                                {this.listObjects(2)}
                            </LayerGroup>
                        </Overlay>

                        <Overlay checked name="Grau 3">
                            <LayerGroup>
                                {this.listObjects(3)}
                            </LayerGroup>
                        </Overlay>

                        <Overlay checked name="Grau 4">
                            <LayerGroup>
                                {this.listObjects(4)}
                            </LayerGroup>
                        </Overlay>

                        <Overlay checked name="Grau 5">
                            <LayerGroup>
                                {this.listObjects(5)}
                            </LayerGroup>
                        </Overlay>
                    </LayersControl>
                </Map>
            </div>
        )
    }
}
