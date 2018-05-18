import {Map, InfoWindow, Marker, GoogleApiWrapper, Listing} from 'google-maps-react';
import React, {Component} from 'react';

const style = {
    width: '50%',
    height: '50%'
};

export class MapContainer extends Component {
    state = {
        showingInfoWindow: false,
        activeMarker: {},
        selectedPlace: {},
    };

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

    render() {
        const {google} = window.google;
        
        return (
            <Map google={this.props.google} style={style} zoom={14}
            onReady={this.fetchPlaces} onClick={this.onMapClicked} className={'map'}>
                <Marker onClick={this.onMarkerClick}
                    title={'The marker`s title will appear as a tooltip.'}
                    name={'SOMA'} />
                    {/*position={{lat: 37.778519, lng: -122.405640}} />*/}
                {/*<Marker onMouseOver={this.onMouseOverMarker}*/}
                    {/*name={'Dolores park'}*/}
                    {/*position={{lat: 37.759703, lng: -122.428093}} />*/}
                {/*<Marker />*/}
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

export default GoogleApiWrapper({
    apiKey: 'AIzaSyAM-jV8q7-FWs7RdP0G4cH938jWgQwlGVo'
})(MapContainer)