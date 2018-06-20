import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from 'material-ui/styles';
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
import img2 from './img/news2.jpg';
import {Map, InfoWindow, Marker, GoogleApiWrapper, Listing} from 'google-maps-react';
import GoogleMapReact from 'google-map-react';
import CircularProgress from '@material-ui/core/CircularProgress';

const AnyReactComponent = ({ text }) => <div>{text}</div>;

const styles = theme => ({
    root: {
        width: 800,
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
        height: 255,
        maxWidth: 400,
        overflow: 'hidden',
        width: '100%',
        marginBottom: 20,
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
});

const mapstyle = {
    width: '50%',
    height: '50%'
};

let xmlRequest = new Promise(function(resolve, reject) {
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

});

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
    state={
        activeStep: 0,
        activeStepTwo: 0,
        object: [
            {user_occurrence_title: 'titulo1'},
            {user_occurrence_title: 'titulo2'}],
        lat: 59.95,
        lng: 30.33,
        center: {
                    lat: 59.95,
                    lng: 30.33
                },
        zoom: 14,
        showingInfoWindow: false,
        activeMarker: {},
        selectedPlace: {},
        loading: true,
    };

    componentDidMount () {
        xmlRequest.then((value) =>{
                this.setState({object: value});
                this.setState({lat: value.user_occurrence_lat});
                this.setState({lng: value.user_occurrence_lon});
                this.setState({loading: false});
                // this.setState({center: {'lat': value.user_occurrence_lat, 'lng': value.user_occurrence_lon}});
                console.log("state object");
                console.log(this.state.object);
            }
        );
    }

    handleNext = () => {
        this.setState(prevState => ({
            activeStep: prevState.activeStep + 1,
        }));
    };

    handleBack = () => {
        this.setState(prevState => ({
            activeStep: prevState.activeStep - 1,
        }));
    };

    handleStepChange = activeStep => {
        this.setState({ activeStep });
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

    render() {
        const { classes, theme } = this.props;
        const { activeStep, object, activeStepTwo, center, zoom, loading } = this.state;
        const steps = getSteps();

        const maxSteps = object.length;

        return (
            <div>
                {loading && <div className={"imgcontainer"}><CircularProgress /></div>}

                {!loading && <div className={classes.root} style={{margin: '0 auto'}}>
                <Paper square elevation={0} className={classes.header}>
                    <div className={classes.opName}>{object[activeStep].user_occurrence_title}</div>
                </Paper>
                <Paper square elevation={0} className={classes.headerTwo}>
                    <p>Tipo:</p> <Typography>{object[activeStep].user_occurrence_type}</Typography>
                </Paper>
                <Paper square elevation={0} className={classes.headerTwo}>
                    <p>Grau de urgencia: </p><Typography> {object[activeStep].user_occurrence_level}</Typography>
                </Paper>
                <Paper square elevation={0} className={classes.headerTwo}>
                    <p>Data: </p><Typography> {object[activeStep].user_occurrence_data}</Typography>
                </Paper>
                <SwipeableViews
                    axis={theme.direction === 'rtl' ? 'x-reverse' : 'x'}
                    index={this.state.activeStep}
                    onChangeIndex={this.handleStepChange}
                    enableMouseEvents
                >
                    {object.map(step => (
                        <img key={step.user_occurrence_data} className={classes.img} src={img2} style={{margin: '0 auto'}} alt={step.user_occurrence_title} />
                    ))}
                </SwipeableViews>

                {/*MAPA*/}
                {/*<div className={classes.map} style={{ height: '40vh', width: '100%' }}>*/}
                    {/*<GoogleMapReact*/}
                        {/*bootstrapURLKeys={{ key: 'AIzaSyAM-jV8q7-FWs7RdP0G4cH938jWgQwlGVo' }}*/}
                        {/*defaultCenter={center}*/}
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

export default withStyles(styles, { withTheme: true })(SwipeableTextMobileStepper);