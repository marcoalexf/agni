import React from 'react';
import TextField from 'material-ui/TextField';
import SearchIcon from '@material-ui/icons/Search';
import Icon from 'material-ui/Icon';
import Visibility from '@material-ui/icons/Visibility';
import VisibilityOff from '@material-ui/icons/VisibilityOff';
import ErrorIcon from '@material-ui/icons/Cancel';
import Typography from 'material-ui/Typography';
import Select from 'material-ui/Select';
import { InputLabel } from 'material-ui/Input';
import { MenuItem } from 'material-ui/Menu';
import Button from 'material-ui/Button';
import AddIcon from '@material-ui/icons/Add';
import WallpaperIcon from '@material-ui/icons/Wallpaper';
import Paper from 'material-ui/Paper';
import Checkbox from 'material-ui/Checkbox';
import NotificationsOff from '@material-ui/icons/NotificationsOff';
import NotificationsActive from '@material-ui/icons/NotificationsActive';
import Radio, { RadioGroup } from 'material-ui/Radio';
import { FormControl, FormHelperText } from 'material-ui/Form';
import { FormLabel, FormControlLabel } from 'material-ui/Form';
import Dialog, {DialogActions, DialogContent, DialogContentText, DialogTitle,} from 'material-ui/Dialog';
import {withStyles} from "material-ui/styles/index";
import GoogleMapReact from 'google-map-react';
//import Marker from 'google-map-react';
import {Link} from "react-router-dom";
import Table, { TableBody, TableCell, TablePagination, TableHead, TableRow } from 'material-ui/Table';
import Toolbar from 'material-ui/Toolbar';
import UpdateListIcon from '@material-ui/icons/Save';
import Tooltip from 'material-ui/Tooltip';
import PropTypes from 'prop-types';
import IconButton from 'material-ui/IconButton';
import CircularProgress from '@material-ui/core/CircularProgress';
import PlacesAutocomplete from 'react-places-autocomplete';
import { geocodeByAddress, geocodeByPlaceId, getLatLng } from 'react-places-autocomplete';
import AddressIcon from '@material-ui/icons/Navigation';
//import {SearchBox, Marker} from 'react-google-maps';
import classNames from 'classnames';
import './Maps.css';
//import { EditControl } from './EditControl';
import L from 'leaflet';
import GpsIcon from '@material-ui/icons/LocationSearching';
import {noAccess1, noAccess2, noAccess3, noAccess4, noAccess5} from './mapIcons';
import {trash1, trash2, trash3, trash4, trash5} from "./mapIcons";
import {greenIcon, redIcon, yellowIcon, ligthGreenIcon, orangeIcon} from "./mapIcons";
import {cuttree1, cuttree2, cuttree3, cuttree4, cuttree5} from "./mapIcons";
import { Map, TileLayer, Marker, Popup } from 'react-leaflet'

const AnyReactComponent = ({ text }) => <div>{text}</div>;

const styles =  theme => ({
    title:{
        margin:20,
    },
    paper:theme.mixins.gutters({
        padding: 40,
        width: 500,
    }),
    textField: {
        margin: theme.spacing.unit,
        width: 550,
    },
    formControl: {
        margin: theme.spacing.unit,
        width: 250,
    },
    selectEmpty: {
        marginTop: theme.spacing.unit * 2,
    },
    uploadButton: {
        margin: 20,
    },
    map:{
        marginBottom: 20,
    },
    properties:{
        display: 'inline-block',
    },
    regButton:{
        marginLeft: 1000,
    },
    searchButton:{
        marginLeft: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
    },
    input: {
        display: 'none',
    },
    toolbarroot: {
        paddingRight: theme.spacing.unit,
    },
    toolbartitle: {
        flex: '0 0 auto',
    },
    spacer: {
        flex: '1 1 100%',
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

let EnhancedTableToolbar = props => {
    const { classes } = props;

    return (
        <Toolbar
            className={classes.toolbarroot}
        >
            <div className={classes.toolbartitle}>
                {(
                    <Typography variant="title" id="tableTitle">
                        Registar Problema
                    </Typography>
                )}
            </div>
            <div className={classes.spacer} />
            <div className={classes.actions}>
                {(
                    <Tooltip title="Guardar">
                        <IconButton aria-label="Guardar">
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

function loadJS(src) {
    var ref = window.document.getElementsByTagName("script")[0];
    var script = window.document.createElement("script");
    script.src = src;
    script.async = true;
    ref.parentNode.insertBefore(script, ref);
}

class NewRegistProblem extends React.Component {

    constructor(props) {
        super(props);
        this.handleSeeIfLoggedIn = this.handleSeeIfLoggedIn.bind(this);
        this.getLocation = this.getLocation.bind(this);
        this._handleImageChange = this._handleImageChange.bind(this);
        this._handleSubmit = this._handleSubmit.bind(this);
        this.handleRegistProblem = this.handleRegistProblem.bind(this);

    this.state = {
        name: 'Nome do Registo do Problema',
        description: '',
        private: false,
        visibility: "public",
        spaceType: "public",
        problemType: 'Limpeza de Mato',
        problem: "Limpeza de Mato",
        urgency: 3,
        location: '',
        open: false,
        privateSpace: false,
        myLatLng: {
            lat: 49.2827,
            lng: -123.1207
        },
        loading: true,
        file: '',
        imagePreviewUrl: '',
        address: '',
        nUploads: 0,
        userLocality: '',
        userLatLng: {
            lat: 0,
            lng: 0,
        },
    };
}

    async componentDidMount(){
        var token = window.localStorage.getItem('token');

        if(token != null){
            let a = await getUserAddress();
            if(a != null){
                this.setState({userLocality: a});
                // geocodeByAddress(a)
                //     .then(results => getLatLng(results[0]))
                //     //.then(latLng => this.setState({userLatLng: latLng}), console.log(this.state.userLatLng))
                //     .then(latLng => new Promise(console.log("Success", latLng)))
                //     //.then(latLng => this.setState({userLatLng: latLng}), console.log(this.state.userLatLng))
                //     .catch(error => console.error('Error', error));
                let b = await getUserAddressLatLng(a);
                this.setState({userLatLng:{
                        lat: b.lat,
                        lng: b.lng
                    } });
                console.log(b);
                console.log(this.state.userLatLng);
                console.log(this.state.userLocality);

                this.getLocation();
            }

            else{
                this.getLocation();
            }
            //this.initMap();
            // Asynchronously load the Google Maps script, passing in the callback reference
            //loadJS('https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&callback=initMap');
        }

        else{
            document.getElementById("tologin").click();
        }
    }

    initMap () {
        // var map = L.map('map').setView([this.state.myLatLng.lat, this.state.myLatLng.lng], 14);
        // L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
        //     attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
        // }).addTo(map);
        // var marker = L.marker([this.state.myLatLng.lat, this.state.myLatLng.lng]).addTo(map);
        // mapLink =
        //     '<a href="http://openstreetmap.org">OpenStreetMap</a>';
        // L.tileLayer(
        //     'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        //         attribution: '&copy; ' + mapLink + ' Contributors',
        //         maxZoom: 18,
        //     }).addTo(map);

        // var drawnItems = new L.FeatureGroup();
        // map.addLayer(drawnItems);
        //
        // var drawControl = new L.Control.Draw({
        //     edit: {
        //         featureGroup: drawnItems
        //     }
        // });
        // map.addControl(drawControl);
        //
        // map.on('draw:created', function (e) {
        //     var type = e.layerType,
        //         layer = e.layer;
        //     drawnItems.addLayer(layer);
        // });
    }

    getLocation = () => {
        var token = window.localStorage.getItem('token');
        this.setState({loading: true});

        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition((position) => {
                this.setState({
                        myLatLng: {
                            lat: position.coords.latitude,
                            lng: position.coords.longitude
                        }
                    }
                );
                this.setState({loading: false});
                this.setState({address: ''});
            })
        } else if(token != null && this.state.userLocality != '') {
            //browser doesn't support geolocation, set as vancouver
            this.setState({
                    myLatLng: this.state.userLatLng
                }
            );

            this.setState({loading: false});
            this.setState({address: this.state.userLocality});

            console.log("Browser does not support geolocation");
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
        this.setState({address: this.state.userLocality});
        this.setState({myLatLng: this.state.userLatLng});
    }

    handleChange = name => event => {
        this.setState({
            [name]: event.target.value,
        });
        console.log("spaceType: " + this.state.spaceType + " privateSpace: " + this.state.privateSpace);
    };

    handleMouseDownVisibility = event => {
        event.preventDefault();
    };

    handleVisibility = event => {
        if(event.target.value == 'private'){
            this.setState(
                {private: true}
            );
        }
        else{
            this.setState({private: false});
        }

        this.setState({ [event.target.name]: event.target.value });
    };

    handleTypeOfSpace = event => {
        if(event.target.value == 'private'){
            this.setState(
                {privateSpace: true}
            );
        }
        else{
            this.setState({privateSpace: false});
        }

        this.setState({ [event.target.name]: event.target.value });
        console.log("spaceType: " + this.state.spaceType + " privateSpace: " + this.state.privateSpace);
    };

    handleSpaceChange = event => {
        this.setState({ spaceType: event.target.value });
    };

    handleTypeChange = event => {
        this.setState({ [event.target.name]: event.target.value });
        console.log("tipo de problema " + this.state.problem);
        //this.setState({ problemType: event.target.toString()});
        //console.log("problemType: " + this.state.problemType);
    };

    handleClickOpen = () => {
        this.setState({ open: true });
    };

    handleClose = () => {
        this.setState({ open: false });
    };

    handleRegistProblem = () => {
        var token = window.localStorage.getItem('token');
        var tokenjson = JSON.parse(token);

        var data = {
            "token": tokenjson,
            "title": this.state.name,
            "description": this.state.description,
            "type": this.state.problemType,
            "level": this.state.urgency,
            "visibility": !this.state.private,
            "lat": this.state.myLatLng.lat,
            "lon": this.state.myLatLng.lng,
            "notificationOnResolve": this.state.open,
            "uploadMedia": this.state.wantUploadPhoto,
            "nUploads": this.state.nUploads,
        };
        console.log("nuploads" + this.state.nUploads);
        console.log("lat " + this.state.myLatLng.lat);
        console.log("lon " + this.state.myLatLng.lng);


        console.log(data);

        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open( "POST", 'https://custom-tine-204615.appspot.com/rest/occurrence/register');
        xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        var myJSON = JSON.stringify(data);
        xmlHttp.send(myJSON);

        xmlHttp.onreadystatechange = function() {//Call a function when the state changes.
            if(xmlHttp.readyState === XMLHttpRequest.DONE) {

                if(xmlHttp.status === 200){
                    console.log("Sucesso");

                    if(this.state.file != ''){
                        var response = xmlHttp.response;
                        var id = JSON.parse(response);
                        var length = id.length;
                        console.log("id");
                        console.log(id);
                        console.log(id.uploadMediaIDs);
                        console.log(id.uploadMediaIDs[0]);
                        this.uploadPhoto(id.uploadMediaIDs[0]);
                    }

                    document.getElementById("tothankyou").click();
                }

                else{
                    console.log("Ocorreu um erro - Nao foi possivel registar o problema");
                    console.log("O token pode ter expirado");
                }
            }

        }.bind(this)
    };

    handleSeeIfLoggedIn = () => {
        console.log("here");
        var token = window.localStorage.getItem('token');

        if(token == null){
            console.log("null");
            //document.getElementById("tologin").click();
        }

    };
    handleNotificationChange = () =>{

    }

    static defaultProps = {
        center: {
            lat: 38.661453,
            lng: -9.206618
        },
        zoom: 11
    };

    _handleSubmit(e) {
        e.preventDefault();
        // TODO: do something with -> this.state.file
    }

    _handleImageChange(e) {
        e.preventDefault();

        let reader = new FileReader();
        let file = e.target.files[0];

        if(file){
            reader.onloadend = () => {
                this.setState({
                    file: file,
                    imagePreviewUrl: reader.result,
                    wantUploadPhoto: true,
                    nUploads: 1,
                });
            };

            reader.readAsDataURL(file)
        }
    }

    uploadPhoto = id => {
        console.log("uploadPhoto");
        var file = this.state.file;
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open( "POST", 'https://custom-tine-204615.appspot.com/rest/upload/' + id);
        xmlHttp.setRequestHeader("Content-Type", file.type);
        // xmlHttp.setRequestHeader("Access-Control-Request-Headers", file.type);
        // var myJSON = JSON.stringify(file);
        xmlHttp.send(file);

        xmlHttp.onreadystatechange = function() {//Call a function when the state changes.
            if(xmlHttp.readyState === XMLHttpRequest.DONE) {
                if(xmlHttp.status === 200){
                    console.log("uploadPhoto sucessfully!");
                    // document.getElementById("tologin").click();
                }

                else{
                    // document.getElementById("errorMessage").innerHTML = "Ocorreu um erro ao carregar a sua foto";
                }
            }
        }.bind(this)

    };

    handleAddressChange = (address) => {
        this.setState({address});
        // this.setState({locality: ''});
    };

    handleAddressSelect = (address) => {
        geocodeByAddress(address)
            .then(results => getLatLng(results[0]))
            .then(latLng => console.log('Success', latLng))
            .catch(error => console.error('Error', error));

        this.setState({address});
        console.log("getLatLng: ");

        geocodeByAddress(address)
            .then(results => getLatLng(results[0]))
            .then(latLng => this.setState({myLatLng: latLng}), console.log(this.state.myLatLng));

        //this.setState({myLatLng: getLatLng});
        // this.setState({ locality: address });
        // this.setState({validLocation: true});
        // console.log("locality: " + this.state.locality);
    };

    handleFormSubmit = (event) => {
        event.preventDefault()
        const { address } = this.state

        geocodeByAddress(address,  (err, { lat, lng }) => {
            if (err) { console.log('Oh no!', err) }

            console.log(`Yay! got latitude and longitude for ${address}`, { lat, lng })
        })
    };

    onChange = (address) => this.setState({ address });

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

    render(){
        const { classes } = this.props;
        const {loading} = this.state;
        let {imagePreviewUrl} = this.state;
        let $imagePreview = null;
        // (<img src={require('./img/registUser2.png')} alt="Avatar2" width={100} style={{marginBottom: '20'}} />);
        if (imagePreviewUrl) {
            $imagePreview = (<img src={imagePreviewUrl} width={300} className={classes.accountPhoto} />);
        }
        const position = [this.state.myLatLng.lat, this.state.myLatLng.lng];

        return(
            <div onLoad={this.handleSeeIfLoggedIn()}>
                <EnhancedTableToolbar></EnhancedTableToolbar>
                <Paper>
                <Table>
                    <TableBody>
                        <TableRow>
                            <TableCell>
                                <TextField
                                    id="name"
                                    label="Nome do registo"
                                    onChange={this.handleChange('name')}
                                    className={classes.textField}
                                />
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                {$imagePreview}
                                <form onSubmit={this._handleSubmit}>
                                    <input type="file" onChange={this._handleImageChange} />
                                    {/*<button type="submit" onClick={this._handleSubmit}>Upload Image</button>*/}
                                </form>
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                {loading && <CircularProgress/>}
                                {!loading && <div>
                                    {/*<TextField*/}
                                        {/*id="location"*/}
                                        {/*label="Localizacao"*/}
                                        {/*onChange={this.handleChange('location')}*/}
                                        {/*className={classes.textField}*/}
                                    {/*/>*/}

                                    <PlacesAutocomplete
                                        value={this.state.address}
                                        onChange={this.handleAddressChange}
                                        onSelect={this.handleAddressSelect}
                                        //className="pac-container"
                                    >
                                        {({ getInputProps, suggestions, getSuggestionItemProps }) => {
                                            return(
                                                <div>
                                                    <div className={"search-input-container"}>
                                                        <TextField label={'Localidade'}
                                                                   {...getInputProps({
                                                                       //placeholder: 'Localidade',
                                                                       //className: 'search-input'
                                                                   })}
                                                                   className={classes.textField}
                                                        />

                                                        <Tooltip title={"Localizar por GPS"}>
                                                            <IconButton
                                                                aria-label={"Localizar por GPS"}
                                                                //variant="fab" mini
                                                                className={classes.searchButton}
                                                                onClick={this.getLocation}> <GpsIcon/> </IconButton>
                                                        </Tooltip>

                                                        <Tooltip title={"Localizar por Morada"}>
                                                            <IconButton
                                                                aria-label={"Localizar por Morada"}
                                                                //variant="fab" mini
                                                                className={classes.searchButton}
                                                                onClick={this.getAddressLocation}> <AddressIcon/> </IconButton>
                                                        </Tooltip>
                                                    </div>

                                                    {suggestions.length> 0 &&

                                                    (<div className='autocomplete-container'>
                                                            {suggestions.map(suggestion => {
                                                                const className = classNames(
                                                                    'suggestion-item'
                                                                    , {
                                                                        'suggestion-item--active': suggestion.active,
                                                                    })
                                                                ;
                                                                //suggestion.active ? 'suggestion-item--active' : 'suggestion-item';
                                                                // inline style for demonstration purpose
                                                                // const style = suggestion.active
                                                                //     ? { backgroundColor: '#d3d3d3', cursor: 'pointer' }
                                                                //     : { backgroundColor: '#ffffff', cursor: 'pointer' };
                                                                return (
                                                                    <div {...getSuggestionItemProps(suggestion, { className })}>
                                                                        <strong>
                                                                            {suggestion.formattedSuggestion.mainText}
                                                                        </strong>{' '}
                                                                        <small>
                                                                            {suggestion.formattedSuggestion.secondaryText}
                                                                        </small>
                                                                    </div>
                                                                );
                                                            })}
                                                            <div className="dropdown-footer">
                                                                <div>
                                                                    <img
                                                                        src={require('./img/powered_by_google_default.png')}
                                                                        className="dropdown-footer-image"
                                                                    />
                                                                </div>
                                                            </div>
                                                        </div>
                                                    )}
                                                </div>
                                            );
                                        }}
                                    </PlacesAutocomplete>
                                </div>
                                }

                                {!loading && <div className={classes.map} style={{ height: '50vh', width: '100%' }}>
                                    <GoogleMapReact
                                        bootstrapURLKeys={{ key: 'AIzaSyDdgLsB6JZ6PGlDphDfM_9At-kVkdq-2VI' }}
                                        center={this.state.myLatLng}
                                        defaultZoom={this.props.zoom}
                                    >
                                        {/*<Marker lat={this.state.myLatLng.lat} lng={this.state.myLatLng.lng} position={this.state.myLatLng}*/}
                                        {/*visible={true} title={"nova ocorrencia"}/>*/}
                                        {/*<AnyReactComponent*/}
                                            {/*lat={38.661453}*/}
                                            {/*lng={-9.206618}*/}
                                        {/*/>*/}

                                        {/*<SearchBox*/}
                                            {/*ref={this.props.onSearchBoxMounted}*/}
                                            {/*bounds={this.props.bounds}*/}
                                            {/*controlPosition={window.google.maps.ControlPosition.TOP_LEFT}*/}
                                            {/*onPlacesChanged={this.props.onPlacesChanged}*/}
                                        {/*>*/}
                                            {/*<input*/}
                                                {/*type="text"*/}
                                                {/*placeholder="Customized your placeholder"*/}
                                                {/*style={{*/}
                                                    {/*boxSizing: `border-box`,*/}
                                                    {/*border: `1px solid transparent`,*/}
                                                    {/*width: `240px`,*/}
                                                    {/*height: `32px`,*/}
                                                    {/*marginTop: `27px`,*/}
                                                    {/*padding: `0 12px`,*/}
                                                    {/*borderRadius: `3px`,*/}
                                                    {/*boxShadow: `0 2px 6px rgba(0, 0, 0, 0.3)`,*/}
                                                    {/*fontSize: `14px`,*/}
                                                    {/*outline: `none`,*/}
                                                    {/*textOverflow: `ellipses`,*/}
                                                {/*}}*/}
                                            {/*/>*/}
                                        {/*</SearchBox>*/}
                                        {/*{this.props.markers.map((marker, index) =>*/}
                                            {/*<Marker key={index} position={marker.position} />*/}
                                        {/*)}*/}
                                    </GoogleMapReact>
                                </div>}
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                {!loading &&
                                <Map id={"map"} center={position} zoom={11} style={{width: '100%', height: '50vh'}} scrollWheelZoom={false}>
                                    <TileLayer
                                        attribution="&amp;copy <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
                                        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                                    />
                                    <Marker position={position}
                                            icon={this.iconWithLevel(this.state.problem, this.state.urgency)}>
                                        <Popup>
                                            <p>Nova ocorrencia</p>
                                        </Popup>
                                    </Marker>
                                </Map>}
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                {/*{!loading && <div id="map" style={{height: '180px'}}></div>}*/}
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                <TextField
                                    id="description"
                                    label="Descricao - descreva o estado generico do problema"
                                    multiline
                                    rows="4"
                                    onChange={this.handleChange('description')}
                                    className={classes.textField}
                                />
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                <FormControl
                                    component="fieldset"
                                    required
                                >
                                    <FormLabel component="legend">Tipo de espaco</FormLabel>
                                    <RadioGroup
                                        aria-label="typeSpace"
                                        name="typeSpace"
                                        value={this.state.spaceType}
                                        onChange={this.handleSpaceChange}
                                    >
                                        <FormControlLabel
                                            value="public"
                                            control={<Radio color="primary" />}
                                            label="Publico"
                                        />
                                        <FormControlLabel value="private" control={<Radio color="primary" />} label="Privado" />
                                    </RadioGroup>
                                </FormControl>
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                <FormControl className={classes.formControl}>
                                    <InputLabel htmlFor="problem-name">Tipo de Problema</InputLabel>
                                    <Select
                                        value={this.state.problem}
                                        onChange = {this.handleTypeChange}
                                        inputProps={{
                                            name: 'problem',
                                            id: 'problem-name',
                                        }}
                                    >
                                        <MenuItem value={"Limpeza de Mato"}>Limpeza de Mato</MenuItem>
                                        <MenuItem value={"Zona de mau acesso"}>Zona de mau acesso</MenuItem>
                                        <MenuItem value={"Outro"}>Outro</MenuItem>
                                    </Select>
                                </FormControl>
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                <FormControl className={classes.formControl}>
                                    <InputLabel htmlFor="urgency-level">Grau de urgencia:</InputLabel>
                                    <Select
                                        value={this.state.urgency}
                                        onChange = {this.handleTypeChange}
                                        inputProps={{
                                            name: 'urgency',
                                            id: 'urgency-level',
                                        }}
                                    >
                                        <MenuItem value={1}>1</MenuItem>
                                        <MenuItem value={2}>2</MenuItem>
                                        <MenuItem value={3}>3</MenuItem>
                                        <MenuItem value={4}>4</MenuItem>
                                        <MenuItem value={5}>5</MenuItem>
                                    </Select>
                                </FormControl>
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                <FormControl className={classes.formControl}>
                                    <Select
                                        value={this.state.visibility}
                                        onChange = {this.handleVisibility}
                                        displayEmpty
                                        name='visibility'
                                        className={classes.selectEmpty}
                                    >
                                        <MenuItem value="public">Toda a gente pode ver este problema</MenuItem>
                                        <MenuItem value="private">Apenas eu posso ver este problema</MenuItem>
                                    </Select>
                                    <FormHelperText>Visibilidade</FormHelperText>
                                </FormControl>
                                <FormControl>
                                    <Icon
                                        aria-label="regist visibility"
                                    >
                                        {this.state.private ? <VisibilityOff /> : <Visibility />}
                                    </Icon>
                                </FormControl>
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                <FormControlLabel
                                    control={
                                        <Checkbox icon={<NotificationsOff />} checkedIcon={<NotificationsActive />} color={"primary"} />
                                    }
                                    label="Notificar quando estiver resolvido"
                                    onChange={this.handleNotificationChange}
                                />
                            </TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
                </Paper>

                <div id="submeter" className={"imgcontainer"}>
                    <Button variant="raised" size="small" color="primary" style={{margin:'0 auto'}} onClick={this.handleClickOpen}>
                        <AddIcon />
                        Registar Problema
                    </Button>

                    <Dialog
                        open={this.state.open}
                        onClose={this.handleClose}
                        aria-labelledby="alert-dialog-title"
                        aria-describedby="alert-dialog-description"
                    >
                        <DialogTitle id="alert-dialog-title">{"Tem a certeza que pretende registar o problema?"}</DialogTitle>
                        <DialogContent>
                            <DialogContentText id="alert-dialog-description">
                                Podera posteriormente editar este registo no seu perfil na seccao reportes.
                            </DialogContentText>
                        </DialogContent>
                        <DialogActions>
                            <Button onClick={this.handleClose} color="primary">
                                Cancelar
                            </Button>
                            <Button onClick={this.handleRegistProblem} color="primary" autoFocus>
                                Sim
                            </Button>
                        </DialogActions>
                    </Dialog>
                </div>

                <Button id={"tothankyou"} component={Link} to='/obrigada' className={classes.input} color={"primary"}>
                    Obrigada
                </Button>

                <Button id={"tologin"} component={Link} to='/login' className={classes.input} color={"primary"}>
                    Sem sessao iniciada
                </Button>
            </div>
        );
    }
}

export default withStyles(styles)(NewRegistProblem);