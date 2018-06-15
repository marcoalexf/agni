import {Map, InfoWindow, Marker, GoogleApiWrapper, Listing} from 'google-maps-react';
import React, {Component} from 'react';
// import { Popup } from 'react-leaflet';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';

const style = {
    width: '80%',
    height: '70%'
};

const LoadingContainer = (props) => (
    <div>A carregar mapa...</div>
);

// const { Map: LeafletMap, Popup } = window.ReactLeaflet;
// const { Map: LeafletMap, TileLayer, Marker, Popup } = ReactLeaflet;

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



export class MapContainer extends Component {
    constructor(props){
        super(props);

        this.onMarkerClick = this.onMarkerClick.bind(this);

        this.state = {
            showingInfoWindow: false,
            activeMarker: {},
            selectedPlace: {},
            object: [
                {user_occurrence_title: ''}],
        };
    }

    fetchPlaces(mapProps, map) {
        const {google} = mapProps;
        const service = new google.maps.places.PlacesService(map);
        // ...
    }

    onMouseOverMarker(props, marker, e){
        //...
    }

    onMarkerClick = (props, marker, e) =>
        this.setState({
            selectedPlace: props,
            activeMarker: marker,
            showingInfoWindow: true
        });

    onMapClicked = (props) => {
        if (this.state.showingInfoWindow) {
            this.setState({
                showingInfoWindow: false,
                activeMarker: null
            })
        }
    };

    componentDidMount() {
        xmlRequest.then((value) =>{
                this.setState({object: value});
                console.log("state object");
                console.log(this.state.object);
            }
        );
    }

    render() {
        const {google} = window.google;
        const {object} = this.state;
        const position = [38.66, -9.20];
        
        return (
            <Map google={this.props.google}
                 style={style}
                 initialCenter={{
                     lat: 38.66,
                     lng: -9.20
                 }}
                 zoom={14}
                 onReady={this.fetchPlaces}
                 onClick={this.onMapClicked}
                 className={'map'}>
                {/*<Marker position={position}>*/}
                    {/*<Popup>*/}
                        {/*It works!!!*/}
                    {/*</Popup>*/}
                {/*</Marker>*/}
                <Marker onClick={this.onMarkerClick}
                    title={'Clica para mais detalhes'} position={{lat: 38.661453, lng: -9.206618}} />
                    {/*position={{lat: 38.661453, lng: -9.206618}} />*/}
                {/*<Marker onMouseOver={this.onMouseOverMarker}*/}
                    {/*name={'Dolores park'}*/}
                    {/*position={{lat: 37.759703, lng: -122.428093}} />*/}
                {/*<Marker />*/}
                {/*<InfoWindow*/}
                    {/*marker={this.state.activeMarker}*/}
                    {/*visible={this.state.showingInfoWindow}>*/}
                    {/*<div>*/}
                        {/*<h1>{this.state.selectedPlace.name}</h1>*/}
                    {/*</div>*/}
                {/*</InfoWindow>*/}

                {object.map(n => {
                    return (
                            <Marker onClick={this.onMarkerClick}
                                    title={n.user_occurrence_title}
                                    name={n.user_occurrence_title}
                                    position={{lat: n.user_occurrence_lat, lng: n.user_occurrence_lon}}/>

                    );})
                }

                <InfoWindow
                    marker={this.state.activeMarker}
                    visible={this.state.showingInfoWindow}>
                    <div>
                        <h1>{this.state.selectedPlace.name}</h1>
                    </div>
                </InfoWindow>

                {/*<Marker*/}
                    {/*name={'Your position'}*/}
                    {/*position={{lat: 37.762391, lng: -122.439192}}*/}
                    {/*icon={{*/}
                        {/*url: "/path/to/custom_icon.png",*/}
                        {/*anchor: new google.maps.Point(32,32),*/}
                        {/*scaledSize: new google.maps.Size(64,64)*/}
                    {/*}} />*/}
                {/*<Listing places={this.state.places} />*/}
                {/*<Marker onClick={this.onMarkerClick}*/}
                        {/*name={'Current location'} />*/}

                {/*<InfoWindow onClose={this.onInfoWindowClose}>*/}
                    {/*<div>*/}
                        {/*<h1>{this.state.selectedPlace.name}</h1>*/}
                    {/*</div>*/}
                {/*</InfoWindow>*/}
            </Map>
        );
    }
}

// MapContainer.propTypes = {
//     google: React.PropTypes.object,
//     zoom: React.PropTypes.number,
//     initialCenter: React.PropTypes.object
// }

// MapContainer.propTypes = {
//     google: PropTypes.object,
//     zoom: PropTypes.number,
//     initialCenter: PropTypes.object,
//     centerAroundCurrentLocation: PropTypes.bool
// };
//
// MapContainer.defaultProps = {
//     zoom: 13,
//     // San Francisco, by default
//     initialCenter: {
//         lat: 37.774929,
//         lng: -122.419416
//     },
//     centerAroundCurrentLocation: false
// };

export default GoogleApiWrapper({
    apiKey: 'AIzaSyAM-jV8q7-FWs7RdP0G4cH938jWgQwlGVo',
    LoadingContainer: LoadingContainer
})(MapContainer)