"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const router = express_1.default.Router();
const movie_1 = require("../Controllers/movie");
router.get('/', (req, res, next) => { (0, movie_1.DisplayQuotes)(req, res, next); });
router.get('/list', (req, res, next) => { (0, movie_1.DisplayQuotes)(req, res, next); });
router.get('/find/:id', (req, res, next) => { (0, movie_1.DisplayMovieById)(req, res, next); });
exports.default = router;
//# sourceMappingURL=movie.js.map