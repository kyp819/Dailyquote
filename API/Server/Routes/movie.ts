import express from 'express';
const router = express.Router();
import passport from 'passport';

import { DisplayMovieById, DisplayQuotes } from '../Controllers/movie';


/* List of Movie Routes (endpoints) */

/* GET Movie List - fallback in case /list is not used */
router.get('/', (req, res, next) => {  DisplayQuotes(req, res, next); });

/* GET Movie List. */
router.get('/list', (req, res, next) => {  DisplayQuotes(req, res, next); });

/* GET Movie by ID. */
router.get('/find/:id', (req, res, next) => {  DisplayMovieById(req, res, next); });

export default router;
