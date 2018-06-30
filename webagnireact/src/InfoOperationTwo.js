import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from 'material-ui/styles';
import classNames from 'classnames';
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
// import {Map, InfoWindow, Marker, GoogleApiWrapper, Listing} from 'google-maps-react';
// import GoogleMapReact from 'google-map-react';
import CircularProgress from '@material-ui/core/CircularProgress';
import {skyBlue, lightGrey, white, lightBlue} from "./Colors";
import LikeIcon from '@material-ui/icons/Favorite';
import NonLikeIcon from '@material-ui/icons/FavoriteBorder';
import Checkbox from 'material-ui/Checkbox';
import { FormLabel, FormControlLabel } from 'material-ui/Form';
import TextField from "material-ui/TextField";
import {redIcon} from './mapIcons';
import Card from '@material-ui/core/Card';
import CardHeader from '@material-ui/core/CardHeader';
import CardMedia from '@material-ui/core/CardMedia';
import CardContent from '@material-ui/core/CardContent';
import CardActions from '@material-ui/core/CardActions';

const AnyReactComponent = ({ text }) => <div>{text}</div>;

const styles = theme => ({
    root: {
        width: 700,
        flexGrow: 1,
    },
    header: {
        display: 'flex',
        alignItems: 'center',
        height: 50,
        paddingLeft: theme.spacing.unit * 4,
        marginBottom: 20,
        backgroundColor: theme.palette.background.default,
        //justifyContent: 'center',
    },
    headerTwo: {
        display: 'flex',
        alignItems: 'center',
        height: 10,
        paddingLeft: theme.spacing.unit * 4,
        marginBottom: 10,
        backgroundColor: theme.palette.background.default,
    },
    img: {
        // height: 255,
        // maxWidth: 400,
        // overflow: 'hidden',
        // width: '100%',
        // marginBottom: 20,
        // marginTop: 20,
    },
    opName:{
        fontFamily: 'Montserrat',
        fontSize: 30,
    },
    rootTwo: {
        width: '100%',
    },
    backButton: {
        marginRight: theme.spacing.unit,
    },
    instructions: {
        marginTop: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
    },
    map:{
        marginBottom: 20,
    },
    likeButton:{
        color: skyBlue,
    },
    bootstrapRoot: {
        padding: 0,
        "label + &": {
            marginTop: theme.spacing.unit * 3
        }
    },
    bootstrapInput: {
        borderRadius: 4,
        backgroundColor: theme.palette.common.white,
        border: "1px solid #ced4da",
        fontSize: 16,
        padding: "10px 12px",
        width: 630,
        transition: theme.transitions.create(["border-color", "box-shadow"]),
        fontFamily: [
            "-apple-system",
            "BlinkMacSystemFont",
            '"Segoe UI"',
            "Roboto",
            '"Helvetica Neue"',
            "Arial",
            "sans-serif",
            '"Apple Color Emoji"',
            '"Segoe UI Emoji"',
            '"Segoe UI Symbol"'
        ].join(","),
        "&:focus": {
            borderColor: "#80bdff",
            boxShadow: "0 0 0 0.2rem rgba(0,123,255,.25)"
        }
    },
    commentButton:{
        backgroundColor: lightBlue,
        color: white,
        marginTop: 10,
        marginLeft: 550,
    },
    comments:{
        marginBottom: 20,
        borderRadius: 4,
        backgroundColor: theme.palette.common.white,
        border: "1px solid #ced4da",
        fontSize: 16,
        padding: "10px 12px",
        //width: 500,
    },
    info:{
        backgroundColor: lightGrey,
    },
    location:{
        display: 'inline-block',
        marginLeft: 30,
    },
    basicInfo:{
        display: 'inline-block',
    },
});

const mapstyle = {
    width: '50%',
    height: '50%'
};

function xmlRequest (){
    return new Promise(resolve => {
        console.log("xmlRequest");
        // var t = true;
        // var token = window.localStorage.getItem('token');
        //
        // if(token != null){
        //     var uname = JSON.parse(token).username;
        //     var tokenObj = JSON.parse(token);
            var map;
        //
        //     var user = {
        //         "username": uname,
        //         "token": tokenObj,
        //         "showPrivate": true //MUDAR ISTO DEPOIS
        //     }

        console.log("pedido");
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open("GET", "https://custom-tine-204615.appspot.com/rest/occurrence/list", true);
        //xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        // var myJSON = JSON.stringify(user);
        // xmlHttp.send(myJSON);
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
                    // var array = Object.values(map);
                    // console.log(array);
                    // console.log(operationsData);
                    //getComments(obj.mapList);
                    resolve(obj.mapList);
                    // resolve('xml value')
                }
                else {
                    console.log("tempo expirado");
                    // reject(Error('Tempo expirado'));
                    //TODO - Link to Login
                }
            }
        }

        // if(t=true)
        //     resolve('xml value')
    // }

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
        console.log("comments: " + c[0].comment_text);
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
                            console.log("Sucesso");
                        }

                        else {
                            console.log("Ocorreu um erro - Nao foi possivel registar o problema");
                        }
                    }
                }
            }
        }

        else{
            console.log("tologin");
        }

    };

    handleCommentChange = event =>{
        this.setState({comment: event.target.value});
    };

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
                <div className={classes.root} style={{margin: '0 auto'}}>
                <Card>
                    <CardHeader
                        title={object[activeStep].user_occurrence_title}
                        subheader={object[activeStep].user_occurrence_date}
                    />

                    <CardContent>

                        <div style={{margin: 20}}>
                            <img key={object[activeStep].user_occurrence_data} className={classes.img} src={img2} style={{margin: '0 auto'}} alt={object[activeStep].user_occurrence_title}/>
                        </div>

                        <div className={classNames(classes.info, "w3-container")}>
                            <div className={"w3-third"}>
                            {/*<Paper square elevation={0} className={classes.header}>*/}
                                {/*<div className={classes.opName}>{object[activeStep].user_occurrence_title}</div>*/}
                            {/*</Paper>*/}
                            {/*<Paper square elevation={0} className={classes.headerTwo}>*/}
                                <p>Tipo:</p> <Typography>{object[activeStep].user_occurrence_type}</Typography>
                            {/*</Paper>*/}
                            {/*<Paper square elevation={0} className={classes.headerTwo}>*/}
                                <p>Grau de urgencia:</p> <Typography> {object[activeStep].user_occurrence_level}</Typography>
                            {/*</Paper>*/}
                            {/*<Paper square elevation={0} className={classes.headerTwo}>*/}
                                {/*<p>Data: </p><Typography> {object[activeStep].user_occurrence_date}</Typography>*/}
                            {/*</Paper>*/}
                                <p>Estado:</p>
                                <p>Data de previsão da limpeza:</p>
                            </div>
                            <div className={"w3-twothird"} style={{marginBottom: 10}}>
                                <p>Localização: </p>
                                {/*<div className={classes.map} style={{ height: '30vh', width: '100%' }}>*/}
                                {/*<GoogleMapReact*/}
                                {/*bootstrapURLKeys={{ key: 'AIzaSyAM-jV8q7-FWs7RdP0G4cH938jWgQwlGVo' }}*/}
                                {/*center={this.state.myLatLng}*/}
                                {/*defaultZoom={11}*/}
                                {/*>*/}
                                {/*<Marker name={"Localização"} position={myLatLng}/>*/}
                                {/*<InfoWindow/>*/}
                                {/*</GoogleMapReact>*/}
                                {/*</div>*/}

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

                        <div>
                    <FormControlLabel
                        control={
                            <Checkbox icon={<NonLikeIcon />} checkedIcon={<LikeIcon />} color={"primary"} />
                        }
                        onChange={this.handleLikeChange}

                    />Apoiar
                </div>

                <div>
                    <p>Comentários</p>
                    {comments.map(c =>{
                        return(
                            <div>
                                <Typography variant={"caption"} gutterBottom align={"right"}>{c.comment_date}</Typography>
                                <div className={classes.comments}>
                                    <b>{c.comment_userID} </b>{c.comment_text}
                                </div>

                            </div>
                        )})}
                </div>

                <div>
                    <TextField
                        placeholder="(Escreva aqui o seu comentário)"
                        id={"bootstrap-input" + object[activeStep].user_occurrence_title}
                        InputProps={{
                            disableUnderline: true,
                            classes: {
                                root: classes.bootstrapRoot,
                                input: classes.bootstrapInput
                            }
                        }}
                        multiline
                        rows="4"
                        value={this.state.comment}
                        onChange={this.handleCommentChange}
                    />
                </div>
                <Button variant={"raised"} color={"primary"} className={classes.commentButton} onClick={this.handleComment}>Comentar</Button>
                    </CardContent>
                    {/*<div>{this.props.occurrences.map(n => <p>n.user_occurrence_title</p>)}</div>*/}

                {/*<SwipeableViews*/}
                    {/*axis={theme.direction === 'rtl' ? 'x-reverse' : 'x'}*/}
                    {/*index={this.state.activeStep}*/}
                    {/*onChangeIndex={this.handleStepChange}*/}
                    {/*enableMouseEvents*/}
                {/*>*/}
                    {/*{object.map(step => (*/}
                        {/*<div>*/}
                            {/*<img key={step.user_occurrence_data} className={classes.img} src={img2} style={{margin: '0 auto'}} alt={step.user_occurrence_title} />*/}

                            {/*<div>*/}
                                {/*<FormControlLabel*/}
                                    {/*control={*/}
                                        {/*<Checkbox icon={<NonLikeIcon />} checkedIcon={<LikeIcon />} color={"primary"} />*/}
                                    {/*}*/}
                                    {/*onChange={this.handleLikeChange}*/}

                                {/*/>Apoiar*/}
                            {/*</div>*/}

                            {/*<div>*/}
                                {/*<TextField*/}
                                    {/*placeholder="(Escreva aqui o seu comentário)"*/}
                                    {/*id="bootstrap-input"*/}
                                    {/*InputProps={{*/}
                                        {/*disableUnderline: true,*/}
                                        {/*classes: {*/}
                                            {/*root: classes.bootstrapRoot,*/}
                                            {/*input: classes.bootstrapInput*/}
                                        {/*}*/}
                                    {/*}}*/}
                                    {/*multiline*/}
                                    {/*rows="4"*/}
                                    {/*value={this.state.comment}*/}
                                    {/*onChange={this.handleCommentChange}*/}
                                {/*/>*/}
                            {/*</div>*/}
                            {/*<Button variant={"raised"} color={"primary"} className={classes.commentButton} onClick={this.handleComment}>Comentar</Button>*/}
                        {/*</div>*/}
                    {/*))}*/}
                {/*</SwipeableViews>*/}

                {/*MAPA*/}
                {/*<div className={classes.map} style={{ height: '40vh', width: '100%' }}>*/}
                    {/*<GoogleMapReact*/}
                        {/*bootstrapURLKeys={{ key: 'AIzaSyAM-jV8q7-FWs7RdP0G4cH938jWgQwlGVo' }}*/}
                        {/*defaultCenter={myLatLng}*/}
                        {/*defaultZoom={zoom}*/}
                    {/*>*/}
                    {/*<Marker onClick={this.onMarkerClick}*/}
                            {/*title={'The marker`s title will appear as a tooltip.'}*/}
                            {/*name={'SOMA'}*/}
                            {/*position={{lat: 59.95, lng: 30.33}} />*/}
                    {/*<InfoWindow*/}
                        {/*marker={this.state.activeMarker}*/}
                        {/*visible={this.state.showingInfoWindow}>*/}
                        {/*<div>*/}
                            {/*<h1>{this.state.selectedPlace.name}</h1>*/}
                        {/*</div>*/}
                    {/*</InfoWindow>*/}
                        {/*/!*<AnyReactComponent*!/*/}
                            {/*/!*lat={59.955413}*!/*/}
                            {/*/!*lng={30.337844}*!/*/}
                            {/*/!*text={'Kreyser Avrora'}*!/*/}
                        {/*/>*/}
                    {/*</GoogleMapReact>*/}
                {/*</div>*/}

                {/*<div className={classes.rootTwo}>*/}
                    {/*//NAO APAGAR*/}
                    {/*<Stepper activeStep={activeStepTwo} alternativeLabel>*/}
                        {/*{steps.map(label => {*/}
                            {/*return (*/}
                                {/*<Step key={label}>*/}
                                    {/*<StepLabel>{label}</StepLabel>*/}
                                {/*</Step>*/}
                            {/*);*/}
                        {/*})}*/}
                    {/*</Stepper>*/}
                    {/*NAO APAGAR ISTO PODE SER NECESSARIO PARA DEPOIS*/}
                    {/*<div>*/}
                        {/*{this.state.activeStepTwo === steps.length ? (*/}
                            {/*<div>*/}
                                {/*<Typography className={classes.instructions}>*/}
                                    {/*All steps completed - you&quot;re finished*/}
                                {/*</Typography>*/}
                                {/*<Button onClick={this.handleReset}>Reset</Button>*/}
                            {/*</div>*/}
                        {/*) : (*/}
                            {/*<div>*/}
                                {/*<Typography className={classes.instructions}>{getStepContent(activeStepTwo)}</Typography>*/}
                                {/*<div>*/}
                                    {/*<Button*/}
                                        {/*disabled={activeStepTwo === 0}*/}
                                        {/*onClick={this.handleBackTwo}*/}
                                        {/*className={classes.backButton}*/}
                                    {/*>*/}
                                        {/*Back*/}
                                    {/*</Button>*/}
                                    {/*<Button variant="raised" color="primary" onClick={this.handleNextTwo}>*/}
                                        {/*{activeStepTwo === steps.length - 1 ? 'Finish' : 'Next'}*/}
                                    {/*</Button>*/}
                                {/*</div>*/}
                            {/*</div>*/}
                        {/*)}*/}
                    {/*</div>*/}
                {/*</div>*/}

                </Card>
                <MobileStepper
                    variant="text"
                    steps={maxSteps}
                    position="static"
                    activeStep={activeStep}
                    className={classes.mobileStepper}
                    nextButton={
                        <Button size="small" onClick={this.handleNext} disabled={activeStep === maxSteps - 1}>
                            Next
                            {theme.direction === 'rtl' ? <KeyboardArrowLeft /> : <KeyboardArrowRight />}
                        </Button>
                    }
                    backButton={
                        <Button size="small" onClick={this.handleBack} disabled={activeStep === 0}>
                            {theme.direction === 'rtl' ? <KeyboardArrowRight /> : <KeyboardArrowLeft />}
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

export default withStyles(styles, { withTheme: true })(SwipeableTextMobileStepper);