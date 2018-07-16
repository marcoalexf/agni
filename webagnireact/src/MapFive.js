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
import GpsIcon from '@material-ui/icons/LocationSearching';
import CircularProgress from '@material-ui/core/CircularProgress';
import {noAccess1, noAccess2, noAccess3, noAccess4, noAccess5} from './mapIcons';
import {trash1, trash2, trash3, trash4, trash5} from "./mapIcons";
import {greenIcon, redIcon, yellowIcon, ligthGreenIcon, orangeIcon} from "./mapIcons";
import {cuttree1, cuttree2, cuttree3, cuttree4, cuttree5} from "./mapIcons";
// import {MapContainer} from "./MapTwo";
//import {MapContainer} from "leaflet";
// import {Legend} from 'leaflet';
import AddressIcon from '@material-ui/icons/Navigation';
import {geocodeByAddress, getLatLng} from "react-places-autocomplete";

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
    opName:{
        fontFamily: 'Montserrat',
        // fontSize: 48,
    },
});

function getUserAddress() {
    return new Promise( resolve => {
        console.log("informations1");
        var obj;
        var token = window.localStorage.getItem('token');
        var d = new Date();
        var t = d.getTime();

        if(token != null){
            var uname = JSON.parse(token).username;
            var tokenObj = JSON.parse(token);

            var user = {
                "username": uname,
                "token": tokenObj
            };

            var xmlHttp = new XMLHttpRequest();
            xmlHttp.open( "POST", "https://custom-tine-204615.appspot.com/rest/profile/", true);
            xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            var myJSON = JSON.stringify(user);
            xmlHttp.send(myJSON);

            xmlHttp.onreadystatechange = function() {
                if (xmlHttp.readyState === XMLHttpRequest.DONE){
                    if(xmlHttp.status === 200){
                        console.log("informations2");
                        var response = xmlHttp.response;
                        console.log("XML response: " + response);
                        obj = JSON.parse(response);
                        console.log(obj);

                        console.log(uname.charAt(0));

                        resolve(obj.user_locality);
                    }

                    else{
                        console.log("erro");
                        var expirationData = JSON.parse(token).expirationData;
                        console.log(token);
                        console.log("tempo atual: " + t);
                        console.log("data de expiracao: " + expirationData);
                        if(expirationData <= t){
                            console.log("tempo expirado");
                            window.localStorage.removeItem('token');
                            document.getElementById("tologin").click();
                        }
                    }
                }
            }.bind(this)
        }
        else{
            console.log("Sem sessao iniciada");
            // document.getElementById("tologin").click();

        }
    });
}

function getUserAddressLatLng(a){
    return new Promise(resolve =>{
        geocodeByAddress(a).then(results => getLatLng(results[0]))
        //.then(latLng => console.log("Success", latLng))
            .then(latLng => resolve(latLng))
            .catch(error => console.error('Error', error));
    })
}

function xmlRequest (){
    return new Promise(resolve => {
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
}

export default class SimpleExample extends Component {
    state = {
        lat: 38.66,
        lng: -9.20,
        zoom: 10,
        object: [
            {user_occurrence_title: '',
                user_occurrence_lat: 38.66,
                user_occurrence_lon: -9.20}],
        loading: true,
        level1: true,
        level2: true,
        level3: true,
        level4: true,
        level5: true,
        loading: true,
    };

    async componentDidMount() {
        let o = await xmlRequest();
        this.setState({object: o});
        console.log("map occurrences:");
        console.log(this.state.object);

        var token = window.localStorage.getItem('token');

        if(token != null) {
            let a = await getUserAddress();
            if (a != null) {
                this.setState({userLocality: a});
                let b = await getUserAddressLatLng(a);
                this.setState({
                    userLatLng: {
                        lat: b.lat,
                        lng: b.lng
                    }
                });
                console.log(b);
                console.log(this.state.userLatLng);
                console.log(this.state.userLocality);

                this.getLocation();
            }

            else {
                this.getLocation();
            }
        }
    }

    getLocation = () => {
        var token = window.localStorage.getItem('token');
        this.setState({loading: true});
        console.log(this.state.loading);

        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition((position) => {
                this.setState({
                            lat: position.coords.latitude,
                            lng: position.coords.longitude
                    }
                );
                // this.setState(() =>
                // {
                //     this.timer = setTimeout(() => {
                //         this.setState({
                //             loading: false,
                //         });
                //     }, 2000);
                // });
            });
            this.setState({loading: false});
            console.log("GPS");
        }
        else if(token != null && this.state.userLocality != '') {
            //browser doesn't support geolocation, set as vancouver
            //localidade do user
            this.setState({
                        lat: this.state.userLatLng.lat,
                        lng: this.state.userLatLng.lng

                }
            );
            this.setState({loading: false});
            console.log("browser does not support GPS");
        }
        else{
            //capital - Lisboa
            this.setState({
                    lat: 38.729948,
                    lng: -9.139606,
                }
            );
            this.setState({loading: false});
        }
    }

    getAddressLocation = () => {
        this.setState({lat: this.state.userLatLng.lat, lng: this.state.userLatLng.lng});
    }

    iconWithLevel = (type, level) => {
        if (level == 1) {
            if (type == 'Limpeza de mato' || type=='Limpeza de Mato')
                return trash1;
            else if (type == 'Zona de mau acesso')
                return noAccess1;
            else if (type == "Corte de árvores")
                return cuttree1;
            else
                return greenIcon;
        }
        else if (level == 2) {
            if (type == 'Limpeza de mato' || type=='Limpeza de Mato')
                return trash2;
            else if (type == 'Zona de mau acesso')
                return noAccess2;
            else if (type == "Corte de árvores")
                return cuttree2;
            else
                return ligthGreenIcon;
        }
        else if (level == 3) {
            if (type == 'Limpeza de mato' || type=='Limpeza de Mato')
                return trash3;
            else if (type == 'Zona de mau acesso')
                return noAccess3;
            else if (type == "Corte de árvores")
                return cuttree3;
            else
                return yellowIcon;
        }
        else if (level == 4) {
            if(type=='Limpeza de mato'|| type=='Limpeza de Mato')
                return trash4;
            else if(type=='Zona de mau acesso')
                return noAccess4;
            else if(type=="Corte de árvores")
                return cuttree4;
            else
                return orangeIcon;
        }
        else{
            if(type=='Limpeza de mato'|| type=='Limpeza de Mato')
                return trash5;
            else if(type=='Zona de mau acesso')
                return noAccess5;
            else if(type=="Corte de árvores")
                return cuttree5;
            else
                return redIcon;
        }

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
        const classes = this.props;
        var result = [];
        for(var i =0; i < this.state.object.length; i++) {
            var obj = this.state.object;

            if(obj[i].user_occurrence_level == level){
                result.push(
                    <Marker position={[obj[i].user_occurrence_lat, obj[i].user_occurrence_lon]}
                            icon={this.iconWithLevel(obj[i].user_occurrence_type, obj[i].user_occurrence_level)}>
                        <Popup>
                            <div className={classes.opName}>{obj[i].user_occurrence_title}</div>
                            <b>Data de registo:</b> {obj[i].user_occurrence_data} <br />
                            <b>Grau de urgencia:</b> {obj[i].user_occurrence_level} <br/>
                            <b> Tipo de problema:</b> {obj[i].user_occurrence_type}
                        </Popup>
                    </Marker>
                )}
            }

            // if(level == 5)
            //     this.setState({loading: false});

        return result;
    };

    listTypeProblem = (type) =>{
        var result = [];



        return result;
    }

    changeLevel = (level) => {

    }



    render() {
        const position = [this.state.lat, this.state.lng];
        const {object, loading} = this.state;
        const center = [51.505, -0.09];
        const rectangle = [[51.49, -0.08], [51.5, -0.06]];
        const classes = this.props;

        // // initialize the map
        // var map = L.map('map').setView([42.35, -71.08], 13);
        //
        // // load a tile layer
        // L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
        //     attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
        //     maxZoom: 18,
        //     id: 'mapbox.streets',
        //     accessToken: 'your.mapbox.access.token'
        // }).addTo(map);
        //
        // var legend = L.control({position: 'bottomright'});
        //
        // // var map = document.getElementById("map");
        //
        // legend.onAdd = function (map) {
        //
        //     var div = L.DomUtil.create('div', 'info legend'),
        //         grades = [0, 10, 20, 50, 100, 200, 500, 1000],
        //         labels = [];
        //
        //     //loop through our density intervals and generate a label with a colored square for each interval
        //     for (var i = 0; i < grades.length; i++) {
        //         div.innerHTML +=
        //             '<i style="background:' +
        //             // getColor(grades[i] + 1) +
        //             '"></i> ' +
        //             grades[i] + (grades[i + 1] ? '&ndash;' + grades[i + 1] + '<br>' : '+');
        //     }
        //
        //     return div;
        // };
        // //
        // // legend.addTo(map);

        return (
            <div>
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
                            <Tooltip title="Localizar por GPS">
                                <IconButton
                                    aria-label="Localizar por GPS"
                                    onClick={this.getLocation}>
                                    <GpsIcon />
                                </IconButton>
                            </Tooltip>
                        )}
                    </div>
                    {/*<div className={classes.actions}>*/}
                    {/*{(*/}
                    {/*<Tooltip title="Atualizar mapa">*/}
                    {/*<IconButton aria-label="Atualizar mapa">*/}
                    {/*<UpdateListIcon />*/}
                    {/*</IconButton>*/}
                    {/*</Tooltip>*/}
                    {/*)}*/}
                    {/*</div>*/}
                    <div className={classes.actions}>
                        <Tooltip title={"Localizar por Morada"}>
                            <IconButton
                                aria-label={"Localizar por Morada"}
                                //variant="fab" mini
                                className={classes.searchButton}
                                onClick={this.getAddressLocation}> <AddressIcon/> </IconButton>
                        </Tooltip>
                    </div>
                </Toolbar>

                {loading && <CircularProgress/>}

                <div id={"map"} style={{heigth: 570}}></div>

                {!loading && <Map id={"map"} center={position} zoom={this.state.zoom} style={{width: '100%', height: 570}}>

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
                        {/*<BaseLayer checked name="Espaco publico">*/}
                            {/*{}*/}
                        {/*</BaseLayer>*/}

                        {/*<BaseLayer name="Espaco privado">*/}
                            {/*{}*/}
                        {/*</BaseLayer>*/}

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

                        {/*<Overlay checked name="Limpeza de mato">*/}
                            {/*<LayerGroup>*/}
                                {/*{}*/}
                            {/*</LayerGroup>*/}
                        {/*</Overlay>*/}

                        {/*<Overlay checked name="Zona de mau acesso">*/}
                            {/*<LayerGroup>*/}
                                {/*{}*/}
                            {/*</LayerGroup>*/}
                        {/*</Overlay>*/}

                        {/*<Overlay checked name="Outro">*/}
                            {/*<LayerGroup>*/}
                                {/*{}*/}
                            {/*</LayerGroup>*/}
                        {/*</Overlay>*/}

                    </LayersControl>

                </Map>}

                {/*<IconButton onClick={this.getLocation}><GpsIcon/></IconButton> Localizar por GPS*/}
            </div>
        )
    }
}
