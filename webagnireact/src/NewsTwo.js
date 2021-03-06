import React, { Component } from 'react';
import Paper from 'material-ui/Paper';
import Typography from 'material-ui/Typography';
import Table, { TableBody, TableCell, TableHead, TableRow } from 'material-ui/Table';
import GridList, { GridListTile, GridListTileBar } from 'material-ui/GridList';
import Subheader from 'material-ui/List/ListSubheader';
import IconButton from 'material-ui/IconButton';
import StarBorderIcon from '@material-ui/icons/StarBorder';
import InfoIcon from '@material-ui/icons/Info';
import appProgressData from './appProgressData';
import {Link} from 'react-router-dom';
import {withStyles} from "material-ui/styles/index";
import newsData from './newsData';

const styles = theme => ({
    root: {
        display: 'flex',
        flexWrap: 'wrap',
        justifyContent: 'space-around',
        overflow: 'hidden',
        backgroundColor: theme.palette.background.paper,
    },
    gridList: {
        width: 500,
        height: 450,
    },
    icon: {
        color: 'rgba(255, 255, 255, 0.54)',
    },
});

class NewsTwo extends Component {
    constructor(props) {
        super(props)
    }
    render() {
        const { classes } = this.props;

        return (
            <div>
                <Typography variant="display1">Noticias</Typography>
            <div className={classes.root}>
                {/*<Paper className={classes.paper} style={{backgroundColor: '#f2f2f2'}} >*/}
                    <GridList cellHeight={180} className={classes.gridList}>
                        <GridListTile key="Subheader" cols={2} style={{ height: 'auto' }}>
                            <Subheader component="div">Abril</Subheader>
                        </GridListTile>
                        {newsData.map(news => (
                            <GridListTile key={news.img}>
                                <img src={news.img} alt={news.title} />
                                {/*<GridListTileBar*/}
                                    {/*title=''*/}
                                    {/*titlePosition="top"*/}
                                    {/*actionIcon={*/}
                                        {/*<IconButton className={classes.iconStar}>*/}
                                            {/*<StarBorderIcon />*/}
                                        {/*</IconButton>*/}
                                    {/*}*/}
                                    {/*actionPosition="left"*/}
                                    {/*className={classes.titleBar}*/}
                                {/*/>*/}
                                <GridListTileBar
                                    title={news.title}
                                    subtitle={<span>by: {news.author}</span>}
                                    actionIcon={
                                        <IconButton className={classes.icon}>
                                            <StarBorderIcon />
                                        </IconButton>
                                    }
                                />
                            </GridListTile>
                        ))}
                    </GridList>
                {/*</Paper>*/}

            </div>

                <div className={classes.root}>
                    <GridList cellHeight={180} className={classes.gridList}>
                        <GridListTile key="Subheader" cols={2} style={{ height: 'auto' }}>
                            <Subheader component="div">Maio</Subheader>
                        </GridListTile>
                        {newsData.map(news => (
                            <GridListTile key={news.img}>
                                <img src={news.img} alt={news.title} />
                                <GridListTileBar
                                    title={news.title}
                                    subtitle={<span>by: {news.author}</span>}
                                    actionIcon={
                                        <IconButton className={classes.icon}>
                                            <StarBorderIcon />
                                        </IconButton>
                                    }
                                />
                            </GridListTile>
                        ))}
                    </GridList>

                </div>
            </div>

        );
    }
}

export default withStyles(styles)(NewsTwo);