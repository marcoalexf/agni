import React from 'react';
import PropTypes from 'prop-types';
import MobileStepper from 'material-ui/MobileStepper';
import Paper from 'material-ui/Paper';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';
import Typography from 'material-ui/Typography';
import Button from 'material-ui/Button';
import KeyboardArrowLeft from '@material-ui/icons/KeyboardArrowLeft';
import KeyboardArrowRight from '@material-ui/icons/KeyboardArrowRight';
import SwipeableViews from 'react-swipeable-views';
import img2 from './img/matasuja.jpg';
import { Map, TileLayer, Marker, Popup } from 'react-leaflet'
import AddressIcon from '@material-ui/icons/Navigation';
// import {Map, InfoWindow, Marker, GoogleApiWrapper, Listing} from 'google-maps-react';
// import GoogleMapReact from 'google-map-react';
import CircularProgress from '@material-ui/core/CircularProgress';
import {skyBlue, lightGrey, white, lightBlue} from "./Colors";
import LikeIcon from '@material-ui/icons/Favorite';
import NonLikeIcon from '@material-ui/icons/FavoriteBorder';
import CommentIcon from '@material-ui/icons/ModeComment';
import Checkbox from 'material-ui/Checkbox';
import { FormLabel, FormControlLabel } from 'material-ui/Form';
import TextField from "material-ui/TextField";
import {redIcon} from './mapIcons';
import Card from 'material-ui/Card';
import CardHeader from '@material-ui/core/CardHeader';
import CardMedia from '@material-ui/core/CardMedia';
import CardContent from '@material-ui/core/CardContent';
import CardActions from '@material-ui/core/CardActions';
import IconButton from 'material-ui/IconButton';

const AnyReactComponent = ({ text }) => <div>{text}</div>;

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
                    var mapList = obj.mapList;
                    map = mapList[0];
                    console.log("map:");
                    console.log(map);
                    resolve(obj.mapList);
                }
                else {
                    console.log("tempo expirado");
                }
            }
        }
    });}

function getComments (obj, index){
    return new Promise(resolve => {
            console.log("listar comentários de: " + obj[index].user_occurrence_title);
            //console.log("listar comentários de: " + this.state.object[this.state.activeStep].user_occurrence_title);
            var token = window.localStorage.getItem('token');
            var d = new Date();
            var t = d.getTime();
            // var object = this.state.object;
            // var activeStep = this.state.activeStep;

            if(token != null){
                var tokenjson = JSON.parse(token);
                var expirationData = JSON.parse(token).expirationData;
                console.log("tempo atual: " + t);
                console.log("data de expiracao: " + expirationData);

                if(expirationData <= t){
                    console.log("tempo expirado");
                    window.localStorage.removeItem('token');
                }

                else{
                    var data = {
                        "token": tokenjson,
                        "userID": Number(obj[index].userID),
                        "occurrenceID": Number(obj[index].occurrenceID),
                        "cursor": null,
                    }

                    console.log(data);

                    var xmlHttp = new XMLHttpRequest();
                    xmlHttp.open( "POST", 'https://custom-tine-204615.appspot.com/rest/occurrence/comment/list');
                    xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
                    var myJSON = JSON.stringify(data);
                    xmlHttp.send(myJSON);

                    xmlHttp.onreadystatechange = function() {
                        if (xmlHttp.readyState === XMLHttpRequest.DONE) {

                            if (xmlHttp.status === 200) {
                                var response = xmlHttp.response;
                                var comments = JSON.parse(response);

                                resolve(comments.mapList);

                                console.log("Sucesso");
                            }

                            else {
                                console.log("Ocorreu um erro - Nao foi listar comentarios");
                            }
                        }
                    }.bind(this)
                }
            }

            else{
                console.log("tologin");
            }
        }
    );
}

function getCommentsUsers(userID){
    return new Promise(resolve => {
        var token = window.localStorage.getItem('token');
        var d = new Date();
        var t = d.getTime();

        if(token != null){
            var tokenjson = JSON.parse(token);
            var expirationData = JSON.parse(token).expirationData;
            console.log("tempo atual: " + t);
            console.log("data de expiracao: " + expirationData);

            if(expirationData <= t){
                console.log("tempo expirado");
                window.localStorage.removeItem('token');
            }

            else{
                var data = {
                    "userID": userID,
                };

                console.log(data);

                var xmlHttp = new XMLHttpRequest();
                xmlHttp.open( "POST", 'https://custom-tine-204615.appspot.com/rest/profile/username');
                xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
                var myJSON = JSON.stringify(data);
                xmlHttp.send(myJSON);

                xmlHttp.onreadystatechange = function() {
                    if (xmlHttp.readyState === XMLHttpRequest.DONE) {

                        if (xmlHttp.status === 200) {
                            var response = xmlHttp.response;
                            var username = JSON.parse(response);

                            resolve(username);

                            console.log("Sucesso");
                        }

                        else {
                            console.log("Ocorreu um erro - Nao foi possivel encontrar o nome");
                        }
                    }
                }.bind(this)
            }
        }

        else{
            console.log("tologin");
        }
    });
}

function getSteps() {
    return ['Nao tratado', 'Ja foi mandado alguem para la', 'Tratado'];
}

function getStepContent(stepIndex) {
    switch (stepIndex) {
        case 0:
            return 'Select campaign settings...';
        case 1:
            return 'What is an ad group anyways?';
        case 2:
            return 'This is the bit I really care about!';
        default:
            return 'Uknown stepIndex';
    }
}

class SwipeableTextMobileStepper extends React.Component {
    constructor(){
        super();

        this.handleNext = this.handleNext.bind(this);
        this.handleBack = this.handleBack.bind(this);
    }
    state={
        activeStep: 0,
        activeStepTwo: 0,
        object: [
            {user_occurrence_title: 'titulo1'},
            {user_occurrence_title: 'titulo2'}],
        myLatLng: {
            lat: 59.95,
            lng: 30.33
        },
        zoom: 14,
        showingInfoWindow: false,
        activeMarker: {},
        selectedPlace: {},
        loading: true,
        comment: '',
        comments: [{comment_text: "comentário"}],
    };

    // async componentWillMount (){
    //     getComments = getComments.bind(this);
    //     let c = await getComments();
    //     this.setState({comments: c});
    // }

    async componentDidMount () {
        let o = await xmlRequest();
        this.setState({object: o});
        this.setState({myLatLng: {
                lat: this.state.object[this.state.activeStep].user_occurrence_lat,
                lng: this.state.object[this.state.activeStep].user_occurrence_lon
            }});
        this.setState({lat: this.state.object[this.state.activeStep].user_occurrence_lat});
        this.setState({lng: this.state.object[this.state.activeStep].user_occurrence_lon});
        // this.setState({center: {'lat': value.user_occurrence_lat, 'lng': value.user_occurrence_lon}});
        console.log("state object");
        console.log(this.state.object);

        // getComments.bind(this);
        let c = await getComments(o, 0);
        this.setState({comments: c});
        //console.log("comments: " + c[0].comment_text);
        this.setState({loading: false});
    }

    async handleNext (){

        console.log(this.state.activeStep);
        let c = await getComments(this.state.object, this.state.activeStep + 1);
        this.setState({comments: c});

        this.setState(prevState => ({
            activeStep: prevState.activeStep + 1,
        }));
        this.setState({myLatLng: {
                lat: this.state.object[this.state.activeStep].user_occurrence_lat,
                lng: this.state.object[this.state.activeStep].user_occurrence_lon
            }});
        this.setState({lat: this.state.object[this.state.activeStep].user_occurrence_lat});
        this.setState({lng: this.state.object[this.state.activeStep].user_occurrence_lon});
        console.log("lat: " + this.state.lat + "lng: " + this.state.lng);

    };

    async handleBack() {

        console.log(this.state.activeStep);
        let c = await getComments(this.state.object, this.state.activeStep - 1);
        this.setState({comments: c});

        this.setState(prevState => ({
            activeStep: prevState.activeStep - 1,
        }));
        this.setState({myLatLng: {
                lat: this.state.object[this.state.activeStep].user_occurrence_lat,
                lng: this.state.object[this.state.activeStep].user_occurrence_lon
            }});
        this.setState({lat: this.state.object[this.state.activeStep].user_occurrence_lat});
        this.setState({lng: this.state.object[this.state.activeStep].user_occurrence_lon});
        console.log("lat: " + this.state.lat + "lng: " + this.state.lng);
    };

    handleStepChange = activeStep => {
        this.setState({ activeStep });
        this.setState({myLatLng: {
                lat: this.state.object[activeStep].user_occurrence_lat,
                lng: this.state.object[activeStep].user_occurrence_lon
            }});
        this.setState({lat: this.state.object[this.state.activeStep].user_occurrence_lat});
        this.setState({lng: this.state.object[this.state.activeStep].user_occurrence_lon});
    };

    handleNextTwo = () => {
        const { activeStepTwo } = this.state;
        this.setState({
            activeStepTwo: activeStepTwo + 1,
        });
    };

    handleBackTwo = () => {
        const { activeStepTwo } = this.state;
        this.setState({
            activeStepTwo: activeStepTwo - 1,
        });
    };

    handleReset = () => {
        this.setState({
            activeStepTwo: 0,
        });
    };

    // static defaultProps = {
    //     center: {
    //         lat: 59.95,
    //         lng: 30.33
    //     },
    //     zoom: 11
    // };

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

    handleLikeChange = () => {
        console.log("Gosto mudado");
    };

    handleComment = () => {
        console.log("guardar comentario: " + this.state.comment);
        var token = window.localStorage.getItem('token');
        var d = new Date();
        var t = d.getTime();
        var object = this.state.object;
        var activeStep = this.state.activeStep;

        if(token != null){
            var tokenjson = JSON.parse(token);
            var expirationData = JSON.parse(token).expirationData;
            console.log("tempo atual: " + t);
            console.log("data de expiracao: " + expirationData);

            if(expirationData <= t){
                console.log("tempo expirado");
                window.localStorage.removeItem('token');
            }

            else{
                var data = {
                    "token": tokenjson,
                    "userID": object[activeStep].userID,
                    "occurrenceID": Number(object[activeStep].occurrenceID),
                    "comment": this.state.comment,
                }

                console.log(data);

                var xmlHttp = new XMLHttpRequest();
                xmlHttp.open( "POST", 'https://custom-tine-204615.appspot.com/rest/occurrence/comment');
                xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
                var myJSON = JSON.stringify(data);
                xmlHttp.send(myJSON);

                xmlHttp.onreadystatechange = function() {
                    if (xmlHttp.readyState === XMLHttpRequest.DONE) {

                        if (xmlHttp.status === 200) {
                            this.setState({comment: ''});
                            this.updateComments();
                            console.log("Sucesso");
                        }

                        else {
                            console.log("Ocorreu um erro - Nao foi possivel registar o problema");
                        }
                    }
                }.bind(this);
            }
        }

        else{
            console.log("tologin");
        }

    };

    async updateComments(){
        let c = await getComments(this.state.object, this.state.activeStep);
        this.setState({comments: c});
    }

    handleCommentChange = event =>{
        this.setState({comment: event.target.value});
    };

    handleClickCommentIcon = () =>{
        console.log("evento de mousedown");
        document.getElementById("addComment").focus();
    }

    render() {
        const { classes, theme } = this.props;
        const { activeStep, object, activeStepTwo, center, zoom, loading, lat, lng, myLatLng, comments } = this.state;
        const steps = getSteps();
        const position = [lat, lng];

        const maxSteps = object.length;

        return (
            <div>
                {loading && <div className={"imgcontainer"}><CircularProgress /></div>}

                {!loading &&
                <div>
                    <div >
                        <Card>
                            <CardHeader
                                title={object[activeStep].user_occurrence_title}
                                subheader={object[activeStep].user_occurrence_date}
                            />

                            <CardContent>

                                <div style={{margin: 20}}>
                                    <img key={object[activeStep].user_occurrence_data} src={img2} style={{margin: '0 auto'}} alt={object[activeStep].user_occurrence_title}/>
                                </div>

                                <div >
                                    <div >
                                        <p>Tipo:</p> <Typography>{object[activeStep].user_occurrence_type}</Typography>
                                        <p>Grau de urgencia:</p> <Typography> {object[activeStep].user_occurrence_level}</Typography>
                                        <p>Estado:</p>
                                        <p>Data de previsão da limpeza:</p>
                                    </div>
                                    <div className={"w3-twothird"} style={{marginBottom: 10}}>
                                        <p>Localização: </p>

                                        <Map id={"map"} center={position} zoom={this.state.zoom} scrollWheelZoom={false} style={{width: 350, height: 190}}>
                                            <TileLayer
                                                attribution="&amp;copy <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
                                                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                                            />
                                            <Marker position={position}
                                                    icon={redIcon}
                                            >
                                                <Popup>
                                                    {object[activeStep].user_occurrence_title}
                                                </Popup>
                                            </Marker>
                                        </Map>
                                    </div>
                                </div>

                            </CardContent>
                        </Card>
                    </div>

                    <div >
                        <Card>
                            <CardHeader title={"Comentários"}/>
                            <CardContent >
                                <div>
                                    {/*<p>Comentários</p>*/}

                                    {comments.map(c =>{
                                        return(
                                            <div key={c.comment_date + c.comment_userID}>
                                                <Typography variant={"caption"} gutterBottom align={"right"}>{c.comment_date}</Typography>
                                                <div >
                                                    <b>{c.comment_username} </b>{c.comment_text}
                                                </div>

                                            </div>
                                        )})}

                                    {comments.length == 0 ?
                                        <Typography variant="caption" gutterBottom align="center">Sem comentários de momento</Typography> : null
                                    }

                                </div>

                            </CardContent>

                            <CardActions>

                                <div>
                                    <div>
                                        <FormControlLabel
                                            key={object[activeStep].occurrenceID}
                                            control={
                                                <Checkbox icon={<NonLikeIcon  />}
                                                          checkedIcon={<LikeIcon  />} color={"primary"} />
                                            }
                                            onChange={this.handleLikeChange}
                                            //label={"Apoiar"}

                                        /><div style={{display: 'inline'}}>? apoios</div>
                                        <IconButton>
                                            <CommentIcon onClick={this.handleClickCommentIcon}/>
                                        </IconButton>
                                        <div style={{display: 'inline'}}>{comments.length}</div>
                                        <div style={{display: 'inline'}}> {"comentários"}</div>
                                    </div>

                                    <TextField
                                        key={object[activeStep].occurrenceID}
                                        placeholder="(Escreva aqui o seu comentário)"
                                        id={"addComment"}
                                        // InputProps={{
                                        //     disableUnderline: true,
                                        //     classes: {
                                        //         root: classes.bootstrapRoot,
                                        //         input: classes.bootstrapInput
                                        //     }
                                        // }}
                                        multiline
                                        rows="4"
                                        value={this.state.comment}
                                        onChange={this.handleCommentChange}
                                    />
                                </div>
                                <Button variant={"raised"} color={"primary"}  onClick={this.handleComment}>Comentar</Button>
                            </CardActions>
                        </Card>
                    </div>

                    <MobileStepper
                        variant="text"
                        steps={maxSteps}
                        position="static"
                        activeStep={activeStep}
                        nextButton={
                            <Button size="small" onClick={this.handleNext} disabled={activeStep === maxSteps - 1}>
                                Next
                                {/*{theme.direction === 'rtl' ? <KeyboardArrowLeft /> : <KeyboardArrowRight />}*/}
                            </Button>
                        }
                        backButton={
                            <Button size="small" onClick={this.handleBack} disabled={activeStep === 0}>
                                {/*{theme.direction === 'rtl' ? <KeyboardArrowRight /> : <KeyboardArrowLeft />}*/}
                                Back
                            </Button>
                        }
                    />
                </div>}
            </div>
        );
    }
}

SwipeableTextMobileStepper.propTypes = {
    classes: PropTypes.object.isRequired,
    theme: PropTypes.object.isRequired,
};

SwipeableTextMobileStepper.defaultProps = {};

export default (SwipeableTextMobileStepper);