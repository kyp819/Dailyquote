import createError, { HttpError } from 'http-errors';
import express, { NextFunction, Request, Response } from 'express';
import cookieParser from 'cookie-parser';
import logger from 'morgan';
import dotenv from 'dotenv';
dotenv.config();

// modules for authentication
import session from 'express-session';
import passport from 'passport';
import passportLocal from 'passport-local';

// prevent memory leaks with memorystore
import createMemoryStore from 'memorystore';
const MemoryStore = createMemoryStore(session);

// modules for JWT support
import cors from 'cors';
import passportJWT from 'passport-jwt';

// define JWT Aliases
let ExtractJWT = passportJWT.ExtractJwt; // alias

// define authentication strategy
//let strategy = passportLocal.Strategy; // alias

// import the User Model

// import mongoose and related modules
import mongoose from 'mongoose';
import db from './db';

mongoose.connect(db.remoteURI);

// DB Connection Events
mongoose.connection.on('connected', () => {
  console.log(`Connected to MongoDB Atlas`);
})


import indexRouter from '../Routes/index';
import movieRouter from '../Routes/movie';

import { dot } from 'node:test/reporters';

// create an express application
const app = express();

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());

// add cors to the config
app.use(cors());

// setup express session
app.use(session({
  cookie: { maxAge: 86400000}, // 1 day in milliseconds
  store: new MemoryStore({checkPeriod: 86400000}), // 1 day in milliseconds
  secret: db.secret,
  saveUninitialized: false,
  resave: false
}));

// initialize passport and session
app.use(passport.initialize());
app.use(passport.session());

// implement an authentication strategy

// serialize and deserialize the User info


//setup JWT options
let jwtOptions = 
{
  jwtFromRequest: ExtractJWT.fromAuthHeaderAsBearerToken(),
  secretOrKey: db.secret
};

// setup JWT Strategy


// deploy the strategy

app.use('/api', indexRouter);
/* Example: Secure the movie routes with JWT authentication */
//app.use('/api/movie', passport.authenticate('jwt', {session: false}), movieRouter);
app.use('/api/movie', movieRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err: HttpError, req:Request, res:Response, next:NextFunction) 
{
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.end('error - please use /api as a route prefix for your API requests');
});

export default app;
