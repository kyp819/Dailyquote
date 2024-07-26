"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.DisplayMovieById = exports.DisplayQuotes = void 0;
const movie_1 = __importDefault(require("../Models/movie"));
function DisplayQuotes(req, res, next) {
    movie_1.default.find({})
        .then((data) => {
        res.status(200).json({ success: true, msg: "Movie List Retrieved and Displayed", data: data, token: null });
    })
        .catch((err) => {
        console.error(err);
    });
}
exports.DisplayQuotes = DisplayQuotes;
function DisplayMovieById(req, res, next) {
    let id = req.params.id;
    if (id.length != 24) {
        res.status(400).json({ success: false, msg: "A valid ID is required to retrieve a movie", data: null, token: null });
    }
    else {
        movie_1.default.findById({ _id: id })
            .then((data) => {
            if (data) {
                res.status(200).json({ success: true, msg: "One Movie Retrieved and Displayed", data: data, token: null });
            }
            else {
                res.status(404).json({ success: false, msg: "Movie not found", data: null, token: null });
            }
        })
            .catch((err) => {
            console.error(err);
        });
    }
}
exports.DisplayMovieById = DisplayMovieById;
//# sourceMappingURL=movie.js.map